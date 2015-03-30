/**
 * 检测网络环境@author vita
 * WIFI返回1，无网络连接返回-1，其他情况返回0
 */

package com.example.windenglish;

import android.content.Context;
import android.net.*;

public class CheckIfWifi {
	 

	public static int checkIfWifi(Context context) {
		// TODO Auto-generated method stub

	    if (context != null) {  
	        ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
	                .getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
	        if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {  
	            if("WIFI".equals(mNetworkInfo.getTypeName()))
	            {
	            	return 1;
	            }
	            else//("MOBILE".equals(mNetworkInfo.getTypeName()))
	            {
	            	return 0;
	            }
	        }  
	        else {
	    	    return -1;  
			}
	    }
		return 0;  
	}

}
