package com.demo.lenovo.testcoolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.demo.lenovo.testcoolweather.gson.Forecast;
import com.demo.lenovo.testcoolweather.gson.Weather;
import com.demo.lenovo.testcoolweather.service.AutoUpdateService;
import com.demo.lenovo.testcoolweather.util.HttpUtil;
import com.demo.lenovo.testcoolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private static String mWeatherId;
    public SwipeRefreshLayout mRefresh;
    public DrawerLayout mDrawer;
    private ScrollView mWeatherLayout;
    private TextView mTitleCity;
    private TextView mTitleUpdateTime;
    private TextView mDegreeText;
    private TextView mWeatherInfoText;
    private LinearLayout mForecastLayout;
    private TextView mAQIText;
    private TextView mPM25Text;
    private TextView mComfortText;
    private TextView mCarWashText;
    private TextView mSportText;
    private ImageView image_back;
    private ImageView weather_background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        init();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        } else {
            mWeatherId = getIntent().getStringExtra("weather_id");
            mWeatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }

        loadBackGroundPic();
        setListener();


    }

    private void loadBackGroundPic() {
        HttpUtil.sendOkHttpRequest(HttpUtil.WEATHER_BACKGROUND, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String pic = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(pic).into(weather_background);
                    }
                });

            }
        });
    }

    private void setListener() {

        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                requestWeather(mWeatherId);
            }
        });
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.openDrawer(GravityCompat.START);
            }
        });

    }

    public void requestWeather(String weatherId) {
        mWeatherId = weatherId;
        String weatherUrl = HttpUtil.WEATHER_INFO + "?cityid=" + weatherId + "&key=" + HttpUtil.KEY;
        // Log.e("WeatherActivity", weatherUrl);
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败requestWeather", Toast.LENGTH_SHORT).show();
                        mRefresh.setRefreshing(false);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                //   Log.e("WeatherActivity", responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (weather != null & "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败onResponse", Toast.LENGTH_SHORT).show();
                        }
                        mRefresh.setRefreshing(false);
                    }
                });
                loadBackGroundPic();


            }
        });

    }

    private void showWeatherInfo(Weather weather) {
        if (weather != null && "ok".equals(weather.status)) {
            mWeatherId = weather.basic.weatherId;


            String cityName = weather.basic.cityName;
            String updateTime = weather.basic.update.updateTime.split(" ")[1];
            String degree = weather.now.temperature + "℃";
            String weatherInfo = weather.now.more.info;
            mTitleCity.setText(cityName);
            mTitleUpdateTime.setText(updateTime);
            mDegreeText.setText(degree);
            mWeatherInfoText.setText(weatherInfo);

            mForecastLayout.removeAllViews();
            for (Forecast forecast : weather.forecastList) {
                View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, mForecastLayout, false);
                TextView dateText = (TextView) view.findViewById(R.id.date_text);
                TextView infoText = (TextView) view.findViewById(R.id.info_text);
                TextView maxText = (TextView) view.findViewById(R.id.max_text);
                TextView minText = (TextView) view.findViewById(R.id.min_text);
                dateText.setText(forecast.date);

                infoText.setText(forecast.more.info);
                maxText.setText(forecast.temperature.max + "");
                minText.setText(forecast.temperature.min + "");
                mForecastLayout.addView(view);
            }
            if (weather.aqi != null) {
                mAQIText.setText(weather.aqi.city.aqi);
                mPM25Text.setText(weather.aqi.city.pm25);

            }
            String comfort = "舒适度：" + weather.suggestion.comfort.info;
            String carWash = "洗车指数：" + weather.suggestion.carWash.info;
            String sport = "运动建议：" + weather.suggestion.sport.info;
            mComfortText.setText(comfort);
            mCarWashText.setText(carWash);
            mSportText.setText(sport);
            mWeatherLayout.setVisibility(View.VISIBLE);
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
        } else {
            Toast.makeText(this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
        }

    }

    private void init() {
        image_back = (ImageView) findViewById(R.id.image_back);
        mWeatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        mTitleCity = (TextView) findViewById(R.id.title_city);
        mTitleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        mDegreeText = (TextView) findViewById(R.id.degree_text);
        mWeatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        mForecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        mAQIText = (TextView) findViewById(R.id.aqi_text);
        mPM25Text = (TextView) findViewById(R.id.pm25_text);
        mComfortText = (TextView) findViewById(R.id.comfort_text);
        mCarWashText = (TextView) findViewById(R.id.wash_text);
        mSportText = (TextView) findViewById(R.id.sport_text);
        weather_background = (ImageView) findViewById(R.id.weather_background);
        mRefresh = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mRefresh.setColorSchemeResources(R.color.colorPrimary);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer);


    }
}
