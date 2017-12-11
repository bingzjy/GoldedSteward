package com.ldnet.utility;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import com.ldnet.entities.StatusBoolean;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

//获取服务器数据
public class HttpGetData<T extends Serializable> {

    // 获取数据的URL
    private String mUrl;

    // 泛型的具体类型
    private Class<T> mType;

    // 错误信息
    private String mErrorCode;

    // 构造方法
    public HttpGetData(Class<T> type, String url) {

        // 请求的URL
        mUrl = url;
        // 返回的数据类型
        mType = type;
    }

    // 从服务器读取数据
    private String readStream(String url) {
        try {
            // 清空服务器返回的内容
            String response = "";
            String aa = Services.timeFormat();
            String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
            String aa2 = url;
            String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
            // 请求服务器数据
            HttpURLConnection connection = (HttpURLConnection) new URL(url)
                    .openConnection();

            // 设置Cookie
            String  cookie = CookieInformation.getUserInfo().getCookieinfo();
            Log.i("Services Status", "Cookie:" + cookie);
            if (!TextUtils.isEmpty(cookie)) {
                connection.setRequestProperty("Cookie", cookie);
            }
            connection.setRequestProperty("phone", UserInformation.getUserInfo().getUserPhone());
            connection.setRequestProperty("timestamp", aa);
            connection.setRequestProperty("nonce", aa1);
            connection.setRequestProperty("signature", Services.textToMD5L32(md5));
            connection.setRequestMethod("GET");
            connection.setReadTimeout(20000);
            connection.setConnectTimeout(20000);// 设置超时为20秒
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                // 得到登录时候的cookie值，再次调用时需要回传给服务器,如果需要可以判断当前是否为登录Url
                cookie = connection.getHeaderField("Set-Cookie");
                if (cookie != null && cookie.length() > 0) {
                    CookieInformation.setCookieInfo("cookie",cookie);
                }

                InputStreamReader isr = new InputStreamReader(
                        connection.getInputStream(), "utf-8");
                // 缓冲??
                BufferedReader br = new BufferedReader(isr);
                // 读取的行数据
                String line = "";
                // 循环读取所有服务器返回
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                Log.e("Services Status",
                        mUrl + "(Code:" + connection.getResponseCode() + ")");
            }

            // 得到服务器返回的结果
            return response;

        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }

    }

    // 获取对象集合
    public List<T> getJsonDatas() throws InterruptedException,
            ExecutionException {
        return new HttpGetObjectsAsyncTask().execute(mUrl).get();
    }

    // 获取单个对象
    public T getJsonData() throws InterruptedException, ExecutionException {
        return new HttpGetObjectAsyncTask().execute(mUrl).get();
    }

    // 返回相关的错误信息
    public String getErrorCode() {
        return mErrorCode;
    }

    public String getJsonString() throws ExecutionException, InterruptedException {
        return new HttpGetStringAsyncTask().execute(mUrl).get();
    }
    class HttpGetStringAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String response = readStream(params[0]);

            Log.i("Services Status", "服务器返回-->" + response);


            return response;
        }
    }
    class HttpGetObjectAsyncTask extends AsyncTask<String, Void, T> {
        @Override
        protected T doInBackground(String... params) {
            String response = readStream(params[0]);

            Log.i("Services Status", "服务器返回-->" + response);

            try {
                JSONObject object = new JSONObject(response);

                // 判断服务器返回状态
                if (object.getBoolean("Status")) {
                    // 获取到的数据对象
                    JSONObject jsonObject = object.getJSONObject("Data");
                    if (jsonObject.isNull("Obj") || TextUtils.isEmpty(jsonObject.getString("Obj"))) {
                        return (T) new StatusBoolean(jsonObject.getBoolean("Valid"), jsonObject.getString("Message"));
                    }
                    // 返回所有对象
                    return new JsonToObject<T>(mType, jsonObject).getObject();
                } else {
                    mErrorCode = object.getString("Code");
                    throw new Exception(object.getString("Code"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    class HttpGetObjectsAsyncTask extends AsyncTask<String, Void, List<T>> {
        @Override
        protected List<T> doInBackground(String... params) {
            String response = readStream(params[0]);

            Log.i("Services Status", "服务器返回-->" + response);

            try {
                // 转换数据
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("Status")) {
                    // 获取到的数据集合
                    JSONObject jsonObject = object.getJSONObject("Data");

                    if (jsonObject.isNull("Obj")) {
                        return new ArrayList<T>();
                    }
                    // 返回所有对象
                    return new JsonToObject<T>(mType, jsonObject).getObjects();
                } else {
                    mErrorCode = object.getString("Code");
                    throw new Exception(object.getString("Code"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
