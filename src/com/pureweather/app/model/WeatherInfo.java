package com.pureweather.app.model;

public class WeatherInfo {

//������ObjectΪ"HeWeather data service 3.0"�����µ��������ڸö����µ�������
//��basic�����У�������Ϣ
	String cityName;
	String cityId;
	// ��update������
		String updateTime;	//����ʱ�䣬��ȷ����
	
//��aqi�����У���������ָ��
	String aqiValue;//��������ָ��
	String pm25;	
	
//��daily_forecastd�����У�����Ԥ�����ݣ�ע�⣺dailyforecast��һ���������飬�����˼����Ԥ������
	//��astro������
		String sunsetTime;
		String sunriseTime;
	//��cond������
		String dailyCond;//��Ҫ��ԭʼ�����еİ���������ҹ������ƴ�ӳɣ�����ת���ơ��ĸ�ʽ
	String forecastDate;//Ԥ������
	String dailyHumidity;//ʪ��
	String pop;//��ˮ����
	//��tmp������
		String maxTemp;
		String minTemp;
	String windLevel;
//��now�����У���ǰ�Ĳ�������
	String nowCond;
	String nowHimidity;
	String nowTemp;
//��status�����У����ʵ�״̬
	String status;
		
	public String getCityId(){
		return cityId;
	}
	public void setCityId(String cityId){
		this.cityId = cityId;	
	}	
	
}
