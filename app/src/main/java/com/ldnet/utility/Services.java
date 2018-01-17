package com.ldnet.utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.google.gson.Gson;
import com.ldnet.activity.base.LoadingDialog;
import com.ldnet.entities.*;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.attr.bitmap;
import static com.unionpay.mobile.android.global.a.s;

public class Services {

    // 服务器主机地址及端口
    // 测试环境
//    public final static String mHost1 = "http://192.168.0.105:8080/";
//    public final static String mHost = "http://192.168.0.105:8042/";
//    public final static String mImageHost = "http://192.168.0.105:8042/api/file/images/%s";
//    public final static String mCommunicationUrl = "http://192.168.0.105:8080";
//    public final static String mPaidUrl = "http://p.goldwg.com/Property/Fee/AppPay?feeHistoryIds=%s&payerId=%s";
//    public final static String mInvitationUrl = "http://192.168.0.105:8082/Invitation?UserID=%s";
//    public final static String mPayCallBackTaoBao = "http://192.168.0.105:8042/BOrder/PayCallBack";

    // 正式环境
    public final static String mHost1 = "http://p.goldwg.com/";
    public final static String mHost = "http://apifive.goldwg.com/";
    public final static String mImageHost = "http://apifive.goldwg.com/api/file/images/%s";
    public final static String mCommunicationUrl = "http://p.goldwg.com";
    public final static String mPaidUrl = "http://p.goldwg.com/Property/Fee/AppPay?feeHistoryIds=%s&payerId=%s";
    public final static String mInvitationUrl = "http://www.goldwg.com:85/Invitation?UserID=%s";
    public final static String mPayCallBackTaoBao = "http://apifive.goldwg.com/BOrder/PayCallBack";

    // 分享好友页面
    public final static String mSharePageUrl = "http://www.goldwg.com:85/Invitation?UserID=%s";
    //分页常量
    public final static Integer PAGE_SIZE = 10;
    //Hubs
    public final static String COMMUNICATION_HUBNAME = "chat";
    public final static String COMMUNICATION_REPAIR_HUBNAME = "repairsHub";
    public final static String COMMUNICATION_COMPLAIN_HUBNAME = "complainHub";
    public static String TOKEN ="";
    public static List<Integer> integerList = new ArrayList<Integer>();
    public static boolean net = true;
    public static boolean notification = false;
    public static boolean fee = false;
    public static String orientation, fitment, roomType, rentType, roomConfig;
    public static String comment = "";
    public static String visionCode = "";
    public static String visionName = "";
    public static LoadingDialog dialog;


    //绑定房子，传递参数名
    public final static String COMMUNITY_ID="COMMUNITY_ID";
    public final static String COMMUNITY_NAME="COMMUNITY_NAME";
    public final static String ROOM_ID="ROOM_ID";
    public final static String ROOM_NAME="ROOM_NAME";
    public final static String CLASS_FROM="CLASS_FROM";
    public final static String OWNER_TEL="OWNER_TEL";
    public final static String OWNER_ID="OWNER_ID";
    public final static String OWNER_FLAG="OWNER_FLAG";
    public final static String TO_APPLY="APPLY";
    public final static String RESIDENT_TYPE="RESIDENT_TYPE";
    public final static String RESIDENT_DATE_START="RESIDENT_DATE_START";
    public final static String RESIDENT_DATE_END="RESIDENT_DATE_END";


    public void showProgressDialog(String str, Context context) {
        if (dialog == null) {
            dialog = new LoadingDialog(context);
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.setText(str);
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void closeProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    // 获取分页中，每页的条数
    public static Integer getPageSize() {
        return PAGE_SIZE;
    }

    // 获取当前服务器的Host信息，用户判断cookie是否存在
    public static String getServerDomain() {
        try {
            String status = new URL(mHost).getHost();
            return status;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取服务器最新的APP版本
    public UpdateInformation getVersion() {
        try {
            String url = mHost + "api/Common/GetLastAPPId/%s";
            url = String.format(url, APP_NAME);
            HttpGetData<UpdateInformation> request = new HttpGetData<UpdateInformation>(UpdateInformation.class, url);
            UpdateInformation result = request.getJsonData();
            if (result != null) {
                return result;
            } else {
                networkException();
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //获取邀请好友的网络URL
    public String getInvitation(Boolean isApp) {
        String url = String.format(mInvitationUrl, UserInformation.getUserInfo().UserId);
        if (url != null) {
            if (isApp) {
                return url + "&IsApp=true";
            }
            return url;
        } else {
            networkException();
            return null;
        }

    }

    public boolean netWorkConnected(){
        Context context = GSApplication.getInstance();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void networkException() {
        Context context = GSApplication.getInstance();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    Toast.makeText(GSApplication.getInstance(), "服务器异常，稍后重试！", 1000).show();
                }else{
                    Toast.makeText(GSApplication.getInstance(), "请检查您的网络状态，稍后重试！", 1000).show();
                }
            }
        }

    }

    // 验证长度是否在一个范围内
    public static boolean isWithinScopeOfLength(String s, int minLength, int maxLength) {
        int mLength = s.length();
        boolean b = false;
        if (mLength >= minLength && mLength <= maxLength) {
            b = true;
        }
        return b;
    }

    //支付宝支付回调函数
    public static String getPayCallBackByTaobao() {
        return mPayCallBackTaoBao;
    }

    // 支付宝回调函数
    public static String getPayCallBack() {
        // 请求的URL
        String url = mHost + "BAccount/PayCallBack";
        return url;
    }

    // 验证电话号码
    public static boolean isPhone(String s) {
        Pattern p = Pattern.compile("^[1][3-9][0-9]{9}$"); // 验证手机号
        Matcher m = p.matcher(s);
        boolean b = false;
        b = m.matches();
        return b;
    }

    //APP信息的APP_NAME，和服务器必须相同
    private static final String APP_NAME = "jpgjyzAPP";

    //获取APP下载地址
    public static String getAPPDownloadUrl(String id) {
        String url = mHost + "API/File/GetMobileAppApk/%s";
        url = String.format(url, id);
        return url;
    }

    // 获取图片的网络URL
    public static String getImageUrl(String imageId) {
        String status = String.format(mImageHost, imageId);
        if (status != null) {
            Log.d("asdsdasd", status);
            return status;
        } else {
            return null;
        }
    }

    // 验证密码规则
    public static boolean validPassword(String password) {
        Pattern p = Pattern.compile("^[A-Za-z0-9]{6,22}$"); // 密码规则数字或字母，长度6-22位
        Matcher m = p.matcher(password);
        boolean b = false;
        b = m.matches();
        return b;
    }

    //获取沟通的URL，金牌管家的物业网站URL
    public static String getCommunicationUrl() {
        return mCommunicationUrl;
    }

    //转bitmap
    public static Bitmap convertStringToIcon(String st) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap =
                    BitmapFactory.decodeByteArray(bitmapArray, 0,
                            bitmapArray.length);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    //上传图片
    public FileUpload Upload(Context context, String fileName) {
        try {
            //文件上传的路径
            String url = mHost + "API/File/UploadFile/";
            // 发起请求
            HttpPostData<FileUpload> httpContext = new HttpPostData<FileUpload>(FileUpload.class, url);
            FileUpload status = httpContext.PostFile(fileName);
            if (status != null) {
                if (status.Valid) {
                    IntegralTip(url);
                }
                return status;
            } else {
                networkException();
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public static void IntegralTip(String pUrl) {

        String url1 = "";
        try {
            pUrl = new URL(pUrl).getPath();
            url1 = Services.mHost + "API/Prints/Add/" + UserInformation.getUserInfo().UserId + "?route=" + pUrl;
            String aa = Services.timeFormat();
            String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
            String aa2 = url1;
            String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
            OkHttpUtils.get().url(url1)
                    .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                    .addHeader("timestamp", aa)
                    .addHeader("nonce", aa1)
                    .addHeader("signature", Services.textToMD5L32(md5))
                    .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo()).build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                        }

                        @Override
                        public void onResponse(String s, int i) {
                            Log.d("asdsdasd", "111111111" + s);
                            try {
                                JSONObject json = new JSONObject(s);
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (json.getBoolean("Status")) {
                                    if (jsonObject.getBoolean("Valid")) {
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    // 根据商家分类ID 获取商品列表
    public List<Goods> getGoodsList(String lastId, Integer pageSize, String retailerId, String typeId) {
        try {
            // 请求的URL
            String url = mHost + "BGoods/App_GetGoodsList_ByTypeID?LastID=%s&PageCnt=%s&RetailerID=%s&TypeID=%s";
            url = String.format(url, lastId, pageSize, retailerId, typeId);
            HttpGetData<Goods> request = new HttpGetData<Goods>(Goods.class, url);
            List<Goods> result = request.getJsonDatas();
            if (result != null) {
                return result;
            } else {
                networkException();
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // 根据商家ID获取商品分类
    public List<RetailerGoodsType> getGoodsTypes(String retailerID) {
        try {
            // 请求的URL
            String url = mHost + "BGoods/App_GetGoodsTypes?RetailerID=%s";
            url = String.format(url, retailerID);
            HttpGetData<RetailerGoodsType> request = new HttpGetData<RetailerGoodsType>(RetailerGoodsType.class, url);
            List<RetailerGoodsType> result = request.getJsonDatas();
            if (result != null) {
                return result;
            } else {
                networkException();
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String subStr(String str) {
        String[] strs = str.split("T");
        String[] strs1 = strs[1].split(":");
        return strs[0] + "  " + strs1[0] + ":" + strs1[1];
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    //验证非空
    public static boolean isEmpty(EditText view, Context context, String str) {
        if (TextUtils.isEmpty(view.getText().toString().trim())) {
            Toast.makeText(context, str, 1000).show();
            return false;
        }
        return true;
    }

    // 验证是否为空
    public static boolean isNotNullOrEmpty(String s) {
        boolean b = false;
        if (s != null && !s.equalsIgnoreCase("")) {
            b = true;
        }
        return b;
    }

    public static String timeFormat() {
        String timeStamp = null;
        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dff.setTimeZone(TimeZone.getTimeZone("UTC"));
        String ee = dff.format(new Date());
        Date d;
        try {
            d = dff.parse(ee);
            long l = d.getTime();
            timeStamp = String.valueOf(l);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStamp;
    }

    public static String textToMD5L32(String plainText) {
        String result = null;
        //首先判断是否为空
        if (TextUtils.isEmpty(plainText)) {
            return "";
        }
        try {
            plainText = plainText.replace(" ", "");
            String encode = URLEncoder.encode(plainText, "UTF-8");
            Services.sort(encode.toUpperCase());
            //首先进行实例化和初始化
            MessageDigest md = MessageDigest.getInstance("MD5");
            //得到一个操作系统默认的字节编码格式的字节数组
            byte[] btInput = Services.sort(encode.toUpperCase()).getBytes();
            //对得到的字节数组进行处理
            md.update(btInput);
            //进行哈希计算并返回结果
            byte[] btResult = md.digest();
            //进行哈希计算后得到的数据的长度
            StringBuffer sb = new StringBuffer();
            for (byte b : btResult) {
                int bt = b & 0xff;
                if (bt < 16) {
                    sb.append(0);
                }
                sb.append(Integer.toHexString(bt));
            }
            result = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String sort(String str) {
        char[] chars = str.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    public static String json(HashMap<String, String> extras) {
        JSONObject jsonObject1 = new JSONObject();
        boolean flag = false;
        for (int i = 0; i < extras.size(); i++) {
            // 添加参数
//            if (extras != null && extras.size() > 0) {
            Iterator<Map.Entry<String, String>> iterator = extras.entrySet()
                    .iterator();
            while (iterator.hasNext()) {
                @SuppressWarnings("rawtypes")
                Map.Entry entry = (Map.Entry) iterator.next();

                try {

                    String key = entry.getKey().toString();
                    String value = entry.getValue().toString();
                    Object obj = null;
                    Log.d("PARAMS[OBJ]--->", key+"--"+value);
                    try {
                        if (value.contains("[")) {
                            obj = new JSONArray(value);
                            flag = true;
                        } else {
                            obj = new JSONObject(value);
                            flag = true;
                        }
                        Log.d("PARAMS[OBJ]--->", obj.toString());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    if (obj != null) {
                        jsonObject1.put(key, obj);
                        //return jsonObject1.toString() + "\"\"";
                    } else {
                        jsonObject1.put(key, value);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("PARAMS--->", jsonObject1.toString());
//        }
        if (flag) {
            return jsonObject1.toString() + "\"\"";
        } else {
            return jsonObject1.toString();
        }
    }

    public void PostError(String logger) {
        try {
            // 请求的URL
            String url = mHost + "API/Common/SetLog";
            //获取当前应用的版本号
            GSApplication application = GSApplication.getInstance();
            String appVersion = application.getPackageManager().getPackageInfo(application.getPackageName(), 0).versionName;
            String message = "业主App" + appVersion + Build.MODEL + "Android " + Build.VERSION.RELEASE + UserInformation.getUserInfo().UserName + UserInformation.getUserInfo().UserPhone;
            // android.os.Build.VERSION.RELEASE获取版本号
            //  android.os.Build.MODEL 获取手机型号
            // 请求参数
            HashMap<String, String> extras = new HashMap<>();
            extras.put("Message", message.replaceAll(" ", ""));
            extras.put("Logger", logger.replaceAll(" ", ""));
            String aa = Services.timeFormat();
            String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
            String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + Services.json(extras) + TOKEN;
//            Log.d("asdsdasd12132", url + "--" + aa + "--" + aa1 + "--" + md5 + "--" + Services.json(extras) + "--" + logger);
//            Log.d("asdsdasd12132","=-=-=-="+TOKEN);
            OkHttpUtils.post().url(url)
                    .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                    .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                    .addHeader("timestamp", aa)
                    .addHeader("nonce", aa1)
                    .addHeader("signature", Services.textToMD5L32(md5))
                    .addParams("Message", message.replaceAll(" ", ""))
                    .addParams("Logger", logger.replaceAll(" ", ""))
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            Log.e("asdsdasd12132", "PostError--onError");
                        }

                        @Override
                        public void onBefore(Request request, int id) {
                            super.onBefore(request, id);
                            Log.e("asdsdasd12132", "PostError--before");
                        }

                        @Override
                        public void onResponse(String s, int i) {
                            Log.e("asdsdasd12132", "111111111" + s);
                            try {
                                JSONObject json = new JSONObject(s);
                                JSONObject jsonObject = new JSONObject(json.getString("Data"));
                                if (json.getBoolean("Status")) {
                                    if (jsonObject.getBoolean("Valid")) {
                                        Gson gson = new Gson();
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

    // 添加URL参数，使用在Post的时候来构造POST参数
    private String setUrlParms(Map<String, String> parms) {
        // Post 参数
        String sParams = "";

        // 循环添加URL参数
        for (Map.Entry<String, String> entry : parms.entrySet()) {
            try {
                sParams += entry.getKey() + "="
                        + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        // 返回构造参数
        return sParams.substring(0, sParams.length() - 1);

    }

    /**
     * 图片按比例大小压缩方法
     *
     * @param image （根据Bitmap图片压缩）
     * @return
     */
    public static Bitmap compressScale(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        // 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
        if (baos.toByteArray().length / 1024 > 1024) {
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        Log.i("asfasfasfasfasf", w + "---------------" + h);
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        // float hh = 800f;// 这里设置高度为800f
        // float ww = 480f;// 这里设置宽度为480f
        float hh = 512f;
        float ww = 512f;
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) { // 如果高度高的话根据高度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be; // 设置缩放比例
        // newOpts.inPreferredConfig = Config.RGB_565;//降低图片从ARGB888到RGB565

        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);

        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩

        //return bitmap;
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;

        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 图片按比例大小压缩方法
     *
     * @param srcPath （根据路径获取图片并压缩）
     * @return
     */
    public static Bitmap getimage(Activity context, String srcPath) {

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        float width = dm.widthPixels * dm.density;
        float height = dm.heightPixels * dm.density;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    public final boolean ping() {
        String result = null;
        try {
            String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 1 " + ip);// ping网址3次
            // 读取ping的内容，可以不加
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            Log.d("------ping-----", "result content : " + stringBuffer.toString());
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            Log.d("----result---", "result = " + result);
        }
        return false;

    }

}


