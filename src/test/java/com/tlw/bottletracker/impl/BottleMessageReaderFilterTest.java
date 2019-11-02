package com.tlw.bottletracker.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;

import org.junit.Test;

import com.tlw.bottletracker.impl.BottleMessageReader;

public class BottleMessageReaderFilterTest {
	public String subject001() {
		return "Bottle Event Alert";
	}

	public String subject002() {
		return "Diaper Event Alert";
	}

	public Address address001() throws UnsupportedEncodingException {
		return new InternetAddress("noreply@kidreports.com", "KidReports Notifier");
	}

	public Address address002() throws UnsupportedEncodingException {
		return new InternetAddress("kimberly@kidreports.com", "KidReports Notifier");
	}

	public Address address003() throws UnsupportedEncodingException {
		return new InternetAddress("noreply@kidreports.com", "Kimberlyr");
	}

	@Test
	public void testMatches() throws UnsupportedEncodingException {
		BottleMessageReader filter = new BottleMessageReader();

		String subject = subject001();
		Address address = address001();

		assertTrue(filter.matches(subject, new Address[] { address }));

	}

	@Test
	public void testMatchesWithFailingSubject() throws UnsupportedEncodingException {
		BottleMessageReader filter = new BottleMessageReader();
		String subject = subject002();
		Address address = address001();
		assertFalse(filter.matches(subject, new Address[] { address }));
	}

	@Test
	public void testMatchesWithFailingAddress() throws UnsupportedEncodingException {
		BottleMessageReader filter = new BottleMessageReader();

		String subject = subject001();
		// verify failure isn't because of the subject
		assertTrue(filter.matches(subject, new Address[] { address001() }));

		// verify failure of email or name cause errors
		assertFalse(filter.matches(subject, new Address[] { address002() }));
		assertFalse(filter.matches(subject, new Address[] { address003() }));
	}

	@Test
	public void testMatchesWithSuccessfulAddressInPosition2() throws UnsupportedEncodingException {
		BottleMessageReader filter = new BottleMessageReader();

		String subject = subject001();
		Address[] addresses = new Address[2];
		addresses[0] = address002();
		addresses[1] = address001();

		assertTrue(filter.matches(subject, addresses));
	}

}
