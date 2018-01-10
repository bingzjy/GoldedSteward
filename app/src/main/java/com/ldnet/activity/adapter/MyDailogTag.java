package com.ldnet.activity.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.ldnet.activity.home.Property_Fee;
import com.ldnet.goldensteward.R;

/**
 * Created by lee on 2017/5/8.
 */
public class MyDailogTag {
    Activity activity;
    CustomAlertDialog.Dialogcallback dialogcallback;
    AlertDialog alertDialog;
    TextView close_dialog,warmPrompty;
    Button checkBtn;
    /**
     * init the dialog
     * @return
     */
    public MyDailogTag(final Activity act, final String fee) {
        this.activity = act;
        alertDialog = new AlertDialog.Builder(act).create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setCancelable(true);
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.ly_dialog_opendoor2);
        checkBtn = (Button) alertDialog.findViewById(R.id.tv_dialog_opendoor_goArrearage);
        close_dialog = (TextView) alertDialog.findViewById(R.id.tv_dialog_opendoor_close);
        warmPrompty = (TextView) alertDialog.findViewById(R.id.tv_dialog_opendoor_arrearage);
       if(fee.equals("")){
           checkBtn.setText("    关闭    ");
           warmPrompty.setText("您目前暂无欠费");
       }else{
           warmPrompty.setText("温馨提示：您已欠费" + fee + "元");
       }
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setAttributes(lp);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fee.equals("")){
                    Intent intent = new Intent(act, Property_Fee.class);
                    act.startActivity(intent);
                    act.overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                    act.finish();
                    alertDialog.dismiss();
                }else {
                    alertDialog.dismiss();
                }

            }
        });
        close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    public void show() {
        alertDialog.show();
    }
}
