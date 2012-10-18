package lab.mobile.my_netsoul;

import java.util.Date;

public class Message {
	Date date;
	String msg;
	Contact contact;
	
	public Message(Contact contact, String message) {
		this.msg = message;
		this.contact = contact;
		this.date = new Date();
	}
}
