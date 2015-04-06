/**
 * 用户个人主页@author vita
 * 暂未完善，目前用来修改邮箱
 */

package com.genfengxue.windenglish;

import java.io.FileOutputStream;

import com.genfengxue.windenglish.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class Profile extends Activity{

	private Context  context = this;  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.profile);
	
		final EditText editText = (EditText) findViewById(R.id.profileText1);
		final Button button = (Button)findViewById(R.id.profileButton1);
		
        
        TextWatcher textWatcher = new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
		        int length= editText.getText().toString().length();  
		        if(length>0)
		        {
		        	button.setBackgroundColor(Color.parseColor("#691281"));
		        }
		        else {
		        	button.setBackgroundColor(Color.parseColor("#aaaaaa"));
				}
			}
		};
		editText.addTextChangedListener(textWatcher);
		
		button.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String email = editText.getText().toString();
				FileOutputStream out = null;  
		        String fileName = "email";
		        try {  
		            out = context.openFileOutput(fileName, Context.MODE_PRIVATE);  
		            out.write(email.getBytes("UTF-8"));  
			        out.close();
		        } catch (Exception e) {  
					// TODO Auto-generated catch block
		            e.printStackTrace();  
		        }  

				Intent intent = new Intent(Profile.this, Main.class);
				startActivity(intent);
			}
		});
	}
	
}
