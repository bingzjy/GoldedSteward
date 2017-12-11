package com.ldnet.utility;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.mall.GoodsList;
import com.ldnet.entities.Goods;
import com.ldnet.entities.Stock;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.GoodsService;
import com.ldnet.service.OrderService;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import static com.ldnet.goldensteward.R.id.tv_goods_list;
import static com.ldnet.map.ChString.To;

/**
 * Created by Alex on 2015/9/28.
 */
public class DialogGoods extends Dialog {

    private ImageView dialog_goods_thumbnail;
    private TextView dialog_goods_title;
    private TextView dialog_goods_price;
    private RadioGroup dialog_goods_spec;
    private TextView dialog_goods_stock;
    private Button dialog_goods_minus;
    private EditText dialog_goods_numbers;
    private Button dialog_goods_plus;
    private TextView dialog_goods_total_price;
    private Button dialog_button_cancel;
    private Button dialog_button_comfirm;

    private Integer mNumber;
    private String mSPECId;

    private List<Stock> mStocks;
    private Goods mGoods;
    private Context mContext;
    private Services services;
    private OnGoodsDialogListener goodsDialogListener;
    private String mTitle;

    private Window window = null;
    String aa = Services.timeFormat();
    String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
    protected DisplayImageOptions imageOptions;
    private OrderService orderService;
    //构造函数
    public DialogGoods(Context context, Goods goods, OnGoodsDialogListener customDialogListener, String title) {
        super(context, R.style.dialog_fullscreen);
        mContext = context;
        //商品信息
        mGoods = goods;
        //服务--获取规格
        services = new Services();
        //确定按钮响应事件
        this.goodsDialogListener = customDialogListener;
        mTitle = title;
        imageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_info)     //url爲空會显示该图片，自己放在drawable里面的
                .showImageOnFail(R.drawable.default_info)                //加载图片出现问题，会显示该图片
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .extraForDownloader(UserInformation.getUserInfo().UserPhone + "," + aa + "," + aa1)
                .build();
    }

    //onCreate方法
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_goods);
        orderService=new OrderService(mContext);
        windowDeploy(0,0);

        this.setCancelable(true);
        this.setCanceledOnTouchOutside(true);

        //商品图片
        dialog_goods_thumbnail = (ImageView) findViewById(R.id.dialog_goods_thumbnail);
        ImageLoader.getInstance().displayImage(services.getImageUrl(mGoods.getThumbnail()), dialog_goods_thumbnail, imageOptions);

        //商品名称
        dialog_goods_title = (TextView) findViewById(R.id.dialog_goods_title);
        dialog_goods_title.setText(mGoods.T);

        //金牌价
        dialog_goods_price = (TextView) findViewById(R.id.dialog_goods_price);
        dialog_goods_price.setText("￥" + String.valueOf(mGoods.GP));

        //规格
        dialog_goods_spec = (RadioGroup) findViewById(R.id.dialog_goods_spec);

        //获取最新商品信息
        orderService.getStock(mGoods.GID,handler);

        //库存
        dialog_goods_stock = (TextView) findViewById(R.id.dialog_goods_stock);

        //商品数量
        dialog_goods_numbers = (EditText) findViewById(R.id.dialog_goods_numbers);
        //得到商品数量
        mNumber = Integer.valueOf(dialog_goods_numbers.getText().toString().trim());
        //减
        dialog_goods_minus = (Button) findViewById(R.id.dialog_goods_minus);
        dialog_goods_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGoodsNumbers(true);
            }
        });
        //加
        dialog_goods_plus = (Button) findViewById(R.id.dialog_goods_plus);
        dialog_goods_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGoodsNumbers(false);
            }
        });

        //总价
        dialog_goods_total_price = (TextView) findViewById(R.id.dialog_goods_total_price);
        BigDecimal pSum = new BigDecimal(mGoods.FGP);
        dialog_goods_total_price.setText("￥" + pSum.multiply(new BigDecimal(mNumber.floatValue())).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());

        //取消
        dialog_button_cancel = (Button) findViewById(R.id.dialog_button_cancel);
        dialog_button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDialog();
            }
        });
        dialog_button_comfirm = (Button) findViewById(R.id.dialog_button_comfirm);
        dialog_button_comfirm.setText(mTitle);
        dialog_button_comfirm.setOnClickListener(clickListener);
    }

    //设置窗口显示
    public void windowDeploy(int x, int y){
        window = getWindow(); //得到对话框
        window.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
        window.setBackgroundDrawableResource(R.color.vifrification); //设置对话框背景为透明
        WindowManager.LayoutParams wl = window.getAttributes();
        //根据x，y坐标设置窗口需要显示的位置
        wl.x = x; //x小于0左移，大于0右移
        wl.y = y; //y小于0上移，大于0下移
        window.setAttributes(wl);
    }

    //设置商品数量（是否减数）
    private void setGoodsNumbers(Boolean isMinus) {
        //加减操作
        if (isMinus) {
            mNumber--;
        } else {
            mNumber++;
        }
        dialog_goods_numbers.setText(String.valueOf(mNumber));
        BigDecimal pSum = new BigDecimal(mGoods.FGP);
        dialog_goods_total_price.setText("￥" + pSum.multiply(new BigDecimal(mNumber.floatValue())).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());

        if (mNumber == 1) {
            dialog_goods_minus.setEnabled(false);
        } else if (mNumber.intValue() == mGoods.ST.intValue()) {
            dialog_goods_plus.setEnabled(false);
        } else {
            dialog_goods_minus.setEnabled(true);
            dialog_goods_plus.setEnabled(true);
        }
    }

    //按钮事件监听
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            goodsDialogListener.Confirm(mGoods.RID, mGoods.GID, mSPECId, mNumber);
            DialogGoods.this.dismiss();
        }
    };

    //定义回调事件
    public interface OnGoodsDialogListener {
        void Confirm(String businessId, String goodsId, String stockId, Integer number);
    }

    //取消Dialog
    private void closeDialog() {
        this.cancel();
    }


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    mStocks = (List<Stock>)msg.obj;
                    RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(4, 4);
                    params.setMargins(0, 0, Utility.dip2px(mContext, 4.0f), Utility.dip2px(mContext, 4.0f));
                    for (Stock s1 : mStocks) {
                        final RadioButton radio = (RadioButton) getLayoutInflater().inflate(R.layout.item_goods_stock, null);//new RadioButton(mContext);
                        radio.setButtonDrawable(android.R.color.transparent);
                        radio.setTag(mStocks.indexOf(s1));
                        radio.setText(s1.N);
                        dialog_goods_spec.addView(radio, params);
                    }

                    dialog_goods_spec.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int i) {

                                RadioButton button = (RadioButton) findViewById(i);
                                Stock stock = mStocks.get((Integer) button.getTag());
                                mSPECId = stock.ID;
                                mGoods.FRP = stock.RP;
                                mGoods.FGP = stock.GP;
                                mGoods.ST = stock.S;
                                dialog_goods_price.setText("￥" + mGoods.FGP);
                                dialog_goods_stock.setText(String.valueOf(mGoods.ST));
                                BigDecimal pSum = new BigDecimal(mGoods.FGP);
                                dialog_goods_total_price.setText("￥" + pSum.multiply(new BigDecimal(mNumber.floatValue())).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());

                                if (mGoods.ST <= 1) {
                                    dialog_goods_plus.setEnabled(false);
                                } else {
                                    dialog_goods_plus.setEnabled(true);
                                }
                            }
                    });
                    //选择第一个规格
                    dialog_goods_spec.getChildAt(0).performClick();
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    Toast.makeText(mContext,msg.obj.toString(),Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
