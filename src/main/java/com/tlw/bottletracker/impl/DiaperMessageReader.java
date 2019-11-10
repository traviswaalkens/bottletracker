package com.tlw.bottletracker.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tlw.bottletracker.dto.DiaperData;
import com.tlw.bottletracker.dto.MessageData;

public class DiaperMessageReader extends KidsReportMessageReader {

	private static final Logger LOG = LoggerFactory.getLogger(DiaperMessageReader.class);
	private static String subjectPattern = "Diaper Event Alert";

	public boolean matches(String subject, Address[] from) {

		LOG.debug("Matching subject {} to {}.", subject, subjectPattern);
		if (!subjectPattern.equals(subject)) {
			return false;
		}

		return isValidFrom(from);
	}

	public MessageData read(Message m) {

		DiaperData bd = new DiaperData();
		bd.setValid(false);

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

	public void parseContents(Message msg, DiaperData dd) throws MessagingException {
		dd.setTime(new Date());

		//
		if (msg.getReceivedDate() != null) {
			dd.setTime(msg.getReceivedDate());
		}

		Calendar c = Calendar.getInstance();
		c.setTime(dd.getTime());

		// TODO pull the date parsing up to the KidsReportMessageReader class
		String regex = "(\\d+):(\\d+) ([AP]M)</i>\\s+Diaper\\s+-\\s+((?:(?:[a-z\\s]+|BM|WET)[,\\s]*)+)";

		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(dd.getContents());

		if (m.find()) {

			int hour;

			hour = Integer.parseInt(m.group(1));

			if (m.group(3).equals("PM") && hour < 12) {
				hour += 12;
			}

			c.set(Calendar.HOUR_OF_DAY, hour);
			c.set(Calendar.MINUTE, Integer.parseInt(m.group(2)));
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			dd.setTime(c.getTime());

			parseDiaperDetails(m.group(4), dd);

			if (dd.isStool() || dd.isWet()) {
				dd.setValid(true);
			}
		} else {
			dd.setNotes("could extract data from message");
			dd.setValid(false);
			dd.setTime(null);
		}
	}

	void parseDiaperDetails(String diaperTypes, DiaperData dd) {
		Collection<String> types = extractTypes(diaperTypes);

		if (types.contains("BM")) {
			dd.setStool(true);
		}

		if (types.contains("Wet")) {
			dd.setWet(true);
		}
	}

	Collection<String> extractTypes(String diaperTypes) {
		ArrayList<String> typeList = new ArrayList<String>();
		Pattern types = Pattern.compile("\\w+(?:\\s+\\w+)?\\s*(?=[,\\n])", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matches = types.matcher(diaperTypes);

		while (matches.find()) {
			LOG.debug(matches.group(0));
			typeList.add(matches.group().trim());
		}
		return typeList;
	}
}
