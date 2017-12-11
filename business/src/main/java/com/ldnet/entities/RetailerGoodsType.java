package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by zxs on 2016/5/18.
 */
public class RetailerGoodsType implements Serializable {
    //    "ImgID":null,
//            "IsSystem":false,
//            "IsYGoods":false,
//            "OrderBy":null,
//            "RetailerId":"087f75305e2448558bb97cc5bf7aslbb",
//            "Title":"零食饼干",
//            "Created":"2016-04-19T10:54:23.637",
//            "Id":"1368eac619e543c584fe17d88bb8c6d0",
//            "Updated":"2016-04-19T10:54:23.637"
    public String ImgID;//
    public String IsSystem;//
    public String IsYGoods;//分类下是否有商品，没有不显示该分类
    public String OrderBy;//
    public String RetailerId;//
    public String Title;//分类标题
    public String Created;//
    public String Id;//分类id
    public String Updated;//

    public String getImgID() {
        return ImgID;
    }

    public void setImgID(String imgID) {
        ImgID = imgID;
    }

    public String getIsSystem() {
        return IsSystem;
    }

    public void setIsSystem(String isSystem) {
        IsSystem = isSystem;
    }

    public String getIsYGoods() {
        return IsYGoods;
    }

    public void setIsYGoods(String isYGoods) {
        IsYGoods = isYGoods;
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
