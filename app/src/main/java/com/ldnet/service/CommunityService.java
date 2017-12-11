package com.ldnet.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.BindingHouse;
import com.ldnet.activity.home.CommunityServicesPageTabActivity;
import com.ldnet.activity.home.YellowPages_Map;
import com.ldnet.activity.me.Community;
import com.ldnet.entities.CommunityServices;
import com.ldnet.entities.CommunityServicesDetails;
import com.ldnet.entities.CommunityServicesModel;
import com.ldnet.entities.MyProperties;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.CookieInformation;
import com.ldnet.utility.CustomListView;
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
import okhttp3.Request;

import static android.R.attr.data;
import static android.R.attr.id;
import static com.ldnet.goldensteward.R.id.tv_community_services;
import static com.unionpay.mobile.android.global.a.C;
import static com.unionpay.mobile.android.global.a.s;
/**
 * Created by lee on 2017/10/10
 */
public class CommunityService extends BaseService {

    private String tag = CommunityService.class.getSimpleName();

    public CommunityService(Context context) {
        this.mContext = context;
    }

    //获取我的小区
    public void getMyCommunity(final Handler handler) {
        // 请求的URL
        String url = Services.mHost + "API/Resident/GetResidentBindInfo/%s";
        url = String.format(url, UserInformation.getUserInfo().getUserId());
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
                .execute(new DataCallBack(mContext) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag, "我的小区----Community" + s);
                        try {
                            JSONObject json = new JSONObject(s);

                            if (checkJsonData(s, handler)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.optBoolean("Valid")&&!jsonObject.getString("Obj").equals("null")) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<MyProperties>>() {
                                    }.getType();
                                    List<MyProperties> propertiesList = gson.fromJson(jsonObject.getString("Obj"), type);
                                    if (propertiesList != null && propertiesList.size() > 0) {
                                        Message msg = handler.obtainMessage();
                                        msg.what = BaseService.DATA_SUCCESS;
                                        msg.obj = propertiesList;
                                        handler.sendMessage(msg);
                                    } else {
                                        handler.sendEmptyMessage(DATA_SUCCESS_OTHER);
                                    }
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

    //获取小区服务
    public void getCommunityService(final String typeId,final String lastID,final Handler handler){
        String url = Services.mHost + "API/Property/GetHouseKeeping/%s/%s/%s?lastId=%s";
        url = String.format(url, typeId, UserInformation.getUserInfo().CommunityId, UserInformation.getUserInfo().CommuntiyCityId, lastID);
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext,handler) {

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag,"getCommunityService:"+s);
                        try {
                            JSONObject object=new JSONObject(s);
                            if (checkJsonData(s,handler)){
                                JSONObject jsonObject=new JSONObject(object.getString("Data"));
                                if (jsonObject.optBoolean("Valid")){
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<com.ldnet.entities.CommunityServices>>() {
                                    }.getType();
                                    List<CommunityServices> data=gson.fromJson(jsonObject.optString("Obj"),listType);
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

    //获取小区服务详情
    public void getCommunityServiceDetail(final String id,final Handler handler){
        // 请求的URL
        String url = Services.mHost + "API/Property/GetHouseKeepingById/%s";
        url = String.format(url, id);
        OkHttpService.get(url).execute(new DataCallBack(mContext,handler){
            @Override
            public void onResponse(String s, int i) {
                super.onResponse(s, i);
                Log.e(tag,"getCommunityServiceDetail:"+s);
                try {
                    JSONObject object=new JSONObject(s);
                    if (checkJsonData(s,handler)){
                        JSONObject jsonObject=new JSONObject(object.getString("Data"));
                        if (jsonObject.optBoolean("Valid")){
                            Gson gson = new Gson();
                            CommunityServicesDetails communityServicesDetails = gson.fromJson(jsonObject.getString("Obj"),
                                    com.ldnet.entities.CommunityServicesDetails.class);
                            if (communityServicesDetails!=null){
                                Message msg=handler.obtainMessage();
                                msg.obj=communityServicesDetails;
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

            @Override
            public void onError(Call call, Exception e, int i) {
                super.onError(call, e, i);
                Log.e(tag,"getCommunityServiceDetail---error:"+e.toString());
            }
        });
    }

    //获取小区服务子分类
    public void getYellowPageSortById(final Handler handler) {
        String url = Services.mHost + "API/Property/GetHouseKeepType";
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext,handler) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag, "getYellowPageSortById:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s,handler)){
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.optBoolean("Valid")){
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<CommunityServicesModel>>() {
                                    }.getType();
                                    List<CommunityServicesModel> data= gson.fromJson(jsonObject.getString("Obj"), listType);
                                    if (data!=null&&data.size()>0){
                                        Message msg=handler.obtainMessage();
                                        msg.obj=data;
                                        msg.what=DATA_SUCCESS;
                                        handler.sendMessage(msg);
                                    }else{
                                        sendErrorMessage(handler, "暂时没有数据");
                                    }
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

    //获取小区服务（周边惠）商家活动
    public void getYellowPageActivity(final String lastId,final Handler handler){
        String url=Services.mHost+"API/Property/GetHouseKeepingActivity/%s/%s?lastId=%s";
        url=String.format(url,UserInformation.getUserInfo().CommunityId,UserInformation.getUserInfo().CommuntiyCityId,lastId);
        OkHttpService.get(url).execute(new DataCallBack(mContext,handler){
            @Override
            public void onResponse(String s, int i) {
                Log.e(tag,"getYellowPageActivity:------"+UserInformation.getUserInfo().CommunityId+"城市ID："+UserInformation.getUserInfo().CommuntiyCityId);
                Log.e(tag,"getYellowPageActivity:"+s);
                try {
                    JSONObject object=new JSONObject(s);
                    if (checkJsonData(s,handler)){
                    JSONObject jsonObject=new JSONObject(object.getString("Data"));
                        if (jsonObject.optBoolean("Valid")){
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<com.ldnet.entities.CommunityServices>>() {
                            }.getType();
                            List<CommunityServices> data=gson.fromJson(jsonObject.optString("Obj"),listType);
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

            @Override
            public void onBefore(Request request, int id) {
                Log.e(tag,"getYellowPageActivity--------before");

            }
        });

    }
}
