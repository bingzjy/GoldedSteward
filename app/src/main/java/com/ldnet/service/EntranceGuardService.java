package com.ldnet.service;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.commen.Constant;
import com.ldnet.entities.KeyChain;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.*;
import com.zhy.http.okhttp.OkHttpUtils;

import okhttp3.Call;
import okhttp3.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lee on 2017/7/30.
 */
public class EntranceGuardService extends BaseService {
    private String tag = EntranceGuardService.class.getSimpleName();
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public EntranceGuardService(Context context) {
        this.mContext = context;
    }


    //获取钥匙串
    public void getKeyChain(final boolean init, final Handler handlerGetKeyChain) {
        String url = Services.mHost + "API/EntranceGuard/KeyChain?residentId=%s&roomId=%s";
        url = String.format(url, UserInformation.getUserInfo().getUserId(), UserInformation.getUserInfo().getHouseId());
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .build()
                .execute(new DataCallBack(mContext) {

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "访问密码请求结果" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handlerGetKeyChain)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<KeyChain>>() {
                                    }.getType();
                                    if (jsonObject.getString("Obj") != null && !jsonObject.getString("Obj").equals("[]")) {
                                        Message message = handlerGetKeyChain.obtainMessage();
                                        message.obj = gson.fromJson(jsonObject.getString("Obj"), listType);
                                        message.what = DATA_SUCCESS;
                                        handlerGetKeyChain.sendMessage(message);

                                    } else {
                                        KeyCache.saveKey(null, UserInformation.getUserInfo().getCommuntiyName() + "  " + UserInformation.getUserInfo().getHouseName()); //保存钥匙串
                                        sendErrorMessage(handlerGetKeyChain, "钥匙串暂无");
                                    }
                                } else {
                                    sendErrorMessage(handlerGetKeyChain, jsonObject);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    public void checkOpenEntrance(final Handler handlerCheckEntrance) {
        //true 表示未开通门禁；false表示开通门禁
        String url = Services.mHost + "API/EntranceGuard/Unused?communityid=" + UserInformation.getUserInfo().getCommunityId();
        url = String.format(url);
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
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        //  showProgressDialog();
                    }

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                        //  closeProgressDialog();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag, "checkOpenEntrance:" + s);
                        // closeProgressDialog();
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handlerCheckEntrance)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (json.getBoolean("Status")) {
                                    if (jsonObject.getBoolean("Valid")) {
                                        handlerCheckEntrance.sendEmptyMessage(DATA_SUCCESS_OTHER);
//                                        OpenEntranceState = getString(R.string.nouse_entrance);
//                                        sensorManager.unregisterListener(sensorEventListener);
                                    } else {
                                        handlerCheckEntrance.sendEmptyMessage(DATA_SUCCESS);
                                        // OpenEntranceState = getString(R.string.use_entrance);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    //添加开门日志
    public void EGLog(final String deviceID, final Handler handlerEGlog) {
        String url = Services.mHost + "API/EntranceGuard/EGLog";
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("communityId", UserInformation.getUserInfo().getCommunityId());
        extras.put("residentId", UserInformation.getUserInfo().getUserId());
        extras.put("egId", deviceID);
        extras.put("date", mFormatter.format(new Date()));
        Services.json(extras);
        String md5 = UserInformation.getUserInfo().getUserPhone() +
                aa + aa1 + Services.json(extras) + Services.TOKEN;
        OkHttpUtils.post().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addParams("communityId", UserInformation.getUserInfo().getCommunityId())
                .addParams("residentId", UserInformation.getUserInfo().getUserId())
                .addParams("egId", deviceID)
                .addParams("date", mFormatter.format(new Date()))
                .build()
                .execute(new DataCallBack(mContext, handlerEGlog) {

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
                        Log.e(tag, "EGLog：" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handlerEGlog)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
                                    handlerEGlog.sendEmptyMessage(DATA_SUCCESS);
                                } else {
                                    sendErrorMessage(handlerEGlog, jsonObject);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
