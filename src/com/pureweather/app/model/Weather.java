package com.pureweather.app.model;

public class Weather {

	private String imageCode;
	private String cityName;
	private String condText;
	private String tempRange;
	
	public String getImageCode(){
		return this.imageCode;
	}
	public String getCityName(){
		return this.cityName;
	}
	public String getCondText(){
		return this.condText;
	}
	public String getTempRange(){
		return this.tempRange;
	}
	
	public void setImageCode(String imageCode){
		this.imageCode = imageCode;
	}
	public void setCityName(String cityName){
		this.cityName = cityName;
	}
	public void setCondText(String condText){
		this.condText = condText;
	}
	public void setTempRange(String tempRange){
		this.tempRange = tempRange;
	}
}
