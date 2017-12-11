package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by zxs on 2016/4/5.
 */
public class Coupon implements Serializable {
    //    "BeginTime":"2016-04-01T00:00:00",
//            "CreateDay":"2016-04-13T14:22:55.793",
//            "EndTime":"2016-06-29T00:00:00",
//            "FullMoney":100,
//            "Goodstypes":null,
//            "ID":"6eeca609945c494e81fcb19bd099cca5",
//            "IsForeign":true,
//            "IsOverdue":false,
//            "IsReceive":false,
//            "IsVoid":false,
//            "MainTypeID":2,
//            "MainTypeName":"商家卷",
//            "Number":1,
//            "RangeTypeID":1,
//            "RangeTypeName":"全品类",
//            "ReduceMoney":10,
//            "Retailer":"e1b568533e1849a9b068359a6de23b9f",
//            "Title":"6-1儿童节日大促销",
//            "TypeID":1,
//            "TypeName":"满减卷",
// "XXRLQ":0,
//    "XXRSY":0,
//            "volume_reange_goodstype":null
    public String BeginTime;
    public String CreateDay;
    public String EndTime;
    public Float FullMoney;
    public Integer Goodstypes;
    public String ID;
    public Boolean IsForeign;
    public Boolean IsOverdue;
    public Boolean IsReceive;
    public Boolean IsVoid;
    public Integer MainTypeID;
    public String MainTypeName;
    public Integer Number;
    public Integer RangeTypeID;
    public String RangeTypeName;
    public Float ReduceMoney;
    public String Retailer;
    public String Title;
    public Integer TypeID;
    public String TypeName;
    public Integer volume_reange_goodstype;

    //以下字段在我的优惠劵中才有
//    "IsAvailable":false,
//            "IsUse":false,
//            "RETAILERNAME":"GMY",
//            "ReceiveDay":"2016-04-18T10:06:17.187",
//            "ResidentID":"b17fbe548fc6473cb1037f84659ba4c1",
//            "UseDay":"0001-01-01T00:00:00",
//            "VolumeID":"6eeca609945c494e81fcb19bd099cca5",
    //  优惠劵是否可用
    public Boolean IsAvailable;
    //  是否使用优惠劵
    public Boolean IsUse;
    // 零售商名字
    public String RETAILERNAME;
    // 领取优惠劵时间
    public String ReceiveDay;
    // 商家id
    public String ResidentID;
    // 优惠劵使用时间
    public String UseDay;
    // 优惠劵id
    public String VolumeID;

    public Boolean IsChecked;

    public String getBeginTime() {
        return BeginTime;
    }

    public void setBeginTime(String beginTime) {
        BeginTime = beginTime;
    }

    public String getCreateDay() {
        return CreateDay;
    }

    public void setCreateDay(String createDay) {
        CreateDay = createDay;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public Float getFullMoney() {
        return FullMoney;
    }

    public void setFullMoney(Float fullMoney) {
        FullMoney = fullMoney;
    }

    public Integer getGoodstypes() {
        return Goodstypes;
    }

    public void setGoodstypes(Integer goodstypes) {
        Goodstypes = goodstypes;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Boolean getIsForeign() {
        return IsForeign;
    }

    public void setIsForeign(Boolean isForeign) {
        IsForeign = isForeign;
    }

    public Boolean getIsOverdue() {
        return IsOverdue;
    }

    public void setIsOverdue(Boolean isOverdue) {
        IsOverdue = isOverdue;
    }

    public Boolean getIsReceive() {
        return IsReceive;
    }

    public void setIsReceive(Boolean isReceive) {
        IsReceive = isReceive;
    }

    public Boolean getIsVoid() {
        return IsVoid;
    }

    public void setIsVoid(Boolean isVoid) {
        IsVoid = isVoid;
    }

    public Integer getMainTypeID() {
        return MainTypeID;
    }

    public void setMainTypeID(Integer mainTypeID) {
        MainTypeID = mainTypeID;
    }

    public String getMainTypeName() {
        return MainTypeName;
    }

    public void setMainTypeName(String mainTypeName) {
        MainTypeName = mainTypeName;
    }

    public Integer getNumber() {
        return Number;
    }

    public void setNumber(Integer number) {
        Number = number;
    }

    public Integer getRangeTypeID() {
        return RangeTypeID;
    }

    public void setRangeTypeID(Integer rangeTypeID) {
        RangeTypeID = rangeTypeID;
    }

    public String getRangeTypeName() {
        return RangeTypeName;
    }

    public void setRangeTypeName(String rangeTypeName) {
        RangeTypeName = rangeTypeName;
    }

    public Float getReduceMoney() {
        return ReduceMoney;
    }

    public void setReduceMoney(Float reduceMoney) {
        ReduceMoney = reduceMoney;
    }

    public String getRetailer() {
        return Retailer;
    }

    public void setRetailer(String retailer) {
        Retailer = retailer;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public Integer getTypeID() {
        return TypeID;
    }

    public void setTypeID(Integer typeID) {
        TypeID = typeID;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }

    public Integer getVolume_reange_goodstype() {
        return volume_reange_goodstype;
    }

    public void setVolume_reange_goodstype(Integer volume_reange_goodstype) {
        this.volume_reange_goodstype = volume_reange_goodstype;
    }

    public Boolean getIsAvailable() {
        return IsAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        IsAvailable = isAvailable;
    }

    public Boolean getIsUse() {
        return IsUse;
    }

    public void setIsUse(Boolean isUse) {
        IsUse = isUse;
    }

    public String getRETAILERNAME() {
        return RETAILERNAME;
    }

    public void setRETAILERNAME(String RETAILERNAME) {
        this.RETAILERNAME = RETAILERNAME;
    }

    public String getResidentID() {
        return ResidentID;
    }

    public void setResidentID(String residentID) {
        ResidentID = residentID;
    }

    public String getVolumeID() {
        return VolumeID;
    }

    public void setVolumeID(String volumeID) {
        VolumeID = volumeID;
    }

    public Boolean getIsChecked() {
        return IsChecked;
    }

    public void setIsChecked(Boolean isChecked) {
        IsChecked = isChecked;
    }
}
