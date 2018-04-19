package com.ldnet.activity.informationpublish;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ldnet.activity.base.BaseFragment;
import com.ldnet.entities.InfoBarData;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.InfoBarService;
import com.ldnet.activity.adapter.ListViewAdapter;
import com.ldnet.activity.commen.Services;
import com.ldnet.utility.Utility;
import com.ldnet.utility.ViewHolder;
import com.ldnet.view.FooterLayout;
import com.ldnet.view.HeaderLayout;
import com.ldnet.view.listview.MyListView;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InfoShowContentFragment extends BaseFragment {

    private String littleType;
    private MyListView listView;
    private PullToRefreshScrollView refreshScrollView;
    private List<InfoBarData> dataList=new ArrayList<>();
    private InfoBarService service;
    private boolean loadMore;
    private ListViewAdapter<InfoBarData> adapter;
    private String tag=InfoShowContentFragment.class.getSimpleName();
    private TextView tvNull;
    private MyCustomBroadCaseReceiver mReceiver;

    public static Fragment getInstance(Bundle bundle){
        InfoShowContentFragment fragment=new InfoShowContentFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service=new InfoBarService(getActivity());

        //注册广播
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction("com.ldnet.my.test.broadcast");
            mReceiver=new MyCustomBroadCaseReceiver();
            getActivity().registerReceiver(mReceiver,filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(getActivity(), "邻里通：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(getActivity(), "邻里通：" + this.getClass().getSimpleName());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_infor, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        loading(false);
    }


    public void initView(View view){
        tvNull=(TextView)view.findViewById(R.id.tv_find_informations);
        littleType =String.valueOf(getArguments().getInt("value"));
        listView=(MyListView)view.findViewById(R.id.lv_find_informations);
        refreshScrollView=(PullToRefreshScrollView)view.findViewById(R.id.main_act_scrollview);
        refreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        refreshScrollView.setHeaderLayout(new HeaderLayout(getActivity()));
        refreshScrollView.setFooterLayout(new FooterLayout(getActivity()));
        listView.setFocusable(false);

        adapter=new ListViewAdapter<InfoBarData>(getActivity(),R.layout.item_info_bar,dataList) {
            @Override
            public void convert(ViewHolder holder, InfoBarData infoBarData) {
                holder.setText(R.id.tv_item_info_bar_title,infoBarData.Title);
                holder.setText(R.id.tv_item_info_bar_date,infoBarData.Created);
                ImageView imageViewCover=holder.getView(R.id.iv_item_info_bar_cover);
                if (!TextUtils.isEmpty(infoBarData.Cover)){
                    imageViewCover.setImageResource(R.drawable.default_info);
                    ImageLoader.getInstance().displayImage(Services.getImageUrl(infoBarData.Cover), imageViewCover, Utility.imageOptions);
                }else{
                    imageViewCover.setImageResource(R.drawable.default_info);
                }
            }
        };

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                InfoBarData data=dataList.get(position);
                HashMap<String, String> extra = new HashMap<String, String>();
                extra.put("INFO_ID", data.Id);
                extra.put("SHARE_URL", data.url);
                extra.put("ITEM",String.valueOf(CommunityInfoBarMainActivity.viewPager.getCurrentItem()));
                extra.put("NEED",CommunityInfoBarMainActivity.currentBigType);
                extra.put("FROM_CLASS", getActivity().getClass().getName());
                try {
                    gotoActivity(InfoPublishDetailActivity.class.getName(), extra);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                getActivity().overridePendingTransition(R.anim.slide_in_from_left,R.anim.slide_out_to_right);
            }
        });
        initEvent();
    }


    public void initEvent(){
        refreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                dataList.clear();
                service.getInfoList(CommunityInfoBarMainActivity.currentBigType,littleType, "","",handler);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (dataList!=null&&dataList.size()>0){
                    service.getInfoList(CommunityInfoBarMainActivity.currentBigType,littleType,"",dataList.get(dataList.size()-1).Id,handler);
                }else{
                    refreshScrollView.onRefreshComplete();
                }
            }
        });
    }


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshScrollView.onRefreshComplete();
            closeProgressDialog();
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    tvNull.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    dataList.addAll((List<InfoBarData>)msg.obj);
                    adapter.notifyDataSetChanged();
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    if (dataList!=null&&dataList.size()>0){
                        showToast("没有更多数据");
                        tvNull.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                    }else{
                        tvNull.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    listView.setVisibility(View.GONE);
                    tvNull.setVisibility(View.GONE);
                        showToast(msg.obj.toString());
                        break;
            }
        }
    };

    Handler deleteHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    showToast("删除成功");
                    service.getInfoList(CommunityInfoBarMainActivity.currentBigType,littleType,"","",handler);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    public class MyCustomBroadCaseReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.ldnet.my.test.broadcast".equals(intent.getAction())) {
                //重新获取数据
               loading(true);
            }
        }
    }


    private void loading(boolean show){
        dataList.clear();
        if (show){
            showProgressDialog();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    service.getInfoList(CommunityInfoBarMainActivity.currentBigType, littleType, "", "", handler);
                }
            },300);
        }else{
            service.getInfoList(CommunityInfoBarMainActivity.currentBigType, littleType, "", "", handler);
        }
    }

}
