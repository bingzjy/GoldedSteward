package com.ldnet.service;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.entities.Notifications;
import com.ldnet.entities.SurveyEntity;
import com.ldnet.utility.http.DataCallBack;
import com.ldnet.activity.commen.Services;
import com.ldnet.utility.sharepreferencedata.UserInformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;


/**
 * Created by lee on 2017/11/20
 */

public class NotificationService extends BaseService {

    private String tag=NotificationService.this.getClass().getSimpleName();

    public NotificationService(Context context) {
        this.mContext = context;
    }

    //获取通知
    public void getNotificationList(final String lastId,final Handler handler){
        // 请求的URL
        String url = Services.mHost + "API/Property/GetNewsByRoomId/%s?roomId=%s&lastId=%s";
        String houseId = !TextUtils.isEmpty(UserInformation.getUserInfo().getHouseId()) ? UserInformation.getUserInfo().getHouseId() : "";
        url = String.format(url, UserInformation.getUserInfo().getCommunityId(), houseId, lastId);
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext,handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag, "通知Notifications：" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handler)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<Notifications>>() {
                                    }.getType();
                                    List<Notifications> datas = gson.fromJson(jsonObject.getString("Obj"), type);

                                    if (datas != null && datas.size() > 0) {
                                        Message msg = handler.obtainMessage();
                                        msg.what = DATA_SUCCESS;
                                        msg.obj = datas;
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

    //获取公告
    public void getAnnouncementList(final String lastId, final Handler handler) {
        // 请求的URL
        String url = Services.mHost + "API/Property/GetAnnouncementByRoomId/%s?roomId=%s&lastId=%s";
        String houseId = !TextUtils.isEmpty(UserInformation.getUserInfo().getHouseId()) ? UserInformation.getUserInfo().getHouseId() : "";
        url = String.format(url, UserInformation.getUserInfo().getCommunityId(), houseId, lastId);
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext, handler) {
                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag, "公告Announcements:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handler)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<Notifications>>() {
                                    }.getType();
                                    List<Notifications> datas = gson.fromJson(jsonObject.getString("Obj"), type);
                                    if (datas != null && datas.size() > 0) {
                                        Message msg = handler.obtainMessage();
                                        msg.what = DATA_SUCCESS;
                                        msg.obj = datas;
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
    //获取调研
    public void getSurveyList(final String lastId,final Handler handler){
        // 请求的URL
        String url = Services.mHost + "Survey/APP_GetSurveyList?CID=%s&UID=%s&PageCnt=%s&LastID=%s";
        url = String.format(url, UserInformation.getUserInfo().getCommunityId(), UserInformation.getUserInfo().getUserId(), Services.PAGE_SIZE, lastId);
        OkHttpService.get(url).execute(new DataCallBack(mContext,handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag, "调研getSurveyList:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handler)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<SurveyEntity>>() {
                                    }.getType();
                                    List<SurveyEntity> datas = gson.fromJson(jsonObject.getString("Obj"), type);
                                    if (datas != null && datas.size() > 0) {
                                        Message msg = handler.obtainMessage();
                                        msg.what = DATA_SUCCESS;
                                        msg.obj = datas;
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

    //获取通知公告的详情
    public void getNotificationDetail(final String id,final Handler handler){
        // 请求的URL
        String url = Services.mHost + "API/Property/GetNewsById/%s";
        url = String.format(url, id);
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext,handler) {
                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "getNotificationDetail:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Notifications data = gson.fromJson(jsonObject.getString("Obj"), Notifications.class);
                                    if (data != null) {
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

}
