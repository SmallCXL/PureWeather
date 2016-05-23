package com.pureweather.app.activity;

import java.util.ArrayList;
import java.util.List;


import com.pureweather.app.R;
import com.pureweather.app.model.PureWeatherDB;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SearchCityActivity extends Activity implements OnClickListener{

	
	private EditText inputName;
	private Button searchCity;
	private Button backButton;
	private Button seleteFromList;
	private List<String> dataList = new ArrayList<String>();
	private ListView searchResult;
	private ArrayAdapter<String> adapter;
	//private Button backButton;
	private String seletedCity;
	private PureWeatherDB pureWeatherDB;
	private static final String SEARCH_FAIL = "�Ҳ����ó��У�����������...";
	private String lastCity;
	
	private String httpResponse;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_city_activity);

		inputName = (EditText)findViewById(R.id.input_city_name);
		searchCity = (Button) findViewById(R.id.search_city);
		backButton = (Button) findViewById(R.id.search_activity_back);
		seleteFromList = (Button) findViewById(R.id.search_from_list);
		searchResult = (ListView) findViewById(R.id.search_result);
		adapter = new ArrayAdapter<String>(this, R.layout.search_listview_item, dataList);
		searchResult.setAdapter(adapter);
		
		
		pureWeatherDB = PureWeatherDB.getInstance(this);
		
		searchCity.setOnClickListener(this);
		backButton.setOnClickListener(this);
		seleteFromList.setOnClickListener(this);
		
		SharedPreferences pref = PreferenceManager.
				getDefaultSharedPreferences(SearchCityActivity.this);	
		lastCity = pref.getString("last_city", "");
		
		searchResult.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				// TODO Auto-generated method stub
				seletedCity = dataList.get(index);
				if((!seletedCity.equals(SEARCH_FAIL) && (!TextUtils.isEmpty(seletedCity)))){
					//ͬʱ����������Ϣ���浽���ݿ��У��Թ�main������ʾ
					pureWeatherDB.saveWeather( httpResponse);
					//�÷�������һ��boolean������ȷ���Ƿ񱣴�ɹ���Ŀǰδʹ��

					SharedPreferences.Editor editor = PreferenceManager.
							getDefaultSharedPreferences(SearchCityActivity.this).edit();
					editor.putString("last_city", seletedCity);
					editor.commit();
					
					Intent intent = new Intent(SearchCityActivity.this,WeatherActivity.class);
					startActivity(intent);
					finish();					
				}
			}
			
		});
		
		
	}

	@Override
	public void onResume(){
		super.onResume();
		SharedPreferences pref = PreferenceManager.
				getDefaultSharedPreferences(this);
		lastCity = pref.getString("last_city", "");	

	} 	
	
	
	public void searchForCity(String cityName){
		String address = new StringBuilder().append("http://apis.baidu.com/heweather/pro/weather?city=")
				.append(cityName).toString();

		HttpUtils.sendHttpRequest(address, new HttpCallbackListener(){

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				if(!TextUtils.isEmpty(response)){
					//���ؽ����Ϊ�գ���������Ϣ
					if(ResponseHandleUtils.handleWeatherResponse(SearchCityActivity.this, response)){
						//���ҵĳ�������ȷʵ���ڣ������ListView
						httpResponse = response;
						runOnUiThread(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								SharedPreferences pref = PreferenceManager.
										getDefaultSharedPreferences(SearchCityActivity.this);
								//Toast.makeText(SearchCityActivity.this, pref.getString("city_name", ""), Toast.LENGTH_SHORT).show();
									dataList.clear();
									dataList.add(pref.getString("city_name", ""));
									adapter.notifyDataSetChanged();								
							}
						});						
					}
					else{
						//��ѯ�Ľ�����Ϸ�
						dataList.clear();
						dataList.add(SEARCH_FAIL);
						adapter.notifyDataSetChanged();								
					}
				}
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				//��ѯ��ʱ����
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(SearchCityActivity.this, "��������ʱ�������²�ѯ...", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case (R.id.search_city):
			String input = inputName.getText().toString();
			if(!TextUtils.isEmpty(input)){
				searchForCity(input);
				inputName.setText("");
			}
			
			break;
		case (R.id.search_from_list):
			Intent intent = new Intent(SearchCityActivity.this,ChooseAreaActivity.class);
			startActivity(intent);
			//finish();
			break;
		
		case (R.id.search_activity_back):
			intent = new Intent(this,MainActivity.class);
			startActivity(intent);
			finish();			
			break;
		}
	}
	@Override
	public void onBackPressed(){
		if(TextUtils.isEmpty(lastCity)){
			finish();
			System.exit(0);
		}
		Intent intent = new Intent(this,MainActivity.class);
		startActivity(intent);
		finish();
	}
}
