package com.ldnet.service;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.mall.Goods_Details;
import com.ldnet.activity.mall.Order_Confirm;
import com.ldnet.activity.mall.Order_Details;
import com.ldnet.activity.mall.Pay;
import com.ldnet.activity.mall.Shopping_Carts;
import com.ldnet.entities.AddressSimple;
import com.ldnet.entities.Goods;
import com.ldnet.entities.Goods1;
import com.ldnet.entities.OD;
import com.ldnet.entities.OrderPay;
import com.ldnet.entities.Orders;
import com.ldnet.entities.RS;
import com.ldnet.entities.SD;
import com.ldnet.entities.ShoppingCart;
import com.ldnet.entities.Stock;
import com.ldnet.entities.SubOrders;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.CookieInformation;
import com.ldnet.utility.DataCallBack;
import com.ldnet.utility.ListViewAdapter;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.ldnet.utility.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

import static com.ldnet.goldensteward.R.id.ll_goods_balance;
import static com.ldnet.goldensteward.R.id.lv_order_details;
import static com.ldnet.goldensteward.R.id.tv_address_name;
import static com.ldnet.goldensteward.R.id.tv_address_title;
import static com.ldnet.goldensteward.R.id.tv_address_zipcode;
import static com.ldnet.goldensteward.R.id.tv_business_name;
import static com.ldnet.goldensteward.R.id.tv_business_phone;
import static com.ldnet.goldensteward.R.id.tv_orders_created;
import static com.ldnet.goldensteward.R.id.tv_orders_numbers;
import static com.ldnet.goldensteward.R.id.tv_orders_prices;
import static com.ldnet.goldensteward.R.id.tv_orders_status;
import static com.ldnet.utility.Utility.imageOptions;
import static com.unionpay.mobile.android.global.a.D;


/**
 * Created by lee on 2017/10/22
 */

public class OrderService extends BaseService {
    private String tag=OrderService.class.getSimpleName();

    public OrderService(Context context) {
        this.mContext=context;
    }

    //获取收获地址
    public void getPlaceOfReceipt(final Handler handler){
        String url = Services.mHost + "DeliveryAddress/APP_GetAddressSimpleList?ResidentID=%s";
        url = String.format(url, UserInformation.getUserInfo().getUserId());

        OkHttpService.get(url).execute(new DataCallBack(mContext,handler){
            @Override
            public void onResponse(String s, int i) {
                Log.e(tag,"getPlaceOfReceipt:"+s);
                try {
                    JSONObject json = new JSONObject(s);
                    if (checkJsonData(s,handler)){
                        JSONObject jsonObject = new JSONObject(json.getString("Data"));
                        if (jsonObject.getBoolean("Valid")) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<AddressSimple>>() {
                            }.getType();
                            List<AddressSimple> orderses = gson.fromJson(jsonObject.getString("Obj"), type);
                            if (orderses != null && orderses.size() > 0) {
                                Message msg = handler.obtainMessage();
                                msg.what = DATA_SUCCESS;
                                msg.obj = orderses;
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

    //查询库存
    public void getStock(final String goodsId, final Handler handler) {
        // 请求的URL
        String url = Services.mHost + "BGoodsStandard/APP_GetInfo_ByGoodsID?GID=%s";
        url = String.format(url, goodsId);
        OkHttpService.get(url).execute(new DataCallBack(mContext, handler) {

            @Override
            public void onResponse(String s, int i) {
                Log.e(tag, "查询库存:"+goodsId+"---getStock:" + s);
                try {
                    JSONObject json = new JSONObject(s);

                    if (checkJsonData(s,handler)){
                        JSONObject jsonObject = new JSONObject(json.getString("Data"));
                        if (jsonObject.getBoolean("Valid")) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<Stock>>(){}.getType();
                            List<Stock> mStocks = gson.fromJson(jsonObject.getString("Obj"),type);

                            if (mStocks!=null&&mStocks.size()>0){
                                Message msg=handler.obtainMessage();
                                msg.what=DATA_SUCCESS;
                                msg.obj=mStocks;
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

    //添加购物车
    public void addPurchaseCar(String bid, String gid, String ggid, Integer n, final Handler handler) {
            try {
                //JSON对象
                final JSONObject object = new JSONObject();
                object.put("BID", bid);
                object.put("RID", UserInformation.getUserInfo().UserId);
                object.put("GID", gid);
                object.put("GGID", ggid);
                object.put("N", n);
                object.put("GI", "");

                // 请求的URL
                String url = Services.mHost + "BShoppingCart/APP_InsertShopping";
                HashMap<String, String> extras = new HashMap<>();
                extras.put("str", object.toString());
                Services.json(extras);
                String aa = Services.timeFormat();
                String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
                String aa2 = url;
                String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + Services.json(extras) + Services.TOKEN;
                OkHttpUtils.post().url(url)
                        .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                        .addHeader("timestamp", aa)
                        .addHeader("nonce", aa1)
                        .addHeader("signature", Services.textToMD5L32(md5))
                        .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                        .addParams("str",object.toString())
                        .build()
                        .execute(new DataCallBack(mContext, handler) {

                            @Override
                            public void onResponse(String s, int i) {
                                Log.e(tag, "addPurchaseCar---params:" + object.toString());
                                Log.e(tag, "addPurchaseCar:" + s);
                                try {
                                    JSONObject json = new JSONObject(s);
                                    if (checkJsonData(s, handler)) {
                                        JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                        if (jsonObject.optBoolean("Valid")) {
                                            handler.sendEmptyMessage(BaseService.DATA_SUCCESS);
                                        } else {
                                            sendErrorMessage(handler, jsonObject);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
    }

    //提交订单
    public void orderPreSubmit(final String businessId, final String goodsId, final String stockId, final Integer number, final Handler handler){
            String aa = Services.timeFormat();
            String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
            try {
                // 请求的URL
                String url = Services.mHost + "BOrder/APP_GetSubOrderList_Post_New";
                JSONArray array = new JSONArray();
                JSONObject subOrderInfos = new JSONObject();
                //构造订单详细信息
                subOrderInfos.put("BID", businessId);
                subOrderInfos.put("MS", "");
                //订单中商品的详细信息
                JSONArray goodsArray = new JSONArray();
                JSONObject goodsInfos = new JSONObject();
                goodsInfos.put("GID", goodsId);
                goodsInfos.put("GGID", stockId);
                goodsInfos.put("SID", "");
                goodsInfos.put("N", number);
                goodsInfos.put("GIM", "");
                goodsArray.put(goodsInfos);
                //添加商品详细信息到订单中
                subOrderInfos.put("SD", goodsArray);
                subOrderInfos.put("UID", UserInformation.getUserInfo().UserId);
                array.put(subOrderInfos);
                HashMap<String, String> extras = new HashMap<>();
                extras.put("str", array.toString());
                Services.json(extras);

                Log.e(tag, "orderPreSubmit---params:" +   Services.json(extras));

                String md5 = UserInformation.getUserInfo().getUserPhone() +
                        aa + aa1 + Services.json(extras) + Services.TOKEN;
                OkHttpUtils.post().url(url)
                        .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                        .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                        .addHeader("timestamp", aa)
                        .addHeader("nonce", aa1)
                        .addHeader("signature", Services.textToMD5L32
                                (md5))
                        .addParams("str", array.toString())
                        .build()
                        .execute(new DataCallBack(mContext,handler) {

                            @Override
                            public void onResponse(String s, int i) {
                                super.onResponse(s, i);

                                Log.e(tag, "orderPreSubmit:" + s);
                                try {
                                    JSONObject json = new JSONObject(s);
                                    if (checkJsonData(s,handler)){
                                        JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                            if (jsonObject.getBoolean("Valid")) {
                                                Gson gson = new Gson();
                                                Type type = new TypeToken<List<SubOrders>>() {
                                                }.getType();
                                                List<SubOrders> orderses = gson.fromJson(jsonObject.getString("Obj"), type);
                                                if (orderses != null && orderses.size() > 0) {
                                                    Message msg = handler.obtainMessage();
                                                    msg.what = DATA_SUCCESS;
                                                    msg.obj = orderses;
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
            } catch (Exception ex) {
                ex.printStackTrace();
            }
    }

    //最终提交
    public void orderSubmitConfirm(final List<SubOrders> orderses,final String addressID,final Handler handler){
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        try {
            // 请求的URL
            String url = Services.mHost + "BOrder/APP_SubOrder_Two_Post_New";
            JSONArray array = new JSONArray();
            for (SubOrders orders : orderses) {
                JSONObject subOrderInfos = new JSONObject();
                //构造订单详细信息
                subOrderInfos.put("BID", orders.BID);
                subOrderInfos.put("MS", orders.Message);
                subOrderInfos.put("ISYHJ", orders.ISYHJ);
                subOrderInfos.put("YHJID", orders.YHJID);
                subOrderInfos.put("UID", UserInformation.getUserInfo().UserId);
                //订单中商品的详细信息
                JSONArray goodsArray = new JSONArray();
                for (RS s : orders.RS) {
                    JSONObject goodsInfos = new JSONObject();
                    goodsInfos.put("GID", s.GID);
                    goodsInfos.put("GGID", s.GGID);
                    goodsInfos.put("SID", s.SID);
                    goodsInfos.put("N", s.GC);
                    goodsInfos.put("GIM", "");
                    goodsArray.put(goodsInfos);
                }
                //添加商品详细信息到订单中
                subOrderInfos.put("SD", goodsArray);
                array.put(subOrderInfos);
            }
            HashMap<String, String> extras = new HashMap<>();
            extras.put("AddressID", addressID);
            extras.put("ResidentID", UserInformation.getUserInfo
                    ().getUserId());
            extras.put("str",array.toString());
            Services.json(extras);

            Log.e(tag,"orderSubmitConfirm---params:"+Services.json(extras));
            String md5 = UserInformation.getUserInfo().getUserPhone() +
                    aa + aa1 + Services.json(extras) + Services.TOKEN;
            OkHttpUtils.post().url(url)
                    .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                    .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                    .addHeader("timestamp", aa)
                    .addHeader("nonce", aa1)
                    .addHeader("signature", Services.textToMD5L32
                            (md5))
                    .addParams("str", array.toString())
                    .addParams("ResidentID", UserInformation.getUserInfo().getUserId())
                    .addParams("AddressID", addressID)
                    .build()
                    .execute(new DataCallBack(mContext,handler) {

                        @Override
                        public void onResponse(String s, int i) {
                            Log.e(tag, "orderSubmitConfirm：" + s);
                            try {
                                JSONObject json = new JSONObject(s);
                                if (checkJsonData(s,handler)) {
                                    JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                    if (jsonObject.optBoolean("Valid")){
                                        Gson gson = new Gson();
                                        OrderPay mPayInformation = gson.fromJson(jsonObject.getString("Obj"),OrderPay.class);

                                        if (mPayInformation!=null){
                                            Message msg=handler.obtainMessage();
                                            msg.obj=mPayInformation;
                                            msg.what=BaseService.DATA_SUCCESS;
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //获取购物车清单
    public void getShoppingCar(final Integer pageIndex,final Handler handler){
        String url = Services.mHost + "BShoppingCart/APP_GetShoppingList?ResidentID=%s&pageCnt=%s&pageIndex=%s";
        url = String.format(url, UserInformation.getUserInfo().UserId, Services.PAGE_SIZE, pageIndex);
        OkHttpService.get(url).execute(new DataCallBack(mContext,handler){
            @Override
            public void onResponse(String s, int i) {
                Log.e(tag,"getShoppingCar购物车清单："+s);
                try {
                    JSONObject object=new JSONObject(s);
                    if (checkJsonData(s,handler)){
                        JSONObject jsonObject=new JSONObject(object.getString("Data"));
                        if (jsonObject.optBoolean("Valid")){
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<ShoppingCart>>() {
                            }.getType();
                            List<ShoppingCart> datas = gson.fromJson(jsonObject.getString("Obj"), type);
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

    //更改购物车商品数量
    public void updateShopping(final Integer type,final String detailIds,final Integer count,final Handler handler){
        String url = Services.mHost + "BShoppingCart/APP_UpdShoppingDetailCnt?ShoppingDetailID=%s&Type=%s&cnt=%s";
        url = String.format(url, detailIds, type, count);
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
                    public void onResponse(String s, int i) {
                        Log.e(tag, "购物车修改数量：" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    handler.sendEmptyMessage(DATA_SUCCESS);
                                }else {
                                    sendErrorMessage(handler,jsonObject);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //删除购物车的商品
    public void deleteShopping(final String detailIds,final Handler handler){
        // 请求的URL
        String url = Services.mHost + "BShoppingCart/APP_DeleteShopping_ByDetailID?ShoppingDetailIDs=%s&ResidentID=%s";
        url = String.format(url, detailIds, UserInformation.getUserInfo().getUserId());
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
                        Log.e(tag, "deleteShopping:" + s);
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

    //结算购物车
    public void shoppingOrderSubmit(final List<ShoppingCart> carts, final Handler handler){
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        try {
            String url = Services.mHost + "BOrder/APP_GetSubOrderList_Post_New";
            JSONArray array = new JSONArray();
            for (ShoppingCart cart : carts) {
                JSONObject subOrderInfos = new JSONObject();
                //构造订单详细信息
                subOrderInfos.put("BID", cart.BID);
                subOrderInfos.put("MS", "");
                //订单中商品的详细信息
                JSONArray goodsArray = new JSONArray();
                for (SD s : cart.SD) {
                    JSONObject goodsInfos = new JSONObject();
                    goodsInfos.put("GID", s.GID);
                    goodsInfos.put("GGID", s.GGID);
                    goodsInfos.put("SID", s.ID);
                    goodsInfos.put("N", s.N);
                    goodsInfos.put("GIM", "");
                    goodsArray.put(goodsInfos);
                }
                //添加商品详细信息到订单中
                subOrderInfos.put("SD", goodsArray);
                subOrderInfos.put("UID", UserInformation.getUserInfo().UserId);
                array.put(subOrderInfos);
            }
            String dd = "{"+"\"str\""+":"+"\""+array.toString()+"\"}";
            String md5 = UserInformation.getUserInfo().getUserPhone() +
                    aa + aa1 + dd + Services.TOKEN;
            OkHttpUtils.post().url(url)
                    .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                    .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                    .addHeader("timestamp", aa)
                    .addHeader("nonce", aa1)
                    .addHeader("signature", Services.textToMD5L32
                            (md5))
                    .addParams("str", array.toString())
                    .build()
                    .execute(new DataCallBack(mContext,handler) {
                        @Override
                        public void onResponse(String s, int i) {
                            super.onResponse(s, i);
                            Log.e(tag, "ShoppingOrderSubmit购物车预提交：" + s);
                            try {
                                JSONObject json = new JSONObject(s);
                                if (checkJsonData(s,handler)) {
                                    JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                    if (jsonObject.getBoolean("Valid")) {
                                        Gson gson = new Gson();
                                        Type type = new TypeToken<List<SubOrders>>() {
                                        }.getType();
                                        List<SubOrders> orderses = gson.fromJson(jsonObject.getString("Obj"), type);
                                        if (orderses != null&&orderses.size()>0) {
                                            Message msg = handler.obtainMessage();
                                            msg.what = DATA_SUCCESS;
                                            msg.obj = orderses;
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

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //获取订单列表
    public void getOrders(final Integer viceType, final Integer pageIndex, final Handler handler) {
        // 请求的URL
        String url = Services.mHost + "BOrder/APP_GetOrderList?ResidentID=%s&ViceType=%s&PageCnt=%s&PageIndex=%s";
        url = String.format(url, UserInformation.getUserInfo().getUserId(), viceType, Services.PAGE_SIZE, pageIndex);
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext, handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag, viceType + "getOrders:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (checkJsonDataSuccess(s, handler)) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<List<Orders>>() {
                                }.getType();

                                List<Orders> datas = gson.fromJson(jsonObject.getString("Obj"), type);
                                if (datas != null && datas.size() > 0) {
                                    Message msg = handler.obtainMessage();
                                    msg.what = DATA_SUCCESS;
                                    msg.obj = datas;
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

    //获取订单的支付信息
    public void getOrderPayInformation(final String orderIDs, final Handler handler) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL
        String url = Services.mHost + "BOrder/APP_GetGoPayInfo_Post";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("IDS", orderIDs);
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
                .addParams("IDS", orderIDs)
                .build()
                .execute(new DataCallBack(mContext, handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "OrderPayInformation:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (checkJsonDataSuccess(s, handler)) {
                                Gson gson = new Gson();
                                OrderPay mPayInformation = gson.fromJson(jsonObject.getString("Obj"), OrderPay.class);
                                if (mPayInformation != null) {
                                    Message msg = handler.obtainMessage();
                                    msg.what = DATA_SUCCESS;
                                    msg.obj = mPayInformation;
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

    //删除订单
    public void orderDelete(final String orderID,final Handler handler) {
        // 请求的URL
        String url = Services.mHost + "BOrder/APP_DeleteOrder?OrderID=%s";
        url = String.format(url, orderID);
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
                        Log.e(tag, "OrderDelete:" + s);
                        if (checkJsonDataSuccess(s,handler)) {
                            handler.sendEmptyMessage(DATA_SUCCESS);
                        }
                    }
                });
    }

    //确认收货
    public void receiveComfirm(final String orderID, final Handler handler) {
        // 请求的URL
        String url = Services.mHost + "BOrder/APP_ConfirmReceive?OrderID=%s";
        url = String.format(url, orderID);
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
                        Log.e(tag, "ReceiveComfirm:" + s);
                        if (checkJsonDataSuccess(s, handler)) {
                            handler.sendEmptyMessage(DATA_SUCCESS);
                        }
                    }
                });
    }

    //取消订单
    public void orderCancel(final String orderID, final Handler handler) {
        // 请求的URL
        String url = Services.mHost + "BOrder/APP_CancelOrder?OrderID=%s";
        url = String.format(url, orderID);
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
                        Log.e(tag, "OrderCancel:" + s);
                        if (checkJsonDataSuccess(s, handler)) {
                            handler.sendEmptyMessage(DATA_SUCCESS);
                        }
                    }
                });
    }

    //获取订单详细
    public void getOrderDetails(final String orderID, final Handler handler) {
        String url = Services.mHost + "BOrder/APP_GetOrderInfo?OrderID=%s&ResidentID=%s";
        url = String.format(url, orderID, UserInformation.getUserInfo().UserId);
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext, handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, orderID+"OrderDetails:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (checkJsonDataSuccess(s, handler)) {
                                Gson gson = new Gson();
                                Orders mOrders = gson.fromJson(jsonObject.getString("Obj"), Orders.class);
                                if (mOrders != null) {
                                    Message msg = handler.obtainMessage();
                                    msg.what = DATA_SUCCESS;
                                    msg.obj = mOrders;
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

    //获取商品信息
    public void getGoodsInfo(final String goodsId, final Handler handler) {
        // 请求的URL
        String url = Services.mHost + "BGoods/App_GetGoodsInfo?GoodsID=%s";
        url = String.format(url, goodsId);
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext, handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "GetGoodsInfo:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (checkJsonDataSuccess(s, handler)) {
                                Gson gson = new Gson();
                                Goods1 goods = gson.fromJson(jsonObject.getString("Obj"), Goods1.class);
                                if (goods != null) {
                                    Message msg = handler.obtainMessage();
                                    msg.what = DATA_SUCCESS;
                                    msg.obj = goods;
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

}



