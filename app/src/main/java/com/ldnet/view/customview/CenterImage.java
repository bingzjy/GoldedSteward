package com.ldnet.view.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ldnet.goldensteward.R;

/**
 * @author zhangjinye
 * @name GoldedSteward2
 * @class name：com.ldnet.utility
 * @class describe
 * @time 2018/1/25 9:48
 * @change
 * @chang time
 * @class describe
 */

public class CenterImage extends ImageView {
    private Paint paint;
    private boolean isCenterImgShow;
    private Bitmap bitmap;
    private int imageType;

    public void setCenterImgShow(int type) {
        imageType = type;
        if (imageType == 1) {  //无货图标
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_no_stock);
            invalidate();
        } else if (imageType == 2) { //下架图标
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_sold_out);
            invalidate();
        }
    }

    public CenterImage(Context context) {
        super(context);
        init();
    }

    public CenterImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CenterImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (imageType >= 1 && bitmap != null) {
            canvas.drawBitmap(bitmap, getMeasuredWidth() / 2 - bitmap.getWidth() / 2, getMeasuredHeight() / 2 - bitmap.getHeight() / 2, paint);
        }
    }

}
