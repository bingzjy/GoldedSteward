package com.ldnet.goldsteward.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.chinaums.pppay.unify.UnifyPayPlugin;
import com.chinaums.pppay.unify.WXPayResultListener;
import com.ldnet.goldensteward.R;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "WXPayEntryActivity";


    private static WXPayResultListener mListener;
    private IWXAPI api;

    private void setListener(WXPayResultListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_results);
        api = WXAPIFactory.createWXAPI(this, UnifyPayPlugin.getInstance(this).getAppId());
        api.handleIntent(getIntent(), this);
        Log.d("zhangxiulu", "WXPayEntryActivity onCreate");
        setListener(UnifyPayPlugin.getInstance(WXPayEntryActivity.this).getWXListener());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.e("weixin", "errorCode:" + resp.errCode);
        Log.e("weixin", "errorStr:" + resp.errStr);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (mListener != null) {
                mListener.onResponse(WXPayEntryActivity.this, resp);
            }
            if (resp.errCode != 0) // 支付失败
            {
                finish();
            } else {
                Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
                finish();
            }
            // resp.errCode == -1 原因：支付错误,可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等
            // resp.errCode == -2 原因 用户取消,无需处理。发生场景：用户不支付了，点击取消，返回APP
         /*   if (resp.errCode == 0) // 支付成功
            {
                Log.d("zhangxiulu", "mListener not null:" + (mListener != null));
                if(mListener != null){
                    mListener.onSuccess(WXPayEntryActivity.this, resp);
                }
//                Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
            }
            else
            {
                if(mListener != null){
                    mListener.onError(WXPayEntryActivity.this, resp.errCode, resp.errStr, resp);
                }
//                Toast.makeText(this, getString(R.string.action_cancel) + resp.errCode + "test", Toast.LENGTH_SHORT)
//                    .show();
//
                finish();
            }*/
        }
    }


}