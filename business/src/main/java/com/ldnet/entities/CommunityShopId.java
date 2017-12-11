package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by zxs on 2016/1/25.
 */
public class CommunityShopId implements Serializable {
    //    "CID":"84219348cdf8404bbf1f9033d678a530",
//            "CName":null,
//            "CREATEDAY":"2015-12-14T17:09:19.317",
//            "CityName":null,
//            "ID":"9e7ef60a45b6402d8b8606d30994f938",
//            "RID":"b77ce82fc9e74087b671127c10fa5fea",
//            "Remarks":"配送时间为：早9:00-晚21:00 ",
//            "Status":true
    //小区id
    public String CID;
    //
    public String CName;
    //
    public String CREATEDAY;
    //城市名字
    public String CityName;
    //
    public String ID;
    //小店id
    public String RID;
    //配送时间
    public String Remarks;
    //是否开启小店
    public Boolean Status;

    public String getCID() {
        return CID;
    }

    public void setCID(String CID) {
        this.CID = CID;
    }

    public String getCName() {
        return CName;
    }

    public void setCName(String CName) {
        this.CName = CName;
    }

    public String getCREATEDAY() {
        return CREATEDAY;
    }

    public void setCREATEDAY(String CREATEDAY) {
        this.CREATEDAY = CREATEDAY;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getRID() {
        return RID;
    }

    public void setRID(String RID) {
        this.RID = RID;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public Boolean getStatus() {
        return Status;
    }

    public void setStatus(Boolean status) {
        Status = status;
    }
}
