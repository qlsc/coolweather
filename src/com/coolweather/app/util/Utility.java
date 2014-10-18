package com.coolweather.app.util;

import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.modle.City;
import com.coolweather.app.modle.County;
import com.coolweather.app.modle.Province;

public class Utility {
	/**
	 * 解析处理省级数据
	 * 
	 * @param db
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleProvincesResponse(
			CoolWeatherDB db, String response) {
		boolean flag = false;
		if (!TextUtils.isEmpty(response)) {
			String allProvinces[] = response.split(",");
			if (null != allProvinces && allProvinces.length > 0) {
				for (String p : allProvinces) {
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					// 存数据库
					db.saveProvince(province);
					flag = true;
				}
			}
		}
		return flag;
	}
	/**
	 * 解析处理市级数据
	 * 
	 * @param db
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleCitiesResponse(
			CoolWeatherDB db, String response,int provinceId) {
		boolean flag = false;
		if (!TextUtils.isEmpty(response)) {
			String allCities[] = response.split(",");
			if (null != allCities && allCities.length > 0) {
				for (String p : allCities) {
					String[] array = p.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					// 存数据库
					db.saveCity(city);
					flag = true;
				}
			}
		}
		return flag;
	}
	/**
	 * 解析处理县级数据
	 * 
	 * @param db
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleCountiesResponse(
			CoolWeatherDB db, String response,int cityId) {
		boolean flag = false;
		if (!TextUtils.isEmpty(response)) {
			String allCounties[] = response.split(",");
			if (null != allCounties && allCounties.length > 0) {
				for (String p : allCounties) {
					String[] array = p.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					// 存数据库
					db.saveCounty(county);
					flag = true;
				}
			}
		}
		return flag;
	}

}
