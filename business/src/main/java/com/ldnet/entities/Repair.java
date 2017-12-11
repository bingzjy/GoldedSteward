package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by lee on 2016/8/2.
 */
public class Repair implements Serializable {

    public String CommunityId;
    public String CommunityName;
    public String Content;
    public String ContentImg;
    public String CreateDay;
    public String ID;
    public String IsScore;
    public String LastOptionDay;
    public String Name;
    public String NodesID;
    public String NodesName;
    public String OrderNumber;
    public String ResidentId;
    public String RoomId;
    public String RoomName;
    public String Rtype;
    public String RtypeName;
    public String Tel;

    public String getCommunityId() {
        return CommunityId;
    }

    public void setCommunityId(String communityId) {
        CommunityId = communityId;
    }

    public String getCommunityName() {
        return CommunityName;
    }

    public void setCommunityName(String communityName) {
        CommunityName = communityName;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getContentImg() {
        return ContentImg;
    }

    public void setContentImg(String contentImg) {
        ContentImg = contentImg;
    }

    public String getCreateDay() {
        return CreateDay;
    }

    public void setCreateDay(String createDay) {
        CreateDay = createDay;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getIsScore() {
        return IsScore;
    }

    public void setIsScore(String isScore) {
        IsScore = isScore;
    }

    public String getLastOptionDay() {
        return LastOptionDay;
    }

    public void setLastOptionDay(String lastOptionDay) {
        LastOptionDay = lastOptionDay;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNodesID() {
        return NodesID;
    }

    public void setNodesID(String nodesID) {
        NodesID = nodesID;
    }

    public String getNodesName() {
        return NodesName;
    }

    public void setNodesName(String nodesName) {
        NodesName = nodesName;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }

    public String getResidentId() {
        return ResidentId;
    }

    public void setResidentId(String residentId) {
        ResidentId = residentId;
    }

    public String getRoomId() {
        return RoomId;
    }

    public void setRoomId(String roomId) {
        RoomId = roomId;
    }

    public String getRoomName() {
        return RoomName;
    }

    public void setRoomName(String roomName) {
        RoomName = roomName;
    }

    public String getRtype() {
        return Rtype;
    }

    public void setRtype(String rtype) {
        Rtype = rtype;
    }

    public String getRtypeName() {
        return RtypeName;
    }

    public void setRtypeName(String rtypeName) {
        RtypeName = rtypeName;
    }

    public String getTel() {
        return Tel;
    }

    public void setTel(String tel) {
        Tel = tel;
    }
}
