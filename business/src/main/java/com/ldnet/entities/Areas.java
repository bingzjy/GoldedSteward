package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by Alex on 2015/9/24.
 */
public class Areas implements Serializable {
    public Areas(){}

    //构造函数
    public Areas(Integer id, String name) {
        Id = id;
        Name = name;
    }

    public Integer Id;
    public String Name;

    @Override
    public String toString() {
        return Name;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
