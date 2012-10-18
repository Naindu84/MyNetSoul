package lab.mobile.my_netsoul;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import lab.mobile.my_netsoul.NetworkService.NetworkTask;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Dispatcher {
	private final static String pref_Login = "pref_credentials_login";
	private final static String pref_Passwd = "pref_credentials_password";
	
	class Action {
		String name;
		String[] params;
		
		public Action(String n, String[] p) {
			name = n;
			params = p;
		}
	}
	
	private HashMap<String, String> commands = new HashMap<String, String>();
	NetworkTask nt;
	NetworkService ns;
	private LinkedList<Action> _queue = new LinkedList<Action>();
	
	public Dispatcher(NetworkTask os, NetworkService ns) {
		this.nt = os;
		this.ns = ns;
		commands.put("salut", "connexion");
		commands.put("ping", "ping");
		commands.put("rep", "rep");
		commands.put("user_cmd", "userCmd");
	}
	
	public Boolean doDispatch(String line) {
		String[] res = line.split(" ");
		Log.i("Parse", res[0] + "; Length: " + res.length);
		if (res.length > 0) {
			if(commands.containsKey(res[0])) {
				Log.d("Parse", "Try to exec: " + commands.get(new String(res[0])));
				try {
					Boolean r = true;
					String cmd = (String)commands.get(new String(res[0]));
					Method m = this.getClass().getMethod(cmd, String[].class);
					Object[] p = new Object[1];
					p[0] = res;
					m.invoke(this, p);
					return r;
				} catch (IllegalArgumentException e) {
					Log.e("Parse", "exec IllegalArgumentException " + e.getMessage());
				} catch (IllegalAccessException e) {
					Log.e("Parse", "exec IllegalAccessException " + e.getMessage());
				} catch (InvocationTargetException e) {
					Log.e("Parse", "exec InvocationTargetException " + e.getMessage());
				} catch (NoSuchMethodException e) {
					Log.e("Parse", "exec Error, Method doesn't exists " + e.getMessage());
				}
			}
		}
		return false;
	}
	
	public Boolean ping(String[] params) {
		if (params.length < 2)
			return false;
		String str = "ping ";
		str = str.concat(params[1]);
		Log.i("Ping", str);
		nt.SendDataToNetwork(str);
		return true;
	}
	
	public Boolean rep(String[] params) {
		if (_queue.size() == 0 || params.length < 2)
			return false;
		String code_resp = new String(params[1]);
		Log.d("Resp", "Code : " + code_resp); // 033  Possible ??
		if (_queue.getFirst().name.equals("connexion")) {
			Action a = _queue.pop();
			Log.d("Dispatcher", "Auth autorisee " + a.params);
			this.auth(a.params);
			return true;
		} else if (_queue.getFirst().name.equals("auth")) {
			_queue.pop();
			if (code_resp.equals("002")) {
				Log.d("Dispatcher", "Vous etes connectes");
				ns.commands.changeStatus("actif");
				ns.commands.updateUsers(ContactManager.getInstance().getFormattedList());
				ns.commands.watchUsers();
				ns.connected = true;
				//_queue.add(new Action("who", new String[0]));
				return true;
			} else
				Log.e("Dispatcher", "Votre mot de passe n'est pas correcte.");
		}
		
		return false;
	}
	
	public Boolean auth(String[] params) {
		if (params.length < 4) {
			Log.e("Dispatcher", "auth, expected 4 params but " + params.length + " given");
			return false;
		}
		String r = new String();
		for (String string : params) {
			r += string + " ";
		}
		Log.d("Auth", r);
		String str = new String("ext_user_log ");
		str += params[0] + " ";
		Log.d("Auth", "Auth : " + str);
		str += params[1] + " " + params[2] + " " + params[3];
		Log.i("Auth", "Auth : " + str);
		_queue.push(new Action("auth", new String[0]));
		nt.SendDataToNetwork(str);
		return true;
	}
	
	public Boolean connexion(String[] params) {
		if (params.length < 6) {
			Log.e("Dispatcher", "Connexion, expected 6 params but " + params.length + " given");
			return false;
		}
//		String cmd = params[0];
//		String socket_nb = params[1];
		String rand_hash = params[2];
		String host_client = params[3];
		String port_client = params[4];
//		String timestamp = params[5];
		
		String[] tab = new String[4];
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ns.getApplicationContext());
		
		tab[0] = sharedPref.getString(pref_Login, "login_x");
		tab[1] = NetsoulTools.gen_hash_auth(rand_hash, host_client, port_client, sharedPref.getString(pref_Passwd, "*******"));
		tab[2] = NetsoulTools.url_encode("user data");
		tab[3] = NetsoulTools.url_encode("user location");
		_queue.push(new Action("connexion", tab));
		
		nt.SendDataToNetwork("auth_ag ext_user none none");
		return false;
	}
	
	public Boolean userCmd(String[] params) {
		Log.d("user_cmd", "Received: " + params.length + " params");
		if (params.length >= 4) {
			if (params[3].equals("msg") && params.length >= 5) {
				userCmd_msg(params);
			} else if (params[3].equals("state") && params.length >= 5) {
				userCmd_state(params);
			} else if (params[3].equals("who") && params.length >= 15) {
				userCmd_who(params);
			} else if (params[3].equals("login") && params.length >= 4) {
				userCmd_changeState("login", params);
			} else if (params[3].equals("logout") && params.length >= 4) {
				userCmd_changeState("logout", params);
			} else if (params[3].equals("dotnetSoul_UserTyping") && params.length >= 4) {
				userCmd_isTyping(params);
			} else if (params[3].equals("dotnetSoul_UserCancelledTyping") && params.length >= 4) {
				userCmd_cancelTyping(params);
			} else {
				String r = new String();
				for (String string : params) {
					r += string + " ";
				}
				Log.w("user_cmd", "Misc. error: no commands match (" + r + ")");
			}
		}
		return false;
	}
	
	private void userCmd_changeState(String string, String[] params) {
		Log.i("user_cmd", "Change State received " + string);
		String long_login = params[1];
		String[] split_login = long_login.split(":");
		String login = split_login[3].substring(0, split_login[3].indexOf('@'));
		
		if (string.equals("logout")) {
			ContactManager.getInstance().updateState(login, "Disconnected");
			if (MainActivity.context != null && MainActivity.context.adapter != null)
				MainActivity.context.adapter.notifyDataSetChanged();
		} else {
			ns.commands.updateUser(login);
			//ContactManager.getInstance().updateState(login, "actif");
		}
	}

	public void userCmd_msg(String[] params) {
		String msg = NetsoulTools.url_decode(params[4]);
		String long_login = params[1];
		String[] split_login = long_login.split(":");
		String login = split_login[3].substring(0, split_login[3].indexOf('@'));
		
		Contact c = ContactManager.getInstance().getContactByLogin(login);
		if (c != null) {
			c.messages.add(new Message(c, msg));
			if (c.cAdapter != null)
				c.cAdapter.notifyDataSetChanged();
		}
		Log.i("user_cmd", "Msg received from : " + login + " msg(" + msg + ")");
	}
	
	
	public void userCmd_state(String[] params) {
		String long_login = params[1];
		String[] split_login = long_login.split(":");
		String login = split_login[3].substring(0, split_login[3].indexOf('@'));
		String state;
		if (params[4].indexOf(':') > 0)
			state = params[4].substring(0, params[4].indexOf(':'));
		else
			state = params[4];
		Log.i("user_cmd", "State received for " + login + " : " + state);
		ContactManager.getInstance().updateState(login, state);
		if (MainActivity.context != null && MainActivity.context.adapter != null)
			MainActivity.context.adapter.notifyDataSetChanged();
	}
	
	public void userCmd_who(String[] params) {
		Log.d("user_cmd", "Who received");
		String state = "";
		if (params[14].indexOf(':')  > 0)
			state = params[14].substring(0, params[14].indexOf(':'));
		String comment = (params.length >= 16 ? NetsoulTools.url_decode(params[15]) : "");
		ContactManager.getInstance().updateContact(params[5], params[4], params[6], params[13], state, NetsoulTools.url_decode(params[12]), comment);
		if (MainActivity.context != null && MainActivity.context.adapter != null)
			MainActivity.context.adapter.notifyDataSetChanged();
	}
	
	public void userCmd_isTyping(String[] params) {
		String long_login = params[1];
		String[] split_login = long_login.split(":");
		String login = split_login[3].substring(0, split_login[3].indexOf('@'));
		
		Log.i("user_cmd", login + " is typing");
		ContactManager.getInstance().updateTyping(login, true);
		if (MainActivity.context != null && MainActivity.context.adapter != null)
			MainActivity.context.adapter.notifyDataSetChanged();
	}
	
	public void userCmd_cancelTyping(String[] params) {
		String long_login = params[1];
		String[] split_login = long_login.split(":");
		String login = split_login[3].substring(0, split_login[3].indexOf('@'));
		Log.i("user_cmd", login + " stopped typing");
		ContactManager.getInstance().updateTyping(login, false);
		if (MainActivity.context != null && MainActivity.context.adapter != null)
			MainActivity.context.adapter.notifyDataSetChanged();
	}
}
