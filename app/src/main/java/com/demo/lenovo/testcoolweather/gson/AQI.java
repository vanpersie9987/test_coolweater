package com.demo.lenovo.testcoolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * PROJECT_NAME: TestCoolWeather.
 * PACKAGE_NAME: com.demo.lenovo.testcoolweather.gson.
 * Created by vanpersie on 2016/12/13.
 */

public class AQI {
    @SerializedName("city")
    public AQICity city;

    public class AQICity {
        @SerializedName("aqi")
        public String aqi;
        @SerializedName("pm25")
        public String pm25;
    }
}
