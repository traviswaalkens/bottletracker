package com.tlw.bottletracker;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;

import com.sun.mail.imap.IMAPFolder;

public class App {
  public static void main(String[] args) {
    Properties credentialsProperties, props = new Properties();
    
    PropertyReader pr = new PropertyReader();
    
    try {
      credentialsProperties = pr.getCredentialsProperties();
    } catch (IOException e) {      
      e.printStackTrace();      
      return;
    } 
    
    String host = credentialsProperties.getProperty("host");
    String username = credentialsProperties.getProperty("username");
    String password = credentialsProperties.getProperty("password");
    String provider = credentialsProperties.getProperty("provider");
    String port = credentialsProperties.getProperty("port");
        
    try { 
      Session session = Session.getDefaultInstance(props, null);
      Store store = session.getStore(provider);
      store.connect(host, Integer.parseInt(port), username, password);
      
      Folder inbox = store.getFolder("INBOX");
      inbox.open(Folder.READ_WRITE);
      
      // Folder receptacle = store.getFolder( "recorded_events");
      // receptacle.open(Folder.READ_WRITE);
      
      Folder archive = store.getFolder( "Archive" ); 
      archive.open( Folder.READ_ONLY );
      	   
      Message[] messages = inbox.getMessages();
      System.out.println("found " + messages.length + " messages ");
      
      MessageFilter messageFilter = new MessageFilter();
      int max = 50;
      for (int i = 0; i < max; i++) {    	  
    	  
        Message message = messages[messages.length - i - 1];
        Address[] from = message.getFrom();
        String subject = message.getSubject();
        
        System.out.println("Checking- " + subject + " by " + from[0] );
        
        if( messageFilter.matches(subject, from) ) {        	
        	message.setFlag( Flags.Flag.SEEN, true);
        } else {
        	Message[] tempMessages = new Message[] {message}; 
        	
        	try {
        		inbox.copyMessages( tempMessages, archive );        		
        		inbox.setFlags(tempMessages, new Flags( Flags.Flag.DELETED ), true );        		
        	} catch (Exception ex ) {
        		System.out.println( "Failed copy or delete message:"+ ex.getMessage() ); 
        	}
        }
        /*
		folder.copyMessages(msgs, dfolder);
		folder.setFlags(msgs, new Flags(Flags.Flag.DELETED), true);

		// Dump out the Flags of the moved messages, to insure that
		// all got deleted
		for (int i = 0; i < msgs.length; i++) {
		    if (!msgs[i].isSet(Flags.Flag.DELETED))
			System.out.println("Message # " + msgs[i] + 
						" not deleted");
		}
		*/
      }

      inbox.close(false);
      // receptacle.close(false);
      archive.close(false);
      
      store.close();
    } catch (NoSuchProviderException nspe) {
      System.err.println("invalid provider name");
    } catch (MessagingException me) {
      System.err.println("messaging exception");
      me.printStackTrace();
    } 
  }
}
