package com.pureweather.app.activity;

import com.pureweather.app.R;
import com.pureweather.app.utils.HttpCallbackListener;
import com.pureweather.app.utils.HttpUtils;
import com.pureweather.app.utils.ResponseHandleUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity implements OnClickListener{

	private TextView weatherTitle;
	private TextView updateTime;
	//private TextView aqiValue;
	private TextView pm25Value;
	//private TextView nowCond;
	private TextView nowTemp;
	private TextView forecastDate;
	private TextView rainyPos;
	private TextView maxTemp;
	private Button updateInfo;
	private ImageView condImage;
	//private TextView minTemp;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_activity);
		
		weatherTitle = (TextView)findViewById(R.id.weather_title);
		updateTime = (TextView)findViewById(R.id.update_time);
		pm25Value = (TextView)findViewById(R.id.pm25_value);
		nowTemp = (TextView)findViewById(R.id.now_temp);
		forecastDate = (TextView)findViewById(R.id.forecast_date);
		rainyPos = (TextView)findViewById(R.id.rainy_pos);
		maxTemp = (TextView)findViewById(R.id.max_min_temp);
		updateInfo = (Button)findViewById(R.id.update_weather);
		condImage = (ImageView)findViewById(R.id.cond_imag);
		updateInfo.setOnClickListener(this);
		String countyCode = getIntent().getStringExtra("county_code");
		
		if(!TextUtils.isEmpty(countyCode)){
			queryWeatherCode(countyCode);
			//Toast.makeText(WeatherActivity.this, countyCode, Toast.LENGTH_SHORT).show();
			//downloadWeatherInfo(countyCode);
		}
		else{
			showWeather();
		}		
				
	}

	private void queryWeatherCode(String countyCode) {
		// TODO Auto-generated method stub
		String address = new StringBuilder().append("http://www.weather.com.cn/data/list3/city")
				.append(countyCode).append(".xml").toString();
		searchInternetForInfo(address, "countyCode");
	}

	private void searchInternetForInfo(String address,final String type) {
		// TODO Auto-generated method stub
				
		HttpUtils.sendHttpRequest(address, new HttpCallbackListener(){

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				if(type.equals("countyCode")){
					if(!TextUtils.isEmpty(response)){
						String[] array = response.split("\\|");
						if(array != null && array.length == 2){
							createDownloadAddress(array[1]);
						}
					}
				}
				else if(type.equals("weatherCode")){
					ResponseHandleUtils.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}
						
					});					
				}
				

			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(WeatherActivity.this, "下载天气信息失败", Toast.LENGTH_SHORT).show();
					}
					
				});
			}
			
		});
	}

	private void showWeather() {
		// TODO Auto-generated method stub
		SharedPreferences pref = PreferenceManager.
				getDefaultSharedPreferences(this);
		weatherTitle.setText(pref.getString("city_name", ""));
		updateTime.setText(pref.getString("update_time", ""));
		pm25Value.setText(pref.getString("pm25_value", ""));
		nowTemp.setText(pref.getString("now_temp", ""));
		forecastDate.setText(pref.getString("forecast_date", ""));
		rainyPos.setText(pref.getString("rainy_pos", ""));
		maxTemp.setText(pref.getString("max_temp", ""));
		condImage.setImageResource(R.drawable.clond);
		
	}

	private void createDownloadAddress(String weatherCode) {
		// TODO Auto-generated method stub
		String address = new StringBuilder()
			.append("https://api.heweather.com/x3/weather?cityid=CN").append(weatherCode)
			.append("&key=37fa5d4ad1ea4d5da9f37e75732fb2e7").toString();	
		Toast.makeText(WeatherActivity.this, address, Toast.LENGTH_SHORT).show();
		searchInternetForInfo(address, "weatherCode");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case (R.id.update_weather):
			Intent intent = new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();			
			break;
		default:
			break;
		}

	}
}
