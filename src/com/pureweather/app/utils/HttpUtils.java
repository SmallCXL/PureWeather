package com.pureweather.app.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {
/*
 * ����һ��http����	
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
					connection.setConnectTimeout(5000);
					connection.setReadTimeout(5000);
					connection.setRequestProperty("apikey",  "3cc16767d699197caf22d11f13c5729b");
					//connection.connect();
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
					StringBuilder response = new StringBuilder();
					String line;
					while((line = reader.readLine()) != null){
						response.append(line);
					}
					//response Ϊ���صĳ�ʼ���� ����������
					if(listener != null){
						listener.onFinish(response.toString());//�ص���������onFinish����������						
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
