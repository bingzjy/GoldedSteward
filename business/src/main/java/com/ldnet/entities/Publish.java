package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by Alex on 2015/9/16.
 */
public class Publish implements Serializable {
    //ID
    public String Id;
    //标题
    public String Title;
    //发布时间
    public String DateTime;
    //发布类型  房屋租赁/闲置物品/周末去哪
    public String Type;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
