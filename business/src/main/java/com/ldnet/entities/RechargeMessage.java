package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by zxs on 2016/2/17.
 */
public class RechargeMessage implements Serializable {
    //    "PayOrder":"3c6f5aba483c44ada6476af5eff4fb67",
//            "TN":"201602171126264742428",
//            "IsOk":false,
//            "IsError":false,
//            "Moneys":1
    public String PayOrder;
    public String TN;
    public Boolean IsOk;
    public Boolean IsError;
    public Float Moneys;

    public String getPayOrder() {
        return PayOrder;
    }

    public void setPayOrder(String payOrder) {
        PayOrder = payOrder;
    }

    public String getTN() {
        return TN;
    }

    public void setTN(String TN) {
        this.TN = TN;
    }

    public Boolean getIsOk() {
        return IsOk;
    }

    public void setIsOk(Boolean isOk) {
        IsOk = isOk;
    }

    public Boolean getIsError() {
        return IsError;
    }

    public void setIsError(Boolean isError) {
        IsError = isError;
    }

    public Float getMoneys() {
        return Moneys;
    }

    public void setMoneys(Float moneys) {
        Moneys = moneys;
    }
}
