package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by lee on 2016/7/27.
 */
public class MessageType implements Serializable {
    /**
     * PushTypeName : 物业消息
     * Content : 新通知
     * Created : 2017-10-31 14:30:00
     * PushType : 0
     * Image :
     */

    private String PushTypeName;
    private String Content;
    private String Created;
    private int PushType;
    private String Image;
    public boolean Pushing;

    public String getPushTypeName() {
        return PushTypeName;
    }

    public void setPushTypeName(String PushTypeName) {
        this.PushTypeName = PushTypeName;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }

    public String getCreated() {
        return Created;
    }

    public void setCreated(String Created) {
        this.Created = Created;
    }

    public int getPushType() {
        return PushType;
    }

    public void setPushType(int PushType) {
        this.PushType = PushType;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String Image) {
        this.Image = Image;
    }


//    protected String Title;
//    protected String Content;
//    protected String Created;
//    protected String Type;//0-物业消息
//    protected String Image;
//
//    public String getTitle() {
//        return Title;
//    }
//
//    public void setTitle(String title) {
//        Title = title;
//    }
//
//    public String getContent() {
//        return Content;
//    }
//
//    public void setContent(String content) {
//        Content = content;
//    }
//
//    public String getCreated() {
//        return Created;
//    }
//
//    public void setCreated(String created) {
//        Created = created;
//    }
//
//    public String getType() {
//        return Type;
//    }
//
//    public void setType(String type) {
//        Type = type;
//    }
//
//    public String getImage() {
//        return Image;
//    }
//
//    public void setImage(String image) {
//        Image = image;
//    }
}
