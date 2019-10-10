package com.tlw.bottletracker;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import com.tlw.bottletracker.dto.BottleEvent;
import com.tlw.bottletracker.service.BabyStatsHttpService;

public class App {
	public static void main(String[] args) throws IOException {
		Properties credentialsProperties, props = new Properties();

		PropertyReader pr = new PropertyReader();

		try {
			credentialsProperties = pr.getCredentialsProperties();
			props = pr.getConfigProperties();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		BabyStatsHttpService babyStatsService = new BabyStatsHttpService();
		babyStatsService.setUrl(props.getProperty("babystats.url"));

		String host = credentialsProperties.getProperty("host");
		String username = credentialsProperties.getProperty("username");
		String password = credentialsProperties.getProperty("password");
		String provider = credentialsProperties.getProperty("provider");
		String port = credentialsProperties.getProperty("port");

		try {
			Session session = Session.getDefaultInstance(props, null);
			Store store = session.getStore(provider);
			store.connect(host, Integer.parseInt(port), username, password);

			Folder inbox = store.getFolder("INBOX");
			inbox.open(Folder.READ_WRITE);

			// TODO (Eclipse) add code template
			// TODO set up receptacle folder for completed messages.
			// Folder receptacle = store.getFolder( "recorded_events");
			// receptacle.open(Folder.READ_WRITE);

			Folder archive = store.getFolder("Archive");
			archive.open(Folder.READ_ONLY);

			Message[] messages = inbox.getMessages();
			System.out.println("found " + messages.length + " messages ");

			MessageFilter messageFilter = new MessageFilter();
			int max = Math.min(100, messages.length);
			for (int i = 0; i < max; i++) {

				Message message = messages[i];
				Address[] from = message.getFrom();
				String subject = message.getSubject();

				System.out.println("Checking- " + subject + " by " + from[0]);

				// TODO: Alysha wants it to handle diaper events too.
				if (messageFilter.matches(subject, from)) {
					// TODO (Eclipse) auto trip trailing spaces?
					/*
					 * TODO: parse message for ounces and time, execute save to baby stats
					 * transaction. Move completed item to receptacle
					 */
					DateFormat df = new SimpleDateFormat("HH:mm");

					MessageReader mr = new MessageReader(message);

					if (mr.isValid) {
						System.out.println(String.format("%.2f Ounces @ %s", mr.ounces, df.format(mr.time)));

						BottleEvent be = new BottleEvent();
						be.setBottleOunces(String.valueOf(mr.ounces));
						be.setEvent("AddFeeding");
						be.setEventTime(df.format(mr.time));
						be.setId(props.getProperty("babystats.id"));
						be.setAccessToken(props.getProperty("babystats.token"));
						be.setUom("oz");

						String result = babyStatsService.addEvent(be);
						System.out.println(result);

					} else {
						System.out.println("Message doesn't match.");
						System.out.println(mr.contents);
					}
				} else {
					Message[] tempMessages = new Message[] { message };

					try {
						inbox.copyMessages(tempMessages, archive);
						// TODO only delete if copy succeeds. How to tell if copy worked?
						inbox.setFlags(tempMessages, new Flags(Flags.Flag.DELETED), true);
					} catch (Exception ex) {
						System.out.println("Failed copy or delete message:" + ex.getMessage());
					}
				}
			}

			System.out.println("Done.");

			inbox.close(false);
			// receptacle.close(false);
			archive.close(false);

			store.close();
		} catch (NoSuchProviderException nspe) {
			System.err.println("invalid provider name");
		} catch (MessagingException me) {
			System.err.println("messaging exception");
			me.printStackTrace();
		}
	}
}
