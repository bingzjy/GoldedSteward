package com.ldnet.utility;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import com.ldnet.activity.adapter.MyDialog;
import com.ldnet.entities.StatusBoolean;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//获取服务器数据
public class HttpPostData<T extends Serializable> {

    // 获取数据的URL
    private String mUrl;

    // 传递的参数
    private String mJsonParams;

    // 服务器异常
    private String mErrorCode;

    // 泛型的具体类型
    private Class<T> mType;
    private Context con;
    private static final String BOUNDARY = "--------httppost123";

    // 构造方法
    public HttpPostData(Class<T> type, String url) {

        mUrl = url;
        mType = type;
    }

    //上传文件
    private String PostFileToStream(String fileName) {
        HttpURLConnection connection = null;
        try {
            String cookie;
            // 清空服务器返回的内容
            String response = "";
            String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
            String aa = Services.timeFormat();
            String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + "" + Services.TOKEN;
            // 请求服务器数据
            connection = (HttpURLConnection) new URL(mUrl)
                    .openConnection();
            // 请求方式
            connection.setRequestMethod("POST");
            // 超时时间
            connection.setReadTimeout(5 * 1000);
            connection.setConnectTimeout(5 * 1000);
            // POST请求必须设置允许输入、输出,不允许缓存
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            // 设置请求头
            cookie = CookieInformation.getUserInfo().getCookieinfo();
            Log.i("Services Status", "Cookie:" + cookie);
            if (!TextUtils.isEmpty(cookie)) {
                connection.setRequestProperty("Cookie", cookie);
            }
            connection.setRequestProperty("phone", UserInformation.getUserInfo().getUserPhone());
            connection.setRequestProperty("timestamp", aa);
            connection.setRequestProperty("nonce", aa1);
            connection.setRequestProperty("signature", Services.textToMD5L32(md5));
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            //开始上传文件
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

            //文件流
            File file = new File(fileName);
            outputStream.writeBytes("--" + BOUNDARY + "\r\n");
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + file.getName()
                    + "\"; filename=\"" + URLEncoder.encode(file.getName()) + "\"\r\n");
            outputStream.writeBytes("Content-Type:image/jpeg\r\n");
            outputStream.writeBytes("\r\n");
            FileInputStream in = new FileInputStream(file);
            byte[] b = new byte[1024];
            int n;
            while ((n = in.read(b)) != -1) {
                outputStream.write(b, 0, n);
            }
            in.close();
            //outputStream.write(out.toByteArray());
            outputStream.writeBytes("\r\n");

            outputStream.writeBytes("--" + BOUNDARY + "\r\n");
            outputStream.writeBytes("Content-Disposition: form-data; name=\"contentType\"\r\n");
            outputStream.writeBytes("\r\n");
            outputStream.writeBytes("image/jpeg\r\n");
            //文件結束
            outputStream.writeBytes("--" + BOUNDARY + "--" + "\r\n");
            outputStream.writeBytes("\r\n");
            outputStream.flush();
            outputStream.close();
            Log.d("assdddddddddddd", "4444444444444" + connection.getResponseCode());
            // 实际上开发发送数据是从下边的getInputStream开始的
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                // 得到登录时候的cookie值，再次调用时需要回传给服务器,如果需要可以判断当前是否为登录Url
                cookie = connection.getHeaderField("Set-Cookie");
                if (cookie != null && cookie.length() > 0) {
                    CookieInformation.setCookieInfo("cookie", cookie);
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
                isr.close();
                // 得到服务器返回的结果
            } else {
                Log.e("Services Status",
                        mUrl + "(Code:" + connection.getResponseCode() + ")");
            }
            return response;
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // POST DATA TO SERVICES
    private String PostDataToStream() {
        String cookie;
        try {
            // 清空服务器返回的内容
            String response = "";

            // 请求服务器数据
            HttpURLConnection connection = (HttpURLConnection) new URL(mUrl)
                    .openConnection();
            // 请求方式
            connection.setRequestMethod("POST");
            // 超时时间
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            // 设置请求头
            cookie = CookieInformation.getUserInfo().getCookieinfo();

            Log.i("Services Status", "Cookie:" + cookie);

            if (!TextUtils.isEmpty(cookie)) {
                connection.setRequestProperty("Cookie", cookie);
            }

            connection.setRequestProperty("connection", "keep-Alive");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length",
                    String.valueOf(mJsonParams.getBytes().length));
            connection
                    .setRequestProperty("user-agent",
                            "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
            // POST请求必须设置允许输入、输出
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // 写入内容到流中
            OutputStream outStream = (OutputStream) connection
                    .getOutputStream();
            outStream.write(mJsonParams.getBytes());
            outStream.flush();

            // 实际上开发发送数据是从下边的getInputStream开始的
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                // 得到登录时候的cookie值，再次调用时需要回传给服务器,如果需要可以判断当前是否为登录Url
                cookie = connection.getHeaderField("Set-Cookie");
                if (cookie != null && cookie.length() > 0) {
                    CookieInformation.setCookieInfo("cookies", cookie);
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

    // POST FROM DATA TO SERVICES
    private String PostFormToStream() {
        String cookie;
        try {
            // 清空服务器返回的内容
            String response = "";

            // 请求服务器数据
            HttpURLConnection connection = (HttpURLConnection) new URL(mUrl)
                    .openConnection();
            // 请求方式
            connection.setRequestMethod("POST");
            // 超时时间
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            // 设置请求头
            cookie = CookieInformation.getUserInfo().getCookieinfo();


            Log.i("Services Status", "Cookie:" + cookie);

            if (!TextUtils.isEmpty(cookie)) {
                connection.setRequestProperty("Cookie", cookie);
            }

            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length",
                    String.valueOf(mJsonParams.getBytes().length));

            // POST请求必须设置允许输入、输出
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);

            // 写入内容到流中
            OutputStream outStream = (OutputStream) connection
                    .getOutputStream();
            outStream.write(mJsonParams.getBytes());
//            outStream.flush();
//            outStream.close();
            // 实际上开发发送数据是从下边的getInputStream开始的
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                // 得到登录时候的cookie值，再次调用时需要回传给服务器,如果需要可以判断当前是否为登录Url
                cookie = connection.getHeaderField("Set-Cookie");
                if (cookie != null && cookie.length() > 0) {
                    CookieInformation.setCookieInfo("cookies", cookie);
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

//                isr.close();
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

    //上传文件

    public T PostFile(String fileName) throws Exception {
        return new HttpPostFileAsyncTask().execute(fileName).get();
    }

    // 返回对象
    public String Post(String jsonParams) throws Exception {
        // 传递的参数
        mJsonParams = jsonParams;
        // 异步执行Post
        Log.d("asdsdasd12132", mUrl + "--");
        return new HttpPostAsyncTask().execute(mUrl).get();
    }

    // 返回集合
    public List<T> Posts(String jsonParams) throws Exception {
        // 传递的参数
        mJsonParams = jsonParams;
        // 异步执行Post
        return new HttpPostsAsyncTask().execute(mUrl).get();
    }

    public String PostString(String jsonParams) throws Exception {
        // 传递的参数
        mJsonParams = jsonParams;
        // 异步执行Post
        return new HttpPostStringAsyncTask().execute(mUrl).get();
    }

    // 返回服务器错误信息
    public String getErrorCode() {
        return mErrorCode;
    }

    class HttpPostStringAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            // 提交数据到服务器并得到返回值
            String response = PostFormToStream();

            Log.i("Services Status", "服务器返回-->" + response);

            return response;
        }
    }

    class HttpPostsAsyncTask extends AsyncTask<String, Void, List<T>> {
        @Override
        protected List<T> doInBackground(String... params) {

            // 提交数据到服务器并得到返回值
            String response = PostFormToStream();

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

    class HttpPostAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            // 提交数据到服务器并得到返回值
            String response = PostFileToStream(params[0]);
            Log.i("Services Status", "服务器返回-->" + response);
            String fileName = "";
            try {
                fileName = new JSONObject(new JSONObject(response).getString("Obj")).getString("FileName");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return fileName;
        }
    }

    class HttpPostFileAsyncTask extends AsyncTask<String, Void, T> {

        @Override
        protected T doInBackground(String... params) {
            // 提交数据到服务器并得到返回值
            String response = PostFileToStream(params[0]);

            Log.i("Services Status", "服务器返回-->" + response);
            try {
                if (response != null) {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("Status")) {
                        // 获取到的数据集合
                        JSONObject jsonObject = object.getJSONObject("Data");
                        if (jsonObject.isNull("Obj")) {
                            return (T) new StatusBoolean(jsonObject.getBoolean("Valid"), jsonObject.getString("Message"));
                        }
                        // 返回所有对象
                        return new JsonToObject<T>(mType, jsonObject).getObject();
                    } else {
                        mErrorCode = object.getString("Code");
                        throw new Exception(mErrorCode);
                    }
                } else {
                    Log.d("assdddddddddddd", "2222222222222");
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
