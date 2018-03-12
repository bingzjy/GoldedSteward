package com.ldnet.activity.me;

import android.content.Intent;
import android.os.*;
import android.os.Message;
import android.view.View;
import android.widget.*;

import com.ldnet.activity.adapter.MyDialog;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.home.HouseRent_List;
import com.ldnet.entities.MessageData;
import com.ldnet.entities.MessageType;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.MessageService;
import com.ldnet.utility.*;
import com.ldnet.view.FooterLayout;
import com.ldnet.view.HeaderLayout;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lee on 2016/7/27.
 */
public class MessageList extends BaseActionBarActivity {

    private TextView tv_main_title,tv_message,tv_message_detail_time,tv_message_detail_title,tv_message_detail_content;
    private ImageButton btn_back;
    private MessageType messageType;
    private List<MessageData> list;
    private CustomListView2 lv_message_detail;
    private ListViewAdapter<MessageData> mAdapter;
    private List<MessageData> mDatas= new ArrayList<MessageData>();
    private PullToRefreshScrollView mPullToRefreshScrollView;
    private MessageService messageService;
    private MessageData currentMessageData;
    private int currentIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_message_detail);
        //初始化服务
        messageService =new MessageService(this);
        //初始化View、事件
        initView();
        initEvents();
        //获取参数
        messageType = (MessageType) getIntent().getSerializableExtra("message");

        //请求数据
        messageService.getMsgListByType(messageType.getPushType(),"0",handler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "消息列表：" + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "消息列表：" + this.getClass().getSimpleName());
    }


    public void initView(){
        // 标题
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_message = (TextView) findViewById(R.id.tv_message);
        tv_main_title.setText(R.string.fragment_me_message_list);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.main_act_scrollview);
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshScrollView.setHeaderLayout(new HeaderLayout(this));
        mPullToRefreshScrollView.setFooterLayout(new FooterLayout(this));
        lv_message_detail = (CustomListView2)findViewById(R.id.lv_message_detail);
        lv_message_detail.setFocusable(false);

        lv_message_detail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(MessageList.this, MessageDetail.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("messageData", mDatas.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        lv_message_detail.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                currentMessageData=mDatas.get(position);
                currentIndex=position;
                crenteCencalDialog();
                return true;
            }
        });

        mAdapter = new ListViewAdapter<MessageData>(MessageList.this, R.layout.item_message_detail, mDatas) {
            @Override
            public void convert(ViewHolder holder, MessageData messageType) {
                //标题、价格、时间、地址
                holder.setText(R.id.tv_message_detail_title, messageType.Title+" [ "+messageType.CommunityName+" ]")
                        .setText(R.id.tv_message_detail_content, messageType.Content)
                        .setText(R.id.tv_message_detail_time,messageType.Created);
            }
        };
        lv_message_detail.setAdapter(mAdapter);
    }


    //删除对话框
    private void crenteCencalDialog() {
        MyDialog dialog = new MyDialog(this,"确定要删除吗");
        dialog.show();
        dialog.setDialogCallback(dialogcallback);
    }

    MyDialog.Dialogcallback dialogcallback = new MyDialog.Dialogcallback() {
        @Override
        public void dialogdo() {
          //删除消息
            if (currentMessageData!=null){
                messageService.deleteMessage(currentMessageData.Id,handlerDelete);
            }
        }

        @Override
        public void dialogDismiss() {
        }
    };

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_back){
            finish();
        }
    }

    private void initEvents() {
        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                mDatas.clear();
                messageService.getMsgListByType(messageType.getPushType(),"0",handler);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (mDatas != null && mDatas.size() > 0) {
                    messageService.getMsgListByType(messageType.getPushType(),mDatas.get(mDatas.size() - 1).Id,handler);
                }else{
                    mPullToRefreshScrollView.onRefreshComplete();
                }
            }
        });
    }


    //消息数据返回
   Handler handler=new Handler(){
       @Override
       public void handleMessage(Message msg) {
           super.handleMessage(msg);
           mPullToRefreshScrollView.onRefreshComplete();
           switch (msg.what){
               case BaseService.DATA_SUCCESS:
                   list = (List<MessageData>) msg.obj;
                   mDatas.addAll(list);
                   mAdapter.notifyDataSetChanged();
                   break;
               case BaseService.DATA_SUCCESS_OTHER:
                   if (mDatas != null && mDatas.size() > 0) {
                       showToast("没有更多数据");
                   } else {
                       tv_message.setVisibility(View.VISIBLE);
                   }
                   break;
               case BaseService.DATA_FAILURE:
               case BaseService.DATA_REQUEST_ERROR:
                   showToast(msg.obj.toString());
                   break;
           }
       }
   };


    //删除消息返回
    Handler handlerDelete = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mPullToRefreshScrollView.onRefreshComplete();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    showToast("删除成功");
                    mDatas.remove(currentIndex);
                    mAdapter.notifyDataSetChanged();
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };
}
