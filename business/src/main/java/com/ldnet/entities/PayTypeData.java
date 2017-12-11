package com.ldnet.entities;

/**
 * Created by lee on 2017/6/29.
 */
public class PayTypeData {

    public String title;
    public String describe;
    public int imgResource;


    public PayTypeData(String title, String describe, int imgResource) {
        this.title = title;
        this.describe = describe;
        this.imgResource = imgResource;
    }

    public String getTitle() {
        return title;
    }

    public String getDescribe() {
        return describe;
    }

    public int getImgResource() {
        return imgResource;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public void setImgResource(int imgResource) {
        this.imgResource = imgResource;
    }
}
