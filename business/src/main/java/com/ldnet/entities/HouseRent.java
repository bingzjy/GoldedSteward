package com.ldnet.entities;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by Murray on 2015/9/10.
 */
public class HouseRent implements Serializable {

    public String Id;
    public String CommunityId;
    public String Title;
    public String Abstract;
    public String Room;
    public String Hall;
    public String Toilet;
    public String Acreage;
    public String Floor;
    public String FloorCount;
    public String Orientation;
    public String FitmentType;
    public String RoomType;
    public String RoomDeploy;
    public String Price;
    public String RentType;
    public String Images;
    public String ContactTel;
    public String Address;
    public String Elevator;
    public String Status;
    public String IsRental;
    public String Reject;

    //获得封面
    public String getThumbnail() {
        if (!TextUtils.isEmpty(Images)) {
            String[] ImageIds = Images.split(",");
            if (ImageIds.length > 0) {
                return ImageIds[0];
            }
        }
        return null;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getCommunityId() {
        return CommunityId;
    }

    public void setCommunityId(String communityId) {
        CommunityId = communityId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getAbstract() {
        return Abstract;
    }

    public void setAbstract(String anAbstract) {
        Abstract = anAbstract;
    }

    public String getRoom() {
        return Room;
    }

    public void setRoom(String room) {
        Room = room;
    }

    public String getHall() {
        return Hall;
    }

    public void setHall(String hall) {
        Hall = hall;
    }

    public String getToilet() {
        return Toilet;
    }

    public void setToilet(String toilet) {
        Toilet = toilet;
    }

    public String getAcreage() {
        return Acreage;
    }

    public void setAcreage(String acreage) {
        Acreage = acreage;
    }

    public String getFloor() {
        return Floor;
    }

    public void setFloor(String floor) {
        Floor = floor;
    }

    public String getFloorCount() {
        return FloorCount;
    }

    public void setFloorCount(String floorCount) {
        FloorCount = floorCount;
    }

    public String getOrientation() {
        return Orientation;
    }

    public void setOrientation(String orientation) {
        Orientation = orientation;
    }

    public String getFitmentType() {
        return FitmentType;
    }

    public void setFitmentType(String fitmentType) {
        FitmentType = fitmentType;
    }

    public String getRoomType() {
        return RoomType;
    }

    public void setRoomType(String roomType) {
        RoomType = roomType;
    }

    public String getRoomDeploy() {
        return RoomDeploy;
    }

    public void setRoomDeploy(String roomDeploy) {
        RoomDeploy = roomDeploy;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getRentType() {
        return RentType;
    }

    public void setRentType(String rentType) {
        RentType = rentType;
    }

    public String getImages() {
        return Images;
    }

    public void setImages(String images) {
        Images = images;
    }

    public String getContactTel() {
        return ContactTel;
    }

    public void setContactTel(String contactTel) {
        ContactTel = contactTel;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getElevator() {
        return Elevator;
    }

    public void setElevator(String elevator) {
        Elevator = elevator;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getIsRental() {
        return IsRental;
    }

    public void setIsRental(String isRental) {
        IsRental = isRental;
    }

    public String getReject() {
        return Reject;
    }

    public void setReject(String reject) {
        Reject = reject;
    }
}
