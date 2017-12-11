package com.ldnet.entities;

import java.util.List;

/**
 * Created by zxs on 2015/12/9.
 */
public class APPHomePage_Row {

    public String AID;
    public String CREATEDAY;
    public String HEIGHTBI;
    public String ID;
    public String IsSlide;
    public String ORDERBY;
    public String TITLE;
    public List<com.ldnet.entities.APPHomePage_Column> APPHomePage_Column;

    public Float getRowHeightBI() {
        Float bi = 0.0f;
        bi += Float.parseFloat(HEIGHTBI);
        return bi;
    }

    public String getAID() {
        return AID;
    }

    public void setAID(String AID) {
        this.AID = AID;
    }

    public String getCREATEDAY() {
        return CREATEDAY;
    }

    public void setCREATEDAY(String CREATEDAY) {
        this.CREATEDAY = CREATEDAY;
    }

    public String getHEIGHTBI() {
        return HEIGHTBI;
    }

    public void setHEIGHTBI(String HEIGHTBI) {
        this.HEIGHTBI = HEIGHTBI;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getIsSlide() {
        return IsSlide;
    }

    public void setIsSlide(String isSlide) {
        IsSlide = isSlide;
    }

    public String getORDERBY() {
        return ORDERBY;
    }

    public void setORDERBY(String ORDERBY) {
        this.ORDERBY = ORDERBY;
    }

    public String getTITLE() {
        return TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }

    public List<com.ldnet.entities.APPHomePage_Column> getAPPHomePage_Column() {
        return APPHomePage_Column;
    }

    public void setAPPHomePage_Column(List<com.ldnet.entities.APPHomePage_Column> APPHomePage_Column) {
        this.APPHomePage_Column = APPHomePage_Column;
    }
}
