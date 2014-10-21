package com.coolweather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.activity.WeatherActivity;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.modle.City;
import com.coolweather.app.modle.County;
import com.coolweather.app.modle.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
					System.out.println("ProvinceCode:"+array[0]+" ==== ");
					System.out.print("ProvinceName:"+array[1]);
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
                    System.out.println("CityCode:"+array[0]+" ==== ");
                    System.out.print("CityName:"+array[1]);
                    System.out.print("=== provinceId:"+provinceId);
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
                    System.out.println("CountyCode:"+array[0]+" ==== ");
                    System.out.print("CountyName:"+array[1]);
                    System.out.print("=== cityId:"+cityId);
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
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
                    weatherDesp, publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 将服务器返回的所有天气信息存储到SharedPreferences文件中。
     */
    public static void saveWeatherInfo(Context context, String cityName,
                                       String weatherCode, String temp1, String temp2, String weatherDesp,
                                       String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.commit();
    }
}
