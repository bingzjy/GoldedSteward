package com.ldnet.entities;

import android.text.TextUtils;

/**
 * Created by zjy on 2017/10/22
 */

public class GoodsForGroupPurchase {

    /**
     * GID : 623f2066285f40a5943f640c24d2972e
     * RID : 5bedc0af89bb4425aea17258a175d485
     * T : 闰土的声音
     * IMG : 497904137982445493db9aa0cec1d3ab
     * DS : MP3类
     * RP : 0
     * GP : 0
     * SN :
     * ST : 0
     * GSID : 528617aadd60428681006005005612a3
     * GSN : 茶爽类
     */

    public String GID;
    public String RID;
    public String T;
    public String IMG;
    public String DS;
    public String RP;
    public String GP;
    public String SN;
    public Integer ST;
    public String GSID;
    public String GSN;


    public Float FRP = (!TextUtils.isEmpty(RP) && RP.matches("[\\d]+\\.[\\d]+")) ? Float.valueOf(RP) : 0.0f;
    public Float FGP = (!TextUtils.isEmpty(GP) && GP.matches("[\\d]+\\.[\\d]+")) ? Float.valueOf(GP) : 0.0f;



    //获得封面
    public String getThumbnail() {
        if (!TextUtils.isEmpty(IMG)) {
            String[] ImageIds = IMG.split(",");
            if (ImageIds.length > 0) {
                return ImageIds[0];
            }
        }
        return null;
    }

}
