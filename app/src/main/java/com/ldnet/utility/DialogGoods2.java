package com.ldnet.utility;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.entities.Goods;
import com.ldnet.entities.Stock;
import com.ldnet.goldensteward.R;
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

import static com.ldnet.map.ChString.To;

/**
 * Created by Alex on 2015/9/28.
 */
public class DialogGoods2 extends Dialog {

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


    public String strThumbnail;
    public String strT;
    public String strGP;
    public String strGID;
    public Integer strST;
    public Float strFGP;
    public Float strFRP;
    public String strRID;



    public DialogGoods2(Context context,OnGoodsDialogListener customDialogListener, String title,String thumbnail,
                       String T,String GP,String GID,Integer ST,Float FGP,Float FRP,String RID ){
        super(context, R.style.dialog_fullscreen);
        mContext=context;
        mTitle = title;

        strThumbnail=thumbnail;
        strT=T;
        strGP=GP;
        strGID=GID;
        strST=ST;
        strFGP=FGP;
        strFRP=FRP;
        strRID=RID;

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


    //构造函数
    public DialogGoods2(Context context, Goods goods, OnGoodsDialogListener customDialogListener, String title) {
        super(context, R.style.dialog_fullscreen);
        mContext = context;
        //商品信息
        mGoods = goods;

        strThumbnail=mGoods.getThumbnail();
        strT=mGoods.T;
        strGP=mGoods.GP;
        strGID=mGoods.GID;
        strST=mGoods.ST;
        strFRP=mGoods.FRP;
        strFGP=mGoods.FGP;
        strRID=mGoods.RID;

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
        windowDeploy(0,0);
        //商品图片
        dialog_goods_thumbnail = (ImageView) findViewById(R.id.dialog_goods_thumbnail);
        //    ImageLoader.getInstance().displayImage(services.getImageUrl(mGoods.getThumbnail()), dialog_goods_thumbnail, imageOptions);
        ImageLoader.getInstance().displayImage(services.getImageUrl(strThumbnail), dialog_goods_thumbnail, imageOptions);

        //商品名称
        dialog_goods_title = (TextView) findViewById(R.id.dialog_goods_title);
        //   dialog_goods_title.setText(mGoods.T);
        dialog_goods_title.setText(strT);

        //金牌价
        dialog_goods_price = (TextView) findViewById(R.id.dialog_goods_price);
        //     dialog_goods_price.setText("￥" + String.valueOf(mGoods.GP));
        dialog_goods_price.setText("￥" + String.valueOf(strGP));

        //规格
        dialog_goods_spec = (RadioGroup) findViewById(R.id.dialog_goods_spec);
        //  Stocks(mGoods.GID);
        Stocks(strGID);

        //库存
        dialog_goods_stock = (TextView) findViewById(R.id.dialog_goods_stock);
        //     dialog_goods_stock.setText(String.valueOf(mGoods.ST));
        dialog_goods_stock.setText(String.valueOf(strST));


        //商品数量
        dialog_goods_numbers = (EditText) findViewById(R.id.dialog_goods_numbers);
        //得到商品数量
        mNumber = Integer.valueOf(dialog_goods_numbers.getText().toString().trim());
        //减
        dialog_goods_minus = (Button) findViewById(R.id.dialog_goods_minus);
        if (strST<=1){
            dialog_goods_minus.setEnabled(false);
        }else {
            dialog_goods_minus.setEnabled(true);
        }
        dialog_goods_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGoodsNumbers(true);
            }
        });
        //加
        dialog_goods_plus = (Button) findViewById(R.id.dialog_goods_plus);
        //如果库存为1，则不能再添加商品数量
        if (strST <=1) {
            dialog_goods_plus.setEnabled(false);
        }else{
            dialog_goods_plus.setEnabled(true);
        }
        dialog_goods_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGoodsNumbers(false);
            }
        });

        //总价
        dialog_goods_total_price = (TextView) findViewById(R.id.dialog_goods_total_price);
        BigDecimal pSum = new BigDecimal(strFGP);
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
//            wl.alpha = 0.6f; //设置透明度
//            wl.gravity = Gravity.BOTTOM; //设置重力
        window.setAttributes(wl);
    }

    //设置商品数量
    private void setGoodsNumbers(Boolean isMinus) {
        //加减操作
        if (isMinus) {
            mNumber--;
        } else {
            mNumber++;
        }
        dialog_goods_numbers.setText(String.valueOf(mNumber));
        BigDecimal pSum = new BigDecimal(strFGP);
        dialog_goods_total_price.setText("￥" + pSum.multiply(new BigDecimal(mNumber.floatValue())).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());

        if (mNumber == 1) {
            dialog_goods_minus.setEnabled(false);
        } else if (mNumber.intValue() == strST.intValue()) {
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
            //    goodsDialogListener.Confirm(mGoods.RID, mGoods.GID, mSPECId, mNumber);
            DialogGoods2.this.dismiss();
            if (strST==0){
                Toast.makeText(mContext,mContext.getResources().getString(R.string.mall_goods_not),Toast.LENGTH_SHORT).show();
            }else{
                goodsDialogListener.Confirm(strRID, strGID, mSPECId, mNumber);
            }
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

    //获取购物车列表
    public void Stocks(String goodsId) {
        // 请求的URL
        String url = Services.mHost + "BGoodsStandard/APP_GetInfo_ByGoodsID?GID=%s";
        url = String.format(url, goodsId);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get().url(url)
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo()).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {

                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e("asdsdasd", "购物车Stocks:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<Stock>>(){}.getType();
                                    mStocks = gson.fromJson(jsonObject.getString("Obj"),type);
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
//                                            mGoods.FRP = stock.RP;
//                                            mGoods.FGP = stock.GP;
//                                            mGoods.ST = stock.S;

                                            strFRP = stock.RP;
                                            strFGP = stock.GP;
                                            strST = stock.S;
                                            dialog_goods_price.setText("￥" +  strFGP);
                                            dialog_goods_stock.setText(String.valueOf( strST));
                                            BigDecimal pSum = new BigDecimal( strFGP);
                                            dialog_goods_total_price.setText("￥" + pSum.multiply(new BigDecimal(mNumber.floatValue())).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
                                        }
                                    });
                                    //选择第一个规格
                                    dialog_goods_spec.getChildAt(0).performClick();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
