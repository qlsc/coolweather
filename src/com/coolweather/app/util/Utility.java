package com.coolweather.app.util;

import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.modle.City;
import com.coolweather.app.modle.County;
import com.coolweather.app.modle.Province;

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

}
