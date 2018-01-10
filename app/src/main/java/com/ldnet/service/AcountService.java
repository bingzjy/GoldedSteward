package com.ldnet.service;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.*;
import com.zhy.http.okhttp.OkHttpUtils;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;


/**
 * Created by lee on 2017/6/24.
 */
public class AcountService extends BaseService {

    public String tag = AcountService.class.getSimpleName();

    public AcountService(Context context) {
        this.mContext = context;
        Services.TOKEN= TokenInformation.getTokenInfo();
    }

    //getToken
    public void getToken(final String phone, final Handler handlerToken) {
        String url1 = Services.mHost + "GetToken?phone=%s&clientType=22";
        url1 = String.format(url1, phone);
        OkHttpUtils.get().url(url1)
                .addHeader("phone", phone)
                .addHeader("timestamp", Services.timeFormat())
                .addHeader("nonce", (int) ((Math.random() * 9 + 1) * 100000) + "")
                .build()
                .execute(new DataCallBack(mContext,handlerToken) {

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "getToken----" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (s.contains(mContext.getString(R.string.refuse))) {
                                sendErrorMessage(handlerToken, json);
                            } else {
                                if (json.getBoolean("Status")) {
                                    JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                    if (jsonObject.getBoolean("Valid") && !TextUtils.isEmpty(jsonObject.getString("Obj"))) {
                                        String token = jsonObject.getString("Obj");
                                        TokenInformation.setTokenInfo(token);
                                        Services.TOKEN = TokenInformation.getTokenInfo().toString();

                                        Message msg = new Message();
                                        msg.what = DATA_SUCCESS;
                                        msg.obj = phone;
                                        handlerToken.sendMessage(msg);
                                    } else {
                                        sendErrorMessage(handlerToken, jsonObject);
                                    }
                                } else {
                                    sendErrorMessage(handlerToken, "");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    // 获取验证码
    public void GetCode(final String tel,final  String type,final  Handler handlerGetCode) {
        // 请求的URL
        // Get请求,第一个参数电话、第二个参数类型是指注册（0）还是忘记密码（1）
        String url = Services.mHost + "API/Account/RegisterSendSMS/%s/%s";
        url = String.format(url, tel, type);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = tel + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get().url(url)
                .addHeader("phone", tel)
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .build()
                .execute(new DataCallBack(mContext) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "111111111" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (s.contains(mContext.getString(R.string.refuse))) {
                                sendErrorMessage(handlerGetCode, json);
                            } else {
                                if (json.getBoolean("Status")) {
                                    JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                    if (jsonObject.getBoolean("Valid") && !TextUtils.isEmpty(jsonObject.getString("Obj"))) {
                                        Message message = new Message();
                                        message.what = DATA_SUCCESS;
                                        handlerGetCode.sendMessage(message);
                                    } else {
                                        sendErrorMessage(handlerGetCode, jsonObject);
                                    }
                                } else {
                                    sendErrorMessage(handlerGetCode, "");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    // 验证验证码正确性
    public void VaildCode(final String tel,final  String code, final String type, final Handler handlerValidCode) {
        // 请求的URL
        // Get请求,第一个参数电话、第二个参数验证码、第三个参数类型是指注册（0）还是忘记密码（1）
        String url = Services.mHost + "API/Account/ValidSNSCode/%s/%s/%s";
        url = String.format(url, tel, code, type);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = tel + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get().url(url)
                .addHeader("phone", tel)
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .build()
                .execute(new DataCallBack(mContext,handlerValidCode) {
                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "VaildCode111111111" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (s.contains(mContext.getString(R.string.refuse))) {
                                sendErrorMessage(handlerValidCode, json);
                            } else {
                                if (json.getBoolean("Status")) {
                                    JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                    if (jsonObject.getBoolean("Valid") && !TextUtils.isEmpty(jsonObject.getString("Obj"))) {
                                        Message message = new Message();
                                        message.what = DATA_SUCCESS;
                                        handlerValidCode.sendMessage(message);
                                    } else {
                                        sendErrorMessage(handlerValidCode, jsonObject);
                                    }
                                } else {
                                    sendErrorMessage(handlerValidCode, "");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    // 用户注册
    public void Register(final String tel, final String code,final  String password,
                         final String recommendTel,final  Handler handlerRegister) {
        Log.e("asdsdasd", "注册---参数" + tel + "----" + code + "----" + password + "---" + recommendTel);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL
        final String url = Services.mHost + "API/Account/Register";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("Tel", tel);
        extras.put("Code", code);
        extras.put("Password", password);
        extras.put("RecommendTel", recommendTel);
        Services.json(extras);
        String md5 = tel +
                aa + aa1 + Services.json(extras) + Services.TOKEN;
        OkHttpUtils.post().url(url)
                .addHeader("phone", tel)
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32
                        (md5))
                .addParams("Tel", tel)
                .addParams("Code", code)
                .addParams("Password", password)
                .addParams("RecommendTel", recommendTel)
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
                        Log.e(tag, "注册---" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (s.contains(mContext.getString(R.string.refuse))) {
                                sendErrorMessage(handlerRegister, json);
                            } else {
                                if (json.getBoolean("Status")) {
                                    JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                    if (jsonObject.getBoolean("Valid") && !TextUtils.isEmpty(jsonObject.getString("Obj"))) {
                                        Message message = new Message();
                                        message.what = DATA_SUCCESS;
                                        handlerRegister.sendMessage(message);
                                    } else {
                                        sendErrorMessage(handlerRegister, jsonObject);
                                    }
                                } else {
                                    sendErrorMessage(handlerRegister, "");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //用户登录
    public void Login(final String phone,final String password,final Handler handler){
        String url = Services.mHost + "API/Account/Logon";
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("UserName", phone);
        extras.put("Password", password);
        extras.put("PlatForm", "Android");
        Services.json(extras);
        String md5 =  phone+
                aa + aa1 + Services.json(extras) + Services.TOKEN;
        Log.d("Services.aa",aa+","+aa1+","+Services.TOKEN);
        OkHttpUtils.post().url(url)
                .addHeader("phone", phone)
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addParams("UserName", phone)
                .addParams("Password", password)
                .addParams("PlatForm", "Android")
                .build().execute(new DataCallBack(mContext,handler) {

            @Override
            public String parseNetworkResponse(Response response, int id) throws IOException {
                if (response.isSuccessful()) {
                    Headers headers = response.headers();
                    if (headers.values("Set-Cookie").size() > 0) {
                        List<String> cookies = headers.values("Set-Cookie");
                        CookieInformation.setCookieInfo("cookies", cookies.get(0));
                    }
                }
                return super.parseNetworkResponse(response, id);
            }

            @Override
            public void onResponse(String s, int i) {
                Log.e(tag, "登录Login:" + s);
                try {
                    if (checkJsonDataSuccess(s, handler)) {
                        JSONObject json = new JSONObject(s);
                        JSONObject jsonObject = new JSONObject(json.getString("Data"));
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

    //添加手机和用户关系
    public void setLoginUserPush(final String phoneDeviceId, final Handler handler) {
        final String url = Services.mHost + "API/Account/SetLoginUserPush";
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";

        HashMap<String, String> extras = new HashMap<>();
        extras.put("PhoneCode", phoneDeviceId);
        extras.put("UserId",  UserInformation.getUserInfo().getUserId());
        extras.put("OSType", "0");
        Services.json(extras);
        String md5 = UserInformation.getUserInfo().getUserPhone() +
                aa + aa1 + Services.json(extras) + Services.TOKEN;
        OkHttpUtils.post().url(url)
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32
                        (md5))
                .addParams("PhoneCode", phoneDeviceId)
                .addParams("UserId", UserInformation.getUserInfo().getUserId())
                .addParams("OSType", "0")
                .build().execute(new DataCallBack(mContext) {
            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                Log.e(tag,"setLoginUserPush----before:--params"+phoneDeviceId+"   "+UserInformation.getUserInfo().getUserId());
            }

            @Override
            public void onResponse(String s, int i) {
                super.onResponse(s, i);
                Log.e(tag,"setLoginUserPush:"+s);
                try {
                    JSONObject json = new JSONObject(s);
                    if (checkJsonData(s, handler)) {
                        JSONObject jsonObject = new JSONObject(json.getString("Data"));
                        if (jsonObject.optBoolean("Valid")&&!jsonObject.opt("Obj").equals("null")) {
                            JSONObject data=new JSONObject(jsonObject.getString("Obj"));
                            Message msg=handler.obtainMessage();
                            msg.what=DATA_SUCCESS;
                            msg.obj=data.optString("Id");
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

    //删除手机和用户关系
    public void deleteLoginUserPush(Handler handler){
        String url= Services.mHost +"API/Account/DeleteLoginUserPush";
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";

        HashMap<String, String> extras = new HashMap<>();
        extras.put("id",  UserInformation.getUserInfo().getUserId());
        Services.json(extras);
        String md5 = UserInformation.getUserInfo().getUserPhone() +
                aa + aa1 + Services.json(extras) + Services.TOKEN;
        OkHttpUtils.post().url(url)
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32
                        (md5))
                .addParams("id", UserInformation.getUserInfo().getUserId())
                .build().execute(new DataCallBack(mContext) {
            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                Log.e(tag,"deleteLoginUserPush---before");
            }

            @Override
            public void onResponse(String s, int i) {
                super.onResponse(s, i);
                Log.e(tag,"deleteLoginUserPush:"+s);

            }
        });
    }

    //获取用户数据
    public void getData(final String phone, final String psd, final int type,final  Handler handlerGetData) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        final String url = Services.mHost + "API/Account/Logon";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("UserName", phone);
        extras.put("Password", psd);
        extras.put("PlatForm", "Android");
        Services.json(extras);
        String md5 = phone +
                aa + aa1 + Services.json(extras) + Services.TOKEN;
        OkHttpUtils.post().url(url)
                .addHeader("phone", phone)
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32
                        (md5))
                .addParams("UserName", phone)
                .addParams("Password", psd)
                .addParams("PlatForm", "Android")
                .build().execute(new DataCallBack(mContext) {

            @Override
            public String parseNetworkResponse(Response response, int id) throws IOException {
                if (response.isSuccessful()) {
                    Headers headers = response.headers();
                    if (headers.values("Set-Cookie").size() > 0) {
                        List<String> cookies = headers.values("Set-Cookie");
                        CookieInformation.setCookieInfo("cookies", cookies.get(0));
                    }
                }
                return super.parseNetworkResponse(response, id);
            }

            @Override
            public void onResponse(String s, int i) {
                Log.e(tag, "获取数据getData--" + s + "type-------" + type+"---Services.TOKEN---------"+Services.TOKEN);
                try {
                    JSONObject json = new JSONObject(s);
                    if (s.contains(mContext.getString(R.string.refuse))) {
                        sendErrorMessage(handlerGetData, json);
                    } else {
                        if (json.getBoolean("Status")) {
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (jsonObject.getBoolean("Valid") && !TextUtils.isEmpty(jsonObject.getString("Obj"))) {
                                Gson gson = new Gson();
                                User user = gson.fromJson(jsonObject.getString("Obj"), User.class);
                                UserInformation.setUserInfo(user);

                                Message msg = new Message();
                                msg.what = DATA_SUCCESS;
                                msg.obj = type;
                                handlerGetData.sendMessage(msg);
                            } else {
                                sendErrorMessage(handlerGetData, jsonObject);
                            }
                        } else {
                            sendErrorMessage(handlerGetData, "");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //判断是否业主
    public void checkIsOwner(final Handler handlerIsOwner) {
        Log.e(tag, "判断是否业主参数" + UserInformation.getUserInfo().getUserPhone() + "---" + UserInformation.getUserInfo().getUserId());
        String url = Services.mHost + "API/Account/IsOwner?residentId=" + UserInformation.getUserInfo().getUserId();
        url = String.format(url);
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext,handlerIsOwner) {

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "判断是否业主checkIsOwner:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (s.contains(mContext.getString(R.string.refuse))) {
                                sendErrorMessage(handlerIsOwner, json);
                            } else {
                                if (json.getBoolean("Status")) {
                                    JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                    Message msg = new Message();
                                    if (jsonObject.getBoolean("Valid") && !TextUtils.isEmpty(jsonObject.getString("Obj"))) {
                                        msg.what = DATA_SUCCESS;
                                        msg.obj = jsonObject.getString("Message") == null ? mContext.getString(R.string.welcome_owner) : jsonObject.getString("Message");
                                    } else {
                                        msg.what = DATA_SUCCESS_OTHER;
                                    }
                                    handlerIsOwner.sendMessage(msg);
                                } else {
                                    sendErrorMessage(handlerIsOwner, "");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //获取积分
    public static void IntegralTip(final int type, final Handler handlerIntegralTip) {
        String pUrl = Services.mHost + "API/Account/Logon";
        try {
            pUrl = new URL(pUrl).getPath();
            // 请求的URL
            String url = Services.mHost + "API/Prints/Add/%s?route=%s";
            url = String.format(url, UserInformation.getUserInfo().UserId, URLEncoder.encode(pUrl, "UTF-8"));
            OkHttpService.get(url).execute(new DataCallBack(mContext) {

                @Override
                public void onResponse(String s, int i) {
                    try {
                        JSONObject json = new JSONObject(s);
                        if (s.contains(mContext.getString(R.string.refuse))) {
                            sendErrorMessage(handlerIntegralTip, json);
                        } else {
                            if (json.getBoolean("Status")) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (jsonObject.getBoolean("Valid") && !TextUtils.isEmpty(jsonObject.getString("Obj"))) {
                                    Message msg = new Message();
                                    msg.what = DATA_SUCCESS;
                                    msg.obj = type;
                                    handlerIntegralTip.sendMessage(msg);
                                } else {
                                    sendErrorMessage(handlerIntegralTip, jsonObject);
                                }
                            } else {
                                sendErrorMessage(handlerIntegralTip, "");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //判断房屋与APP用户绑定关系是否通过验证[业主]
    public void getApprove(final String roomId, final String residentId, final Handler handlerGetApprove) {
        String url = Services.mHost + "API/EntranceGuard/Approve?roomId=" + roomId + "&residentId=" + residentId;
        url = String.format(url);
        OkHttpService.get(url)
                .execute(new DataCallBack(mContext, handlerGetApprove) {

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e(tag, "getApprove:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (checkJsonData(s, handlerGetApprove)) {
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (!jsonObject.getBoolean("Valid")) {
                                    Message msg=handlerGetApprove.obtainMessage();
                                    msg.what = DATA_SUCCESS_OTHER;
                                    handlerGetApprove.sendMessage(msg);
                                } else {
                                    Message msg=handlerGetApprove.obtainMessage();
                                    msg.what = DATA_SUCCESS;
                                    handlerGetApprove.sendMessage(msg);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //修改用户头像
    public void changeInformation(String name, String image,final Handler handler) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL
        String url = Services.mHost + "API/Resident/UpdateResident";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("img", image);
        extras.put("nickName", name);
        extras.put("Id", UserInformation.getUserInfo().getUserId());
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
                .addParams("Id", UserInformation.getUserInfo().getUserId())
                .addParams("nickName", name)
                .addParams("img", image)
                .build()
                .execute(new DataCallBack(mContext,handler) {

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e(tag, "ChangeInformation:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    handler.sendEmptyMessage(DATA_SUCCESS);
                                }else{
                                    sendErrorMessage(handler,jsonObject);
                                }
                            }else{
                                sendErrorMessage(handler,"");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //设置积分
    public  void setIntegralTip(final Handler handler, String routeURL){
        try {
            routeURL=new URL(routeURL).getPath();
            // 请求的URL
            String url = Services.mHost + "API/Prints/Add/%s?route=%s";
            url = String.format(url, UserInformation.getUserInfo().UserId, URLEncoder.encode(routeURL, "UTF-8"));
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
                    .execute(new DataCallBack(mContext,handler) {
                        @Override
                        public void onResponse(String s, int i) {
                            Log.e(tag, "setIntegralTip:" + s);
                            try {
                                JSONObject json = new JSONObject(s);

                                if (json.optBoolean("Status")) {
                                    JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                    if (jsonObject.optBoolean("Valid")) {
                                        Message msg = handler.obtainMessage();
                                        msg.what = DATA_SUCCESS;
                                        if (jsonObject.getString("Obj") != null) {
                                            JSONObject jsonObject1 = new JSONObject(jsonObject.getString("Obj"));
                                            msg.obj = jsonObject1.getString("Show");
                                            handler.sendMessage(msg);

                                            android.widget.Toast.makeText(mContext, jsonObject1.getString("Show"), Toast.LENGTH_SHORT).show();
                                        } else {
                                            handler.sendEmptyMessage(DATA_SUCCESS);
                                        }
                                    } else {
                                        handler.sendEmptyMessage(DATA_FAILURE);
                                    }
                                } else {
                                    handler.sendEmptyMessage(DATA_FAILURE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }  catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
