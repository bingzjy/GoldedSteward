package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by lee on 2016/7/28.
 */
public class MsgCenter implements Serializable {

    private static final long serialVersionUID = 6012222825670374690L;

    public String msg;

    public MsgCenter() {
        this.msg = "";
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
