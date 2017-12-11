package com.ldnet.entities;

import java.io.Serializable;

//小区
public class Community implements Serializable,
        Comparable<Community> {

    /* 类序列化的标识？？？ */
    private static final long serialVersionUID = -6675548012036858654L;

    // 服务器中的社区ID
    public String ID;

    // 高德、百度等地图上点的唯一标识 --过期
    public String Uid;

    // 名称
    public String Name;

    // 是否物业入住
    public Boolean IsProperty;

    // 地址
    public String Address;

    // 电话 --过期
    public String Tel;

    // 经度 --过期
    public String Longitude;

    // 纬度 --过期
    public String Latitude;

    // 离中心点的距离 --过期
    public Double Distance;

    // 比较距离远近，时间比较接口 --过期
    @Override
    public int compareTo(Community another) {
        Community c = (Community) another;
        return this.Distance.compareTo(c.Distance);
    }

    //小区所在城市编码 --过期
    public String CityCode;

    //小区所在区域编码 --过期
    public String AreaId;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Boolean getIsProperty() {
        return IsProperty;
    }

    public void setIsProperty(Boolean isProperty) {
        IsProperty = isProperty;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getTel() {
        return Tel;
    }

    public void setTel(String tel) {
        Tel = tel;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public Double getDistance() {
        return Distance;
    }

    public void setDistance(Double distance) {
        Distance = distance;
    }

    public String getCityCode() {
        return CityCode;
    }

    public void setCityCode(String cityCode) {
        CityCode = cityCode;
    }

    public String getAreaId() {
        return AreaId;
    }

    public void setAreaId(String areaId) {
        AreaId = areaId;
    }
}
