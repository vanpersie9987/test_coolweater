package com.demo.lenovo.testcoolweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.lenovo.testcoolweather.db.City;
import com.demo.lenovo.testcoolweather.db.County;
import com.demo.lenovo.testcoolweather.db.Province;
import com.demo.lenovo.testcoolweather.util.HttpUtil;
import com.demo.lenovo.testcoolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * PROJECT_NAME: TestCoolWeather.
 * PACKAGE_NAME: com.demo.lenovo.testcoolweather.
 * Created by vanpersie on 2016/12/12.
 */

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog mProgressDialog;
    private TextView mTitleText;
    private Button mBackButton;
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private List<String> mData = new ArrayList<>();
    private List<Province> mProvinceList;
    private List<City> mCityList;
    private List<County> mCountyList;
    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.choose_area, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTitleText = (TextView) view.findViewById(R.id.title_text);
        mBackButton = (Button) view.findViewById(R.id.back_button);
        mListView = (ListView) view.findViewById(R.id.list_view);
        mAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mData);
        mListView.setAdapter(mAdapter);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = mProvinceList.get(position);
                    queryCities();

                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = mCityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {

                    String weatherId = mCountyList.get(position).getWeatherId();

                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();


                    } else if (getActivity() instanceof WeatherActivity) {
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.mDrawer.closeDrawers();
                        activity.mRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);


                    }


                }
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }


    private void queryProvinces() {
        mTitleText.setText("中国");
        mBackButton.setVisibility(View.GONE);
        mProvinceList = DataSupport.findAll(Province.class);
        if (mProvinceList != null & mProvinceList.size() > 0) {
            mData.clear();
            for (Province province : mProvinceList) {
                mData.add(province.getProvinceName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(HttpUtil.ADDRESS_ROOT, "province");
        }
    }

    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Log.e("cityList", responseText);
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }

                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }

                        }
                    });
                }

            }
        });


    }

    private void closeProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("正在加载...");
            mProgressDialog.setCanceledOnTouchOutside(false);

        }
        mProgressDialog.show();
    }

    private void queryCounties() {
        mTitleText.setText(selectedCity.getCityName());
        mBackButton.setVisibility(View.VISIBLE);
        mCountyList = DataSupport.where("cityId = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if (mCountyList != null & mCountyList.size() > 0) {
            mData.clear();
            for (County county : mCountyList) {
                mData.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVEL_COUNTY;

        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = HttpUtil.ADDRESS_ROOT + "/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }


    }

    private void queryCities() {
        mTitleText.setText(selectedProvince.getProvinceName());
        mBackButton.setVisibility(View.VISIBLE);
        mCityList = DataSupport.where("provinceId = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (mCityList != null & mCityList.size() > 0) {
            mData.clear();
            for (City city : mCityList) {
                mData.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVEL_CITY;

        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = HttpUtil.ADDRESS_ROOT + "/" + provinceCode;
            queryFromServer(address, "city");
        }
    }


}
