package com.example.windenglish;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class Record extends Activity{

	private VideoView videoView;
	private MediaController mediaController;
	private Button backButton;
	private String fileName = "";	
	private static int nowtime = 0;
	private static int reFlag = 0;
	private static boolean isTrue = true;
	private Context  context = this;  
	private MediaRecorder mediaRecorder;
	
	Thread timingthread = new Thread(new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {

				while(isTrue){
				Thread.sleep(1000);
				nowtime+= 1000;
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	});
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		//��ʼ�����Ž���
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		//���ݲ��Ŵ����Զ��岼��
        Intent intent = getIntent();
        setContentView(R.layout.record);
        
        //��ȡ���ŵ���Ƶ�ļ���
        final String videoId = intent.getStringExtra("videoId");
        String path = "video";
        path = path + videoId;
        path = path + "_4";
        
		videoView=(VideoView)findViewById(R.id.videoView);
		mediaController = new MediaController(this,false);
        this.videoView.setMediaController(mediaController); 
        
        //���ò���·��
        /*
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
        videoView.setVideoURI(Uri.parse("/data/data/com.example.windenglish/files/videos/"+path+".mp4"));
        
        
        //����¼������
        mediaRecorder = new MediaRecorder();  
        fileName = Environment.getExternalStorageDirectory().getAbsolutePath();  
        fileName = fileName + "/windenglish/";
        fileName = fileName + videoId;
        fileName = fileName + ".3gp";
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); 
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(fileName);
        

        mediaController.setVisibility(View.INVISIBLE);
        mediaController.setAnchorView(videoView);
        
        //�����Ի���
        Dialog alertDialogOnPlay = new AlertDialog.Builder(Record.this)
        							.setMessage("���ڿ�ʼ¼��")
        							.setCancelable(false)
        							.setPositiveButton("��ʼ",new DialogInterface.OnClickListener() {
        								
        								@Override
        								public void onClick(DialogInterface dialog, int which) {
        									// TODO Auto-generated method stub

        									videoView.start();           
        									reFlag=1;
        									try {  
        										 mediaRecorder.prepare();  
        						            } catch (Exception e) {  
        						                 ;
        						            }  
        									mediaRecorder.start();  
        									timingthread.start();
        									
        								}
        							})
        							.setNegativeButton("�˳�",new DialogInterface.OnClickListener() {
        								
        								@Override
        								public void onClick(DialogInterface dialog, int which) {
        									// TODO Auto-generated method stub

        									Intent intent = new Intent(Record.this, Main.class);
        									isTrue=false;
        									startActivity(intent);
        									Record.this.finish();
        								}
        							})
        							.create();
        alertDialogOnPlay.show();
        
        //����back��ť

		backButton = (Button)findViewById(R.id.backButton);
		backButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v)
			{
				videoView.pause();
				Dialog alertDialog = new AlertDialog.Builder(Record.this)
				.setMessage("¼����δ�����������˳��´�Ҫ���¹ۿ�3��")
				.setCancelable(false)
				.setPositiveButton("����¼��",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						videoView.start();
					}
				})
				.setNeutralButton("����¼��",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						videoView.pause();
						nowtime=0;
			        	mediaRecorder.stop();  
			        	mediaRecorder.release();  
			        	mediaRecorder = null;  
			            mediaRecorder = new MediaRecorder();  
			            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); 
			            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			            mediaRecorder.setOutputFile(fileName);
			            try {  
							 mediaRecorder.prepare();  
			            } catch (Exception e) {  
			                 ;
			            }  
						mediaRecorder.start();  
						
						videoView.seekTo(0);
						videoView.start();
					}
				})
				.setNegativeButton("ȷ���˳�",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						nowtime=0;
						reFlag=0;
			        	mediaRecorder.stop();  
			        	mediaRecorder.release();  
			        	mediaRecorder = null;  
						Intent intent = new Intent(Record.this, Main.class);
						isTrue=false;
						startActivity(intent);
						Record.this.finish();
					}
				})
				.create();
	        alertDialog.show(); 
			}
		});
                
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
         @Override
         public void onCompletion(MediaPlayer mp)
         {
             //���Ž�����Ķ���            
        	 mediaRecorder.stop();  
        	 mediaRecorder.release();  
        	 mediaRecorder = null;  
        	 
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
				 states[videoIdInt]='1';
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

             Dialog alertDialogEndRecord = new AlertDialog.Builder(Record.this)
             							.setMessage("��ϲ����¼��������")
             							.setCancelable(false)
             							.setPositiveButton("�Դ�",new DialogInterface.OnClickListener() {
             								
             								@Override
             								public void onClick(DialogInterface dialog, int which) {
             									// TODO Auto-generated method stub
             									
             							        Intent newIntent = new Intent();
             							        isTrue=false;
           						 		     	newIntent.putExtra("videoId",videoId);
           						 		     	newIntent.setClass(Record.this, Check.class);
           						 		     	Record.this.startActivity(newIntent);
             									Record.this.finish();
             								}
             							})
             							.setNegativeButton("�˳�", new DialogInterface.OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {
												// TODO Auto-generated method stub
												reFlag=0;
												nowtime=0;
												Intent intent = new Intent(Record.this, Main.class);
												isTrue=false;
	        									startActivity(intent);
	        									Record.this.finish();
											}
										})
             							.create();
             alertDialogEndRecord.show();
        	 
         }
        });
	}


    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setKeepScreenOn(true);
        if(null != videoView) {
        	if(reFlag!=0)
        	{
        		//����û�н���thread�ļ�����������ҪΪ�䵥������һ���࣡Ŀǰֻ����ͣһ��
                videoView.seekTo(nowtime);
                videoView.start();
        	}
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
        //outState.putLong("time", nowtime);
    	isTrue=false;
        super.onSaveInstanceState(outState);
        }
    
    @Override
	public void onBackPressed(){
		videoView.pause();
		Dialog alertDialog = new AlertDialog.Builder(Record.this)
		.setMessage("¼����δ�����������˳��´�Ҫ���¹ۿ�3��")
		.setCancelable(false)
		.setPositiveButton("����¼��",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				videoView.start();
			}
		})
		.setNeutralButton("����¼��",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				videoView.pause();
				nowtime=0;
	        	mediaRecorder.stop();  
	        	mediaRecorder.release();  
	        	mediaRecorder = null;  
	            mediaRecorder = new MediaRecorder();  
	            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); 
	            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	            mediaRecorder.setOutputFile(fileName);
	            try {  
					 mediaRecorder.prepare();  
	            } catch (Exception e) {  
	                 ;
	            }  
				mediaRecorder.start();  
				
				videoView.seekTo(0);
				videoView.start();
			}
		})
		.setNegativeButton("ȷ���˳�",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				reFlag=0;
				nowtime=0;
	        	mediaRecorder.stop();
	        	mediaRecorder.release();
	        	mediaRecorder = null;
				Intent intent = new Intent(Record.this, Main.class);
				isTrue=false;
				startActivity(intent);
				Record.this.finish();
			}
		})
		.create();
    alertDialog.show(); 
    }
    
}
