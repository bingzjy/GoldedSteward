package com.ldnet.goldsteward.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ldnet.utility.Toast;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
    private static final String APP_ID = "wxa4207e39a8e5cf0f";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        regTiWx();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

    }

    private void regTiWx() {
        api = WXAPIFactory.createWXAPI(this, APP_ID);
        api.handleIntent(getIntent(), this);
    }


    //微信发送的请求将回调到onReq方法
    @Override
    public void onReq(BaseReq baseReq) {
        Log.e("weixin", "微信分享:" + baseReq.toString());
    }


    //发送到微信请求的响应结果将回调到onResp方法
    @Override
    public void onResp(BaseResp baseResp) {
        Log.e("weixin", "微信分享:" + baseResp.toString());
        String result = null;
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "分享成功";
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "分享取消";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "分享被拒绝";
                break;
            default:
                result = "分享返回";
                break;
        }
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        this.finish();
    }


}
