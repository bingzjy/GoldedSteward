package com.ldnet.entities;

/**
 * Created by zxs on 2015/12/9.
 */
public class APPHomePage_Column {
    /**
     * AID : ebcf6a389bc84b72965d790453bebc8e
     * CREATEDAY : 2016-08-15T15:10:00.397
     * DESCRIPTION : 2311
     * DESCRIPTIONCOLOR : #cdcdcd
     * GOODSID : null
     * GOODSTYPEID :
     * GOODSTYPE_SELID :
     * GoodsUrl : null
     * ID : 9f5d9e0106694ce1920775c7528fea37
     * IMGID : c07e9502c30c4c6fbf7763e92728c4e2
     * ImgHeightPro : 0.66
     * ImgPosition : 1
     * ImgWidthPro : 20
     * ORDERBY : 1
     * RID : aa36630195af47b59f23c1943d8a232e
     * SHOWTITLE : true
     * TITLE : 132
     * TITLECOLOR : #000000
     * TYPES : 3
     * URL : 231321
     */

    public String AID;
    public String CREATEDAY;
    public String DESCRIPTION;
    public String DESCRIPTIONCOLOR;
    public Object GOODSID;
    public String GOODSTYPEID;
    public String GOODSTYPE_SELID;
    public Object GoodsUrl;
    public String ID;
    public String IMGID;
    public double ImgHeightPro;
    public int ImgPosition;
    public double ImgWidthPro;
    public int ORDERBY;
    public String RID;
    public boolean SHOWTITLE;
    public String TITLE;
    public String TITLECOLOR;
    public int TYPES;
    public String URL;


    public Object getGoodsUrl() {
        return GoodsUrl;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getIMGID() {
        return IMGID;
    }


    public String getRID() {
        return RID;
    }

    public void setRID(String RID) {
        this.RID = RID;
    }

    public String getTITLE() {
        return TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }


    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

}
