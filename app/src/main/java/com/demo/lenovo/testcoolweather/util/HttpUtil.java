package com.demo.lenovo.testcoolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * PROJECT_NAME: TestCoolWeather.
 * PACKAGE_NAME: com.demo.lenovo.testcoolweather.util.
 * Created by vanpersie on 2016/12/12.
 */

public class HttpUtil {
    public static final String ADDRESS_ROOT = "http://guolin.tech/api/china";
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
