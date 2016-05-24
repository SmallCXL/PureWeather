package com.pureweather.app.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.pureweather.app.db.PureWeatherOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
/*
 * �������������������ݿ�Ĵ洢���ȡ
 */
public class PureWeatherDB {

	
	private Context mContext;
	

	/*
	 * ���ݿ��� 
	 */
	public static final String DB_NAME = "PureWeather.db";
	/*
	 * ���ݿ�汾�� 
	 */
	public static final int VERSION = 1;
	/*
	 * ˽�б���
	 */
	private static PureWeatherDB pureWeatherDB;
	private SQLiteDatabase db;
	
	/*
	 * ���췽��˽�л���ȷ��ֻ��һ��CoolWeatherDB��ʵ��
	 */
	private PureWeatherDB(Context context){
		PureWeatherOpenHelper dbHelper = new PureWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
		mContext = context;
	}
	
	/*
	 * ��ȡCoolWeatherDB��ʵ��
	 */
	public synchronized static PureWeatherDB getInstance(Context context){
		if(pureWeatherDB == null){
			pureWeatherDB = new PureWeatherDB(context);			
		}
		return pureWeatherDB;
	}
	/*
	 * �����ݿ��ж�ȡȫ�����е�ʡ����Ϣ
	 */
	public List<Province> loadProvinces(){
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));//ȡ����Ϊ"id"��һ�е�����
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			}while(cursor.moveToNext());
		}
		if(cursor != null){
			cursor.close();			
		}
		return list;	
	}
	/*
	 * ��Provinceʵ���洢�����ݿ�
	 */
	public void saveProvince(Province province){
		if (province!=null){
			ContentValues value = new ContentValues();
			value.put("province_name", province.getProvinceName());
			value.put("province_code", province.getProvinceCode());
			db.insert("Province", null, value);
		}
	}
	/*
	 * �����ݿ��ж�ȡ��Ӧʡ�ݵĳ�����Ϣ
	 */
	public List<City> loadCities(int provinceId){
		List<City> list = new ArrayList<City>();
		Cursor cursor = 
				db.query("City", null, "province_id = ?", new String[]{String.valueOf(provinceId)}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				City city = new City();
				city.setProviceId(provinceId);
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				list.add(city);
			}while(cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		return list;
	}
	/*
	 * ��Cityʵ���洢�����ݿ���
	 */
	public void saveCity(City city){
		if(city != null){
			ContentValues value = new ContentValues();
			value.put("city_name", city.getCityName());
			value.put("city_code", city.getCityCode());
			value.put("province_id", city.getProvinceId());
			db.insert("City", null, value);
		}
	}
	/*
	 * �����ݿ��ж�ȡ��Ӧ���е�����Ϣ
	 */
	public List<County> loadCounties(int cityId){
		List<County> list = new ArrayList<County>();
		Cursor cursor =
			db.query("County", null, "city_id = ?", new String[]{String.valueOf(cityId)}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				County county = new County();
				county.setCityId(cityId);
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				list.add(county);
			}while(cursor.moveToNext());
		}
		if(cursor != null){
			cursor.close();
		}
		return list;
	}
	/*
	 * ��Countyʵ���洢�����ݿ���
	 */
	public void saveCounty(County county){
		if(county != null){
			ContentValues value = new ContentValues();
			value.put("city_id", county.getCityId());
			value.put("county_name",county.getCountyName());
			value.put("county_code", county.getCountyCode());
			db.insert("County", null, value);
		}
	}
	
	/*
	 * �� SharedPreferenceWeather ��������Ϣ�������ݿ�
	 */
	public boolean saveWeather(String response){
		

		ContentValues value = new ContentValues();
		
		boolean isLegal = false;
		
		//���ȴ�����״̬  status �ֶ�
		try{
			JSONObject jsonObject = new JSONObject(response);
			JSONArray HeWeatherInfo = jsonObject.getJSONArray("HeWeather data service 3.0");
			String status = ((JSONObject)HeWeatherInfo.get(0)).getString("status");
			isLegal = status.equals("ok");

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
					 aqiValue = "����Ϣ";
					 pm25Value = "����Ϣ";	
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
				String imageCode = nowCondInfo.getString("code");
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
				//����status
				String status = ((JSONObject)HeWeatherInfo.get(0)).getString("status");
				//����suggestion�ֶ�
				JSONObject suggestionInfo = ((JSONObject)HeWeatherInfo.get(0)).getJSONObject("suggestion");
				JSONObject comfInfo = suggestionInfo.getJSONObject("comf");
				String suggestion = new StringBuilder().append("         ")
						.append(comfInfo.getString("brf")).append("��").append(comfInfo.getString("txt")).toString();
				//SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��",Locale.CHINA);
				//
				value.put("city_name", cityName);
				value.put("city_id", cityId);
				value.put("update_time", updateTime);
				value.put("aqi_value", aqiValue);
				value.put("pm25_value", pm25Value);
				value.put("now_cond", nowCond);
				value.put("now_temp", nowTemp);
				value.put("sunset_time", sunsetTime);
				value.put("sunrise_time", sunriseTime);
				value.put("forecast_date", forecastDate);
				value.put("rainy_pos", rainyPos);
				value.put("max_temp", maxTemp);
				value.put("min_temp", minTemp);
				value.put("status", status);
				value.put("image_code", imageCode);
				value.put("suggestion", suggestion);
				value.put("humi_value", humiValue);
				
				db.replace("Weather", null, value);
				
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
	 * �����ݿ��ж�ȡ�ѱ����������Ϣ
	 */
	public List<Weather> loadWeatherInfo(){
		List<Weather> infoList = null;
		Cursor cursor = null;
		cursor = db.query("Weather", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			infoList = new ArrayList<Weather>();
			do{
				Weather info = new Weather();
				
				info.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				
				StringBuilder condText = new StringBuilder().append(cursor.getString(cursor.getColumnIndex("now_cond")))
						.append("  ").append(cursor.getString(cursor.getColumnIndex("now_temp"))).append("��C");
				info.setCondText(condText.toString());
				
				StringBuilder tempRange = new StringBuilder().append(cursor.getString(cursor.getColumnIndex("max_temp")))
						.append("��C ~ ").append(cursor.getString(cursor.getColumnIndex("min_temp")))
						.append("��C");
				info.setTempRange(tempRange.toString());
				
				
				info.setImageCode(cursor.getString(cursor.getColumnIndex("image_code")));
				
				infoList.add(info);
			}while(cursor.moveToNext());
		}
		if(cursor != null){
			cursor.close();
		}	

		return infoList;
	}

	public Cursor loadWeatherInfo(String cityName){
		Cursor cursor = null;
		cursor = db.query("Weather", null, "city_name = ?", new String[]{cityName}, null, null, null);
		return cursor;
		
	}
	
	public int deleteWeatherInfo(String cityName){
		
		return db.delete("Weather", "city_name = ?", new String[]{cityName});
		
	}	
	
}
