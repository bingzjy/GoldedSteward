package com.ldnet.utility;

import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.ldnet.activity.qindian.ChargeBatteryActivity;
import com.ldnet.goldensteward.R;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by lee on 2017/9/23
 */

public class QinDianNetState extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(context.getClass().getSimpleName().equals(ChargeBatteryActivity.class.getSimpleName())){
            WifiManager wifiManager = (WifiManager)context.getSystemService(WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo();
            String wifiName = info.getSSID();
            if (!wifiName.equals("Qindian")) {
                com.ldnet.utility.Toast.makeText(context, "请链接到Qindain", 1000).show();
            }
        }
    }
}
