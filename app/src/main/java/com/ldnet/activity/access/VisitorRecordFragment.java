package com.ldnet.activity.access;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ldnet.activity.base.BaseActionBarFragmentActivity;
import com.ldnet.activity.base.BaseFragment;
import com.ldnet.activity.base.LoadingDialog;
import com.ldnet.entities.AccessVisitorRecord;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AccessControlService;
import com.ldnet.service.BaseService;
import com.ldnet.utility.ListViewAdapter;
import com.ldnet.utility.Utility;
import com.ldnet.utility.ViewHolder;
import com.ldnet.view.FooterLayout;
import com.ldnet.view.HeaderLayout;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;
import com.tendcloud.tenddata.TCAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;




/**
 * Created by lee on 2017/9/29
 */

public class VisitorRecordFragment extends BaseFragment implements View.OnClickListener {

    private ListView listView;
    private Button addInviteCard;
    private PullToRefreshScrollView pullToRefreshScrollView;
    private AccessControlService service;
    private SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private ListViewAdapter<AccessVisitorRecord> adapter;
    private TextView tvNullData;
    private boolean refresh = false;
    private List<AccessVisitorRecord> accessVisitorRecordList = new ArrayList<>();
    private String tag = VisitorRecordFragment.class.getSimpleName();
    public VisitorRecordFragment() {
    }

    public static VisitorRecordFragment newInstance() {
        return new VisitorRecordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_accesss_control_record, container, false);
        service = new AccessControlService(getActivity());
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showProgressDialog("");
        accessVisitorRecordList.clear();
        service.getVisitorAccessRecord("2016-10-09 00:00:00", mformat.format(new Date()), "", getDataHandler);
        TCAgent.onPageStart(getActivity(), "访客记录:" + this.getClass().getSimpleName());
    }

    private void initView(View view) {
        addInviteCard = (Button) view.findViewById(R.id.btn_add_invite_visitor);
        listView = (ListView) view.findViewById(R.id.lv_account_detail);
        tvNullData = (TextView) view.findViewById(R.id.null_data);

        pullToRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.access_visitor_pull_refresh);
        pullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshScrollView.setHeaderLayout(new HeaderLayout(getActivity()));
        pullToRefreshScrollView.setFooterLayout(new FooterLayout(getActivity()));

        pullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                refresh = false;
                accessVisitorRecordList.clear();
                service.getVisitorAccessRecord("2016-10-09 00:00:00", mformat.format(new Date()), "", getDataHandler);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                refresh = true;
                if (accessVisitorRecordList != null && accessVisitorRecordList.size() > 0) {
                    int size = accessVisitorRecordList.size();
                    service.getVisitorAccessRecord("2016-10-09 00:00:00", mformat.format(new Date()), accessVisitorRecordList.get(size - 1).getId(), getDataHandler);
                } else {
                    pullToRefreshScrollView.onRefreshComplete();
                }
            }
        });


        adapter = new ListViewAdapter<AccessVisitorRecord>(getActivity(), R.layout.item_access_visitor_record, accessVisitorRecordList) {
            @Override
            public void convert(ViewHolder holder, AccessVisitorRecord accessVisitorRecord) {


                holder.setText(R.id.tv_item_access_visitor_name, accessVisitorRecord.getInviterName());
                holder.setText(R.id.tv_item_access_visitor_date, Utility.getDate(accessVisitorRecord.getDate()));
                holder.setText(R.id.tv_item_access_visitor_reason, accessVisitorRecord.getReasons());
                holder.setText(R.id.tv_item_access_visitor_room, accessVisitorRecord.getRoomNo());

                //0待使用  1已进门  2已出门  3已过期
                if (accessVisitorRecord.getStatus() == 0) {
                    holder.setText(R.id.tv_item_access_visitor_status, "未到访");
                } else if (accessVisitorRecord.getStatus() == 1||accessVisitorRecord.getStatus()==2) { //1已进门  2已出门
                    holder.setText(R.id.tv_item_access_visitor_status, "已到访");
                } else if (accessVisitorRecord.getStatus() == 3) {
                    holder.setText(R.id.tv_item_access_visitor_status, "已过期");
                }
            }
        };
        listView.setAdapter(adapter);
        //数据适配
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AccessVisitorRecord record = accessVisitorRecordList.get(position);
                Intent intent = new Intent(getActivity(), VisitorCardActivity.class);
                intent.putExtra("IMAGE_ID", record.getId());
                intent.putExtra("DATE", Utility.getDate(record.getDate()));
                intent.putExtra("TEL", record.getInviterTel());
                intent.putExtra("NAME", record.getInviterName());
                intent.putExtra("STATUS", record.getStatus() + "");
                intent.putExtra("ROOM",record.getRoomNo());
                intent.putExtra("FROM_CLASS", getActivity().getClass().getName());
                startActivity(intent);
            }
        });


        addInviteCard.setOnClickListener(this);
    }


    Handler getDataHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            pullToRefreshScrollView.onRefreshComplete();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    listView.setVisibility(View.VISIBLE);
                    tvNullData.setVisibility(View.GONE);

                    accessVisitorRecordList.addAll((List<AccessVisitorRecord>) msg.obj);
                    adapter.notifyDataSetChanged();
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    if (!refresh) {   //刷新
                        listView.setVisibility(View.GONE);
                        tvNullData.setVisibility(View.VISIBLE);
                    } else {         //加载
                        listView.setVisibility(View.VISIBLE);
                        tvNullData.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_invite_visitor:
                Intent intent = new Intent(getActivity(), AddVisitorInviteActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(getActivity(), "访客记录:" + this.getClass().getSimpleName());
    }
}
