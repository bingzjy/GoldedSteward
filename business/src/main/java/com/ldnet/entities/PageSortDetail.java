package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by Murray on 2015/10/13.
 */
public class PageSortDetail implements Serializable {
    public String Id;
    public String Updated;
    public String Created;
    public String Title;
    public String Memo;
    public String Address;
    public String Contact;
    public String Tel;
    public String Latitude;
    public String Longitude;
    public String CommunityId;
    public String CommunityName;
    public String SortId;
    public String SortName;
    public Integer Distance;//单位为:米

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUpdated() {
        return Updated;
    }

    public void setUpdated(String updated) {
        Updated = updated;
    }

    public String getCreated() {
        return Created;
    }

    public void setCreated(String created) {
        Created = created;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMemo() {
        return Memo;
    }

    public void setMemo(String memo) {
        Memo = memo;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getTel() {
        return Tel;
    }

    public void setTel(String tel) {
        Tel = tel;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

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

    public String getSortId() {
        return SortId;
    }

    public void setSortId(String sortId) {
        SortId = sortId;
    }

    public String getSortName() {
        return SortName;
    }

    public void setSortName(String sortName) {
        SortName = sortName;
    }

    public Integer getDistance() {
        return Distance;
    }

    public void setDistance(Integer distance) {
        Distance = distance;
    }
}
