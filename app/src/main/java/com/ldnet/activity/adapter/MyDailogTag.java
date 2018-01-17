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

import pl.droidsonroids.gif.GifImageView;

import static android.R.attr.type;

/**
 * Created by lee on 2017/5/8.
 */
public class MyDailogTag {
    Activity activity;
    DialogOpenDoorCallBack dialogOpenDoorCallBack;
    AlertDialog alertDialog;
    TextView close_dialog,warmPrompty,tvOpenState;
    Button checkBtn;
    int btnType;
    String fee;
    /**
     * init the dialog
     * @return
     */
    public MyDailogTag(final Activity act, final String feeValue) {
        this.activity = act;
        this.fee=feeValue;
        alertDialog = new AlertDialog.Builder(act).create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setCancelable(true);

        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.ly_dialog_opendoor2);
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setAttributes(lp);

        checkBtn = (Button) alertDialog.findViewById(R.id.tv_dialog_opendoor_goArrearage);
        close_dialog = (TextView) alertDialog.findViewById(R.id.tv_dialog_opendoor_close);
        warmPrompty = (TextView) alertDialog.findViewById(R.id.tv_dialog_opendoor_arrearage);
        tvOpenState=(TextView)alertDialog.findViewById(R.id.tv_dialog_opendoor_state);
        checkBtn.setText("    关闭    ");

        initView(fee);
        updateView();
        initEvent();

    }


    public void initView(String feeValue){
        if(feeValue.equals("")){
            warmPrompty.setVisibility(View.GONE);
            checkBtn.setVisibility(View.GONE);
        }else{
            warmPrompty.setVisibility(View.VISIBLE);
            warmPrompty.setText("温馨提示：您已欠费" + fee + "元");
        }
    }


    private void initEvent(){
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogOpenDoorCallBack.clickEvent(btnType);
            }
        });

        close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }


    public void startDialogShow(final String feeValue){
       this.fee=feeValue;
        initView(this.fee);
    }


    public void updateView(){
        GifImageView gifImageView =(GifImageView)alertDialog.findViewById(R.id.imgv_gif_open_icon);
        if (btnType==0){ //开门等待
            checkBtn.setVisibility(View.GONE);
            tvOpenState.setText("开门中...");
            gifImageView.setImageResource(R.drawable.shortcut_open_wait);
        }else if (btnType==1){ //开门成功  ，分欠费、不欠费两种
            checkBtn.setVisibility(View.VISIBLE);
            checkBtn.setText("去看看");
            tvOpenState.setText("开门成功");
            gifImageView.setImageResource(R.drawable.shortcut_opendoor_success);

            if(fee.equals("")){
                warmPrompty.setVisibility(View.VISIBLE);
                warmPrompty.setText("请随手关门");
                checkBtn.setVisibility(View.GONE);
            }else{
                warmPrompty.setVisibility(View.VISIBLE);
                warmPrompty.setText("温馨提示：您已欠费" + fee + "元");
            }

        }else if (btnType==2){ //开门失败
            checkBtn.setVisibility(View.VISIBLE);
            checkBtn.setText("重新试试");
            tvOpenState.setText("开门失败");
            gifImageView.setImageResource(R.drawable.shortcut_open_fail);
        }
    }

    public void setType(int type){
        this.btnType=type;
        updateView();
    }

    public interface DialogOpenDoorCallBack{
        void clickEvent(int type);
    }

    public void setDialogcallback(DialogOpenDoorCallBack callBack){
        this.dialogOpenDoorCallBack=callBack;
    }


    public void show() {
        alertDialog.show();
    }

    public void hide(){
        if (alertDialog!=null&&alertDialog.isShowing()){
            alertDialog.dismiss();
        }
    }

    public boolean isShowing(){
        if (alertDialog!=null){
            return alertDialog.isShowing();
        }
        return false;
    }
}
