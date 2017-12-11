package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by Alex on 2015/9/28.
 */
public class OrderPay implements Serializable {

    public String OrderPayNumber;//交易号
    public Float Amount;//总金额

    public String getOrderPayNumber() {
        return OrderPayNumber;
    }

    public void setOrderPayNumber(String orderPayNumber) {
        OrderPayNumber = orderPayNumber;
    }

    public Float getAmount() {
        return Amount;
    }

    public void setAmount(Float amount) {
        Amount = amount;
    }
}
