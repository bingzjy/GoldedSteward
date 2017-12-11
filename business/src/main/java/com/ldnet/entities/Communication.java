package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by Alex on 2015/9/10.
 */
public class Communication implements Serializable {
    public String Id;
    public Boolean SendType;
    public String Img;
    public String Name;
    public String Content;
    public String Type;
    public String Created;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public Boolean getSendType() {
        return SendType;
    }

    public void setSendType(Boolean sendType) {
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
