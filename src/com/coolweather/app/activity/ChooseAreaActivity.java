package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.modle.City;
import com.coolweather.app.modle.County;
import com.coolweather.app.modle.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVIMCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	private ProgressDialog progressdialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolweatherdb;
	private List<String> dataList = new ArrayList<String>();
	// 省列表
	private List<Province> provincelist;
	// 市列表
	private List<City> citylist;
	// 县列表
	private List<County> countylist;
	// 选中的省份
	private City selectedcity;
	// 选中的城市
	private County selectedCounty;
	// 选中的县
	private Province selectedProvince;
	// 当前选中级别
	private int currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,
		android.R.layout.simple_expandable_list_item_1, dataList);
		listView.setAdapter(adapter);
		coolweatherdb = CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
					if (currentLevel == LEVEL_PROVIMCE) {
						selectedProvince= provincelist.get(index);
						queryCities();
					}else if (currentLevel == LEVEL_CITY) {
						selectedcity = citylist.get(index);
						queryCounties();
					}
			}
			
		});
		queryProvinces();
	}

	private void queryProvinces() {
		provincelist = coolweatherdb.loadProvinces();
		if (provincelist.size()>0) {
			dataList.clear();
			for (Province province  : provincelist) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVIMCE;
			
		}else {
			queryFromServer(null,"province");  //加载省级数据
		}
		
	}

	protected void queryCounties() {
		citylist = coolweatherdb.loadcities(selectedProvince.getId());
		if (citylist.size()>0) {
			dataList.clear();
			for(City city:citylist){
				dataList.add(city.getCityName());
				
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel  = LEVEL_CITY;
		}else {
			queryFromServer(selectedProvince.getProvinceCode(),"city");  //加载市级数据
		}
	}

	protected void queryCities() {
		countylist = coolweatherdb.loadcounties(selectedcity.getId());
		if (countylist.size()>0) {
			dataList.clear();
			for (County county : countylist) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedcity.getCityName());
			currentLevel = LEVEL_COUNTY;
		}else {
			queryFromServer(selectedcity.getCityCode(),"county");  //加载市级数据
		}
	}

	private void queryFromServer(final String code,final String type) {
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(coolweatherdb, response);
				}else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(coolweatherdb, response, selectedProvince.getId());
				}else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(coolweatherdb, response, selectedcity.getId());
				}
				if (result) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							}else if ("city".equals(type)) {
								queryCities();
							}else if ("county".equals(type)) {
								queryCounties();
							}
						}

						
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this,"加载失败", Toast.LENGTH_SHORT).show();					}
				});
			}
		});
	}

	private void showProgressDialog() {
		if (null == progressdialog) {
			progressdialog = new ProgressDialog(this);
			progressdialog.setMessage("正在加载");
			progressdialog.setCanceledOnTouchOutside(false);
		}
		progressdialog.show();
	}
	private void closeProgressDialog() {
		if (null != progressdialog) {
			progressdialog.dismiss();
		}
	}
	
	
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		}else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		}else {
			finish();
		}
	}
	
	
	
	
	
	
}