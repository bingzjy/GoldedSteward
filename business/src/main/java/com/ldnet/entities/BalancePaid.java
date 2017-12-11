package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by zxs on 2016/1/22.
 */
public class BalancePaid implements Serializable {
    //    "orderID":"8fa7a1ec5aec4f088c87cd810cd7f0e4",
//            "PayMoney":0,
//            "TN":null,
//            "IsBalancePayOK":true
    public String orderID;
    public String TN;
    public Float PayMoney;
    public Boolean IsBalancePayOK;

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getTN() {
        return TN;
    }

    public void setTN(String TN) {
        this.TN = TN;
    }

    public Float getPayMoney() {
        return PayMoney;
    }

    public void setPayMoney(Float payMoney) {
        PayMoney = payMoney;
    }

    public Boolean getIsBalancePayOK() {
        return IsBalancePayOK;
    }

    public void setIsBalancePayOK(Boolean isBalancePayOK) {
        IsBalancePayOK = isBalancePayOK;
    }
}
