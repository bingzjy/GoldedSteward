package com.ldnet.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lee on 2016/7/1.
 */
public class HouseProperties1 implements Serializable {
    public List<com.ldnet.entities.Orientation> Orientation;
    public List<FitmentType> FitmentType;
    public List<com.ldnet.entities.RoomType> RoomType;
    public List<com.ldnet.entities.RoomDeploy> RoomDeploy;
    public List<com.ldnet.entities.RentType> RentType;

    public List<com.ldnet.entities.Orientation> getOrientation() {
        return Orientation;
    }

    public void setOrientation(List<com.ldnet.entities.Orientation> orientation) {
        Orientation = orientation;
    }

    public List<FitmentType> getFitmentType() {
        return FitmentType;
    }

    public void setFitmentType(List<FitmentType> fitmentType) {
        FitmentType = fitmentType;
    }

    public List<com.ldnet.entities.RoomType> getRoomType() {
        return RoomType;
    }

    public void setRoomType(List<com.ldnet.entities.RoomType> roomType) {
        RoomType = roomType;
    }

    public List<com.ldnet.entities.RoomDeploy> getRoomDeploy() {
        return RoomDeploy;
    }

    public void setRoomDeploy(List<com.ldnet.entities.RoomDeploy> roomDeploy) {
        RoomDeploy = roomDeploy;
    }

    public List<com.ldnet.entities.RentType> getRentType() {
        return RentType;
    }

    public void setRentType(List<com.ldnet.entities.RentType> rentType) {
        RentType = rentType;
    }
}
