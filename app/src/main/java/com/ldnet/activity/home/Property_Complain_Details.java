package com.ldnet.activity.home;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.*;
import android.view.animation.*;
import android.widget.*;

import com.ldnet.activity.adapter.GridViewAdapter;
import com.ldnet.activity.adapter.LoopViewPager1;
import com.ldnet.activity.commen.Services;
import com.ldnet.activity.adapter.TimeAdapter;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.Property;
import com.ldnet.entities.Repair;
import com.ldnet.entities.Score;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AcountService;
import com.ldnet.service.BaseService;
import com.ldnet.service.PropertyServeService;
import com.ldnet.view.listview.MyListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tendcloud.tenddata.TCAgent;

import java.util.*;

/**
 * Created by lee on 2016/7/29.
 */
public class Property_Complain_Details extends BaseActionBarActivity {

    private ImageButton btn_back;
    private TextView tv_main_title, tv_property_details_no, tv_share;
    private TextView tv_property_details_title;
    private TextView tv_property_details_status;
    private TextView tv_property_details_type;
    private TextView tv_property_details_house;
    private TextView tv_property_details_time;
    private TextView tv_property_details_appraisal_content;
    private MyListView lv_list;
    private GridView gv_list;
    private GridViewAdapter gridViewAdapter;
    // 列表适配器
    private TimeAdapter adapter;
    private List<Property> mTemp=new ArrayList<>();
    private List<Property> mDatas;
    private List<String> mDatas1;
    private String mRepairId = "";
    private String status = "";
    private Repair repair_complain;
    private String flag = "";

    private PopupWindows popupWindows;
    private int num;
    private LinearLayout ll_popup;

    private String[] pics;
    private String SCORE = "";

    private AlertDialog alertDialog;
    private TextView tv_socre;
    private EditText et_say;
    private RatingBar room_ratingbar, rb_score;
    private Button btn_cancel, btn_confirm;
    private Score score;
    private float s;
    private boolean aaa = false;
    private AcountService acountService;
    private float currentRate;
    private String appraiseContent;
    private PropertyServeService propertyService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_details);

        acountService=new AcountService(this);
        propertyService=new PropertyServeService(this);

        SCORE = getIntent().getStringExtra("SCORE");
        flag = getIntent().getStringExtra("FLAG");
        mRepairId = getIntent().getStringExtra("REPAIR_ID");
        status = getIntent().getStringExtra("REPAIR_STATUS");
        repair_complain = (Repair) getIntent().getSerializableExtra("REPAIR");
        findView();
    }

    public void findView() {
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_share = (TextView) findViewById(R.id.tv_share);
        tv_share.setVisibility(View.GONE);
        if (flag.equals("COMPLAIN")) {
            tv_main_title.setText("投诉详情");
        } else if (flag.equals("REPAIR")) {
            tv_main_title.setText("报修详情");
        }
        if (!TextUtils.isEmpty(SCORE) && SCORE.equals("TRUE")) {
            tv_share.setVisibility(View.VISIBLE);
            tv_share.setText("评价");
        }
        lv_list = (MyListView) findViewById(R.id.lv_list);
        gv_list = (GridView) findViewById(R.id.gv_list);
        lv_list.setFocusable(false);
        tv_property_details_title = (TextView) findViewById(R.id.tv_property_details_title);
        tv_property_details_no = (TextView) findViewById(R.id.tv_property_details_no);
        tv_property_details_status = (TextView) findViewById(R.id.tv_property_details_status);
        tv_property_details_type = (TextView) findViewById(R.id.tv_property_details_type);
        tv_property_details_time = (TextView) findViewById(R.id.tv_property_details_time);
        tv_property_details_house = (TextView) findViewById(R.id.tv_property_details_house);
        tv_property_details_appraisal_content = (TextView) findViewById(R.id.tv_appraisal_content);
        rb_score = (RatingBar) findViewById(R.id.rb_score);
        tv_property_details_type.setVisibility(View.GONE);
        tv_property_details_title.setText(repair_complain.getContent());
        tv_property_details_status.setText(repair_complain.getNodesName());
        tv_property_details_no.setText("订单编号:" + repair_complain.getOrderNumber());
        tv_property_details_house.setText("房号:" + repair_complain.getRoomName());
        tv_property_details_type.setText("报修类型:" + repair_complain.getRtypeName());
        tv_property_details_time.setText(Services.subStr(repair_complain.getCreateDay()));
        btn_back.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        mDatas = new ArrayList<Property>();
        mDatas1 = new ArrayList<String>();

        propertyService.getComplainCommunicate(mRepairId,handlerGetCommunicate);
        propertyService.getComplainScoreInfo(mRepairId,handlerGetScore);

        AnimationSet set = new AnimationSet(false);
        Animation animation = new AlphaAnimation(0, 1);   //AlphaAnimation 控制渐变透明的动画效果
        animation.setDuration(500);     //动画时间毫秒数
        set.addAnimation(animation);    //加入动画集合
        LayoutAnimationController controller = new LayoutAnimationController(set, 1);
        lv_list.setLayoutAnimation(controller);   //ListView 设置动画效果
        if (!TextUtils.isEmpty(repair_complain.getContentImg())) {
            if (repair_complain.getContentImg().contains(",")) {
                pics = repair_complain.getContentImg().split(",");
                for (String pic : pics) {
                    if (!TextUtils.isEmpty(pic)) {
                        mDatas1.add(pic);
                    }
                }
            } else {
                mDatas1.add(repair_complain.getContentImg());
            }
            gridViewAdapter = new GridViewAdapter(this, mDatas1);
            gv_list.setAdapter(gridViewAdapter);
        }

        gv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                num = position;
                popupWindows = new PopupWindows(Property_Complain_Details.this, view, mDatas1);
            }
        });
        //沟通适配
        adapter = new TimeAdapter(Property_Complain_Details.this, mTemp);
        lv_list.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_back) {
            finish();
            if(aaa) {
                Services.comment = mRepairId;
            }
        } else if (view.getId() == R.id.tv_share) {
            ScoreDialog();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            if(aaa) {
                Services.comment = mRepairId;
            }
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void ScoreDialog() {
        alertDialog= new AlertDialog.Builder(this).create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.score_dialog);
        tv_socre = (TextView) alertDialog.findViewById(R.id.tv_socre);
        et_say = (EditText) alertDialog.findViewById(R.id.et_say);
        room_ratingbar = (RatingBar) alertDialog.findViewById(R.id.room_ratingbar);
        btn_cancel = (Button) alertDialog.findViewById(R.id.btn_cancel);
        btn_confirm = (Button) alertDialog.findViewById(R.id.btn_confirm);
        WindowManager.LayoutParams lp = window.getAttributes();
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        lp.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.65
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setAttributes(lp);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        room_ratingbar.setOnRatingBarChangeListener(new RatingBarChangeListenerImpl());
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((int)s != 0) {
                    currentRate=Math.abs(6 - s);
                    appraiseContent = et_say.getText().toString().trim();
                    propertyService.createComplainScore(mRepairId, currentRate, appraiseContent, handlerCreateScore);
                } else {
                    showToast("请先评分");
                    return;
                }
                alertDialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                hintKbTwo(et_say);
            }
        });
    }

    private class RatingBarChangeListenerImpl implements RatingBar.OnRatingBarChangeListener {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            s = rating;
            if (rating == 0) {
                tv_socre.setText("评分");
            } else if (rating == 1) {
                tv_socre.setText("非常不满意");
            } else if (rating == 2) {
                tv_socre.setText("不满意");
            } else if (rating == 3) {
                tv_socre.setText("一般");
            } else if (rating == 4) {
                tv_socre.setText("满意");
            } else if (rating == 5) {
                tv_socre.setText("非常满意");
            }
        }

    }

    public class PopupWindows extends PopupWindow {

        public PopupWindows(Context mContext, View parent, List<String> TimeBean) {

            View view = View
                    .inflate(mContext, R.layout.pop_viewpager, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.fade_in));
            ll_popup = (LinearLayout) view
                    .findViewById(R.id.ll_popup);
            setAnimationStyle(R.style.AnimationPreview);
            setWidth(getWindowManager().getDefaultDisplay().getWidth());
            setHeight(getWindowManager().getDefaultDisplay().getHeight());
            setBackgroundDrawable(new BitmapDrawable());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM | Gravity.TOP | Gravity.LEFT | Gravity.RIGHT, -10, -10);
            update();
            ll_popup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
            LoopViewPager1 iv_show = (LoopViewPager1) view
                    .findViewById(R.id.pager);
            iv_show.setAdapter(new MyAdapter(Property_Complain_Details.this,
                    TimeBean));
            iv_show.setCurrentItem(num);
        }
    }

    private class MyAdapter extends PagerAdapter {

        /**
         * 图片资源列表
         */
        private List<String> mAdList = new ArrayList<String>();
        private Context mContext;

        public MyAdapter(Context context, List<String> adList) {
            this.mContext = context;
            this.mAdList = adList;
        }

        @Override
        public int getCount() {
            return mAdList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            String imageUrl = mAdList.get(position);
            final View view = LayoutInflater.from(mContext).inflate(
                    R.layout.layout_popwindow, null);
            ImageView imageView = (ImageView) view
                    .findViewById(R.id.iv_show);
            TextView textView = (TextView) view.findViewById(R.id.tv_count1);
            // 设置图片点击监听
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindows.dismiss();
                }
            });
            textView.setText((position + 1) + "/" + mAdList.size());
            ImageLoader.getInstance().displayImage(Services.getImageUrl(imageUrl), imageView,
                    imageOptions);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // 这里不需要做任何事情
        }

    }

    //提交评分
    Handler handlerCreateScore=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    showToast("评价成功");
                    hintKbTwo(et_say);
                    tv_share.setVisibility(View.GONE);
                    rb_score.setVisibility(View.VISIBLE);
                    rb_score.setRating(Math.abs(6 -currentRate));
                    tv_property_details_appraisal_content.setText(appraiseContent);
                    aaa = true;
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    //获取评分
    Handler handlerGetScore=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    score=(Score)msg.obj;
                    rb_score.setVisibility(View.VISIBLE);
                    tv_share.setVisibility(View.GONE);
                    rb_score.setRating(Math.abs(6 - Float.parseFloat(score.getSocreCnt())));
                    tv_property_details_appraisal_content.setText(score.getOrtherContent());
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    rb_score.setVisibility(View.GONE);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    rb_score.setVisibility(View.GONE);
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    //报修&投诉沟通信息
    Handler handlerGetCommunicate=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    mTemp.clear();
                    mTemp.addAll((List<Property>)msg.obj);
                    adapter.notifyDataSetChanged();
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "物业服务-投诉详情" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "物业服务-投诉详情" + this.getClass().getSimpleName());
    }
}
