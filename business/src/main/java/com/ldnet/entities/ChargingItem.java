package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by lee on 2016/10/18.
 */
public class ChargingItem implements Serializable {
    private String CID;
    private String CNAME;
    private String CREATEDAY;
    private String ID;
    private String SFMONEY;
    private String STAFFID;
    private String STAFFNAME;
    private String TITLE;

    public String getCID() {
        return CID;
    }

    public void setCID(String CID) {
        this.CID = CID;
    }

    public String getCNAME() {
        return CNAME;
    }

    public void setCNAME(String CNAME) {
        this.CNAME = CNAME;
    }

    public String getCREATEDAY() {
        return CREATEDAY;
    }

    public void setCREATEDAY(String CREATEDAY) {
        this.CREATEDAY = CREATEDAY;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSFMONEY() {
        return SFMONEY;
    }

    public void setSFMONEY(String SFMONEY) {
        this.SFMONEY = SFMONEY;
    }

    public String getSTAFFID() {
        return STAFFID;
    }

    public void setSTAFFID(String STAFFID) {
        this.STAFFID = STAFFID;
    }

    public String getSTAFFNAME() {
        return STAFFNAME;
    }

    public void setSTAFFNAME(String STAFFNAME) {
        this.STAFFNAME = STAFFNAME;
    }

    public String getTITLE() {
        return TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }
}
