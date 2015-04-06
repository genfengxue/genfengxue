/**
 * 录音界面@author vita
 */

package com.genfengxue.windenglish;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.http.util.EncodingUtils;

import com.genfengxue.windenglish.R;

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
	
	//计时器
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

		//初始化播放界面
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		//根据播放次数自定义布局
        Intent intent = getIntent();
        setContentView(R.layout.record);
        
        //获取播放的视频文件名
        final String videoId = intent.getStringExtra("videoId");
        String path = "video";
        path = path + videoId;
        path = path + "_4";
        
		videoView=(VideoView)findViewById(R.id.videoView);
		mediaController = new MediaController(this,false);
        this.videoView.setMediaController(mediaController); 
        
        //设置播放路径
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
        
        
        //设置录音参数
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
        
        //弹出对话框
        Dialog alertDialogOnPlay = new AlertDialog.Builder(Record.this)
        							.setMessage("现在开始录音")
        							.setCancelable(false)
        							.setPositiveButton("开始",new DialogInterface.OnClickListener() {
        								
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
        							.setNegativeButton("退出",new DialogInterface.OnClickListener() {
        								
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
        
        //设置back按钮

		backButton = (Button)findViewById(R.id.backButton);
		backButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v)
			{
				videoView.pause();
				Dialog alertDialog = new AlertDialog.Builder(Record.this)
				.setMessage("录音还未结束，现在退出下次要重新观看3遍")
				.setCancelable(false)
				.setPositiveButton("继续录音",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						videoView.start();
					}
				})
				.setNeutralButton("重新录音",new DialogInterface.OnClickListener() {
					
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
				.setNegativeButton("确认退出",new DialogInterface.OnClickListener() {
					
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
             //播放结束后的动作            
        	 mediaRecorder.stop();  
        	 mediaRecorder.release();  
        	 mediaRecorder = null;  
        	 
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
             							.setMessage("恭喜您，录音结束！")
             							.setCancelable(false)
             							.setPositiveButton("对答案",new DialogInterface.OnClickListener() {
             								
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
             							.setNegativeButton("退出", new DialogInterface.OnClickListener() {
											
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
        		//这里没有进行thread的继续操作！需要为其单独创建一个类！目前只能暂停一次
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
		.setMessage("录音还未结束，现在退出下次要重新观看3遍")
		.setCancelable(false)
		.setPositiveButton("继续录音",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				videoView.start();
			}
		})
		.setNeutralButton("重新录音",new DialogInterface.OnClickListener() {
			
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
		.setNegativeButton("确认退出",new DialogInterface.OnClickListener() {
			
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
