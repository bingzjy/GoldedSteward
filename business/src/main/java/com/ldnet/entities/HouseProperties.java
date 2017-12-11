package com.ldnet.entities;


import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Murray on 2015/9/9.
 */
public class HouseProperties implements Serializable {
    public String Orientation;
    public String FitmentType;
    public String RoomType;
    public String RoomDeploy;
    public String RentType;

    //
    public List<KValues> getOrientation() throws Exception {
        List<KValues> maps = new ArrayList<KValues>();
        JSONObject jsonObject = new JSONObject(Orientation);
        maps.add(new KValues("-1", "朝向"));
        for (int i = 0; i < 10; i++) {
            KValues values = new KValues();
            values.Key = String.valueOf(i);
            values.Value = jsonObject.getString(i + "");
            maps.add(values);
        }
        return maps;
    }

    public List<KValues> getFitmentType() throws Exception {
        List<KValues> maps = new ArrayList<KValues>();
        maps.add(new KValues("-1", "装修情况"));
        JSONObject jsonObject = new JSONObject(FitmentType);
        for (int i = 0; i < 5; i++) {
            KValues values = new KValues();
            values.Key = String.valueOf(i);
            values.Value = jsonObject.getString(i + "");
            maps.add(values);
        }
        return maps;
    }

    public List<KValues> getRoomType() throws Exception {
        List<KValues> maps = new ArrayList<KValues>();
        maps.add(new KValues("-1", "住宅类型"));
        JSONObject jsonObject = new JSONObject(RoomType);
        for (int i = 0; i < 6; i++) {
            KValues values = new KValues();
            values.Key = String.valueOf(i);
            values.Value = jsonObject.getString(i + "");
            maps.add(values);
        }
        return maps;
    }

    public List<KValues> getRoomDeploy() throws Exception {
        List<KValues> maps = new ArrayList<KValues>();
        maps.add(new KValues("-1", "房屋配置"));
        JSONObject jsonObject = new JSONObject(RoomDeploy);
        for (int i = 0; i < 2; i++) {
            KValues values = new KValues();
            values.Key = String.valueOf(i);
            values.Value = jsonObject.getString(i + "");
            maps.add(values);
        }
        return maps;
    }

    public List<KValues> getRentType() throws Exception {
        List<KValues> maps = new ArrayList<KValues>();
        maps.add(new KValues("-1", "租金类型"));
        JSONObject jsonObject = new JSONObject(RentType);
        for (int i = 0; i < 12; i++) {
            KValues values = new KValues();
            values.Key = String.valueOf(i);
            values.Value = jsonObject.getString(i + "");
            maps.add(values);
        }
        return maps;
    }

    public void setOrientation(String orientation) {
        Orientation = orientation;
    }

    public void setFitmentType(String fitmentType) {
        FitmentType = fitmentType;
    }

    public void setRoomType(String roomType) {
        RoomType = roomType;
    }

    public void setRoomDeploy(String roomDeploy) {
        RoomDeploy = roomDeploy;
    }

    public void setRentType(String rentType) {
        RentType = rentType;
    }
}
