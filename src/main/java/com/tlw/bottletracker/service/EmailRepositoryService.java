package com.tlw.bottletracker.service;

import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class EmailRepositoryService {

	private String host;
	private String username;
	private String password;
	private String provider;
	private String port;

	private Boolean connected = false;

	private Session session;
	private Store store;

	private Folder inbox;
	private Folder noiseArchive;
	private Folder completedArchive;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public Store getStore() {
		return this.store;
	}

	public Message[] getNewMessages() throws NumberFormatException, MessagingException {

		if (!connected) {
			connect();
		}

		Message[] messages = inbox.getMessages();
		return messages;
	}

	public void connect() throws NoSuchProviderException, MessagingException {
		Properties p = new Properties();
		session = Session.getDefaultInstance(p, null);
		store = session.getStore(provider);
		store.connect(host, Integer.parseInt(port), username, password);

		inbox = store.getFolder("INBOX");
		inbox.open(Folder.READ_WRITE);

		// TODO This name should be configurable
		noiseArchive = store.getFolder("Archive");
		noiseArchive.open(Folder.READ_ONLY);

		// TODO this name should be configurable
		completedArchive = store.getFolder("recorded_events");
		completedArchive.open(Folder.READ_ONLY);

		connected = true;
	}

	public void disconnect() throws MessagingException {

		inbox.close(false);

		noiseArchive.close(false);
		completedArchive.close(false);
		store.close();
	}

	public void archiveNoiseMessage(Message m) throws MessagingException {
		if (!connected) {
			connect();
		}

		Message[] tempMessages = new Message[] { m };
		inbox.copyMessages(tempMessages, noiseArchive);
		// TODO only delete if copy succeeds. How to tell if copy worked?
		inbox.setFlags(tempMessages, new Flags(Flags.Flag.DELETED), true);
	}

	public void archiveCompletedMessage(Message m) throws MessagingException {
		if (!connected) {
			connect();
		}

		Message[] tempMessages = new Message[] { m };
		inbox.copyMessages(tempMessages, completedArchive);
		// TODO only delete if copy succeeds. How to tell if copy worked?
		inbox.setFlags(tempMessages, new Flags(Flags.Flag.DELETED), true);
	}

	public Folder getInbox() {
		return inbox;
	}

	public Folder getNoiseArchive() {
		return noiseArchive;
	}

	public Folder getCompletedArchive() {
		return completedArchive;
	}
}
