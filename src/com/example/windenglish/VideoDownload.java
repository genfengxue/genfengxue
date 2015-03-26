package com.example.windenglish;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class VideoDownload {
	
	public static int videoDownload(int videoId,int part)
	{
		String fileName ;
		String videoUrl = "http://7u2qm8.com1.z0.glb.clouddn.com/video";
		String dirName = "/data/data/com.example.windenglish/files/videos/";
	    videoUrl += Integer.toString(videoId);
	    videoUrl += "_";
	    videoUrl += Integer.toString(part);
	    videoUrl += ".mp4";
		
	    File f = new File(dirName);
	    if(!f.exists())
	    {
	       f.mkdir();
	    }

	    fileName = dirName + "video" ; 
	    fileName += Integer.toString(videoId);
	    fileName += "_";
	    fileName += Integer.toString(part);
	    fileName += ".mp4";
	    
	    final String ffileName = fileName;
	    final String fvideoUrl =  videoUrl;
	    
	    
	    File file = new File(fileName);
	    if(file.exists())
	    {
	       file.delete();
	    }
	    
	     

	        		   try{   

	    	    	   //����URL
	    	           URL url = new URL(fvideoUrl);
	    	           //������
	    	           final URLConnection con = url.openConnection();
	    	           //����ļ��ĳ���
	    	           //int contentLength = con.getContentLength();
	    	           //System.out.println("���� :"+contentLength);
	    	           //������
	    	           InputStream is = con.getInputStream();
	    	           //1K�����ݻ���
	    	           byte[] bs = new byte[1024];
	    	           //��ȡ�������ݳ���
	    	           int len;
	    	           //������ļ���
	    	           OutputStream os = new FileOutputStream(ffileName);
	    	           //��ʼ��ȡ
	    	           while ((len = is.read(bs)) != -1) {
	    	               os.write(bs, 0, len);
	    	           }
	    	           //��ϣ��ر���������
	    	           os.close();
	    	           is.close();

	        		   }catch(Exception e){
	        			   e.printStackTrace();
	        			   return 0;
	        		   }
	    
		return 1;
	}

}
