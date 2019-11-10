package com.tlw.bottletracker.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tlw.bottletracker.dto.BottleData;
import com.tlw.bottletracker.dto.MessageData;

public class BottleMessageReader extends KidsReportMessageReader {
	public static final Logger LOG = LoggerFactory.getLogger(BottleMessageReader.class);

	private static String subjectPattern = "Bottle Event Alert";

	public boolean matches(String subject, Address[] from) {

		LOG.debug("Matching subject {} to {}.", subject, subjectPattern);
		if (!subjectPattern.equals(subject)) {
			return false;
		}

		return isValidFrom(from);
	}

	public MessageData read(Message m) {

		BottleData bd = new BottleData();
		bd.setValid(false);
		bd.setType("Bottle");

		try {

			bd.setContents(getTextFromMessage(m));
			parseContents(m, bd);

		} catch (MessagingException e) {
			bd.setNotes(e.getMessage());
		} catch (IOException e) {
			bd.setNotes(e.getMessage());
		}

		return bd;

	}

	public void parseContents(Message msg, BottleData bd) throws MessagingException {
		bd.setOunces(0);
		bd.setTime(new Date());

		//
		if (msg.getReceivedDate() != null) {
			bd.setTime(msg.getReceivedDate());
		}

		Calendar c = Calendar.getInstance();
		c.setTime(bd.getTime());

		String regex = "(\\d+):(\\d+) ([AP]M)</i>\\s+Bottle\\s+-\\s+(\\d)\\s*(?:\\s(\\d)/(\\d)|(\\.\\d))?(?:\\s*oz)?,";

		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(bd.getContents());

		if (m.find()) {
			bd.setOunces(0);
			int hour;

			hour = Integer.parseInt(m.group(1));

			if (m.group(3).equals("PM") && hour < 12) {
				hour += 12;
			}

			c.set(Calendar.HOUR_OF_DAY, hour);
			c.set(Calendar.MINUTE, Integer.parseInt(m.group(2)));
			bd.setTime(c.getTime());

			bd.setOunces(bd.getOunces() + Integer.parseInt(m.group(4)));
			if (m.group(7) != null) {
				// ".5";
				float p1;
				p1 = Float.parseFloat(m.group(7));
				bd.setOunces(bd.getOunces() + p1);
			} else if (m.group(5) != null) {
				float p1, p2, p3;
				p1 = Float.parseFloat(m.group(5));
				p2 = Float.parseFloat(m.group(6));
				p3 = p1 / p2;
				bd.setOunces(bd.getOunces() + p3);
			}

			bd.setValid(true);
		} else {
			bd.setNotes("could extract data from message");
			bd.setValid(false);
			bd.setOunces(0);
			bd.setTime(null);
		}
	}
}
