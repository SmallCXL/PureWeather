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
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ChooseAreaActivity extends Activity implements OnClickListener{
	
	public static final int PROVINCE = 0;
	public static final int CITY = 1;
	public static final int COUNTY = 2;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private Button backButton;
	
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
	private String lastCity;
	private int currentLevel = PROVINCE;	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area_activity);
		
		seletedProvince = null;
		seletedCity = null;
		
		backButton = (Button)findViewById(R.id.choose_activity_back);
		backButton.setOnClickListener(this);
		listView = (ListView)findViewById(R.id.list_view);
		adapter = new ArrayAdapter<String>(this, R.layout.choose_listview_item, dataList);

		listView.setAdapter(adapter);

		pureWeatherDB = PureWeatherDB.getInstance(this);
		
		titleText = (TextView)findViewById(R.id.choose_activity_title);
		
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
										
					lastCity = dataList.get(index);

					
					queryFromServer(lastCity, "last_city");
					//closeProgressDialog();
					//intent.putExtra("city_name", dataList.get(index));
					/*
					Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					startActivity(intent);
					finish();
					*/
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
			StringBuilder title = new StringBuilder().append("已选择： ").append("中国");
			titleText.setText(title.toString());
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
			StringBuilder title = new StringBuilder().append("已选择： ").append("中国 - ").append(seletedProvince.getProvinceName());
			titleText.setText(title.toString());
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
			StringBuilder title = new StringBuilder().append("已选择： ").append("中国 - ").append(seletedProvince.getProvinceName())
					.append(" - ").append(seletedCity.getCityName());			
			titleText.setText(title.toString());
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
		String address = "";
		if (type.equals("city") || type.equals("county")){
			address = new StringBuilder().append("http://www.weather.com.cn/data/list3/city").
					append(code).append(".xml").toString();
		}
		else if(type.equals("province")){
			address = new StringBuilder().append("http://www.weather.com.cn/data/list3/city.xml").toString();
		}
		else{
			//address = new StringBuilder().append("http://apis.baidu.com/heweather/pro/weather?city=")
			//		.append(code).toString();
			address = new StringBuilder().append("https://api.heweather.com/x3/weather?city=")
					.append(code).append("&key=37fa5d4ad1ea4d5da9f37e75732fb2e7").toString();
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
				else if(type.equals("last_city")){
					result = pureWeatherDB.saveWeather(response);
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
							else{
								SharedPreferences.Editor editor = PreferenceManager.
										getDefaultSharedPreferences(ChooseAreaActivity.this).edit();
								editor.putString("last_city", code);
								editor.commit();
								Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
								startActivity(intent);
								finish();
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
								"加载网络数据失败!请重试...", Toast.LENGTH_SHORT).show();
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
			listView.setSelection(dataList.indexOf(seletedCity.getCityName()));
		}
		else if(currentLevel == CITY){
			queryProvinces();			
			listView.setSelection(dataList.indexOf(seletedProvince.getProvinceName()));
		}
		else if(currentLevel == PROVINCE){
			Intent intent = new Intent(this,SearchCityActivity.class);
			startActivity(intent);
			finish();		
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case (R.id.choose_activity_back):
				Intent intent = new Intent(this,SearchCityActivity.class);
				startActivity(intent);
				finish();					
				break;
		}
	}	
}
