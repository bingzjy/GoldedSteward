package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by Alex on 2015/9/23.
 */
public class AddressSimple implements Serializable {
    //收货地址ID
    public String ID;
    //收货人姓名+电话
    public String NP;
    //邮编
    public String ZC;
    //省市区+详细地址
    public String AD;
    //是否为默认地址
    public Boolean ISD;
    //是否被选中
    public Boolean IsChecked = ISD;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNP() {
        return NP;
    }

    public void setNP(String NP) {
        this.NP = NP;
    }

    public String getZC() {
        return ZC;
    }

    public void setZC(String ZC) {
        this.ZC = ZC;
    }

    public String getAD() {
        return AD;
    }

    public void setAD(String AD) {
        this.AD = AD;
    }

    public Boolean getISD() {
        return ISD;
    }

    public void setISD(Boolean ISD) {
        this.ISD = ISD;
    }

    public Boolean getIsChecked() {
        return IsChecked;
    }

    public void setIsChecked(Boolean isChecked) {
        IsChecked = isChecked;
    }
}
