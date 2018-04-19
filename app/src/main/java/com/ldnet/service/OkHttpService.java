package com.ldnet.service;

import com.ldnet.utility.sharepreferencedata.CookieInformation;
import com.ldnet.activity.commen.Services;
import com.ldnet.utility.sharepreferencedata.UserInformation;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.HashMap;

/**
 * Created by zjy on 2017/6/28.
 */
public class OkHttpService {

    public static RequestCall get(String url){
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;

        GetBuilder builder= OkHttpUtils.get().url(url);
        builder.addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo());
        builder.addHeader("phone", UserInformation.getUserInfo().getUserPhone());
        builder.addHeader("timestamp", aa);
        builder.addHeader("nonce", aa1);
        builder.addHeader("signature", Services.textToMD5L32(md5));
        return builder.build();
    }



    public static PostFormBuilder post(String url, HashMap params){
        Services.json(params);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + Services.json(params) + Services.TOKEN;

        PostFormBuilder builder=OkHttpUtils.post().url(url);
        builder.addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo());
        builder.addHeader("phone", UserInformation.getUserInfo().getUserPhone());
        builder.addHeader("timestamp", aa);
        builder.addHeader("nonce", aa1);
        builder.addHeader("signature", Services.textToMD5L32(md5));
        return builder;
    }



}
