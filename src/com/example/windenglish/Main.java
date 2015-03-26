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
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

//��½activity
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
				pd.setMessage("������25%����");
				break;
            case 2:
				pd.setMessage("������50%����");
				break;
            case 3:
				pd.setMessage("������75%����");
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
				.setMessage("��Ƶ������ϣ�")
				.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
					
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
				.setMessage("��Ƶ���س��������ԡ�")
				.setPositiveButton("ȷ��",null)
				.create();
				alertDialogError.show(); 
                break;  
            }  
            super.handleMessage(msg);  
        }  
    };  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//�����ʼ��
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
		
        
		//���ԴӶ�ȡ�û���Ϣ
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
        

        //�����޸��û���Ϣ
        mainUsername.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

		    	Dialog alertDialog = new AlertDialog.Builder(Main.this)
				.setMessage("��ע�⣬���޸��ǳƽ���ɾ�����ȫ��ѧϰ��¼��")
				.setPositiveButton("�޸��ǳ�",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						File userFile = new File("/data/data/com.example.windenglish/files/userdata");
						userFile.delete();
						Intent intent = new Intent(Main.this, Login.class);
						startActivity(intent);
						Main.this.finish();
					}
				})
				.setNegativeButton("ȡ��", null)
				
				
				
				.setNeutralButton("�޸�����", new DialogInterface.OnClickListener() {
					
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
        
        //������
        videoList = (ListView)findViewById(R.id.videoList);
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,Object>>();
        
        //TODO:�������ֶ����ÿһ�ογ̵���ϸ��Ϣ
        
        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson1);//����ͼƬ          
        map.put("ItemTitle", "1");  //���
        map.put("ItemTextEnglish", "Introduction 1");  
        map.put("ItemTextChinese", "���ҽ��� 1"); 
        file = new File(frontFile + "1_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[1]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[1]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson2);//����ͼƬ          
        map.put("ItemTitle", "2");  
        map.put("ItemTextEnglish", "Introduction 2");  
        map.put("ItemTextChinese", "���ҽ��� 2"); 
        file = new File(frontFile + "2_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[2]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[2]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson3);//����ͼƬ          
        map.put("ItemTitle", "3");  
        map.put("ItemTextEnglish", "Job Description");  
        map.put("ItemTextChinese", "��������");  
        file = new File(frontFile + "3_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[3]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[3]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson4);//����ͼƬ          
        map.put("ItemTitle", "4");  
        map.put("ItemTextEnglish", "Baby Talk");  
        map.put("ItemTextChinese", "��Ӥ����̸��");   
        file = new File(frontFile + "4_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[4]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[4]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson5);//����ͼƬ          
        map.put("ItemTitle", "5");  
        map.put("ItemTextEnglish", "Time Flies");  
        map.put("ItemTextChinese", "ʱ�����");  
        file = new File(frontFile + "5_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[5]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[5]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson6);//����ͼƬ          
        map.put("ItemTitle", "6");  
        map.put("ItemTextEnglish", "The Brochure");  
        map.put("ItemTextChinese", "С����");   
        file = new File(frontFile + "6_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[6]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[6]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson7);//����ͼƬ          
        map.put("ItemTitle", "7");  
        map.put("ItemTextEnglish", "Out to Lunch-Decision");  
        map.put("ItemTextChinese", "������-����");   
        file = new File(frontFile + "7_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[7]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[7]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson8);//����ͼƬ          
        map.put("ItemTitle", "8");  
        map.put("ItemTextEnglish", "Out to Lunch-Ordering");  
        map.put("ItemTextChinese", "������-���");   
        file = new File(frontFile + "8_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[8]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[8]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson9);//����ͼƬ          
        map.put("ItemTitle", "9");  
        map.put("ItemTextEnglish", "Not Without My Bags!");  
        map.put("ItemTextChinese", "�ҵİ������ˣ�");   
        file = new File(frontFile + "9_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[9]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[9]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson10);//����ͼƬ          
        map.put("ItemTitle", "10");  
        map.put("ItemTextEnglish", "Check In");  
        map.put("ItemTextChinese", "�Ǽ�");   
        file = new File(frontFile + "10_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[10]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[10]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);    

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson11);//����ͼƬ          
        map.put("ItemTitle", "11");  
        map.put("ItemTextEnglish", "Partners");  
        map.put("ItemTextChinese", "���");   
        file = new File(frontFile + "11_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[11]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[11]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson12);//����ͼƬ          
        map.put("ItemTitle", "12");  
        map.put("ItemTextEnglish", "No Cream");  
        map.put("ItemTextChinese", "��Ҫ����");   
        file = new File(frontFile + "12_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[12]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[12]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson13);//����ͼƬ          
        map.put("ItemTitle", "13");  
        map.put("ItemTextEnglish", "City Tour");  
        map.put("ItemTextChinese", "���й۹�");   
        file = new File(frontFile + "13_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[13]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[13]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson14);//����ͼƬ          
        map.put("ItemTitle", "14");  
        map.put("ItemTextEnglish", "Star Signs");  
        map.put("ItemTextChinese", "����");   
        file = new File(frontFile + "14_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[14]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[14]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson15);//����ͼƬ          
        map.put("ItemTitle", "15");  
        map.put("ItemTextEnglish", "Baggage Experts");  
        map.put("ItemTextChinese", "����ר��");   
        file = new File(frontFile + "15_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[15]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[15]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson16);//����ͼƬ          
        map.put("ItemTitle", "16");  
        map.put("ItemTextEnglish", "Market Research");  
        map.put("ItemTextChinese", "�г�����");   
        file = new File(frontFile + "16_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[16]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[16]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson17);//����ͼƬ          
        map.put("ItemTitle", "17");  
        map.put("ItemTextEnglish", "Wendy at Work");  
        map.put("ItemTextChinese", "����ʱ��wendy");   
        file = new File(frontFile + "17_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[17]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[17]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson18);//����ͼƬ          
        map.put("ItemTitle", "18");  
        map.put("ItemTextEnglish", "William's Day-off");  
        map.put("ItemTextChinese", "William�ļ���");   
        file = new File(frontFile + "18_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[18]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[18]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson19);//����ͼƬ          
        map.put("ItemTitle", "19");  
        map.put("ItemTextEnglish", "Nu la's Interview");  
        map.put("ItemTextChinese", "Ŭ��������");   
        file = new File(frontFile + "19_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[19]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[19]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson20);//����ͼƬ          
        map.put("ItemTitle", "20");  
        map.put("ItemTextEnglish", "Celebrity!");  
        map.put("ItemTextChinese", "����");   
        file = new File(frontFile + "20_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[20]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[20]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson21);//����ͼƬ          
        map.put("ItemTitle", "21");  
        map.put("ItemTextEnglish", "Whose Day Off");  
        map.put("ItemTextChinese", "˭�ļ���");   
        file = new File(frontFile + "21_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[21]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[21]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson22);//����ͼƬ          
        map.put("ItemTitle", "22");  
        map.put("ItemTextEnglish", "No Problem");  
        map.put("ItemTextChinese", "û����");   
        file = new File(frontFile + "22_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[22]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[22]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson23);//����ͼƬ          
        map.put("ItemTitle", "23");  
        map.put("ItemTextEnglish", "Use the Computer");  
        map.put("ItemTextChinese", "ʹ�õ���");  
        file = new File(frontFile + "23_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else  
        if(states[23]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[23]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson24);//����ͼƬ          
        map.put("ItemTitle", "24");  
        map.put("ItemTextEnglish", "A Small Problem");  
        map.put("ItemTextChinese", "С����");   
        file = new File(frontFile + "24_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[24]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[24]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson25);//����ͼƬ          
        map.put("ItemTitle", "25");  
        map.put("ItemTextEnglish", "Dean's Birthday Present");  
        map.put("ItemTextChinese", "Dean����������");   
        file = new File(frontFile + "25_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[25]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[25]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson26);//����ͼƬ          
        map.put("ItemTitle", "26");  
        map.put("ItemTextEnglish", "Office Supplies");  
        map.put("ItemTextChinese", "�칫��Ʒ");   
        file = new File(frontFile + "26_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else 
        if(states[26]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[26]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson27);//����ͼƬ          
        map.put("ItemTitle", "27");  
        map.put("ItemTextEnglish", "Simon's Dream");  
        map.put("ItemTextChinese", "Simon����");   
        file = new File(frontFile + "27_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[27]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[27]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson28);//����ͼƬ          
        map.put("ItemTitle", "28");  
        map.put("ItemTextEnglish", "Anne's passport");  
        map.put("ItemTextChinese", "Anne�Ļ���");   
        file = new File(frontFile + "28_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[28]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[28]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson29);//����ͼƬ          
        map.put("ItemTitle", "29");  
        map.put("ItemTextEnglish", "Relatives");  
        map.put("ItemTextChinese", "����");   
        file = new File(frontFile + "29_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[29]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[29]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson30);//����ͼƬ          
        map.put("ItemTitle", "30");  
        map.put("ItemTextEnglish", "No turns");  
        map.put("ItemTextChinese", "��׼����");   
        file = new File(frontFile + "30_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[30]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[30]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson31);//����ͼƬ          
        map.put("ItemTitle", "31");  
        map.put("ItemTextEnglish", "Where is it");  
        map.put("ItemTextChinese", "��������");   
        file = new File(frontFile + "31_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[31]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[31]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson32);//����ͼƬ          
        map.put("ItemTitle", "32");  
        map.put("ItemTextEnglish", "No jet lag");  
        map.put("ItemTextChinese", "û��ʱ��");   
        file = new File(frontFile + "32_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[32]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[32]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson33);//����ͼƬ          
        map.put("ItemTitle", "33");  
        map.put("ItemTextEnglish", "Trip report");  
        map.put("ItemTextChinese", "���б���");   
        file = new File(frontFile + "33_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[33]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[33]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson34);//����ͼƬ          
        map.put("ItemTitle", "34");  
        map.put("ItemTextEnglish", "On foot");  
        map.put("ItemTextChinese", "����");   
        file = new File(frontFile + "34_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[34]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[34]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson35);//����ͼƬ          
        map.put("ItemTitle", "35");  
        map.put("ItemTextEnglish", "William's dream");  
        map.put("ItemTextChinese", "William����");   
        file = new File(frontFile + "35_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[35]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[35]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson36);//����ͼƬ          
        map.put("ItemTitle", "36");  
        map.put("ItemTextEnglish", "Business report");  
        map.put("ItemTextChinese", "��ҵ����");   
        file = new File(frontFile + "36_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[36]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[36]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson37);//����ͼƬ          
        map.put("ItemTitle", "37");  
        map.put("ItemTextEnglish", "Education");  
        map.put("ItemTextChinese", "����");   
        file = new File(frontFile + "37_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[37]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[37]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson38);//����ͼƬ          
        map.put("ItemTitle", "38");  
        map.put("ItemTextEnglish", "One percent");  
        map.put("ItemTextChinese", "�ٷ�֮һ");   
        file = new File(frontFile + "38_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[38]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[38]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson39);//����ͼƬ          
        map.put("ItemTitle", "39");  
        map.put("ItemTextEnglish", "Not your job!");  
        map.put("ItemTextChinese", "������Ĺ���");   
        file = new File(frontFile + "39_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[39]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[39]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson40);//����ͼƬ          
        map.put("ItemTitle", "40");  
        map.put("ItemTextEnglish", "Doctor!");  
        map.put("ItemTextChinese", "ҽ��!");   
        file = new File(frontFile + "40_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[40]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[40]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson41);//����ͼƬ          
        map.put("ItemTitle", "41");  
        map.put("ItemTextEnglish", "Business travel");  
        map.put("ItemTextChinese", "����");   
        file = new File(frontFile + "41_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[41]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[41]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson42);//����ͼƬ          
        map.put("ItemTitle", "42");  
        map.put("ItemTextEnglish", "Blind date");  
        map.put("ItemTextChinese", "����");   
        file = new File(frontFile + "42_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[42]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[42]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson43);//����ͼƬ          
        map.put("ItemTitle", "43");  
        map.put("ItemTextEnglish", "An interesting combination");  
        map.put("ItemTextChinese", "��Ȥ�����");   
        file = new File(frontFile + "43_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[43]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[43]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson44);//����ͼƬ          
        map.put("ItemTitle", "44");  
        map.put("ItemTextEnglish", "Business with Nu La");  
        map.put("ItemTextChinese", "��Nu La������");   
        file = new File(frontFile + "44_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[44]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[44]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson45);//����ͼƬ          
        map.put("ItemTitle", "45");  
        map.put("ItemTextEnglish", "Menu planning");  
        map.put("ItemTextChinese", "�ƶ��˵�");   
        file = new File(frontFile + "45_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[45]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[45]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson46);//����ͼƬ          
        map.put("ItemTitle", "46");  
        map.put("ItemTextEnglish", "New menu");  
        map.put("ItemTextChinese", "�²˵�");   
        file = new File(frontFile + "46_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[46]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[46]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson47);//����ͼƬ          
        map.put("ItemTitle", "47");  
        map.put("ItemTextEnglish", "Rock star");  
        map.put("ItemTextChinese", "ҡ������");   
        file = new File(frontFile + "47_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[47]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[47]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson48);//����ͼƬ          
        map.put("ItemTitle", "48");  
        map.put("ItemTextEnglish", "On camera");  
        map.put("ItemTextChinese", "�Ͼ�");   
        file = new File(frontFile + "48_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[48]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[48]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson49);//����ͼƬ          
        map.put("ItemTitle", "49");  
        map.put("ItemTextEnglish", "Phone call");  
        map.put("ItemTextChinese", "��绰");   
        file = new File(frontFile + "49_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[49]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[49]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson50);//����ͼƬ          
        map.put("ItemTitle", "50");  
        map.put("ItemTextEnglish", "A beauty");  
        map.put("ItemTextChinese", "һ������");   
        file = new File(frontFile + "50_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[50]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[50]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson51);//����ͼƬ          
        map.put("ItemTitle", "51");  
        map.put("ItemTextEnglish", "On the road");  
        map.put("ItemTextChinese", "��·��");   
        file = new File(frontFile + "51_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[51]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[51]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson52);//����ͼƬ          
        map.put("ItemTitle", "52");  
        map.put("ItemTextEnglish", "A long way from home");  
        map.put("ItemTextChinese", "��Һ�Զ");   
        file = new File(frontFile + "52_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[52]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[52]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson53);//����ͼƬ          
        map.put("ItemTitle", "53");  
        map.put("ItemTextEnglish", "Group booking");  
        map.put("ItemTextChinese", "����Ԥ��");   
        file = new File(frontFile + "53_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[53]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[53]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson54);//����ͼƬ          
        map.put("ItemTitle", "54");  
        map.put("ItemTextEnglish", "Travel plans");  
        map.put("ItemTextChinese", "���мƻ�");   
        file = new File(frontFile + "54_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[54]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[54]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson55);//����ͼƬ          
        map.put("ItemTitle", "55");  
        map.put("ItemTextEnglish", "Radio reporter");  
        map.put("ItemTextChinese", "�㲥��̨����");   
        file = new File(frontFile + "55_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[55]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[55]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson56);//����ͼƬ          
        map.put("ItemTitle", "56");  
        map.put("ItemTextEnglish", "I wanna do that!");  
        map.put("ItemTextChinese", "�������Ǹ�");   
        file = new File(frontFile + "56_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[56]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[56]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson57);//����ͼƬ          
        map.put("ItemTitle", "57");  
        map.put("ItemTextEnglish", "Computer training");  
        map.put("ItemTextChinese", "�������ѵ");   
        file = new File(frontFile + "57_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[57]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[57]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson58);//����ͼƬ          
        map.put("ItemTitle", "58");  
        map.put("ItemTextEnglish", "I made it!");  
        map.put("ItemTextChinese", "�ҳɹ���!");   
        file = new File(frontFile + "58_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[58]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[58]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson59);//����ͼƬ          
        map.put("ItemTitle", "59");  
        map.put("ItemTextEnglish", "Look before you leap");  
        map.put("ItemTextChinese", "��˼������");   
        file = new File(frontFile + "59_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[59]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[59]=='1')
        	map.put("ItemState", R.drawable.recorded);
        else
        	map.put("ItemState", R.drawable.waitingrecord);
        listItem.add(map);  

        map = new HashMap<String, Object>();  
        map.put("ItemImage", R.drawable.lesson60);//����ͼƬ          
        map.put("ItemTitle", "60");  
        map.put("ItemTextEnglish", "Real estate");  
        map.put("ItemTextChinese", "���ز�");   
        file = new File(frontFile + "60_4.mp4");
        if(!file.exists())
        	map.put("ItemState", R.drawable.waitingdownload);
        else if(states[60]=='2') 
        	map.put("ItemState", R.drawable.submitted);
        else if(states[60]=='1')
        	map.put("ItemState", R.drawable.recorded);
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
				final String videoIdString = Integer.toString(videoId);
				final Intent intent = new Intent();
				final int fposition = position;
				
				//����ļ������ڣ����������ļ�
				String videoPath = "/data/data/com.example.windenglish/files/videos/video";
				videoPath += videoIdString;
				videoPath += "_4.mp4";
				
				final File f=new File(videoPath);

				if(!f.exists())
		        {  
					//��ʾ�û���Ҫ�����ļ�
					Dialog alertDialog = new AlertDialog.Builder(Main.this)
					.setMessage("��Ƶ��δ���أ���Ҫ���ڿ�ʼ������")
					.setCancelable(false)
					.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							//dFlag=1;
							cancelFlag=0;
							//���
							Context context = getApplicationContext();
							if(CheckIfWifi.checkIfWifi(context)== -1 )
							{
								//dFlag=0;
								Dialog alertDialog = new AlertDialog.Builder(Main.this)
								.setMessage("�޿�����������")
								.setPositiveButton("ȷ��", null)
								.create();
								alertDialog.show();
							}
							else if(CheckIfWifi.checkIfWifi(context)==0)
							{
								Dialog alertDialogDownload = new AlertDialog.Builder(Main.this)
								.setMessage("��ȷ��Ҫ�ڷ�Wi-fi������������")
								.setCancelable(false)
								.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub

										pd = new ProgressDialog(Main.this);
										pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
										pd.setCanceledOnTouchOutside(false);
										pd.setCancelable(true);
										pd.setMessage("������5%����");
										pd.setTitle("������");
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
												//���ص�һ����Ƶ
												
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
													
													//���صڶ�����Ƶ
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
														
														//���ص�������Ƶ
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
															
															//���ص��Ķ���Ƶ
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
								.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
									
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
							pd.setMessage("������5%����");
							pd.setTitle("������");
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
									//���ص�һ����Ƶ
									
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
										
										//���صڶ�����Ƶ
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
											
											//���ص�������Ƶ
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
												
												//���ص��Ķ���Ƶ
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
								//��ʾ������Ϣ
		    					Dialog alertDialog = new AlertDialog.Builder(Main.this)
		    					.setMessage("��Ƶ���س��������ԡ�")
		    					.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
		    						
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
		    					.setMessage("��Ƶ������ϣ�")
		    					.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
		    						
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
					.setNegativeButton("ȡ��",new DialogInterface.OnClickListener() {
						
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
				//����û�δ¼��������ôֱ�ӽ��뿴��Ƶ����
				if(states[position]=='0')
				{
					intent.putExtra("videoId",videoIdString);
					intent.putExtra("playStyle","1");
					intent.setClass(Main.this, Play.class);
					Main.this.startActivity(intent);
				}
				
				//����û�¼��������ô��ѡ��˵�
				*/ 
				else 
				{

			        AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
			        final String[] works = {"����Ƶ", "����˵Ӣ", "�Դ�","ɾ����Ƶ"};
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
			    					//û��������¼��
			    					Dialog alertDialog = new AlertDialog.Builder(Main.this)
			    					.setMessage("��û������Ƶ������ֱ��¼����")
			    					.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
			    						
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
			    					//û�������ܶԴ�
			    					Dialog alertDialog = new AlertDialog.Builder(Main.this)
			    					.setMessage("��û������Ƶ�����ܶԴ𰸣�")
			    					.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
			    						
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
			    					//û¼�����ܶԴ�
			    					Dialog alertDialog = new AlertDialog.Builder(Main.this)
			    					.setMessage("��û¼�������ܶԴ𰸣�")
			    					.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
			    						
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
				    						.setMessage("�޿�����������")
				    						.setPositiveButton("ȷ��", null)
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
		        //����ѡ��˵�
			}
		});
        //��λҳ��
        

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



