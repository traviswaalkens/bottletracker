package com.tlw.bottletracker;

import javax.mail.Address;
import javax.mail.Message;

import com.tlw.bottletracker.dto.MessageData;

public interface MessageReader {

	public boolean matches(String subject, Address[] from);

	public MessageData read(Message m);
}
