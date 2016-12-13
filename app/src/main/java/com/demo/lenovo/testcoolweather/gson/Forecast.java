package com.demo.lenovo.testcoolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * PROJECT_NAME: TestCoolWeather.
 * PACKAGE_NAME: com.demo.lenovo.testcoolweather.gson.
 * Created by vanpersie on 2016/12/13.
 */

public class Forecast {
    @SerializedName("date")
    public String date;

    @SerializedName("tmp")
    public Temperature temperature;



    public class Temperature {
        @SerializedName("max")
        public int max;
        @SerializedName("min")
        public int min;
    }
    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("txt_d")
        public String info;
    }
}
