package com.ldnet.activity.home;

import android.content.Context;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.ldnet.utility.AppUtils;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.commen.Services;
import com.ldnet.entities.Communication;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.sharepreferencedata.UserInformation;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tendcloud.tenddata.TCAgent;
import com.third.signala.hubs.HubConnection;
import com.third.signala.hubs.HubInvokeCallback;
import com.third.signala.hubs.HubOnDataCallback;
import com.third.signala.hubs.IHubProxy;
import com.third.signala.transport.StateBase;
import com.third.signala.transport.longpolling.LongPollingTransport;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Property_Communicate extends BaseActionBarActivity {
    private TextView tv_main_title;
    private ImageButton btn_back;
    private Services services;
    private LinearLayout ll_communicate;
    private HubConnection con = null; // SignalA 连接
    private IHubProxy hub = null;//Hub
    private EditText et_send_content;
    private ScrollView sv_communicate;
    private List<Communication> mDatas;
    private Button btn_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_property_communicate);
        AppUtils.setupUI(findViewById(R.id.ll_property_communicate), this);
        //初始化控件
        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.property_services_communicate);

        //返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);

        //初始化服务
        mDatas = new ArrayList<Communication>();
        services = new Services();

        //按钮
        btn_send = (Button) findViewById(R.id.btn_send);
        //聊天列表
        sv_communicate = (ScrollView) findViewById(R.id.sv_communicate);
        ll_communicate = (LinearLayout) findViewById(R.id.ll_communicate);
        et_send_content = (EditText) findViewById(R.id.et_send_content);
        initEvent();
    }

    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);
        btn_send.setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                try {
                    gotoActivityAndFinish(Property_Services.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_send:
                SendMessage();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            try {
                gotoActivityAndFinish(Property_Services.class.getName(), null);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }

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
            tv.setText(c.Content);
            ll_communicate.addView(communicateRight);
        } else {
            LinearLayout communicateLeft = (LinearLayout) getLayoutInflater().inflate(R.layout.item_communicate_left, null);
            ImageView iv = (ImageView) communicateLeft.findViewById(R.id.iv_communication_icon);
            //设置用户头像
            if (!TextUtils.isEmpty(c.Img)) {
                ImageLoader.getInstance().displayImage(services.getImageUrl(c.Img), iv, imageOptions);
            }
            TextView tv = (TextView) communicateLeft.findViewById(R.id.tv_communication_content);
            tv.setText(c.Content);
            ll_communicate.addView(communicateLeft);
        }

        //滚动条滚动到底部
        scrollToBottom(sv_communicate, ll_communicate);
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
                        User user = UserInformation.getUserInfo();
                        List<String> args = new ArrayList<String>();
                        args.add(user.UserId);
                        hub.Invoke("AddConnections", args, new HubInvokeCallback() {
                            @Override
                            public void OnResult(boolean succeeded, String response) {
                                Log.i(this.getClass().getSimpleName(), "SignalA Connection - AddConnections Succeed!");
                            }

                            @Override
                            public void OnError(Exception ex) {
                                Log.i(this.getClass().getSimpleName(), "SignalA Connection - AddConnections Error!");
                            }
                        });
                        List<String> args1 = new ArrayList<String>();
                        args1.add(user.UserId);
                        args1.add(user.PropertyId);
                        args1.add(user.CommunityId);
                        hub.Invoke("HistoryMessage", args1, new HubInvokeCallback() {
                            @Override
                            public void OnResult(boolean succeeded, String response) {
                                Log.i(this.getClass().getSimpleName(), "SignalA Connection - AddConnections Succeed!");
                            }

                            @Override
                            public void OnError(Exception ex) {
                                Log.i(this.getClass().getSimpleName(), "SignalA Connection - AddConnections Error!");
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
            hub = con.CreateHubProxy(services.COMMUNICATION_HUBNAME);

            //得到推送的消息
            hub.On("sendMessage", new HubOnDataCallback() {
                @Override
                public void OnReceived(JSONArray jsonArray) {
                    try {
                        Log.e("Services Status", "Services sendMessage  jsonArray:" + jsonArray);
                        if (jsonArray != null && jsonArray.getString(0) != null) {

                            //服务器推送的数据
                            Log.e("Services Status", "Services sendMessage:" + jsonArray.getString(0));

                            //解析JSON
                            Communication communication = new Communication();
                            JSONObject object = new JSONObject(jsonArray.getString(0));
                            communication.SendType = Boolean.valueOf(object.getString("isresident"));
                            communication.Content = object.getString("message");
                            communication.Type = "TEXT";
                            communication.Name = object.getString("name");
                            communication.Img = object.getString("avatar");
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            communication.Created = object.getString("time");

                            //绑定到列表
                            BindingData(communication);
                            mDatas.add(communication);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            //得到推送的历史消息
            hub.On("getMessage", new HubOnDataCallback() {
                @Override
                public void OnReceived(JSONArray jsonArray) {
                    try {
                        if (jsonArray != null && jsonArray.getString(0) != null) {
                            //服务器推送的数据
                            Log.e("Services Status", "Services getMessage:" + jsonArray.getString(0));
                            //解析JSON
                            Communication communication = new Communication();
                            JSONArray objects = new JSONArray(jsonArray.getString(0));
                            for (int i = 0; i < objects.length(); i++) {
                                JSONObject object = objects.getJSONObject(i);

                                communication.SendType = Boolean.valueOf(object.getString("isresident"));
                                communication.Content = object.getString("message");
                                communication.Type = "TEXT";
                                communication.Name = object.getString("name");
                                communication.Img = object.getString("avatar");
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                communication.Created = object.getString("time");

                                //绑定到列表
                                BindingData(communication);
                                mDatas.add(communication);
                            }
                        }
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
    private void SendMessage() {
        if (con != null && hub != null) {
            String message = et_send_content.getText().toString().trim();

            User user = UserInformation.getUserInfo();
            List<String> args = new ArrayList<String>();
            args.add("true");
            args.add(user.UserId);
            args.add(user.PropertyId);
            args.add(user.PropertyId);
            args.add(user.CommunityId);
            args.add(message);

            Log.e("aaa",user.UserId+"   "+user.PropertyId+"   "+user.CommunityId);

            if (!TextUtils.isEmpty(message)) {
                hub.Invoke("SendMessage", args, new HubInvokeCallback() {
                    @Override
                    public void OnResult(boolean succeeded, String response) {
                        if (succeeded) {
                            Log.i("Services Status", "SignalA Connection - SendMessage Succeed!");
                            et_send_content.setText("");
                                 /* 隐藏软键盘 */
                            InputMethodManager imm = (InputMethodManager) et_send_content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm.isActive()) {
                                imm.hideSoftInputFromWindow(et_send_content.getApplicationWindowToken(), 0);
                            }
                        }
                    }

                    @Override
                    public void OnError(Exception ex) {
                        Log.i("Services Status", "SignalA Connection - SendMessage Error!");
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSignalA();
        TCAgent.onPageStart(this, "物业服务-沟通" + this.getClass().getSimpleName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Activity STOP时，停止链接
        if (con!=null){
            con.Stop();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "物业服务-沟通" + this.getClass().getSimpleName());
    }
}
