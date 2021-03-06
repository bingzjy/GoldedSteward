package com.ldnet.service;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.entities.FreaMarket;
import com.ldnet.entities.FreaMarketDetails;
import com.ldnet.entities.Information;
import com.ldnet.entities.InformationType;
import com.ldnet.entities.User;
import com.ldnet.entities.WeekendDetails;
import com.ldnet.entities.WeekendSignUp;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.sharepreferencedata.CookieInformation;
import com.ldnet.utility.http.DataCallBack;
import com.ldnet.activity.commen.Services;
import com.ldnet.utility.sharepreferencedata.UserInformation;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * Created by lee on 2017/10/26
 */

public class FindService extends BaseService {

    private String tag = FindService.class.getSimpleName();

    public FindService(Context context) {
        this.mContext = context;
    }

    //获取我的发布
    public void getMyPublish(final String lastId, final Handler handler) {
        // 请求的URL
        String url = Services.mHost + "API/Resident/GetMyPublish/%s?lastId=%s";
        url = String.format(url, UserInformation.getUserInfo().UserId, lastId);
        OkHttpService.get(url).execute(new DataCallBack(mContext, handler) {

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                Log.e(tag, "getMyPublish    before:");
            }

            @Override
            public void onResponse(String s, int i) {
                super.onResponse(s, i);
                Log.e(tag, "getMyPublish:" + s);
                try {
                    JSONObject json = new JSONObject(s);
                    if (checkJsonData(s, handler)) {
                        JSONObject jsonObject = new JSONObject(json.getString("Data"));
                        if (jsonObject.getBoolean("Valid")) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<com.ldnet.entities.Publish>>() {
                            }.getType();
                            List<com.ldnet.entities.Publish> datas = gson.fromJson(jsonObject.getString("Obj"), type);
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

    //获取周末详情
    public void getWeekendDetail(final String id, final Handler handler) {
        // 请求的URL
        String url = Services.mHost + "API/Resident/GetWeekendById/%s?residentId=%s";
        url = String.format(url, id, UserInformation.getUserInfo().UserId);
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext, handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "getWeekendDetail:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handler)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.optBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    WeekendDetails details = gson.fromJson(jsonObject.getString("Obj"),
                                            WeekendDetails.class);
                                    if (details != null) {
                                        Message msg = handler.obtainMessage();
                                        msg.what = DATA_SUCCESS;
                                        msg.obj = details;
                                        handler.sendMessage(msg);
                                    } else {
                                        sendErrorMessage(handler, "暂无详细信息");
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

    //获取周边游列表
    public void getWeekendList(final String id, final Handler handler) {
        // 请求的URL
        String url = Services.mHost + "API/Resident/GetWeekendByLastId/%s?lastId=%s";
    //    url = String.format(url, UserInformation.getUserInfo().getCommuntiyCityId(), id);
        url = String.format(url,"0", id);
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext, handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag, "getWeekendList:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handler)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<com.ldnet.entities.Weekend>>() {
                                    }.getType();

                                    if (!jsonObject.getString("Obj").equals("null")) {
                                        List<com.ldnet.entities.Weekend> datas = gson.fromJson(jsonObject.getString("Obj"), type);
                                        if (datas != null && datas.size() > 0) {
                                            Message msg = handler.obtainMessage();
                                            msg.what = DATA_SUCCESS;
                                            msg.obj = datas;
                                            handler.sendMessage(msg);
                                        } else {
                                            handler.sendEmptyMessage(DATA_SUCCESS_OTHER);
                                        }
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

    //发布周边游
    public void weekendCreate(final String title, final String sDatetime, final String eDatetime, final String address,
                              final String cost, final String imageIds, final String content, final Handler handler) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL
        final String url = Services.mHost + "API/Resident/WeekendAdd";
        User user = UserInformation.getUserInfo();
        HashMap<String, String> extras = new HashMap<>();
        extras.put("Title", title);
        extras.put("Memo", content);
        extras.put("Img", imageIds);
        extras.put("ActiveAddress", address);
        extras.put("Cost", cost);
        extras.put("StartDatetime", sDatetime);
        extras.put("EndDatetime", eDatetime);
        extras.put("ContractName", user.getUserName());
        extras.put("ContractTel", user.getUserPhone());
        extras.put("CityId", user.getCommuntiyCityId());
        extras.put("ResidentId", user.getUserId());
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
                .addParams("Title", title)
                .addParams("Memo", content)
                .addParams("Img", imageIds)
                .addParams("ActiveAddress", address)
                .addParams("Cost", cost)
                .addParams("StartDatetime", sDatetime)
                .addParams("EndDatetime", eDatetime)
                .addParams("ContractName", user.getUserName())
                .addParams("ContractTel", user.getUserPhone())
                .addParams("CityId", user.getCommuntiyCityId())
                .addParams("ResidentId", user.getUserId())
                .build().execute(new DataCallBack(mContext, handler) {

            @Override
            public void onResponse(String s, int i) {
                Log.e(tag, "weekendCreate:" + s);
                try {
                    JSONObject json = new JSONObject(s);
                    if (checkJsonData(s, handler)) {
                        JSONObject jsonObject = new JSONObject(json.getString("Data"));
                        if (jsonObject.getBoolean("Valid")) {
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

    //更新周边游
    public void weekendUpdate(final String id, final String title, final String sDatetime, final String eDatetime,
                              final String address, final String cost, final String imageIds, final String content, final Handler handler) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL
        final String url = Services.mHost + "API/Resident/WeekendUpdate";
        // 发起请求
        User user = UserInformation.getUserInfo();
        HashMap<String, String> extras = new HashMap<>();
        extras.put("Id", id);
        extras.put("Title", title);
        extras.put("Memo", content);
        extras.put("Img", imageIds);
        extras.put("ActiveAddress", address);
        extras.put("Cost", cost);
        extras.put("StartDatetime", sDatetime);
        extras.put("EndDatetime", eDatetime);
        extras.put("ContractName", user.getUserName());
        extras.put("ContractTel", user.getUserPhone());
        extras.put("CityId", user.getCommuntiyCityId());
        extras.put("ResidentId", user.getUserId());
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
                .addParams("Id", id)
                .addParams("Title", title)
                .addParams("Memo", content)
                .addParams("Img", imageIds)
                .addParams("ActiveAddress", address)
                .addParams("Cost", cost)
                .addParams("StartDatetime", sDatetime)
                .addParams("EndDatetime", eDatetime)
                .addParams("ContractName", user.getUserName())
                .addParams("ContractTel", user.getUserPhone())
                .addParams("CityId", user.getCommuntiyCityId())
                .addParams("ResidentId", user.getUserId())
                .build().execute(new DataCallBack(mContext, handler) {
            @Override
            public void onResponse(String s, int i) {
                Log.e(tag, "weekendUpdate:" + s);
                try {
                    JSONObject json = new JSONObject(s);
                    if (checkJsonData(s, handler)) {
                        JSONObject jsonObject = new JSONObject(json.getString("Data"));
                        if (jsonObject.getBoolean("Valid")) {
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

    //获取闲置物品列表
    public void getFreaMarketList(final String lastID, final Handler handler) {
        // 请求的URL
        String url = Services.mHost + "API/Resident/GetUnusedGoodsList/%s?lastId=%s";
      //  url = String.format(url, UserInformation.getUserInfo().CommuntiyCityId, lastID);
        url = String.format(url,"0", lastID);
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext, handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag, "getFreaMarketList:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handler)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<FreaMarket>>() {
                                    }.getType();

                                    if (!jsonObject.getString("Obj").equals("null")) {
                                        List<FreaMarket> datas = gson.fromJson(jsonObject.getString("Obj"), type);
                                        if (datas != null && datas.size() > 0) {
                                            Message msg = handler.obtainMessage();
                                            msg.what = DATA_SUCCESS;
                                            msg.obj = datas;
                                            handler.sendMessage(msg);
                                        } else {
                                            handler.sendEmptyMessage(DATA_SUCCESS_OTHER);
                                        }
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


    //获取闲置物品详情
    public void getFreaMarketDetails(String id, final Handler handler) {
        // 请求的URL
        String url = Services.mHost + "API/Resident/GetUnusedGoodsById/%s?residentId=%s";
        url = String.format(url, id, UserInformation.getUserInfo().UserId);
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext, handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "FreaMarketDetails:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handler)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.optBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    FreaMarketDetails details = gson.fromJson(jsonObject.getString("Obj"),
                                            FreaMarketDetails.class);
                                    if (details != null) {
                                        Message msg = handler.obtainMessage();
                                        msg.what = DATA_SUCCESS;
                                        msg.obj = details;
                                        handler.sendMessage(msg);
                                    } else {
                                        sendErrorMessage(handler, "暂无商品信息");
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

    //修改闲置物品
    public void updateUnUsedGoods(final String id, final String title, final String content,
                                  final String imageIds, final String newPrice, final String thinkPrice,
                                  final Handler handler) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL
        final String url = Services.mHost + "API/Resident/UnUsedGoodsUpdate";
        // 发起请求
        User user = UserInformation.getUserInfo();
        HashMap<String, String> extras = new HashMap<>();
        extras.put("Id", id);
        extras.put("Title", title);
        extras.put("Memo", content);
        extras.put("Img", imageIds);
        extras.put("Address", user.getCommuntiyAddress() + user.getCommuntiyName());
        extras.put("OrgPrice", newPrice);
        extras.put("Price", thinkPrice);
        extras.put("ContractName", user.getUserName());
        extras.put("ContractTel", user.getUserPhone());
        extras.put("CityId", user.getCommuntiyCityId());
        extras.put("ResidentId", user.getUserId());
        extras.put("CommunityId", user.getCommunityId());
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
                .addParams("Id", id)
                .addParams("Title", title)
                .addParams("Memo", content)
                .addParams("Img", imageIds)
                .addParams("Address", user.getCommuntiyAddress() + user.getCommuntiyName())
                .addParams("OrgPrice", newPrice)
                .addParams("Price", thinkPrice)
                .addParams("ContractName", user.getUserName())
                .addParams("ContractTel", user.getUserPhone())
                .addParams("CityId", user.getCommuntiyCityId())
                .addParams("ResidentId", user.getUserId())
                .addParams("CommunityId", user.getCommunityId())
                .build().execute(new DataCallBack(mContext, handler) {

            @Override
            public void onResponse(String s, int i) {
                Log.e(tag, "updateUnUsedGoods:" + s);
                try {
                    JSONObject json = new JSONObject(s);
                    if (checkJsonData(s, handler)) {
                        JSONObject jsonObject = new JSONObject(json.getString("Data"));
                        if (jsonObject.getBoolean("Valid")) {
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

    //发布闲置物品
    public void FreaMarketCreate(final String title, final String content, final String imageIds, final String newPrice, final String thinkPrice, final Handler handler) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL
        final String url = Services.mHost + "API/Resident/UnUsedGoodsAdd";
        User user = UserInformation.getUserInfo();
        HashMap<String, String> extras = new HashMap<>();
        extras.put("Title", title);
        extras.put("Memo", content);
        extras.put("Img", imageIds);
        extras.put("Address", user.getCommuntiyAddress() + user.getCommuntiyName());
        extras.put("OrgPrice", newPrice);
        extras.put("Price", thinkPrice);
        extras.put("ContractName", user.getUserName());
        extras.put("ContractTel", user.getUserPhone());
        extras.put("CityId", user.getCommuntiyCityId());
        extras.put("ResidentId", user.getUserId());
        extras.put("CommunityId", user.getCommunityId());
        Services.json(extras);
        String md5 = UserInformation.getUserInfo().getUserPhone() +
                aa + aa1 + Services.json(extras) + Services.TOKEN;
        OkHttpUtils.post().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addParams("Title", title)
                .addParams("Memo", content)
                .addParams("Img", imageIds)
                .addParams("Address", user.getCommuntiyAddress() + user.getCommuntiyName())
                .addParams("OrgPrice", newPrice)
                .addParams("Price", thinkPrice)
                .addParams("ContractName", user.getUserName())
                .addParams("ContractTel", user.getUserPhone())
                .addParams("CityId", user.getCommuntiyCityId())
                .addParams("ResidentId", user.getUserId())
                .addParams("CommunityId", user.getCommunityId())
                .build().execute(new DataCallBack(mContext, handler) {

            @Override
            public void onResponse(String s, int i) {
                Log.e(tag, "FreaMarketCreate---params:" + imageIds);
                Log.e(tag, "FreaMarketCreate：" + s);
                try {
                    JSONObject json = new JSONObject(s);
                    if (checkJsonData(s, handler)) {
                        JSONObject jsonObject = new JSONObject(json.getString("Data"));
                        if (jsonObject.getBoolean("Valid")) {
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

    //删除闲置物品
    public void deleteFreaMarket(final String id, final Handler handler) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL
        String url = Services.mHost + "API/Resident/DeleteUnusedGoods?id=%s";
        url = String.format(url, id);
        HashMap<String, String> extras = new HashMap<>();
        extras.put("id", id);
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
                .addParams("id", id)
                .build()
                .execute(new DataCallBack(mContext, handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "deleteFreaMarket:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handler)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
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

    //删除周边游
    public void deleteWeekend(final String id, final Handler handler) {
        // 请求的URL
        String url = Services.mHost + "API/Resident/DeleteWeekendById/%s";
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
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .build()
                .execute(new DataCallBack(mContext, handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("asdsdasd", "111111111" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handler)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
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

    //获取资讯分类
    public void getInfomationTypes(final Handler handler) {
        //请求的URL
        String url = Services.mHost + "Information/Sel_TypeList";
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext, handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag, "getInfomationTypes:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handler)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.optBoolean("Valid")) {
                                    if (!jsonObject.opt("Obj").equals("null")) {
                                        Gson gson = new Gson();
                                        Type type = new TypeToken<List<InformationType>>() {
                                        }.getType();
                                        List<InformationType> datas = gson.fromJson(jsonObject.getString("Obj"), type);
                                        if (datas != null && datas.size() > 0) {
                                            Message msg = handler.obtainMessage();
                                            msg.obj = datas;
                                            msg.what = DATA_SUCCESS;
                                            handler.sendMessage(msg);
                                        } else {
                                            handler.sendEmptyMessage(DATA_SUCCESS_OTHER);
                                        }
                                    } else {
                                        handler.sendEmptyMessage(DATA_SUCCESS_OTHER);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //生活资讯 - 根据分类获取
    public void getInformationsByType(final String typeId, final String lastId, final String uid, final String name, final String imageId, final Handler handler) {
        //请求的URL
        String url = Services.mHost + "Information/Sel_PageList?LastID=%s&PageCnt=%s&TypeID=%s&UID=%s&UName=%s&UImgID=%s";
        url = String.format(url, lastId, Services.PAGE_SIZE, typeId, uid, name, imageId);
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
                .execute(new DataCallBack(mContext, handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag, "F   GetInformationsByType:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handler)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<Information>>() {
                                    }.getType();
                                    List<Information> datas = gson.fromJson(jsonObject.getString("Obj"), type);
                                    if (datas != null && datas.size() > 0) {
                                        Message msg = handler.obtainMessage();
                                        msg.obj = datas;
                                        msg.what = DATA_SUCCESS;
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

    //获取周边游报名信息
    public void WeekendSignUpInformation(final String weekId, final String lastId, final Handler handler) {
        // 请求的URL
        String url = Services.mHost + "API/Resident/GetWeekendRecord/%s?lastId=%s";
        url = String.format(url, weekId, lastId);
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext, handler) {
                    @Override
                    public void onResponse(String s, int o) {
                        super.onResponse(s, o);
                        Log.e(tag, "WeekendSignUpInformation:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handler)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<WeekendSignUp>>() {
                                    }.getType();
                                    List<WeekendSignUp> data = gson.fromJson(jsonObject.getString("Obj"), type);
                                    if (data != null && data.size() > 0) {
                                        Message msg = handler.obtainMessage();
                                        msg.obj = data;
                                        msg.what = DATA_SUCCESS;
                                        handler.sendMessage(msg);
                                    } else {
                                        sendErrorMessage(handler, mContext.getResources().getString(R.string.weekend_signup_none));
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

    //周边游报名
    public void WeekendSignUp(final String weekendId, final Handler handler) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL
        final String url = Services.mHost + "API/Resident/WeekendRecordAdd";
        User user = UserInformation.getUserInfo();
        HashMap<String, String> extras = new HashMap<>();
        extras.put("WeekendId", weekendId);
        extras.put("Name", user.getUserName());
        extras.put("Tel", user.getUserPhone());
        extras.put("ResidentId", user.getUserId());
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
                .addParams("WeekendId", weekendId)
                .addParams("Name", user.getUserName())
                .addParams("Tel", user.getUserPhone())
                .addParams("ResidentId", user.getUserId())
                .build().execute(new DataCallBack(mContext, handler) {
            @Override
            public void onResponse(String s, int i) {
                Log.e(tag, "WeekendSignUp:" + s);
                try {
                    JSONObject json = new JSONObject(s);
                    JSONObject jsonObject = new JSONObject(json.getString("Data"));
                    if (json.getBoolean("Status")) {
                        if (jsonObject.getBoolean("Valid")) {
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


    //按分类获取我的发布  0房屋租赁，1邻里通，2闲置物品，3周边游
    public void getMyPublishByType(final int type, final String lastId, final Handler handler) {
        // 请求的URL
        String url = Services.mHost + "API/Resident/GetMyPublish2/%s?lastId=%s&type=%s";
        url = String.format(url, UserInformation.getUserInfo().UserId, lastId, type);
        Log.e(tag, "getMyPublish2_url:" + url);
        OkHttpService.get(url).execute(new DataCallBack(mContext, handler) {

            @Override
            public void onResponse(String s, int i) {
                super.onResponse(s, i);

                Log.e(tag, "getMyPublish2:" + s);
                try {
                    JSONObject json = new JSONObject(s);
                    if (checkJsonData(s, handler)) {
                        JSONObject jsonObject = new JSONObject(json.getString("Data"));
                        if (jsonObject.getBoolean("Valid")) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<com.ldnet.entities.PublishEntity>>() {
                            }.getType();
                            List<com.ldnet.entities.PublishEntity> datas = gson.fromJson(jsonObject.getString("Obj"), type);
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

}
