package com.tlw.bottletracker.impl;

import java.io.IOException;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tlw.bottletracker.MessageReader;

abstract public class KidsReportMessageReader implements MessageReader {

	public static Logger LOG = LoggerFactory.getLogger(KidsReportMessageReader.class);
	protected static String fromPattern = "KidReports Notifier <noreply@kidreports.com>";

	protected boolean isValidFrom(Address[] from) {

		for (int x = 0; x < from.length; x++) {
			LOG.debug("Matching email {} to {}.", from[x].toString(), fromPattern);
			if (fromPattern.equals(from[x].toString())) {
				return true;
			}
		}
		return false;
	}

	/*
	 * https://stackoverflow.com/questions/11240368/how-to-read-text-inside-body-of-
	 * mail-using-javax-mail
	 */
	protected String getTextFromMessage(Message message) throws MessagingException, IOException {
		String result = "";

		// this shouldn't happen, kids report are html emails with multipart
		if (message.isMimeType("text/plain")) {
			result = message.getContent().toString();
		} else if (message.isMimeType("multipart/*")) {
			MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
			result = getTextFromMimeMultipart(mimeMultipart);
		} else {
			LOG.error("Unknown mime type for content type {}. Unable to retreive text of message.",
					message.getContentType());
		}

		return result;
	}

	/*
	 *
	 * With modifications from
	 * :https://stackoverflow.com/questions/11240368/how-to-read-text-inside-body-of
	 * -mail-using-javax-mail
	 */
	protected String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {

		String result = "";
		int count = mimeMultipart.getCount();

		for (int i = 0; i < count; i++) {
			BodyPart bodyPart = mimeMultipart.getBodyPart(i);

			LOG.debug("Reading part {} with content type {}.", bodyPart.getContentType());
			if (bodyPart.isMimeType("text/html")) {
				result = bodyPart.getContent().toString();
				break;
			}
		}
		return result;
	}
}
