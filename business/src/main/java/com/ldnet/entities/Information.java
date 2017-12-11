package com.ldnet.entities;

import java.io.Serializable;

/*资讯*/
public class Information implements Serializable {
    //ID
    public String ID;
    //点击预览页面
    public String InfoUrl;
    //标题
    public String Title;
    //预览次数
    public Integer PreviewCnt;
    //描述
    public String Description;
    //发布时间
    public String ReleaseDate;
    //图片
    public String TitleImageID;
    //资讯分类
    public String TypeName;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getInfoUrl() {
        return InfoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        InfoUrl = infoUrl;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public Integer getPreviewCnt() {
        return PreviewCnt;
    }

    public void setPreviewCnt(Integer previewCnt) {
        PreviewCnt = previewCnt;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getReleaseDate() {
        return ReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        ReleaseDate = releaseDate;
    }

    public String getTitleImageID() {
        return TitleImageID;
    }

    public void setTitleImageID(String titleImageID) {
        TitleImageID = titleImageID;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }
}
