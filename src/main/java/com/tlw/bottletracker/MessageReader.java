package com.tlw.bottletracker;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;

public class MessageReader {
	public Message msg;
	public String contents;
	public boolean isValid = false; 
	
	public float ounces;
	public Date time; 
	
	MessageReader( Message m ){
		msg = m;
		
		try {
			contents = getTextFromMessage( msg );
			parseContents();
			
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/*
	 * https://stackoverflow.com/questions/11240368/how-to-read-text-inside-body-of-mail-using-javax-mail
	 */
	public String getTextFromMessage(Message message) throws MessagingException, IOException {
	    String result = "";
	    
	    // this shouldn't happen, kids report are html emails with multipart 
	    if (message.isMimeType("text/plain")) {
	        result = message.getContent().toString();
	    } 
	    else if (message.isMimeType("multipart/*")) {
	        MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
	        result = getTextFromMimeMultipart(mimeMultipart);
	    }
	    
	    return result;
	}

	/*
	 * 
	 * With modifications from :https://stackoverflow.com/questions/11240368/how-to-read-text-inside-body-of-mail-using-javax-mail
	 */
	public String getTextFromMimeMultipart(MimeMultipart mimeMultipart)  
		throws MessagingException, IOException{
		
	    String result = "";
	    int count = mimeMultipart.getCount();
	    
	    for (int i = 0; i < count; i++) {
	        BodyPart bodyPart = mimeMultipart.getBodyPart(i);
	        
	        if( bodyPart.isMimeType( "text/html" ) ) {
	        	result = bodyPart.getContent().toString();
	        	break;
	        }
	        
	        // Kids report doesn't provide text/plain version and we aren't prepared to parse it.
	        /*
	        if (bodyPart.isMimeType("text/plain")) {
	        	continue;
	        }
	        else if (bodyPart.isMimeType("text/html")) {
	            String html = (String) bodyPart.getContent();
	            result = html; 
	        }
	        // this hasn't happened yet, nor do I think it's correct to append the data so commenting out.
	        
	        else if (bodyPart.getContent() instanceof MimeMultipart){
	            result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
	        }
	        */
	    }
	    return result;
	}
	
	public void parseContents() throws MessagingException {
		ounces = 0; 
		time = new Date();
		
		if( msg.getReceivedDate() != null ) {
			time = msg.getReceivedDate();
		}
		
		Calendar c = Calendar.getInstance(); 
		c.setTime(time);
 
		String regex = "(\\d+):(\\d+) ([AP]M)</i>\\s+Bottle\\s+-\\s+(\\d)\\s*(?:\\s(\\d)/(\\d)|(\\.\\d))?(?:\\s*oz)?,";
		
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher( contents ); 
		
		if( m.find() ) {
			ounces = 0; 
			int hour;
			
			hour = Integer.parseInt(m.group(1));
			
			if( m.group(3).equals("PM") && hour <12 ) {
				hour += 12;
			}
			
			c.set( Calendar.HOUR_OF_DAY, hour );
			c.set( Calendar.MINUTE, Integer.parseInt(m.group(2)));
			time = c.getTime();
			
			ounces += Integer.parseInt( m.group(4 ) ); 
			if( m.group(7) != null ) {
				// ".5";
				float p1; 
				p1 = Float.parseFloat( m.group(7) );
				ounces += p1;
			}
			else if( m.group(5) != null ) {
				float p1, p2, p3; 
				p1 = Float.parseFloat( m.group(5) );
				p2 = Float.parseFloat(m.group(6) );
				p3 = p1/p2;				
				ounces += p3;			
			}
			
			isValid = true; 
		} else {
			ounces = 0;
			time = null;			
		}
	}
}
