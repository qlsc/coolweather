package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.modle.City;
import com.coolweather.app.modle.County;
import com.coolweather.app.modle.Province;
import com.coolweather.app.modle.WeatherModel;
public class Utility {
    /**
     *
     * @param db
     * @param response
     * @return
     */
	public synchronized static boolean handleProvincesResponse(
			CoolWeatherDB db, String response) {
		boolean flag = false;
		if (!TextUtils.isEmpty(response)) {
			String allProvinces[] = response.replace("{", "").replace("}", "").replaceAll("\"","").split(",");
			if (null != allProvinces && allProvinces.length > 0) {
				for (String p : allProvinces) {
					String[] array = p.split(":");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					db.saveProvince(province);
					flag = true;
				}
			}
		}
		return flag;
	}
	/**
	 * @param db
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleCitiesResponse(
			CoolWeatherDB db, String response,int provinceId) {
		boolean flag = false;
		if (!TextUtils.isEmpty(response)) {
			String allCities[] = response.replace("{", "").replace("}", "").replaceAll("\"","").split(",");
			if (null != allCities && allCities.length > 0) {
				for (String p : allCities) {
					String[] array = p.split(":");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					db.saveCity(city);
					flag = true;
				}
			}
		}
		return flag;
	}
	/**
	 * @param db
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleCountiesResponse(
			CoolWeatherDB db, String response,int cityId) {
		boolean flag = false;
		if (!TextUtils.isEmpty(response)) {
			String allCounties[] = response.replace("{", "").replace("}", "").replaceAll("\"","").split(",");
			if (null != allCounties && allCounties.length > 0) {
				for (String p : allCounties) {
					String[] array = p.split(":");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					db.saveCounty(county);
					flag = true;
				}
			}
		}
		return flag;
	}

    /**
     * 解析服务器返回的JSON数据，并将解析出的数据存储到本地。
     */
    public static void handleWeatherResponse(Context context, String response) {
        try {
        	JSONObject dataOfJson = JSON.parseObject(response);  
    		//从json数据中取得的时间
            String date = dataOfJson.getString("date");  
            int year = Integer.parseInt(date.substring(0, 4));  
            int month = Integer.parseInt(date.substring(5, 7));  
            int day = Integer.parseInt(date.substring(8, 10));  
            Date today = new Date(year-1900,month-1,day);  

            JSONArray results=dataOfJson.getJSONArray("results");  
            JSONObject results0=results.getJSONObject(0);  
              
            String location = results0.getString("currentCity");  

            JSONArray weather_data = results0.getJSONArray("weather_data");//weather_data中有四项
            System.out.println(weather_data.get(0));
            //今天实时天气
            JSONObject OneDayWeatherInfo = weather_data.getJSONObject(0);
            String dayData = OneDayWeatherInfo.getString("date"); 
            WeatherModel weather = new WeatherModel();
            weather.setDate((today.getYear()+1900)+"."+(today.getMonth()+1)+"."+today.getDate());
            weather.setTemperature(OneDayWeatherInfo.getString("temperature"));
            weather.setWeather(OneDayWeatherInfo.getString("weather"));
            weather.setWind(OneDayWeatherInfo.getString("wind"));
            String cityName = location;
            saveWeatherInfo(context, cityName,weather);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 将服务器返回的所有天气信息存储到SharedPreferences文件中。
     */
    public static void saveWeatherInfo(Context context,String cityName, WeatherModel weather) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日 HH:mm:ss", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("temp1", weather.getTemperature());
        editor.putString("temp2", weather.getWind());
        editor.putString("weather_desp", weather.getWeather());
        editor.putString("publish_time", weather.getDate());
        editor.putString("current_date", sdf.format(new Date()));
        editor.commit();
    }
}
