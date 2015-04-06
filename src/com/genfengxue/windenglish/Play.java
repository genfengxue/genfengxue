/**
 * 视频播放页面@author vita
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
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class Play extends Activity{

	private VideoView videoView;
	private MediaController mediaController;
	private Button backButton;
	private Context  context = this;  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		//初始化播放界面
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		//根据播放次数自定义布局
        Intent intent = getIntent();
        String playStyleString = intent.getStringExtra("playStyle");
        final int playStyle = Integer.parseInt(playStyleString);
        setContentView(R.layout.play);
        
        //获取播放的视频文件名
        final String videoId = intent.getStringExtra("videoId");
        String path = "video";
        path = path + videoId;
        path = path + "_";
        path = path + playStyleString;
        
		videoView=(VideoView)findViewById(R.id.videoView);
		mediaController = new MediaController(this,false);
        this.videoView.setMediaController(mediaController); 
        
        //设置视频播放路径
        /*
         * 通过反射获取RAW里的视频
         * 
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

        //mediaController.setVisibility(View.INVISIBLE); 屏蔽进度条
        mediaController.setAnchorView(videoView);
        
        //弹出对话框
        Dialog alertDialogOnPlay = new AlertDialog.Builder(Play.this)
        							.setPositiveButton("开始播放",new DialogInterface.OnClickListener() {
        								
        								@Override
        								public void onClick(DialogInterface dialog, int which) {
        									// TODO Auto-generated method stub

        									videoView.start();
        									
        								}
        							})
        							.setNegativeButton("跳过此段",new DialogInterface.OnClickListener() {
        								
        								@Override
        								public void onClick(DialogInterface dialog, int which) {
        									// TODO Auto-generated method stub
        									Intent newIntent = new Intent();
        						        	 if(playStyle == 3)
        						        	 {

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
        											 states[videoIdInt]='3';
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
        						        		 
        						     		    	newIntent.putExtra("videoId",videoId);
        											newIntent.setClass(Play.this, Record.class);
        											Play.this.startActivity(newIntent);
        											Play.this.finish();
        						        	 }
        						        	 else if(playStyle == 1)
        						        	 {
        						        		    newIntent.putExtra("videoId",videoId);
        											newIntent.putExtra("playStyle","2");
        											newIntent.setClass(Play.this, Play.class);
        											Play.this.startActivity(newIntent);
        											Play.this.finish();
        						        	 }
        						        	 else if(playStyle == 2)
        						        	 {
        						        		    newIntent.putExtra("videoId",videoId);
        											newIntent.putExtra("playStyle","3");
        											newIntent.setClass(Play.this, Play.class);
        											Play.this.startActivity(newIntent);
        											Play.this.finish();
        						        	 }
        								}
        							})
        							.setCancelable(false)
        							.create();
        if(playStyle == 1)
        {
        	((AlertDialog) alertDialogOnPlay).setMessage("视频共播放三遍，第一遍无字幕");
        }
        else if(playStyle == 2)
        {
        	((AlertDialog) alertDialogOnPlay).setMessage("第二遍有双语字幕");
        }
        else if(playStyle == 3)
        {
        	((AlertDialog) alertDialogOnPlay).setMessage("第三遍有中文字幕");
        }
        alertDialogOnPlay.show();
        
        //设置back按钮

		backButton = (Button)findViewById(R.id.backButton);
		backButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v)
			{
				videoView.pause();
				Dialog alertDialog = new AlertDialog.Builder(Play.this)
				.setMessage("播放还未结束，现在退出下次要重新观看3遍")
				.setCancelable(false)
				.setPositiveButton("继续看",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						videoView.start();
						
					}
				})
				.setNegativeButton("退出",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						Intent intent = new Intent(Play.this, Main.class);
						startActivity(intent);
						Play.this.finish();
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
        	 Intent newIntent = new Intent();
        	 if(playStyle == 3)
        	 {

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
					 states[videoIdInt]='3';
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
        		 
     		    	newIntent.putExtra("videoId",videoId);
					newIntent.setClass(Play.this, Record.class);
					Play.this.startActivity(newIntent);
					Play.this.finish();
        	 }
        	 else if(playStyle == 1)
        	 {
        		    newIntent.putExtra("videoId",videoId);
					newIntent.putExtra("playStyle","2");
					newIntent.setClass(Play.this, Play.class);
					Play.this.startActivity(newIntent);
					Play.this.finish();
        	 }
        	 else if(playStyle == 2)
        	 {
        		    newIntent.putExtra("videoId",videoId);
					newIntent.putExtra("playStyle","3");
					newIntent.setClass(Play.this, Play.class);
					Play.this.startActivity(newIntent);
					Play.this.finish();
        	 }
         }
        });
	}


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
    
    @Override
	public void onBackPressed(){
		videoView.pause();
		
		Dialog alertDialog = new AlertDialog.Builder(Play.this)
		.setMessage("播放还未结束，现在退出下次要重新观看3遍")
		.setCancelable(false)
		.setPositiveButton("继续看",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				videoView.start();
				
			}
		})
		.setNegativeButton("确认退出",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(Play.this, Main.class);
				startActivity(intent);
				Play.this.finish();
			}
		})
		.create();
    alertDialog.show(); 
    }
    
}
