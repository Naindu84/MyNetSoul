package lab.mobile.my_netsoul;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class ContactManager {
	private LinkedList<Contact> _list;
	
	private static ContactManager _instance;
	
	public static ContactManager getInstance() {
		if (_instance == null) {
			synchronized (ContactManager.class) {
				if (_instance == null)
					_instance = new ContactManager();
			}
		}
		return _instance;
	}
	
	private ContactManager() {
		_list = new LinkedList<Contact>();
		if (MainActivity.context != null) {
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.context);
			HashSet<String> list = (HashSet) sp.getStringSet("contacts", new HashSet<String>());
			Log.d("ContactManager", "List Size: " + list.size());
			for (String object : list) {
				_list.add(new Contact(object));
			}
		}
	}
	
	public void save() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.context);
		Editor e = sp.edit();
		HashSet<String> list = new HashSet<String>();
		for (Contact c : _list) {
			list.add(c.login);
		}
		e.putStringSet("contacts", list);
		e.apply();
	}
	
	public Long addContact(String login) {
		Contact c = new Contact(login);
		_list.add(c);
		save();
		return c.id;
	}
	
	public Contact getContactByPosition(Integer pos) {
		return _list.get(pos);
	}
	
	public Contact getContact(long id) {
		for (Contact elem : _list) {
			if (elem.id.equals(id))
				return elem;
		}
		return null;
	}
	
	public Contact getContactByLogin(String login) {
		for (Contact elem : _list) {
			if (elem.login.equals(login))
				return elem;
		}
		return null;
	}
	
	public Boolean removeContact(Long id) {
		for (Contact elem : _list) {
			if (elem.id.equals(id)) {
				_list.remove(elem);
				return true;
			}
		}
		return false;
	}
	
	public Integer getCount() {
		return  _list.size();
	}

	/**
	 * @return the _list
	 */
	public LinkedList<Contact> getList() {
		return _list;
	}
	
	public String getFormattedList() {
		String str = new String();
		str = "{";
		for (Contact elem : _list) {
			if (!str.equals("{"))
				str += ",";
			str += elem.login;
		}
		str += "}";
		return str;
	}

	public void removeContactByPosition(Integer pos_to_del) {
		_list.remove(pos_to_del.intValue());
		save();
	}

	public void updateContact(String login, String id, String ip, String group, String state, String location, String comment) {
		for (Contact c : _list) {
			if (c.login.equals(login)) {
				c.id = Long.parseLong(id);
				c.ip = ip;
				c.group = group;
				c.location = location;
				c.status = state;
				c.comment = comment;
				return ;
			}
		}
	}
	
	public void updateState(String login, String state) {
		for (Contact c : _list) {
			if (c.login.equals(login)) {
				c.status = state;
				return ;
			}
		}
	}

	public void updateTyping(String login, boolean b) {
		for (Contact c : _list) {
			if (c.login.equals(login)) {
				c.is_typing = b;
				return ;
			}
		}
	}
	
	
}
