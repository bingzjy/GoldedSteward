package com.ldnet.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.autonavi.rtbt.IFrameForRTBT;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.entities.InfoBarData;
import com.ldnet.entities.InfoBarDetail;
import com.ldnet.entities.InfoBarType;
import com.ldnet.entities.User;
import com.ldnet.utility.CookieInformation;
import com.ldnet.utility.DataCallBack;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.OTHER;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.iflytek.cloud.Setting.LOG_LEVEL.detail;
import static com.ldnet.goldensteward.R.id.cover;
import static com.unionpay.mobile.android.global.a.J;
import static com.unionpay.mobile.android.global.a.h;
import static com.unionpay.mobile.android.global.a.s;
import static com.unionpay.mobile.android.global.a.t;

public class InfoBarService extends BaseService {

    private String tag=InfoBarService.class.getSimpleName();

    public InfoBarService(Context context) {
        this.mContext=context;
    }

    //获取所有子分类
    public void getInfoBarType(final Handler handler){
        String url = Services.mHost + "API/InfoBar/GetType";
        OkHttpService.get(url).execute(new DataCallBack(mContext,handler){

            @Override
           public void onResponse(String s, int i) {
               Log.e(tag,"getInfoBarType:"+s);
                try {
                    JSONObject object=new JSONObject(s);
                    if (checkJsonData(s,handler)){
                        JSONObject jsonObject=new JSONObject(object.getString("Data"));
                        if (jsonObject.optBoolean("Valid")){
                            Gson gson=new Gson();
                            Type type=new TypeToken<List<InfoBarType>>(){}.getType();
                            List<InfoBarType> list=gson.fromJson(jsonObject.optString("Obj"),type);
                            if (list!=null&&list.size()>0){
                                Message msg=handler.obtainMessage();
                                msg.obj=list;
                                msg.what=DATA_SUCCESS;
                                handler.sendMessage(msg);
                            }else{
                                sendErrorMessage(handler,jsonObject);
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


    //新增信息  bar供应需求
    public void addInfoAction(final String barType,final String title, final String content,final String type,
                              final String tel,final String cover,final String images,final Handler handler){
        String url=Services.mHost+"API/InfoBar/Add";
        HashMap<String,String> params=new HashMap<>();
        params.put("CommunityId", UserInformation.getUserInfo().CommunityId);
        params.put("ResidentId",UserInformation.getUserInfo().UserId);
        params.put("BarType",barType);
        params.put("Title",title);
        params.put("Content",content);
        params.put("Type",type);
        params.put("TEL",tel);
        params.put("Cover",cover);
        params.put("Images",images);

        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";

        Log.e(tag,"addInfoAction--params:"+Services.json(params));
        String md5 = UserInformation.getUserInfo().getUserPhone() +
                aa + aa1 + Services.json(params) + Services.TOKEN;
        OkHttpUtils.post().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addParams("CommunityId",UserInformation.getUserInfo().CommunityId)
                .addParams("ResidentId",UserInformation.getUserInfo().UserId)
                .addParams("BarType",barType)
                .addParams("Title",title)
                .addParams("Content",content)
                .addParams("Type",type)
                .addParams("TEL",tel)
                .addParams("Cover",cover)
                .addParams("Images",images)
                .build()
                .execute(new DataCallBack(mContext,handler){
                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag,"addInfoAction:"+s);
                        try {
                            JSONObject object=new JSONObject(s);
                            if (checkJsonData(s,handler)){
                                JSONObject jsonObject=new JSONObject(object.getString("Data"));
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

    //删除信息
    public void deleteInfoAction(final String infoId,final Handler handler){
        String url=Services.mHost+"API/InfoBar/Delete";
        HashMap<String, String> params = new HashMap<>();
        params.put("UserId", UserInformation.getUserInfo().UserId);
        params.put("id", infoId);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String md5 = UserInformation.getUserInfo().getUserPhone() +
                aa + aa1 + Services.json(params) + Services.TOKEN;
        OkHttpUtils.post().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32
                        (md5))
                .addParams("UserId", UserInformation.getUserInfo().UserId)
                .addParams("id",infoId)
                .build()
                .execute(new DataCallBack(mContext,handler){
                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag,"deleteInfoAction:"+s);
                        try {
                            JSONObject object = new JSONObject(s);
                            if (checkJsonData(s,handler)){
                                JSONObject jsonObject=new JSONObject(object.getString("Data"));
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


    //获取信息列表(barType全部是-1)
    public void getInfoList(final String barType, final String type, final String search, final String lastId, final Handler handler){
        String url=Services.mHost+"API/InfoBar/GetInfoBarList/%s?barType=%s&type=%s&search=%s&uid=%s&lastId=%s";
        User user=UserInformation.getUserInfo();
        url=String.format(url,user.CommunityId,barType,type,search,user.UserId,lastId);
        Log.e(tag,"getInfoList----params:"+url);
        OkHttpService.get(url).execute(new DataCallBack(mContext,handler){
            @Override
            public void onResponse(String s, int i) {
                Log.e(tag,"getInfoList:"+s);
                try {
                    JSONObject object=new JSONObject(s);
                    if (checkJsonData(s,handler)){
                        JSONObject jsonObject=new JSONObject(object.getString("Data"));
                        if (jsonObject.optBoolean("Valid")){
                            Gson gson=new Gson();
                            Type type=new TypeToken<List<InfoBarData>>(){}.getType();
                            List<InfoBarData> list=gson.fromJson(jsonObject.optString("Obj"),type);

                            if (list!=null&&list.size()>0){
                                Message msg=handler.obtainMessage();
                                msg.what=DATA_SUCCESS;
                                msg.obj=list;
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
                Log.e(tag,"getInfoList-----onError:"+e.toString());
            }
        });
    }


    //获取信息详情
    public void getInfoDetail(final String typeId,final Handler handler){
        String url=Services.mHost+"API/InfoBar/GetInfoBarDetail/id?id=%s";
        url=String.format(url,typeId);
        OkHttpService.get(url).execute(new DataCallBack(mContext,handler){
            @Override
            public void onResponse(String s, int i) {
                Log.e(tag,"getInfoDetail:"+s);
                try {
                    JSONObject object=new JSONObject(s);
                    if (checkJsonData(s,handler)){
                        JSONObject jsonObject=new JSONObject(object.getString("Data"));
                        if (jsonObject.optBoolean("Valid")){
                            Gson gson=new Gson();
                            Type type=new TypeToken<InfoBarDetail>(){}.getType();
                            InfoBarDetail detail=gson.fromJson(jsonObject.optString("Obj"),type);
                            if (detail!=null){
                                Message msg=handler.obtainMessage();
                                msg.what=DATA_SUCCESS;
                                msg.obj=detail;
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

}
