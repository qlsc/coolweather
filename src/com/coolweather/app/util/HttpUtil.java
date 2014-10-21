package com.coolweather.app.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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

    /**
     * 用Jsoup抓取天气预报结果页面
     * @param address
     * @param listener
     */
    public static void sendHttpRequestFromJsoup(final String address,
			final HttpCallbackListener listener) {

		new Thread(new Runnable() {

			@Override
			public void run() {

                Document doc = null;
                try {
                    // 解析url
                    doc = (Document) Jsoup.connect(address).get();
                } catch (Exception e) {
                    System.out.println("socket 无法连接");
                    e.printStackTrace();
                }



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
					//在这里处理抓取结果

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
		String address = "";
//		address = "http://www.weather.com.cn/data/city3jdata/station/1012701.html";
//		address = "http://www.weather.com.cn/data/city3jdata/china.html";
		address = "http://www.weather.com.cn/weather1d/101270101.shtml";
//		HttpUtil.sendHttpRequest(address, null);



	}
}
