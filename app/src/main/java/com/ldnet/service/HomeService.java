package com.ldnet.service;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.entities.MessageCallBack;
import com.ldnet.utility.*;
import com.zhy.http.okhttp.OkHttpUtils;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import static com.unionpay.mobile.android.global.a.J;
import static com.unionpay.mobile.android.global.a.s;

/**
 * Created by lee on 2017/7/28.
 */
public class HomeService extends BaseService {

    private String tag = HomeService.class.getSimpleName();

    public HomeService(Context context) {
        this.mContext = context;
    }

    //获取推送信息
    public void APPGetJpushNotification(final int id,final  Handler handlerGetJpushNotification) {
        String url = Services.mHost + "API/Property/APPGetJpushNotification/%s?communityId=%s&resultId=%s";
        url = String.format(url, UserInformation.getUserInfo().UserId, UserInformation.getUserInfo().CommunityId, id);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
        // 请求的URL
        OkHttpUtils.get().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .build()
                .execute(new DataCallBack(mContext) {

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                    }

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "APPGetJpushNotification" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handlerGetJpushNotification)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    MessageCallBack messageCallBack = gson.fromJson(jsonObject.getString("Obj"), MessageCallBack.class);
                                    android.os.Message msg = handlerGetJpushNotification.obtainMessage(DATA_SUCCESS,messageCallBack);
                                    handlerGetJpushNotification.sendMessage(msg);
                                } else {
                                    sendErrorMessage(handlerGetJpushNotification, jsonObject);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //获取小红点推送
    //（0 通知公告,1 投诉,2 报修,3 沟通,4 缴费,5 网页,6 意见反馈,7 订单,8 其他,9消息中心（指物业消息））

    /**
     * 通知公告：如果推送范围是包含了业主当前房屋，那么会收到通知公告以及推送，如果不包含当前房屋，但是业主拥有推送的房屋，那么会在接受到消息中心以及推送
     *
     */

    public void getAppRedPoint(final Handler handler) {
        String url = Services.mHost + "API/AppPush/GetAppRedPoint/%s?communityId=%s&roomId=%s";
        url = String.format(url, UserInformation.getUserInfo().UserId, UserInformation.getUserInfo().CommunityId,
                UserInformation.getUserInfo().getHouseId());
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext, handler) {

                    @Override
                    public void onBefore(Request request, int id) {
                        Log.e(tag, "首页小红点getAppRedPoint   before" );
                    }

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Log.e(tag, "首页小红点getAppRedPoint   error" + e.toString());
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "getAppRedPoint:" + s);
                        try {
                            JSONObject object = new JSONObject(s);
                            if (checkJsonData(s, handler)) {
                                JSONObject jsonObject = new JSONObject(object.getString("Data"));
                                if (jsonObject.optBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<com.ldnet.entities.Type>>() {
                                    }.getType();

                                    List<Type> data = gson.fromJson(jsonObject.getString("Obj"), type);
                                    if (data != null && data.size() > 0) {
                                        Message msg = handler.obtainMessage();
                                        msg.what = DATA_SUCCESS;
                                        msg.obj = data;
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

    //删除小红点提示
    public void deleteRedPoint(final int type,final Handler handler){
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        final String url = Services.mHost + "API/AppPush/DeleteAppRedPoint";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("UserId", UserInformation.getUserInfo().UserId);
        extras.put("CommunityId", UserInformation.getUserInfo().CommunityId);
        extras.put("Type", String.valueOf(type));
        extras.put("RoomId",UserInformation.getUserInfo().HouseId);
        Services.json(extras);
        String md5 = UserInformation.getUserInfo().UserPhone +
                aa + aa1 + Services.json(extras) + Services.TOKEN;
        OkHttpUtils.post().url(url)
                .addHeader("phone", UserInformation.getUserInfo().UserPhone)
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32
                        (md5))
                .addParams("UserId", UserInformation.getUserInfo().UserId)
                .addParams("CommunityId", UserInformation.getUserInfo().CommunityId)
                .addParams("Type", String.valueOf(type))
                .addParams("RoomId",UserInformation.getUserInfo().HouseId)
                .build()
                .execute(new DataCallBack(mContext,handler){
                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag,"deleteRedPoint:"+s);
                        JSONObject object = null;
                        try {
                            object = new JSONObject(s);
                            if (checkJsonData(s, handler)) {
                                JSONObject jsonObject = new JSONObject(object.getString("Data"));
                                if (jsonObject.optBoolean("Valid")) {
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
