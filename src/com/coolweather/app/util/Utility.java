package com.coolweather.app.util;

import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.modle.City;
import com.coolweather.app.modle.County;
import com.coolweather.app.modle.Province;

public class Utility {
	/**
	 * 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹鐪侀敓鏂ゆ嫹閿熸枻鎷烽敓锟�	 * 
	 * @param db
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleProvincesResponse(
			CoolWeatherDB db, String response) {
		boolean flag = false;
		if (!TextUtils.isEmpty(response)) {
//			String allProvinces[] = response.split(",");
			String allProvinces[] = response.replace("{", "").replace("}", "").split(",");
			if (null != allProvinces && allProvinces.length > 0) {
				for (String p : allProvinces) {
					String[] array = p.split(":");
//					String[] array = p.split("\\|");
					Province province = new Province();
					//System.out.println("array[0]"+array[0]);
					//System.out.println("array[1]"+array[1]);
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					// 閿熸枻鎷烽敓鏂ゆ嫹鑿橀敓锟�		
					db.saveProvince(province);
					flag = true;
				}
			}
		}
		return flag;
	}
	/**
	 * 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熷彨纭锋嫹閿熸枻鎷烽敓锟�	 * 
	 * @param db
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleCitiesResponse(
			CoolWeatherDB db, String response,int provinceId) {
		boolean flag = false;
		if (!TextUtils.isEmpty(response)) {
			String allCities[] = response.replace("{", "").replace("}", "").split(",");
			if (null != allCities && allCities.length > 0) {
				for (String p : allCities) {
					String[] array = p.split(":");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					// 閿熸枻鎷烽敓鏂ゆ嫹鑿橀敓锟�					
					db.saveCity(city);
					flag = true;
				}
			}
		}
		return flag;
	}
	/**
	 * 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸埅纭锋嫹閿熸枻鎷烽敓锟�	 * 
	 * @param db
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleCountiesResponse(
			CoolWeatherDB db, String response,int cityId) {
		boolean flag = false;
		if (!TextUtils.isEmpty(response)) {
			String allCounties[] = response.replace("{", "").replace("}", "").split(",");
			if (null != allCounties && allCounties.length > 0) {
				for (String p : allCounties) {
					String[] array = p.split(":");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					// 閿熸枻鎷烽敓鏂ゆ嫹鑿橀敓锟�					
					db.saveCounty(county);
					flag = true;
				}
			}
		}
		return flag;
	}

}
