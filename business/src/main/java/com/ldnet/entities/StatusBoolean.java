package com.ldnet.entities;

import java.io.Serializable;

//服务器Bool值返回数据
public class StatusBoolean implements Serializable {

    private static final long serialVersionUID = -7607404596053904220L;

    public StatusBoolean(){}

    public StatusBoolean(Boolean valid, String message) {
        Valid = valid;
        Message = message;
    }

    public Boolean Valid;

    public String Message;
}
