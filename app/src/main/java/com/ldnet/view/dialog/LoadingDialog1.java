package com.ldnet.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.ldnet.goldensteward.R;

public class LoadingDialog1 extends Dialog {
	private ImageView iv;
	private AnimationDrawable anim;

	public LoadingDialog1(Context context) {
        super(context, R.style.MyDialogStyle);
		setContentView(R.layout.dialog_loading1);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
		iv = (ImageView) findViewById(R.id.splash_iv);
		anim = (AnimationDrawable) iv.getDrawable();

		ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				anim.start();
				return true; // 必须要有这个true返回
			}
		};
		iv.getViewTreeObserver().addOnPreDrawListener(preDrawListener);
	}
	
}
