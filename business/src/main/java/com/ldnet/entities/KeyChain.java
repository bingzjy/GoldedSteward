package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by lee on 2017/4/26.
 */
public class KeyChain implements Serializable {


    private String Password;//设备密码
    private String Id;//设备ID
    private String CommunityNo;//小区编号
    private String BuildingNo;//楼栋编号
    private String Type;//门禁类型，0-公共门，1-单元门

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getCommunityNo() {
        return CommunityNo;
    }

    public void setCommunityNo(String communityNo) {
        CommunityNo = communityNo;
    }

    public String getBuildingNo() {
        return BuildingNo;
    }

    public void setBuildingNo(String buildingNo) {
        BuildingNo = buildingNo;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }



}
