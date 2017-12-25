package com.ldnet.service;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.entities.Address;
import com.ldnet.entities.AddressSimple;
import com.ldnet.entities.Areas;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.CookieInformation;
import com.ldnet.utility.DataCallBack;
import com.ldnet.utility.ListViewAdapter;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.ldnet.utility.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import okhttp3.Call;
/**
 * Created by lee on 2017/11/8
 */

public class AddressService extends BaseService {

    private String tag=AddressService.this.getClass().getSimpleName();
    public AddressService(Context context) {
        this.mContext=context;
    }

    //获取省
    public void getProvinces(final Handler handler){
        // 请求的URL
        String url = Services.mHost + "API/Common/GetProvince";
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext,handler) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call,e,i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag,"getProvinces:"+s);
                        super.onResponse(s, i);
                        try {
                            JSONObject json = new JSONObject(s);

                            if (checkJsonData(s,handler)){
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));

                                if (jsonObject.optBoolean("Valid")){
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<Areas>>() {
                                    }.getType();

                                    List<Areas> data=gson.fromJson(jsonObject.getString("Obj"), type);
                                    if (data!=null&&data.size()>0){
                                        Message msg=handler.obtainMessage();
                                        msg.what=DATA_SUCCESS;
                                        msg.obj=data;
                                        handler.sendMessage(msg);
                                    }
                                }else{
                                    sendErrorMessage(handler,jsonObject);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //获取市
    public void getCities(final Integer provinceId, final Handler handler) {
        // 请求的URL
        String url = Services.mHost + "API/Common/GetCity/%s";
        url = String.format(url, provinceId);
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext, handler) {
                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag, "getCities:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s,handler)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.optBoolean("Valid")){
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<Areas>>() {
                                    }.getType();
                                    List<Areas> data = gson.fromJson(jsonObject.getString("Obj"), type);
                                    if (data!=null&&data.size()>0){
                                        Message msg=handler.obtainMessage();
                                        msg.obj=data;
                                        msg.what=DATA_SUCCESS;
                                        handler.sendMessage(msg);
                                    }else{
                                        handler.sendEmptyMessage(DATA_SUCCESS_OTHER);
                                    }
                                }else{
                                    sendErrorMessage(handler,jsonObject);
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //获取地区
    public void getAreas(final Integer cityId, final Handler handler) {
        // 请求的URL
        String url = Services.mHost + "API/Common/GetArea/%s";
        url = String.format(url, cityId);
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext,handler) {
                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag, "getAreas:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s,handler)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.optBoolean("Valid")){
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<Areas>>() {
                                    }.getType();
                                    List<Areas> data = gson.fromJson(jsonObject.getString("Obj"), type);
                                    if (data!=null&&data.size()>0){
                                        Message msg=handler.obtainMessage();
                                        msg.obj=data;
                                        msg.what=DATA_SUCCESS;
                                        handler.sendMessage(msg);
                                    }else{
                                        handler.sendEmptyMessage(DATA_SUCCESS_OTHER);
                                    }
                                }else{
                                    sendErrorMessage(handler,jsonObject);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //新增收货地址
    public void addAddress(final String n, final String mp, final String ac, final String tp,
                           final String zc, final Boolean isd, final String ad, final Integer pid,
                           final Integer cid, final Integer aid, final Handler handler) {
        try {
            //JSON对象
            JSONObject object = new JSONObject();
            object.put("ID", "");
            object.put("N", n);
            object.put("MP", mp);
            object.put("AC", ac);
            object.put("TP", tp);
            object.put("ZC", zc);
            object.put("ISD", isd);
            object.put("AD", ad);
            object.put("RID", UserInformation.getUserInfo().UserId);
            object.put("PID", pid);
            object.put("CID", cid);
            object.put("AID", aid);

            // 请求的URL
            String url = Services.mHost + "DeliveryAddress/APP_InsertAddress";
            HashMap<String, String> extras = new HashMap<>();
            extras.put("str", object.toString());
            Services.json(extras);
            String aa = Services.timeFormat();
            String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
            String aa2 = url;
            String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + Services.json(extras) + Services.TOKEN;
            OkHttpUtils.post().url(url)
                    .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                    .addHeader("timestamp", aa)
                    .addHeader("nonce", aa1)
                    .addHeader("signature", Services.textToMD5L32(md5))
                    .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                    .addParams("str",object.toString())
                    .build()
                    .execute(new DataCallBack(mContext,handler) {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            Log.e(tag, "新增addAddress   ERROR" + e.toString());
                        }

                        @Override
                        public void onResponse(String s, int i) {
                            Log.e(tag, "新增addAddress:" + s);
                            try {
                                JSONObject json = new JSONObject(s);
                                if(checkJsonData(s,handler)){
                                    JSONObject jsonObject=new JSONObject(json.getString("Data"));
                                    if (jsonObject.optBoolean("Valid")){
                                        handler.sendEmptyMessage(DATA_SUCCESS);
                                    }else{
                                        sendErrorMessage(handler,jsonObject);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //修改收货地址
    public void updateAddress(final String id, final String n, final String mp, final String ac,
                              final String tp, final String zc, final Boolean isd, final String ad,
                              final Integer pid, final Integer cid, final Integer aid, final Handler handler) {
        try {
            //JSON对象
            JSONObject object = new JSONObject();
            object.put("ID", id);
            object.put("N", n);
            object.put("MP", mp);
            object.put("AC", ac);
            object.put("TP", tp);
            object.put("ZC", zc);
            object.put("ISD", isd);
            object.put("AD", ad);
            object.put("RID", UserInformation.getUserInfo().UserId);
            object.put("PID", pid);
            object.put("CID", cid);
            object.put("AID", aid);
            // 请求的URL
            String url = Services.mHost + "DeliveryAddress/APP_UpdAddress";
            HashMap<String, String> extras = new HashMap<>();
            extras.put("str", object.toString());
            Services.json(extras);
            String aa = Services.timeFormat();
            String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
            String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 +  Services.json(extras) + Services.TOKEN;
            OkHttpUtils.post().url(url)
                    .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                    .addHeader("timestamp", aa)
                    .addHeader("nonce", aa1)
                    .addHeader("signature", Services.textToMD5L32(md5))
                    .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                    .addParams("str",object.toString())
                    .build()
                    .execute(new DataCallBack(mContext,handler) {

                        @Override
                        public void onResponse(String s, int i) {
                            Log.e(tag, "updateAddress:" + s);
                            try {
                                JSONObject json = new JSONObject(s);
                                if(checkJsonData(s,handler)){
                                    JSONObject jsonObject=new JSONObject(json.getString("Data"));
                                    if (jsonObject.optBoolean("Valid")){
                                        handler.sendEmptyMessage(DATA_SUCCESS);
                                    }else{
                                        sendErrorMessage(handler,jsonObject);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //查询收货地址
    public void getAddressById(final String addressId, final Handler handler) {
        String url = Services.mHost + "DeliveryAddress/APP_GetAddress_ByID?AddressID=%s";
        url = String.format(url, addressId);
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext, handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag,"getAddressById:"+s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s,handler)){
                                JSONObject jsonObject=new JSONObject(json.getString("Data"));
                                if (jsonObject.optBoolean("Valid")){
                                    Gson gson = new Gson();
                                    Address data = gson.fromJson(jsonObject.getString("Obj"), com.ldnet.entities.Address.class);
                                    Message msg=handler.obtainMessage();
                                    msg.obj=data;
                                    msg.what=DATA_SUCCESS;
                                    handler.sendMessage(msg);
                                }else{
                                    sendErrorMessage(handler,jsonObject);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //获取收货地址列表
    public void getAddressList(final Handler handler) {
        String url = Services.mHost + "DeliveryAddress/APP_GetAddressSimpleList?ResidentID=%s";
        url = String.format(url, UserInformation.getUserInfo().getUserId());
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext,handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag, "getAddressList:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s,handler)){
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.optBoolean("Valid")){
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<AddressSimple>>() {
                                    }.getType();
                                    List<AddressSimple> data = gson.fromJson(jsonObject.getString("Obj"), type);
                                    if (data!=null&&data.size()>0){
                                        Message msg = handler.obtainMessage();
                                        msg.what = DATA_SUCCESS;
                                        msg.obj = data;
                                        handler.sendMessage(msg);
                                    }else{
                                        handler.sendEmptyMessage(DATA_SUCCESS_OTHER);
                                    }
                                }else{
                                    sendErrorMessage(handler,jsonObject);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //设置默认收获地址
    public void setDefaultAddress(final String addressId,final Handler handler){
        // 请求的URL
        String url = Services.mHost + "DeliveryAddress/APP_SetDefaultAddress?AddressID=%s&ResidentID=%s";
        url = String.format(url, addressId, UserInformation.getUserInfo().UserId);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get().url(url)
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo()).build()
                .execute(new DataCallBack(mContext,handler) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call,e,i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "setDefaultAddress:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s,handler)){
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.optBoolean("Valid")){
                                    handler.sendEmptyMessage(DATA_SUCCESS);
                                }else{
                                    sendErrorMessage(handler,jsonObject);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //删除收货地址
    public void deleteAddress(final String addressId, final Handler handler) {
        // 请求的URL
        String url = Services.mHost + "DeliveryAddress/APP_DelAddress?AddressID=%s";
        url = String.format(url, addressId);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get().url(url)
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo()).build()
                .execute(new DataCallBack(mContext, handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "deleteAddress:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handler)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
                                    handler.sendEmptyMessage(DATA_SUCCESS);
                                } else {
                                    sendErrorMessage(handler, jsonObject);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
