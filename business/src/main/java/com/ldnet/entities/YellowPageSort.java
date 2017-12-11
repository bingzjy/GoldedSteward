package com.ldnet.entities;

import java.io.Serializable;

/**
 * Created by Murray on 2015/10/12.
 */
public class YellowPageSort implements Serializable {
    public String Icon;
    public String Level;
    public String Memo;
    public String OrderBy;
    public String Title;
    public String ParentId;
    public String Community_Id;
    public String Community_Name;
    public String Created;
    public String Id;
    public String Updated;
    public String Keywords;
    public String Types;

    public String getIcon() {
        return Icon;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }

    public String getMemo() {
        return Memo;
    }

    public void setMemo(String memo) {
        Memo = memo;
    }

    public String getOrderBy() {
        return OrderBy;
    }

    public void setOrderBy(String orderBy) {
        OrderBy = orderBy;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getParentId() {
        return ParentId;
    }

    public void setParentId(String parentId) {
        ParentId = parentId;
    }

    public String getCommunity_Id() {
        return Community_Id;
    }

    public void setCommunity_Id(String community_Id) {
        Community_Id = community_Id;
    }

    public String getCommunity_Name() {
        return Community_Name;
    }

    public void setCommunity_Name(String community_Name) {
        Community_Name = community_Name;
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

    public String getKeywords() {
        return Keywords;
    }

    public void setKeywords(String keywords) {
        Keywords = keywords;
    }

    public String getTypes() {
        return Types;
    }

    public void setTypes(String types) {
        Types = types;
    }
}
