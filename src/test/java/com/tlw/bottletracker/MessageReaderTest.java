package com.tlw.bottletracker;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Calendar;
import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;

public class MessageReaderTest {

	private static Message validMessage001;
	private static Message validMessage002;
	private static Message validMessage003; 
	private static Message invalidMessage001;
	
	@Before
	public void setUp() throws Exception {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
						
		InputStream r1 = getClass().getClassLoader().getResourceAsStream("InvalidBottleMessage.msg");
		invalidMessage001 = new MimeMessage(session, r1);		
		
		InputStream r2 = getClass().getClassLoader().getResourceAsStream( "validBottleMessage.msg" );
		validMessage001 = new MimeMessage( session, r2 ); 
		

		validMessage002 = new MimeMessage(session);
		validMessage002.setText( "<li><i class=\"byline alertTime\" style=\"border-right: 1px solid grey;padding-right: 5px;float: left;clear: left;margin-right: 5px;\">03:45 PM</i>\r\n" + 
				"    Bottle  -\r\n" + 
				"\r\n" + 
				"                                                                                         6, \r\n" + 
				"                                             Formula\r\n" + 
				"                                        (Sample Sample)\r\n</li>" );
		
		validMessage003 = new MimeMessage(session);
		validMessage003.setText( "<li><i class=\"byline alertTime\" style=\"border-right: 1px solid grey;padding-right: 5px;float: left;clear: left;margin-right: 5px;\">08:44 AM</i>\r\n" + 
				"    Bottle  -\r\n" + 
				"\r\n" + 
				"                                                                                         6 1/2 oz, \r\n" + 
				"                                             Formula\r\n" + 
				"                                        (Ida Gaddy)\r\n" + 
				"                            \r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"             \r\n" + 
				"     \r\n" + 
				"     \r\n" + 
				"\r\n" + 
				"</li>"); 
	}

	@Test
	public void testGetTextFromMessage() {
		MessageReader mr = new MessageReader( validMessage001 );
		assertEquals( 6273, mr.contents.length() ); 	
		
		Pattern p = Pattern.compile( "\\(Sample Sample\\)"); 
		Matcher m = p.matcher( mr.contents );
		boolean matches = m.find();  
		assertTrue( matches ); 
		
		MessageReader mr2 = new MessageReader( validMessage002 );
		assertNotNull(mr2.contents );		
		assertTrue( p.matcher( mr.contents ).find() ); 
	}
	
	@Test
	public void testParseValidMessage() throws MessagingException {
		MessageReader mr = new MessageReader( validMessage001 );
		assertTrue( mr.isValid );		
		
		Calendar c = Calendar.getInstance();
		c.setTime( new Date() );
		c.set(Calendar.HOUR_OF_DAY, 12 );
		c.set(Calendar.MINUTE, 30 );
		
		assertEquals( 6.5, mr.ounces, .0001 );
		assertTrue( Math.abs(c.getTime().getTime() - mr.time.getTime()) < 1000 );
	}
	
	@Test
	public void testParseInvalidMessage() throws MessagingException {
		MessageReader mr = new MessageReader( invalidMessage001 );
		assertFalse( mr.isValid );
		assertEquals( 0, mr.ounces, .0001 );		
	}
	
	@Test
	public void testMessageWithWholeOunces() throws MessagingException {
		MessageReader mr = new MessageReader( validMessage002 );
		assertTrue( "Message 2 is not valid", mr.isValid );		
		assertEquals( 6, mr.ounces, .0001 );
		
		Calendar c = Calendar.getInstance();
		c.setTime( new Date() );
		c.set(Calendar.HOUR_OF_DAY, 15 );
		c.set(Calendar.MINUTE, 45 );
		assertTrue( Math.abs(c.getTime().getTime() - mr.time.getTime()) < 1000 );
	}
	
	@Test
	public void testMessageWithAmDate() throws MessagingException {
		MessageReader mr = new MessageReader( validMessage003 );
		assertTrue( "Message 3 is not valid", mr.isValid );		
		assertEquals( 6.5, mr.ounces, .0001 );
		
		Calendar c = Calendar.getInstance();
		c.setTime( new Date() );
		c.set(Calendar.HOUR_OF_DAY, 8 );
		c.set(Calendar.MINUTE, 44 );
		assertTrue( Math.abs(c.getTime().getTime() - mr.time.getTime()) < 1000 );
	}
}
