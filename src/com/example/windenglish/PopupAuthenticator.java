package com.example.windenglish;

import javax.mail.*;  

public class PopupAuthenticator extends Authenticator{  
   String userName="service@genfengxue.com";  
   String password="gfx100";  
      
   public PopupAuthenticator(){  
   }  
   protected PasswordAuthentication getPasswordAuthentication(){  
       return new PasswordAuthentication(userName, password);  
   }  
}  