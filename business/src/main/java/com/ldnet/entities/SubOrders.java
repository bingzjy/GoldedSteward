package com.ldnet.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Alex on 2015/9/28.
 */
public class SubOrders implements Serializable {
    public String BID;//商家ID
    public String BN;//商家名称
    public Boolean ISP;//商家是否收取配送费
    public Float PE;//配送费金额
    public Boolean ISPH;//商家是否满“XX”元免配送费
    public Float MPE;//商家满“XX”元免配送费金额
    public Float P;//总金额（包含配送费）
    public List<com.ldnet.entities.RS> RS;//列表页详细
    public String Message;
    public Boolean ISYHJ; //是否使用优惠劵
    public String YHJID;//优惠劵id
    public Float YHJJM;//优惠劵减免


    //总计
    public Float TotalPrices() {
        return P;
    }

    // 总的减免
    public Float TotalYhjjm() {
        return YHJJM;
    }

    //商品数量
    public Integer TotalNumbers() {
        Integer numbers = 0;
        for (com.ldnet.entities.RS rs : RS) {
            numbers += rs.GC;
        }
        return numbers;
    }

    public String getBID() {
        return BID;
    }

    public void setBID(String BID) {
        this.BID = BID;
    }

    public String getBN() {
        return BN;
    }

    public void setBN(String BN) {
        this.BN = BN;
    }

    public Boolean getISP() {
        return ISP;
    }

    public void setISP(Boolean ISP) {
        this.ISP = ISP;
    }

    public Float getPE() {
        return PE;
    }

    public void setPE(Float PE) {
        this.PE = PE;
    }

    public Boolean getISPH() {
        return ISPH;
    }

    public void setISPH(Boolean ISPH) {
        this.ISPH = ISPH;
    }

    public Float getMPE() {
        return MPE;
    }

    public void setMPE(Float MPE) {
        this.MPE = MPE;
    }

    public Float getP() {
        return P;
    }

    public void setP(Float p) {
        P = p;
    }

    public List<com.ldnet.entities.RS> getRS() {
        return RS;
    }

    public void setRS(List<com.ldnet.entities.RS> RS) {
        this.RS = RS;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public Boolean getISYHJ() {
        return ISYHJ;
    }

    public void setISYHJ(Boolean ISYHJ) {
        this.ISYHJ = ISYHJ;
    }

    public String getYHJID() {
        return YHJID;
    }

    public void setYHJID(String YHJID) {
        this.YHJID = YHJID;
    }

    public Float getYHJJM() {
        return YHJJM;
    }

    public void setYHJJM(Float YHJJM) {
        this.YHJJM = YHJJM;
    }
}
