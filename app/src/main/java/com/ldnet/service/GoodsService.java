package com.ldnet.service;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.entities.*;
import com.ldnet.utility.http.DataCallBack;
import com.ldnet.activity.commen.Services;
import com.ldnet.utility.sharepreferencedata.UserInformation;

import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;


/**
 * Created by lee on 2017/7/30.
 */
public class GoodsService extends BaseService {


    private String tag = GoodsService.class.getSimpleName();

    public GoodsService(Context context) {
        this.mContext = context;
    }


    //获取商品列表
    public void getGoodsData(final String lastId,final  int PAGE_SIZE,final  Handler handlerGetGoodsData) {
        String url = Services.mHost + "BGoods/App_GetHomeGoodsList_2?CityID=%s&LastID=%s&PageCnt=%s";
        url = String.format(url, UserInformation.getUserInfo().CommuntiyCityId, lastId, PAGE_SIZE);
      OkHttpService.get(url).execute(new DataCallBack(mContext, handlerGetGoodsData) {

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "获取商品列表getGoodsData:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handlerGetGoodsData)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<Goods>>() {
                                    }.getType();
                                    List<Goods> goodslist;
                                    goodslist = gson.fromJson(jsonObject.getString("Obj"), listType);

                                    Message msg = handlerGetGoodsData.obtainMessage(DATA_SUCCESS, goodslist);
                                    handlerGetGoodsData.sendMessage(msg);

                                } else {
                                    sendErrorMessage(handlerGetGoodsData, jsonObject);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    //获取首页区域
    public void getHomePageArea(final Handler handlerGetHomePageArea) {
        String url = Services.mHost + "APPHomePageSet/App_GetList_Two?CID=%s";
        url = String.format(url, UserInformation.getUserInfo().CommunityId);
        OkHttpService.get(url).execute(new DataCallBack(mContext, handlerGetHomePageArea) {

            @Override
            public void onBefore(Request request, int id) {
            }

            @Override
            public void onError(Call call, Exception e, int i) {
            }

            @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag, "getHomePageArea:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handlerGetHomePageArea)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<APPHomePage_Area>>() {
                                    }.getType();

                                    List<APPHomePage_Area> mAppHomePageArea = gson.fromJson(jsonObject.getString("Obj"), listType);
                                    Message msg = handlerGetHomePageArea.obtainMessage(DATA_SUCCESS, mAppHomePageArea);
                                    handlerGetHomePageArea.sendMessage(msg);
                                } else {
                                    sendErrorMessage(handlerGetHomePageArea, jsonObject);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    //获取周边小店
    public void communityshops(final Handler handlerCommunityShop) {
        // 请求的URL
        String url = Services.mHost + "GoodsShop/GetInfo_BYCID?CID=%s";
        url = String.format(url, UserInformation.getUserInfo().CommunityId);
       OkHttpService.get(url).execute(new DataCallBack(mContext,handlerCommunityShop) {

           @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "communityshops:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handlerCommunityShop)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    CommunityShopId communityShopId = gson.fromJson(jsonObject.getString("Obj"), CommunityShopId.class);
                                    Message msg=handlerCommunityShop.obtainMessage(DATA_SUCCESS,communityShopId);
                                    handlerCommunityShop.sendMessage(msg);

                                }else{
                                    sendErrorMessage(handlerCommunityShop,jsonObject);

                                }                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }


    //根据ID获取商品列表
    public void getGoodsListByColumnId(final String columnID, final String lastID, final Handler handler){
        String url = Services.mHost + "APPHomePageSet/APP_GetHomePageGoodsList?CID=%s&LastID=%s&PageCnt=%s";
        url = String.format(url, columnID, lastID, Services.PAGE_SIZE);
        OkHttpService.get(url).execute(new DataCallBack(mContext,handler){
            @Override
            public void onResponse(String s, int i) {
                Log.e(tag,"getGoodsListByColumnId:"+s);
                try {
                    JSONObject  json=new JSONObject(s);
                    if (checkJsonData(s,handler)){
                        JSONObject jsonObject=new JSONObject(json.getString("Data"));
                        if (jsonObject.optBoolean("Valid")){
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<Goods>>() {
                            }.getType();
                            List<Goods> datas = gson.fromJson(jsonObject.getString("Obj"), type);

                            if (datas!=null&&datas.size()>0){
                                Message msg=handler.obtainMessage();
                                msg.what=DATA_SUCCESS;
                                msg.obj=datas;
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
            public void onBefore(Request request, int id) {
            }
        });

    }


    //根据商品ID获取商品信息
    public void getGoodsInfoByGoodsId(final String goodsID,final Handler handler){
        String url = Services.mHost + "BGoods/App_GetGoodsInfo?GoodsID=%s";
        url = String.format(url,goodsID);
        OkHttpService.get(url).execute(new DataCallBack(mContext,handler){
            @Override
            public void onResponse(String s, int i) {
                Log.e(tag,"getGoodsInfoByGoodsId:"+s);
                try {
                    JSONObject  json=new JSONObject(s);
                    if (checkJsonData(s,handler)){
                        JSONObject jsonObject=new JSONObject(json.getString("Data"));
                        if (jsonObject.optBoolean("Valid")){
                            Type type=new TypeToken<Goods>(){}.getType();
                            Gson gson=new Gson();
                            Goods goods=gson.fromJson(jsonObject.getString("Obj"),type);
                            if (goods!=null){
                                Message msg=handler.obtainMessage();
                                msg.what=DATA_SUCCESS;
                                msg.obj=goods;
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


    //获取商品库存信息
    public void getGoodsStock(final String goodsId,final Handler handler){
        String url = Services.mHost + "BGoodsStandard/APP_GetInfo_ByGoodsID?GID=%s";
        url = String.format(url, goodsId);
        OkHttpService.get(url).execute(new DataCallBack(mContext,handler){
            @Override
            public void onResponse(String s, int i) {
                Log.e(tag,"getGoodsStock最新库存："+s);

            }
        });
    }




}
