package com.ldnet.activity.adapter;

import com.ldnet.entities.Repair_Complain_Status;

public class TimeBean {
    private String SendType;
    private String Img;
    private String Name;
    private String Content;
    private String Type;
    private String Created;

    public TimeBean() {

    }

    public TimeBean(String sendType, String img, String name, String content, String type, String created) {
        SendType = sendType;
        Img = img;
        Name = name;
        Content = content;
        Type = type;
        Created = created;
    }

    public String getSendType() {
        return SendType;
    }

    public void setSendType(String sendType) {
        SendType = sendType;
    }

    public String getImg() {
        return Img;
    }

    public void setImg(String img) {
        Img = img;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getCreated() {
        return Created;
    }

    public void setCreated(String created) {
        Created = created;
    }
}
