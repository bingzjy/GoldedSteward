package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by zxs on 2015/12/8.
 */
public class Advertisement implements Serializable {
    //广告图片
    public String Cover;
    // 广告信息Url
    public String Url;
    // 广告标题
    public String Title;

    public String getCover() {
        return Cover;
    }

    public void setCover(String cover) {
        Cover = cover;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}
