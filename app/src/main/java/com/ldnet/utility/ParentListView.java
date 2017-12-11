package com.ldnet.utility;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by lee on 2017/5/23.
 */
public class ParentListView extends ListView {
    public ParentListView(Context context) {
        super(context);
    }

    public ParentListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ParentListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
//                MeasureSpec.AT_MOST);
//        super.onMeasure(widthMeasureSpec, expandSpec);
//    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }



}
