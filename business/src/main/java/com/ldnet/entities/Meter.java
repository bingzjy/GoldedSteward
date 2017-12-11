package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by Alex on 2015/9/2.
 */
public class Meter implements Serializable{
    public String No;
    public String Title;

    public String getNo() {
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}
