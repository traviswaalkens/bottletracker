package com.tlw.bottletracker.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.junit.BeforeClass;
import org.junit.Test;

import com.tlw.bottletracker.dto.DiaperData;

public class DiaperMessageReaderTest {

	private DiaperMessageReader dmr = new DiaperMessageReader();

	private static Message validMessage001;
	private static Message validMessage002;
	private static Message validMessage003;
	private static Message validMessage004;
	private static Message validMessage005;
	private static Message validMessage006;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		validMessage001 = new MimeMessage(session);
		validMessage001.setText("\r\n"
				+ "<li><i class=\"byline alertTime\" style=\"border-right: 1px solid grey;padding-right: 5px;float: left;clear: left;margin-right: 5px;\">04:26 PM</i>\r\n"
				+ "    Diaper  -\r\n" + "\r\n"
				+ "                                                                                         BM, \r\n"
				+ "                                             Wet\r\n"
				+ "                                        (Sample Sample)\r\n" + "                            \r\n"
				+ "\r\n" + "\r\n" + "\r\n" + "             \r\n" + "     \r\n" + "     \r\n" + "\r\n" + "</li>\r\n"
				+ "\r\n");
		validMessage002 = new MimeMessage(session);
		validMessage002.setText(
				"<li><i class=\"byline alertTime\" style=\"border-right: 1px solid grey;padding-right: 5px;float: left;clear: left;margin-right: 5px;\">02:50 PM</i>\r\n"
						+ "    Diaper  -\r\n" + "\r\n"
						+ "                                                                                         Wet\r\n"
						+ "                                        (samplea samp)\r\n"
						+ "                            \r\n" + "\r\n" + "\r\n" + "\r\n" + "             \r\n"
						+ "     \r\n" + "     \r\n" + "\r\n" + "</li>\r\n");
		validMessage003 = new MimeMessage(session);
		validMessage003.setText(
				"<li><i class=\"byline alertTime\" style=\"border-right: 1px solid grey;padding-right: 5px;float: left;clear: left;margin-right: 5px;\">12:37 PM</i>\r\n"
						+ "    Diaper  -\r\n" + "\r\n"
						+ "                                                                                         Dry\r\n"
						+ "                                        (sam ple)\r\n" + "                            \r\n"
						+ "\r\n" + "\r\n" + "\r\n" + "             \r\n" + "     \r\n" + "     \r\n" + "\r\n"
						+ "</li>\r\n");
		validMessage004 = new MimeMessage(session);
		validMessage004.setText(
				"<li><i class=\"byline alertTime\" style=\"border-right: 1px solid grey;padding-right: 5px;float: left;clear: left;margin-right: 5px;\">09:01 AM</i>\r\n"
						+ "    Diaper  -\r\n" + "\r\n"
						+ "                                                                                         Applied Cream, \r\n"
						+ "                                             BM, \r\n"
						+ "                                             Wet\r\n"
						+ "                                        (sam ple)\r\n" + "                            \r\n"
						+ "\r\n" + "\r\n" + "\r\n" + "             \r\n" + "     \r\n" + "     \r\n" + "\r\n"
						+ "</li>\r\n");
		validMessage005 = new MimeMessage(session);
		validMessage005.setText(
				"<li><i class=\"byline alertTime\" style=\"border-right: 1px solid grey;padding-right: 5px;float: left;clear: left;margin-right: 5px;\">04:07 PM</i>\r\n"
						+ "    Diaper  -\r\n" + "\r\n"
						+ "                                                                                         BM\r\n"
						+ "                                        (samplea samp)\r\n"
						+ "                            \r\n" + "\r\n" + "\r\n" + "\r\n" + "             \r\n"
						+ "     \r\n" + "     \r\n" + "\r\n" + "</li>\r\n");
		validMessage006 = new MimeMessage(session);
		validMessage006.setText(
				"<li><i class=\"byline alertTime\" style=\"border-right: 1px solid grey;padding-right: 5px;float: left;clear: left;margin-right: 5px;\">02:46 PM</i>\r\n"
						+ "    Diaper  -\r\n" + "\r\n"
						+ "                                                                                         Wet\r\n"
						+ "                                        (sample sample)\r\n"
						+ "                            \r\n" + "\r\n" + "\r\n" + "\r\n" + "             \r\n"
						+ "     \r\n" + "     \r\n" + "\r\n" + "</li>");
	}

	private long buildTime(int hour, int minute) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime().getTime();
	}

	@Test
	public void testMatches() {

	}

	@Test
	public void testReadMessage001() {
		DiaperData dd = (DiaperData) dmr.read(validMessage001);
		assertTrue(dd.isWet());
		assertTrue(dd.isStool());
		assertEquals(this.buildTime(16, 26), dd.getTime().getTime());
	}

	@Test
	public void testReadMessage002() {
		DiaperData dd = (DiaperData) dmr.read(validMessage002);
		assertTrue(dd.isWet());
		assertTrue(!dd.isStool());
		assertEquals(this.buildTime(14, 50), dd.getTime().getTime());
	}

	@Test
	public void testReadMessage003() {
		DiaperData dd = (DiaperData) dmr.read(validMessage003);
		assertTrue(!dd.isWet());
		assertTrue(!dd.isStool());
		assertEquals(this.buildTime(12, 37), dd.getTime().getTime());
	}

	@Test
	public void testReadMessage004() {
		DiaperData dd = (DiaperData) dmr.read(validMessage004);
		assertTrue(dd.isWet());
		assertTrue(dd.isStool());
		assertEquals(this.buildTime(9, 1), dd.getTime().getTime());
	}

	@Test
	public void testReadMessage005() {
		DiaperData dd = (DiaperData) dmr.read(validMessage005);
		assertTrue(!dd.isWet());
		assertTrue(dd.isStool());
		assertEquals(this.buildTime(16, 7), dd.getTime().getTime());
	}

	@Test
	public void testReadMessage006() {
		DiaperData dd = (DiaperData) dmr.read(validMessage006);
		assertTrue(dd.isWet());
		assertTrue(!dd.isStool());
		assertEquals(this.buildTime(14, 46), dd.getTime().getTime());
	}

	@Test
	public void testParseDiaperDetails() {
		String v = "Applied Cream, \n                                             BM, \n                                             Wet\n                                        ";
		DiaperMessageReader mr = new DiaperMessageReader();

		DiaperData d = new DiaperData();
		mr.parseDiaperDetails(v, d);
		assertTrue(d.isStool());
		assertTrue(d.isWet());

		DiaperData d2 = new DiaperData();
		String v2 = "BM, \n                                             Wet\n                                        ";
		DiaperMessageReader mr2 = new DiaperMessageReader();
		mr2.parseDiaperDetails(v2, d2);
		assertTrue(d2.isStool());
		assertTrue(d2.isWet());
	}

	@Test
	public void testExtractTypes() {
		String testString = "Applied Cream, \n                                             BM, \n                                             Wet\n                                        ";
		DiaperMessageReader m = new DiaperMessageReader();
		Collection<String> c = m.extractTypes(testString);
		assertEquals(3, c.size());
		assertTrue(c.contains("Applied Cream"));
		assertTrue(c.contains("BM"));
		assertTrue(c.contains("Wet"));

	}

}
