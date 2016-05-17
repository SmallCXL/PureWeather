package com.pureweather.app.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.pureweather.app.model.City;
import com.pureweather.app.model.PureWeatherDB;
import com.pureweather.app.model.County;
import com.pureweather.app.model.Province;

/*
 * �������������ص�response���ݣ������������洢
 */
public class ResponseHandleUtils {
	/*
	 * 
	 */
	/*
	 * ���������ʹ�����������ص�ʡ�����ݣ������������ݿ���
	 */
		public synchronized static boolean handleProvincesResponse(
				PureWeatherDB pureWeatherDB, String response){
			if(!TextUtils.isEmpty(response)){
				String[] allProvinces = response.split(",");
				if(allProvinces != null && allProvinces.length > 0){
					for(String p : allProvinces){
						Province province = new Province();
						String[] array = p.split("\\|");
						province.setProvinceCode(array[0]);
						province.setProvinceName(array[1]);
						pureWeatherDB.saveProvince(province);
					}//end for
					return true;
				}//end if(allProvince != null && allProvince.length>0)
			}//end if(!TextUtils.isEmpty(response))
					return false;	
		}
		/*
		 * ���������ʹ�����������صĳ������ݣ������������ݿ���
		 */	
		public static boolean handleCitiesResponse(
				PureWeatherDB pureWeatherDB, String response, int provinceId){
			if(!TextUtils.isEmpty(response)){
				String[] allCities = response.split(",");
				if(allCities != null && allCities.length > 0){
					for(String c : allCities){
						String[] array = c.split("\\|");
						City city = new City();
						city.setCityCode(array[0]);
						city.setCityName(array[1]);
						city.setProviceId(provinceId);
						pureWeatherDB.saveCity(city);
					}//end for
					return true;
				}//end if(allCities != null && allCities.length>0)
			}//end if(!TextUtils.isEmpty(response))
					return false;
		}
		/*
		 * ���������ʹ�����������ص��ؼ����ݣ������������ݿ���
		 */	
		public static boolean handleCountiesResponse(
				PureWeatherDB pureWeatherDB, String response, int cityId){
			if(!TextUtils.isEmpty(response)){
				String[] allCounties = response.split(",");
				if(allCounties != null && allCounties.length > 0){
					for(String c : allCounties){
						County county = new County();
						String[] array = c.split("\\|");
						county.setCountyCode(array[0]);
						county.setCountyName(array[1]);
						county.setCityId(cityId);
						pureWeatherDB.saveCounty(county);
					}//end for
					return true;
				}
			}//end if(!TextUtils.isEmpty(response))
					return false;
		}
		/*
		 * �����ʹ�����������ص�JSON��ʽ���������ݣ������浽�ļ���
		 */
		public static void handleWeatherResponse(Context context, String response){
			try{
				JSONObject jsonObject = new JSONObject(response);
				JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
				String cityName = weatherInfo.getString("city");
				String weatherCode = weatherInfo.getString("cityid");
				String temp1 = weatherInfo.getString("temp1");
				String temp2 = weatherInfo.getString("temp2");
				String weatherDesp = weatherInfo.getString("weather");
				String publishTime = weatherInfo.getString("ptime");
				saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
				
				
				
				
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		/*
		 * �����������ص������������ݱ�����SharedPrefercens�ļ���
		 */
		private static void saveWeatherInfo(Context context, String cityName, String weatherCode,
				String temp1, String temp2, String weatherDesp, String publishTime) {
			// TODO Auto-generated method stub
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��",Locale.CHINA);
			
			//Ϊ����Ҫһ��context��������
			SharedPreferences.Editor editor = PreferenceManager.
					getDefaultSharedPreferences(context).edit();
			
			editor.putBoolean("city_seleted", true);
			editor.putString("city_name", cityName);
			editor.putString("weather_code", weatherCode);
			editor.putString("temp1", temp1);
			editor.putString("temp2", temp2);
			editor.putString("weather_desp", weatherDesp);
			editor.putString("publish_time", publishTime);
			editor.putString("current_date", sdf.format(new Date()));
			
			editor.commit();	
		}	
		
		
	
}
