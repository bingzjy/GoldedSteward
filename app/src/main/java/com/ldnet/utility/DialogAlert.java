package com.ldnet.utility;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.ldnet.goldensteward.R;

/**
 * Created by Alex on 2015/9/28.
 */
public class DialogAlert extends Dialog {
    private Context mContext;
    private Services services;
    private OnAlertDialogListener goodsDialogListener;
    private String mAlertMessage;

    private TextView dialog_alert_message;
    private Button dialog_button_cancel;
    private Button dialog_button_comfirm;


    //构造函数
    public DialogAlert(Context context, String message, OnAlertDialogListener customDialogListener) {
        super(context, R.style.dialog_fullscreen);
        //
        mContext = context;
        //提示消息
        mAlertMessage = message;
        //确定按钮响应事件
        this.goodsDialogListener = customDialogListener;
    }

    //onCreate方法
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alert);

        //提示信息
        dialog_alert_message = (TextView) findViewById(R.id.dialog_alert_message);
        dialog_alert_message.setText(mAlertMessage);

        //取消按钮
        dialog_button_cancel = (Button) findViewById(R.id.dialog_button_cancel);
        dialog_button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDialog();
            }
        });

        //确定按钮
        dialog_button_comfirm = (Button) findViewById(R.id.dialog_button_comfirm);
        dialog_button_comfirm.setOnClickListener(clickListener);
    }

    //按钮事件监听
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            goodsDialogListener.GotoActivity();
            DialogAlert.this.dismiss();
        }
    };

    //定义回调事件
    public interface OnAlertDialogListener {
        void GotoActivity();
    }

    //取消Dialog
    private void closeDialog() {
        this.cancel();
    }
}
