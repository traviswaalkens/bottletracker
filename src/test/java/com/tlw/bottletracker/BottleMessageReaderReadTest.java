package com.tlw.bottletracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;

public class BottleMessageReaderReadTest {

	private static Message validMessage001;
	private static Message validMessage002;
	private static Message validMessage003;
	private static Message validMessage004;
	private static Message validMessage005;
	private static Message validMessage006;
	private static Message validMessage007;

	private static Message invalidMessage001;

	@Before
	public void setUp() throws Exception {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		InputStream r1 = getClass().getClassLoader().getResourceAsStream("InvalidBottleMessage.msg");
		invalidMessage001 = new MimeMessage(session, r1);

		InputStream r2 = getClass().getClassLoader().getResourceAsStream("validBottleMessage.msg");
		validMessage001 = new MimeMessage(session, r2);

		validMessage002 = new MimeMessage(session);
		validMessage002.setText(
				"<li><i class=\"byline alertTime\" style=\"border-right: 1px solid grey;padding-right: 5px;float: left;clear: left;margin-right: 5px;\">03:45 PM</i>\r\n"
						+ "    Bottle  -\r\n" + "\r\n"
						+ "                                                                                         6, \r\n"
						+ "                                             Formula\r\n"
						+ "                                        (Sample Sample)\r\n</li>");

		validMessage003 = new MimeMessage(session);
		validMessage003.setText(
				"<li><i class=\"byline alertTime\" style=\"border-right: 1px solid grey;padding-right: 5px;float: left;clear: left;margin-right: 5px;\">08:44 AM</i>\r\n"
						+ "    Bottle  -\r\n" + "\r\n"
						+ "                                                                                         6 1/2 oz, \r\n"
						+ "                                             Formula\r\n"
						+ "                                        (Joe Daddy)\r\n" + "                            \r\n"
						+ "\r\n" + "\r\n" + "\r\n" + "             \r\n" + "     \r\n" + "     \r\n" + "\r\n"
						+ "</li>");
		validMessage004 = new MimeMessage(session);
		validMessage004.setText(
				"<li><i class=\"byline alertTime\" style=\"border-right: 1px solid grey;padding-right: 5px;float: left;clear: left;margin-right: 5px;\">02:15 PM</i>\r\n"
						+ "    Bottle  -\r\n" + "\r\n"
						+ "                                                                                         1, \r\n"
						+ "                                             Formula\r\n"
						+ "                                        (Ruth Less)");

		validMessage005 = new MimeMessage(session);
		validMessage005.setText(
				"<li><i class=\"byline alertTime\" style=\"border-right: 1px solid grey;padding-right: 5px;float: left;clear: left;margin-right: 5px;\">01:18 PM</i>\r\n"
						+ "    Bottle  -\r\n" + "\r\n"
						+ "                                                                                         6 oz, \r\n"
						+ "                                             Formula\r\n"
						+ "                                        (Jasmin Espana)\r\n"
						+ "                            \r\n" + "\r\n" + "\r\n" + "\r\n");

		validMessage006 = new MimeMessage(session);
		validMessage006.setText(
				"<li><i class=\"byline alertTime\" style=\"border-right: 1px solid grey;padding-right: 5px;float: left;clear: left;margin-right: 5px;\">01:02 PM</i>\r\n"
						+ "    Bottle  -\r\n" + "\r\n"
						+ "                                                                                         6 1/2oz, \r\n"
						+ "                                             Formula\r\n"
						+ "                                        (Crystal Jay)\r\n"
						+ "                            \r\n" + "\r\n" + "\r\n" + "\r\n" + "             \r\n"
						+ "     \r\n" + "     \r\n" + "\r\n" + "</li>");

		validMessage007 = new MimeMessage(session);
		validMessage007.setText(
				"<li><i class=\"byline alertTime\" style=\"border-right: 1px solid grey;padding-right: 5px;float: left;clear: left;margin-right: 5px;\">04:03 PM</i>\r\n"
						+ "    Bottle  -\r\n" + "\r\n"
						+ "                                                                                         6.5, \r\n"
						+ "                                             Formula\r\n"
						+ "                                        (Crystal Jay)\r\n"
						+ "                            \r\n" + "\r\n" + "\r\n" + "\r\n"
						+ "        <p style=\"background-color:  #ACFCFD;padding: 5px;border-radius: 4px;font-weight: bold;\" class=\"emailAlertNotes\">Notes: Offered her 6.5oz</p>\r\n"
						+ "             \r\n" + "     \r\n" + "     \r\n" + "\r\n" + "</li>");

	}

	@Test
	public void testGetTextFromMessage() {
		BottleMessageReader mr = new BottleMessageReader();
		mr.readMessage(validMessage001);
		assertEquals(6273, mr.contents.length());

		Pattern p = Pattern.compile("\\(Sample Sample\\)");
		Matcher m = p.matcher(mr.contents);
		boolean matches = m.find();
		assertTrue(matches);

		BottleMessageReader mr2 = new BottleMessageReader();
		mr2.readMessage(validMessage002);
		assertNotNull(mr2.contents);
		assertTrue(p.matcher(mr.contents).find());
	}

	@Test
	public void testParseValidMessage() throws MessagingException {
		BottleMessageReader mr = new BottleMessageReader();
		mr.readMessage(validMessage001);
		assertTrue(mr.isValid);

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 12);
		c.set(Calendar.MINUTE, 30);

		assertEquals(6.5, mr.ounces, .0001);
		assertTrue(Math.abs(c.getTime().getTime() - mr.time.getTime()) < 1000);
	}

	@Test
	public void testParseInvalidMessage() throws MessagingException {
		BottleMessageReader mr = new BottleMessageReader();
		mr.readMessage(invalidMessage001);
		assertFalse(mr.isValid);
		assertEquals(0, mr.ounces, .0001);
	}

	@Test
	public void testMessageWithWholeOunces() throws MessagingException {
		BottleMessageReader mr = new BottleMessageReader();
		mr.readMessage(validMessage002);
		assertTrue("Message 2 is not valid", mr.isValid);
		assertEquals(6, mr.ounces, .0001);

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 15);
		c.set(Calendar.MINUTE, 45);
		assertTrue(Math.abs(c.getTime().getTime() - mr.time.getTime()) < 1000);
	}

	@Test
	public void testMessageWithAmDate() throws MessagingException {
		BottleMessageReader mr = new BottleMessageReader();
		mr.readMessage(validMessage003);
		assertTrue("Message 3 is not valid", mr.isValid);
		assertEquals(6.5, mr.ounces, .0001);

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 8);
		c.set(Calendar.MINUTE, 44);
		assertTrue(Math.abs(c.getTime().getTime() - mr.time.getTime()) < 1000);
	}

	@Test
	public void testMessage4() throws MessagingException {
		BottleMessageReader mr = new BottleMessageReader();
		mr.readMessage(validMessage004);
		assertTrue("Message 4 is not valid", mr.isValid);
		assertEquals(1, mr.ounces, .0001);

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 14);
		c.set(Calendar.MINUTE, 15);
		assertTrue(Math.abs(c.getTime().getTime() - mr.time.getTime()) < 1000);
	}

	@Test
	public void testMessage5() throws MessagingException {
		BottleMessageReader mr = new BottleMessageReader();
		mr.readMessage(validMessage005);
		assertTrue("Message 5 is not valid", mr.isValid);
		assertEquals(6, mr.ounces, .0001);

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 13);
		c.set(Calendar.MINUTE, 18);
		assertTrue(Math.abs(c.getTime().getTime() - mr.time.getTime()) < 1000);
	}

	@Test
	public void testMessage6() throws MessagingException {
		BottleMessageReader mr = new BottleMessageReader();
		mr.readMessage(validMessage006);
		assertTrue("Message 6 is not valid", mr.isValid);
		assertEquals(6.5, mr.ounces, .0001);

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 13);
		c.set(Calendar.MINUTE, 2);
		assertTrue(Math.abs(c.getTime().getTime() - mr.time.getTime()) < 1000);
	}

	@Test
	public void testMessage7() throws MessagingException {
		BottleMessageReader mr = new BottleMessageReader();
		mr.readMessage(validMessage007);
		assertTrue("Message 7 is not valid", mr.isValid);
		assertEquals(6.5, mr.ounces, .0001);

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 16);
		c.set(Calendar.MINUTE, 3);
		assertTrue(Math.abs(c.getTime().getTime() - mr.time.getTime()) < 1000);
	}
}
