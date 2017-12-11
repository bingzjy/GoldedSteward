package com.ldnet.entities;

import java.io.Serializable;

/*资讯*/
public class InformationType implements Serializable {
    //ID
    public String ID;
    //标题
    public String Title;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}
