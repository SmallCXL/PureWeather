package com.pureweather.app.model;

public class WeatherInfo {

//最外层的Object为"HeWeather data service 3.0"，以下的外层对象都在该对象下的数组中
//在basic对象中，基本信息
	String cityName;
	String cityId;
	// 在update对象中
		String updateTime;	//更新时间，精确到分
	
//在aqi对象中，空气质量指数
	String aqiValue;//空气质量指数
	String pm25;	
	
//在daily_forecastd对象中，当日预测数据，注意：dailyforecast是一个对象数组，包含了几天的预测数据
	//在astro对象中
		String sunsetTime;
		String sunriseTime;
	//在cond对象中
		String dailyCond;//需要将原始数据中的白天描述和夜晚描述拼接成：“晴转多云”的格式
	String forecastDate;//预测日期
	String dailyHumidity;//湿度
	String pop;//降水概率
	//在tmp对象中
		String maxTemp;
		String minTemp;
	String windLevel;
//在now对象中，当前的测量数据
	String nowCond;
	String nowHimidity;
	String nowTemp;
//在status对象中，访问的状态
	String status;
		
	public String getCityId(){
		return cityId;
	}
	public void setCityId(String cityId){
		this.cityId = cityId;	
	}	
	
}
