package com.ldnet.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.dh.bluelock.imp.BlueLockPubCallBackBase;
import com.dh.bluelock.object.LEDevice;
import com.dh.bluelock.pub.BlueLockPub;
import com.dh.bluelock.util.Constants;
import com.ldnet.goldensteward.R;
import com.ldnet.service.EntranceGuardService;
import com.ldnet.utility.KeyCache;

import java.util.HashMap;
import java.util.Set;

import cn.finalteam.galleryfinal.widget.GFImageView;
import pl.droidsonroids.gif.GifImageView;

import static com.unionpay.mobile.android.global.a.B;

/**
 * @author lpf
 * @since 2017/8/9
 */
public class EntranceGuardSplash extends Activity {
    private TextView mTvKeyChainPacket, tvHouseInfo;
    private ImageView imageViewBack;
    private BluetoothAdapter mBTAdapter;
    private TextView mTvOpenDoorTip;
    private HashMap<String, LEDevice> mScanDeviceResult = new HashMap<>();
    private String mDeviceId;
    private BlueLockPub mBlueLockPub;
    private String deviceID;
    private boolean opened;
    private EntranceGuardService entranceGuardService;
    private GifImageView mGifImage;
    private BlueToothReceiver mReceviver;
    private static final String TAG = "EntranceGuardSplash";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance_guard_splash2);
        entranceGuardService = new EntranceGuardService(this);

        initView();
        requestPermission();

        //注册蓝牙广播
        IntentFilter filter=new IntentFilter();
        filter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        mReceviver=new BlueToothReceiver();
        this.registerReceiver(mReceviver,filter);
    }

    private void initView() {
        tvHouseInfo = (TextView) findViewById(R.id.tv_house_info);
        mTvOpenDoorTip = (TextView) findViewById(R.id.tv_show_open_door_information);
        imageViewBack = (ImageView) findViewById(R.id.imageView_back);
        mGifImage=(GifImageView)findViewById(R.id.imgv_gif_open_icon);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (KeyCache.getCurrentHouse() != null) {
            tvHouseInfo.setText(KeyCache.getCurrentHouse());
        }
    }


    private void initBlueTooth() {
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mBlueLockPub = BlueLockPub.bleLockInit(EntranceGuardSplash.this);
        LocalCallBack localCallBack = new LocalCallBack();
        mBlueLockPub.setResultCallBack(localCallBack);
        if (mBTAdapter.isEnabled()) {
            mTvOpenDoorTip.setText("正在连接蓝牙门禁...");
            startScan();
        } else {
            Toast.makeText(EntranceGuardSplash.this, "请手动打开蓝牙", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 开始扫描蓝牙设备
     */
    private void startScan(){
        //再次启动扫描
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBlueLockPub.setLockMode(Constants.LOCK_MODE_MANUL, null, false);
                mBlueLockPub.scanDevice(3000);
            }
        }).start();
    }

    /**
     * 是否开启蓝牙的对话框
     */
    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EntranceGuardSplash.this);
        builder.setMessage("是否打开蓝牙设备?")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        dialogInterface.dismiss();
                    }
                })

                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        mBTAdapter.enable();
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }



    //定义回调类（开门回调、扫描回调、扫描完毕回调）
    class LocalCallBack extends BlueLockPubCallBackBase {
        @Override
        public void openCloseDeviceCallBack(int i, int i1, String... strings) {
            mTvOpenDoorTip.setText("欢迎回家");
            mGifImage.setImageResource(R.drawable.shortcut_opendoor2);
            //添加开门日志
           entranceGuardService.EGLog(deviceID, handlerEGlog);
        }

        @Override
        public void scanDeviceCallBack(LEDevice leDevice, int i1, int i2) {
            if (leDevice != null) {
                mDeviceId = leDevice.getDeviceId();
                mScanDeviceResult.put(mDeviceId, leDevice);
            } else {
                Toast.makeText(EntranceGuardSplash.this, "搜索设备失败-_-", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        public void scanDeviceEndCallBack(int j) {
            Set<String> keySet = KeyCache.getKeyCache();
            if (keySet == null || keySet.size() == 0) {
                Toast.makeText(EntranceGuardSplash.this, "当前房屋暂无钥匙，无法开门", Toast.LENGTH_SHORT).show();
            } else {
                for (String keyMsg : keySet) {
                    Log.e(TAG, keyMsg);
                    String[] msgArr = keyMsg.split(",");
                    String keyId = msgArr[0];
                    LEDevice device = mScanDeviceResult.get(keyId);
                    if (device != null) {
                        Log.e(TAG, "开门");
                        opened = true;
                        deviceID = device.getDeviceId();
                        device.setDevicePsw(msgArr[1]);
                        mBlueLockPub.oneKeyOpenDevice(device, device.getDeviceId(), device.getDevicePsw());
                        break;
                    }
                }

                if (!opened) {
                    Toast.makeText(EntranceGuardSplash.this, "请靠近设备再试", Toast.LENGTH_SHORT).show();
                    //再次启动扫描
                    startScan();
                }
            }
        }
    }


    private void requestPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 110);
            } else {
                //权限已获取，做自己的处理
                initBlueTooth();
            }
        } else {
            //权限已获取，做自己的处理
            initBlueTooth();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 110) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //权限已获取，做自己的处理
                    initBlueTooth();
                } else {
                    Toast.makeText(EntranceGuardSplash.this, "请手动开启位置权限", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    Handler handlerEGlog = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 2000:
                    Toast.makeText(EntranceGuardSplash.this,"已添加开门日志",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void stopBlueScan(){
        if(mBTAdapter!=null&&mBTAdapter.enable()){
            mBTAdapter.cancelDiscovery();
            mBTAdapter.disable();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopBlueScan();
    }

    class BlueToothReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e(TAG,"bluetooth 广播接收了");
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.STATE_OFF);
//            int STATE_OFF = 10; //蓝牙关闭
//            int STATE_ON = 12; //蓝牙打开
//            int STATE_TURNING_OFF = 13; //蓝牙正在关闭
//            int STATE_TURNING_ON = 11; //蓝牙正在打开
            switch (state){
                case 10:
                    mTvOpenDoorTip.setText("请打开蓝牙");
                    break;
                case 12:
                    mTvOpenDoorTip.setText("正在连接蓝牙门禁...");
                    startScan();
                    break;
                case 13:
                    mTvOpenDoorTip.setText("蓝牙正在关闭");
                    break;
                case 11:
                    mTvOpenDoorTip.setText("蓝牙正在打开");
                    break;
            }
        }
    }

}
