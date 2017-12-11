package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by zxs on 2016/2/18.
 */
public class ConsumptionMessage implements Serializable {
//    "ID":"bc458ad409fd4767baec5b690ac62234",
//            "ResidentID":"b17fbe548fc6473cb1037f84659ba4c1",
//            "OperDay":"2016-02-23T09:58:54.173",
//            "OperTypes":2,
//            "OperMoneys":0.01,
//            "OperSource":3,
//            "OrderNumber":"6414915acf4d41d5ae1a01eb40f24cc6",
//            "IsOperBalance":true,
//            "BalanceMoneys":0.01,
//            "PayTypeID":0,
//            "OperTypesTitle":"入账",
//            "OperSourceTitle":"充值",
//            "PayTypeTitle":"余额支付"
    //交易id
    public String ID;
    // 账户id
    public String ResidentID;
    //    交易时间
    public String OperDay;
    //    交易类型
    public String OperTypes;
    //    交易金额
    public Float OperMoneys;
    //
    public Integer OperSource;
    //    订单号
    public String OrderNumber;
    //    是否交易余额
    public Boolean IsOperBalance;
    //    余额
    public Float BalanceMoneys;
    //    支付类型
    public Integer PayTypeID;
    //
    public String OperTypesTitle;
    //
    public String OperSourceTitle;
    //
    public String PayTypeTitle;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getResidentID() {
        return ResidentID;
    }

    public void setResidentID(String residentID) {
        ResidentID = residentID;
    }

    public String getOperDay() {
        return OperDay;
    }

    public void setOperDay(String operDay) {
        OperDay = operDay;
    }

    public String getOperTypes() {
        return OperTypes;
    }

    public void setOperTypes(String operTypes) {
        OperTypes = operTypes;
    }

    public Float getOperMoneys() {
        return OperMoneys;
    }

    public void setOperMoneys(Float operMoneys) {
        OperMoneys = operMoneys;
    }

    public Integer getOperSource() {
        return OperSource;
    }

    public void setOperSource(Integer operSource) {
        OperSource = operSource;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }

    public Boolean getIsOperBalance() {
        return IsOperBalance;
    }

    public void setIsOperBalance(Boolean isOperBalance) {
        IsOperBalance = isOperBalance;
    }

    public Float getBalanceMoneys() {
        return BalanceMoneys;
    }

    public void setBalanceMoneys(Float balanceMoneys) {
        BalanceMoneys = balanceMoneys;
    }

    public Integer getPayTypeID() {
        return PayTypeID;
    }

    public void setPayTypeID(Integer payTypeID) {
        PayTypeID = payTypeID;
    }

    public String getOperTypesTitle() {
        return OperTypesTitle;
    }

    public void setOperTypesTitle(String operTypesTitle) {
        OperTypesTitle = operTypesTitle;
    }

    public String getOperSourceTitle() {
        return OperSourceTitle;
    }

    public void setOperSourceTitle(String operSourceTitle) {
        OperSourceTitle = operSourceTitle;
    }

    public String getPayTypeTitle() {
        return PayTypeTitle;
    }

    public void setPayTypeTitle(String payTypeTitle) {
        PayTypeTitle = payTypeTitle;
    }
}
