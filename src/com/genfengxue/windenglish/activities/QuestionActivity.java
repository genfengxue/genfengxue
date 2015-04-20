/**
 * 提问页面@author vita
 */

package com.genfengxue.windenglish.activities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Address;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.genfengxue.windenglish.PopupAuthenticator;
import com.genfengxue.windenglish.R;
import com.genfengxue.windenglish.mgr.AccountMgr;
import com.genfengxue.windenglish.struct.UserProfile;
import com.sun.mail.util.MailSSLSocketFactory;

public class QuestionActivity extends Activity {
	private Context  context = this;  
	private EditText editText;
	private static String tempQuestion = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.question);
		final Intent intent = getIntent();
		
		//配置文本框
		final String hintString = intent.getStringExtra("Text");
		
		//读出临时文件的内容
		try{   
	         FileInputStream fin = openFileInput("questionTemp");   
	         int length = fin.available();   
	         byte [] buffer = new byte[length];   
	         fin.read(buffer);       
	         tempQuestion = EncodingUtils.getString(buffer, "UTF-8");   
	         fin.close();       
	     }   
	     catch(Exception e){   
	         e.printStackTrace();   
	     }   
		editText = (EditText) findViewById(R.id.questionText);
		if(tempQuestion.length()!=0)
		{
			editText.setText(tempQuestion);
		}
		editText.setFocusable(true);   
		editText.setHint("你对于" + hintString + "有什么疑问吗？");
		editText.setFocusableInTouchMode(true);   
		editText.requestFocus();  
		
		//配置back按钮
		Button backButton = (Button)findViewById(R.id.questionBackButton);
		backButton.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FileOutputStream out = null;  
				tempQuestion=editText.getText().toString();
		        try {  
		            out = context.openFileOutput("questionTemp", Context.MODE_PRIVATE);  
		            out.write(tempQuestion.getBytes("UTF-8"));  
			        out.close();
		        } catch (Exception e) {  
					// TODO Auto-generated catch block
		            e.printStackTrace();  
		        }  
		        QuestionActivity.this.finish();
			}
		});
		
		//配置提交按钮
		Button subButton = (Button)findViewById(R.id.questionSubButton);
		subButton.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//获取信息
				UserProfile userProfile = AccountMgr.getUserProfile(QuestionActivity.this);
				int courseNo = intent.getIntExtra("courseNo", 1);
				int lessonNo = intent.getIntExtra("lessonNo", 1);
				
				String nickname = userProfile.getNickname();
				String email = userProfile.getEmail();
				
				//组装邮件
				Resources res = getResources();
				String mailTitle = res.getString(R.string.question_email_subject, nickname);
				String mailText  = res.getString(R.string.question_email_content, nickname, courseNo, lessonNo);

				mailText+="原文是："+hintString+ "\n";
				mailText+=editText.getText().toString();
				mailText+="\n";
				mailText+="邮箱地址是：";
				mailText+=email;
				
				//准备邮件
			    Address[] to = null;
				try {
					to = new Address[]{new InternetAddress("819432228@qq.com")};
				} catch (AddressException e1) {
					// 
					e1.printStackTrace();
				}
		        Properties props = new Properties();  
		        props.put("mail.smtp.auth","true");        
		        props.put("mail.transport.protocol","smtp");        
		        

		        //SSL
		        props.put("mail.smtp.ssl.enable", "true");  
		        MailSSLSocketFactory sf;
				try {
					sf = new MailSSLSocketFactory();
			        sf.setTrustAllHosts(true);  
			        props.put("mail.smtp.ssl.socketFactory", sf);  
				} catch (GeneralSecurityException e1) {
					e1.printStackTrace();
				}  
		        
		        PopupAuthenticator auth = new PopupAuthenticator();
		        Session session = Session.getInstance(props, auth);
		       
		          
		        // 邮件内容对象组装  
		        MimeMessage message = new MimeMessage(session);  
		        try  
		        {   		  
		        	message.setSubject(mailTitle);
		        	message.setFrom(new InternetAddress("service@genfengxue.com"));
		        	
		        	Multipart multipart = new MimeMultipart();   
		        	
		        	MimeBodyPart contentPart = new MimeBodyPart();
		        	contentPart.setText(mailText);
		        	multipart.addBodyPart(contentPart);

		            message.setContent(multipart);
		            
		              
		            // 获取SMTP协议客户端对象，连接到指定SMPT服务器  
		            Transport transport = session.getTransport();  
		            transport.connect("smtp.exmail.qq.com",465,"service@genfengxue.com","gfx100");  
		            System.out.println("connet it success!!!!");  
		              
		            MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
		            mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
		            mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
		            mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
		            mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
		            mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
		            CommandMap.setDefaultCommandMap(mc);
		            
		            // 发送邮件到SMTP服务器
		            transport.sendMessage(message,to);
		            // 关闭连接  
		            transport.close();  
		        }
		        catch(Exception e)  
		        {  
		            e.printStackTrace();  
		        }  

	            Dialog alertDialogAfterSub = new AlertDialog.Builder(QuestionActivity.this)
	            .setMessage("提问成功！")
	    		.setCancelable(false)
				.setPositiveButton("确定",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 
						FileOutputStream out = null;  
						tempQuestion="";
				        try {  
				            out = context.openFileOutput("questionTemp", Context.MODE_PRIVATE);  
				            out.write(tempQuestion.getBytes("UTF-8"));  
					        out.close();
				        } catch (Exception e) {  
				            e.printStackTrace();  
				        }  
						QuestionActivity.this.finish();
					}
				})
				.create();
	            alertDialogAfterSub.show();
		        
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		FileOutputStream out = null;  
		tempQuestion=editText.getText().toString();
        try {  
            out = context.openFileOutput("questionTemp", Context.MODE_PRIVATE);  
            out.write(tempQuestion.getBytes("UTF-8"));  
	        out.close();
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        QuestionActivity.this.finish();
	}
	
}
