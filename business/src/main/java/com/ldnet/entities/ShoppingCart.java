package com.ldnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Alex on 2015/9/28.
 */
public class ShoppingCart implements Serializable {
    //购物车ID
    public String ID;
    //商家ID
    public String BID;
    //商户名称
    public String BN;
    //商家是否收取配送费
    public Boolean ISP;
    //配送费金额
    public Float PE;
    //商家是否满XXX元免配送费
    public Boolean ISPH;
    //XXX元免配送费
    public Float MPE;
    //用户ID
    public String RID;
    //购物车子表（商品详情）
    public List<com.ldnet.entities.SD> SD;
    //是否被选中
    public Boolean IsChecked = true;
    //商家留言
    public String Message;

    //总计
    public Float TotalPrices() {
        BigDecimal pSum = new BigDecimal("0.00");
        for (com.ldnet.entities.SD sd : SD) {
            if (sd.IsChecked) {
                pSum = pSum.add(new BigDecimal(sd.GGP * sd.N));
            }
        }
        return pSum.floatValue();
    }

    //商品数量
    public Integer TotalNumbers() {
        Integer numbers = 0;
        for (com.ldnet.entities.SD sd : SD) {
            if (sd.IsChecked) {
                numbers += sd.N;
            }
        }
        return numbers;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

    public String getRID() {
        return RID;
    }

    public void setRID(String RID) {
        this.RID = RID;
    }

    public List<com.ldnet.entities.SD> getSD() {
        return SD;
    }

    public void setSD(List<com.ldnet.entities.SD> SD) {
        this.SD = SD;
    }

    public Boolean getIsChecked() {
        return IsChecked;
    }

    public void setIsChecked(Boolean isChecked) {
        IsChecked = isChecked;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
