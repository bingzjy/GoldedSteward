package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by Alex on 2015/9/2.
 */
public class Building implements Serializable {

    public Building(){}

    //构造函数
    public Building(String id, String name) {
        Id = id;
        Name = name;
    }

    public String Id;
    public String Name;

    @Override
    public String toString() {
        return Name;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
