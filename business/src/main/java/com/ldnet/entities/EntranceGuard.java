package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by lee on 2017/4/24.
 */
public class EntranceGuard implements Serializable {

    private String Id;
    private String Value;
    private String Flag;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public String getFlag() {
        return Flag;
    }

    public void setFlag(String flag) {
        Flag = flag;
    }
}
