package com.ldnet.service;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.entities.AccountRecord;
import com.ldnet.entities.ChargAmount;
import com.ldnet.utility.DataCallBack;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.zhy.http.okhttp.OkHttpUtils;

import okhttp3.Call;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zjy on 2017/9/10.
 */
public class QinDianService extends BaseService {

    private String tag = QinDianService.class.getSimpleName();
    private String ipAddress = "http://139.196.105.221/qindian/";
    private final String STASTE = "status";
    private final String RESOURCES = "resources";
    private final String INFO = "info";

    public QinDianService(Context context) {
        this.mContext = context;
    }

    //注册充智安
    public void registerCZAUser(final Handler handler) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        final String url = Services.mHost + "API/Account/CZARegister";
        HashMap<String, String> extra = new HashMap<>();
        extra.put("CZACommunityId", UserInformation.getUserInfo().CZAID);
        extra.put("ResidentId", UserInformation.getUserInfo().getUserId());
        Services.json(extra);
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + Services.json(extra) + Services.TOKEN;
        OkHttpUtils.post().url(url)
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addParams("ResidentId", UserInformation.getUserInfo().getUserId())
                .addParams("CZACommunityId", UserInformation.getUserInfo().CZAID)
                .build().execute(new DataCallBack(mContext) {
            @Override
            public void onResponse(String s, int i) {
                Log.e(tag, "registerCZAUser------onResponse:" + s);

                try {
                    if (checkJsonData(s, handler)) {
                        JSONObject json = new JSONObject(s);
                        JSONObject jsonObject = new JSONObject(json.getString("Data"));
                        if (jsonObject.getBoolean("Valid") && !jsonObject.getString("Obj").equals("") && !jsonObject.get("Obj").equals("[]")) {
                            Message msg = handler.obtainMessage();
                            msg.what = DATA_SUCCESS;
                            msg.obj = jsonObject.getString("Obj");
                            handler.sendMessage(msg);

                        } else {
                            sendErrorMessage(handler, jsonObject);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onResponse(s, i);
            }

            @Override
            public void onError(Call call, Exception e, int i) {
                Log.e(tag, "registerCZAUser------onError:" + e.toString());
                super.onError(call, e, i);
            }
        });
    }


    //登录充智安
    public void loginCZAUser(final Handler handler) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String url = Services.mHost + "API/Account/CZALogin";
        HashMap<String, String> extra = new HashMap<>();
        extra.put("CZAUserId", UserInformation.getUserInfo().CZAUserId);
        Services.json(extra);
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + Services.json(extra) + Services.TOKEN;
        OkHttpUtils.post().url(url)
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addParams("CZAUserId", UserInformation.getUserInfo().CZAUserId)
                .build().execute(new DataCallBack(mContext) {
            @Override
            public void onResponse(String s, int i) {
                Log.e(tag, "loginCZAUser------onResponse:" + s);

                try {
                    if (checkJsonData(s, handler)) {
                        JSONObject object = new JSONObject(s);
                        JSONObject jsonObject = object.getJSONObject("Data");
                        if (jsonObject.getBoolean("Valid") && !jsonObject.getJSONObject("Obj").equals("") && !jsonObject.getJSONObject("Obj").equals("[]")) {
                            Message msg = handler.obtainMessage();
                            msg.what = DATA_SUCCESS;
                            msg.obj = jsonObject.getJSONObject("Obj").get("token");
                            handler.sendMessage(msg);
                        } else {
                            sendErrorMessage(handler, jsonObject);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onResponse(s, i);
            }

            @Override
            public void onError(Call call, Exception e, int i) {
                Log.e(tag, "loginCZAUser------onError:" + e.toString());
                super.onError(call, e, i);
            }
        });
    }


    //获取充值金额面值种类  get，参数phone
    public void getChargeAmount(final Handler handler) {
        String url = "http://139.196.105.221/qindian/user/telQuery?phone=" + UserInformation.getUserInfo().getUserPhone();
        OkHttpUtils.get().url(url)
                .addHeader("E-Auth-Token", UserInformation.getUserInfo().CZAToken)
                .addHeader("Machinecode", "LDGOLDWGCOM20170809")
                .addHeader("content-type", "application/json")
                .build().execute(new DataCallBack(mContext) {
            @Override
            public void onResponse(String s, int i) {
                Log.e(tag, "getChargeAmount--onResponse" + s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getString("status").equals("1")) {
                        JSONObject object = jsonObject.getJSONObject("resources");
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<ChargAmount>>() {
                        }.getType();
                        List<ChargAmount> chargAmountList = gson.fromJson(object.optString("result"), type);

                        if (chargAmountList != null && chargAmountList.size() > 0) {
                            Message msg = handler.obtainMessage();
                            msg.what = DATA_SUCCESS;
                            msg.obj = chargAmountList;
                            handler.sendMessage(msg);
                        } else {
                            sendErrorMessage(handler, "暂时没有数据");
                        }
                    } else {
                        sendErrorMessage(handler, jsonObject.getString("info"));
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
                super.onResponse(s, i);
            }
        });
    }



    //查看账户余额
    public void getRemind(final Handler handler) {
        String url = "http://139.196.105.221/qindian/user/remain";
        OkHttpUtils.get().url(url)
                .addHeader("E-Auth-Token", UserInformation.getUserInfo().CZAToken)
                .addHeader("Machinecode", "LDGOLDWGCOM20170809")
                .addHeader("content-type", "application/json")
                .build().execute(new DataCallBack(mContext) {
            @Override
            public void onResponse(String s, int i) {
                Log.e(tag, "getRemind:" + s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getString("status").equals("1")) {
                        JSONObject resource = jsonObject.getJSONObject("resources");
                        String balance = resource.getString("balance");
                        Message msg = handler.obtainMessage();
                        msg.obj = balance;
                        msg.what = DATA_SUCCESS;
                        handler.sendMessage(msg);
                    } else {
                        sendErrorMessage(handler, jsonObject.getString("info"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onResponse(s, i);
            }
        });
    }


    //获取充值金额信息
    public void getAmountInfo(final Handler handler) {
        String url = "http://139.196.105.221/qindian/general/getAmout";
        OkHttpUtils.get().url(url)
                .addHeader("E-Auth-Token", UserInformation.getUserInfo().CZAToken)
                .addHeader("Machinecode", "LDGOLDWGCOM20170809")
                .addHeader("content-type", "application/json")
                .build().execute(new DataCallBack(mContext) {
            @Override
            public void onResponse(String s, int i) {
                super.onResponse(s, i);
                Log.e(tag, "getAmountInfo----params:" + UserInformation.getUserInfo().CZAToken);
                Log.e(tag, "getAmountInfo-----" + s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String data = jsonObject.getString("resources");
                    if (jsonObject.getString("status").equals("1") && !data.equals("[]") && !data.equals("")) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<ChargAmount>>() {
                        }.getType();
                        List<ChargAmount> list = gson.fromJson(data, type);

                        if (list != null && list.size() > 0) {
                            Message msg = handler.obtainMessage();
                            msg.what = DATA_SUCCESS;
                            msg.obj = list;
                            handler.sendMessage(msg);
                        } else {
                            sendErrorMessage(handler, "暂时没有数据");
                        }
                    } else {
                        sendErrorMessage(handler, jsonObject.getString("info"));
                    }
                } catch (JSONException e) {
                    sendErrorMessage(handler, "");
                    e.printStackTrace();
                }
            }
        });
    }


    //账户充值  (1支付宝  2微信)
    public void chargeAmount(final String id, final String type, final Handler handler) {
        String url = "http://139.196.105.221/qindian/user/rechargeForJPGJ?m=" + id + "&type=" + type;
        OkHttpUtils.get().url(url)
                .addHeader("E-Auth-Token", UserInformation.getUserInfo().CZAToken)
                .addHeader("Machinecode", "LDGOLDWGCOM20170809")
                .addHeader("content-type", "application/json")
                .build().execute(new DataCallBack(mContext) {
            @Override
            public void onResponse(String s, int i) {
                super.onResponse(s, i);
                Log.e(tag, "chargeAmount-----" + s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getString("status").equals("1")) {
                        JSONObject result = jsonObject.getJSONObject("resources");
                        if (result != null && !result.toString().equals("{}")) {
                            Message msg = handler.obtainMessage();
                            msg.what = DATA_SUCCESS;
                            msg.obj = result;
                            handler.sendMessage(msg);
                        } else {
                            sendErrorMessage(handler, jsonObject.getString("info"));
                        }
                    } else {
                        sendErrorMessage(handler, jsonObject.getString("info"));
                    }
                } catch (JSONException e) {
                    sendErrorMessage(handler, "");
                    e.printStackTrace();
                }

            }
        });
    }

    //账单记录
    public void getConsumeData(final String time, final String index, final Handler handler) {
        String url = "http://139.196.105.221/qindian/user/cost?time=" + time + "&pageNo=" + index + "&pageSize=" + Services.PAGE_SIZE;
        OkHttpUtils.get().url(url)
                .addHeader("E-Auth-Token", UserInformation.getUserInfo().CZAToken)
                .addHeader("Machinecode", "LDGOLDWGCOM20170809")
                .addHeader("content-type", "application/json")
                .build().execute(new DataCallBack(mContext) {
            @Override
            public void onResponse(String s, int i) {
                super.onResponse(s, i);
                Log.e(tag, "getConsumeData-----" + s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String result = jsonObject.getString("resources");
                    if (jsonObject.getString("status").equals("1")) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<AccountRecord>>() {
                        }.getType();
                        List<AccountRecord> accountRecordList = gson.fromJson(result, type);
                        if (accountRecordList != null && accountRecordList.size() > 0) {
                            Message msg = handler.obtainMessage();
                            msg.what = DATA_SUCCESS;
                            msg.obj = accountRecordList;
                            handler.sendMessage(msg);
                        } else {
                            Message msg = handler.obtainMessage();
                            msg.what = DATA_SUCCESS_OTHER;
                            handler.sendMessage(msg);
                        }
                    } else {
                        sendErrorMessage(handler, jsonObject.getString("info"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    //获取订单信息QR	String	桩ID
    public void getOrderInfo(final String stateModelID, final String stateModelPort, final Handler handler) {
        String url = ipAddress + "user/prentOrder?QR=" + stateModelID + "&proNo=" + stateModelPort;
        OkHttpUtils.get().url(url)
                .addHeader("E-Auth-Token", UserInformation.getUserInfo().CZAToken)
                .addHeader("Machinecode", "LDGOLDWGCOM20170809")
                .addHeader("content-type", "application/json")
                .build().execute(new DataCallBack(mContext) {
            @Override
            public void onResponse(String s, int i) {
                super.onResponse(s, i);
                Log.e(tag, "getOrderInfo-----" + s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getString(STASTE).equals("1")) {
                        JSONObject data = jsonObject.getJSONObject(RESOURCES);
                        Message msg = handler.obtainMessage();
                        msg.what = DATA_SUCCESS;
                        msg.obj = data;
                        handler.sendMessage(msg);
                    } else {
                        sendErrorMessage(handler, jsonObject.getString(INFO));
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

    //付款type	String	付款类型。0：钱包付款 1：支付宝付款
    // oid
    public void payChargeMoney(final String type, final String oid, final Handler handler) {
        String url = ipAddress + "user/payment?type=" + type + "&oid=" + oid;
        OkHttpUtils.get().url(url)
                .addHeader("E-Auth-Token", UserInformation.getUserInfo().CZAToken)
                .addHeader("Machinecode", "LDGOLDWGCOM20170809")
                .addHeader("content-type", "application/json")
                .build().execute(new DataCallBack(mContext) {
            @Override
            public void onResponse(String s, int i) {
                super.onResponse(s, i);
                Log.e(tag, "payChargeMoney-----" + s);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    if (jsonObject.getString(STASTE).equals("1")) {
                        JSONObject data = jsonObject.getJSONObject(RESOURCES);
                        Message msg = handler.obtainMessage();
                        msg.what = DATA_SUCCESS;
                        msg.obj = data;
                        handler.sendMessage(msg);
                    } else {
                        sendErrorMessage(handler, jsonObject.getString(INFO));
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

    //get请求
    //type	String	扫码的结果 0:失败 1：成功
    //code	String	充电桩编号
    //即刻充电
    public void charge(final String type, final String code, final Handler chargHandler) {
        String url = "http://139.196.105.221/qindian/poin/charg?type=" + type + "&code=" + code;
        OkHttpUtils.get().url(url)
                .addHeader("E-Auth-Token", UserInformation.getUserInfo().CZAToken)
                .addHeader("Machinecode", "LDGOLDWGCOM20170809")
                .addHeader("content-type", "application/json")
                .build().execute(new DataCallBack(mContext) {
            @Override
            public void onResponse(String s, int i) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getString("status").equals("0")) {
                        String errorInfo = jsonObject.getString("info");
                        sendErrorMessage(chargHandler, errorInfo);
                    } else if (jsonObject.getString("status").equals("1")) {
                        chargHandler.sendEmptyMessage(DATA_SUCCESS);
                    }
                } catch (JSONException e) {
                    sendErrorMessage(chargHandler, "");
                    e.printStackTrace();
                }
                super.onResponse(s, i);
            }
        });

    }

}
