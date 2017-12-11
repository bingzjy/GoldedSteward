package com.ldnet.entities;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zxs on 2016/3/1.
 */
public class CommunityServicesDetails implements Serializable {


    /**
     * Id : 0a91a4c486e64fbc9f981e4177b38a54
     * Title : 测试3
     * Memo : 阿甘萨嘎sag
     * Address : 陕西省西安市莲湖区青年路街道药王洞29号北洞巷小区
     * Images : c4371ea255f74961bad81fadc15f687e
     * Latitude : 34.274084
     * Longitude : 108.941014
     * Tel_Count : 0
     * Phone : 65156161616
     * Tel : 1515851485
     * ActivityTitle : 现在不行
     * ActivityImages : 86da00b233a8465499d105ae5b6123d9
     * ActivityUrl : http://192.168.0.105:8085/Mobile/HAD?id=0a91a4c486e64fbc9f981e4177b38a54
     * Item : [{"Id":"ff3f5f957e0742c095293d30c1f240d0","Name":"48964496","Cost":"488489"}]
     */

    public String Id;
    public String Title;
    public String Memo;
    public String Address;
    public String Images;
    public String Latitude;
    public String Longitude;
    public int Tel_Count;
    public String Phone;
    public String Tel;
    public String ActivityTitle;
    public String ActivityImages;
    public String ActivityUrl;
    public List<Item> Item;


    public String getThumbnail() { //优先展示活动的缩略图
        if (!TextUtils.isEmpty(ActivityImages)) {
            String[] ImageIds = ActivityImages.split(",");
            if (ImageIds.length > 0) {
                return ImageIds[0];
            }
        }
        if (!TextUtils.isEmpty(Images)){
            String [] images=Images.split(",");
            if (images.length>0){
                return images[0];
            }
        }
        return null;
    }

}
