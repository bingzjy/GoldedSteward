package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by Alex on 2015/9/23.
 */
public class PPhones implements Serializable {
    public String Title;
    public String Tel;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getTel() {
        return Tel;
    }

    public void setTel(String tel) {
        Tel = tel;
    }
}
