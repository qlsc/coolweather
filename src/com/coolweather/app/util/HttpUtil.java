package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;

public class HttpUtil {
	public static void sendHttpRequest(final String address,
			final HttpCallbackListener listener) {
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

	public static void getMessage(final String address,
			final HttpCallbackListener listener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 解析url
				Document doc = readUrlFist(address);
				try {
					if (null != listener) {
						listener.onFinish(doc.toString());
					}
				} catch (Exception e) {
					if (null != listener) {
						listener.onError(e);
					}
					e.printStackTrace();
				}

			}
		}).start();
	}
	
	/**
	 * jsoup 连接不上重连
	 * @param url
	 * @return
	 */
	public static Document readUrlFist(String url) {
		Document doc = null;
		Connection conn = Jsoup.connect(url);
		conn.header(
				"User-Agent",
				"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2 Googlebot/2.1");
		try {
			doc = (Document) conn.timeout(5 * 1000).get();
		} catch (IOException e) {
			System.out.println("重试连接！");
			if ((e instanceof UnknownHostException)
					|| (e instanceof SocketTimeoutException)) {
				doc = readUrlFist(url);
			}

		}
		return doc;
	}
	
	
	
	public static void main(String[] args) {
		String address = "";
//		address = "http://www.weather.com.cn/data/city3jdata/station/1012701.html";
//		address = "http://www.weather.com.cn/data/city3jdata/china.html";
//		address = "http://www.weather.com.cn/weather1d/101270101.shtml";
		address = "http://api.map.baidu.com/telematics/v3/weather?location=齐河&output=json&ak=1bf4f9bb38cf3d3aaec3b7e4967d7421";
		HttpUtil.getMessage(address,null);



	}
}
