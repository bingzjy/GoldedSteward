package com.ldnet.utility;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;

import android.util.Log;
import com.ldnet.activity.Browser;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.home.Notification;
import com.ldnet.activity.home.Property_Fee;

import com.ldnet.activity.home.Property_Services;
import com.ldnet.activity.mall.Orders;
import com.ldnet.activity.me.Feedback;
import com.ldnet.activity.me.Message;
import com.ldnet.entities.Msg;
import com.ldnet.entities.MsgCenter;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zxs on 2015/12/24.
 */
public class MyNetWorkBroadcastReceive extends BroadcastReceiver {
    Context mContext;
    String type = null;
    String cid = null;
    MsgCenter msgCenter = new MsgCenter();
    String center = "";

    public static ArrayList<onNewMessageListener> msgListeners = new ArrayList<onNewMessageListener>();

    public static interface onNewMessageListener {
        public abstract void onNewMessage(String message);
    }

    public MyNetWorkBroadcastReceive() {

    }

    //（0 通知公告,1 投诉,2 报修,3 沟通,4 缴费,5 网页,6 意见反馈,7 订单,8 其他,9消息中心）
    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.e("asdsdasd", "NetWorkBroadcastReceive.......open:" + extras);   //{"url":"","type":9}
            Intent intentOther = new Intent(context, MainActivity.class);
            intentOther.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intentOther);
        }
    }

    private KeyguardManager km;
    private KeyguardManager.KeyguardLock kl;
    private PowerManager pm;
    private PowerManager.WakeLock wl;


    private void wakeAndUnlock(boolean b) {
        if (b) {
            //获取电源管理器对象
            pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
            wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            //点亮屏幕
            wl.acquire();
            //得到键盘锁管理器对象
            km = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
            kl = km.newKeyguardLock("unLock");
            //解锁
//            kl.disableKeyguard();
        } else {
            //锁屏
            kl.reenableKeyguard();
            //释放wakeLock，关灯
            wl.release();
        }
    }

}
