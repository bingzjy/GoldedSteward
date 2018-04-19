package com.ldnet.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.ldnet.goldensteward.R;


public class NormalLoadingDialog extends Dialog {
	private TextView mTextTv;

	public NormalLoadingDialog(Context context) {
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
