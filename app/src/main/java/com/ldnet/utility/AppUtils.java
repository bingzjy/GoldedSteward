package com.ldnet.utility;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by Administrator on 14-10-23.
 */
public class AppUtils {

    /** 点击EditText以外的地方, 隐藏软键盘和功能选项
     *  eg:setupUI(findViewById(R.id.scrollview_smallnote));
     * @param view 该activity的根view
     * @param c
     */
    public static void setupUI(View view, final Activity c) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(c);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView, c);
            }
        }
    }

    /**
     * 隐藏输入法面板
     */
    public static void hideKeyboard(Activity c) {
        InputMethodManager imm = ((InputMethodManager) c.getSystemService(c.INPUT_METHOD_SERVICE));
        if (imm != null && c.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(c.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
