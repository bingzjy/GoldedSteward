package com.ldnet.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import com.autonavi.rtbt.IFrameForRTBT;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.adapter.FeeListViewAdapter;
import com.ldnet.activity.home.PropertyFeeDetail;
import com.ldnet.activity.home.Property_Fee;
import com.ldnet.activity.me.Message;
import com.ldnet.entities.Fees;
import com.ldnet.entities.PPhones;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.CookieInformation;
import com.ldnet.utility.DataCallBack;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.zhy.http.okhttp.OkHttpUtils;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lee on 2017/7/25.
 */
public class PropertyFeeService extends BaseService {

    private static String Tag = "PropertyFeeService";

    public PropertyFeeService(Context context) {
        this.mContext = context;
    }


    //查询物业费
    public void getPropertyFee( String year,  String month, String status, final Handler handlerGetFee) {
        if (TextUtils.isEmpty(year)) {
            year = "0";
        }
        if (TextUtils.isEmpty(month)) {
            month = "0";
        }
        if (TextUtils.isEmpty(status)) {
            status = "2";
        }
        String url = Services.mHost + "API/Fee/GetFeeByRoomID/%s?year=%s&month=%s&status=%s";
        url = String.format(url, UserInformation.getUserInfo().HouseId, year, month, status);
        OkHttpService.get(url).execute(new DataCallBack(mContext, handlerGetFee) {
                                           @Override
                                           public void onBefore(Request request, int id) {
                                               Log.e(Tag, "getPropertyFee:-----onBefore:");
                                               super.onBefore(request, id);
                                           }

                                           @Override
                                           public void onError(Call call, Exception e, int i) {
                                               Log.e(Tag, "getPropertyFee:-----onError:" + e.toString());
                                               super.onError(call, e, i);
                                           }

                                           @Override
                                           public void onResponse(String s, int i) {
                                               super.onResponse(s, i);

                                               try {
                                                   JSONObject json = new JSONObject(s);
                                                   if (checkJsonData(s, handlerGetFee)) {
                                                       JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                                       if (jsonObject.optBoolean("Valid")) {
                                                           Gson gson = new Gson();
                                                           Type type = new TypeToken<List<Fees>>() {
                                                           }.getType();
                                                           if (jsonObject.getString("Obj").equals("[]") || TextUtils.isEmpty(jsonObject.getString("Obj"))) {
                                                               //无数据
                                                               android.os.Message msg = handlerGetFee.obtainMessage();
                                                               msg.what = DATA_SUCCESS;
                                                               msg.obj = null;
                                                               handlerGetFee.sendMessage(msg);
                                                           } else {
                                                               List<Fees> feesList = gson.fromJson(jsonObject.getString("Obj"), type);
                                                               android.os.Message msg = handlerGetFee.obtainMessage();
                                                               msg.what = DATA_SUCCESS;
                                                               msg.obj = feesList;
                                                               handlerGetFee.sendMessage(msg);
                                                           }
                                                       } else {
                                                           sendErrorMessage(handlerGetFee, jsonObject);
                                                       }
                                                   }

                                               } catch (JSONException e) {
                                                   e.printStackTrace();
                                               }
                                           }
                                       }
        );

    }


    //获取银联支付所需的TN码
    public void GetTnByFeesIds(final String feesIds,final  String payerId, final Handler handlerGetTnByFeesIds) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL
        String url = Services.mHost + "API/Fee/PostTNByFeesIds_CountMerter";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("feesIds", feesIds);
        extras.put("payerId", payerId);
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
                .addParams("feesIds", feesIds)
                .addParams("payerId", payerId)
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
                        Log.e(Tag, "GetTnByFeesIds" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handlerGetTnByFeesIds)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.optBoolean("Valid")) {
                                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString("Obj"));
                                    String tn = jsonObject1.getString("TN");
                                    if (!TextUtils.isEmpty(tn)) {
                                        android.os.Message msg = handlerGetTnByFeesIds.obtainMessage();
                                        msg.what = DATA_SUCCESS;
                                        msg.obj = tn;
                                        handlerGetTnByFeesIds.sendMessage(msg);

                                        // doStartUnionPayPlugin(Property_Fee.this, tn, "00");
                                    }
                                } else {
                                    sendErrorMessage(handlerGetTnByFeesIds, jsonObject);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    //打开微信或者支付宝
    public void ALWeiXinPay(final String feesIds, final String payerId,final  String channel,final  Handler handleAlPay) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL
        String url = Services.mHost + "API/Fee/HFQRCodeFeesPay";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("feesIds", feesIds);
        extras.put("payerId", payerId);
        extras.put("channel", channel);  //WXPAY微信  ALIPAY支付宝
        OkHttpService.post(url, extras)
                .addParams("feesIds", feesIds)
                .addParams("payerId", payerId)
                .addParams("channel", channel)
                .build()
                .execute(new DataCallBack(mContext) {
                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        Log.e(Tag, "ALWeiXinPay:-----onBefore");
                    }

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Log.e(Tag, "ALWeiXinPay:-----onError:" + e.toString());
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(Tag, "ALWeiXinPay——onResponse:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (json.getBoolean("Status")) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
                                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString("Obj"));

                                    String orderId = jsonObject1.getString("ORDERID");
                                    String url = jsonObject1.getString("URL");

                                    if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(orderId)) {

                                        android.os.Message msg = handleAlPay.obtainMessage();
                                        msg.what = DATA_SUCCESS;
                                        msg.obj = url + "," + orderId;
                                        handleAlPay.sendMessage(msg);
                                    } else {
                                        sendErrorMessage(handleAlPay, mContext.getString(R.string.network_request_fail));
                                    }
                                } else {
                                    sendErrorMessage(handleAlPay, jsonObject);
                                }
                            } else {
                                if (json.getString("Data") == null || json.getString("Data").equals("null")) {
                                    sendErrorMessage(handleAlPay, "该小区暂未开通线上支付宝支付方式");
                                } else {
                                    sendErrorMessage(handleAlPay, mContext.getString(R.string.network_request_fail));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    //恒丰支付回调接口
    public void aliPayCallBack(final String orderId,final  Handler handlerAliPayCallBack) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL   HFOrderConfirm
        String url = Services.mHost + "API/Fee/HFOrderConfirm";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("CompanyId", UserInformation.getUserInfo().getPropertyId());
        extras.put("HFOrderId", orderId);
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
                .addParams("CompanyId", UserInformation.getUserInfo().getPropertyId())
                .addParams("HFOrderId", orderId)
                .build()
                .execute(new DataCallBack(mContext) {
                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                    }

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Log.e(Tag, "aliPayCallBack:-----onError:" + e.toString());
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(Tag, "aliPayCallBack" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handlerAliPayCallBack)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
                                    handlerAliPayCallBack.sendEmptyMessage(DATA_SUCCESS);
                                } else {
                                    sendErrorMessage(handlerAliPayCallBack, jsonObject);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    //获取用户欠费记录
    public void getArrearageAmount(final Handler handlerGetArrageAmount) {
        String url = Services.mHost + "API/Fee/GetArrearageAmount?roomId=%s";
        url = String.format(url, UserInformation.getUserInfo().getHouseId());
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
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                    }

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(Tag, "getArrearageAmount:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handlerGetArrageAmount)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
                                    android.os.Message msg = handlerGetArrageAmount.obtainMessage(DATA_SUCCESS, jsonObject.optString("Obj"));
                                    handlerGetArrageAmount.sendMessage(msg);
                                } else {
                                    sendErrorMessage(handlerGetArrageAmount, jsonObject);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    //获取物业联系电话
    public void getPropertyTelphone(final Handler handlerGetPropertyTel) {
        String url = Services.mHost + "Api/Property/GetCommonTel/%s";
        url = String.format(url, UserInformation.getUserInfo().getCommunityId());
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
                        //closeProgressDialog();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        //  closeProgressDialog();
                        Log.e(Tag, "getPropertyTelphone:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handlerGetPropertyTel)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<PPhones>>() {
                                    }.getType();
                                    List<PPhones> mDatas = gson.fromJson(jsonObject.getString("Obj"), listType);
                                    List<PPhones> newDatas = new ArrayList<PPhones>();
                                    if (mDatas != null && mDatas.size() > 0) {
                                        for (int t = 0; t < mDatas.size(); t++) {
                                            if (mDatas.get(t).getTitle().equals("物业管理处电话")) {
                                                newDatas.add(mDatas.get(t));
                                            }
                                        }
                                        if (newDatas.size() == 0) {
                                            android.os.Message msg = handlerGetPropertyTel.obtainMessage(DATA_SUCCESS, mDatas);
                                            handlerGetPropertyTel.sendMessage(msg);
                                        } else {
                                            android.os.Message msg = handlerGetPropertyTel.obtainMessage(DATA_SUCCESS, newDatas);
                                            handlerGetPropertyTel.sendMessage(msg);
                                        }
                                    } else {
                                        sendErrorMessage(handlerGetPropertyTel, mContext.getString(R.string.Property_does_not_provide_phone_call));
                                    }
                                } else {
                                    sendErrorMessage(handlerGetPropertyTel, jsonObject);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


}
