package com.demo.lenovo.testcoolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * PROJECT_NAME: TestCoolWeather.
 * PACKAGE_NAME: com.demo.lenovo.testcoolweather.gson.
 * Created by vanpersie on 2016/12/13.
 */

public class Suggestion {


    @SerializedName("sport")
    public Sport sport;
    @SerializedName("comf")
    public Comfort comfort;
    @SerializedName("cw")
    public CarWash carWash;

    public class Sport {
        @SerializedName("txt")
        public String info;
    }

    public class Comfort {
        @SerializedName("txt")
        public String info;
    }


    public class CarWash {
        @SerializedName("txt")
        public String info;
    }

}
