package com.demo.lenovo.testcoolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * PROJECT_NAME: TestCoolWeather.
 * PACKAGE_NAME: com.demo.lenovo.testcoolweather.gson.
 * Created by vanpersie on 2016/12/13.
 */

public class Weather {

    @SerializedName("status")
    public String status;

    @SerializedName("basic")
    public Basic basic;

    @SerializedName("aqi")
    public AQI aqi;
    @SerializedName("now")
    public Now now;

    @SerializedName("suggestion")
    public Suggestion suggestion;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
