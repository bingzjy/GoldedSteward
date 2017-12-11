package com.ldnet.activity.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.ldnet.goldensteward.R;

/**
 * Created by lee on 2016/8/3.
 */
public class MyDialog2 {
    Context context;
    Dialogcallback dialogcallback;
    AlertDialog alertDialog;
    TextView log_off_cancel;
    TextView log_off_confirm;
    TextView tv_dialog_title;
    /**
     * init the dialog
     * @return
     */
    public MyDialog2(final Context con,final String type) {
        this.context = con;
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.show();
      //  alertDialog.setCanceledOnTouchOutside(false);
       // alertDialog.setCancelable(false);

        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.ly_off);
        alertDialog.findViewById(R.id.line).setVisibility(View.GONE);
        tv_dialog_title =(TextView)alertDialog.findViewById(R.id.tv_dialog_title);
        tv_dialog_title.setText("为保证小区安全，请验证后进行其他操作");
        log_off_cancel =(TextView)alertDialog.findViewById(R.id.log_off_cancel);
        log_off_cancel.setVisibility(View.GONE);
        log_off_confirm =(TextView)alertDialog.findViewById(R.id.log_off_confirm);
        log_off_confirm.setText("验证");
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setAttributes(lp);
        log_off_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogcallback.dialogdo(type);
                dismiss();
            }
        });

//        log_off_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogcallback.dialogDismiss();
//                dismiss();
//            }
//        });
    }
    /**
     * 设定一个interfack接口,使mydialog可以處理activity定義的事情
     * @author sfshine
     *
     */
    public interface Dialogcallback {
        public void dialogdo(String type);
        public void dialogDismiss();
    }
    public void setDialogCallback(Dialogcallback dialogcallback) {
        this.dialogcallback = dialogcallback;
    }

    public void show() {
        alertDialog.show();
    }
    public void hide() {
        alertDialog.hide();
    }
    public void dismiss() {
        alertDialog.dismiss();
    }
}