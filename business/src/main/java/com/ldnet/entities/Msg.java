package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by lee on 2016/7/28.
 */
public class Msg implements Serializable {

    private static final long serialVersionUID = 6012222825670374690L;

    public boolean NOTICE;
    public boolean COMPLAIN;
    public boolean REPAIRS;
    public boolean COMMUNICATION;
    public boolean FEE;
    public boolean PAGE;
    public boolean FEEDBACK;
    public boolean ORDER;
    public boolean OTHER;
    public boolean MESSAGE;
    public boolean PROPERTY_MSG;
    public int callbackId;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public boolean isNOTICE() {
        return NOTICE;
    }

    public void setNOTICE(boolean NOTICE) {
        this.NOTICE = NOTICE;
    }

    public boolean isCOMPLAIN() {
        return COMPLAIN;
    }

    public void setCOMPLAIN(boolean COMPLAIN) {
        this.COMPLAIN = COMPLAIN;
    }

    public boolean isREPAIRS() {
        return REPAIRS;
    }

    public void setREPAIRS(boolean REPAIRS) {
        this.REPAIRS = REPAIRS;
    }

    public boolean isCOMMUNICATION() {
        return COMMUNICATION;
    }

    public void setCOMMUNICATION(boolean COMMUNICATION) {
        this.COMMUNICATION = COMMUNICATION;
    }

    public boolean isFEE() {
        return FEE;
    }

    public void setFEE(boolean FEE) {
        this.FEE = FEE;
    }

    public boolean isPAGE() {
        return PAGE;
    }

    public void setPAGE(boolean PAGE) {
        this.PAGE = PAGE;
    }

    public boolean isFEEDBACK() {
        return FEEDBACK;
    }

    public void setFEEDBACK(boolean FEEDBACK) {
        this.FEEDBACK = FEEDBACK;
    }

    public boolean isORDER() {
        return ORDER;
    }

    public void setORDER(boolean ORDER) {
        this.ORDER = ORDER;
    }

    public boolean isOTHER() {
        return OTHER;
    }

    public void setOTHER(boolean OTHER) {
        this.OTHER = OTHER;
    }

    public boolean isMESSAGE() {
        return MESSAGE;
    }

    public void setMESSAGE(boolean MESSAGE) {
        this.MESSAGE = MESSAGE;
    }

    public int getCallbackId() {
        return callbackId;
    }

    public void setCallbackId(int callbackId) {
        this.callbackId = callbackId;
    }
}
