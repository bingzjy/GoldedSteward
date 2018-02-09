package com.ldnet.service;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
//import com.ldnet.entities.AccessGoodsRecord;
//import com.ldnet.entities.AccessVisitorRecord;
import com.ldnet.entities.AccessGoodsRecord;
import com.ldnet.entities.AccessVisitorRecord;
import com.ldnet.utility.CookieInformation;
import com.ldnet.utility.DataCallBack;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.ldnet.utility.Utility;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;



/**
 * Created by lee on 2017/9/30
 */

public class AccessControlService extends BaseService {

    private String tag = AccessControlService.class.getSimpleName();
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public AccessControlService(Context context) {
        this.mContext = context;
    }


    //获取访客记录
    public void getVisitorAccessRecord(String startDate, String endDate, String lastID, final Handler handler) {

        String url = Services.mHost + "API/InOut/Men/GetBySponsorId/%s?startDate=%s&endDate=%s&lastId=%s&pageSize=%s";
        url = String.format(url, UserInformation.getUserInfo().getUserId(),startDate,endDate, lastID, Services.PAGE_SIZE);
        Log.e(tag, "getAccessRecord:---url:" + url);

        OkHttpService.get(url).execute(new DataCallBack(mContext, handler) {
            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
            }

            @Override
            public void onResponse(String s, int i) {
                super.onResponse(s, i);
                Log.e(tag, "getAccessRecord:" + s);
                try {
                    JSONObject json = new JSONObject(s);
                    if (checkJsonData(s, handler)) {
                        JSONObject jsonObject = new JSONObject(json.getString("Data"));
                        if (jsonObject.optBoolean("Valid")) {
                            if (!jsonObject.opt("Obj").equals("null")){
                                //获取到访客记录
                                Gson gson=new Gson();
                                Type type=new TypeToken<List<AccessVisitorRecord>>(){}.getType();
                                List<AccessVisitorRecord> list=gson.fromJson(jsonObject.optString("Obj"),type);
                                if (list!=null&&list.size()>0){
                                    Message msg=handler.obtainMessage();
                                    msg.what=BaseService.DATA_SUCCESS;
                                    msg.obj=list;
                                    handler.sendMessage(msg);

                                }else{
                                    handler.sendEmptyMessage(BaseService.DATA_SUCCESS_OTHER);

                                }
                            }else{
                                handler.sendEmptyMessage(BaseService.DATA_SUCCESS_OTHER);
                            }
                        } else {
                            sendErrorMessage(handler, jsonObject);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(Call call, Exception e, int i) {
                super.onError(call, e, i);
            }
        });
    }


    //添加访客邀请
    public void addVisitorAccess(final String id, final String inviteName,
                                 final String inviteTel, final String accessDate, final String reason, final String isDriving,
                                 final String carNo, final String communityID, final String roomId, final String roomName, final Handler handler) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String url = Services.mHost + "API/InOut/Men/Add";

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Id", id);
            jsonObject.put("Created", mFormatter.format(new Date()));  //创建时间
            jsonObject.put("RoomId", roomId);   //房间ID
            jsonObject.put("RoomNo", roomName);     //房间房号
            jsonObject.put("SponsorId", UserInformation.getUserInfo().getUserId());  //发起人
            jsonObject.put("SponsorName", UserInformation.getUserInfo().getUserName());  //发起人姓名
            jsonObject.put("SponsorTel", UserInformation.getUserInfo().getUserPhone());       //发起人电话
            jsonObject.put("InviterName", inviteName);      //邀请人姓名
            jsonObject.put("InviterTel", inviteTel);
            jsonObject.put("Date", accessDate);  //来访日期
            jsonObject.put("Reasons", reason); //来访事由
            jsonObject.put("IsDriving", isDriving); //是否开车
            jsonObject.put("CarNo", carNo);
            jsonObject.put("CommunityId", communityID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String dd = "{" + "\"str\"" + ":" + "\"" + jsonObject.toString() + "\"}";
        String md5 = UserInformation.getUserInfo().getUserPhone() +
                aa + aa1 + dd + Services.TOKEN;
        OkHttpUtils.post().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addParams("str", jsonObject.toString())
                .build()
                .execute(new DataCallBack(mContext, handler) {

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        Log.e(tag, "addVisitorAccess---onBefore:"+dd);
                    }

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                        Log.e(tag, "addVisitorAccess---onError" + e.toString());
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "addVisitorAccess---onResponse:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handler)) {

                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
                                    handler.sendEmptyMessage(BaseService.DATA_SUCCESS);
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

    //添加物品出入
    public void addGoodsAccess(final String id, final String goods, final String reason, final String date,
                               final String communityId, final String roomId, final String roomName, final Handler handler) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String url = Services.mHost + "API/InOut/Goods/Add";
        Log.e(tag, "addGoodsAccess---url:" + url);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Id", id);
            jsonObject.put("Created", mFormatter.format(new Date()));  //创建时间
            jsonObject.put("RoomId", roomId);   //房间ID
            jsonObject.put("RoomNo", roomName);     //房间房号
            jsonObject.put("SponsorId", UserInformation.getUserInfo().getUserId());  //发起人
            jsonObject.put("SponsorName", UserInformation.getUserInfo().getUserName());  //发起人姓名
            jsonObject.put("SponsorTel", UserInformation.getUserInfo().getUserPhone());       //发起人电话
            jsonObject.put("Reasons", reason); //来访事由
            jsonObject.put("Date", date);  //来访日期
            jsonObject.put("Goods", goods); //是否开车
            jsonObject.put("CommunityId", communityId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String dd = "{" + "\"str\"" + ":" + "\"" + jsonObject.toString() + "\"}";

        Log.e(tag, "addGoodsAccess---param:" + dd);
        String md5 = UserInformation.getUserInfo().getUserPhone() +
                aa + aa1 + dd + Services.TOKEN;
        OkHttpUtils.post().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32
                        (md5))
                .addParams("str", jsonObject.toString())
                .build()
                .execute(new DataCallBack(mContext, handler) {
                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag, "addGoodsAccess" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handler)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.optBoolean("Valid")) {
                                    handler.sendEmptyMessage(BaseService.DATA_SUCCESS);
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

    //获取物品出入记录
    public void getGoodsAccessRecord(final String startDate, final String endDate, final String lastId, final Handler handler) {
        String url = Services.mHost + "API/InOut/Goods/GetBySponsorId/%s?startDate=%s&endDate=%s&lastId=%s&pageSize=%s";
        url = String.format(url, UserInformation.getUserInfo().getUserId(), startDate, endDate, lastId, Services.PAGE_SIZE);
        Log.e(tag, "getGoodsAccessRecord-----url:" + url);
        OkHttpService.get(url).execute(new DataCallBack(mContext) {
            @Override
            public void onResponse(String s, int i) {
                super.onResponse(s, i);
                Log.e(tag, "getGoodsAccessRecord:" + s);
                try {
                    JSONObject json = new JSONObject(s);
                    if (checkJsonData(s, handler)) {
                        JSONObject jsonObject = new JSONObject(json.getString("Data"));
                        if (jsonObject.optBoolean("Valid")) {
                            if (!jsonObject.getString("Obj").equals("null")) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<List<AccessGoodsRecord>>() {
                                }.getType();
                                List<AccessGoodsRecord> list = gson.fromJson(jsonObject.getString("Obj"), type);
                                if (list != null && list.size() > 0) {
                                    Message msg = handler.obtainMessage();
                                    msg.what = BaseService.DATA_SUCCESS;
                                    msg.obj = list;
                                    handler.sendMessage(msg);
                                } else {
                                    handler.sendEmptyMessage(BaseService.DATA_SUCCESS_OTHER);
                                }
                            } else {
                                handler.sendEmptyMessage(BaseService.DATA_SUCCESS_OTHER);
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

}
