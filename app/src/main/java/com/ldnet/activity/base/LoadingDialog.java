package com.ldnet.activity.base;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.ldnet.goldensteward.R;


public class LoadingDialog extends Dialog {
	private TextView mTextTv;

	public LoadingDialog(Context context) {
        super(context, R.style.MyDialogStyle);
		setContentView(R.layout.dialog_loading);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
		mTextTv = (TextView) findViewById(R.id.txt_dialog);
	}
	
	public void setText(String text) {
		mTextTv.setText(text);
	}
	
	public String getText() {
		return mTextTv.getText().toString();
	}
}
