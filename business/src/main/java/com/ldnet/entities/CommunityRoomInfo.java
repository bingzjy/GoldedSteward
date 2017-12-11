package com.ldnet.entities;

/**
 * Created by lee on 2017/10/10
 */

public class CommunityRoomInfo {
    private String communityID;
    private String communityName;
    private String roomID;
    private String roomName;

    public CommunityRoomInfo(String communityID, String communityName, String roomID, String roomName) {
        this.communityID = communityID;
        this.communityName = communityName;
        this.roomID = roomID;
        this.roomName = roomName;
    }

    public String getCommunityID() {
        return communityID;
    }

    public void setCommunityID(String communityID) {
        this.communityID = communityID;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
