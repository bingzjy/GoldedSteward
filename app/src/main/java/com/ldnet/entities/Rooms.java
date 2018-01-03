package com.ldnet.entities;

import android.util.Log;
import com.ldnet.utility.UserInformation;

/**
 * Created by Alex on 2015/9/1.
 */
public class Rooms {
    public String RoomId;
    public String Abbreviation;
    public boolean Default;

    public Boolean IsDefalut() {
        if (UserInformation.getUserInfo().getHouseId().equals(getRoomId())) {
            return true;
        }
        return false;
    }

    public String getRoomId() {
        return RoomId;
    }

    public void setRoomId(String roomId) {
        RoomId = roomId;
    }

    public String getAbbreviation() {
        return Abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        Abbreviation = abbreviation;
    }
}
