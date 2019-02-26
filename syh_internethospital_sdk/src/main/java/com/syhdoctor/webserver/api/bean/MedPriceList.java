package com.syhdoctor.webserver.api.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class MedPriceList {

    @JSONField(name = "med_class_code")
    private String medClassCode;

    public String getMedClassCode() {
        return medClassCode;
    }

    public void setMedClassCode(String medClassCode) {
        this.medClassCode = medClassCode;
    }

    public String getMedClassName() {
        return medClassName;
    }

    public void setMedClassName(String medClassName) {
        this.medClassName = medClassName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @JSONField(name = "med_class_name")
    private String medClassName;

    @JSONField(name = "price")
    private String price;
}
