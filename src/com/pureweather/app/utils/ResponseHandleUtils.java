package com.pureweather.app.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

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
		 * ����ֻȡ���������ƣ������� pref 
		 */
		public static boolean handleWeatherResponse(Context context, String response){
			
			boolean isLegal = false;
			SharedPreferences.Editor editor = PreferenceManager.
					getDefaultSharedPreferences(context).edit();
			//���ȴ�����״̬  status �ֶ�
			try{
				JSONObject jsonObject = new JSONObject(response);
				JSONArray HeWeatherInfo = jsonObject.getJSONArray("HeWeather data service 3.0");
				String status = ((JSONObject)HeWeatherInfo.get(0)).getString("status");
				isLegal = status.equals("ok");

				//editor.putString("status", status);
				//seditor.commit();
			}
			catch(Exception e){
				e.printStackTrace();
				return false;
			}

			if(isLegal){
				try{
					JSONObject jsonObject = new JSONObject(response);
					JSONArray HeWeatherInfo = jsonObject.getJSONArray("HeWeather data service 3.0");
					
					// ����basic�ֶ�
					JSONObject basicInfo = ((JSONObject) HeWeatherInfo.get(0)).getJSONObject("basic");
					String cityName = basicInfo.getString("city");
					
					editor.putString("city_name", cityName);

					editor.commit();
					return true;
				}
				catch(Exception e){
					//Toast.makeText(MyApplication.getContext(), "���ݽ�������", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
					return false;
				}				
			}
			return false;
		}

		
/*		
		public static boolean handleWeatherResponse(Context context, String response){
			boolean isLegal = false;
			SharedPreferences.Editor editor = PreferenceManager.
					getDefaultSharedPreferences(context).edit();
			//���ȴ�����״̬  status �ֶ�
			try{
				JSONObject jsonObject = new JSONObject(response);
				JSONArray HeWeatherInfo = jsonObject.getJSONArray("HeWeather data service 3.0");
				String status = ((JSONObject)HeWeatherInfo.get(0)).getString("status");
				isLegal = status.equals("ok");

				editor.putString("status", status);
				editor.commit();
			}
			catch(Exception e){
				e.printStackTrace();
				return false;
			}

			if(isLegal){
				try{
					JSONObject jsonObject = new JSONObject(response);
					JSONArray HeWeatherInfo = jsonObject.getJSONArray("HeWeather data service 3.0");
					//����aqi�ֶΣ���Ϊ���ֳ���û�������ֵ��ֱ�ӽ����ᵼ�³�������Ҫ�ֿ�����
					String aqiValue;
					String pm25Value;
					try{
					
					JSONObject aqiInfo = ((JSONObject) HeWeatherInfo.get(0)).getJSONObject("aqi");
					JSONObject cityAqiInfo = aqiInfo.getJSONObject("city");
					 aqiValue = cityAqiInfo.getString("aqi");
					 pm25Value = cityAqiInfo.getString("pm25");	
					}
					catch(Exception e){
						e.printStackTrace();
						 aqiValue = "�������Ϣ";
						 pm25Value = "�������Ϣ";	
					}
					
					// ����basic�ֶ�
					JSONObject basicInfo = ((JSONObject) HeWeatherInfo.get(0)).getJSONObject("basic");
					String cityName = basicInfo.getString("city");
					String cityId = basicInfo.getString("id");
					JSONObject updateInfo = (JSONObject)basicInfo.getJSONObject("update");
					String updateTime = updateInfo.getString("loc");
					

					//����now�ֶ�
					JSONObject nowInfo = ((JSONObject) HeWeatherInfo.get(0)).getJSONObject("now");
					JSONObject nowCondInfo = nowInfo.getJSONObject("cond");
					String nowCond = nowCondInfo.getString("txt");
					String nowCode = nowCondInfo.getString("code");
					String nowTemp =  nowInfo.getString("tmp");
					String humiValue = nowInfo.getString("hum");
					
					//����daily_forecast�ֶ�
					JSONArray dailyForecastInfo = ((JSONObject)HeWeatherInfo.get(0)).getJSONArray("daily_forecast");
					JSONObject todayInfo = (JSONObject)dailyForecastInfo.get(0);
					JSONObject astroInfo = todayInfo.getJSONObject("astro");
					String sunsetTime = astroInfo.getString("ss");
					String sunriseTime = astroInfo.getString("sr");
					String forecastDate = todayInfo.getString("date");
					String rainyPos = todayInfo.getString("pop");
					JSONObject tempInfo = todayInfo.getJSONObject("tmp");
					String maxTemp = tempInfo.getString("max");
					String minTemp = tempInfo.getString("min");

					//����suggestion�ֶ�
					JSONObject suggestionInfo = ((JSONObject)HeWeatherInfo.get(0)).getJSONObject("suggestion");
					JSONObject comfInfo = suggestionInfo.getJSONObject("comf");
					String suggestion = new StringBuilder().append("         ")
							.append(comfInfo.getString("brf")).append("��").append(comfInfo.getString("txt")).toString();
					//SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��",Locale.CHINA);
					//
					
					editor.putBoolean("city_seleted", true);
					editor.putString("city_name", cityName);
					editor.putString("city_id", cityId);
					editor.putString("update_time", updateTime.split(" ")[1]);
					editor.putString("aqi_value", aqiValue);
					editor.putString("pm25_value", pm25Value);
					editor.putString("now_cond", nowCond);
					editor.putString("now_temp", nowTemp);
					editor.putString("sunset_time", sunsetTime);
					editor.putString("sunrise_time", sunriseTime);
					editor.putString("forecast_date", forecastDate);
					editor.putString("rainy_pos", rainyPos);
					editor.putString("max_temp", maxTemp);
					editor.putString("min_temp", minTemp);
					editor.putString("image_code", nowCode);
					editor.putString("suggestion", suggestion);
					editor.putString("humi_value", humiValue);
					editor.commit();
					return true;
				}
				catch(Exception e){
					//Toast.makeText(MyApplication.getContext(), "���ݽ�������", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
					return false;
				}				
			}
			return false;
		}
*/
		
	
}
