package com.ldnet.entities;

import com.ldnet.utility.UserInformation;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Alex on 2015/9/1.
 */
public class MyProperties implements Serializable {
    public String CommunityId;
    public String Address;
    public String Name;
    public List<com.ldnet.entities.Rooms> Rooms;
    public boolean Default;

    public Boolean IsDefalut() {
        if (UserInformation.getUserInfo().CommunityId.equals(CommunityId)) {
            return true;
        }
        return false;
    }

    public String getCommunityId() {
        return CommunityId;
    }

    public void setCommunityId(String communityId) {
        CommunityId = communityId;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public List<com.ldnet.entities.Rooms> getRooms() {
        return Rooms;
    }

    public void setRooms(List<com.ldnet.entities.Rooms> rooms) {
        Rooms = rooms;
    }
}

