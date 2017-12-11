package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by Alex on 2015/9/29.
 * 商品规格
 */
public class Stock implements Serializable {
    //规格ID
    public String ID;
    //规格名称
    public String N;
    //零售价格
    public Float RP;
    //金牌价格
    public Float GP;
    //库存
    public Integer S;
    //商品ID
    public String GID;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getN() {
        return N;
    }

    public void setN(String n) {
        N = n;
    }

    public Float getRP() {
        return RP;
    }

    public void setRP(Float RP) {
        this.RP = RP;
    }

    public Float getGP() {
        return GP;
    }

    public void setGP(Float GP) {
        this.GP = GP;
    }

    public Integer getS() {
        return S;
    }

    public void setS(Integer s) {
        S = s;
    }

    public String getGID() {
        return GID;
    }

    public void setGID(String GID) {
        this.GID = GID;
    }
}
