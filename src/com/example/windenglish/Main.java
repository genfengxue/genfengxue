/**
 * 主菜单@author vita
 */

package com.example.windenglish;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class Main extends Activity{
	private TextView mainUsername;
	private String userdata;
	private String[] names;
	private String[] tempSC;
	private SimpleAdapter mSimpleAdapter;
	private char[] states;
	private String fileName="userdata";
	private ListView videoList;
	private HashMap<String, Object> map;
	private int videoId;
	private ProgressDialog pd;  
	//private int dFlag=1;
	private int cancelFlag = 0;
	private int scX=0;
	private int scY=0;
	
    public Handler mHandler=new Handler()  
    {  
        public void handleMessage(Message msg)  
        {  
            switch(msg.what)  
            {  
            case 1:
				pd.setMessage("已下载25%……");
				break;
            case 2:
				pd.setMessage("已下载50%……");
				break;
            case 3:
				pd.setMessage("已下载75%……");
				break;
            case 4:  
            	pd.dismiss();
            	scY=videoList.getFirstVisiblePosition();
				String temp=String.valueOf(scX) + "?" + String.valueOf(scY);
				String tempFilename="tempSC";
				Context context = Main.this;
		        FileOutputStream out = null;  
		        try {  
		            out = context.openFileOutput(tempFilename, Context.MODE_PRIVATE);  
		            out.write(temp.getBytes("UTF-8"));  
			        out.close();
		        } catch (Exception e) {  
					// TODO Auto-generated catch block
		            e.printStackTrace();  
		        }  
				Dialog alertDialogSucceed = new AlertDialog.Builder(Main.this)
				.setMessage("视频下载完毕！")
				.setPositiveButton("确定",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(Main.this,Main.class);
						startActivity(intent);
					}
				})
				.create();
				alertDialogSucceed.show(); 
                break;  
            default:  
            	pd.dismiss();
				Dialog alertDialogError = new AlertDialog.Builder(Main.this)
				.setMessage("视频下载出错，请重试。")
				.setPositiveButton("确定",null)
				.create();
				alertDialogError.show(); 
                break;  
            }  
            super.handleMessage(msg);  
        }  
    };  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//界面初始化
		File eFile = new File("/data/data/com.example.windenglish/files/email");
		if(!eFile.exists())
		{
					Intent intent = new Intent(Main.this, Profile.class);
					startActivity(intent);
		}
        
        
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
		
        Log.e("wind", Integer.toString(Constants.example));

		//尝试从读取用户信息
		try{   
	         FileInputStream fin = openFileInput(fileName);   
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
		states=names[1].toCharArray();
        mainUsername = (TextView)findViewById(R.id.mainUsername); 
        mainUsername.setText(names[0]); 
        

        //允许修改用户信息
        mainUsername.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

		    	Dialog alertDialog = new AlertDialog.Builder(Main.this)
				.setMessage("请注意，若修改昵称将会删除你的全部学习记录！")
				.setPositiveButton("修改昵称",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						File userFile = new File("/data/data/com.example.windenglish/files/userdata");
						userFile.delete();
						Intent intent = new Intent(Main.this, Login.class);
						startActivity(intent);
						Main.this.finish();
					}
				})
				.setNegativeButton("取消", null)
				
				
				
				.setNeutralButton("修改邮箱", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(Main.this,Profile.class);
						startActivity(intent);
						/*
						Intent intent = new Intent(Main.this,CheckTest.class);
						intent.putExtra("videoId","12");
						startActivity(intent);
						*/
					}
				})
				
				
				
				.create();
				alertDialog.show(); 
			}
		});

        File file = new File("");
        String frontFile = "/data/data/com.example.windenglish/files/videos/video";
        
        //绑定数据
        videoList = (ListView)findViewById(R.id.videoList);
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,Object>>();
        
        //TODO:在这里手动添加每一次课程的详细信息
        
        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson1);//加入图片          
        map.put("ItemTitle", "1");  //序号
        map.put("ItemTextEnglish", "Introduction 1");  
        map.put("ItemTextChinese", "自我介绍 1"); 
        file = new File(frontFile + "1_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[1]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[1]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson2);//加入图片          
        map.put("ItemTitle", "2");  
        map.put("ItemTextEnglish", "Introduction 2");  
        map.put("ItemTextChinese", "自我介绍 2"); 
        file = new File(frontFile + "2_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[2]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[2]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson3);//加入图片          
        map.put("ItemTitle", "3");  
        map.put("ItemTextEnglish", "Job Description");  
        map.put("ItemTextChinese", "描述工作");  
        file = new File(frontFile + "3_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[3]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[3]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson4);//加入图片          
        map.put("ItemTitle", "4");  
        map.put("ItemTextEnglish", "Baby Talk");  
        map.put("ItemTextChinese", "与婴儿的谈话");   
        file = new File(frontFile + "4_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[4]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[4]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson5);//加入图片          
        map.put("ItemTitle", "5");  
        map.put("ItemTextEnglish", "Time Flies");  
        map.put("ItemTextChinese", "时光飞逝");  
        file = new File(frontFile + "5_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[5]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[5]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson6);//加入图片          
        map.put("ItemTitle", "6");  
        map.put("ItemTextEnglish", "The Brochure");  
        map.put("ItemTextChinese", "小册子");   
        file = new File(frontFile + "6_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[6]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[6]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson7);//加入图片          
        map.put("ItemTitle", "7");  
        map.put("ItemTextEnglish", "Out to Lunch-Decision");  
        map.put("ItemTextChinese", "外出午餐-决定");   
        file = new File(frontFile + "7_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[7]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[7]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson8);//加入图片          
        map.put("ItemTitle", "8");  
        map.put("ItemTextEnglish", "Out to Lunch-Ordering");  
        map.put("ItemTextChinese", "外出午餐-点菜");   
        file = new File(frontFile + "8_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[8]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[8]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson9);//加入图片          
        map.put("ItemTitle", "9");  
        map.put("ItemTextEnglish", "Not Without My Bags!");  
        map.put("ItemTextChinese", "我的包不见了！");   
        file = new File(frontFile + "9_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[9]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[9]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson10);//加入图片          
        map.put("ItemTitle", "10");  
        map.put("ItemTextEnglish", "Check In");  
        map.put("ItemTextChinese", "登记");   
        file = new File(frontFile + "10_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[10]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[10]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);    

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson11);//加入图片          
        map.put("ItemTitle", "11");  
        map.put("ItemTextEnglish", "Partners");  
        map.put("ItemTextChinese", "伙伴");   
        file = new File(frontFile + "11_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[11]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[11]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson12);//加入图片          
        map.put("ItemTitle", "12");  
        map.put("ItemTextEnglish", "No Cream");  
        map.put("ItemTextChinese", "不要奶油");   
        file = new File(frontFile + "12_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[12]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[12]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson13);//加入图片          
        map.put("ItemTitle", "13");  
        map.put("ItemTextEnglish", "City Tour");  
        map.put("ItemTextChinese", "城市观光");   
        file = new File(frontFile + "13_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[13]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[13]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson14);//加入图片          
        map.put("ItemTitle", "14");  
        map.put("ItemTextEnglish", "Star Signs");  
        map.put("ItemTextChinese", "星座");   
        file = new File(frontFile + "14_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[14]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[14]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson15);//加入图片          
        map.put("ItemTitle", "15");  
        map.put("ItemTextEnglish", "Baggage Experts");  
        map.put("ItemTextChinese", "行李专家");   
        file = new File(frontFile + "15_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[15]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[15]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson16);//加入图片          
        map.put("ItemTitle", "16");  
        map.put("ItemTextEnglish", "Market Research");  
        map.put("ItemTextChinese", "市场调查");   
        file = new File(frontFile + "16_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[16]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[16]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson17);//加入图片          
        map.put("ItemTitle", "17");  
        map.put("ItemTextEnglish", "Wendy at Work");  
        map.put("ItemTextChinese", "工作时的wendy");   
        file = new File(frontFile + "17_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[17]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[17]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson18);//加入图片          
        map.put("ItemTitle", "18");  
        map.put("ItemTextEnglish", "William's Day-off");  
        map.put("ItemTextChinese", "William的假日");   
        file = new File(frontFile + "18_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[18]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[18]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson19);//加入图片          
        map.put("ItemTitle", "19");  
        map.put("ItemTextEnglish", "Nu la's Interview");  
        map.put("ItemTextChinese", "努拉的面试");   
        file = new File(frontFile + "19_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[19]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[19]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson20);//加入图片          
        map.put("ItemTitle", "20");  
        map.put("ItemTextEnglish", "Celebrity!");  
        map.put("ItemTextChinese", "名人");   
        file = new File(frontFile + "20_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[20]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[20]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson21);//加入图片          
        map.put("ItemTitle", "21");  
        map.put("ItemTextEnglish", "Whose Day Off");  
        map.put("ItemTextChinese", "谁的假日");   
        file = new File(frontFile + "21_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[21]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[21]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson22);//加入图片          
        map.put("ItemTitle", "22");  
        map.put("ItemTextEnglish", "No Problem");  
        map.put("ItemTextChinese", "没问题");   
        file = new File(frontFile + "22_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[22]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[22]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson23);//加入图片          
        map.put("ItemTitle", "23");  
        map.put("ItemTextEnglish", "Use the Computer");  
        map.put("ItemTextChinese", "使用电脑");  
        file = new File(frontFile + "23_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else  
        if(states[23]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[23]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson24);//加入图片          
        map.put("ItemTitle", "24");  
        map.put("ItemTextEnglish", "A Small Problem");  
        map.put("ItemTextChinese", "小问题");   
        file = new File(frontFile + "24_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[24]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[24]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson25);//加入图片          
        map.put("ItemTitle", "25");  
        map.put("ItemTextEnglish", "Dean's Birthday Present");  
        map.put("ItemTextChinese", "Dean的生日礼物");   
        file = new File(frontFile + "25_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[25]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[25]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson26);//加入图片          
        map.put("ItemTitle", "26");  
        map.put("ItemTextEnglish", "Office Supplies");  
        map.put("ItemTextChinese", "办公用品");   
        file = new File(frontFile + "26_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[26]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[26]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson27);//加入图片          
        map.put("ItemTitle", "27");  
        map.put("ItemTextEnglish", "Simon's Dream");  
        map.put("ItemTextChinese", "Simon的梦");   
        file = new File(frontFile + "27_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[27]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[27]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson28);//加入图片          
        map.put("ItemTitle", "28");  
        map.put("ItemTextEnglish", "Anne's passport");  
        map.put("ItemTextChinese", "Anne的护照");   
        file = new File(frontFile + "28_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[28]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[28]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson29);//加入图片          
        map.put("ItemTitle", "29");  
        map.put("ItemTextEnglish", "Relatives");  
        map.put("ItemTextChinese", "亲属");   
        file = new File(frontFile + "29_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[29]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[29]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson30);//加入图片          
        map.put("ItemTitle", "30");  
        map.put("ItemTextEnglish", "No turns");  
        map.put("ItemTextChinese", "不准掉属");   
        file = new File(frontFile + "30_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[30]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[30]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson31);//加入图片          
        map.put("ItemTitle", "31");  
        map.put("ItemTextEnglish", "Where is it");  
        map.put("ItemTextChinese", "它在哪里");   
        file = new File(frontFile + "31_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[31]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[31]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson32);//加入图片          
        map.put("ItemTitle", "32");  
        map.put("ItemTextEnglish", "No jet lag");  
        map.put("ItemTextChinese", "没有时差");   
        file = new File(frontFile + "32_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[32]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[32]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson33);//加入图片          
        map.put("ItemTitle", "33");  
        map.put("ItemTextEnglish", "Trip report");  
        map.put("ItemTextChinese", "旅行报告");   
        file = new File(frontFile + "33_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[33]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[33]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson34);//加入图片          
        map.put("ItemTitle", "34");  
        map.put("ItemTextEnglish", "On foot");  
        map.put("ItemTextChinese", "步行");   
        file = new File(frontFile + "34_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[34]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[34]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson35);//加入图片          
        map.put("ItemTitle", "35");  
        map.put("ItemTextEnglish", "William's dream");  
        map.put("ItemTextChinese", "William的梦");   
        file = new File(frontFile + "35_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[35]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[35]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson36);//加入图片          
        map.put("ItemTitle", "36");  
        map.put("ItemTextEnglish", "Business report");  
        map.put("ItemTextChinese", "商业报告");   
        file = new File(frontFile + "36_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[36]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[36]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson37);//加入图片          
        map.put("ItemTitle", "37");  
        map.put("ItemTextEnglish", "Education");  
        map.put("ItemTextChinese", "教育");   
        file = new File(frontFile + "37_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[37]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[37]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson38);//加入图片          
        map.put("ItemTitle", "38");  
        map.put("ItemTextEnglish", "One percent");  
        map.put("ItemTextChinese", "百分之一");   
        file = new File(frontFile + "38_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[38]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[38]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson39);//加入图片          
        map.put("ItemTitle", "39");  
        map.put("ItemTextEnglish", "Not your job!");  
        map.put("ItemTextChinese", "不是你的工作");   
        file = new File(frontFile + "39_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[39]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[39]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson40);//加入图片          
        map.put("ItemTitle", "40");  
        map.put("ItemTextEnglish", "Doctor!");  
        map.put("ItemTextChinese", "医生!");   
        file = new File(frontFile + "40_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[40]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[40]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson41);//加入图片          
        map.put("ItemTitle", "41");  
        map.put("ItemTextEnglish", "Business travel");  
        map.put("ItemTextChinese", "出差");   
        file = new File(frontFile + "41_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[41]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[41]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson42);//加入图片          
        map.put("ItemTitle", "42");  
        map.put("ItemTextEnglish", "Blind date");  
        map.put("ItemTextChinese", "相亲");   
        file = new File(frontFile + "42_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[42]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[42]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson43);//加入图片          
        map.put("ItemTitle", "43");  
        map.put("ItemTextEnglish", "An interesting combination");  
        map.put("ItemTextChinese", "有趣的组合");   
        file = new File(frontFile + "43_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[43]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[43]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson44);//加入图片          
        map.put("ItemTitle", "44");  
        map.put("ItemTextEnglish", "Business with Nu La");  
        map.put("ItemTextChinese", "与Nu La做生意");   
        file = new File(frontFile + "44_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[44]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[44]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson45);//加入图片          
        map.put("ItemTitle", "45");  
        map.put("ItemTextEnglish", "Menu planning");  
        map.put("ItemTextChinese", "制定菜单");   
        file = new File(frontFile + "45_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[45]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[45]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson46);//加入图片          
        map.put("ItemTitle", "46");  
        map.put("ItemTextEnglish", "New menu");  
        map.put("ItemTextChinese", "新菜单");   
        file = new File(frontFile + "46_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[46]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[46]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson47);//加入图片          
        map.put("ItemTitle", "47");  
        map.put("ItemTextEnglish", "Rock star");  
        map.put("ItemTextChinese", "摇滚明星");   
        file = new File(frontFile + "47_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[47]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[47]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson48);//加入图片          
        map.put("ItemTitle", "48");  
        map.put("ItemTextEnglish", "On camera");  
        map.put("ItemTextChinese", "上镜");   
        file = new File(frontFile + "48_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[48]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[48]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson49);//加入图片          
        map.put("ItemTitle", "49");  
        map.put("ItemTextEnglish", "Phone call");  
        map.put("ItemTextChinese", "打电话");   
        file = new File(frontFile + "49_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[49]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[49]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson50);//加入图片          
        map.put("ItemTitle", "50");  
        map.put("ItemTextEnglish", "A beauty");  
        map.put("ItemTextChinese", "一个美人");   
        file = new File(frontFile + "50_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[50]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[50]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson51);//加入图片          
        map.put("ItemTitle", "51");  
        map.put("ItemTextEnglish", "On the road");  
        map.put("ItemTextChinese", "在路上");   
        file = new File(frontFile + "51_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[51]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[51]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson52);//加入图片          
        map.put("ItemTitle", "52");  
        map.put("ItemTextEnglish", "A long way from home");  
        map.put("ItemTextChinese", "离家很远");   
        file = new File(frontFile + "52_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[52]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[52]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson53);//加入图片          
        map.put("ItemTitle", "53");  
        map.put("ItemTextEnglish", "Group booking");  
        map.put("ItemTextChinese", "团体预订");   
        file = new File(frontFile + "53_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[53]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[53]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson54);//加入图片          
        map.put("ItemTitle", "54");  
        map.put("ItemTextEnglish", "Travel plans");  
        map.put("ItemTextChinese", "旅行计划");   
        file = new File(frontFile + "54_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[54]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[54]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson55);//加入图片          
        map.put("ItemTitle", "55");  
        map.put("ItemTextEnglish", "Radio reporter");  
        map.put("ItemTextChinese", "广播电台记者");   
        file = new File(frontFile + "55_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[55]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[55]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson56);//加入图片          
        map.put("ItemTitle", "56");  
        map.put("ItemTextEnglish", "I wanna do that!");  
        map.put("ItemTextChinese", "我想做那个");   
        file = new File(frontFile + "56_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[56]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[56]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson57);//加入图片          
        map.put("ItemTitle", "57");  
        map.put("ItemTextEnglish", "Computer training");  
        map.put("ItemTextChinese", "计算机培训");   
        file = new File(frontFile + "57_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[57]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[57]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson58);//加入图片          
        map.put("ItemTitle", "58");  
        map.put("ItemTextEnglish", "I made it!");  
        map.put("ItemTextChinese", "我成功了!");   
        file = new File(frontFile + "58_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[58]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[58]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson59);//加入图片          
        map.put("ItemTitle", "59");  
        map.put("ItemTextEnglish", "Look before you leap");  
        map.put("ItemTextChinese", "三思而后行");   
        file = new File(frontFile + "59_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[59]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[59]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson60);//加入图片          
        map.put("ItemTitle", "60");  
        map.put("ItemTextEnglish", "Real estate");  
        map.put("ItemTextChinese", "房地产");   
        file = new File(frontFile + "60_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[60]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[60]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson61);//加入图片          
        map.put("ItemTitle", "61");  
        map.put("ItemTextEnglish", "Common ground");  
        map.put("ItemTextChinese", "共同之处");   
        file = new File(frontFile + "61_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[61]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[61]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson62);//加入图片          
        map.put("ItemTitle", "62");  
        map.put("ItemTextEnglish", "Just a trim");  
        map.put("ItemTextChinese", "稍作修剪");   
        file = new File(frontFile + "62_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[62]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[62]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson63);//加入图片          
        map.put("ItemTitle", "63");  
        map.put("ItemTextEnglish", "Commuter's tale");  
        map.put("ItemTextChinese", "公交车奇闻");   
        file = new File(frontFile + "63_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[63]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[63]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson64);//加入图片          
        map.put("ItemTitle", "64");  
        map.put("ItemTextEnglish", "Model Behavior I");  
        map.put("ItemTextChinese", "模范行为I");   
        file = new File(frontFile + "64_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[64]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[64]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson65);//加入图片          
        map.put("ItemTitle", "65");  
        map.put("ItemTextEnglish", "Model Behavior II");  
        map.put("ItemTextChinese", "模范行为II");   
        file = new File(frontFile + "65_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[65]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[65]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson66);//加入图片          
        map.put("ItemTitle", "66");  
        map.put("ItemTextEnglish", "Emergency!");  
        map.put("ItemTextChinese", "紧急情况");   
        file = new File(frontFile + "66_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[66]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[66]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson67);//加入图片          
        map.put("ItemTitle", "67");  
        map.put("ItemTextEnglish", "New York express");  
        map.put("ItemTextChinese", "纽约专列");   
        file = new File(frontFile + "67_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[67]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[67]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson68);//加入图片          
        map.put("ItemTitle", "68");  
        map.put("ItemTextEnglish", "Chef's story");  
        map.put("ItemTextChinese", "主厨的故事");   
        file = new File(frontFile + "68_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[68]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[68]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson69);//加入图片          
        map.put("ItemTitle", "69");  
        map.put("ItemTextEnglish", "A good trip");  
        map.put("ItemTextChinese", "难忘的旅行");   
        file = new File(frontFile + "69_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[69]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[69]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson70);//加入图片          
        map.put("ItemTitle", "70");  
        map.put("ItemTextEnglish", "He drives her crazy");  
        map.put("ItemTextChinese", "他把她搞疯了");   
        file = new File(frontFile + "70_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[70]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[70]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson71);//加入图片          
        map.put("ItemTitle", "71");  
        map.put("ItemTextEnglish", "Big house hunt");  
        map.put("ItemTextChinese", "寻找大房子");   
        file = new File(frontFile + "71_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[71]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[71]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson72);//加入图片          
        map.put("ItemTitle", "72");  
        map.put("ItemTextEnglish", "The postcard");  
        map.put("ItemTextChinese", "明信片");   
        file = new File(frontFile + "72_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[72]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[72]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson73);//加入图片          
        map.put("ItemTitle", "73");  
        map.put("ItemTextEnglish", "Sports talk");  
        map.put("ItemTextChinese", "谈论运动");   
        file = new File(frontFile + "73_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[73]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[73]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson74);//加入图片          
        map.put("ItemTitle", "74");  
        map.put("ItemTextEnglish", "Guest survey");  
        map.put("ItemTextChinese", "顾客调查");   
        file = new File(frontFile + "74_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[74]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[74]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson75);//加入图片          
        map.put("ItemTitle", "75");  
        map.put("ItemTextEnglish", "Bargain hunting");  
        map.put("ItemTextChinese", "寻购便宜货");   
        file = new File(frontFile + "75_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[75]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[75]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson76);//加入图片          
        map.put("ItemTitle", "76");  
        map.put("ItemTextEnglish", "Arrival tips");  
        map.put("ItemTextChinese", "入境指南");   
        file = new File(frontFile + "76_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[76]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[76]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson77);//加入图片          
        map.put("ItemTitle", "77");  
        map.put("ItemTextEnglish", "Schedules");  
        map.put("ItemTextChinese", "日程安排");   
        file = new File(frontFile + "77_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[77]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[77]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson78);//加入图片          
        map.put("ItemTitle", "78");  
        map.put("ItemTextEnglish", "Reporting live");  
        map.put("ItemTextChinese", "现场报道");   
        file = new File(frontFile + "78_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[78]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[78]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson79);//加入图片          
        map.put("ItemTitle", "79");  
        map.put("ItemTextEnglish", "The future");  
        map.put("ItemTextChinese", "未来");   
        file = new File(frontFile + "79_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[79]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[79]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson80);//加入图片          
        map.put("ItemTitle", "80");  
        map.put("ItemTextEnglish", "Nothing fancy");  
        map.put("ItemTextChinese", "不要花哨的东西");   
        file = new File(frontFile + "80_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[80]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[80]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson81);//加入图片          
        map.put("ItemTitle", "81");  
        map.put("ItemTextEnglish", "Goodbye and good luck!");  
        map.put("ItemTextChinese", "再见并祝你好运");   
        file = new File(frontFile + "81_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[81]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[81]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);    

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson371);//加入图片          
        map.put("ItemTitle", "71");  
        map.put("ItemTextEnglish", "He's awful!");  
        map.put("ItemTextChinese", "他讨厌透了!");   
        file = new File(frontFile + "371_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[371]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[371]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);    

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson373);//加入图片          
        map.put("ItemTitle", "73");  
        map.put("ItemTextEnglish", "The way to King Street");  
        map.put("ItemTextChinese", "到国王街的走法");   
        file = new File(frontFile + "373_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[373]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[373]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);    

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson375);//加入图片          
        map.put("ItemTitle", "75");  
        map.put("ItemTextEnglish", "Uncomfortable shoes");  
        map.put("ItemTextChinese", "不舒适的鞋子");   
        file = new File(frontFile + "375_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[375]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[375]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);    

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson377);//加入图片          
        map.put("ItemTitle", "77");  
        map.put("ItemTextEnglish", "Terrible toothache");  
        map.put("ItemTextChinese", "要命的牙痛");   
        file = new File(frontFile + "377_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[377]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[377]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);    

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson379);//加入图片          
        map.put("ItemTitle", "79");  
        map.put("ItemTextEnglish", "Carol's shopping list");  
        map.put("ItemTextChinese", "卡罗尔的购物单");   
        file = new File(frontFile + "379_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[379]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[379]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);    

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson381);//加入图片          
        map.put("ItemTitle", "81");  
        map.put("ItemTextEnglish", "Roast beef and potatoes");  
        map.put("ItemTextChinese", "烤牛肉和土豆");   
        file = new File(frontFile + "381_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[381]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[381]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);    

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson383);//加入图片          
        map.put("ItemTitle", "83");  
        map.put("ItemTextEnglish", "Going on holiday");  
        map.put("ItemTextChinese", "度假");   
        file = new File(frontFile + "383_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[383]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[383]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);    

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson385);//加入图片          
        map.put("ItemTitle", "85");  
        map.put("ItemTextEnglish", "Paris in the spring");  
        map.put("ItemTextChinese", "巴黎之春");   
        file = new File(frontFile + "385_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[385]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[385]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);    

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson387);//加入图片          
        map.put("ItemTitle", "87");  
        map.put("ItemTextEnglish", "A car crash");  
        map.put("ItemTextChinese", "车祸");   
        file = new File(frontFile + "387_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[387]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[387]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);    

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson389);//加入图片          
        map.put("ItemTitle", "89");  
        map.put("ItemTextEnglish", "For sale");  
        map.put("ItemTextChinese", "待售");   
        file = new File(frontFile + "389_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[389]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[389]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);    

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson391);//加入图片          
        map.put("ItemTitle", "91");  
        map.put("ItemTextEnglish", "Poor Ian!");  
        map.put("ItemTextChinese", "可怜的伊恩!");   
        file = new File(frontFile + "391_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[391]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[391]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);    

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson393);//加入图片          
        map.put("ItemTitle", "93");  
        map.put("ItemTextEnglish", "Our new neighbour");  
        map.put("ItemTextChinese", "我们的新邻居");   
        file = new File(frontFile + "393_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[393]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[393]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);    

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson395);//加入图片          
        map.put("ItemTitle", "95");  
        map.put("ItemTextEnglish", "Tickets, please");  
        map.put("ItemTextChinese", "请把车票拿出来");   
        file = new File(frontFile + "395_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[395]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[395]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);    

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson397);//加入图片          
        map.put("ItemTitle", "97");  
        map.put("ItemTextEnglish", "A small blue case");  
        map.put("ItemTextChinese", "一只蓝色的小箱子");   
        file = new File(frontFile + "397_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[397]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[397]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);    

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson399);//加入图片          
        map.put("ItemTitle", "99");  
        map.put("ItemTextEnglish", "Ow!");  
        map.put("ItemTextChinese", "啊哟!");   
        file = new File(frontFile + "399_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[399]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[399]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);      

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson401);//加入图片          
        map.put("ItemTitle", "101");  
        map.put("ItemTextEnglish", "A card from Jimmy");  
        map.put("ItemTextChinese", "吉米的明信片");   
        file = new File(frontFile + "401_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[401]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[401]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);      

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson403);//加入图片          
        map.put("ItemTitle", "103");  
        map.put("ItemTextEnglish", "The French test");  
        map.put("ItemTextChinese", "法语考试");   
        file = new File(frontFile + "403_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[403]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[403]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);      

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson405);//加入图片          
        map.put("ItemTitle", "105");  
        map.put("ItemTextEnglish", "Full of mistakes");  
        map.put("ItemTextChinese", "错误百出");   
        file = new File(frontFile + "405_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[405]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[405]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);      

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson407);//加入图片          
        map.put("ItemTitle", "107");  
        map.put("ItemTextEnglish", "It's too small");  
        map.put("ItemTextChinese", "太小了");   
        file = new File(frontFile + "407_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[407]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[407]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);      

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson409);//加入图片          
        map.put("ItemTitle", "109");  
        map.put("ItemTextEnglish", "A good idea");  
        map.put("ItemTextChinese", "好主意");   
        file = new File(frontFile + "409_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[409]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[409]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);      

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson411);//加入图片          
        map.put("ItemTitle", "111");  
        map.put("ItemTextEnglish", "The most expensive model");  
        map.put("ItemTextChinese", "最昂贵的型号");   
        file = new File(frontFile + "411_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[411]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[411]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);      

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson413);//加入图片          
        map.put("ItemTitle", "113");  
        map.put("ItemTextEnglish", "Small changes");  
        map.put("ItemTextChinese", "零钱");   
        file = new File(frontFile + "413_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[413]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[413]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);      

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson415);//加入图片          
        map.put("ItemTitle", "115");  
        map.put("ItemTextEnglish", "Knock, Knock!");  
        map.put("ItemTextChinese", "敲敲门!");   
        file = new File(frontFile + "415_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[415]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[415]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);      

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson417);//加入图片          
        map.put("ItemTitle", "117");  
        map.put("ItemTextEnglish", "Tommy's breakfast");  
        map.put("ItemTextChinese", "汤米的早餐");   
        file = new File(frontFile + "417_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[417]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[417]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);      

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson419);//加入图片          
        map.put("ItemTitle", "119");  
        map.put("ItemTextEnglish", "A true story");  
        map.put("ItemTextChinese", "一个真实的故事");   
        file = new File(frontFile + "419_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[419]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[419]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);      

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson421);//加入图片          
        map.put("ItemTitle", "121");  
        map.put("ItemTextEnglish", "The man in a hat");  
        map.put("ItemTextChinese", "戴帽子的男士");   
        file = new File(frontFile + "421_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[421]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[421]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);      

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson423);//加入图片          
        map.put("ItemTitle", "123");  
        map.put("ItemTextEnglish", "A Trip to Australia");  
        map.put("ItemTextChinese", "澳大利亚之行");   
        file = new File(frontFile + "423_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[423]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[423]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);      

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson425);//加入图片          
        map.put("ItemTitle", "125");  
        map.put("ItemTextEnglish", "Tea for two");  
        map.put("ItemTextChinese", "两个人一起喝茶");   
        file = new File(frontFile + "425_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[425]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[425]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);      

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson427);//加入图片          
        map.put("ItemTitle", "127");  
        map.put("ItemTextEnglish", "A famous actress");  
        map.put("ItemTextChinese", "著名的女演员");   
        file = new File(frontFile + "427_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[427]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[427]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);      

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson429);//加入图片          
        map.put("ItemTitle", "129");  
        map.put("ItemTextEnglish", "Seventy miles an hour");  
        map.put("ItemTextChinese", "时速70英里");   
        file = new File(frontFile + "429_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[429]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[429]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);      

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson431);//加入图片          
        map.put("ItemTitle", "131");  
        map.put("ItemTextEnglish", "Don't be so sure!");  
        map.put("ItemTextChinese", "别那么肯定!");   
        file = new File(frontFile + "431_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[431]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[431]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);      

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson433);//加入图片          
        map.put("ItemTitle", "133");  
        map.put("ItemTextEnglish", "Sensational news!");  
        map.put("ItemTextChinese", "爆炸性新闻!");   
        file = new File(frontFile + "433_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[433]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[433]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);      

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson435);//加入图片          
        map.put("ItemTitle", "135");  
        map.put("ItemTextEnglish", "The latest report");  
        map.put("ItemTextChinese", "最新消息");   
        file = new File(frontFile + "435_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[435]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[435]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);      

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson437);//加入图片          
        map.put("ItemTitle", "137");  
        map.put("ItemTextEnglish", "A pleasant dream");  
        map.put("ItemTextChinese", "美好的梦");   
        file = new File(frontFile + "437_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[437]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[437]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);      

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson439);//加入图片          
        map.put("ItemTitle", "139");  
        map.put("ItemTextEnglish", "Is that you,John?");  
        map.put("ItemTextChinese", "是你吗,约翰?");   
        file = new File(frontFile + "439_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[439]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[439]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);      

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson441);//加入图片          
        map.put("ItemTitle", "141");  
        map.put("ItemTextEnglish", "Sally's first train ride");  
        map.put("ItemTextChinese", "萨莉第一次乘火车旅行");   
        file = new File(frontFile + "441_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[441]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[441]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);      

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson443);//加入图片          
        map.put("ItemTitle", "143");  
        map.put("ItemTextEnglish", "A walk through the woods");  
        map.put("ItemTextChinese", "林中散步");   
        file = new File(frontFile + "443_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[443]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[443]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson1);//加入图片          
        map.put("ItemTitle", "999");  
        map.put("ItemTextEnglish", "Test");  
        map.put("ItemTextChinese", "test");   
        file = new File(frontFile + "999_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[999]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[999]=='1')
        	map.put("ItemState", R.drawable.finished);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);
        
         		mSimpleAdapter = new SimpleAdapter(this, listItem, R.layout.videolistitem, 
        		new String[] {"ItemImage","ItemTitle", "ItemTextEnglish","ItemTextChinese","ItemState"} , 
        		new int[]{R.id.ItemImage,R.id.ItemTitle,R.id.ItemTextEnglish,R.id.ItemTextChinese,R.id.ItemState});

        videoList.setAdapter(mSimpleAdapter);

        
        videoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position,final long id) {
				// TODO 
				position++;
				videoId=(int)id+1;
				
				//测试用
				if(videoId==119)
				{
					videoId=999;
				}

				if(videoId>81){
					videoId *= 2;
					videoId += 207;
				}
					
				final String videoIdString = Integer.toString(videoId);
				final Intent intent = new Intent();
				final int fposition = position;
				
				//如果文件不存在，首先下载文件
				String videoPath = "/data/data/com.example.windenglish/files/videos/video";
				videoPath += videoIdString;
				videoPath += "_4.mp4";
				
				final File f=new File(videoPath);

				if(!f.exists())
		        {  
					//提示用户需要下载文件
					Dialog alertDialog = new AlertDialog.Builder(Main.this)
					.setMessage("视频尚未下载，您要现在开始下载吗？")
					.setCancelable(false)
					.setPositiveButton("确定",new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							//dFlag=1;
							cancelFlag=0;
							//检查
							Context context = getApplicationContext();
							if(CheckIfWifi.checkIfWifi(context)== -1 )
							{
								//dFlag=0;
								Dialog alertDialog = new AlertDialog.Builder(Main.this)
								.setMessage("无可用网络连接")
								.setPositiveButton("确定", null)
								.create();
								alertDialog.show();
							}
							else if(CheckIfWifi.checkIfWifi(context)==0)
							{
								Dialog alertDialogDownload = new AlertDialog.Builder(Main.this)
								.setMessage("您确定要在非Wi-fi环境下下载吗？")
								.setCancelable(false)
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub

										pd = new ProgressDialog(Main.this);
										pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
										pd.setCanceledOnTouchOutside(false);
										pd.setCancelable(true);
										pd.setMessage("已下载5%……");
										pd.setTitle("下载中");
								        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {  
								            @Override  
								            public void onCancel(DialogInterface dialog) {  
								                // TODO Auto-generated method stub  
								            	cancelFlag=1;
								            }  
								        });  
										pd.show();
										
										Thread thread = new Thread(new Runnable() {
											
											@Override
											public void run() {
												// TODO Auto-generated method stub
								                Message message1=new Message();  
								                Message message2=new Message();  
								                Message message3=new Message();  
								                Message message4=new Message();  
												int flag=1;
												//下载第一段视频
												
												flag = VideoDownload.videoDownload(videoId,1);
												if(flag==0) 
												{
													message1.what=0;           
													mHandler.sendMessage(message1);
												}
												else if(cancelFlag==1)
									            	f.delete();
												else 
												{
													message1.what=1;  
													mHandler.sendMessage(message1);  
													
													//下载第二段视频
													flag = VideoDownload.videoDownload(videoId,2);
													if(flag==0)
													{
														message2.what=0;           
														mHandler.sendMessage(message2);
													}
													else if(cancelFlag==1)
														f.delete();
													else 
													{
														message2.what=2;  
														mHandler.sendMessage(message2);  
														
														//下载第三段视频
														flag = VideoDownload.videoDownload(videoId,3);
														if(flag==0)
														{
															message3.what=0;           
															mHandler.sendMessage(message3);
														}
														else if(cancelFlag==1)
															f.delete();
														else 
														{
															message3.what=3;  
															mHandler.sendMessage(message3);  
															
															//下载第四段视频
															flag = VideoDownload.videoDownload(videoId,4);
															if(flag==0)
															{
																message4.what=0;           
																mHandler.sendMessage(message4);
															}
															else if(cancelFlag==1)
																f.delete();
															else 
															{
																message4.what=4;  
																mHandler.sendMessage(message4);  
															}
														}
													}
												}
											}
										});
										thread.start();
										
									}
								})
								.setNegativeButton("取消", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										;
									}
								})
								.create();
								alertDialogDownload.show();
							}
							else{
							pd = new ProgressDialog(Main.this);
							pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
							pd.setCanceledOnTouchOutside(false);
							pd.setCancelable(true);
							pd.setMessage("已下载5%……");
							pd.setTitle("下载中");
					        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {  
					            @Override  
					            public void onCancel(DialogInterface dialog) {  
					                // TODO Auto-generated method stub  
					            	cancelFlag=1;
					            }  
					        });  
							pd.show();
							
							Thread thread = new Thread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
					                Message message1=new Message();  
					                Message message2=new Message();  
					                Message message3=new Message();  
					                Message message4=new Message();  
									int flag=1;
									//下载第一段视频
									
									flag = VideoDownload.videoDownload(videoId,1);
									if(flag==0) 
									{
										message1.what=0;           
										mHandler.sendMessage(message1);
									}
									else if(cancelFlag==1)
						            	f.delete();
									else 
									{
										message1.what=1;  
										mHandler.sendMessage(message1);  
										
										//下载第二段视频
										flag = VideoDownload.videoDownload(videoId,2);
										if(flag==0)
										{
											message2.what=0;           
											mHandler.sendMessage(message2);
										}
										else if(cancelFlag==1)
											f.delete();
										else 
										{
											message2.what=2;  
											mHandler.sendMessage(message2);  
											
											//下载第三段视频
											flag = VideoDownload.videoDownload(videoId,3);
											if(flag==0)
											{
												message3.what=0;           
												mHandler.sendMessage(message3);
											}
											else if(cancelFlag==1)
												f.delete();
											else 
											{
												message3.what=3;  
												mHandler.sendMessage(message3);  
												
												//下载第四段视频
												flag = VideoDownload.videoDownload(videoId,4);
												if(flag==0)
												{
													message4.what=0;           
													mHandler.sendMessage(message4);
												}
												else if(cancelFlag==1)
													f.delete();
												else 
												{
													message4.what=4;  
													mHandler.sendMessage(message4);  
												}
											}
										}
									}
								}
							});
							thread.start();
							}
							
							
							//pd.dismiss();
							
							/*
							if(flag == 0)
							{
								//提示错误信息
		    					Dialog alertDialog = new AlertDialog.Builder(Main.this)
		    					.setMessage("视频下载出错，请重试。")
		    					.setPositiveButton("确定",new DialogInterface.OnClickListener() {
		    						
		    						@Override
		    						public void onClick(DialogInterface dialog, int which) {
		    							// TODO Auto-generated method stub
		    							;
		    						}
		    					})
		    					.create();
		    					alertDialog.show(); 
							}
							else
							{

		    					Dialog alertDialog = new AlertDialog.Builder(Main.this)
		    					.setMessage("视频下载完毕！")
		    					.setPositiveButton("确定",new DialogInterface.OnClickListener() {
		    						
		    						@Override
		    						public void onClick(DialogInterface dialog, int which) {
		    							// TODO Auto-generated method stub
		    							;
		    						}
		    					})
		    					.create();
		    					alertDialog.show(); 
							}
							*/
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
					alertDialog.show(); 
					
		        }   
				/*
				//如果用户未录过音，那么直接进入看视频界面
				if(states[position]=='0')
				{
					intent.putExtra("videoId",videoIdString);
					intent.putExtra("playStyle","1");
					intent.setClass(Main.this, Play.class);
					Main.this.startActivity(intent);
				}
				
				//如果用户录过音，那么打开选项菜单
				*/ 
				else 
				{

			        AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
			        final String[] works = {"看视频", "看中说英", "对答案","删除视频"};
			        builder.setItems(works, new DialogInterface.OnClickListener()
			        {
			            @Override
			            public void onClick(DialogInterface dialog, int which)
			            {
			            	which = which + 1;
			            	String playStyleString = Integer.toString(which);
			            	if(which == 1)
			            	{
								intent.putExtra("videoId",videoIdString);
								intent.putExtra("playStyle",playStyleString);
								intent.setClass(Main.this, Play.class);
								Main.this.startActivity(intent);
			            	}
			            	else if (which == 2)
			            	{
			    				if(states[fposition]=='0')
			    				{
			    					//没看过不能录音
			    					Dialog alertDialog = new AlertDialog.Builder(Main.this)
			    					.setMessage("还没看过视频，不能直接录音！")
			    					.setPositiveButton("确定",new DialogInterface.OnClickListener() {
			    						
			    						@Override
			    						public void onClick(DialogInterface dialog, int which) {
			    							// TODO Auto-generated method stub
			    							;
			    						}
			    					})
			    					.create();
			    					alertDialog.show(); 
			    				}
			    				else 
			    				{
								intent.putExtra("videoId",videoIdString);
								intent.setClass(Main.this, Record.class);
								Main.this.startActivity(intent);
								}
			            	}
			            	else if (which == 3)
			            	{
			    				if(states[fposition]=='0')
			    				{
			    					//没看过不能对答案
			    					Dialog alertDialog = new AlertDialog.Builder(Main.this)
			    					.setMessage("还没看过视频，不能对答案！")
			    					.setPositiveButton("确定",new DialogInterface.OnClickListener() {
			    						
			    						@Override
			    						public void onClick(DialogInterface dialog, int which) {
			    							// TODO Auto-generated method stub
			    							;
			    						}
			    					})
			    					.create();
			    					alertDialog.show(); 
			    				}
			    				else if(states[fposition]=='3')
			    				{
			    					//没录音不能对答案
			    					Dialog alertDialog = new AlertDialog.Builder(Main.this)
			    					.setMessage("还没录音，不能对答案！")
			    					.setPositiveButton("确定",new DialogInterface.OnClickListener() {
			    						
			    						@Override
			    						public void onClick(DialogInterface dialog, int which) {
			    							// TODO Auto-generated method stub
			    							;
			    						}
			    					})
			    					.create();
			    					alertDialog.show(); 
			    				}
			    				else {
			    					
			    					Context context = getApplicationContext();
				    				if(CheckIfWifi.checkIfWifi(context)== -1 )
				    					{
				    						Dialog alertDialog = new AlertDialog.Builder(Main.this)
				    						.setMessage("无可用网络连接")
				    						.setPositiveButton("确定", null)
				    						.create();
				    						alertDialog.show();
				    					}
			    					
				    				else {

					    				intent.putExtra("videoId",videoIdString);
					    				intent.setClass(Main.this, Check.class);
					    				Main.this.startActivity(intent);
									}
								}
			            	}
			            	else if(which == 4)
			            	{
			            		f.delete();
			            	}
			            }
			        });
			        builder.show();
				}
		        //创建选项菜单
			}
		});
        //定位页面
        

        File file2 = new File("/data/data/com.example.windenglish/files/tempSC");
        if(file2.exists())
        {
        	String sCT = "";
    		try{   
    	         FileInputStream fin = openFileInput("tempSC");   
    	         int length = fin.available();   
    	         byte [] buffer = new byte[length];   
    	         fin.read(buffer);       
    	         sCT = EncodingUtils.getString(buffer, "UTF-8");   
    	         fin.close();       
    	     }   
    	     catch(Exception e){   
    	         e.printStackTrace();   
    	     }   
    		tempSC=sCT.split("\\?");
    		scX = Integer.parseInt(tempSC[0]);
    		scY = Integer.parseInt(tempSC[1]);
    		
    		
        
        	if(scY!=0)
        	{
        		if(scY>0) scY--;
        		videoList.setSelection(scY);
        	}
        }
        
	}

    @Override
	public void onBackPressed(){
    	Main.this.finish();
    	android.os.Process.killProcess(android.os.Process.myPid());
    }
    
	
}



