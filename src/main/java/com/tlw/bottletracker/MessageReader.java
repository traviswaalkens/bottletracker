package com.tlw.bottletracker;

import javax.mail.Address;
import javax.mail.Message;

public interface MessageReader {

	public boolean matches(String subject, Address[] from);

	// TODO rename this to read
	// TODO should return a MessageData object instead of void
	public void readMessage(Message m);
}
