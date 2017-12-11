package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by zxs on 2016/1/25.
 */
public class CommunityGoodsType extends StatusBoolean implements Serializable {
    //    "ImgID":null,
//            "IsSystem":false,
//            "OrderBy":null,
//            "RetailerId":"b77ce82fc9e74087b671127c10fa5fea",
//            "Title":"酒水饮料",
//            "Created":"2015-12-12T15:48:42.327",
//            "Id":"2049277866b8461590707df32127c892",
//            "Updated":"2015-12-12T15:48:42.327"
    public String ImgID;
    public Boolean IsSystem;
    public String OrderBy;
    public String RetailerId;
    //分类的标题
    public String Title;
    public String Created;
    //商品分类的id
    public String Id;
    public String Updated;

    public String getImgID() {
        return ImgID;
    }

    public void setImgID(String imgID) {
        ImgID = imgID;
    }

    public Boolean getIsSystem() {
        return IsSystem;
    }

    public void setIsSystem(Boolean isSystem) {
        IsSystem = isSystem;
    }

    public String getOrderBy() {
        return OrderBy;
    }

    public void setOrderBy(String orderBy) {
        OrderBy = orderBy;
    }

    public String getRetailerId() {
        return RetailerId;
    }

    public void setRetailerId(String retailerId) {
        RetailerId = retailerId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getCreated() {
        return Created;
    }

    public void setCreated(String created) {
        Created = created;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUpdated() {
        return Updated;
    }

    public void setUpdated(String updated) {
        Updated = updated;
    }
}
