package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by lee on 2016/8/5.
 */
public class N implements Serializable {

    private String NOTICE;
    private String COMPLAIN;
    private String REPAIRS;
    private String COMMUNICATION;
    private String FEE;
    private String PAGE;
    private String FEEDBACK;
    private String ORDER;
    private String OTHER;
    private String MESSAGE;

    public String getNOTICE() {
        return NOTICE;
    }

    public void setNOTICE(String NOTICE) {
        this.NOTICE = NOTICE;
    }

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

    public String getFEE() {
        return FEE;
    }

    public void setFEE(String FEE) {
        this.FEE = FEE;
    }

    public String getPAGE() {
        return PAGE;
    }

    public void setPAGE(String PAGE) {
        this.PAGE = PAGE;
    }

    public String getFEEDBACK() {
        return FEEDBACK;
    }

    public void setFEEDBACK(String FEEDBACK) {
        this.FEEDBACK = FEEDBACK;
    }

    public String getORDER() {
        return ORDER;
    }

    public void setORDER(String ORDER) {
        this.ORDER = ORDER;
    }

    public String getOTHER() {
        return OTHER;
    }

    public void setOTHER(String OTHER) {
        this.OTHER = OTHER;
    }

    public String getMESSAGE() {
        return MESSAGE;
    }

    public void setMESSAGE(String MESSAGE) {
        this.MESSAGE = MESSAGE;
    }
}
