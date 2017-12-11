package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by Alex on 2015/9/16.
 */
public class WeekendSignUp implements Serializable {
    //姓名
    public String Name;
    //电话
    public String Tel;

    @Override
    public String toString() {
        return Tel + " [" + Name + "]";
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getTel() {
        return Tel;
    }

    public void setTel(String tel) {
        Tel = tel;
    }
}
