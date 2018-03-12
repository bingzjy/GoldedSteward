package com.ldnet.activity.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.chinaums.pppay.unify.UnifyPayListener;
import com.chinaums.pppay.unify.UnifyPayPlugin;
import com.chinaums.pppay.unify.UnifyPayRequest;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.ldnet.activity.adapter.PayTypeAdapter;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.goldensteward.R;
import com.ldnet.service.PropertyFeeService;
import com.ldnet.utility.UserInformation;
import com.tendcloud.tenddata.TCAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ldnet.utility.Utility.backgroundAlpaha;

/**
 * Created by lee on 2017/7/26.
 */


public class PropertyFeeCreateCode extends BaseActionBarActivity implements UnifyPayListener{

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.tv_page_title)
    TextView tvPageTitle;
    @BindView(R.id.tv_fee_title)
    TextView tvFeeValue;
    @BindView(R.id.tv_fee_house)
    TextView tvHouseName;
    @BindView(R.id.tv_fee_property_name)
    TextView tvFeePropertyName;
    @BindView(R.id.img_code)
    ImageView ivCode;
    @BindView(R.id.btn_pay_complete)
    Button btnNext;

    private Bitmap bitmapCode;
    private PropertyFeeService service;
    private String orderId;
    private final String IMAGE_PATH = "/DCIM/" + "金牌管家/";
    private UnifyPayPlugin payPlugin;
    private UnifyPayRequest payRequest;
    private static final String TAG = "PropertyFeeCreateCode";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weixin_pay);
        ButterKnife.bind(this);

        service = new PropertyFeeService(PropertyFeeCreateCode.this);
        initView();
        initData();


    }




    void initView() {


//        tvHouseInfo = (TextView) findViewById(R.id.tv_fee_house);
//        tvFee = (TextView) findViewById(R.id.tv_fee_title);
//        tvPayDate = (TextView) findViewById(R.id.tv_pay_date);
//        payConfirm = (Button) findViewById(R.id.btn_pay_complete);
//        imgCode = (ImageView) findViewById(R.id.img_code);
//        headerBack = (ImageView) findViewById(R.id.btn_back);
//        payConfirm.setOnClickListener(this);
//        headerBack.setOnClickListener(this);
    }


    void initData() {
//        if (getIntent() != null) {
//            orderId=getIntent().getStringExtra("order");
//            String url = getIntent().getStringExtra("url");
//            tvHouseInfo.setText(getIntent().getStringExtra("house").toString());
//            tvFee.setText("物业费："+getIntent().getStringExtra("fee").toString());
//
//            if (!TextUtils.isEmpty(url)) {
//                try {
//                    bitmapCode = createQRCode(url);
//                    imgCode.setImageBitmap(bitmapCode);
//
//                } catch (WriterException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }

        tvFeePropertyName.setText(UserInformation.getUserInfo().PropertyName);
        try {
            bitmapCode = createQRCode("https://qr-test2.chinaums.com/bills/qrCode.do?id=31941801189001119111252841");
            showToast("二维码已保存在本地图库 (DCIM > 金牌管家)");
            ivCode.setImageBitmap(bitmapCode);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_pay_complete:
                showPayTypeSelect();
              //  testPay();
                break;
        }
    }


    public void testPay(){
        payRequest.payChannel = UnifyPayRequest.CHANNEL_ALIPAY;
        payRequest.payData = "{\"qrCode\": \"https://qr.alipay.com/bax05944j5tscxqcoeap20c2\"}";

        payPlugin.sendPayRequest(payRequest);
    }


    @Override
    public void onResult(String s, String s1) {
        if (s.equals("0000")) {
            Log.e(TAG,"支付成功："+s1);
        } else {
            Log.e(TAG,"支付失败："+s1);
        }
    }

    public Bitmap createQRCode(String url) throws WriterException {

        if (url == null || url.equals("")) {
            return null;
        }

        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(url,
                BarcodeFormat.QR_CODE, 150, 150);

        int width = matrix.getWidth();
        int height = matrix.getHeight();

        // 二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        canvas.drawBitmap(bitmap, 0, 0, paint); //将原图使用给定的画笔画到画布上

        saveImage(newBitmap); //保存图片至本地图库
        return newBitmap;
    }


    //保存图片在本地
    public void saveImage(Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory(), IMAGE_PATH);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //支付方式选择
    public void showPayTypeSelect() {
        LayoutInflater layoutInflater = LayoutInflater.from(PropertyFeeCreateCode.this);
        View popupView = layoutInflater.inflate(R.layout.pop_pay_type, null);
        final PopupWindow mPopWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(popupView);
        View rootview = layoutInflater.inflate(R.layout.main, null);
        mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
        mPopWindow.setAnimationStyle(R.anim.slide_in_from_bottom);

        TextView feeTag = (TextView) popupView.findViewById(R.id.pop_type_fee_tag2);
        ListView listView = (ListView) popupView.findViewById(R.id.pop_type_listView2);
        TextView cancel = (TextView) popupView.findViewById(R.id.cancel_call);
        PayTypeAdapter adapter = new PayTypeAdapter(PropertyFeeCreateCode.this);
        listView.setAdapter(adapter);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
                backgroundAlpaha(PropertyFeeCreateCode.this, 1f);
            }
        });


        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpaha(PropertyFeeCreateCode.this, 1f);
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPopWindow.dismiss();
                switch (position) {
                    case 0://支付宝
                        //打开支付宝扫一扫
                        try {
                            Uri uri = Uri.parse("alipayqr://platformapi/startapp?saId=10000007");
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        } catch (Exception e) {
                        }
                        break;
                    case 1: //打开微信扫一扫
                        Intent intent = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
                        intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
                        startActivity(intent);
                        break;
                }
            }
        });
        backgroundAlpaha(PropertyFeeCreateCode.this, 0.5f);
    }



    @OnClick({R.id.btn_back, R.id.btn_pay_complete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_pay_complete:
                try {
                    gotoActivity(PropertyFeePayTypeActivity.class.getName(),null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "物业交费-生成支付码" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "物业交费-生成支付码" + this.getClass().getSimpleName());
    }
}
