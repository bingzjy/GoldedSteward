package com.ldnet.activity.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.intelligoo.sdk.LibDevModel;
import com.intelligoo.sdk.LibInterface;
import com.intelligoo.sdk.ScanCallBackSort;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.LCDevice;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.LadderControlService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
*
* 测试梯控
* lcDeviceHashMap：后台请求设备信息<String,LCDevice>
*
* */
public class LadderControlActivity extends BaseActionBarActivity {
    @BindView(R.id.tv_ladder_info)
    TextView tvLadderInfo;
    @BindView(R.id.tv_scan_info)
    TextView tvScanInfo;
    @BindView(R.id.tv_matched_device)
    TextView tvMatchedDevice;
    //梯控开门回调
    LibInterface.ManagerCallback openCallBack = new LibInterface.ManagerCallback() {
        @Override
        public void setResult(int i, Bundle bundle) {
            if (i == 0x00) {
                showToast("开门 Success！");
            } else {
                if (i == 48) {
                    showToast("Result Error Timer Out");
                } else {
                    showToast("Failure:" + i);
                }
            }
        }
    };

    private LadderControlService ladderControlService;
    private HashMap<String, LCDevice> lcDeviceHashMap = new HashMap<>();
    //梯控扫描结果
    ScanCallBackSort scanCallBack = new ScanCallBackSort() {
        @Override
        public void onScanResult(ArrayList<Map<String, Integer>> arrayList) {

            if (arrayList != null && arrayList.size() > 0) {

                for (Map<String, Integer> map : arrayList) {

                    addText(tvScanInfo, map.toString());

                    for (String sn : map.keySet()) {
                        LCDevice lcDevice = lcDeviceHashMap.get(sn);
                        if (lcDevice != null) {
                            LibDevModel libDevModel = getLibDevModel(lcDevice);
                            addText(tvMatchedDevice, "LibDevModel.devSn:" + libDevModel.devSn +
                                    " \nLibDevModel.devType:" + libDevModel.devType +
                                    "\nLibDevModel.eKey:" + libDevModel.eKey +
                                    "\nLibDevModel.devMac:" + libDevModel.devMac);
                            openElevator(libDevModel);
                        }
                    }
                }
            } else {
                showToast("扫描完毕，未找到设备");
            }
        }

        @Override
        public void onScanResultAtOnce(String s, int i) {

        }
    };


    //请求服务器获取钥匙串
    Handler handlerGetLadderKey = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    List<LCDevice> lcDevices = (List<LCDevice>) msg.obj;
                    lcDeviceHashMap = getHashLCDevice(lcDevices);
                    startScanElevator();
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    showToast("钥匙串为空");
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ladder_control);
        ButterKnife.bind(this);
        ladderControlService = new LadderControlService(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgressDialog();
        ladderControlService.getLadderControlKey(handlerGetLadderKey);
    }

    //调用梯控扫描
    private void startScanElevator() {
        int ret = LibDevModel.scanDeviceSort(this, true, 800, scanCallBack);
        if (ret != 0) {
            showToast("扫描梯控设备失败");
        }
    }

    //调用梯控开门
    private void openElevator(LibDevModel device) {
        int ret = LibDevModel.openDoor(this, device, openCallBack);
        if (ret != 0) {
            showToast("开门失败");
        }
    }


    //创建开门设备
    private LibDevModel getLibDevModel(LCDevice lcDevice) {
        LibDevModel libDevModel = new LibDevModel();
        libDevModel.devMac = lcDevice.devMac;
        libDevModel.devSn = lcDevice.devSN;
        libDevModel.eKey = lcDevice.devEkey;
        libDevModel.devType = Integer.parseInt(lcDevice.devType);

        return libDevModel;
    }


    private HashMap<String, LCDevice> getHashLCDevice(List<LCDevice> list) {
        HashMap<String, LCDevice> hashMap = new HashMap<>();
        for (LCDevice lcDevice : list) {
            hashMap.put(lcDevice.devSN, lcDevice);
            addText(tvLadderInfo, lcDevice.toString());
        }
        return hashMap;
    }


    private void addText(TextView textView, String addContent) {
        if (textView.getText() == null) {
            textView.setText(addContent);
        } else {
            textView.setText(textView.getText().toString() + "\n" + addContent);
        }
    }


}
