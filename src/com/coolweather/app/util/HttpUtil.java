package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil {
	public static void sendHttpRequest(final String address,
			final HttpCallbackListener listener) {
		System.out.println("sendHttpRequest");
		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpURLConnection con = null;
				URL url;
				try {
					url = new URL(address);
					con = (HttpURLConnection) url.openConnection();
					con.setRequestMethod("GET");
					con.setConnectTimeout(8000);
					con.setReadTimeout(8000);
					InputStream in = con.getInputStream();
					BufferedReader read = new BufferedReader(
							new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while (null != (line = read.readLine())) {
						System.out.println(line);
						response.append(line);
					}
					System.out.println(response.toString());
					if (null != listener) {
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					if (null != listener) {
						listener.onError(e);
					}
					e.printStackTrace();
				} finally {
					if (null != con) {
						con.disconnect();
					}
				}

			}
		}).start();
	}
	public static void main(String[] args) {
//		String address = "";
		System.out.println("hehe");
//		address = "http://www.weather.com.cn/data/list3/city.xml";
//		address = "http://www.weather.com.cn/data/city3jdata/china.html";
//		HttpUtil.sendHttpRequest(address, null);
	}
}
