package com.pureweather.app.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.pureweather.app.R;
import com.pureweather.app.model.PureWeatherDB;
import com.pureweather.app.model.WeatherAdapter;
import com.pureweather.app.model.Weather;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{
	
	private Button addCity;
	private Button backButton;
	private ListView cityListView;
	private WeatherAdapter adapter;
	private List<Weather> cityList;;
	private List<Weather> dataList = new ArrayList<Weather>();
	//private String seletedCity;
	private String lastCity;
	private int seletedCity;
	
	private PureWeatherDB pureWeatherDB;
	
	
	@Override 
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_activity);
		//dataInit();
		addCity = (Button)findViewById(R.id.add_city);
		backButton = (Button)findViewById(R.id.main_activity_back);
		cityListView = (ListView) findViewById(R.id.main_city_listview);
		pureWeatherDB = PureWeatherDB.getInstance(this);
		
		adapter = new WeatherAdapter(MainActivity.this, R.layout.main_city_listview_item, dataList);
		cityListView.setAdapter(adapter);
	
		addCity.setOnClickListener(this);
		backButton.setOnClickListener(this);

		SharedPreferences pref = PreferenceManager.
				getDefaultSharedPreferences(MainActivity.this);	
		lastCity = pref.getString("last_city", "");
		
		refreshListView();
		
		if(TextUtils.isEmpty(lastCity)){
			Intent intent = new Intent(MainActivity.this,SearchCityActivity.class);
			startActivity(intent);
			finish();			
		}
		

		cityListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				// TODO Auto-generated method stub
				//showDialog();
				SharedPreferences.Editor editor = PreferenceManager.
						getDefaultSharedPreferences(MainActivity.this).edit();	
				
				lastCity = cityList.get(index).getCityName();
				editor.putString("last_city", lastCity);
				editor.commit();
				//boolean loadSuccess = pureWeatherDB.loadWeatherInfoToPref(seletedCity);
				
				Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
				startActivity(intent);
				finish();					
			}
			
		});
		ItemOnLongClick1();
	}

	@Override
	public void onResume(){
		super.onResume();

		SharedPreferences pref = PreferenceManager.
				getDefaultSharedPreferences(MainActivity.this);	
		lastCity = pref.getString("last_city", "");
		
		refreshListView();
			
	} 	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case (R.id.add_city):
			Intent intent = new Intent(MainActivity.this,SearchCityActivity.class);
			startActivity(intent);
			//finish();
			break;
		case (R.id.main_activity_back):
			intent = new Intent(MainActivity.this,WeatherActivity.class);
			startActivity(intent);
			finish();			
		}
	}
	@Override
	public void onBackPressed(){
		Intent intent = new Intent(this,WeatherActivity.class);
		startActivity(intent);
		finish();
	}	

	public void deleteCity(String cityName){
		
		SharedPreferences.Editor editor = PreferenceManager.
				getDefaultSharedPreferences(MainActivity.this).edit();	
		
		if(cityList.size() > 1){
			if(lastCity.equals(cityName)){
				//����ɾ����ǰ��ʾ�ĳ���
				if(lastCity.equals(cityList.get(0).getCityName())){
					//��ǰ��ʾ�ĳ���λ���б���
					lastCity = cityList.get(1).getCityName();
				}
				else{
					lastCity = cityList.get(0).getCityName();
				}
			} 
		}
		else{
			// ����ɾ�����һ��Ԫ��
			lastCity = "";
		}
		editor.putString("last_city", lastCity);
		editor.commit();
		
		pureWeatherDB.deleteWeatherInfo(cityName);
		//�ж����ݿ��Ƿ������ݣ�����У����ȡ��cityList���У�����ʾ��ListView���� 
		refreshListView();

	}

	private void refreshListView(){

		//�ж����ݿ��Ƿ������ݣ�����У����ȡ��cityList���У�����ʾ��ListView���� 
		cityList = pureWeatherDB.loadWeatherInfo();
		if(cityList.size() > 0){
			dataList.clear();
			for(Weather w : cityList){
				dataList.add(w);
			}
			adapter.notifyDataSetChanged();				
		}		
	}
	
	private void ItemOnLongClick1() { 
	//ע��setOnCreateContextMenuListener��������onContextItemSelected����ʹ�õ� 
		cityListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() { 
	
	        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) { 
	            menu.add(0, 0, 0, "ɾ��"); 
	                                       // menu.add(0, 1, 0, "�ղ�"); 
	                                       // menu.add(0, 2, 0, "�Ա�"); 
	        } 
	   }); 
	} 

// �����˵���Ӧ���� 
	public boolean onContextItemSelected(MenuItem item) { 
	        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo(); 
	        seletedCity = (int) info.id;// �����info.id��Ӧ�ľ������ݿ���_id��ֵ 
	
	        switch (item.getItemId()) { 
	        case 0: 
	        	deleteCity(cityList.get(seletedCity).getCityName());
	                // ��Ӳ��� 
	               // Toast.makeText(ListOnLongClickActivity.this, "���", Toast.LENGTH_SHORT).show(); 
	                break; 
	
	        default: 
	                break; 
	        } 
	        return super.onContextItemSelected(item); 
	}
}