package com.tlw.bottletracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tlw.bottletracker.dto.BabyStatsEvent;
import com.tlw.bottletracker.dto.MessageData;
import com.tlw.bottletracker.impl.BottleMessageReader;
import com.tlw.bottletracker.service.BabyStatsHttpService;
import com.tlw.bottletracker.service.EmailRepositoryService;
import com.tlw.bottletracker.util.BabyStatsEventFactory;
import com.tlw.bottletracker.util.PropertyReader;

public class App {

	public static final Logger LOG = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) throws Exception {
		// TODO Main should provide bootstrap and shutdown only extract the rest into
		// other method calls.

		Properties credentialsProperties, props = new Properties();

		PropertyReader pr = new PropertyReader();

		try {
			credentialsProperties = pr.getCredentialsProperties();
			props = pr.getConfigProperties();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		BabyStatsEventFactory babyStatsFactory = new BabyStatsEventFactory(props);

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

			Message[] messages = emailService.getNewMessages();
			LOG.info("found {} messages", messages.length);

			int max = Math.min(10, messages.length);
			for (int i = 0; i < max; i++) {

				Message message = messages[i];
				Address[] from = message.getFrom();
				String subject = message.getSubject();

				LOG.info("Checking {}/{} email {} by {}", i + 1, max, subject, from[0]);

				for (MessageReader mr : getMessageReaders()) {

					LOG.debug("Trying {}", mr.getClass().getName());

					// TODO extract this into a method call
					if (mr.matches(subject, from)) {

						LOG.info("Email matches filters, processing.");

						MessageData md = mr.read(message);
						if (md.isValid()) {

							Collection<BabyStatsEvent> babyStatEvents = babyStatsFactory.factory(md);

							LOG.info("{} events ready for submission", babyStatEvents.size());
							for (BabyStatsEvent be : babyStatEvents) {
								String result = babyStatsService.addEvent(be);
								LOG.debug("Result: {}", result);
							}

							LOG.debug("Archiving email.");
							emailService.archiveCompletedMessage(message);

						} else {
							LOG.warn("Matched message is invalid.");
							LOG.debug(md.getNotes());
							LOG.debug(md.getContents());
						}

						break;
					} else {
						LOG.info("Email does not match.");
						LOG.debug("Archiving email");
						emailService.archiveNoiseMessage(message);
					}
				}
			}

			LOG.info("No more emails.");

		} catch (NoSuchProviderException nspe) {
			LOG.error("invalid provider name");
		} catch (MessagingException me) {
			LOG.error("messaging exception", me);
		} finally {
			if (emailService != null) {
				emailService.disconnect();
			}
		}

		LOG.info("Done");
	}

	private static List<MessageReader> getMessageReaders() {
		List<MessageReader> readers = new ArrayList<MessageReader>();
		// readers.add(new DiaperMessageReader());
		readers.add(new BottleMessageReader());

		return readers;
	}
}
