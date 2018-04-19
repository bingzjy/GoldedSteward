package com.ldnet.service;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.entities.HouseRent;
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
import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by lee on 2017/11/7
 */

public class HouseRentService extends BaseService {
    public HouseRentService(Context context) {
        this.mContext=context;
    }

    private String tag=HouseRentService.this.getClass().getSimpleName();

    //获取房屋租赁详情
    public void getHouseRentListById(final String id,final Handler handler){
        String url = Services.mHost + "API/Property/GetRentailSaleById/%s";
        url = String.format(url, id);
        OkHttpService.get(url)
                    .execute(new DataCallBack(mContext,handler) {
                        @Override
                        public void onResponse(String s, int i) {
                            Log.e(tag, "getHouseRentListById:" + s);
                            try {
                                JSONObject object=new JSONObject(s);
                                if (checkJsonData(s,handler)){
                                    JSONObject jsonObject=new JSONObject(object.getString("Data"));
                                    if (jsonObject.optBoolean("Valid")){
                                        HouseRent houseRent=new Gson().fromJson(jsonObject.getString("Obj"),HouseRent.class);
                                        Message msg=handler.obtainMessage();
                                        msg.what=DATA_SUCCESS;
                                        msg.obj=houseRent;
                                        handler.sendMessage(msg);
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

    //发布房屋出租信息
    public void addHouseRent(final String Id,final String CommunityId,final String Title,
                             final String Abstract,final String Room,final String Hall,
                             final String Toilet,final String Acreage,final String Floor,
                             final String FloorCount,final String Orientation,final String FitmentType,
                             final String RoomType,final String RoomDeploy,final String Price,
                             final String RentType,final String Images,final String ContactTel,
                             final String Address,final String Elevator,final String Creator,final Handler handler){
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String url = Services.mHost + "API/Property/RentalSaleAdd";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("RentType", RentType);
        extras.put("CommunityId", CommunityId);
        extras.put("Orientation", Orientation);
        extras.put("Creator", Creator);
        extras.put("Toilet", Toilet);
        extras.put("FloorCount", FloorCount);
        extras.put("Title", Title);
        extras.put("Floor", Floor);
        extras.put("Room", Room);
        extras.put("Hall", Hall);
        extras.put("RoomType", RoomType);
        extras.put("Price", Price);
        extras.put("Address", Address);
        extras.put("Acreage", Acreage);
        extras.put("Id", Id);
        extras.put("Elevator", Elevator);
        extras.put("RoomDeploy", RoomDeploy);
        extras.put("ContactTel", ContactTel);
        extras.put("FitmentType", FitmentType);
        extras.put("Abstract", Abstract);
        extras.put("Images", Images);
        Services.json(extras);
        String md5 = UserInformation.getUserInfo().getUserPhone() +
                aa + aa1 + Services.json(extras) + Services.TOKEN;
        OkHttpUtils.post().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addParams("Id", Id)
                .addParams("CommunityId", CommunityId)
                .addParams("Title", Title)
                .addParams("Abstract", Abstract)
                .addParams("Room", Room)
                .addParams("Hall", Hall)
                .addParams("Toilet", Toilet)
                .addParams("Acreage", Acreage)
                .addParams("Floor", Floor)
                .addParams("FloorCount", FloorCount)
                .addParams("Orientation", Orientation)
                .addParams("FitmentType", FitmentType)
                .addParams("RoomType", RoomType)
                .addParams("RoomDeploy", RoomDeploy)
                .addParams("Price", Price)
                .addParams("RentType", RentType)
                .addParams("Images", Images)
                .addParams("ContactTel", ContactTel)
                .addParams("Address", Address)
                .addParams("Elevator", Elevator)
                .addParams("Creator", Creator)
                .build()
                .execute(new DataCallBack(mContext,handler) {

                    public void onResponse(String s, int i) {
                        Log.e(tag, "addHouseRent:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s,handler)){
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
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

    //修改房屋出租信息
    public void houseRentUpdate(final String Id, final String CommunityId, final String Title,
                                final  String Abstract,final  String Room, final String Hall,
                                final String Toilet, final String Acreage, final String Floor,
                                final String FloorCount, final String Orientation,final  String FitmentType,
                                final String RoomType,final  String RoomDeploy,final  String Price,
                                final  String RentType,final String Images,final  String ContactTel,
                                final  String Address,final  String Elevator,final  String Creator ,final Handler handler) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String url = Services.mHost + "API/Property/RentalSaleUpdate";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("RentType", RentType);
        extras.put("CommunityId", CommunityId);
        extras.put("Orientation", Orientation);
        extras.put("Creator", Creator);
        extras.put("Toilet", Toilet);
        extras.put("FloorCount", FloorCount);
        extras.put("Title", Title);
        extras.put("Floor", Floor);
        extras.put("Room", Room);
        extras.put("Hall", Hall);
        extras.put("RoomType", RoomType);
        extras.put("Price", Price);
        extras.put("Address", Address);
        extras.put("Acreage", Acreage);
        extras.put("Id", Id);
        extras.put("Elevator", Elevator);
        extras.put("RoomDeploy", RoomDeploy);
        extras.put("ContactTel", ContactTel);
        extras.put("FitmentType", FitmentType);
        extras.put("Abstract", Abstract);
        extras.put("Images", Images);
        extras.put("FitmentContent", "");
        Services.json(extras);
        String md5 = UserInformation.getUserInfo().getUserPhone() +
                aa + aa1 + Services.json(extras) + Services.TOKEN;
        OkHttpUtils.post().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addParams("Id", Id)
                .addParams("CommunityId", CommunityId)
                .addParams("Title", Title)
                .addParams("Abstract", Abstract)
                .addParams("Room", Room)
                .addParams("Hall", Hall)
                .addParams("Toilet", Toilet)
                .addParams("Acreage", Acreage)
                .addParams("Floor", Floor)
                .addParams("FloorCount", FloorCount)
                .addParams("Orientation", Orientation)
                .addParams("FitmentType", FitmentType)
                .addParams("RoomType", RoomType)
                .addParams("RoomDeploy", RoomDeploy)
                .addParams("Price", Price)
                .addParams("RentType", RentType)
                .addParams("Images", Images)
                .addParams("ContactTel", ContactTel)
                .addParams("Address", Address)
                .addParams("Elevator", Elevator)
                .addParams("Creator", Creator)
                .addParams("FitmentContent", "")
                .build()
                .execute(new DataCallBack(mContext,handler) {

                    @Override
                    public void onBefore(Request request, int id) {
                        Log.e(tag, "houseRentUpdate         before");
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "houseRentUpdate:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    handler.sendEmptyMessage(DATA_SUCCESS);
                                } else {
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
    public void deleteHouseRent(String id, final Handler handler) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL
        String url = Services.mHost + "API/Property/RentailSaleDel?id=%s";
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
                .execute(new DataCallBack(mContext,handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "deleteHouseRent:" + s);
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

    //获取房屋信息
    public void getHouseInfo(final Handler handler){
        String url = Services.mHost + "API/Property/RentailSaleSelect";
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext,handler) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Log.e(tag, "getHouseInfo    Error:" + e.toString());
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "getHouseInfo:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            JSONObject jsonObject1 = new JSONObject(jsonObject.getString("Obj"));
                            if (checkJsonDataSuccess(s,handler)) {
                                if (jsonObject.getString("Obj").equals("[]")) {
                                    sendErrorMessage(handler, "暂时没有数据");
                                } else {
                                    Message msg=handler.obtainMessage();
                                    msg.obj=jsonObject1;
                                    msg.what=DATA_SUCCESS;
                                    handler.sendMessage(msg);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //获取房屋租赁列表
    public void getHouseRentList(final String lastId,final Handler handler) {
        String url = Services.mHost + "API/Property/GetRentailSaleList?lstId=%s";
        url = String.format(url, lastId);
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext,handler) {

                    @Override
                    public void onBefore(Request request, int id) {
                        Log.e(tag, "getHouseRentList    onBefore" );
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag, "getHouseRentList:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<HouseRent>>() {
                                    }.getType();
                                    List<HouseRent> datas = gson.fromJson(jsonObject.getString("Obj"), type);
                                    if (datas!=null&&datas.size()>0){
                                        Message msg=handler.obtainMessage();
                                        msg.obj=datas;
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
}
