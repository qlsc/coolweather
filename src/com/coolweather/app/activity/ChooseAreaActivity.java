package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	private ProgressDialog progressdialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolweatherdb;
	private List<String> dataList = new ArrayList<String>();
	// ʡ�б�
	private List<Province> provinceList;
	// ���б�
	private List<City> cityList;
	// ���б�
	private List<County> countyList;
	// ѡ�е�ʡ��
	private Province selectedProvince;
	// ѡ�еĳ���
	private City selectedCity;


	// ��ǰѡ�м���
	private int currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,
		android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		coolweatherdb = CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
					if (currentLevel == LEVEL_PROVINCE) {
						selectedProvince= provinceList.get(index);
						queryCities();
					}else if (currentLevel == LEVEL_CITY) {
						selectedCity = cityList.get(index);
						queryCounties();
					}
			}
			
		});
		queryProvinces();
	}

	private void queryProvinces() {
		provinceList = coolweatherdb.loadProvinces();
		if (provinceList.size()>0) {
			dataList.clear();
			for (Province province  : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
			
		}else {
			queryFromServer(null,"province");  //����ʡ������
		}
		//queryFromServer(null,"province");  //����ʡ������
	}

	protected void queryCounties() {
		countyList = coolweatherdb.loadcounties(selectedCity.getId());
		if (countyList.size()>0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		}else {
		
			queryFromServer(selectedCity.getCityCode(),"county");  //�����м�����
		}
	}

	protected void queryCities() {
		cityList = coolweatherdb.loadcities(selectedProvince.getId());
		if (cityList.size()>0) {
			dataList.clear();
			for(City city:cityList){
				dataList.add(city.getCityName());
				
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel  = LEVEL_CITY;
		}else {
			queryFromServer(selectedProvince.getProvinceCode(),"city");  //�����м�����
		}
		
	}

	private void queryFromServer(final String code,final String type) {
		String address = null;
		if(!TextUtils.isEmpty(code)){
//			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
			if ("city".equals(type)) {
			address = "http://www.weather.com.cn/data/city3jdata/provshi/"+code+".html";//��
			}else if ("county".equals(type)) {
				address = "http://www.weather.com.cn/data/city3jdata/station/"+code+".html";//��
			}
			
		}else {
//			address = "http://www.weather.com.cn/data/list3/city.xml";
			address = "http://www.weather.com.cn/data/city3jdata/china.html";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				System.out.println("========type:"+type);
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(coolweatherdb, response);
				}else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(coolweatherdb, response, selectedProvince.getId());
				}else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(coolweatherdb, response, selectedCity.getId());
				}
				if (result) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							
							closeProgressDialog();
							System.out.println("runOnUiThread(new Runnable()");
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
						Toast.makeText(ChooseAreaActivity.this,"����ʧ��", Toast.LENGTH_SHORT).show();					}
				});
			}
		});
	}

	private void showProgressDialog() {
		if (null == progressdialog) {
			progressdialog = new ProgressDialog(this);
			progressdialog.setMessage("���ڼ���...");
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