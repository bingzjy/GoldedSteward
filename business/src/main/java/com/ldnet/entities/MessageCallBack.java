package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by lee on 2016/8/5.
 */
public class MessageCallBack implements Serializable {

    private com.ldnet.entities.N N;
    private String callbackId;

    public com.ldnet.entities.N getN() {
        return N;
    }

    public void setN(com.ldnet.entities.N n) {
        N = n;
    }

    public String getCallbackId() {
        return callbackId;
    }

    public void setCallbackId(String callbackId) {
        this.callbackId = callbackId;
    }
}
