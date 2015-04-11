/**
 * SSL方式发送邮件所需要的凭证@author vita
 */

package com.genfengxue.windenglish;

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