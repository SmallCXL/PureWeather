package com.pureweather.app.db;

import com.pureweather.app.utils.MyApplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class PureWeatherOpenHelper extends SQLiteOpenHelper {

	/*
	 * 省份的建表语句 
	 * 
	 */
	public static final String CREATE_PROVINCE = "create table Province (" +
			"id integer primary key autoincrement, " +
			"province_name text, " +
			"province_code text)";
	
	/*
	 * 城市的建表语句 
	 * 
	 */
	public static final String CREATE_CITY = "create table City (" +
			"id integer primary key autoincrement, " +
			"city_name text, " +
			"city_code text, " +
			"province_id integer)";	
	
	/*
	 * 省份的建表语句 
	 * 
	 */
	public static final String CREATE_COUNTY = "create table County (" +
			"id integer primary key autoincrement, " +
			"county_name text, " +
			"county_code text, " +
			"city_id integer)";	
	/*
	 * 天气信息的建表语句
	 */
	public static final String CREATE_WEATHER = "create table Weather (" +
			"city_name, " +
			"city_id text primary key, " +
			"update_time text, " +
			"aqi_value text, " +
			"pm25_value text, " +
			"now_cond text, " +
			"now_temp text, " +
			"sunset_time text, " +
			"sunrise_time text, " +
			"forecast_date text, " +
			"rainy_pos text, " +
			"max_temp text, " +
			"min_temp text, " +
			"status text, " +
			"image_code text, " +
			"suggestion text, " +
			"humi_value text)";
	
	public PureWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_PROVINCE);
		db.execSQL(CREATE_COUNTY);
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_WEATHER);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
