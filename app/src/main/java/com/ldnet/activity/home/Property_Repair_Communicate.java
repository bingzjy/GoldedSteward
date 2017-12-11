package com.ldnet.activity.home;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.base.AppUtils;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.*;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AcountService;
import com.ldnet.utility.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.third.signala.ConnectionState;
import com.third.signala.hubs.HubConnection;
import com.third.signala.hubs.HubInvokeCallback;
import com.third.signala.hubs.HubOnDataCallback;
import com.third.signala.hubs.IHubProxy;
import com.third.signala.transport.StateBase;
import com.third.signala.transport.longpolling.LongPollingTransport;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import okhttp3.Call;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Property_Repair_Communicate extends BaseActionBarActivity {
    private TextView tv_main_title;
    private ImageButton btn_back;
    private Services services;
    private LinearLayout ll_repair_communicate;
    private String mRepairId;
    private Boolean mRepaired = false;//已处理
    private SDCardFileCache mFileCaches;
    private HubConnection con = null; // SignalA 连接
    private IHubProxy hub = null;//Hub
    private EditText et_repair_send_content;
    private ScrollView sv_repair_communicate;
    private List<Communication> mDatas;
    private ImageButton ibtn_repair_send_picture;
    private Button btn_repair_close;
    private LinearLayout ll_repair_communicate_container;
    /* 请求码*/
    private static final int IMAGE_REQUEST_CODE = 1000;
    private static final int CAMERA_REQUEST_CODE = 1001;
    private static final String IMAGE_FILE_NAME = "RepairComplainImage.jpg";
    private static final String IMAGE_TEMP_FILE_NAME = "RCITemplate.jpg";
    private File mFileCacheDirectory;
    private Uri mImageUri;
    private Uri mTemplateImageUri;
    private String mImageId;
    private AcountService acountService;
    private List<Communication> mTemp;

    //初始化视图

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_property_repair_communicate);
        AppUtils.setupUI(findViewById(R.id.ll_property_repair_communicate), this);
        mRepairId = getIntent().getStringExtra("REPAIR_ID");
        String status = getIntent().getStringExtra("REPAIR_STATUS");
        if (!status.equals("0") && !status.equals("1")) {
            mRepaired = true;
        }
        mFileCaches = new SDCardFileCache(this);

        //初始化控件
        services = new Services();
        acountService=new AcountService(this);
        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.property_services_repair);

        //返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);

        //关闭按钮
        btn_repair_close = (Button) findViewById(R.id.btn_custom);
        btn_repair_close.setText(getString(R.string.repair_close));
        btn_repair_close.setVisibility(View.VISIBLE);

        //聊天列表
        sv_repair_communicate = (ScrollView) findViewById(R.id.sv_repair_communicate);
        ll_repair_communicate = (LinearLayout) findViewById(R.id.ll_repair_communicate);
        if (mRepaired) {
            ll_repair_communicate_container = (LinearLayout) findViewById(R.id.ll_repair_communicate_container);
            ll_repair_communicate_container.setVisibility(View.GONE);

            btn_repair_close.setVisibility(View.GONE);
        }
        ibtn_repair_send_picture = (ImageButton) findViewById(R.id.ibtn_repair_send_picture);
        et_repair_send_content = (EditText) findViewById(R.id.et_repair_send_content);
        et_repair_send_content.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //判断是否是“发送”键
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    SendMessage(false, "");
                    return true;
                }
                return false;
            }
        });
        initEvent();
        //初始化聊天列表
        initCommunicationList();

        //初始化SignalA
        //initSignalA();
    }

    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);
        ibtn_repair_send_picture.setOnClickListener(this);
        registerForContextMenu(ibtn_repair_send_picture);
        btn_repair_close.setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                try {
                    gotoActivityAndFinish(Property_Repair.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_custom:
                //弹出对话框？？？ -------------------
                ShowDialog();
                break;
            case R.id.ibtn_repair_send_picture:
                openContextMenu(ibtn_repair_send_picture);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            try {
                gotoActivityAndFinish(Property_Repair.class.getName(), null);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    //弹出评价窗口
    private void ShowDialog() {
        Builder builder = new Builder(this);
        builder.setTitle(R.string.appraise_please);
        View view = getLayoutInflater().inflate(R.layout.ratingbar_complain_repair, null);
        final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rb_repair_complain_score);
        builder.setView(view);
        builder.setPositiveButton(getResources().getString(R.string.sure_information), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                float scroe = ratingBar.getRating();
                CloseRepair(mRepairId, String.valueOf(scroe));
            }
        });
        builder.create();
        builder.show();
    }

    //报修关闭和评价
    public void CloseRepair(String id, String score) {
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 请求的URL
        final String url = Services.mHost + "API/Property/CloseRepairsMark";
        HashMap<String, String> extras = new HashMap<>();
        extras.put("Id", id);
        extras.put("Score", score);
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
                .addParams("Score", score)
                .build()
                .execute(new DataCallBack(this) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("asdsdasd", "111111111" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    acountService.setIntegralTip(new Handler(),url);
                                    try {
                                        HashMap<String, String> extras = new HashMap<String, String>();
                                        extras.put("LEFT", "LEFT");
                                        gotoActivityAndFinish(Property_Repair.class.getName(), extras);
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //报修&投诉沟通信息
    public void RCCommunicate(String id, String lastId) {
        // 请求的URL
        User user = UserInformation.getUserInfo();
        String url = Services.mHost + "API/Property/GetCommunicateByComplainIdOrRepairId/%s?residentId=%s&lastId=%s";
        url = String.format(url, id, user.UserId, lastId);
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
                .execute(new DataCallBack(this) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.d("asdsdasd", "111111111" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<Communication>>() {
                                    }.getType();
                                    mTemp = gson.fromJson(jsonObject.getString("Obj"), listType);
                                    mDatas.addAll(mTemp);
                                    while (mTemp != null && mTemp.size() == 10) {
                                        RCCommunicate(mRepairId, mDatas.get(0).Id);
                                        if (mTemp != null) {
                                            mDatas.addAll(0, mTemp);
                                        }
                                    }
                                    if (mDatas != null) {
                                        for (Communication c : mDatas) {
                                            BindingData(c);
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //报修沟通状态更新
    public void UpdateRCCommunicateStatus(String id) {
        // 请求的URL
        String url = Services.mHost + "API/Property/SetCommunicationStatus/%s?residentId=%s";
        url = String.format(url, id, UserInformation.getUserInfo().UserId);
        final String finalUrl = url;
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
                .execute(new DataCallBack(this) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("asdsdasd", "111111111" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    acountService.setIntegralTip(new Handler(),finalUrl);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void initCommunicationList() {
        //初始化服务
        services = new Services();
        mDatas = new ArrayList<Communication>();
        RCCommunicate(mRepairId, "");
        //更新状态
        UpdateRCCommunicateStatus(mRepairId);
    }

    //绑定数据
    private void BindingData(Communication c) {
        if (c.SendType) {
            LinearLayout communicateRight = (LinearLayout) getLayoutInflater().inflate(R.layout.item_communicate_right, null);
            ImageView iv = (ImageView) communicateRight.findViewById(R.id.iv_communication_icon);
            //设置用户头像
            if (!TextUtils.isEmpty(c.Img)) {
                ImageLoader.getInstance().displayImage(services.getImageUrl(c.Img), iv, imageOptions);
            }
            TextView tv = (TextView) communicateRight.findViewById(R.id.tv_communication_content);
            if (c.Type.equals("TEXT")) {
                tv.setText(c.Content);
            } else {
                String html = "<img src='" + services.getImageUrl(c.Content) + "'/>";
                tv.setText(Html.fromHtml(html, imageGetter, null));
            }
            ll_repair_communicate.addView(communicateRight);
        } else {
            LinearLayout communicateLeft = (LinearLayout) getLayoutInflater().inflate(R.layout.item_communicate_left, null);
            ImageView iv = (ImageView) communicateLeft.findViewById(R.id.iv_communication_icon);
            //设置用户头像
            if (!TextUtils.isEmpty(c.Img)) {
                ImageLoader.getInstance().displayImage(services.getImageUrl(c.Img), iv, imageOptions);
            }
            TextView tv = (TextView) communicateLeft.findViewById(R.id.tv_communication_content);
            if (c.Type.equals("TEXT")) {
                tv.setText(c.Content);
            } else {
                String html = "<img src='" + services.getImageUrl(c.Content) + "'/>";
                tv.setText(Html.fromHtml(html, imageGetter, null));
            }
            ll_repair_communicate.addView(communicateLeft);
        }

        //滚动条滚动到底部
        scrollToBottom(sv_repair_communicate, ll_repair_communicate);
    }

    //滚动条滚动到底部
    private static void scrollToBottom(final ScrollView scroll, final View inner) {
        Handler sHandler = new Handler();
        sHandler.post(new Runnable() {
            public void run() {
                if (scroll == null || inner == null) {
                    return;
                }
                int offset = inner.getHeight() - scroll.getHeight();
                if (offset < 0) {
                    offset = 0;
                }
                scroll.smoothScrollTo(0, offset);
            }
        });
    }

    //初始化SignalA
    private void initSignalA() {
        //建立连接
        con = new HubConnection(services.getCommunicationUrl(), getApplication(), new LongPollingTransport()) {
            @Override
            public void OnError(Exception exception) {
                //Toast.makeText(getApplication(), "SignalA Connection Exception -  " + exception.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void OnStateChanged(StateBase oldState, StateBase newState) {
                super.OnStateChanged(oldState, newState);
                switch (newState.getState()) {
                    case Connected:
                        //发送图片信息
                        if (!TextUtils.isEmpty(mImageId)) {
                            SendMessage(true, mImageId);
                            mImageId = "";
                        }
                        Log.i(this.getClass().getSimpleName(), "SignalA StateChanged---->" + con.getCurrentState().getState().toString());

                        User user = UserInformation.getUserInfo();
                        List<String> args = new ArrayList<String>(3);
                        args.add(mRepairId);
                        args.add(user.UserId);
                        args.add(user.PropertyId);
                        hub.Invoke("JoinCommplain", args, new HubInvokeCallback() {
                            @Override
                            public void OnResult(boolean succeeded, String response) {
                                Log.i(this.getClass().getSimpleName(), "SignalA Connection - JoinComplain Succeed!");
                            }

                            @Override
                            public void OnError(Exception ex) {
                                Log.i(this.getClass().getSimpleName(), "SignalA Connection - JoinComplain Error!");
                            }
                        });
                        break;
                    case Disconnected:
                        break;
                }
            }
        };

        //链接Hub
        try {
            hub = con.CreateHubProxy(services.COMMUNICATION_REPAIR_HUBNAME);

            //得到推送的消息
            hub.On("SendMesg", new HubOnDataCallback() {
                @Override
                public void OnReceived(JSONArray jsonArray) {
                    try {
                        //服务器推送的数据
                        Log.i("Services Status", "Services SendMesg:" + jsonArray.getString(0));

                        //解析JSON
                        Communication communication = new Communication();
                        JSONObject object = new JSONObject(jsonArray.getString(0)).getJSONObject("initData");
                        communication.SendType = Boolean.valueOf(object.getString("SendType"));
                        communication.Content = object.getString("Content");
                        communication.Type = object.getString("Type");
                        communication.Name = object.getString("Name");
                        communication.Img = object.getString("Img");
//                        communication.From = object.getString("From");
                        communication.Created = object.getString("Created");

                        mDatas.add(communication);

                        //绑定到列表
                        BindingData(communication);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

        //开始SignalA链接
        con.Start();
    }

    //发送消息到服务器
    private void SendMessage(Boolean isImage, String imageId) {
        if (con != null && hub != null) {
            String message = "";

            if (isImage) {
                message = imageId;
                Log.i("Services Status:", "ImageId--->" + imageId);
            } else {
                message = et_repair_send_content.getText().toString().trim();
            }

            User user = UserInformation.getUserInfo();
            List<String> args = new ArrayList<String>();
            args.add(mRepairId);
            args.add(message);
            args.add(user.UserId);
            args.add(user.PropertyId);
            if (isImage) {
                hub.Invoke("ImgMessage", args, new HubInvokeCallback() {
                    @Override
                    public void OnResult(boolean succeeded, String response) {
                        if (succeeded) {
                            Log.i("Services Status", "SignalA Connection - ImgMessage Succeed!");
                        }
                    }

                    @Override
                    public void OnError(Exception ex) {
                        Log.i("Services Status", "SignalA Connection - ImgMessage Error!");
                    }
                });
            } else {
                if (!TextUtils.isEmpty(message)) {
                    hub.Invoke("TextMessage", args, new HubInvokeCallback() {
                        @Override
                        public void OnResult(boolean succeeded, String response) {
                            if (succeeded) {
                                Log.i("Services Status", "SignalA Connection - TextMessage Succeed!");

                                et_repair_send_content.setText("");
                                 /* 隐藏软键盘 */
                                InputMethodManager imm = (InputMethodManager) et_repair_send_content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (imm.isActive()) {
                                    imm.hideSoftInputFromWindow(et_repair_send_content.getApplicationWindowToken(), 0);
                                }
                            }
                        }

                        @Override
                        public void OnError(Exception ex) {
                            Log.i("Services Status", "SignalA Connection - TextMessage Error!");
                        }
                    });
                }
            }
        }
    }

    //Activity Resume
    @Override
    protected void onResume() {
        super.onResume();
        initSignalA();
        Log.i(this.getClass().getSimpleName(), "SignalA CurrentState---->" + con.getCurrentState().getState().toString());
    }

    //Activity STOP
    @Override
    protected void onStop() {
        super.onStop();
        //Activity STOP时，停止链接
        con.Stop();
        Log.i(this.getClass().getSimpleName(), "SignalA CurrentState---->" + con.getCurrentState().getState().toString());
    }

    //ImageGetter?
    final Html.ImageGetter imageGetter = new Html.ImageGetter() {
        public Drawable getDrawable(String source) {
            Drawable drawable = null;
            Bitmap image;
            Integer parentcontentWidth = Utility.getScreenWidthforPX(getApplication()) - Utility.dip2px(getApplicationContext(), 96.0f);
            //判断SD卡里面是否存在图片文件
            image = mFileCaches.getFileFromFileCache(source);
            if (image != null) {
                image = mFileCaches.getFileFromFileCache(source);
                drawable = new BitmapDrawable(getResources(), image);
                Integer height = (int) (((float) parentcontentWidth / (float) drawable.getIntrinsicWidth()) * drawable.getIntrinsicHeight());
                drawable.setBounds(0, 0, parentcontentWidth, height);
                return drawable;
            } else {
                try {
                    image = new HttpImageAsyncTask().execute(source).get();
                    drawable = new BitmapDrawable(getResources(), image);
                    Integer height = (int) (((float) parentcontentWidth / (float) drawable.getIntrinsicWidth()) * drawable.getIntrinsicHeight());
                    drawable.setBounds(0, 0, parentcontentWidth, height);
                    return drawable;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            return drawable;
        }
    };


    class HttpImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap image;
            InputStream is;
            try {
                //得到图片的URL地址
                String url = params[0];
                HttpURLConnection connection = (HttpURLConnection) new URL(url)
                        .openConnection();
                is = new BufferedInputStream(connection.getInputStream());
                image = BitmapFactory.decodeStream(is);
                //写入缓存
                mFileCaches.putImageToFileCache(params[0], image);
                // 返回图片
                return image;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(getResources().getString(R.string.action_picture_select));
        menu.setHeaderIcon(R.drawable.me_setting_n);
        menu.add(0, 101, 1, R.string.action_pick);
        menu.add(0, 102, 2, R.string.action_capture);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mFileCacheDirectory = new File(Environment.getExternalStorageDirectory(), getPackageName());

            // 判断当前目录是否存在
            if (!mFileCacheDirectory.exists()) {
                mFileCacheDirectory.mkdir();
            }

            //图片的存储位置
            mImageUri = Uri.fromFile(new File(mFileCacheDirectory.getPath(), IMAGE_FILE_NAME));
            mTemplateImageUri = Uri.fromFile(new File(mFileCacheDirectory.getPath(), IMAGE_TEMP_FILE_NAME));

            switch (item.getItemId()) {
                case 101:
                    Intent intent_pick = new Intent(Intent.ACTION_PICK, null);
                    intent_pick.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent_pick, IMAGE_REQUEST_CODE);
                    con.Stop();
                    break;
                case 102:
                    Intent intent_capture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent_capture.putExtra(MediaStore.EXTRA_OUTPUT, mTemplateImageUri);
                    startActivityForResult(intent_capture, CAMERA_REQUEST_CODE);
                    con.Stop();
                    break;
                default:
                    break;
            }
            return super.onContextItemSelected(item);
        } else {
            showToast(R.string.not_find_phone_sd);
            return false;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(this.getClass().getSimpleName(), "---------onActivityResult");
        if (!con.getCurrentState().getState().equals(ConnectionState.Connected)) {
            con.Start();
        }
        if (RESULT_OK == resultCode) {
            //图片处理，另存为数据较小的JPEG文件，然后上传
            String imageAbsolutePath = "";
            if (requestCode == IMAGE_REQUEST_CODE) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imageAbsolutePath = cursor.getString(columnIndex);
                cursor.close();
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                imageAbsolutePath = mTemplateImageUri.getPath();
            }

            Bitmap image = getimage(imageAbsolutePath);
            if (image != null) {
                //上传服务器
                // 获取本地文件
                File file = new File(mImageUri.getPath());
                try {
                    // 将图片写入到文件
                    FileOutputStream fileOutStream = new FileOutputStream(file);
                    image.compress(Bitmap.CompressFormat.JPEG, 100,
                            fileOutStream);
                    fileOutStream.flush();
                    fileOutStream.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                showToast("上传图片中...");
                if (services.ping()) {
                    //上传到服务器
                    mImageId = services.Upload(this, mImageUri.getPath()).FileName;
                } else {
                    showToast("网络连接失败，请检查您的网络");
                    return;
                }
////                //缓存到本地
////                mImageId = services.getImageUrl(fileId);
//
//                SendMessage(true, fileId);
            }
        }
    }

    //根据分辨率压缩图片
    private Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 1280f;//这里设置高度为800f
        float ww = 800f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    //图片质量压缩
    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 300) {  //循环判断如果压缩后图片是否大于300kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
