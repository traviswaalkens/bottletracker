package com.tlw.bottletracker;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;
import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

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
    
    System.out.println("Host: " + host);
    System.out.println("Username: " + username);
    System.out.println("Password: " + password);
    System.out.println("provider: " + provider);
    
    try {
      Session session = Session.getDefaultInstance(props, null);
      Store store = session.getStore(provider);
      store.connect(host, 993, username, password);

      
      Folder inbox = store.getFolder("INBOX");
      inbox.open(1);

      
      Message[] messages = inbox.getMessages();
      System.out.println("found " + messages.length + " messages ");
      
      TreeSet<String> treeSet = new TreeSet<String>();
      
      MessageFilter messageFilter = new MessageFilter();
      int max = 50;
      for (int i = 0; i < max; i++) {

        
        Message message = messages[messages.length - i - 1];
        Address[] from = message.getFrom();
        String subject = message.getSubject();
        
        System.out.println("Checking- " + subject + " by " + from[0] + " matches? " + messageFilter.matches(subject, from));
      } 
      
      Iterator<String> it = treeSet.iterator();
      while (it.hasNext()) {
        System.out.println("from: " + (String)it.next());
      }

      inbox.close(false);
      store.close();
    } catch (NoSuchProviderException nspe) {
      System.err.println("invalid provider name");
    } catch (MessagingException me) {
      System.err.println("messaging exception");
      me.printStackTrace();
    } 
  }
  
  private static String getFrom(Message javaMailMessage) throws MessagingException {
    String from = "";
    
    Address[] a = javaMailMessage.getFrom();
    
    if (a == null)
      return null; 
    for (int i = 0; i < a.length; i++) {
      Address address = a[i];
      from = String.valueOf(from) + address.toString();
    } 
    
    return from;
  }
}
