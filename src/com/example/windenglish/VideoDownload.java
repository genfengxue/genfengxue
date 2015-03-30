/**
 * 下载操作@author vita
 */

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
		//组装地址
		String fileName ;
		String videoUrl = "http://7u2qm8.com1.z0.glb.clouddn.com/video";
		String dirName = "/data/data/com.example.windenglish/files/videos/";
	    videoUrl += Integer.toString(videoId);
	    videoUrl += "_";
	    videoUrl += Integer.toString(part);
	    videoUrl += ".mp4";
		
	    //检测文件是否存在
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

	    	    	   //构造URL
	    	           URL url = new URL(fvideoUrl);
	    	           //打开连接
	    	           final URLConnection con = url.openConnection();
	    	           //获得文件的长度
	    	           //int contentLength = con.getContentLength();
	    	           //System.out.println("长度 :"+contentLength);
	    	           //输入流
	    	           InputStream is = con.getInputStream();
	    	           //1K的数据缓冲
	    	           byte[] bs = new byte[1024];
	    	           //读取到的数据长度
	    	           int len;
	    	           //输出的文件流
	    	           OutputStream os = new FileOutputStream(ffileName);
	    	           //开始读取
	    	           while ((len = is.read(bs)) != -1) {
	    	               os.write(bs, 0, len);
	    	           }
	    	           //完毕，关闭所有链接
	    	           os.close();
	    	           is.close();

	        		   }catch(Exception e){
	        			   e.printStackTrace();
	        			   return 0;
	        		   }
	    
		return 1;
	}

}
