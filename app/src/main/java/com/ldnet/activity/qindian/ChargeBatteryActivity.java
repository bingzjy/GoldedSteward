package com.ldnet.activity.qindian;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.QinDianService;
import com.ldnet.utility.ListViewAdapter;
import com.ldnet.utility.ViewHolder;
import com.tendcloud.tenddata.TCAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import news.yxd.com.chargesdk.bean.StationModel;
import news.yxd.com.chargesdk.connect.ConnectionUtils;
import news.yxd.com.chargesdk.connect.UdpServerIntance;


/**
 * Created by lee on 2017/9/5.
 */
public class ChargeBatteryActivity extends BaseActionBarActivity {

    private ConnectionUtils connection;
    private List<StationModel> modelList = new ArrayList<>();
    private ListViewAdapter<StationModel> mAdapter;
    private GridView gridView;
    private QinDianService service;
    private String orderID, money, serverOrderID, serverPayToken;
    private StationModel currentStateModel;
    private Float remindMoney;
    private PopupWindow mPopWindow;
    private TextView title;
    private ImageButton back;
    private final String WIFI_SSID = "\"Qindian\"";
    private Timer timer;
    private MyTimerTask task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qindian_charge_battery);
        //获取连接对象
        connection = ConnectionUtils.getIntance();

        service = new QinDianService(this);
        //获取当前余额
        service.getRemind(getRemindHandler);

        initView();
    }


    @Override
    protected void onResume() {
        super.onResume();

        //注册网络广播监听是否连接Qindian
        IntentFilter filter = new IntentFilter();
        filter.addAction(CONNECTIVITY_SERVICE);
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        this.registerReceiver(mReceiver, filter);

        TCAgent.onPageStart(this, "充电服务-充电：" + this.getClass().getSimpleName());
    }


    private void checkTimeOut() {
        try {
            timer = new Timer();
            task = new MyTimerTask();
            timer.schedule(task, 6000);
        } catch (Exception e) {
            Log.e("timer", e.getMessage());
        }
    }


    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            if (modelList.size() == 0) {
                handler.sendEmptyMessage(2);
            }
        }
    }

    private void startSearch() {
        checkTimeOut();
        showProgressDialog1();
        connection.startservice().setudpListener(new UdpServerIntance.OnUdpServerChangeListen() {
            @Override
            public void onUdpServerChangeListen() {
                //广播改变时调用
                modelList.clear();
                if (connection.BuildIcon().size() == 0) {
                    handler.sendEmptyMessage(1);
                } else {
                    modelList.addAll(connection.BuildIcon());
                    handler.sendEmptyMessage(0);
                }
            }
        });
    }


    private void initView() {
        title = (TextView) findViewById(R.id.tv_page_title);
        back = (ImageButton) findViewById(R.id.btn_back);
        gridView = (GridView) findViewById(R.id.gridView_model);
        title.setText("立即充电");
        back.setOnClickListener(this);
        mAdapter = new ListViewAdapter<StationModel>(this, R.layout.item_qindian_equipment, modelList) {
            @Override
            public void convert(ViewHolder holder, StationModel stationModel) {
                holder.setText(R.id.tv_equipment_id, stationModel.getStationID());
                holder.setText(R.id.tv_equipment_port, stationModel.getStationPort());
            }
        };

        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //扣费提示
                showPayPopWindow(modelList.get(position));
            }
        });
    }


    private void showPayPopWindow(final StationModel stationModel) {
        View popView = LayoutInflater.from(this).inflate(R.layout.pop_qindian_charge, null);
        mPopWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(popView);
        View rootview = LayoutInflater.from(this).inflate(R.layout.main, null);
        mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
        mPopWindow.setAnimationStyle(R.anim.fade_in);

        TextView id = (TextView) popView.findViewById(R.id.tv_charge_model_id);
        TextView pay = (TextView) popView.findViewById(R.id.tv_charge_pay_value);
        TextView balance=(TextView)popView.findViewById(R.id.tv_qindian_money_balance);
        balance.setText("¥ "+remindMoney);
        id.setText(stationModel.getStationID() + "-" + stationModel.getStationPort());
        pay.setText("¥ "+Float.parseFloat(stationModel.getStationMoney()));

        LinearLayout start = (LinearLayout) popView.findViewById(R.id.ll_payment_commit);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStateModel = stationModel;
                service.getOrderInfo(stationModel.getStationID(), stationModel.getStationPort(), getOrderHandle);

            }
        });

        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpaha(ChargeBatteryActivity.this, 1f);
            }
        });
        backgroundAlpaha(ChargeBatteryActivity.this, 0.5f);
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }

    //检测到设备
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog1();
            switch (msg.what) {
                case 0:
                    mAdapter.notifyDataSetChanged();
                    break;
                case 1: //搜索失败
                case 2: //搜索超时
                    showToast(getString(R.string.search_fail));
                    break;
            }
        }
    };

    //支付返回
    Handler payHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    try {
                        serverOrderID = jsonObject.getString("oid");
                        serverPayToken = jsonObject.getString("token");
                        if (!TextUtils.isEmpty(serverOrderID) && !TextUtils.isEmpty(serverPayToken)) {
                            showToast(getString(R.string.pay_ok));
                            mPopWindow.dismiss();
                            //充电
                            connection.charge(serverOrderID, serverPayToken, currentStateModel);
                            try {
                                gotoActivityAndFinish(ChargeMoneyComplete.class.getName(), null);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else {
                            mPopWindow.dismiss();
                            showToast(getString(R.string.pay_fail));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    //获取订单信息
    Handler getOrderHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    try {
                        orderID = jsonObject.getString("oid");
                        money = jsonObject.getString("money");
                        if (!TextUtils.isEmpty(orderID)) {
                            Float pay = Float.parseFloat(money);
                            if (pay > remindMoney) {
                                showToast("余额不足请充值");
                                try {
                                    gotoActivityAndFinish(ChargeMoneyActivity.class.getName(), null);
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                service.payChargeMoney("0", orderID, payHandler);
                            }
                        } else {
                            showToast("");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(getString(R.string.get_order_fail));
                    break;
            }

        }
    };

    //获取余额
    Handler getRemindHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    remindMoney = Float.parseFloat(msg.obj.toString());
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    //为弹出层设置遮罩层
    public void backgroundAlpaha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    //网络广播接收器，监听网络
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo.isConnected() && networkInfo.isAvailable()) {
                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String wifiName = wifiInfo.getSSID();
                if (wifiName.equals(WIFI_SSID)) {

                    startSearch();
                } else {
                    closeProgressDialog1();
                    Toast.makeText(ChargeBatteryActivity.this, getString(R.string.qindian_wifi_erroe), Toast.LENGTH_SHORT).show();
                }
            } else {
                closeProgressDialog1();
                Toast.makeText(ChargeBatteryActivity.this, getString(R.string.qindian_wifi_null), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        TCAgent.onPageEnd(this, "充电服务-充电：" + this.getClass().getSimpleName());
    }
}