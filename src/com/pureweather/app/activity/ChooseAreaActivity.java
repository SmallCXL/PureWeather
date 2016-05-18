package com.pureweather.app.activity;

import java.util.ArrayList;
import java.util.List;

//import com.pureweather.app.activity.WeatherActivity;
import com.pureweather.app.utils.HttpCallbackListener;
import com.pureweather.app.utils.HttpUtils;
import com.pureweather.app.utils.ResponseHandleUtils;
import com.pureweather.app.activity.ChooseAreaActivity;
//import com.pureweather.app.activity.WeatherActivity;
import com.pureweather.app.model.PureWeatherDB;
import com.pureweather.app.R;
import com.pureweather.app.model.City;
import com.pureweather.app.model.PureWeatherDB;
import com.pureweather.app.model.County;
import com.pureweather.app.model.Province;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ChooseAreaActivity extends Activity {
	
	public static final int PROVINCE = 0;
	public static final int CITY = 1;
	public static final int COUNTY = 2;
	
	private boolean isFromWeatherActivity;
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private PureWeatherDB pureWeatherDB;
	/*
	 * 数据列表
	 */
	private List<String> dataList = new ArrayList<String>();
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	/*
	 * 选中的内容
	 */
	private Province seletedProvince;
	private City seletedCity;
	
	private int currentLevel = PROVINCE;	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area_activity);
		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ChooseAreaActivity.this);
		if((pref.getBoolean("city_seleted", false)) && !isFromWeatherActivity){
			Intent intent = new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
			return ;
		}
		
		listView = (ListView)findViewById(R.id.list_view);
		titleText = (TextView)findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		pureWeatherDB = PureWeatherDB.getInstance(this);
		
		queryProvinces();
		
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				// TODO Auto-generated method stub
				if(currentLevel == PROVINCE){
					seletedProvince = provinceList.get(index);
					queryCities();
				}
				else if(currentLevel == CITY){
					seletedCity = cityList.get(index);
					queryCounties();					
				}
				
				else if(currentLevel == COUNTY){
					Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					String countyCode = countyList.get(index).getCountyCode();
					intent.putExtra("county_code", countyCode);
					
					//Toast.makeText(ChooseAreaActivity.this, countyCode, Toast.LENGTH_SHORT).show();
					startActivity(intent);
					finish();
				}
				
			}
		});		
	}

	private void queryProvinces() {
		// TODO Auto-generated method stub
		provinceList = pureWeatherDB.loadProvinces();
		if(provinceList.size() > 0){
			dataList.clear();
			for(Province p : provinceList){
				dataList.add(p.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = PROVINCE;
		}//end if
		else{
			queryFromServer(null, "province");
		}
	}
	
	private void queryCities() {
		// TODO Auto-generated method stub		
		cityList = pureWeatherDB.loadCities(seletedProvince.getId());
		if(cityList.size() > 0){
			dataList.clear();
			for(City c : cityList){
				dataList.add(c.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);			
			titleText.setText(seletedProvince.getProvinceName());
			currentLevel = CITY;			
		}
		else{
			queryFromServer(seletedProvince.getProvinceCode(), "city");
		}
	}
	
	private void queryCounties() {
		// TODO Auto-generated method stub
		countyList = pureWeatherDB.loadCounties(seletedCity.getId());
		if(countyList.size() > 0){
			dataList.clear();
			for(County c : countyList){
				dataList.add(c.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);			
			titleText.setText(seletedCity.getCityName());
			currentLevel = COUNTY;			
		}
		else{
			queryFromServer(seletedCity.getCityCode(), "county");
		}		
	}
/*
 *  在数据库中找不到相关数据或者第一次加载，则调用queryFromServer方法从网络上下载城市数据
 */
	private void queryFromServer(final String code, final String type) {
		// TODO Auto-generated method stub
		String address;
		if (!TextUtils.isEmpty(code)){
			address = new StringBuilder().append("http://www.weather.com.cn/data/list3/city").
					append(code).append(".xml").toString();
		}
		else{
			address = new StringBuilder().append("http://www.weather.com.cn/data/list3/city.xml").toString();
		}
		showProgressDialog();
		HttpUtils.sendHttpRequest(address, new HttpCallbackListener(){

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result = false;
				if(type.equals("province")){
					result = ResponseHandleUtils.handleProvincesResponse(pureWeatherDB, response);
				}
				else if(type.equals("city")){
					result = ResponseHandleUtils.handleCitiesResponse(pureWeatherDB, response, seletedProvince.getId());				
				}
				else if(type.equals("county")){
					result = ResponseHandleUtils.handleCountiesResponse(pureWeatherDB, response, seletedCity.getId());				
				}
				//以上已经下载完毕，以下重新调用查询数据库的方法
				if(result){
					//queryProvince一类的方法中涉及到UI操作，而onFinish方法是在子线程中执行的。
					//因此，需要在调用queryProvince之前，将代码放入UI线程中执行
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(type.equals("province")){
								queryProvinces();
							}
							else if(type.equals("city")){
								queryCities();
							}
							else if(type.equals("county")){
								queryCounties();
							}
							closeProgressDialog();//下载完成，关闭提示框
						}//end run
					});//end runOnUiThread
				}//end if(result)
				//closeProgressDialog();
			}//end onFinish

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				//进入子线程中进行UI操作
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this,
								"加载网络数据失败!", Toast.LENGTH_SHORT).show();
					}//end run
				});//end runOnUiThread
			}//end onError
			
		});//end HttpUtil.sendHttpRequest
		
	}
/*
 * 显示下载进度对话框
 */
	private void showProgressDialog() {
		// TODO Auto-generated method stub
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在下载中...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
/*
 * 关闭进度对话框	
 */
	private void closeProgressDialog() {
		// TODO Auto-generated method stub
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}

/*
 * 重载返回事件响应，判断此时返回的界面	
 */
	@Override
	public void onBackPressed(){
		if(currentLevel == COUNTY){
			queryCities();
		}
		else if(currentLevel == CITY){
			queryProvinces();
		}
		else if(currentLevel == PROVINCE){
			
			if(isFromWeatherActivity){
				Intent intent = new Intent(this,WeatherActivity.class);
				startActivity(intent);
			}
			
			finish();		
		}
	}	
}
