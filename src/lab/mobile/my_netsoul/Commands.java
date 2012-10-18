package lab.mobile.my_netsoul;

import android.util.Log;

public class Commands {
	NetworkService service;
	static final String[] allowed = {"actif", "away", "idle", "lock", "none"};
	
	public Commands(NetworkService ns) {
		service = ns;
	}
	
	public Boolean changeStatus(String status) {
		if (!service.connected)
			return false;
		long timestamp = System.currentTimeMillis()/1000;
		
		for (String s : allowed) {
			if (s.equals(status)) {
				Log.i("Commands", "change status with " + status + ":" + timestamp);
				service.networktask.SendDataToNetwork("user_cmd state " + status + ":" + timestamp);
				return true;
			}
		}
		return false;
	}
	public Boolean sendMsg(String[] logins, String msg) {
		if (logins.length == 0 || msg.length() == 0)
			return false;
		String l = new String("{");
		for (String string : logins) {
			if (string.length() > 1) {
				if (!l.equals("{"))
					l += ",";
				l += string;
			}
		}
		l += "}";
		String m = NetsoulTools.url_encode(msg);
		// TODO Limiter a 256 chars ?
		String cmd = new String("user_cmd msg " + l + " msg " + m);
		Log.i("Commands", "Send msg to " + l + " :" + cmd);
		service.networktask.SendDataToNetwork(cmd);
		return true;
	}
	
	public Boolean sendMsg(String login, String msg) {
		if (login == null || login.length() < 2 || msg.length() == 0)
			return false;
		String m = NetsoulTools.url_encode(msg);
		// TODO Limiter a 256 chars ?
		Contact c = ContactManager.getInstance().getContactByLogin(login);
		if (c != null && c.location != null && c.location.length() > 0 && c.login.length() > 1) {
			String cmd = new String("user_cmd msg " + c.getFormatted() + " msg " + m);
			Log.i("Commands", "Send msg to " + c.getFormatted() + " :" + cmd);
			service.networktask.SendDataToNetwork(cmd);
		} else
			Log.e("Commands", "Try to send a msg to user which doesn't exists");
		return true;
	}
	
	/**
	 * Need rep 002
	 * @param logins
	 * @return
	 */
	public Boolean listUsers(String login) {
		if (login.length() < 2)
			return false;
		String cmd = new String("user_cmd list_users " + login);
		Log.i("Commands", "List_users " + login);
		service.networktask.SendDataToNetwork(cmd);
		return true;
	}
	
	/**
	 * Need rep 002
	 * @param logins
	 * @return
	 */
	public Boolean listUsers(String[] logins) {
		if (logins.length == 0)
			return false;
		String l = new String("{");
		for (String string : logins) {
			if (string.length() > 1) {
				if (!l.equals("{"))
					l += ",";
				l += string;
			}
		}
		l += "}";
		String cmd = new String("user_cmd list_users " + l);
		Log.i("Commands", "List_users " + l);
		service.networktask.SendDataToNetwork(cmd);
		return true;
	}
	
	public Boolean watchUser(String login) {
		if (login.length() < 2)
			return false;
		String cmd = new String("user_cmd watch_log_user " + login);
		Log.i("Commands", "Watch user " + login);
		service.networktask.SendDataToNetwork(cmd);
		return true;
	}
	
	public void watchUsers() {
		String l = ContactManager.getInstance().getFormattedList();
		if (l.length() < 3)
			return ;
		String cmd = new String("user_cmd watch_log_user " + l);
		Log.i("Commands", "Watch users " + l);
		service.networktask.SendDataToNetwork(cmd);
	}
	
	public Boolean watchUsers(String[] logins) {
		if (logins.length == 0)
			return false;
		String l = new String("{");
		for (String string : logins) {
			if (string.length() > 1) {
				if (!l.equals("{"))
					l += ",";
				l += string;
			}
		}
		l += "}";
		String cmd = new String("user_cmd watch_log_user " + l);
		Log.i("Commands", "Watch user " + l);
		service.networktask.SendDataToNetwork(cmd);
		return true;
	}
	
	public Boolean exit() {
		return true;
	}

	public void updateUsers(String logins) {
		if (logins.length() < 3)
			return ;
		String cmd = new String("user_cmd who " + logins);
		Log.i("Commands", "Who " + logins);
		service.networktask.SendDataToNetwork(cmd);
	}

	public void updateUser(String login) {
		if (login.length() < 1)
			return ;
		String cmd = new String("user_cmd who " + login);
		Log.i("Commands", "Who " + login);
		service.networktask.SendDataToNetwork(cmd);
	}
	
}
