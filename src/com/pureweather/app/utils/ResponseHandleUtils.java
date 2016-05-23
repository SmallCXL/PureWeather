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
 * 此类用来处理返回的response数据，包括解析、存储
 */
public class ResponseHandleUtils {
	/*
	 * 
	 */
	/*
	 * 用来解析和处理服务器返回的省级数据，并保存至数据库中
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
		 * 用来解析和处理服务器返回的城市数据，并保存至数据库中
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
		 * 用来解析和处理服务器返回的县级数据，并保存至数据库中
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
		 * 解析和处理服务器返回的JSON格式的天气数据，并保存到文件中
		 * 这里只取出城市名称，保存至 pref 
		 */
		public static boolean handleWeatherResponse(Context context, String response){
			
			boolean isLegal = false;
			SharedPreferences.Editor editor = PreferenceManager.
					getDefaultSharedPreferences(context).edit();
			//首先处理返回状态  status 字段
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
					
					// 处理basic字段
					JSONObject basicInfo = ((JSONObject) HeWeatherInfo.get(0)).getJSONObject("basic");
					String cityName = basicInfo.getString("city");
					
					editor.putString("city_name", cityName);

					editor.commit();
					return true;
				}
				catch(Exception e){
					//Toast.makeText(MyApplication.getContext(), "数据解析出错！", Toast.LENGTH_SHORT).show();
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
			//首先处理返回状态  status 字段
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
					//处理aqi字段，因为部分城市没有这个数值，直接解析会导致出错，故需要分开解析
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
						 aqiValue = "无相关信息";
						 pm25Value = "无相关信息";	
					}
					
					// 处理basic字段
					JSONObject basicInfo = ((JSONObject) HeWeatherInfo.get(0)).getJSONObject("basic");
					String cityName = basicInfo.getString("city");
					String cityId = basicInfo.getString("id");
					JSONObject updateInfo = (JSONObject)basicInfo.getJSONObject("update");
					String updateTime = updateInfo.getString("loc");
					

					//处理now字段
					JSONObject nowInfo = ((JSONObject) HeWeatherInfo.get(0)).getJSONObject("now");
					JSONObject nowCondInfo = nowInfo.getJSONObject("cond");
					String nowCond = nowCondInfo.getString("txt");
					String nowCode = nowCondInfo.getString("code");
					String nowTemp =  nowInfo.getString("tmp");
					String humiValue = nowInfo.getString("hum");
					
					//处理daily_forecast字段
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

					//处理suggestion字段
					JSONObject suggestionInfo = ((JSONObject)HeWeatherInfo.get(0)).getJSONObject("suggestion");
					JSONObject comfInfo = suggestionInfo.getJSONObject("comf");
					String suggestion = new StringBuilder().append("         ")
							.append(comfInfo.getString("brf")).append("。").append(comfInfo.getString("txt")).toString();
					//SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
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
					//Toast.makeText(MyApplication.getContext(), "数据解析出错！", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
					return false;
				}				
			}
			return false;
		}
*/
		
	
}
