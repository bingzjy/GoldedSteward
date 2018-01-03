package com.ldnet.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

import com.autonavi.rtbt.IFrameForRTBT;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.*;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.HomeService;
import com.ldnet.service.MessageService;
import com.ldnet.utility.*;
import com.ldnet.view.HeaderLayout;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.type;
import static com.ldnet.goldensteward.R.id.init;
import static com.unionpay.mobile.android.global.a.I;

public class Message extends BaseActionBarActivity {

    private TextView tv_main_title;
    private ImageButton btn_back;
    private Services services;
    private List<MessageType> messageType=new ArrayList<>();
    private MyListView lv_message;
    private ListViewAdapter<MessageType> mAdapter;
    private PullToRefreshScrollView mPullToRefreshScrollView;
    private MessageService messageService;
    private HomeService homeService;
    private Handler handlerDeleteRed=new Handler(){};
    //初始化视图
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_message);
        //初始化操作
        initService();
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgressDialog();
        messageType.clear();
        //获取消息数据
        messageService.getMsgTypes(handler);
    }

    //初始化服务
    private void initService() {
        services = new Services();
        messageService = new MessageService(this);
        homeService = new HomeService(this);
    }

    //初始化View
    private void initView(){
        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.fragment_me_message);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.main_act_scrollview);
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mPullToRefreshScrollView.setHeaderLayout(new HeaderLayout(this));
        lv_message = (MyListView) findViewById(R.id.lv_message);
        lv_message.setFocusable(false);

        //列表item点击事件
        lv_message.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (messageType.get(position).getPushType()==0&&messageType.get(position).Pushing){
                    Msg msg = PushMessage.getPushInfo();
                    msg.PROPERTY_MSG = false;
                    PushMessage.setPushInformation(msg);
                    homeService.deleteRedPoint(9, handlerDeleteRed);
                }
                if (messageType.get(position).getPushType()==1&&messageType.get(position).Pushing){
                    Msg msg = PushMessage.getPushInfo();
                    msg.FEEDBACK = false;
                    PushMessage.setPushInformation(msg);
                    homeService.deleteRedPoint(6, handlerDeleteRed);
                }
                messageType.get(position).Pushing=false;

                Intent intent = new Intent(Message.this, MessageList.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("message", messageType.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });

        Services.setListViewHeightBasedOnChildren(lv_message);
    }

    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);

        //刷新
        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                messageType.clear();
                messageService.getMsgTypes(handler);
            }
        });
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }

    //初始化适配器
    private void initAdapter() {
        mAdapter = new ListViewAdapter<MessageType>(Message.this, R.layout.item_message, messageType) {
            @Override
            public void convert(ViewHolder holder, MessageType messageType) {
                //设置图片
                if (!TextUtils.isEmpty(messageType.getImage())) {
                    ImageLoader.getInstance().displayImage(Services.mHost + messageType.getImage(), (ImageView) holder.getView(R.id.iv_message_image), imageOptions);
                }
                //标题、价格、时间、地址
                holder.setText(R.id.tv_message_title, messageType.getPushTypeName())
                        .setText(R.id.tv_message_content, messageType.getContent())
                        .setText(R.id.tv_message_time, messageType.getCreated());
                ImageView iv_message = holder.getView(R.id.iv_message);
                if (messageType.Pushing) {
                    iv_message.setVisibility(View.VISIBLE);
                } else {
                    iv_message.setVisibility(View.GONE);
                }
            }
        };
        lv_message.setAdapter(mAdapter);
    }

    //设置数据属性-Pushing-状态
    private void setData(){
        //pushType 0:物业消息  1：意见反馈
        for (MessageType type : messageType) {
            if (type.getPushType() == 0 && PushMessage.getPushInfo().PROPERTY_MSG) {
                type.Pushing = true;
            }
            if (type.getPushType() == 1 && PushMessage.getPushInfo().FEEDBACK) {
                type.Pushing = true;
            }
        }
    }

    //推送返回设置红点显示
    Handler handlerGetRedPointPush=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    List<Type> data=(List<Type>)msg.obj;
                    Msg push=PushMessage.setMsg(data);
                    PushMessage.setPushInformation(push);
                    setData();
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    List<Type> data2=new ArrayList<Type>();
                    Msg push2=PushMessage.setMsg(data2);
                    PushMessage.setPushInformation(push2);
                    break;
            }
            //初始化适配器
            initAdapter();
        }
    };


    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            mPullToRefreshScrollView.onRefreshComplete();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    messageType = (List<MessageType>) msg.obj;
                    homeService.getAppRedPoint(handlerGetRedPointPush);
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    showToast("暂时没有数据");
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
