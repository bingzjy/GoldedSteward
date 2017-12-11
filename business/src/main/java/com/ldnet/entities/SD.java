package com.ldnet.entities;

/**
 * Created by Alex on 2015/9/28.
 */
public class SD {

    //子表ID
    public String ID;
    //购物车ID
    public String SID;
    //商品ID
    public String GID;
    //商品名称
    public String GN;
    //商品分组名称
    public String GTN;
    //商品图片
    public String GI;
    //规格ID
    public String GGID;
    //商品规格名称
    public String GGN;
    //商品单价
    public Float GP;
    //金牌单价
    public Float GGP;
    //商品数量
    public Integer N;
    //商品是否已不存在 true-不存在
    public Boolean ISNU = false;
    //商品是否已下架
    public Boolean ISXJ = false;
    //商品是否已更改
    public Boolean ISUD = false;
    //商品是否库存不足
    public Boolean ISKC = false;
    //是否被选中
    public Boolean IsChecked = true;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSID() {
        return SID;
    }

    public void setSID(String SID) {
        this.SID = SID;
    }

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

    public String getGTN() {
        return GTN;
    }

    public void setGTN(String GTN) {
        this.GTN = GTN;
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

    public String getGGN() {
        return GGN;
    }

    public void setGGN(String GGN) {
        this.GGN = GGN;
    }

    public Float getGP() {
        return GP;
    }

    public void setGP(Float GP) {
        this.GP = GP;
    }

    public Float getGGP() {
        return GGP;
    }

    public void setGGP(Float GGP) {
        this.GGP = GGP;
    }

    public Integer getN() {
        return N;
    }

    public void setN(Integer n) {
        N = n;
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

    public Boolean getISKC() {
        return ISKC;
    }

    public void setISKC(Boolean ISKC) {
        this.ISKC = ISKC;
    }

    public Boolean getIsChecked() {
        return IsChecked;
    }

    public void setIsChecked(Boolean isChecked) {
        IsChecked = isChecked;
    }
}
