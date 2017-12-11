package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by zxs on 2016/5/17.
 */
public class RetailerInfo implements Serializable {
    //    "A":"凤城一路雅荷花园A32",
//            "ABS":"马来西亚进口咖啡国内的销售",
//            "AID":610112,
//            "AN":"未央区",
//            "CID":610100,
//            "CN":"西安市",
//            "FM":100,
//            "ID":"97bd4964556449ea96b4ce6fd0787968",
//            "ISAD":true,
//            "ISCT":true,
//            "ISD":true,
//            "LS":"64c6ba31e4f249bb86f29380837b2ff1",
//            "LX":"34.315266",
//            "LY":"108.950061",
//            "N":"爱马来白咖啡",
//            "P":"15209263346",
//            "PID":610000,
//            "PN":"陕西省",
//            "TC":0,
//            "TM":8
    public String A;//地址
    public String ABS;//简介
    public String AID;//所在县、区id
    public String AN;//所在县、区名称
    public String CID;//所在市id
    public String CN;//所在市名称
    public String FM;//满多少免配送费
    public String ID;//商品id
    public Boolean ISAD;//是否满多少免配送费-true是
    public Boolean ISCT;//是否收取配送费-true收取
    public Boolean ISD;//是否送货-true送
    public String LS;//logo
    public String LX;//经度
    public String LY;//纬度
    public String N;//名称
    public String P;//电话
    public String PID;//所在省ID
    public String PN;//所在省名称
    public String TC;//拨打次数
    public String TM;//配送费金额

    public String getA() {
        return A;
    }

    public void setA(String a) {
        A = a;
    }

    public String getABS() {
        return ABS;
    }

    public void setABS(String ABS) {
        this.ABS = ABS;
    }

    public String getAID() {
        return AID;
    }

    public void setAID(String AID) {
        this.AID = AID;
    }

    public String getAN() {
        return AN;
    }

    public void setAN(String AN) {
        this.AN = AN;
    }

    public String getCID() {
        return CID;
    }

    public void setCID(String CID) {
        this.CID = CID;
    }

    public String getCN() {
        return CN;
    }

    public void setCN(String CN) {
        this.CN = CN;
    }

    public String getFM() {
        return FM;
    }

    public void setFM(String FM) {
        this.FM = FM;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Boolean getISAD() {
        return ISAD;
    }

    public void setISAD(Boolean ISAD) {
        this.ISAD = ISAD;
    }

    public Boolean getISCT() {
        return ISCT;
    }

    public void setISCT(Boolean ISCT) {
        this.ISCT = ISCT;
    }

    public Boolean getISD() {
        return ISD;
    }

    public void setISD(Boolean ISD) {
        this.ISD = ISD;
    }

    public String getLS() {
        return LS;
    }

    public void setLS(String LS) {
        this.LS = LS;
    }

    public String getLX() {
        return LX;
    }

    public void setLX(String LX) {
        this.LX = LX;
    }

    public String getLY() {
        return LY;
    }

    public void setLY(String LY) {
        this.LY = LY;
    }

    public String getN() {
        return N;
    }

    public void setN(String n) {
        N = n;
    }

    public String getP() {
        return P;
    }

    public void setP(String p) {
        P = p;
    }

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public String getPN() {
        return PN;
    }

    public void setPN(String PN) {
        this.PN = PN;
    }

    public String getTC() {
        return TC;
    }

    public void setTC(String TC) {
        this.TC = TC;
    }

    public String getTM() {
        return TM;
    }

    public void setTM(String TM) {
        this.TM = TM;
    }
}
