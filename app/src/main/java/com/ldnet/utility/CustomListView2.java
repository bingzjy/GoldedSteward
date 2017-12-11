package com.ldnet.utility;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by zjy on 2017/5/23.
 */
public class CustomListView2 extends ListView {
    public CustomListView2(Context context) {
        super(context);
    }

    public CustomListView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomListView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
