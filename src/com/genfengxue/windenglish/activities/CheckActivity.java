/**
 * 对答案界面 @author vita
 */

package com.genfengxue.windenglish.activities;

import java.io.File;
//import java.security.GeneralSecurityException;
import java.util.List;
//import java.lang.reflect.Field;
//import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
//import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
//import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
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

import com.genfengxue.windenglish.DensityUtil;
import com.genfengxue.windenglish.R;
import com.genfengxue.windenglish.network.JsonApiCaller;
import com.genfengxue.windenglish.utils.FileUtils;
import com.genfengxue.windenglish.utils.NetworkUtils;
import com.genfengxue.windenglish.utils.UriUtils;
//import android.os.Handler;
//import android.os.Message;

public class CheckActivity extends Activity {

	private Button backButton;
	private static int formerUnderline=0,formerChange = 0,formerLine=0;
	private static TextView answer;
	private static String KID;
	private static TextView knowledge;
    private static MediaPlayer player;
    private static Button likeButton,questionButton;
    private String likedPath;

    private static String jsonText;
    private static JSONArray jsonArray = new JSONArray();
    private static JSONArray jsonLikedArray = new JSONArray();
    private static int nowLine,nowNo;
    private static Intent questionIntent;
    private Button backVideoButton;
    private static String[] keyNumbers;
    private static JSONObject shownKnowledge;
    private static Button pauseVideoButton;
    private static float floatX,floatY;
    private RelativeLayout checkRelative;
    private static int H;
    static PopupWindow popupWindow;    
    private LayoutInflater inflater;
    private static View popLayout;
    private static View checkLayout;
	
    private int courseNo;
    private int lessonNo;
    
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		//初始化播放界面

		questionIntent = new Intent(CheckActivity.this, QuestionActivity.class);
		
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		H = mDisplayMetrics.heightPixels;
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		//初始化popupwindow
        inflater  = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        popLayout = inflater.inflate(R.layout.checkpopup,null);
        checkLayout = inflater.inflate(R.layout.check,null);
        popupWindow = new PopupWindow(popLayout, WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        popupWindow.update();
		
		//根据播放次数自定义布局
        setContentView(R.layout.check);
        
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        
        //获取播放的视频文件名
        Intent intent = getIntent();
        courseNo = intent.getIntExtra("courseNo", 1);
        lessonNo = intent.getIntExtra("lessonNo", 1);

        answer = (TextView)findViewById(R.id.answer);
        
        knowledge = (TextView)popLayout.findViewById(R.id.checkPopupText);
        String ssss="This is a popupWindows";
		knowledge.setText(ssss);
		initListener();
		
		new CheckAnswerTask(courseNo, lessonNo).execute();
	}
	
	private class CheckAnswerTask extends AsyncTask<Void, Void, String> {
		private int courseNo;
		private int lessonNo;
		private ProgressDialog progressDialog;
		
		public CheckAnswerTask(int courseNo, int lessonNo) {
			this.courseNo = courseNo;
			this.lessonNo = lessonNo;
			progressDialog = new ProgressDialog(CheckActivity.this);
			progressDialog.setTitle(R.string.getting_sentences);
		}
		
		protected void onPreExecute() {
			progressDialog.show();
		}
		
		@Override
		protected String doInBackground(Void... params) {
			if (NetworkUtils.isNetworkConnected(CheckActivity.this)) {
				return JsonApiCaller.getSentenceListApi(courseNo, lessonNo, true);
			} else {
				return JsonApiCaller.getSentenceListApi(courseNo, lessonNo);
			}
		}
		
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			jsonText = result;
			initCheck();
		}
	}
	
	private void initCheck() {
		//处理文本
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
			e2.printStackTrace();
		}
		getEachWord(answer);
		answer.setMovementMethod(LinkMovementMethod.getInstance());
		
		//读取标记记录
		likedPath = UriUtils.getLikedPath(courseNo, lessonNo);
		File likedJsonFile = new File(likedPath);
		
		if(likedJsonFile.exists())
		{
			String jsonLikedText = FileUtils.readFile(likedPath);
			
			if (jsonLikedText != null) {
				try {
					jsonLikedArray = new JSONArray(jsonLikedText);
				} catch (JSONException e) {
					e.printStackTrace();
					jsonLikedArray = new JSONArray();
				}
				for(int i = 0; i < jsonLikedArray.length(); i++)
				{
					int line = 0;
					String keys = null;
					JSONObject eachKnowledge;
					try {
						eachKnowledge = jsonLikedArray.getJSONObject(i);
						keys = eachKnowledge.getString("key");
						line = eachKnowledge.getInt("line");
					} catch (JSONException e1) {
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
		}
		
		//设置音频播放参数
		player = new MediaPlayer();
		String playerPath = UriUtils.getRecordPath(courseNo, lessonNo);
		
		try {
			player.setDataSource(playerPath);
			player.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		player.start();
	}
	
	private void initListener() {
		// 设置back按钮
		backButton = (Button) findViewById(R.id.checkBackButton);
		backButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// videoView.pause();
				player.pause();
				Dialog alertDialog = new AlertDialog.Builder(CheckActivity.this)
				.setMessage("现在退出，下次要重新对答案")
				.setCancelable(false)
				.setPositiveButton("继续对答案",
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						
						// videoView.start();
						player.start();
						
					}
				})
				.setNegativeButton("确认退出",
						new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						negativeButtonClick();
					}
				}).create();
				alertDialog.show();
			}
		});
		
		//设置向前按钮
		backVideoButton = (Button)findViewById(R.id.backVideoButton);
		backVideoButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v)
			{
				int currentTime = player.getCurrentPosition();
				currentTime -= 3000;
				player.seekTo(currentTime);
			}
		});
		
		//设置暂停按钮
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

    private void getEachWord(TextView answer2) {
    	//首先得到每一行 
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
				String texttt = null;
				try {
					texttt = getKnowledgeText(line, no);
				} catch (JSONException e) {
					//
					e.printStackTrace();
				}
				if (!texttt.equals("nothing")) {
					setUnderline(line, no);
				}

				while (checkText[end + 1] == ' ') {
					ClickableSpan clickSpan = getClickableSpan(line, no);
					if (start == 0) {
						start--;
					}
					spans.setSpan(clickSpan, start + 1, end + 1,
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					no++;
					end++;
					start = end;
				}
        	}
        	else if(checkText[end+1]=='\n')
        	{

				String texttt = null;
				try {
					texttt = getKnowledgeText(line, no);
				} catch (JSONException e) {
					//
					e.printStackTrace();
				}
				if (!texttt.equals("nothing")) {
					setUnderline(line, no);
				}

				ClickableSpan clickSpan = getClickableSpan(line, no);
				if (start == 0) {
					start--;
				}
				spans.setSpan(clickSpan, start + 1, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				end += 2;
				while (checkText[end] != '\n')
					end++;
				start = end;
				line++;
				if (line > lineindices.length / 2) {
					break;
				}
				no = 1;
				while (checkText[end + 1] == ' ') {
					ClickableSpan clickSpan1 = getClickableSpan(line, no);
					spans.setSpan(clickSpan1, start, end,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					no++;
					end += 2;
					start = end;
				}
        	}
        	end++;
        }
        answer2.setHighlightColor(Color.argb(180, 205, 205, 205)); 
	}


	//响应知识点事件
	private ClickableSpan getClickableSpan(final int line,final int no) {
		//          
        return new ClickableSpan() {                
            @Override                
            public void onClick(View widget) { 
            	
            	CheckActivity.changeHighLight(line, no, 2);
        		nowLine = line;
        		nowNo = no;
                showKnowledge(line,no);
            }                
            @Override                  
            public void updateDrawState(TextPaint ds) {                          
                ds.setColor(Color.BLACK);    
            }                                             
        };
	}


	//设置下划线
	protected static void setUnderline(int line, int no) {
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
        if(no-formerUnderline==1)
        {
        	if(start!=0)
        	{
        		start--;
        	}
        }
		formerUnderline=no;
        CharacterStyle span1 = new UnderlineSpan();
        spans.setSpan(span1, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}


	//显示文本和实例化按钮
	protected void showKnowledge(int line,int no){
		// 初始化
		likeButton = (Button)popLayout.findViewById(R.id.checkLike);
		questionButton = (Button)popLayout.findViewById(R.id.checkQuestion);
		String textt ="Oops,There's nothing here!";
		knowledge.setText(textt);
		likeButton.setBackgroundResource(R.drawable.none);
		
		//实例化按钮
		try {
			textt = getKnowledgeText(line,no);
		} catch (JSONException e) {
			// 
			e.printStackTrace();
		}
		if(!textt.equals("nothing"))
		{
			try {
				KID = shownKnowledge.getString("_id");
			} catch (JSONException e1) {
				// 
				e1.printStackTrace();
			}
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
					// 
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
		else {
			likeButton.setOnClickListener(null);
		}
		
		questionButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String hintText = "";
				try {
					JSONArray jsonArray = new JSONArray(jsonText);
					JSONObject jsonObject = jsonArray.getJSONObject(nowLine-1);
					hintText=jsonObject.getString("english");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				questionIntent.putExtra("Text", hintText);
//				questionIntent.putExtra("videoId", videoId);
				questionIntent.putExtra("courseNo", courseNo);
				questionIntent.putExtra("lessonNo", lessonNo);
				v.getContext().startActivity(questionIntent);
			}
		});
		
		player.pause();
		pauseVideoButton.setBackgroundResource(R.drawable.play);
        popupWindow.showAtLocation(checkLayout, Gravity.TOP|Gravity.LEFT, (int)floatX, (int)floatY);
	}

	//高亮文字方法
	@SuppressLint("RtlHardcoded")
	protected static void changeHighLight(int line, int no, int state) {
		// 

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
        if(no-formerChange==1)
        {
        	if(formerLine==line)
        	{
        		if(state!=2)
        		{
        			start--;
        		}
        	}
        }
        formerChange=no;
        formerLine=line;
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


	//更改知识点状态
	protected static void changeState(String KID2, int state) {
		// 
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
				// 
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
					// 
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
						// 
						e.printStackTrace();
					}  
				}
			}
		}
	}

	//检测是否已mark
	protected static boolean hasLiked(String KID) {
		// 
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
				// 
				e.printStackTrace();
			}
		}
		return false;
	}

	//获取知识点文本
	private static String getKnowledgeText(int line, int no) throws JSONException {
		// 

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
		}
		return result;
	}


	//截获touch事件
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// 
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			//更改颜色
			if(chosenHasLiked())
			{
	        	changeHighLight(nowLine, nowNo, 1);
			}
			else {
	        	changeHighLight(nowLine, nowNo, 0);
			}
			
			return super.dispatchTouchEvent(ev);
			
		default:
			//确定浮动框的位置
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
	
	//检测点击的单词是否被mark
    private boolean chosenHasLiked() {
		// 
    	String textt = null;
    	try {
			textt = getKnowledgeText(nowLine,nowNo);
		} catch (JSONException e) {
			// 
			e.printStackTrace();
		}
		try {
			if(shownKnowledge!=null)
			KID = shownKnowledge.getString("_id");
		} catch (JSONException e1) {
			// 
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

    //
    @Override
    protected void onPause() {
    	super.onPause();
    	player.pause();
    }

	@Override
	public void onBackPressed(){
		//videoView.pause();
		player.pause();
		Dialog alertDialog = new AlertDialog.Builder(CheckActivity.this)
		.setMessage("现在退出，下次要重新对答案")
		.setCancelable(false)
		.setPositiveButton("继续对答案",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//videoView.start();
				player.start();
				
			}
		})
		.setNegativeButton("确认退出",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				negativeButtonClick();
			}
		})
		.create();
		alertDialog.show(); 
    }
    
	private void negativeButtonClick() {
		FileUtils.writeFile(likedPath, jsonLikedArray.toString());
		CheckActivity.this.finish();
	}
}
