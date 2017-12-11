package com.ldnet.activity.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.Notifications;
import com.ldnet.entities.SurveyEntity;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.NotificationService;
import com.ldnet.utility.*;
import com.ldnet.view.FooterLayout;
import com.ldnet.view.HeaderLayout;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.http.okhttp.OkHttpUtils;

import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Notification extends BaseActionBarActivity {
    private TextView tv_main_title;
    private ImageButton btn_back;
    private Services services;
    private CustomListView2 lv_property_notification;
    private ListViewAdapter mAdapter;
    private ListViewAdapter mSurveyAdapter;
    private List<Notifications> mDatas;
    private List<String> isReadIds;
    private Handler mHandler;
    private RadioGroup mRdgTenementBottom;
    private RadioButton radioButtonFirst, radioButtonMiddle, radioButtonLast;
    private PullToRefreshScrollView main_act_scrollview;
    private TextView mNotificationEmpty;
    private ProgressBar mProgressBar;
    private List<Notifications> datas;
    private List<SurveyEntity> surveyEntityList = new ArrayList<>();
    private List<SurveyEntity> surveyTempList;
    private User user;
    private boolean showSurvey = false;
    private NotificationService notificationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_notification);
        initView();
        //初始化服务
        notificationService=new NotificationService(this);
        services = new Services();
        mHandler = new Handler();

        initEvent();
        initEvents();
    }

    private void initView(){
        // 标题
        radioButtonFirst = (RadioButton) findViewById(R.id.rdb_tenement_title1);
        radioButtonMiddle = (RadioButton) findViewById(R.id.rdb_tenement_title2);
        radioButtonLast = (RadioButton) findViewById(R.id.rdb_tenement_title3);

        radioButtonMiddle.setVisibility(View.VISIBLE);
        radioButtonFirst.setText("通知");
        radioButtonMiddle.setText("公告");
        radioButtonLast.setText("调研");

        mRdgTenementBottom = (RadioGroup) findViewById(R.id.rdg_tenement_bottom);
        mRdgTenementBottom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mDatas.clear();
                if (((RadioButton) mRdgTenementBottom.getChildAt(0)).isChecked()) {
                    notificationService.getNotificationList("", handlerNotification);
                } else if (((RadioButton) mRdgTenementBottom.getChildAt(1)).isChecked()) {
                    notificationService.getAnnouncementList("",handlerAnnouncement);
                } else if (((RadioButton) mRdgTenementBottom.getChildAt(2)).isChecked()) {
                    notificationService.getSurveyList("",handlerSurvey);
                }
            }
        });

        //返回按钮
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        //获取已读
        ReadInfoIDs read = ReadInfoIDs.getInstance();
        isReadIds = read.getRead(read.TYPE_NOTIFICATION);
        //无物业通知显示图
        mNotificationEmpty = (TextView) findViewById(R.id.notification_empty);
        main_act_scrollview = (PullToRefreshScrollView) findViewById(R.id.main_act_scrollview);
        main_act_scrollview.setMode(PullToRefreshBase.Mode.BOTH);
        main_act_scrollview.setHeaderLayout(new HeaderLayout(this));
        main_act_scrollview.setFooterLayout(new FooterLayout(this));

        //通知列表
        lv_property_notification = (CustomListView2) findViewById(R.id.lv_property_notification);
        lv_property_notification.setFocusable(false);
        mDatas = new ArrayList<Notifications>();
        lv_property_notification.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //通知公告
                if (!showSurvey) {
                    if (i <= mDatas.size()) {
                        user = UserInformation.getUserInfo();
                        HashMap<String, String> extras = new HashMap<String, String>();
                        extras.put("NOTIFICATION_ID", mDatas.get(i).Id);
                        extras.put("PAGE_TITLE", "通知详情");
                        extras.put("FROM_CLASS_NAME", Notification.class.getName());
                        extras.put("PAGE_URL", mDatas.get(i).Url + "&IsApp=1&UID=" + user.UserId + "&UName=" + user.UserName + "&UImgID=" + (!TextUtils.isEmpty(user.UserThumbnail) ? user.UserThumbnail : ""));

                        //分享 - 标题、描述、URL
                        extras.put("PAGE_TITLE_ORGIN", mDatas.get(i).Title);
                        extras.put("PAGE_DESCRIPTION_ORGIN", mDatas.get(i).getCover());
                        extras.put("PAGE_URL_ORGIN", mDatas.get(i).Url);
                        extras.put("NOTIFICATION_TYPE", "1");

                        try {
                            gotoActivity(Notification_Details.class.getName(), extras);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                } else {  //调研
                    if (surveyEntityList != null && surveyEntityList.size() > 0) {

                        HashMap<String, String> extra = new HashMap<String, String>();
                        extra.put("NOTIFICATION_ID", surveyEntityList.get(i).getID());
                        extra.put("PAGE_TITLE", "内部调研");
                        extra.put("PAGE_DISPLAY_URL", surveyEntityList.get(i).getURL());
                        extra.put("PAGE_SHARE_URL", surveyEntityList.get(i).getShareURL());
                        extra.put("NOTIFICATION_TYPE", "2");
                        extra.put("PAGE_TITLE_ORGIN", surveyEntityList.get(i).getTitle());
                        extra.put("FROM_CLASS_NAME", Notification.class.getName());
                        extra.put("PAGE_DATE",surveyEntityList.get(i).getReleaseDate());
                        try {
                            gotoActivity(Notification_Details.class.getName(), extra);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public void setAdapterClear() {
        mAdapter = new ListViewAdapter<Notifications>(this, R.layout.item_home_notification, mDatas) {
            @Override
            public void convert(ViewHolder holder, Notifications notifications) {
                //绑定图片
                ImageView icon = holder.getView(R.id.iv_notification_icon);
                if (!TextUtils.isEmpty(notifications.Cover)) {
                    ImageLoader.getInstance().displayImage(services.getImageUrl(notifications.Cover), icon, imageOptions);
                } else {
                    icon.setImageBitmap(Utility.getBitmapByText(notifications.Title.trim().substring(0, 1), 64, 64, "#ffffffff", "#ff25B59E"));
                }
                holder.setText(R.id.tv_notification_title, notifications.Title).setText(R.id.tv_notification_date, Services.subStr(notifications.getDateTime()));
                //获取标题,设置已读状态的标题颜色
                TextView tv_notification_title = holder.getView(R.id.tv_notification_title);
                if (isReadIds.contains(notifications.Id)) {
                    tv_notification_title.setTextColor(getResources().getColor(R.color.gray_light_1));
                } else {
                    tv_notification_title.setTextColor(getResources().getColor(R.color.gray_deep));
                }
            }
        };
        lv_property_notification.setAdapter(mAdapter);
        services.setListViewHeightBasedOnChildren(lv_property_notification);
        showSurvey = false;
    }

    private void setSurveyAdapter() {
        mSurveyAdapter = new ListViewAdapter<SurveyEntity>(this, R.layout.item_home_notification, surveyEntityList) {
            @Override
            public void convert(ViewHolder holder, SurveyEntity surveyEntity) {
                ImageView icon = holder.getView(R.id.iv_notification_icon);
                icon.setImageBitmap(Utility.getBitmapByText(surveyEntity.getTitle().trim().substring(0, 1), 64, 64, "#ffffffff", "#ff25B59E"));
                holder.setText(R.id.tv_notification_title, surveyEntity.getTitle()).setText(R.id.tv_notification_date, Services.subStr(surveyEntity.getReleaseDate()));
            }
        };

        lv_property_notification.setAdapter(mSurveyAdapter);
        services.setListViewHeightBasedOnChildren(lv_property_notification);
        showSurvey = true;
    }

    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        ReadInfoIDs read = ReadInfoIDs.getInstance();
        isReadIds = read.getRead(read.TYPE_NOTIFICATION);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDatas.clear();
                showProgressDialog();
                if (((RadioButton) mRdgTenementBottom.getChildAt(0)).isChecked()) {
                    notificationService.getNotificationList("", handlerNotification);
                } else if (((RadioButton) mRdgTenementBottom.getChildAt(1)).isChecked()) {
                    notificationService.getAnnouncementList("", handlerAnnouncement);
                } else if (((RadioButton) mRdgTenementBottom.getChildAt(2)).isChecked()) {
                    notificationService.getSurveyList("", handlerSurvey);
                }
            }
        }, 0);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                try {
                    gotoActivityAndFinish(MainActivity.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                try {
                    gotoActivityAndFinish(MainActivity.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void initEvents() {
        main_act_scrollview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                mDatas.clear();
                if (((RadioButton) mRdgTenementBottom.getChildAt(0)).isChecked()) {
                    notificationService.getNotificationList("", handlerNotification);
                } else if (((RadioButton) mRdgTenementBottom.getChildAt(1)).isChecked()) {
                    notificationService.getAnnouncementList("", handlerAnnouncement);
                } else if (((RadioButton) mRdgTenementBottom.getChildAt(2)).isChecked()) {
                    notificationService.getSurveyList("", handlerSurvey);
                }
            }


            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (mDatas != null && mDatas.size() > 0) {
                    if (((RadioButton) mRdgTenementBottom.getChildAt(0)).isChecked()) {
                        notificationService.getNotificationList(mDatas.get(mDatas.size() - 1).Id, handlerNotification);
                    } else if (((RadioButton) mRdgTenementBottom.getChildAt(1)).isChecked()) {
                        notificationService.getAnnouncementList(mDatas.get(mDatas.size() - 1).Id, handlerAnnouncement);
                    } else if (((RadioButton) mRdgTenementBottom.getChildAt(2)).isChecked()) {
                        notificationService.getSurveyList(mDatas.get(mDatas.size() - 1).Id, handlerSurvey);
                    }
                } else {
                    main_act_scrollview.onRefreshComplete();
                }
            }
        });
    }

    Handler handlerNotification =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            main_act_scrollview.onRefreshComplete();
            closeProgressDialog();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    datas=(List<Notifications>)msg.obj;
                    mNotificationEmpty.setVisibility(View.GONE);
                    lv_property_notification.setVisibility(View.VISIBLE);
                    mDatas.addAll(datas);
                    setAdapterClear();
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    if (mDatas == null || mDatas.size() == 0) {
                        mNotificationEmpty.setVisibility(View.VISIBLE);
                        lv_property_notification.setVisibility(View.GONE);
                    } else {
                        mNotificationEmpty.setVisibility(View.GONE);
                        lv_property_notification.setVisibility(View.VISIBLE);
                        showToast("沒有更多数据");
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    Handler handlerAnnouncement=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            main_act_scrollview.onRefreshComplete();
            closeProgressDialog();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    datas=(List<Notifications>)msg.obj;
                    mNotificationEmpty.setVisibility(View.GONE);
                    lv_property_notification.setVisibility(View.VISIBLE);
                    mDatas.addAll(datas);
                    setAdapterClear();
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    if (mDatas == null || mDatas.size() == 0) {
                        mNotificationEmpty.setVisibility(View.VISIBLE);
                        lv_property_notification.setVisibility(View.GONE);
                    } else {
                        mNotificationEmpty.setVisibility(View.GONE);
                        lv_property_notification.setVisibility(View.VISIBLE);
                        showToast("沒有更多数据");
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

    Handler handlerSurvey=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            main_act_scrollview.onRefreshComplete();
            closeProgressDialog();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                        surveyTempList = (List<SurveyEntity>) msg.obj;
                        mNotificationEmpty.setVisibility(View.GONE);
                        lv_property_notification.setVisibility(View.VISIBLE);
                        surveyEntityList = surveyTempList;
                        setSurveyAdapter();
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    if (surveyEntityList == null || surveyEntityList.size() == 0) {
                        mNotificationEmpty.setVisibility(View.VISIBLE);
                        lv_property_notification.setVisibility(View.GONE);
                    } else {
                        mNotificationEmpty.setVisibility(View.GONE);
                        lv_property_notification.setVisibility(View.VISIBLE);
                        showToast("沒有更多数据");
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

}
