package com.ldnet.activity.access;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ldnet.activity.base.BaseFragment;
//import com.ldnet.activity.base.LoadingDialog;
//import com.ldnet.entities.AccessGoodsRecord;
import com.ldnet.entities.AccessGoodsRecord;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AccessControlService;
import com.ldnet.service.BaseService;
import com.ldnet.utility.ListViewAdapter;
import com.ldnet.utility.ViewHolder;
import com.ldnet.view.FooterLayout;
import com.ldnet.view.HeaderLayout;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ldnet.utility.Services.dialog;

/**
 * Created by zjy on 2017/9/29
 */

public class GoodsRecordFragment extends BaseFragment implements View.OnClickListener {

    private ListView listView;
    private Button addInviteCard;
    private TextView tvNullData;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private AccessControlService service;
    private boolean refresh;
    private ListViewAdapter<AccessGoodsRecord> adapter;
    private PullToRefreshScrollView pullToRefreshScrollView;
    private List<AccessGoodsRecord> accessGoodsRecordList = new ArrayList<>();
    public GoodsRecordFragment() {
    }

   public static GoodsRecordFragment newInstance() {
        return new GoodsRecordFragment();
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
        accessGoodsRecordList.clear();
        service.getGoodsAccessRecord("2016-10-10 00:00:00", format.format(new Date()), "", getDataHandler);
    }

    private void initView(View view) {
        pullToRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.access_visitor_pull_refresh);
        pullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshScrollView.setHeaderLayout(new HeaderLayout(getActivity()));
        pullToRefreshScrollView.setFooterLayout(new FooterLayout(getActivity()));
        addInviteCard = (Button) view.findViewById(R.id.btn_add_invite_visitor);
        listView = (ListView) view.findViewById(R.id.lv_account_detail);
        tvNullData = (TextView) view.findViewById(R.id.null_data);

        pullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                refresh = false;
                accessGoodsRecordList.clear();
                service.getGoodsAccessRecord("2016-10-10 00:00:00", format.format(new Date()), "", getDataHandler);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                refresh = true;
                if (accessGoodsRecordList != null && accessGoodsRecordList.size() > 0) {
                    int size = accessGoodsRecordList.size();
                    service.getGoodsAccessRecord("2016-10-10 00:00:00", format.format(new Date()), accessGoodsRecordList.get(size - 1).Id, getDataHandler);
                } else {
                    pullToRefreshScrollView.onRefreshComplete();
                }
            }
        });


        adapter = new ListViewAdapter<AccessGoodsRecord>(getActivity(), R.layout.item_access_goods_record, accessGoodsRecordList) {
            @Override
            public void convert(ViewHolder holder, AccessGoodsRecord accessGoodsRecord) {

                holder.setText(R.id.tv_item_access_goods_reason, accessGoodsRecord.Reasons);
                holder.setText(R.id.tv_item_access_goods_date, accessGoodsRecord.DateStr);
                holder.setText(R.id.tv_item_access_goods_room, accessGoodsRecord.RoomNo);
                if (accessGoodsRecord.Status == 0) {
                    holder.setText(R.id.tv_item_access_goods_status, "审核中");
                } else if (accessGoodsRecord.Status == 1) {
                    holder.setText(R.id.tv_item_access_goods_status, "已审核");
                } else if (accessGoodsRecord.Status == 2) {
                    holder.setText(R.id.tv_item_access_goods_status, "未通过");
                } else if (accessGoodsRecord.Status == 3) {
                    holder.setText(R.id.tv_item_access_goods_status, "已出门");
                } else if (accessGoodsRecord.Status == 4) {
                    holder.setText(R.id.tv_item_access_goods_status, "已过期");
                }
            }
        };


        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AccessGoodsRecord record = accessGoodsRecordList.get(position);

                if (record.Status == 1 || record.Status == 3 || record.Status == 4) {  //已审核、已出门、已过期
                    Intent intent = new Intent(getActivity(), GoodsCardActivity.class);
                    intent.putExtra("IMAGE_ID", record.Id);
                    intent.putExtra("DATE", record.DateStr);
                    intent.putExtra("FROM_CLASS", getActivity().getClass().getName());
                    intent.putExtra("STATUS", record.Status + "");
                    intent.putExtra("APPROVE_DATE", record.ApproveTimeStr.toString());
                    intent.putExtra("RESIDENT_TEL",record.ResidentTel);
                    intent.putExtra("RESIDENT_NAME",record.ResidentName);
                    startActivity(intent);
                } else {

                    Intent intent = new Intent(getActivity(), GoodsRecordDetailActivity.class);
                    intent.putExtra("FROM_CLASS", getActivity().getClass().getName());
                    intent.putExtra("REASON", record.Reasons);
                    intent.putExtra("DATE", record.DateStr);
                    intent.putExtra("GOODS", record.Goods);
                    intent.putExtra("C_NAME", record.RoomNo);
                    if (record.Status == 0) {
                        intent.putExtra("STATUS", "审核中");
                    } else if (record.Status == 2) {
                        intent.putExtra("STATUS", "审核未通过");
                    }
                    startActivity(intent);
                }
            }
        });

        addInviteCard.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_invite_visitor:
                Intent intent=new Intent(getActivity(),AddGoodsApplyActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                break;
        }
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

                    accessGoodsRecordList.addAll((List<AccessGoodsRecord>) msg.obj);
                    adapter.notifyDataSetChanged();

                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    if (!refresh) {
                        listView.setVisibility(View.GONE);
                        tvNullData.setVisibility(View.VISIBLE);
                    } else {
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


//    public void showProgressDialog(String str) {
//        if (dialog == null) {
//            dialog = new LoadingDialog(getActivity());
//            dialog.setCanceledOnTouchOutside(false);
//        }
//        dialog.setText(str);
//        if (!dialog.isShowing()) {
//            dialog.show();
//        }
//    }
//
//    public void closeProgressDialog() {
//        if (dialog != null && dialog.isShowing()) {
//            dialog.dismiss();
//        }
//    }
//



}
