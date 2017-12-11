package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by Alex on 2015/9/28.
 */
public class OD implements Serializable {
    public String GID;//商品ID
    public String GN;//商品名称
    public String GI;//商品图片
    public Float P;//商品单价
    public Float GP;//商品金牌价格（实际价格）
    public Integer N;//商品数量
    public String GTN;//规格名称
    //----------以下在订单详细接口中才会有的数据---------------
    public Boolean ISNU;//商品是否已不存在 true:不存在
    public Boolean ISXJ;//商品是否已下架 true:已下架
    public Boolean ISUD;//商品是否已更改 true:已更改

    public String getGID() {
        return GID;
    }

    public void setGID(String GID) {
        this.GID = GID;
    }

    public String getGN() {
        return GN;
    }

    public void setGN(String GN) {
        this.GN = GN;
    }

    public String getGI() {
        return GI;
    }

    public void setGI(String GI) {
        this.GI = GI;
    }

    public Float getP() {
        return P;
    }

    public void setP(Float p) {
        P = p;
    }

    public Float getGP() {
        return GP;
    }

    public void setGP(Float GP) {
        this.GP = GP;
    }

    public Integer getN() {
        return N;
    }

    public void setN(Integer n) {
        N = n;
    }

    public String getGTN() {
        return GTN;
    }

    public void setGTN(String GTN) {
        this.GTN = GTN;
    }

    public Boolean getISNU() {
        return ISNU;
    }

    public void setISNU(Boolean ISNU) {
        this.ISNU = ISNU;
    }

    public Boolean getISXJ() {
        return ISXJ;
    }

    public void setISXJ(Boolean ISXJ) {
        this.ISXJ = ISXJ;
    }

    public Boolean getISUD() {
        return ISUD;
    }

    public void setISUD(Boolean ISUD) {
        this.ISUD = ISUD;
    }
}
