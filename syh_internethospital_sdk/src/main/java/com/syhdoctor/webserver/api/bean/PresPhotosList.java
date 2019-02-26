package com.syhdoctor.webserver.api.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class PresPhotosList {


    @JSONField(name = "pres_photos")
    private String presPhotos;

    public String getPresPhotos() {
        return presPhotos;
    }

    public void setPresPhotos(String presPhotos) {
        this.presPhotos = presPhotos;
    }
}
