package com.pureweather.app.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {
/*
 * 发送一个http请求	
 */
	public static void sendHttpRequest(final String address, final HttpCallbackListener listener){
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					//connection.connect();
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while((line = reader.readLine()) != null){
						response.append(line);
					}
					//response 为返回的初始数据 待后续处理
					if(listener != null){
						listener.onFinish(response.toString());//回调监听器的onFinish方法处理结果						
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					listener.onError(e);
					e.printStackTrace();
				}finally{
					if(connection != null){
						connection.disconnect();
					}
				}
				
			}
			
			
		}).start();
	}
	
}
