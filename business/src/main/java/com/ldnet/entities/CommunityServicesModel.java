package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by lee on 2016/8/10.
 */
public class CommunityServicesModel implements Serializable {

    private String Id;
    private String Name;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
