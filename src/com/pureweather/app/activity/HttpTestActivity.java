package com.pureweather.app.activity;

import com.pureweather.app.R;
import com.pureweather.app.utils.HttpCallbackListener;
import com.pureweather.app.utils.HttpUtils;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HttpTestActivity extends Activity {
	
	private TextView responseText;
	private Button sendButton;
	//static final String KEY =  "&key=37fa5d4ad1ea4d5da9f37e75732fb2e7";
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		responseText = (TextView) findViewById(R.id.response);
		responseText.setMovementMethod(new ScrollingMovementMethod());
		sendButton = (Button) findViewById(R.id.send);
		//final String httpUrl = "https://api.heweather.com/x3/citylist?search=allchina&key=37fa5d4ad1ea4d5da9f37e75732fb2e7";
		final String httpUrl = "https://api.heweather.com/x3/weather?cityid=CN101281801&key=37fa5d4ad1ea4d5da9f37e75732fb2e7";
		sendButton.setOnClickListener(new OnClickListener(){
		
		
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				HttpUtils.sendHttpRequest(httpUrl, new HttpCallbackListener(){
					@Override
					public void onFinish(final String response) {
						// TODO Auto-generated method stub
						runOnUiThread(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								responseText.setText(response);
							}
							
						});
					}

					@Override
					public void onError(Exception e) {
						// TODO Auto-generated method stub
						runOnUiThread(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub

								Toast.makeText(HttpTestActivity.this, "Á´½Ó´íÎó", Toast.LENGTH_SHORT).show();
								//responseText.setText(response);
							}
							
						});						
					}
					
				});				
			}
			
		});

	}
}
