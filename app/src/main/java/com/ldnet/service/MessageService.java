package com.ldnet.service;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.entities.MessageData;
import com.ldnet.entities.MessageType;
import com.ldnet.utility.DataCallBack;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.zhy.http.okhttp.OkHttpUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lee on 2017/11/13
 */

public class MessageService extends BaseService {
    public MessageService(Context context) {
        this.mContext=context;
    }

    private String tag=MessageService.this.getClass().getSimpleName();


    //获取消息分类
    public void getMsgTypes(final Handler handler){
        String url= Services.mHost+"API/AppPush/GetMyMesgType/%s";
        url=String.format(url, UserInformation.getUserInfo().UserId);
        OkHttpService.get(url).execute(new DataCallBack(mContext,handler){
            @Override
            public void onResponse(String s, int i) {
                Log.e(tag,"getMsgTypes:"+s);
                try {
                    JSONObject object=new JSONObject(s);
                    if (checkJsonData(s,handler)){
                        JSONObject jsonObject=new JSONObject(object.getString("Data"));
                        if (jsonObject.optBoolean("Valid")){
                            Gson gson=new Gson();
                            Type type=new TypeToken<List<MessageType>>(){}.getType();
                            List<MessageType> data=gson.fromJson(jsonObject.optString("Obj"),type);

                            if (data!=null&&data.size()>0){
                                android.os.Message msg=handler.obtainMessage();
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


    //获取分类的信息列表
    public void getMsgListByType(final int type,final String lastId,final Handler handler){
        String url=Services.mHost+"API/AppPush/GetMyMesgContent/%s?pushType=%s&lastId=%s";
        url=String.format(url,UserInformation.getUserInfo().UserId,type,lastId);
        OkHttpService.get(url).execute(new DataCallBack(mContext,handler){
            @Override
            public void onResponse(String s, int i) {
               Log.e(tag,"getMsgListByType:"+s);
                try {
                    JSONObject object=new JSONObject(s);
                    if (checkJsonData(s,handler)){
                        JSONObject jsonObject=new JSONObject(object.getString("Data"));
                        if (jsonObject.optBoolean("Valid")){
                            Gson gson=new Gson();
                            Type type=new TypeToken<List<MessageData>>(){}.getType();
                            List<MessageData> data=gson.fromJson(jsonObject.optString("Obj"),type);

                            if (data!=null&&data.size()>0){
                                android.os.Message msg=handler.obtainMessage();
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


    //删除消息
    public void deleteMessage(final String id,final Handler handler){
        String url=Services.mHost+"API/AppPush/DeletePushContent";
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("UserId", UserInformation.getUserInfo().UserId);
        extras.put("Id", id);

        String md5 = UserInformation.getUserInfo().getUserPhone() +
                aa + aa1 + Services.json(extras) + Services.TOKEN;
        OkHttpUtils.post().url(url)
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32
                        (md5))
                .addParams("UserId", UserInformation.getUserInfo().UserId)
                .addParams("Id", id)
                .build()
                .execute(new DataCallBack(mContext,handler){
                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag,"deleteMessage:"+s);
                        try {
                            JSONObject object = new JSONObject(s);
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
