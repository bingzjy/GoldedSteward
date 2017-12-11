package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by Alex on 2015/9/22.
 */
public class lstAPPFees implements Serializable{
    public String FeeDate;
    public String ID;
    public String ItemId;
    public String ItemTitle;
    public Float Payable;
    public Boolean Status;//状态true - 已缴,false - 未缴

    public String getFeeDate() {
        return FeeDate;
    }

    public void setFeeDate(String feeDate) {
        FeeDate = feeDate;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getItemTitle() {
        return ItemTitle;
    }

    public void setItemTitle(String itemTitle) {
        ItemTitle = itemTitle;
    }

    public Float getPayable() {
        return Payable;
    }

    public void setPayable(Float payable) {
        Payable = payable;
    }

    public Boolean getStatus() {
        return Status;
    }

    public void setStatus(Boolean status) {
        Status = status;
    }

    public String getItemId() {
        return ItemId;
    }

    public void setItemId(String itemId) {
        ItemId = itemId;
    }
}
