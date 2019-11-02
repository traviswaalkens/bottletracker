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

			// TODO (Eclipse) add code template

			Message[] messages = emailService.getNewMessages();
			LOG.info("found {} messages", messages.length);

			int max = Math.min(10, messages.length);
			for (int i = 0; i < max; i++) {

				Message message = messages[i];
				Address[] from = message.getFrom();
				String subject = message.getSubject();

				LOG.info("Checking- {} by {}", subject, from[0]);

				for (MessageReader mr : getMessageReaders()) {

					// TODO extract this into a method call
					if (mr.matches(subject, from)) {

						MessageData md = mr.read(message);
						if (md.isValid()) {

							// TODO this now returns a collection and each event needs to be sent to
							// babystats
							// TODO this is no longer a factory so much as its a conversion coordinator.
							Collection<BabyStatsEvent> babyStatEvents = babyStatsFactory.factory(md);

							for (BabyStatsEvent be : babyStatEvents) {
								// String consoleMessage;

								// TODO this logging should be somwhere else.

								// if ("AddFeeding".equals(be.getEvent())) {
								// BottleEvent _be = (BottleEvent) be;
								// consoleMessage = String.format("Feeding Event - %.2f Ounces @ %s",
								// Float.parseFloat(_be.getBottleOunces()), be.getEventTime());
								// } else {
								// consoleMessage = be.getEvent();
								// }

								// LOG.info(consoleMessage);

								// TODO more logging
								// TODO this should be its own function

								String result = babyStatsService.addEvent(be);
								LOG.info(result);
							}

							emailService.archiveCompletedMessage(message);
						} else {
							LOG.warn("Message doesn't match.");
							LOG.debug(md.getNotes());
							LOG.debug(md.getContents());
						}

						break;
					} else {
						emailService.archiveNoiseMessage(message);
					}
				}
			}

			LOG.info("Done.");

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
		// TODO why is this not allowing ArrayList<>
		List<MessageReader> readers = new ArrayList<MessageReader>();
		// readers.add(new DiaperMessageReader());
		readers.add(new BottleMessageReader());

		return readers;
	}
}
