package com.ldnet.activity.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.adapter.FeeListViewAdapter;
import com.ldnet.activity.adapter.PayTypeAdapter;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.*;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.PropertyFeeService;
import com.ldnet.utility.*;
import com.unionpay.UPPayAssistEx;
import net.tsz.afinal.FinalDb;
import java.util.*;

public class Property_Fee extends BaseActionBarActivity {
    private TextView tv_main_title;
    private ImageButton btn_back;
    private TextView tv_fee_houseinfo;
    private TextView tv_year, tv_fee, tv_fee_sum, tv_fee_pay;
    private LinearLayout ll_year, ll_year1, ll_fee, ll_fee1, ll_not_pay;
    private ImageView iv_year, iv_fee;
    private Services services;
    private ExpandableListView exlv_property_fees;
    private List<Fees> showFeeList = new ArrayList<Fees>();
    private FeeListViewAdapter mAdapter;
    private Button btn_go_paid;
    private List<Fees> totalFeeList=new ArrayList<>();

    private AddPopWindow addPopWindow;
    private List<String> yearList = new ArrayList<String>();
    private FinalDb fd;//定义finaldb
    private boolean flag;
    private String currentFeeType = "0";
    private String currentYearType="0";
    private PropertyFeeService service;
    // WXPAY微信  ALIPAY支付宝
    private final String alPay = "ALIPAY";
    private final String wxPay = "WXPAY";
    private float totalAmount;
    private static final String TAG = "Property_Fee";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_property_fee);
        fd = FinalDb.create(this);//创建数据库  后面参数为自定义库名 否则为FinalDb
        fd.dropDb();
        //初始化View、事件
        initView();
        initEvent();
        initAdapter();
        //初始化服务
        services = new Services();
        service = new PropertyFeeService(Property_Fee.this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //默认获取全部的未交费数据
        showProgressDialog1();
        service.getPropertyFee(null, null, "0", handlerGetFee);
    }

    //初始化控件
    private void initView() {
        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.property_services_fee);
        ll_year = (LinearLayout) findViewById(R.id.ll_year);
        ll_year1 = (LinearLayout) findViewById(R.id.ll_year1);
        ll_fee = (LinearLayout) findViewById(R.id.ll_fee);
        ll_fee1 = (LinearLayout) findViewById(R.id.ll_fee1);
        ll_not_pay = (LinearLayout) findViewById(R.id.ll_not_pay);
        ll_not_pay.setVisibility(View.VISIBLE);
        tv_year = (TextView) findViewById(R.id.tv_year);
        tv_fee_pay = (TextView) findViewById(R.id.tv_fee_pay);
        tv_fee_sum = (TextView) findViewById(R.id.tv_fee_sum);
        tv_fee = (TextView) findViewById(R.id.tv_fee);
        tv_fee.setText(getString(R.string.no_fee));
        iv_year = (ImageView) findViewById(R.id.iv_year);
        iv_fee = (ImageView) findViewById(R.id.iv_fee);
        //房屋信息
        User user = UserInformation.getUserInfo();
        tv_fee_houseinfo = (TextView) findViewById(R.id.tv_fee_houseinfo);
        tv_fee_houseinfo.setText(user.CommuntiyName + "(" + user.HouseName + ")");

        //返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);

        //列表
        exlv_property_fees = (ExpandableListView) findViewById(R.id.exlv_property_fees);
    }


    Handler handlerGetFee = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog1();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    //获取到物业费用
                    yearList.clear();

                    if (msg.obj == null) {
                        showToast("暂时没有数据");
                        yearList.add("全部");
                    } else {
                        totalFeeList.clear();
                        totalFeeList.addAll((List<Fees>)msg.obj);
                        showFee();
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    Handler handGetTnByFeesIds = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    if (!TextUtils.isEmpty((String) msg.obj)) {
                        doStartUnionPayPlugin(Property_Fee.this, (String) msg.obj, "00");
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast((String) msg.obj);
                    break;
            }
        }
    };

    Handler handlerAlPAY = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog1();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    if (!TextUtils.isEmpty(msg.obj.toString())) {

                        String urlAndorder[] = msg.obj.toString().split(",");
                        HashMap extra = new HashMap();
                        extra.put("url", urlAndorder[0]);
                        extra.put("order", urlAndorder[1]);
                        extra.put("from", Property_Fee.class.getName());
                        extra.put("house", tv_fee_houseinfo.getText().toString());
                        extra.put("fee", tv_fee_sum.getText().toString());

                        try {
                            gotoActivity(PayConfirm.class.getName(), extra);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj == null ? getString(R.string.network_error) : msg.obj.toString());
                    break;
            }
        }
    };


    //费用数据展示
    private void showFee() {
        totalAmount = 0;
        showFeeList.clear();
        yearList.add("全部");
        for (Fees f : totalFeeList) {
            String[] aa = f.getFeeDate().split("-");
            yearList.add(aa[0]);
        }
        //去重数据
        yearList=removeDuplicate(yearList);

        //根据年份类型过滤出对应的费用数据
        if (tv_year.getText().toString().equals("全部")) {   //取全部
            showFeeList.addAll(totalFeeList);
        } else {                                            //取特定年份的
            for (Fees fee : totalFeeList) {
                if (fee.getFeeDate().contains(tv_year.getText().toString())) {
                    showFeeList.add(fee);
                }
            }
        }
        //去重复数据
        showFeeList=removeDuplicate1(showFeeList);

        //设置未交费用默认选中状态
        if (currentFeeType.equals("0")) {    //未交费用
            tv_fee.setText(getString(R.string.no_fee));

            for (Fees itemFee : showFeeList) {
                itemFee.IsChecked = true;    //未交费用，默认选中
                if (!itemFee.isPaid()) {
                    totalAmount = Arith.add(totalAmount, itemFee.UnpaidSum().floatValue());
                    tv_fee_sum.setText("总计:" + totalAmount);
                }
            }
        }

        mAdapter.notifyDataSetChanged();
    }


    private void initAdapter(){
        //初始化适配器
        mAdapter = new FeeListViewAdapter(Property_Fee.this, showFeeList, tv_fee_sum, totalAmount);
        exlv_property_fees.setAdapter(mAdapter);
        exlv_property_fees.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.setClass(Property_Fee.this, PropertyFeeDetail.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("lstAPPFees", showFeeList.get(i));
                intent.putExtra("position", i);
                intent.putExtras(bundle);
                intent.putExtra("flag", flag);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });
    }

    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);
        ll_fee.setOnClickListener(this);
        ll_fee1.setOnClickListener(this);
        ll_year.setOnClickListener(this);
        ll_year1.setOnClickListener(this);
        tv_fee_pay.setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btn_back:
                    gotoActivityAndFinish(MainActivity.class.getName(), null);
                    break;
                case R.id.ll_fee:
                    ll_fee.setVisibility(View.GONE);
                    ll_year1.setVisibility(View.GONE);
                    ll_year.setVisibility(View.VISIBLE);
                    ll_fee1.setVisibility(View.VISIBLE);
                    if (addPopWindow != null) {
                        addPopWindow.dismiss();
                    }
                    List<String> str = new ArrayList<String>();

                    str.add(getString(R.string.have_fee));
                    str.add(getString(R.string.no_fee));
                    addPopWindow = new AddPopWindow(Property_Fee.this, str, ll_fee, "1");
                    break;
                case R.id.ll_fee1:
                    ll_fee1.setVisibility(View.GONE);
                    ll_fee.setVisibility(View.VISIBLE);
                    addPopWindow.dismiss();
                    break;
                case R.id.ll_year:
                    ll_year.setVisibility(View.GONE);
                    ll_fee1.setVisibility(View.GONE);
                    ll_fee.setVisibility(View.VISIBLE);
                    ll_year1.setVisibility(View.VISIBLE);
                    if (addPopWindow != null) {
                        addPopWindow.dismiss();
                    }
                    addPopWindow = new AddPopWindow(Property_Fee.this, yearList, ll_year, "0");
                    break;
                case R.id.ll_year1:
                    ll_year1.setVisibility(View.GONE);
                    ll_year.setVisibility(View.VISIBLE);
                    addPopWindow.dismiss();
                    break;
                case R.id.tv_fee_pay:
                    //检查用户选择
                    String ids = "";
                    for (Fees f : showFeeList) {
                        if (f.IsChecked != null && f.IsChecked) {
                            for (lstAPPFees laf : f.lstAPPFees) {
                                if (!laf.Status) {
                                    if (!TextUtils.isEmpty(ids)) {
                                        ids += "," + laf.ID;
                                    } else {
                                        ids = laf.ID;
                                    }
                                }
                            }
                        }
                    }
                    //判断用户选择，并提交到缴费的页面
                    if (!TextUtils.isEmpty(ids)) {
                        showPayTypeSelect(ids, UserInformation.getUserInfo().UserId);
                    } else {
                        showToast(getString(R.string.go_paid_none));
                    }
                    break;
                default:
                    break;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    class AddPopWindow extends PopupWindow {

        private View conentView;
        private LinearLayout ll_pop;

        public AddPopWindow(final Activity context, final List<String> strs, View view1, final String type) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            conentView = inflater.inflate(R.layout.add_popup_dialog, null);
            int h = context.getWindowManager().getDefaultDisplay().getHeight();
            int w = context.getWindowManager().getDefaultDisplay().getWidth() / 2;
            // 设置SelectPicPopupWindow的View
            this.setContentView(conentView);
            // 设置SelectPicPopupWindow弹出窗体的宽
            this.setWidth(w);
            // 设置SelectPicPopupWindow弹出窗体的高
            this.setHeight(h);
            // 设置SelectPicPopupWindow弹出窗体可点击
            this.setFocusable(false);
            //    this.setOutsideTouchable(false);
            this.setBackgroundDrawable(new BitmapDrawable());
            // 刷新状态
            this.update();

            if (Build.VERSION.SDK_INT < 24) {
                this.showAsDropDown(view1);
            } else {
                int[] location = new int[2];
                view1.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                this.showAtLocation(view1, Gravity.NO_GRAVITY, x, y + view1.getHeight());
            }

            // 实例化一个ColorDrawable颜色为半透明
            ColorDrawable dw = new ColorDrawable(0000000000);
            // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
            this.setBackgroundDrawable(dw);
            // 设置SelectPicPopupWindow弹出窗体动画效果
            this.setAnimationStyle(R.style.AnimationPreview);
            ll_pop = (LinearLayout) conentView.findViewById(R.id.ll_pop);
            final ListView listView = (ListView) conentView.findViewById(R.id.lv_list);

            ArrayAdapter adapter = new ArrayAdapter(context, R.layout.add_pop_item, R.id.tx, strs);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if ("0".equals(type)) {      //年份
                        tv_year.setText(strs.get(i));
                        ll_year1.setVisibility(View.GONE);
                        ll_year.setVisibility(View.VISIBLE);
                    } else {                //交费类型
                        tv_fee.setText(strs.get(i));
                        ll_fee1.setVisibility(View.GONE);
                        ll_fee.setVisibility(View.VISIBLE);
                    }

                    showFeeList.clear();
                    if (strs.get(i).equals(getString(R.string.have_fee))) {    //已交费
                        ll_not_pay.setVisibility(View.GONE);
                        flag = true;
                        currentFeeType = "1";

                    } else if (strs.get(i).equals(getString(R.string.no_fee))) {  //未交费
                        ll_not_pay.setVisibility(View.VISIBLE);
                        flag = false;
                        currentFeeType = "0";
                    } else {                                                         //年份类型
                        tv_year.setText(yearList.get(i));
                        if (tv_fee.getText().toString().contains(getString(R.string.have_fee))) {
                            ll_not_pay.setVisibility(View.GONE);
                            flag = true;
                            currentFeeType = "1";
                        } else if (tv_fee.getText().toString().contains(getString(R.string.no_fee))) {
                            ll_not_pay.setVisibility(View.VISIBLE);
                            flag = false;
                            currentFeeType = "0";
                        }
                    }
                    dismiss();
                    //请求数据，关闭选择弹出框
                    service.getPropertyFee(null, null, currentFeeType, handlerGetFee);
                    showProgressDialog1();
                }
            });
        }
    }


    // 调用银联
    public void doStartUnionPayPlugin(Activity activity, String tn, String serverMode) {
        UPPayAssistEx.startPay(activity, null, null, tn, serverMode);
    }

    //处理银联手机支付控件返回的支付结果
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        final String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            msg = "支付成功！";
        } else if (str.equalsIgnoreCase("fail")) {
            msg = "支付失败！";
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "用户取消了支付";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle("支付结果通知");
        builder.setMessage(msg);
        builder.setInverseBackgroundForced(true);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                currentFeeType = "0";
                service.getPropertyFee(null, null, "0", handlerGetFee);
            }
        });
        builder.create().show();
    }


    public static List<String> removeDuplicate(List<String> list) {
        Set set = new LinkedHashSet<String>();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }


    public static List<Fees> removeDuplicate1(List<Fees> list) {
        Set set = new LinkedHashSet<String>();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (addPopWindow != null) {
            addPopWindow.dismiss();
        }
        Services.integerList.clear();
    }


    //支付方式选择
    public void showPayTypeSelect( final String feeId,  final String payerId) {
        LayoutInflater layoutInflater = LayoutInflater.from(Property_Fee.this);
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
        PayTypeAdapter adapter = new PayTypeAdapter(Property_Fee.this);
        listView.setAdapter(adapter);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
                backgroundAlpaha(Property_Fee.this, 1f);
            }
        });


        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpaha(Property_Fee.this, 1f);
            }
        });

        if (tv_fee_sum.getText() != null) {
            feeTag.setText("物业费       " + tv_fee_sum.getText().toString());
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPopWindow.dismiss();
                switch (position) {
                    case 0://支付宝
                        service.ALWeiXinPay(feeId, payerId, alPay, handlerAlPAY);
                        break;
                    case 1:  //银联
                        service.GetTnByFeesIds(feeId, payerId, handGetTnByFeesIds);
                        break;
                }
            }
        });
        backgroundAlpaha(Property_Fee.this, 0.5f);
    }


    //设置遮罩层效果
    public void backgroundAlpaha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }
}
