package com.ldnet.service;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.me.Community;
import com.ldnet.activity.me.VisitorValidComplete;
import com.ldnet.entities.Building;
import com.ldnet.entities.EntranceGuard;
import com.ldnet.entities.MyProperties;
import com.ldnet.entities.PPhones;
import com.ldnet.entities.User;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.id;
import static com.ldnet.goldensteward.R.id.et_visitor_phone;
import static com.ldnet.goldensteward.R.id.tv_vistior_send;
import static com.unionpay.mobile.android.global.a.C;
import static com.unionpay.mobile.android.global.a.t;

/**
 * Created by lee on 2017/6/25.
 */
public class BindingService extends BaseService {

    private String tag=BindingService.class.getSimpleName();
    public BindingService(Context context) {
        this.mContext = context;
    }

    //获取楼栋信息
    public void Buildings(final String communityId,final Handler handlerBuilding) {
        // 请求的URL
        String url = Services.mHost + "API/Property/GetBuildByCommunityId/%s";
        url = String.format(url, communityId);
        OkHttpService.get(url).execute(new DataCallBack(mContext, handlerBuilding) {
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
                super.onResponse(s, i);
                Log.e("asdsdasd", "楼栋" + s);
                try {
                    JSONObject json = new JSONObject(s);
                    if (checkJsonData(s, handlerBuilding)) {
                        JSONObject jsonObject = new JSONObject(json.getString("Data"));
                        if (jsonObject.getBoolean("Valid") && jsonObject.getString("Obj") != null && !jsonObject.getString("Obj").equals("[]")) {
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<Building>>() {
                            }.getType();
                            List<Building> buildings = gson.fromJson(jsonObject.getString("Obj"), listType);

                            Message msg = new Message();
                            msg.what = DATA_SUCCESS;
                            msg.obj = buildings;
                            handlerBuilding.sendMessage(msg);
                        } else {
                            sendErrorMessage(handlerBuilding, jsonObject);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //获取单元信息
    public void Units(final String buildingId, final Handler handlerUnits) {
        // 请求的URL
        String url = Services.mHost + "API/Property/GetUnitByBuildId/%s";
        url = String.format(url, buildingId);
        OkHttpService.get(url).execute(new DataCallBack(mContext, handlerUnits) {
            @Override
            public void onError(Call call, Exception e, int i) {
                super.onError(call, e, i);
            }
            @Override
            public void onResponse(String s, int i) {
                super.onResponse(s, i);
                Log.d("asdsdasd", "111111111" + s);
                try {
                    if (checkJsonData(s, handlerUnits)) {
                        JSONObject json = new JSONObject(s);
                        JSONObject jsonObject = new JSONObject(json.getString("Data"));
                        if (jsonObject.getBoolean("Valid") && !jsonObject.getString("Obj").equals("") && !jsonObject.get("Obj").equals("[]")) {

                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<Building>>() {
                            }.getType();
                            List<Building> buildings = gson.fromJson(jsonObject.getString("Obj"), listType);

                            Message msg = new Message();
                            msg.what = DATA_SUCCESS;
                            msg.obj = buildings;
                            handlerUnits.sendMessage(msg);
                        } else {
                            sendErrorMessage(handlerUnits, jsonObject);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //获取房子列表信息
    public void Houses(final String unitId,final Handler handlerHouses) {
        // 请求的URL
        String url = Services.mHost + "API/Property/GetRoomByUnitId/%s";
        url = String.format(url, unitId);
        OkHttpService.get(url).execute(new DataCallBack(mContext, handlerHouses) {
            @Override
            public void onError(Call call, Exception e, int i) {
                super.onError(call, e, i);
            }

            @Override
            public void onResponse(String s, int i) {
                super.onResponse(s, i);
                Log.d("asdsdasd", "111111111" + s);
                try {
                    if (checkJsonData(s, handlerHouses)) {
                        JSONObject json = new JSONObject(s);
                        JSONObject jsonObject = new JSONObject(json.getString("Data"));
                        if (jsonObject.getBoolean("Valid") && !jsonObject.getString("Obj").equals("") && !jsonObject.get("Obj").equals("[]")) {
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<Building>>() {
                            }.getType();
                            List<Building> buildings2 = gson.fromJson(jsonObject.getString("Obj"), listType);

                            Message msg = new Message();
                            msg.what = DATA_SUCCESS;
                            msg.obj = buildings2;
                            handlerHouses.sendMessage(msg);
                        } else {
                            sendErrorMessage(handlerHouses, jsonObject);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //获取房屋的业主列表
    public void getEntranceGuard(final String roomId,final Handler handlerGetEntranceGuard) {
        String url = Services.mHost + "API/EntranceGuard/RoomOwners?roomId=" + roomId;
        url = String.format(url);
        OkHttpService.get(url).execute(new DataCallBack(mContext, handlerGetEntranceGuard) {

            @Override
            public void onResponse(String s, int i) {
                super.onResponse(s, i);
                Log.e(tag, "getEntranceGuard=" + s);
                try {
                    if (checkJsonData(s, handlerGetEntranceGuard)) {
                        JSONObject json = new JSONObject(s);
                        JSONObject jsonObject = new JSONObject(json.getString("Data"));
                        if (jsonObject.getBoolean("Valid")) {
                            if (jsonObject.getString("Obj") != null && !jsonObject.getString("Obj").equals("[]")) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<List<EntranceGuard>>() {
                                }.getType();
                                List<EntranceGuard> entranceGuards = gson.fromJson(jsonObject.getString("Obj"), type);
                                Message msg = new Message();
                                if (entranceGuards.size() > 0) {
                                    msg.what = DATA_SUCCESS;
                                    msg.obj = entranceGuards;
                                    handlerGetEntranceGuard.sendMessage(msg);

                                } else {
                                    msg.what = DATA_SUCCESS_OTHER;
                                    handlerGetEntranceGuard.sendMessage(msg);
                                }
                            } else {
                                Message msg = new Message();
                                msg.what = DATA_SUCCESS_OTHER;
                                handlerGetEntranceGuard.sendMessage(msg);
                            }
                        } else {
                            sendErrorMessage(handlerGetEntranceGuard, jsonObject);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //绑定房子(绑定成功后，EGBind)
    public void BindingHouse(final String communityId, final String roomId,final Handler handlerBindingHouse) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        final String url = Services.mHost + "API/Resident/ResidentBindRoom";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("CommunityId", communityId);
        extras.put("RoomId", roomId);
        extras.put("ResidentId", UserInformation.getUserInfo().getUserId());
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
                .addParams("CommunityId", communityId)
                .addParams("ResidentId", UserInformation.getUserInfo().getUserId())
                .addParams("RoomId", roomId)
                .build()
                .execute(new DataCallBack(mContext, handlerBindingHouse) {

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "绑定房子结果" + s);
                        try {
                            if (checkJsonData(s, handlerBindingHouse)) {
                                {
                                    JSONObject json = new JSONObject(s);
                                    JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                    if (jsonObject.getBoolean("Valid") && jsonObject.getString("Obj") != null) {
                                        Message msg = new Message();
                                        msg.what = DATA_SUCCESS;
                                        msg.obj = jsonObject.getString("Message");
                                        handlerBindingHouse.sendMessage(msg);
                                    } else {
                                        sendErrorMessage(handlerBindingHouse, jsonObject);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //获取我的小区和房产,判断用户是否绑定该房屋
    public void MyProperties(final Handler handerMyProperties) {
        // 请求的URL
        String url = Services.mHost + "API/Resident/GetResidentBindInfo/%s";
        url = String.format(url, UserInformation.getUserInfo().getUserId());
        OkHttpService.get(url).execute(new DataCallBack(mContext, handerMyProperties) {
            @Override
            public void onError(Call call, Exception e, int i) {
                super.onError(call, e, i);
            }

            @Override
            public void onResponse(String s, int i) {
                super.onResponse(s, i);
                Log.e("asdsdasd", "是否有该房屋" + s);
                try {
                    if (checkJsonData(s, handerMyProperties)) {
                        JSONObject json = new JSONObject(s);
                        JSONObject jsonObject = new JSONObject(json.getString("Data"));
                        if (jsonObject.getBoolean("Valid") && jsonObject.getString("Obj") != null && !jsonObject.getString("Obj").equals("[]")) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<MyProperties>>() {
                            }.getType();
                            List<MyProperties> myProperties = gson.fromJson(jsonObject.getString("Obj"), type);

                            Message msg = new Message();
                            msg.what = DATA_SUCCESS;
                            msg.obj = myProperties;
                            handerMyProperties.sendMessage(msg);

                        } else {
                            sendErrorMessage(handerMyProperties, jsonObject);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //获取物业管理处电话
    public void getPropertyTelphone(final String mCommunityId,final Handler handler) {
        String url = Services.mHost + "Api/Property/GetCommonTel/%s";
        url = String.format(url, mCommunityId == null ? UserInformation.getUserInfo().getCommunityId() : mCommunityId);
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext,handler) {
                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag, "getPropertyTelphone:" + s);
                        try {
                           if (checkJsonDataSuccess(s,handler)){
                               JSONObject json = new JSONObject(s);
                               JSONObject jsonObject = new JSONObject(json.getString("Data"));
                               Gson gson = new Gson();
                               Type listType = new TypeToken<List<PPhones>>() {}.getType();
                               List<PPhones> mDatas = gson.fromJson(jsonObject.getString("Obj"),listType);
                               if (mDatas!=null&&mDatas.size()>0){
                                   Message msg=handler.obtainMessage();
                                   msg.what=DATA_SUCCESS;
                                   msg.obj=mDatas;
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

    //解除房子绑定
    public void RemoveHouse(final String communityId,final String roomId,final Handler handler) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL
        String url = Services.mHost + "API/Resident/RemoveBindRoom";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("CommunityId", communityId);
        extras.put("RoomId", roomId);
        extras.put("ResidentId", UserInformation.getUserInfo().getUserId());
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
                .addParams("ResidentId", UserInformation.getUserInfo().getUserId())
                .addParams("RoomId", roomId)
                .addParams("CommunityId", communityId)
                .build()
                .execute(new DataCallBack(mContext,handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "RemoveHouse:" + s);
                        if (checkJsonDataSuccess(s, handler)) {
                            handler.sendEmptyMessage(DATA_SUCCESS);
                            Toast.makeText(mContext, mContext.getString(R.string.cancel_bind_success), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //解除社区绑定
    public void RemoveCommunity(final String communityId,final Handler handler) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL
        String url = Services.mHost + "API/Resident/RemoveBindCommunity";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("RoomId", "");
        extras.put("CommunityId", communityId);
        extras.put("ResidentId", UserInformation.getUserInfo
                ().getUserId());
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
                .addParams("ResidentId", UserInformation.getUserInfo().getUserId())
                .addParams("RoomId", "")
                .addParams("CommunityId", communityId)
                .build()
                .execute(new DataCallBack(mContext,handler) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag,"RemoveCommunity:"+s);
                        if (checkJsonDataSuccess(s, handler)) {
                            handler.sendEmptyMessage(DATA_SUCCESS);
                            Toast.makeText(mContext, mContext.getString(R.string.cancel_bind_success), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    //修改用户与房屋绑定关系中的门禁信息状态(residentType 0业主 1家属 2租户)
    public void postEGBind(final String residentType,final String ownerId,final String sDate,final String eDate,final String mHouseId,final Handler handler) {
        String url = Services.mHost + "API/EntranceGuard/EGBind";
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("residentId", UserInformation.getUserInfo().getUserId());
        extras.put("roomId", mHouseId);
        extras.put("ownerid", ownerId);
        extras.put("leaseDateS", sDate);
        extras.put("leaseDateE", eDate);
        extras.put("residentType",residentType);
        Services.json(extras);
        String md5 = UserInformation.getUserInfo().getUserPhone() +
                aa + aa1 + Services.json(extras) + Services.TOKEN;
        Log.e(tag, "postEGBind-parmas:  " + Services.json(extras));
        OkHttpUtils.post().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addParams("residentId", UserInformation.getUserInfo().getUserId())
                .addParams("roomId", mHouseId)
                .addParams("ownerid", ownerId)
                .addParams("leaseDateS", sDate)
                .addParams("leaseDateE", eDate)
                .addParams("residentType", residentType)
                .build()
                .execute(new DataCallBack(mContext,handler) {
                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "修改绑定状态结果:" + s);
                      if (checkJsonData(s,handler)){
                          handler.sendEmptyMessage(DATA_SUCCESS);
                      }
                    }
                });
    }

    //切换小区和房产
    public void SetCurrentInforamtion(final String communityId,final String roomId,final Handler handler) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL
        String url = Services.mHost + "API/Account/SetResidentLogonInfo";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("CommunityId", communityId);
        extras.put("HouseId", roomId);
        extras.put("ResidentId", UserInformation.getUserInfo
                ().getUserId());
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
                .addParams("ResidentId", UserInformation.getUserInfo().getUserId())
                .addParams("HouseId", roomId)
                .addParams("CommunityId", communityId)
                .build()
                .execute(new DataCallBack(mContext,handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "SetCurrentInforamtion:" + s);
                        JSONObject json = null;
                        try {
                            json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (checkJsonDataSuccess(s,handler)){
                                Gson gson = new Gson();
                                User user = gson.fromJson(jsonObject.getString("Obj"), User.class);
                                UserInformation.setUserInfo(user);
                                handler.sendEmptyMessage(DATA_SUCCESS);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //获取短信验证码
    public void getValid(final String room_id, final String phone, final String flag, final Handler handler) {
        String url = Services.mHost + "API/EntranceGuard/SendCode";
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("Id", room_id);
        extras.put("Value", phone);
        extras.put("Flag", flag);
        Services.json(extras);
        String md5 = UserInformation.getUserInfo().getUserPhone() +
                aa + aa1 + Services.json(extras) + Services.TOKEN;

        OkHttpUtils.post().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addParams("Id", room_id)
                .addParams("Value", phone)
                .addParams("Flag", flag)
                .build()
                .execute(new DataCallBack(mContext, handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "发送短信结果" + s);
                        if (checkJsonDataSuccess(s, handler)) {
                            handler.sendEmptyMessage(DATA_SUCCESS);
                        }
                    }
                });
    }

    //验证短信验证码是否有效
    public void postValid(final String editCode, final String phone, final String flag, final Handler handler) {

        String url = Services.mHost + "API/EntranceGuard/ValidCode";
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("Id", editCode);
        extras.put("Value", phone);
        extras.put("Flag", flag);
        Services.json(extras);
        String md5 = UserInformation.getUserInfo().getUserPhone() +
                aa + aa1 + Services.json(extras) + Services.TOKEN;

        OkHttpUtils.post().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addParams("Id", editCode)
                .addParams("Value", phone)
                .addParams("Flag", flag)
                .build()
                .execute(new DataCallBack(mContext, handler) {

                    @Override
                    public void onResponse(String s, int i) {

                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            Message msg = handler.obtainMessage();
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    handler.sendEmptyMessage(DATA_SUCCESS);
                                } else {
                                    if (TextUtils.isEmpty(jsonObject.getString("Message"))) {
                                        msg.obj = mContext.getString(R.string.vertification_code_input_error);
                                    } else {
                                        msg.obj = jsonObject.getString("Message");
                                    }
                                    msg.what = DATA_FAILURE;
                                    handler.sendMessage(msg);
                                }
                            } else {
                                msg.obj = mContext.getString(R.string.vertification_code_input_error);
                                msg.what = DATA_FAILURE;
                                handler.sendMessage(msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


}
