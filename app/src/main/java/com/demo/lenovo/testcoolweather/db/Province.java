package com.demo.lenovo.testcoolweather.db;

import org.litepal.crud.DataSupport;

/**
 * PROJECT_NAME: TestCoolWeather.
 * PACKAGE_NAME: com.demo.lenovo.testcoolweather.db.
 * Created by vanpersie on 2016/12/12.
 */

public class Province extends DataSupport {
    private int id;
    private String provinceName;
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
