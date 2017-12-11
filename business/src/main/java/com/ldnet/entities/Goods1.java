package com.ldnet.entities;

import android.text.TextUtils;

import java.io.Serializable;

/* 商品 */
public class Goods1 implements Serializable {

    //商品ID
    public String GID;
    //商家ID
    public String RID;
    //商品名称
    public String T;
    //商品图片
    public String IMG;
    //零售价
    public String RP;
    //金牌价
    public String GP;
    //商品规格
    public String SN;
    //商品库存
    public Integer ST;
    //商品分类
    public String GSID;
    //商品分类名称
    public String GSN;
    public String DS;

    public Float FRP = (!TextUtils.isEmpty(RP) && RP.matches("[\\d]+\\.[\\d]+")) ? Float.valueOf(RP) : 0.0f;
    public Float FGP = (!TextUtils.isEmpty(GP) && GP.matches("[\\d]+\\.[\\d]+")) ? Float.valueOf(GP) : 0.0f;

    //获得封面
    public String getThumbnail() {
        if (!TextUtils.isEmpty(IMG)) {
            String[] ImageIds = IMG.split(",");
            if (ImageIds.length > 0) {
                return ImageIds[0];
            }
        }
        return null;
    }

    public String getGID() {
        return GID;
    }

    public void setGID(String GID) {
        this.GID = GID;
    }

    public String getRID() {
        return RID;
    }

    public void setRID(String RID) {
        this.RID = RID;
    }

    public String getT() {
        return T;
    }

    public void setT(String t) {
        T = t;
    }

    public String getIMG() {
        return IMG;
    }

    public void setIMG(String IMG) {
        this.IMG = IMG;
    }

    public String getRP() {
        return RP;
    }

    public void setRP(String RP) {
        this.RP = RP;
    }

    public String getGP() {
        return GP;
    }

    public void setGP(String GP) {
        this.GP = GP;
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public Integer getST() {
        return ST;
    }

    public void setST(Integer ST) {
        this.ST = ST;
    }

    public String getGSID() {
        return GSID;
    }

    public void setGSID(String GSID) {
        this.GSID = GSID;
    }

    public String getGSN() {
        return GSN;
    }

    public void setGSN(String GSN) {
        this.GSN = GSN;
    }

    public String getDS() {
        return DS;
    }

    public void setDS(String DS) {
        this.DS = DS;
    }

    public Float getFRP() {
        return FRP;
    }

    public void setFRP(Float FRP) {
        this.FRP = FRP;
    }

    public Float getFGP() {
        return FGP;
    }

    public void setFGP(Float FGP) {
        this.FGP = FGP;
    }
}
