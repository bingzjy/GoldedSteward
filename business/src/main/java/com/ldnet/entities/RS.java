package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by Alex on 2015/9/28.
 */
public class RS implements Serializable {
    public String GID;//商品ID
    public String GN;//商品名称
    public String GI;//商品图片
    public String GGID;//规格ID
    public String SID;//购物车商品子表ID
    public Float GP;//商品单价
    public Integer GC;//商品单数量
    public String GGN;//商品规格名称
    public String GSID; // 商品分类id
    public Boolean NOVOLUME;//

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

    public String getGGID() {
        return GGID;
    }

    public void setGGID(String GGID) {
        this.GGID = GGID;
    }

    public String getSID() {
        return SID;
    }

    public void setSID(String SID) {
        this.SID = SID;
    }

    public Float getGP() {
        return GP;
    }

    public void setGP(Float GP) {
        this.GP = GP;
    }

    public Integer getGC() {
        return GC;
    }

    public void setGC(Integer GC) {
        this.GC = GC;
    }

    public String getGGN() {
        return GGN;
    }

    public void setGGN(String GGN) {
        this.GGN = GGN;
    }

    public String getGSID() {
        return GSID;
    }

    public void setGSID(String GSID) {
        this.GSID = GSID;
    }

    public Boolean getNOVOLUME() {
        return NOVOLUME;
    }

    public void setNOVOLUME(Boolean NOVOLUME) {
        this.NOVOLUME = NOVOLUME;
    }
}
