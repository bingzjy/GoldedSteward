package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by zxs on 2016/1/30.
 */
public class PropertyServicesType implements Serializable {
    //投诉
    public String COMPLAIN;
    //报修
    public String REPAIRS;
    //沟通
    public String COMMUNICATION;

    public String getCOMPLAIN() {
        return COMPLAIN;
    }

    public void setCOMPLAIN(String COMPLAIN) {
        this.COMPLAIN = COMPLAIN;
    }

    public String getREPAIRS() {
        return REPAIRS;
    }

    public void setREPAIRS(String REPAIRS) {
        this.REPAIRS = REPAIRS;
    }

    public String getCOMMUNICATION() {
        return COMMUNICATION;
    }

    public void setCOMMUNICATION(String COMMUNICATION) {
        this.COMMUNICATION = COMMUNICATION;
    }
}
