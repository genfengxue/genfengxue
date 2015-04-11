/**
 * 曾经测试用对答案类，已废弃@author vita
 */

package com.genfengxue.windenglish;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.lang.reflect.Field;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
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
import javax.mail.internet.MimeUtility;

import org.apache.http.util.EncodingUtils;

import com.genfengxue.windenglish.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
//import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
//import android.widget.MediaController;
//import android.widget.SeekBar;
//import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
//import android.widget.VideoView;


public class CheckBackup extends Activity{

	//private VideoView videoView;
	//private MediaController mediaController;
	private Button backButton;
	//private String fileName;
	private String datapath; 
	//private SeekBar seekBar;
	private TextView answer;
    private MediaPlayer player;
	private Context  context = this;  
    private String userdata;
    private String[] names;
    //private boolean isPlaying;
    private Button subButton;
    private Button backVideoButton;
    private Button pauseVideoButton;
    private RelativeLayout checkRelative;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		//初始化播放界面
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		//根据播放次数自定义布局
        Intent intent = getIntent();
        setContentView(R.layout.check);
        
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        
        //获取播放的视频文件名
        final String videoId = intent.getStringExtra("videoId");
        String path = "video";
        path = path + videoId;
        path = path + "_4";

        
        /*
		videoView=(VideoView)findViewById(R.id.checkVideo);
		mediaController = new MediaController(this,false);
        this.videoView.setMediaController(mediaController); 
        
        //设置播放路径
        Field field = null ;
        try {
        	 field = Class.forName("com.example.windenglish.R$raw").getField(path); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        try {
			videoView.setVideoURI(Uri.parse("android.resource://com.example.windenglish/"+field.getInt(field)));
		} catch (IllegalAccessException | IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
        
        /*
        mediaController.setVisibility(View.INVISIBLE);
        mediaController.setAnchorView(videoView);
        */
        

        answer = (TextView)findViewById(R.id.answer);
        //加载答案
        String answerName = "lesson";
        answerName += videoId;
        answerName += ".txt";
        AssetManager assetManager = getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(answerName);
            String text = loadTextFile(inputStream);
            answer.setText(text);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //设置音频播放参数
        player  =   new MediaPlayer();
        String  playerPath = Environment.getExternalStorageDirectory().getAbsolutePath();  
        playerPath = playerPath + "/windenglish/";
        playerPath = playerPath + videoId;
        playerPath = playerPath + ".3gp";
	    final String affix = playerPath;
        try {
			player.setDataSource(playerPath);
			player.prepare();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        player.start();
        /*
        //弹出对话框
        Dialog alertDialogOnPlay = new AlertDialog.Builder(Check.this)
        							.setPositiveButton("开始对答案",new DialogInterface.OnClickListener() {
        								
        								@Override
        								public void onClick(DialogInterface dialog, int which) {
        									// TODO Auto-generated method stub

        									
        							        //初始化进度条
        							        seekBar = (SeekBar) findViewById(R.id.checkSeekBar);
        							        seekBar.setOnSeekBarChangeListener(change);
        							        seekBar.setMax(player.getDuration());
        							        
        							        new Thread() {
        							            
        							            @Override
        							            public void run() {
        							                try {
        							                    isPlaying = true;
        							                    while (isPlaying) {
        							                        // 如果正在播放，每0.5秒更新一次进度条
        							                        int current = player.getCurrentPosition();
        							                        seekBar.setProgress(current);
        							                        sleep(500);
        							                    }
        							                } catch (Exception e) {
        							                    e.printStackTrace();
        							                }
        							            }
        							        }.start();
        							        
        									videoView.start();
        									player.start();
        									
        								}
        							})
        							.setCancelable(false)
        							.create();
        alertDialogOnPlay.show();
        */
        //设置back按钮

		backButton = (Button)findViewById(R.id.checkBackButton);
		backButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v)
			{
				//videoView.pause();
				player.pause();
				Dialog alertDialog = new AlertDialog.Builder(CheckBackup.this)
				.setMessage("现在退出，下次要重新对答案")
				.setCancelable(false)
				.setPositiveButton("继续对答案",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						//videoView.start();
						player.start();
						
					}
				})
				.setNegativeButton("确认退出",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						Intent intent = new Intent(CheckBackup.this, Main.class);
						startActivity(intent);
						CheckBackup.this.finish();
					}
				})
				.create();
	        alertDialog.show(); 
			}
		});
		
        /*
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
         @Override
         public void onCompletion(MediaPlayer mp)
         {
             //播放结束后的动作
         }
        });
        */
        
		final int videoID = Integer.parseInt(videoId);
		player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				//播放结束后的动作
				
				if(notSub(videoID))
				{
				//弹出提交提示框
				Dialog alertDialogOver = new AlertDialog.Builder(CheckBackup.this)
				.setMessage("对答案完成！")
				.setCancelable(false)
				.setPositiveButton("提交",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						//获取用户名信息
						datapath="userdata";
						try{   
					         FileInputStream fin = openFileInput(datapath);   
					         int length = fin.available();   
					         byte [] buffer = new byte[length];   
					         fin.read(buffer);       
					         userdata = EncodingUtils.getString(buffer, "UTF-8");   
					         fin.close();       
					     }   
					     catch(Exception e){   
					         e.printStackTrace();   
					     }   
						names=userdata.split("\\?");
						
						
						//发送邮件
					    String email = "819432228@qq.com";
					    String title = "【跟风学】" + names[0] + " Lesson " + videoId;
					    String affixName = names[0] + "_" + videoId + ".3gp";
					    Address[] to = null;
						try {
							to = new Address[]{new InternetAddress(email)};
						} catch (AddressException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				        Properties props = new Properties();  
				        props.put("mail.smtp.auth","true");        
				        props.put("mail.transport.protocol","smtp");        
				        
				        
				        PopupAuthenticator auth = new PopupAuthenticator();
				        Session session = Session.getInstance(props, auth);
				       
				          
				        // 邮件内容对象组装  
				        MimeMessage message = new MimeMessage(session);  
				        try  
				        {   		  
				        	message.setSubject(title);
				        	message.setFrom(new InternetAddress("sunruxiao35635@163.com"));
				        	
				        	Multipart multipart = new MimeMultipart();   
				        	
				        	MimeBodyPart contentPart = new MimeBodyPart();
				        	contentPart.setText("WindEnglish录音");
				        	multipart.addBodyPart(contentPart);

				            MimeBodyPart messageBodyPart= new MimeBodyPart();
				            FileDataSource source = new FileDataSource(affix);
				            messageBodyPart.setDataHandler(new DataHandler(source));
				            messageBodyPart.setFileName(MimeUtility.encodeWord(affixName));
				            multipart.addBodyPart(messageBodyPart);

				            message.setContent(multipart);
				            
				              
				            // 获取SMTP协议客户端对象，连接到指定SMPT服务器  
				            Transport transport = session.getTransport();  
				            transport.connect("smtp.163.com",25,"sunruxiao35635","sunruxiao35635");  
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
				            System.out.println("send it success!!!!");    
				            subButton.setText("已提交");
				            subButton.setTextColor(0xFFDDDDDD);
				            // 关闭连接  
				            transport.close();  
				            

							 //修改用户状态
							 int videoIdInt = Integer.parseInt( videoId ); 
							 
							 String userdata = "";
							 String dataname = "userdata";
							 try{   
						          FileInputStream fin = openFileInput(dataname);   
						          int length = fin.available();   
						          byte [] buffer = new byte[length];   
						          fin.read(buffer);       
						          userdata = EncodingUtils.getString(buffer, "UTF-8");   
						          fin.close();       
						      }   
						      catch(Exception e){   
						          e.printStackTrace();   
						      }   
							 String names[]=userdata.split("\\?");
							 char[] states=names[1].toCharArray();
							 states[videoIdInt]='2';
							 String doneUserdata = names[0];
							 doneUserdata += "?";
							 String doneUserdataPart2 = new String(states);
							 doneUserdata += doneUserdataPart2;
							 
							FileOutputStream out = null;  
					        try {  
					            out = context.openFileOutput(dataname, Context.MODE_PRIVATE);  
					            out.write(doneUserdata.getBytes("UTF-8"));  
						        out.close();
					        } catch (Exception e) {  
								// TODO Auto-generated catch block
					            e.printStackTrace();  
					            
					        }  
				            
				            //返回成功信息
				            Dialog alertDialogAfterSub = new AlertDialog.Builder(CheckBackup.this)
							.setMessage("作业提交成功！")
							.setPositiveButton("返回主菜单",new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
									Intent intent = new Intent(CheckBackup.this, Main.class);
									startActivity(intent);
									CheckBackup.this.finish();
								}
							})
							.setNegativeButton("取消",new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									;
								}
							})
							.create();
				            alertDialogAfterSub.show();
				        }  
				        catch(Exception e)  
				        {  
				            e.printStackTrace();  
				        }  
					}
				})
				.setNegativeButton("退出",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						Intent intent = new Intent(CheckBackup.this, Main.class);
						startActivity(intent);
						CheckBackup.this.finish();
					}
				})
				.create();
		    alertDialogOver.show(); 
				
			}
		}

			private boolean notSub(int videoID) {
				String userdata = "";
				String dataname = "userdata";
				 try{   
			          FileInputStream fin = openFileInput(dataname);   
			          int length = fin.available();   
			          byte [] buffer = new byte[length];   
			          fin.read(buffer);       
			          userdata = EncodingUtils.getString(buffer, "UTF-8");   
			          fin.close();       
			      }   
			      catch(Exception e){   
			          e.printStackTrace();   
			      }   
				 String names[]=userdata.split("\\?");
				 char[] states=names[1].toCharArray();
				 if(states[videoID]=='2')
					 return false;
				 else
					 return true;
			}
		});
		
		//设置提交按钮
		subButton = (Button)findViewById(R.id.checkSubButton);
		if(!notSub(videoID))
		{
			  
            subButton.setText("已提交");
            subButton.setTextColor(0xFFBBBBBB);
		}
		subButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v)
			{

				final int videoID = Integer.parseInt(videoId);
				if(notSub(videoID))
				{
					subButton.setText("提交中");
				
				//获取用户名信息
				datapath="userdata";
				try{   
			         FileInputStream fin = openFileInput(datapath);   
			         int length = fin.available();   
			         byte [] buffer = new byte[length];   
			         fin.read(buffer);       
			         userdata = EncodingUtils.getString(buffer, "UTF-8");   
			         fin.close();       
			     }   
			     catch(Exception e){   
			         e.printStackTrace();   
			     }   
				names=userdata.split("\\?");
				
				
				//发送邮件
			    String email = "819432228@qq.com";
			    String title = "【跟风学】" + names[0] + " Lesson " + videoId;
			    String affixName = names[0] + "_" + videoId + ".3gp";
			    Address[] to = null;
				try {
					to = new Address[]{new InternetAddress(email)};
				} catch (AddressException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        Properties props = new Properties();  
		        props.put("mail.smtp.auth","true");        
		        props.put("mail.transport.protocol","smtp");        
		        
		        
		        PopupAuthenticator auth = new PopupAuthenticator();
		        Session session = Session.getInstance(props, auth);
		       
		          
		        // 邮件内容对象组装  
		        MimeMessage message = new MimeMessage(session);  
		        try  
		        {   		  
		        	message.setSubject(title);
		        	message.setFrom(new InternetAddress("sunruxiao35635@163.com"));
		        	
		        	Multipart multipart = new MimeMultipart();   
		        	
		        	MimeBodyPart contentPart = new MimeBodyPart();
		        	contentPart.setText("WindEnglish录音");
		        	multipart.addBodyPart(contentPart);

		            MimeBodyPart messageBodyPart= new MimeBodyPart();
		            FileDataSource source = new FileDataSource(affix);
		            messageBodyPart.setDataHandler(new DataHandler(source));
		            messageBodyPart.setFileName(MimeUtility.encodeWord(affixName));
		            multipart.addBodyPart(messageBodyPart);

		            message.setContent(multipart);
		            
		              
		            // 获取SMTP协议客户端对象，连接到指定SMPT服务器  
		            Transport transport = session.getTransport();  
		            transport.connect("smtp.163.com",25,"sunruxiao35635","sunruxiao35635");  
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
		            System.out.println("send it success!!!!");  
		            subButton.setText("已提交");
		            subButton.setTextColor(0xFFBBBBBB);
		            //demo
		            

					 //修改用户状态
					 int videoIdInt = Integer.parseInt( videoId ); 
					 
					 String userdata = "";
					 String dataname = "userdata";
					 try{   
				          FileInputStream fin = openFileInput(dataname);   
				          int length = fin.available();   
				          byte [] buffer = new byte[length];   
				          fin.read(buffer);       
				          userdata = EncodingUtils.getString(buffer, "UTF-8");   
				          fin.close();       
				      }   
				      catch(Exception e){   
				          e.printStackTrace();   
				      }   
					 String names[]=userdata.split("\\?");
					 char[] states=names[1].toCharArray();
					 states[videoIdInt]='2';
					 String doneUserdata = names[0];
					 doneUserdata += "?";
					 String doneUserdataPart2 = new String(states);
					 doneUserdata += doneUserdataPart2;
					 
					FileOutputStream out = null;  
			        try {  
			            out = context.openFileOutput(dataname, Context.MODE_PRIVATE);  
			            out.write(doneUserdata.getBytes("UTF-8"));  
				        out.close();
			        } catch (Exception e) {  
						// TODO Auto-generated catch block
			            e.printStackTrace();  
			            
			        }  
		            
		              
		            // 关闭连接  
		            transport.close();  
		            
		            //返回成功信息
		            Dialog alertDialogAfterSub = new AlertDialog.Builder(CheckBackup.this)
		            .setMessage("作业提交成功！")
		    		.setCancelable(false)
					.setNegativeButton("返回主菜单",new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
							Intent intent = new Intent(CheckBackup.this, Main.class);
							startActivity(intent);
							CheckBackup.this.finish();
						}
					})
					.setPositiveButton("取消",new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							;
						}
					})
					.create();
		            alertDialogAfterSub.show();
		        }  
		        catch(Exception e)  
		        {  
		            e.printStackTrace();  
		        }  
			}}

			private boolean notSub(int videoID) {
				// TODO Auto-generated method stub
				String userdata = "";
				String dataname = "userdata";
				 try{   
			          FileInputStream fin = openFileInput(dataname);   
			          int length = fin.available();   
			          byte [] buffer = new byte[length];   
			          fin.read(buffer);       
			          userdata = EncodingUtils.getString(buffer, "UTF-8");   
			          fin.close();       
			      }   
			      catch(Exception e){   
			          e.printStackTrace();   
			      }   
				 String names[]=userdata.split("\\?");
				 char[] states=names[1].toCharArray();
				 if(states[videoID]=='2')
					 return false;
				 else
					 return true;
			}
		});

		//设置向前按钮
		backVideoButton = (Button)findViewById(R.id.backVideoButton);
		backVideoButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v)
			{
				/*
				int currentTime = videoView.getCurrentPosition();
				currentTime -= 3000;
				videoView.seekTo(currentTime);
				*/
				
				int currentTime = player.getCurrentPosition();
				currentTime -= 3000;
				player.seekTo(currentTime);
			}
		});
		
		//设置暂停按钮
		pauseVideoButton = (Button)findViewById(R.id.pauseVideoButton);
		pauseVideoButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v)
			{
				if(player.isPlaying())
				{
					//videoView.pause();
					player.pause();
					pauseVideoButton.setBackgroundResource(R.drawable.play);
				}
				else
				{
					//videoView.start();
					player.start();
					pauseVideoButton.setBackgroundResource(R.drawable.pause);
				}
			}
		});
		
		checkRelative = (RelativeLayout)findViewById(R.id.checkRelative);
		checkRelative.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(player.isPlaying())
				{
					//videoView.pause();
					player.pause();
					pauseVideoButton.setBackgroundResource(R.drawable.play);
				}
				else
				{
					//videoView.start();
					player.start();
					pauseVideoButton.setBackgroundResource(R.drawable.pause);
				}
				
			}
		});
		
		
	}


    private boolean notSub(int videoID) {
		// TODO Auto-generated method stub
		String userdata = "";
		String dataname = "userdata";
		 try{   
	          FileInputStream fin = openFileInput(dataname);   
	          int length = fin.available();   
	          byte [] buffer = new byte[length];   
	          fin.read(buffer);       
	          userdata = EncodingUtils.getString(buffer, "UTF-8");   
	          fin.close();       
	      }   
	      catch(Exception e){   
	          e.printStackTrace();   
	      }   
		 String names[]=userdata.split("\\?");
		 char[] states=names[1].toCharArray();
		 if(states[videoID]=='2')
			 return false;
		 else
			 return true;
	}


	private String loadTextFile(InputStream inputStream) throws IOException {
    	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    	byte[] bytes = new byte[4096];
    	int len = 0;
    	while ((len = inputStream.read(bytes)) > 0) {
    		byteArrayOutputStream.write(bytes, 0, len);   
    	}
    	return new String(byteArrayOutputStream.toByteArray(), "UTF8");
    }


    /*
	@Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setKeepScreenOn(true);
        if(null != videoView) {
            videoView.resume();
        }
    }
   
    @Override
    protected void onPause() {
        super.onPause();
        getWindow().getDecorView().setKeepScreenOn(false);
        if(null != videoView) {
            videoView.pause();
        }
    }
   
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != videoView) {
            videoView.stopPlayback();
            videoView = null;
        }
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        int sec = (int) outState.getLong("time");
            videoView.seekTo(sec);
        super.onRestoreInstanceState(outState);
        }
     
    @Override
    protected void onSaveInstanceState(Bundle outState) {
            int sec = videoView.getCurrentPosition();
        outState.putLong("time", sec);
        super.onSaveInstanceState(outState);
        }
    */
    
    @Override
	public void onBackPressed(){
		//videoView.pause();
		player.pause();
		Dialog alertDialog = new AlertDialog.Builder(CheckBackup.this)
		.setMessage("现在退出，下次要重新对答案")
		.setCancelable(false)
		.setPositiveButton("继续对答案",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				//videoView.start();
				player.start();
				
			}
		})
		.setNegativeButton("确认退出",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(CheckBackup.this, Main.class);
				startActivity(intent);
				CheckBackup.this.finish();
			}
		})
		.create();
    alertDialog.show(); 
    }
    
    
    
    /*
    //设置进度条
    private OnSeekBarChangeListener change = new OnSeekBarChangeListener() {
        
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 当进度条停止修改的时候触发
            // 取得当前进度条的刻度
            int progress = seekBar.getProgress();
            if (videoView != null && videoView.isPlaying()) {
                // 设置当前播放的位置
            	videoView.seekTo(progress);
            }
            if (player != null && player.isPlaying()) {
                // 设置当前播放的位置
            	player.seekTo(progress);
            }
        }
    
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
    
        }
    
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                boolean fromUser) {
    
        }
    };*/
}
