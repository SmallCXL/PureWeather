package com.pureweather.app.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.pureweather.app.R;
import com.pureweather.app.model.PureWeatherDB;
import com.pureweather.app.service.AutoUpdateService;
import com.pureweather.app.utils.HttpCallbackListener;
import com.pureweather.app.utils.HttpUtils;
import com.pureweather.app.utils.ResponseHandleUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity implements OnClickListener{

	//private TextView weatherTitle;
	private TextView updateTime;
	//private TextView aqiValue;
	private TextView pm25Value;
	//private TextView nowCond;
	private TextView nowTemp;	
	private TextView nowCond;
	private TextView suggestion;
	private TextView rainyPos;
	private TextView tempRange;
	private TextView humiValue;
	private Button switchCity;
	private Button updateInfo;
	private Button settingButton;
	//private ImageView condImage;
	private PureWeatherDB pureWeatherDB;

	//private ProgressDialog progressDialog;
	

	//private String cityName = null;
	private String lastCity = null;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_activity_layout);
		
		SharedPreferences pref = PreferenceManager.
				getDefaultSharedPreferences(this);
		lastCity = pref.getString("last_city", "");	
		
		if(TextUtils.isEmpty(lastCity)){
			//û��lastCity��ֱ��ȥ����ҳ��
			Toast.makeText(WeatherActivity.this, "û�г�����Ϣ�ɹ���ʾ��ȥѡ��һ����~", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this,SearchCityActivity.class);
			startActivity(intent);
			finish();			
		}
		
		//weatherTitle = (TextView)findViewById(R.id.weather_title);
		
		suggestion = (TextView)findViewById(R.id.suggestion);
		updateTime = (TextView)findViewById(R.id.update_time);
		pm25Value = (TextView)findViewById(R.id.pm25_value);
		nowTemp = (TextView)findViewById(R.id.now_temp);
		nowCond = (TextView)findViewById(R.id.now_cond);
		humiValue = (TextView)findViewById(R.id.humidity);
		//forecastDate = (TextView)findViewById(R.id.forecast_date);
		rainyPos = (TextView)findViewById(R.id.rainy_pos);
		tempRange = (TextView)findViewById(R.id.temp_range);
		switchCity = (Button)findViewById(R.id.weather_activity_switch_city);
		settingButton = (Button)findViewById(R.id.weather_activity_setting);
		updateInfo = (Button)findViewById(R.id.weather_title);
				
		//condImage = (ImageView)findViewById(R.id.cond_imag);
		
		pureWeatherDB = PureWeatherDB.getInstance(this);
		
		updateInfo.setOnClickListener(this);
		switchCity.setOnClickListener(this);
		settingButton.setOnClickListener(this);
		
		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
		
		showWeather();	
	}
	@Override
	public void onResume(){
		super.onResume();
		SharedPreferences pref = PreferenceManager.
				getDefaultSharedPreferences(this);
		lastCity = pref.getString("last_city", "");	
		showWeather();	
	} 


	private void searchInternetForInfo(String address) {
		// TODO Auto-generated method stub
				
		HttpUtils.sendHttpRequest(address, new HttpCallbackListener(){

			@Override
			public void onFinish(String response) {//�����ṩ�������ݻ�Ӧ
				// TODO Auto-generated method stub

					
					SharedPreferences pref = PreferenceManager.
							getDefaultSharedPreferences(WeatherActivity.this);	
					
					//�������ƴ������ݿ�ʧ�ܵĴ���
					
					//��ȡ����ϢΪ��Ч��������Ϣ����UI�̸߳���UI����ʾ
					if(pureWeatherDB.saveWeather(response)){
						
						//ȡ��response�е� city �ֶβ�������pref�е�"city_name"
						ResponseHandleUtils.handleWeatherResponse(WeatherActivity.this, response);
						
						lastCity = pref.getString("city_name", "");
						
						runOnUiThread(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								showWeather();
								//Toast.makeText(WeatherActivity.this, "��ˢ��������Ϣ~", Toast.LENGTH_SHORT).show();
							}
						});							
					}
					//��ȡ��Ϣʧ��
					else{
						//closeMyDialog();
						Toast.makeText(WeatherActivity.this, "��ʱû��������е�������Ϣ���������¼��ذ�~", Toast.LENGTH_SHORT).show();
					}
				
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						//closeMyDialog();
						Toast.makeText(WeatherActivity.this, "Sorry����������ʧ�ܣ������ԣ�", Toast.LENGTH_SHORT).show();
					}
					
				});
			}
			
		});
	}

	private void showWeather() {
		// TODO Auto-generated method stub

		Cursor cursor = pureWeatherDB.loadWeatherInfo(lastCity);
		if(cursor.moveToNext()){
			//Toast.makeText(WeatherActivity.this, cityName, Toast.LENGTH_SHORT).show();
			updateInfo.setText(cursor.getString(cursor.getColumnIndex("city_name")));
			suggestion.setText(cursor.getString(cursor.getColumnIndex("suggestion")));
			
			String update = new StringBuilder().append("����ʱ�䣺 ")
					.append(cursor.getString(cursor.getColumnIndex("update_time")).replace(' ', '��')).toString();				
			updateTime.setText(update);
			
			pm25Value.setText(cursor.getString(cursor.getColumnIndex("pm25_value")));
			
			nowTemp.setText(cursor.getString(cursor.getColumnIndex("now_temp"))+" ��C");
			
			humiValue.setText(cursor.getString(cursor.getColumnIndex("humi_value"))+"%");
			
			String sunCond = new StringBuilder().append(cursor.getString(cursor.getColumnIndex("now_cond"))).toString();				
			nowCond.setText(sunCond);
			//forecastDate.setText(pref.getString("forecast_date", ""));
			rainyPos.setText(cursor.getString(cursor.getColumnIndex("rainy_pos"))+"%");
			
			String range = new StringBuilder().append(cursor.getString(cursor.getColumnIndex("max_temp"))).append("��C ~ ")
					.append(cursor.getString(cursor.getColumnIndex("min_temp"))).append("��C").toString();				
			tempRange.setText(range);
			
		}
		else{
			//��ȡ�������ݿ�����ݣ�������
		}
		if(cursor != null){
			cursor.close();
		}
		
		
		//closeMyDialog();//������ɣ��رնԻ���

	}

	private void createDownloadAddress(String cityName) {
		// TODO Auto-generated method stub
		String address = new StringBuilder().append("https://api.heweather.com/x3/weather?city=")
				.append(cityName).append("&key=37fa5d4ad1ea4d5da9f37e75732fb2e7").toString();
		//String address = new StringBuilder()
		//	.append("http://apis.baidu.com/heweather/pro/weather?city=").append(cityName).toString();	
		
		searchInternetForInfo(address);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case (R.id.weather_activity_switch_city):
			Intent intent = new Intent(this,MainActivity.class);
			startActivity(intent);
			//finish();			
			break;
		case (R.id.weather_title):
			refreshBy2Click();
			break;
		default:
			break;
		}

	}
	
	private static Boolean isRefresh = false;
	 
	private void refreshBy2Click() {
	 Timer tExit = null;
	 if (isRefresh == false) {
		 isRefresh = true; // ׼������
		 //Toast.makeText(this, "˫���������ƿ���ˢ������Ŷ~", Toast.LENGTH_SHORT).show();
		 tExit = new Timer();
		 tExit.schedule(new TimerTask() {
			 @Override
			 public void run() {
				 isRefresh = false; // ȡ���˳�
			 }
		 }, 2000); // ���2������û�а��¼�����������ʱ������־λ��λΪfalse���û�û��ѡ�����
	 
	 }
	 else{//2��֮���ٴε����ˢ�°�ť�������ˢ�²���		
			createDownloadAddress(lastCity);
				//searchInternetForInfo(address,"countyName");
			Toast.makeText(this, "��ˢ����������~", Toast.LENGTH_SHORT).show();//�������������Ϊ������δ����֪����ʾ��ˢ�³ɹ�������������ˢ������ʾ
	 }
	}
	
	/**
	 * �˵������ؼ���Ӧ
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	 // TODO Auto-generated method stub
		 if(keyCode == KeyEvent.KEYCODE_BACK){ 
		  exitBy2Click(); //����˫���˳�����
		}
		 return false;
	}
	/**
	 * ˫���˳�����
	 */
	private static Boolean isExit = false;
	 
	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			 isExit = true; // ׼���˳�
			 Toast.makeText(this, "�ٰ�һ�η��ؼ����˳�����", Toast.LENGTH_SHORT).show();
			 tExit = new Timer();
			 tExit.schedule(new TimerTask() {
			  @Override
			  public void run() {
			  isExit = false; // ȡ���˳�
			  }
			 }, 2000); // ���2������û�а��·��ؼ�����������ʱ��ȡ�����ղ�ִ�е�����
			 
		} 
		else {
		 finish();
		 System.exit(0);
		}
	}
	
	
}
