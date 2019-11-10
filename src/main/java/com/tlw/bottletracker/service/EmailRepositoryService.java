package com.tlw.bottletracker.service;

import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO remove the get/set for the connection properties.  Should be passed in as properties or some other config.
public class EmailRepositoryService {

	private String host;
	private String username;
	private String password;
	private String provider;
	private String port;

	private Boolean connected = false;

	// private Session session; // TODO this doesn't need to be a member.
	private Store store;

	private Folder inbox;
	private Folder noiseArchive;
	private Folder completedArchive;

	public static final Logger LOG = LoggerFactory.getLogger(EmailRepositoryService.class);

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

		LOG.info("Connecting to {}@{}({})", username, host, port);
		Properties p = new Properties();
		Session session = Session.getDefaultInstance(p, null);
		store = session.getStore(provider);
		store.connect(host, Integer.parseInt(port), username, password);

		LOG.debug("Opening folder INBOX");
		inbox = store.getFolder("INBOX");
		inbox.open(Folder.READ_WRITE);

		// TODO This name should be configurable
		LOG.debug("Opening folder Archive");
		noiseArchive = store.getFolder("Archive");
		noiseArchive.open(Folder.READ_ONLY);

		// TODO this name should be configurable
		LOG.debug("Opening folder recorded_events.");
		completedArchive = store.getFolder("recorded_events");
		completedArchive.open(Folder.READ_ONLY);

		connected = true;
	}

	public void disconnect() throws MessagingException {

		LOG.info("Disconecting {}", host);
		inbox.close(false);
		noiseArchive.close(false);
		completedArchive.close(false);
		store.close();
		connected = false; // thanks dan.
	}

	public void archiveNoiseMessage(Message m) throws MessagingException {
		if (!connected) {
			connect();
		}

		LOG.info("Archiving message to {}", noiseArchive.getName());
		Message[] tempMessages = new Message[] { m };
		inbox.copyMessages(tempMessages, noiseArchive);
		// TODO only delete if copy succeeds. How to tell if copy worked?
		inbox.setFlags(tempMessages, new Flags(Flags.Flag.DELETED), true);
	}

	public void archiveCompletedMessage(Message m) throws MessagingException {
		if (!connected) {
			connect();
		}

		LOG.info("Archiving message to {}", completedArchive.getName());
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
