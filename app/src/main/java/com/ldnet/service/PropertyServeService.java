package com.ldnet.service;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.autonavi.rtbt.IFrameForRTBT;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.adapter.TimeAdapter;
import com.ldnet.activity.home.Property_Complain_Details;
import com.ldnet.activity.home.Property_Repair;
import com.ldnet.activity.home.Property_Repair_Details;
import com.ldnet.entities.ChargingItem;
import com.ldnet.entities.Property;
import com.ldnet.entities.Score;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.CookieInformation;
import com.ldnet.utility.DataCallBack;
import com.ldnet.utility.GSApplication;
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
 * Created by zjy on 2017/11/10
 */

public class PropertyServeService extends BaseService {

    private String tag=PropertyServeService.this.getClass().getSimpleName();

    public PropertyServeService(Context context) {
        this.mContext=context;
    }

    //新增报修
    public void createRepair(final String images, final String content, final Integer type, final Handler handler){
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL rtype 0 公共报修  1个人报修 ResidentId Name Tel Content CommunityId ContentImg RoomId Rtype
        final String url = Services.mHost + "WFRepairs/APP_YZ_CreateRepairs";
        User user = UserInformation.getUserInfo();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ResidentId", user.UserId);
            jsonObject.put("Name", user.getUserName());
            jsonObject.put("Tel", user.getUserPhone());
            jsonObject.put("Content", content);
            jsonObject.put("CommunityId", user.CommunityId);
            jsonObject.put("ContentImg", images);
            jsonObject.put("RoomId", user.HouseId);
            jsonObject.put("Rtype", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String dd = "{" + "\"str\"" + ":" + "\"" + jsonObject.toString() + "\"}";
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
                .execute(new DataCallBack(mContext,handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "createRepair:" + s);
                        try {
                            JSONObject json = new JSONObject(s);

                            if (checkJsonData(s, handler)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
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

    //新增投诉
    public void createComplain(final String content,final String images,final Handler handler){
        // 请求的URL
        final String url = Services.mHost + "WFComplaint/APP_YZ_CreateComplaint";
        User user = UserInformation.getUserInfo();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ResidentId", user.UserId);
            jsonObject.put("Name", user.getUserName());
            jsonObject.put("Tel", user.getUserPhone());
            jsonObject.put("Content", content);
            jsonObject.put("CommunityId", user.CommunityId);
            jsonObject.put("ContentImg", images);
            jsonObject.put("RoomId", user.HouseId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String dd = "{" + "\"str\"" + ":" + "\"" + jsonObject.toString() + "\"}";
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
                .execute(new DataCallBack(mContext,handler) {
                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "createComplain:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handler)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
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

    //获取小区报修收费项目
    public void getSFOptionList(String cid,final Handler handler) {
        String url = Services.mHost + "WFRepairs/APP_WY_GetSFOptionList?CID=%s";
        url = String.format(url, cid);
       OkHttpService.get(url)
                .execute(new DataCallBack(mContext,handler) {
                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "getSFOptionList:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (checkJsonDataSuccess(s, handler)) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<List<ChargingItem>>() {}.getType();
                                List<ChargingItem> dataList=gson.fromJson(jsonObject.getString("Obj"),type);
                                if (dataList!=null&&dataList.size()>0){
                                    Message msg = handler.obtainMessage();
                                    msg.obj = dataList;
                                    msg.what = DATA_SUCCESS;
                                    handler.sendMessage(msg);
                                }else{
                                    handler.sendEmptyMessage(DATA_SUCCESS_OTHER);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //意见反馈
    public void feedback(String content,final Handler handler) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        try {
            // 请求的URL
            String url = Services.mHost + "API/File/AppFeedback";
            //获取当前应用的版本号
            GSApplication application = GSApplication.getInstance();
            String appVersion = application.getPackageManager().getPackageInfo(application.getPackageName(), 0).versionName;
            HashMap<String, String> extras = new HashMap<>();
            extras.put("Content", content);
            extras.put("AppVersion", appVersion);
            extras.put("AppSystem", "android");
            extras.put("AppSystemVersion", Build.MODEL + " - Android " + Build.VERSION.RELEASE);
            extras.put("AppType", "业主App");
            extras.put("UserId", UserInformation.getUserInfo().getUserId());
            extras.put("UserName", UserInformation.getUserInfo().getUserName() + "[" + UserInformation.getUserInfo().UserPhone + "]");
            Services.json(extras);
            String md5 = UserInformation.getUserInfo().getUserPhone() +
                    aa + aa1 + Services.json(extras) + Services.TOKEN;
            OkHttpUtils.post().url(url)
                    .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                    .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                    .addHeader("timestamp", aa)
                    .addHeader("nonce", aa1)
                    .addHeader("signature", Services.textToMD5L32
                            (md5))
                    .addParams("Content", content)
                    .addParams("AppVersion", appVersion)
                    .addParams("AppSystem", "android")
                    .addParams("AppSystemVersion", Build.MODEL + " - Android " + Build.VERSION.RELEASE)
                    .addParams("AppType", "业主App")
                    .addParams("UserId", UserInformation.getUserInfo().getUserId())
                    .addParams("UserName", UserInformation.getUserInfo().getUserName() + "[" + UserInformation.getUserInfo().UserPhone + "]")
                    .build()
                    .execute(new DataCallBack(mContext,handler) {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            super.onError(call,e,i);
                        }

                        @Override
                        public void onResponse(String s, int i) {
                            Log.e(tag, "feedback:" + s);
                            if (checkJsonDataSuccess(s,handler)){
                                handler.sendEmptyMessage(DATA_SUCCESS);
                            }
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //提交报修评分
    public void createRepireScore(final String mRepairId,final float rate, final String et,final Handler handler) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL
        String url = Services.mHost + "WFRepairs/APP_YZ_CreateScore";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RID", mRepairId);
            jsonObject.put("SocreCnt", rate);
            jsonObject.put("OrtherContent", et);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String dd = "{" + "\"str\"" + ":" + "\"" + jsonObject.toString() + "\"}";
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
                .execute(new DataCallBack(mContext,handler) {
                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "CreateScore:" + s);
                        if (checkJsonDataSuccess(s,handler)){
                            handler.sendEmptyMessage(DATA_SUCCESS);
                        }
                    }
                });
    }

    //获取报修评分
    public void getRepireScoreInfo(final String id,final Handler handler) {
        // 请求的URL
        String url = Services.mHost + "WFRepairs/APP_YZ_GetScoreInfo?RID=%s";
        url = String.format(url, id);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        final String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get().url(url)
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo()).build()
                .execute(new DataCallBack(mContext,handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "GetScoreInfo:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (checkJsonDataSuccess(s, handler)) {
                                Gson gson = new Gson();
                                Score score = gson.fromJson(jsonObject.getString("Obj"), Score.class);
                                if (score != null) {
                                    Message msg = handler.obtainMessage();
                                    msg.what = DATA_SUCCESS;
                                    msg.obj = score;
                                    handler.sendMessage(msg);
                                } else {
                                    handler.sendEmptyMessage(DATA_SUCCESS_OTHER);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //报修沟通信息
    public void getRepaireCommunicate(final String id, final Handler handler) {
        // 请求的URL
        String url = Services.mHost + "WFRepairs/APP_YZ_GetOperateList?RID=%s&IsDesc=%s";
        url = String.format(url, id, true);
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext, handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag, "repaireCommunicate:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (checkJsonDataSuccess(s, handler)) {
                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<Property>>() {
                                }.getType();
                                List<Property> data = gson.fromJson(jsonObject.getString("Obj"), listType);

                                if (data != null && data.size() > 0) {
                                    Message msg = handler.obtainMessage();
                                    msg.what = DATA_SUCCESS;
                                    msg.obj = data;
                                    handler.sendMessage(msg);
                                } else {
                                    handler.sendEmptyMessage(DATA_SUCCESS_OTHER);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //投诉沟通信息
    public void getComplainCommunicate(final String id, final Handler handler) {
        // 请求的URL
        String url = Services.mHost + "WFComplaint/APP_YZ_GetOperateList?RID=%s&IsDesc=%s";
        url = String.format(url, id, true);
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext, handler) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag, "complainCommunicate:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (checkJsonDataSuccess(s, handler)) {
                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<Property>>() {
                                }.getType();
                                List<Property> data = gson.fromJson(jsonObject.getString("Obj"), listType);

                                if (data != null && data.size() > 0) {
                                    Message msg = handler.obtainMessage();
                                    msg.what = DATA_SUCCESS;
                                    msg.obj = data;
                                    handler.sendMessage(msg);
                                } else {
                                    handler.sendEmptyMessage(DATA_SUCCESS_OTHER);
                                }

                                }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //获取投诉评分
    public void getComplainScoreInfo(final String id, final Handler handler) {
        // 请求的URL
        String url = Services.mHost + "WFComplaint/APP_YZ_GetScoreInfo?RID=%s";
        url = String.format(url, id);
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
                    public void onResponse(String s, int i) {
                        Log.e(tag, "getComplainScoreInfo:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (checkJsonDataSuccess(s, handler)) {
                                Gson gson = new Gson();
                                Score score = gson.fromJson(jsonObject.getString("Obj"), Score.class);
                                if (score != null) {
                                    Message msg = handler.obtainMessage();
                                    msg.what = DATA_SUCCESS;
                                    msg.obj = score;
                                    handler.sendMessage(msg);
                                } else {
                                    handler.sendEmptyMessage(DATA_SUCCESS_OTHER);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //提交投诉评分
    public void createComplainScore(final String mRepairId,final float rate,final  String et,final Handler handler) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL
        String url = Services.mHost + "WFComplaint/APP_YZ_CreateScore";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RID", mRepairId);
            jsonObject.put("SocreCnt", rate);
            jsonObject.put("OrtherContent", et);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String dd = "{"+"\"str\""+":"+"\""+jsonObject.toString()+"\"}";
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
                .execute(new DataCallBack(mContext,handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "CreateComplainScore:" + s);
                        if (checkJsonDataSuccess(s,handler)){
                            handler.sendEmptyMessage(DATA_SUCCESS);
                        }
                    }
                });
    }


}
