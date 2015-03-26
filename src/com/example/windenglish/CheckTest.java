package com.example.windenglish;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
//import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
//import android.widget.MediaController;
//import android.widget.SeekBar;
//import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
//import android.widget.VideoView;
import android.widget.TextView.BufferType;


public class CheckTest extends Activity{

	//private VideoView videoView;
	//private MediaController mediaController;
	private Button backButton;
	//private String fileName;
	private String datapath; 
	//private SeekBar seekBar;
	private static TextView answer;
	private static String jsonFileName;
	private static String KID;
	private static TextView knowledge;
    private static MediaPlayer player;
    private static Button likeButton;
    private static String likedPath;
	private Context context = this;  
    private String userdata;
    private static String jsonText;
    private static JSONArray jsonArray = new JSONArray();
    private static JSONArray jsonLikedArray = new JSONArray();
    private static int nowLine,nowNo;
    private String[] names;
    //private boolean isPlaying;
    private Button subButton;
    private Button backVideoButton;
    private static String[] keyNumbers;
    private static JSONObject shownKnowledge;
    private static Button pauseVideoButton;
    private static float floatX,floatY;
    private RelativeLayout checkRelative;
    private static File f;
    private static int H;
    static PopupWindow popupWindow;    
    private LayoutInflater inflater;
    private static View popLayout;
    private static View checkLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		//��ʼ�����Ž���
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		H = mDisplayMetrics.heightPixels;
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		//��ʼ��popupwindow
        inflater  = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        popLayout = inflater.inflate(R.layout.checkpopup,null);
        checkLayout = inflater.inflate(R.layout.checktest,null);
        popupWindow = new PopupWindow(popLayout, WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        popupWindow.update();
		
		//���ݲ��Ŵ����Զ��岼��
        Intent intent = getIntent();
        setContentView(R.layout.checktest);
        
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        
        //��ȡ���ŵ���Ƶ�ļ���
        final String videoId = intent.getStringExtra("videoId");
        String path = "video";
        path = path + videoId;
        path = path + "_4";

        
        /*
		videoView=(VideoView)findViewById(R.id.checkVideo);
		mediaController = new MediaController(this,false);
        this.videoView.setMediaController(mediaController); 
        
        //���ò���·��
        Field field = null ;
        try {
        	 field = Class.forName("com.example.windenglish.R$raw").getField(path); 
		} catch (Exception e) {
			// 
			e.printStackTrace();
		}
        
        try {
			videoView.setVideoURI(Uri.parse("android.resource://com.example.windenglish/"+field.getInt(field)));
		} catch (IllegalAccessException | IllegalArgumentException e) {
			// 
			e.printStackTrace();
		}
		*/
        
        /*
        mediaController.setVisibility(View.INVISIBLE);
        mediaController.setAnchorView(videoView);
        */

        answer = (TextView)findViewById(R.id.answer);
        
        knowledge = (TextView)popLayout.findViewById(R.id.checkPopupText);
        String ssss="This is a popupWindows";
		knowledge.setText(ssss);
        //���ش�
        //TODO
        //�ӷ������˻�ȡjson
        String jsonPath = "http://data.genfengxue.com/api/sentences?lessonNo=12";
        jsonText = null;
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet(jsonPath);
            HttpResponse httpResponse = httpClient.execute(httpget);
            int status = httpResponse.getStatusLine().getStatusCode();
            if(status == HttpStatus.SC_OK)
            {
                HttpEntity httpEntity = httpResponse.getEntity();
                if(httpEntity != null)
                {
                    jsonText = EntityUtils.toString(httpEntity,"utf8");
                }
            }
		} catch (Exception e) {
			// 
		}
        httpClient.getConnectionManager().shutdown();
        
        //�����ı�
        String englishText = "";
        try {
			jsonArray = new JSONArray(jsonText);
			for(int i=0;i<jsonArray.length();i++)
			{
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				englishText += jsonObject.getString("english");
				englishText += "\n";
				englishText += jsonObject.getString("chinese");
				englishText += "\n";
			}
			answer.setText(englishText,BufferType.SPANNABLE);
			
		} catch (JSONException e2) {
			// 
			e2.printStackTrace();
		}
        CheckTest.getEachWord(answer);
        answer.setMovementMethod(LinkMovementMethod.getInstance());
        
        //��ȡ��Ǽ�¼
        
        likedPath = "/data/data/com.example.windenglish/files/lessons";
        likedPath += videoId;
        f=new File(likedPath);
    	jsonFileName = "lessons";
    	jsonFileName += videoId;
        if(f.exists())
        {
        	String jsonLikedText = null;
    		try{   
    	         FileInputStream fin = openFileInput(jsonFileName);   
    	         int length = fin.available();   
    	         byte [] buffer = new byte[length];   
    	         fin.read(buffer);       
    	         jsonLikedText = EncodingUtils.getString(buffer, "UTF-8");   
    	         fin.close();       
    	     }   
    	     catch(Exception e){   
    	         e.printStackTrace();   
    	     }
        	try {
				jsonLikedArray = new JSONArray(jsonLikedText);
			} catch (JSONException e) {
				// 
				e.printStackTrace();
			}
			for(int i=0;i<jsonLikedArray.length();i++)
			{
				int line = 0;
				String keys = null;
				JSONObject eachKnowledge;
				try {
					eachKnowledge = jsonLikedArray.getJSONObject(i);
					keys = eachKnowledge.getString("key");
					line = eachKnowledge.getInt("line");
				} catch (JSONException e1) {
					// 
					e1.printStackTrace();
				}
				String[] keyss = keys.split(",");
				for(int j=0;j<keyss.length;j++)
				{
					int theno = Integer.parseInt(keyss[j]);
					changeHighLight(line, theno, 1);
				}
			}
        }
        
        
        /*
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
            // 
            e.printStackTrace();
        }
        */

        //������Ƶ���Ų���
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
			// 
			e.printStackTrace();
		}
        
        player.start();
        
        /*
        //�����Ի���
        Dialog alertDialogOnPlay = new AlertDialog.Builder(CheckTest.this)
        							.setPositiveButton("��ʼ�Դ�",new DialogInterface.OnClickListener() {
        								
        								@Override
        								public void onClick(DialogInterface dialog, int which) {
        									// 

        									
        							        //��ʼ��������
        							        seekBar = (SeekBar) findViewById(R.id.checkSeekBar);
        							        seekBar.setOnSeekBarChangeListener(change);
        							        seekBar.setMax(player.getDuration());
        							        
        							        new Thread() {
        							            
        							            @Override
        							            public void run() {
        							                try {
        							                    isPlaying = true;
        							                    while (isPlaying) {
        							                        // ������ڲ��ţ�ÿ0.5�����һ�ν�����
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
        //����back��ť

		backButton = (Button)findViewById(R.id.checkBackButton);
		backButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v)
			{
				//videoView.pause();
				player.pause();
				Dialog alertDialog = new AlertDialog.Builder(CheckTest.this)
				.setMessage("�����˳����´�Ҫ���¶Դ�")
				.setCancelable(false)
				.setPositiveButton("�����Դ�",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 

						//videoView.start();
						player.start();
						
					}
				})
				.setNegativeButton("ȷ���˳�",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 
						String jsonKnowledgeText = jsonLikedArray.toString();
						FileOutputStream outputStream;
						try {
							outputStream = openFileOutput(jsonFileName,  
							        Activity.MODE_PRIVATE);
					        outputStream.write(jsonKnowledgeText.getBytes());  
					        outputStream.flush();  
					        outputStream.close();  
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  
						Intent intent = new Intent(CheckTest.this, Main.class);
						startActivity(intent);
						CheckTest.this.finish();
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
             //���Ž�����Ķ���
         }
        });
        */
        
		final int videoID = Integer.parseInt(videoId);
		player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				//���Ž�����Ķ���
				
				if(notSub(videoID))
				{
				//�����ύ��ʾ��
				Dialog alertDialogOver = new AlertDialog.Builder(CheckTest.this)
				.setMessage("�Դ���ɣ�")
				.setCancelable(false)
				.setPositiveButton("�ύ",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 
						
						//��ȡ�û�����Ϣ
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
						
						
						//�����ʼ�
					    String email = "819432228@qq.com";
					    String title = "������ѧ��" + names[0] + " Lesson " + videoId;
					    String affixName = names[0] + "_" + videoId + ".3gp";
					    Address[] to = null;
						try {
							to = new Address[]{new InternetAddress(email)};
						} catch (AddressException e1) {
							// 
							e1.printStackTrace();
						}
				        Properties props = new Properties();  
				        props.put("mail.smtp.auth","true");        
				        props.put("mail.transport.protocol","smtp");        
				        
				        
				        PopupAuthenticator auth = new PopupAuthenticator();
				        Session session = Session.getInstance(props, auth);
				       
				          
				        // �ʼ����ݶ�����װ  
				        MimeMessage message = new MimeMessage(session);  
				        try  
				        {   		  
				        	message.setSubject(title);
				        	message.setFrom(new InternetAddress("sunruxiao35635@163.com"));
				        	
				        	Multipart multipart = new MimeMultipart();   
				        	
				        	MimeBodyPart contentPart = new MimeBodyPart();
				        	contentPart.setText("WindEnglish¼��");
				        	multipart.addBodyPart(contentPart);

				            MimeBodyPart messageBodyPart= new MimeBodyPart();
				            FileDataSource source = new FileDataSource(affix);
				            messageBodyPart.setDataHandler(new DataHandler(source));
				            messageBodyPart.setFileName(MimeUtility.encodeWord(affixName));
				            multipart.addBodyPart(messageBodyPart);

				            message.setContent(multipart);
				            
				              
				            // ��ȡSMTPЭ��ͻ��˶������ӵ�ָ��SMPT������  
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
				            
				            // �����ʼ���SMTP������
				            transport.sendMessage(message,to);
				            System.out.println("send it success!!!!");    
				            subButton.setText("���ύ");
				            subButton.setTextColor(0xFFDDDDDD);
				            // �ر�����  
				            transport.close();  
				            

							 //�޸��û�״̬
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
					            e.printStackTrace();  
					            
					        }  
				            
				            //���سɹ���Ϣ
				            Dialog alertDialogAfterSub = new AlertDialog.Builder(CheckTest.this)
							.setMessage("��ҵ�ύ�ɹ���")
							.setPositiveButton("�������˵�",new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									String jsonKnowledgeText = jsonLikedArray.toString();
									FileOutputStream outputStream;
									try {
										outputStream = openFileOutput(jsonFileName,  
										        Activity.MODE_PRIVATE);
								        outputStream.write(jsonKnowledgeText.getBytes());  
								        outputStream.flush();  
								        outputStream.close();  
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}  
									Intent intent = new Intent(CheckTest.this, Main.class);
									startActivity(intent);
									CheckTest.this.finish();
								}
							})
							.setNegativeButton("ȡ��",new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
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
				.setNegativeButton("�˳�",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String jsonKnowledgeText = jsonLikedArray.toString();
						FileOutputStream outputStream;
						try {
							outputStream = openFileOutput(jsonFileName,  
							        Activity.MODE_PRIVATE);
					        outputStream.write(jsonKnowledgeText.getBytes());  
					        outputStream.flush();  
					        outputStream.close();  
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  
						Intent intent = new Intent(CheckTest.this, Main.class);
						startActivity(intent);
						CheckTest.this.finish();
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
		
		//�����ύ��ť
		subButton = (Button)findViewById(R.id.checkSubButton);
		if(!notSub(videoID))
		{
			  
            subButton.setText("���ύ");
            subButton.setTextColor(0xFFBBBBBB);
		}
		subButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v)
			{

				final int videoID = Integer.parseInt(videoId);
				if(notSub(videoID))
				{
					subButton.setText("�ύ��");
				
				//��ȡ�û�����Ϣ
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
				
				
				//�����ʼ�
			    String email = "819432228@qq.com";
			    String title = "������ѧ��" + names[0] + " Lesson " + videoId;
			    String affixName = names[0] + "_" + videoId + ".3gp";
			    Address[] to = null;
				try {
					to = new Address[]{new InternetAddress(email)};
				} catch (AddressException e1) {
					// 
					e1.printStackTrace();
				}
		        Properties props = new Properties();  
		        props.put("mail.smtp.auth","true");        
		        props.put("mail.transport.protocol","smtp");        
		        
		        
		        PopupAuthenticator auth = new PopupAuthenticator();
		        Session session = Session.getInstance(props, auth);
		       
		          
		        // �ʼ����ݶ�����װ  
		        MimeMessage message = new MimeMessage(session);  
		        try  
		        {   		  
		        	message.setSubject(title);
		        	message.setFrom(new InternetAddress("sunruxiao35635@163.com"));
		        	
		        	Multipart multipart = new MimeMultipart();   
		        	
		        	MimeBodyPart contentPart = new MimeBodyPart();
		        	contentPart.setText("WindEnglish¼��");
		        	multipart.addBodyPart(contentPart);

		            MimeBodyPart messageBodyPart= new MimeBodyPart();
		            FileDataSource source = new FileDataSource(affix);
		            messageBodyPart.setDataHandler(new DataHandler(source));
		            messageBodyPart.setFileName(MimeUtility.encodeWord(affixName));
		            multipart.addBodyPart(messageBodyPart);

		            message.setContent(multipart);
		            
		              
		            // ��ȡSMTPЭ��ͻ��˶������ӵ�ָ��SMPT������  
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
		            
		            // �����ʼ���SMTP������
		            transport.sendMessage(message,to);
		            System.out.println("send it success!!!!");  
		            subButton.setText("���ύ");
		            subButton.setTextColor(0xFFBBBBBB);
		            //demo
		            

					 //�޸��û�״̬
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
						// 
			            e.printStackTrace();  
			            
			        }  
		            
		              
		            // �ر�����  
		            transport.close();  
		            
		            //���سɹ���Ϣ
		            Dialog alertDialogAfterSub = new AlertDialog.Builder(CheckTest.this)
		            .setMessage("��ҵ�ύ�ɹ���")
		    		.setCancelable(false)
					.setNegativeButton("�������˵�",new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 
							String jsonKnowledgeText = jsonLikedArray.toString();
							FileOutputStream outputStream;
							try {
								outputStream = openFileOutput(jsonFileName,  
								        Activity.MODE_PRIVATE);
						        outputStream.write(jsonKnowledgeText.getBytes());  
						        outputStream.flush();  
						        outputStream.close();  
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}  
							Intent intent = new Intent(CheckTest.this, Main.class);
							startActivity(intent);
							CheckTest.this.finish();
						}
					})
					.setPositiveButton("ȡ��",new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 
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
				// 
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

		//������ǰ��ť
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
		
		//������ͣ��ť
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


	//:
	//�õ�ÿ��word
    private static void getEachWord(TextView answer2) {
    	//���ȵõ�ÿһ�� 
        int start = 0;        
        int end = 0;       
        int line = 1;
        int no = 1;
		String[] lineindices = answer2.getText().toString().split("\n");
        String checkTextString = answer2.getText().toString();
        char[] checkText = checkTextString.toCharArray();
        Spannable spans = (Spannable)answer2.getText();
        while(end < checkText.length)
        {
        	if(checkText[end+1]==' ')
        	{
        		while(checkText[end+1]==' ')
        		{
	            ClickableSpan clickSpan = getClickableSpan(line,no);      
	            spans.setSpan(clickSpan, start, end+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);    
	            no++;
	        	end+=2;
	        	start=end;
        		}
        	}
        	else if(checkText[end+1]=='\n')
        	{
	            ClickableSpan clickSpan = getClickableSpan(line,no);      
	            spans.setSpan(clickSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
        		end+=2;
        		while(checkText[end]!='\n')
        			end++;
        		end++;
        		start=end;
        		line++;
        		if(line>lineindices.length/2)
        		{
        			break;
        		}
        		no=1;
        		while(checkText[end+1]==' ')
            	{
    	            ClickableSpan clickSpan1 = getClickableSpan(line,no);      
    	            spans.setSpan(clickSpan1, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);     
    	            no++;
    	        	end+=2;
    	        	start=end;
            	}
        	}
        	end++;
        }
        answer2.setHighlightColor(Color.argb(180, 205, 205, 205)); 
        
	}

/*
    //�ָ�
	private static Integer[] getIndices(String s, char c) {
		//        
        int pos = s.indexOf(c, 0);        
        List<Integer> indices = new ArrayList<Integer>();        
        while (pos != -1) {            
            indices.add(pos);            
            pos = s.indexOf(c, pos + 1);        
        }        
        return (Integer[]) indices.toArray(new Integer[0]);    
	}
	*/

	//��Ӧ֪ʶ���¼�
	private static ClickableSpan getClickableSpan(final int line,final int no) {
		//          
        return new ClickableSpan() {                
            @Override                
            public void onClick(View widget) { 
              	/*     
            	TextView tv = (TextView) widget;   
                String s = tv                            
                        .getText()                            
                        .subSequence(tv.getSelectionStart(),                                    
                                tv.getSelectionEnd()).toString();   
                                */
            	CheckTest.changeHighLight(line, no, 2);
        		nowLine = line;
        		nowNo = no;
                CheckTest.showKnowledge(line,no);
            }                
            @Override                  
            public void updateDrawState(TextPaint ds) {                          
                ds.setColor(Color.BLACK);                        
                ds.setUnderlineText(false);   
            }                                             
        };
	}


	//��ʾ�ı���ʵ������ť
	protected static void showKnowledge(int line,int no){
		// ��ʼ��
		likeButton = (Button)popLayout.findViewById(R.id.checkLike);
		String textt ="Oops,There's nothing here!";
		knowledge.setText(textt);
		likeButton.setBackgroundResource(R.drawable.none);

		
		
		//ʵ������ť
		try {
			textt = getKnowledgeText(line,no);
		} catch (JSONException e) {
			// 
			e.printStackTrace();
		}
		try {
			KID = shownKnowledge.getString("_id");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(!textt.equals("nothing"))
		{
			if(hasLiked(KID))
			{
				likeButton.setBackgroundResource(R.drawable.liked);
			}
			else {
				likeButton.setBackgroundResource(R.drawable.like);
			}
			knowledge.setText(textt);
			likeButton.setOnClickListener(new Button.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(hasLiked(KID))
					{
						changeState(KID,0);
						for(int i=0;i<keyNumbers.length;i++)
						{
							changeHighLight(nowLine,Integer.parseInt(keyNumbers[i]),0);
							likeButton.setBackgroundResource(R.drawable.like);
						}
					}
					else 
					{
						changeState(KID,1);
						for(int i=0;i<keyNumbers.length;i++)
						{
							changeHighLight(nowLine,Integer.parseInt(keyNumbers[i]),1);
							likeButton.setBackgroundResource(R.drawable.liked);
						}
					}
				}
			});
		}
		player.pause();
		pauseVideoButton.setBackgroundResource(R.drawable.play);
        popupWindow.showAtLocation(checkLayout, Gravity.TOP|Gravity.LEFT, (int)floatX, (int)floatY);
	}



	protected static void changeHighLight(int line, int no, int state) {
		// TODO Auto-generated method stub

        Spannable spans = (Spannable)answer.getText();
        int start = 0,end = 0;
        String texts = (String) answer.getText().toString();
        char[] textss = texts.toCharArray();
        int i=line*2;
        while(i>2)
        {
        	if(textss[start]=='\n') i--;
        	start++;
        }
        i=no;
        while(i>1)
        {
        	if(textss[start]==' ') i--;
        	start++;
        }
        end=start;
        while(textss[end]!=' '&&textss[end]!='\n') 
        {
        	end++;
        }
        if(state == 0)
        {
        	spans.setSpan(new BackgroundColorSpan(Color.argb(0, 0, 0, 0)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else if (state == 1){
        	spans.setSpan(new BackgroundColorSpan(Color.argb(153, 101, 204, 202)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
        else {
        	spans.setSpan(new BackgroundColorSpan(Color.argb(153, 205, 205, 205)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}


	protected static void changeState(String KID2, int state) {
		// TODO Auto-generated method stub
		String id = null;
		if(state==1)
		{
			JSONObject newKnowledge = new JSONObject();
			try {
				String keys = shownKnowledge.getString("key");
				newKnowledge.put("_id", KID2);
				newKnowledge.put("line", nowLine);
				newKnowledge.put("key", keys);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			jsonLikedArray.put(newKnowledge);
		}
		else {
			for(int i=0;i<jsonLikedArray.length();i++)
			{
				try {
					JSONObject eachKnowledge = jsonLikedArray.getJSONObject(i);
					id=eachKnowledge.getString("_id");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(KID2.equals(id))
				{
				    java.lang.reflect.Field valuesField;
					try {
						valuesField = JSONArray.class.getDeclaredField("values");
					    valuesField.setAccessible(true);  
					    @SuppressWarnings("unchecked")
						List<Object> values=(List<Object>)valuesField.get(jsonLikedArray);  
					    if(i >= values.size())  
					        return;  
					    values.remove(i);  
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
				}
			}
		}
	}


	protected static boolean hasLiked(String KID) {
		// TODO Auto-generated method stub
		String id = null;
		for(int i=0;i<jsonLikedArray.length();i++)
		{
			try {
				JSONObject eachKnowledge = jsonLikedArray.getJSONObject(i);
				id=eachKnowledge.getString("_id");
				if(id.equals(KID))
				{
					return true;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}


	private static String getKnowledgeText(int line, int no) throws JSONException {
		// TODO Auto-generated method stub

		String result = "nothing";
		JSONArray jsonArray3 = new JSONArray(jsonText);
		for(int i=1;i<=jsonArray3.length();i++)
		{
			JSONObject knowledge = jsonArray3.getJSONObject(i-1);
			JSONArray keyPoints =knowledge.getJSONArray("keyPoints");
			for(int j=1;j<=keyPoints.length();j++)
			{
				JSONObject keyPoint = keyPoints.getJSONObject(j-1);
				String keys = keyPoint.getString("key");
				keyNumbers = keys.split(",");
				shownKnowledge = keyPoint;
				for(int k=0;k<keyNumbers.length;k++)
				{
					int keyNo = Integer.parseInt(keyNumbers[k]);
					if(i==line&&keyNo==no)
					{
						JSONArray results = keyPoint.getJSONArray("kps");
						result = "";
						for(int l=1;l<=results.length();l++)
						{
							JSONObject theKeyPoint = results.getJSONObject(l-1);
							JSONObject theKP = theKeyPoint.getJSONObject("kp");
							result += theKP.getString("text");
							result += "\n";
						}
						return result;
					}
				}
			}
			/*
			JSONArray jsonKnowledge = jsonArray.getJSONArray(i-1);
			for(int j=1;j<=jsonKnowledge.length();j++)
			{
				JSONObject perKnowledge = jsonKnowledge.getJSONObject(j-1);
				String keys = perKnowledge.getString("key");
			}
			englishText += jsonObject.getString("english");
			englishText += "\n";
			englishText += jsonObject.getString("chinese");
			englishText += "\n";
			*/
		}
		return result;
	}


	private boolean notSub(int videoID) {
		// 
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

/*
	private String loadTextFile(InputStream inputStream) throws IOException {
    	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    	byte[] bytes = new byte[4096];
    	int len = 0;
    	while ((len = inputStream.read(bytes)) > 0) {
    		byteArrayOutputStream.write(bytes, 0, len);   
    	}
    	return new String(byteArrayOutputStream.toByteArray(), "UTF8");
    }
*/

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
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// 
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			//������ɫ
			if(chosenHasLiked())
			{
	        	changeHighLight(nowLine, nowNo, 1);
			}
			else {
	        	changeHighLight(nowLine, nowNo, 0);
			}
			
			return super.dispatchTouchEvent(ev);
			
		default:
			//ȷ���������λ��
			floatX = ev.getX();
			floatY = ev.getY();
			int lineoffset = DensityUtil.dip2px(this, 16);
			if(floatY>(H/2))
			{
				int pxoffset = DensityUtil.dip2px(this, 128);
				floatY -= pxoffset;
				floatY -= lineoffset;
				floatY -= lineoffset;
				floatY -= lineoffset;
			}
			floatY+=lineoffset;
			
			return super.dispatchTouchEvent(ev);
		}
	}
	
	
    private boolean chosenHasLiked() {
		// TODO Auto-generated method stub
    	String textt = null;
    	try {
			textt = getKnowledgeText(nowLine,nowNo);
		} catch (JSONException e) {
			// 
			e.printStackTrace();
		}
		try {
			KID = shownKnowledge.getString("_id");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(!textt.equals("nothing"))
		{
			if(hasLiked(KID))
			{
				return true;
			}
		}
		return false;
	}


	@Override
	public void onBackPressed(){
		//videoView.pause();
		player.pause();
		Dialog alertDialog = new AlertDialog.Builder(CheckTest.this)
		.setMessage("�����˳����´�Ҫ���¶Դ�")
		.setCancelable(false)
		.setPositiveButton("�����Դ�",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 

				//videoView.start();
				player.start();
				
			}
		})
		.setNegativeButton("ȷ���˳�",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 
				String jsonKnowledgeText = jsonLikedArray.toString();
				FileOutputStream outputStream;
				try {
					outputStream = openFileOutput(jsonFileName,  
					        Activity.MODE_PRIVATE);
			        outputStream.write(jsonKnowledgeText.getBytes());  
			        outputStream.flush();  
			        outputStream.close();  
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
				Intent intent = new Intent(CheckTest.this, Main.class);
				startActivity(intent);
				CheckTest.this.finish();
			}
		})
		.create();
    alertDialog.show(); 
    }
    
    
    /*
    //���ý�����
    private OnSeekBarChangeListener change = new OnSeekBarChangeListener() {
        
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // ��������ֹͣ�޸ĵ�ʱ�򴥷�
            // ȡ�õ�ǰ�������Ŀ̶�
            int progress = seekBar.getProgress();
            if (videoView != null && videoView.isPlaying()) {
                // ���õ�ǰ���ŵ�λ��
            	videoView.seekTo(progress);
            }
            if (player != null && player.isPlaying()) {
                // ���õ�ǰ���ŵ�λ��
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
