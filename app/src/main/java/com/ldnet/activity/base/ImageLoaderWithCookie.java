package com.ldnet.activity.base;

import android.content.Context;
import android.util.Log;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by lee on 2016/12/15.
 */
public class ImageLoaderWithCookie extends BaseImageDownloader {

    public ImageLoaderWithCookie(Context context) {
        super(context);
    }

    @Override
    protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
        // Super...
        String[] str = extra.toString().split(",");
        HttpURLConnection connection = super.createConnection(url, extra);
        connection.setRequestProperty("phone", str[0]);//extra就是SessionId，何时传入，见第三步
        connection.setRequestProperty("timestamp", str[1]);//extra就是SessionId，何时传入，见第三步
        connection.setRequestProperty("nonce", str[2]);//extra就是SessionId，何时传入，见第三步
        connection.setRequestProperty("signature", "");//extra就是SessionId，何时传入，见第三步
        return connection;
    }
}