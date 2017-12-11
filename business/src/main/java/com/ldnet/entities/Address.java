package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by Alex on 2015/9/23.
 */
public class Address implements Serializable {
    //收货地址ID
    public String ID;
    //收货人姓名
    public String N;
    //收货人移动电话
    public String MP;
    //座机区号
    public String AC;
    //座机号码
    public String TP;
    //邮编
    public String ZC;
    //是否为默认地址
    public Boolean ISD;
    //详细地址
    public String AD;
    //用户ID
    public String RID;
    //省份ID
    public Integer PID;
    //省份名称 -- 详细
    public String PN;
    //城市ID
    public Integer CID;
    //城市名称 -- 详细
    public String CN;
    //区域ID
    public Integer AID;
    //区域名称 -- 详细
    public String AN;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getN() {
        return N;
    }

    public void setN(String n) {
        N = n;
    }

    public String getMP() {
        return MP;
    }

    public void setMP(String MP) {
        this.MP = MP;
    }

    public String getAC() {
        return AC;
    }

    public void setAC(String AC) {
        this.AC = AC;
    }

    public String getTP() {
        return TP;
    }

    public void setTP(String TP) {
        this.TP = TP;
    }

    public String getZC() {
        return ZC;
    }

    public void setZC(String ZC) {
        this.ZC = ZC;
    }

    public Boolean getISD() {
        return ISD;
    }

    public void setISD(Boolean ISD) {
        this.ISD = ISD;
    }

    public String getAD() {
        return AD;
    }

    public void setAD(String AD) {
        this.AD = AD;
    }

    public String getRID() {
        return RID;
    }

    public void setRID(String RID) {
        this.RID = RID;
    }

    public Integer getPID() {
        return PID;
    }

    public void setPID(Integer PID) {
        this.PID = PID;
    }

    public String getPN() {
        return PN;
    }

    public void setPN(String PN) {
        this.PN = PN;
    }

    public Integer getCID() {
        return CID;
    }

    public void setCID(Integer CID) {
        this.CID = CID;
    }

    public String getCN() {
        return CN;
    }

    public void setCN(String CN) {
        this.CN = CN;
    }

    public Integer getAID() {
        return AID;
    }

    public void setAID(Integer AID) {
        this.AID = AID;
    }

    public String getAN() {
        return AN;
    }

    public void setAN(String AN) {
        this.AN = AN;
    }
}
