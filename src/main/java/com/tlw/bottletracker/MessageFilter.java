package com.tlw.bottletracker;

import javax.mail.Address;

public class MessageFilter
{
  private static String subjectPattern = "Bottle Event Alert";
  private static String fromPattern = "KidReports Notifier <noreply@kidreports.com>";
  
  public boolean matches(String subject, Address[] from) {
    if (!subjectPattern.equals(subject)) {
      return false;
    }
    
    for (int x = 0; x < from.length; x++) {

      
      if (fromPattern.equals(from[x].toString())) {
        return true;
      }
    } 
    
    return false;
  }
}
