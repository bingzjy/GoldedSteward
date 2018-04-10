package com.ldnet.service;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.entities.LCDevice;
import com.ldnet.utility.DataCallBack;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;


/**
 * @author zhangjinye
 * @name GoldedSteward2
 * @class name：com.ldnet.service
 * @class describe
 * @time 2018/3/27 9:34
 * @change
 * @chang time
 * @class describe
 */

public class LadderControlService extends BaseService {

    private static final String TAG = "LadderControlService";

    public LadderControlService(Context context) {
        this.mContext = context;
    }

    //获取梯控的钥匙串
    public void getLadderControlKey(final Handler handler) {
        String url = Services.mHost + "LadderControl/APP_YZ_GetKey?RoomID=%s";
        url = String.format(url, UserInformation.getUserInfo().getHouseId());
        OkHttpService.get(url).execute(new DataCallBack(mContext, handler) {
            @Override
            public void onResponse(String s, int i) {
                super.onResponse(s, i);
                Log.e(TAG, "获取梯控的钥匙串:" + s);

                try {
                    if (checkJsonDataSuccess(s, handler)) {
                        JSONObject json = new JSONObject(s);
                        JSONObject data = new JSONObject(json.getString("Data"));

                        Type type = new TypeToken<List<LCDevice>>() {
                        }.getType();
                        List<LCDevice> list = new Gson().fromJson(data.getString("Obj"), type);
                        if (list != null && list.size() > 0) {
                            Message message = handler.obtainMessage();
                            message.obj = list;
                            message.what = DATA_SUCCESS;
                            handler.sendMessage(message);
                        } else {
                            handler.sendEmptyMessage(DATA_SUCCESS_OTHER);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
