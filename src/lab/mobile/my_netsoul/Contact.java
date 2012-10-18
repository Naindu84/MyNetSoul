package lab.mobile.my_netsoul;

import java.util.LinkedList;

import android.graphics.Bitmap;
import android.util.Log;

import lab.mobile.my_netsoul.ChatActivity.ChatAdapter;

public class Contact {
	Long id;
	String login;
	String status;
	String ip;
	String group;
	String location;
	String comment;
	private Bitmap photo = null;
	Boolean is_typing;
	static Long incr = (long) 0;
	
	LinkedList<Message> messages;
	ChatAdapter cAdapter = null;
	
	public Contact(String login) {
		this.login = login;
		status = "Disconnected";
		is_typing = false;
		messages = new LinkedList<Message>();
		synchronized (Contact.class) {
			id = incr++;
		}
	}
	
	@Override
	public String toString() {
		return login;
	}
	
	public void setPhoto(Bitmap b) {
		this.photo = b;
		if (cAdapter != null)
			cAdapter.notifyDataSetChanged();
	}
	
	public Bitmap getPhoto() {
		if (photo == null) {
			Log.w("getPhoto", "Photo not available for " + login);
			PictureManager.getInstance().updatePhoto(login);
		}
		return photo;
	}
	
	public String getFormatted() {
		String msg = new String();
		msg += "*:" + login + "@*" + NetsoulTools.url_encode(location) + "*";
		return msg;
	}
	
}
