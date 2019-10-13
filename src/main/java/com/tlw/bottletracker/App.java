package com.tlw.bottletracker;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;

import com.tlw.bottletracker.dto.BottleEvent;
import com.tlw.bottletracker.service.BabyStatsHttpService;
import com.tlw.bottletracker.service.EmailRepositoryService;

public class App {
	public static void main(String[] args) throws IOException, MessagingException {
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
		EmailRepositoryService emailService = new EmailRepositoryService();

		emailService.setHost(credentialsProperties.getProperty("host"));
		emailService.setUsername(credentialsProperties.getProperty("username"));
		emailService.setPassword(credentialsProperties.getProperty("password"));
		emailService.setProvider(credentialsProperties.getProperty("provider"));
		emailService.setPort(credentialsProperties.getProperty("port"));

		try {

			emailService.connect();

			// TODO (Eclipse) add code template

			Message[] messages = emailService.getNewMessages();
			System.out.println("found " + messages.length + " messages ");

			BottleMessageReader mr = new BottleMessageReader();
			int max = Math.min(100, messages.length);
			for (int i = 0; i < max; i++) {

				Message message = messages[i];
				Address[] from = message.getFrom();
				String subject = message.getSubject();

				System.out.println("Checking- " + subject + " by " + from[0]);

				// TODO: Alysha wants it to handle diaper events too.
				if (mr.matches(subject, from)) {

					DateFormat df = new SimpleDateFormat("HH:mm");

					mr.readMessage(message);

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

						emailService.archiveCompletedMessage(message);
					} else {
						System.out.println("Message doesn't match.");
						System.out.println(mr.contents);
					}
				} else {
					emailService.archiveNoiseMessage(message);
				}
			}

			System.out.println("Done.");

		} catch (NoSuchProviderException nspe) {
			System.err.println("invalid provider name");
		} catch (MessagingException me) {
			System.err.println("messaging exception");
			me.printStackTrace();
		} finally {
			if (emailService != null) {
				emailService.disconnect();
			}
		}
	}
}
