package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by Alex on 2015/9/16.
 */
public class Weekend implements Serializable {
    //ID
    public String Id;
    //标题
    public String Title;
    //状态 ，0 - 未开始 1 - 进行中 2 - 已结束
    public String Status;
    //费用
    public Float Cost;
    //所在城市
    public String CityName;
    //封面
    public String Cover;
    //报名人数
    public Integer MemberCount;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public Float getCost() {
        return Cost;
    }

    public void setCost(Float cost) {
        Cost = cost;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public String getCover() {
        return Cover;
    }

    public void setCover(String cover) {
        Cover = cover;
    }

    public Integer getMemberCount() {
        return MemberCount;
    }

    public void setMemberCount(Integer memberCount) {
        MemberCount = memberCount;
    }
}
