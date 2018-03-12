package com.ldnet.activity.qindian;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ldnet.entities.AccountRecord;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.QinDianService;
import com.ldnet.utility.ListViewAdapter;
import com.ldnet.utility.ViewHolder;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;
import com.tendcloud.tenddata.TCAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

import static android.R.attr.format;
import static com.iflytek.cloud.Setting.LOG_LEVEL.all;
import static com.ldnet.goldensteward.R.id.init;

/**
 * Created by lee on 2017/9/5.
 */
public class ConsumeDetailFragment extends Fragment {

    private ListView listView;
    private QinDianService service;
    private TextView tvNullResult;
    private List<AccountRecord> accountRecordList = new ArrayList<>();
    private ListViewAdapter<AccountRecord> mAdapter;
    private SwipeRefreshLayout refreshLayout;
    private ImageView imageViewDate;
    private TextView tvDate;
    private String currentDate;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");

    public ConsumeDetailFragment() {
    }

    static ConsumeDetailFragment newInstance() {
        return new ConsumeDetailFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qindian_account_detail, container, false);
        service = new QinDianService(getActivity());
        initView(view);
        initData();
        initEvent();
        return view;
    }


    @Override
    public void onResume() {
        initData();
        super.onResume();
        TCAgent.onPageStart(getActivity(), "充电服务-消费详情页：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(getActivity(), "充电服务-消费详情页：" + this.getClass().getSimpleName());
    }

    private void initView(View view) {
        imageViewDate = (ImageView) view.findViewById(R.id.iv_date_select);
        tvDate = (TextView) view.findViewById(R.id.tv_account_detail_date);
        tvDate.setText("本月");
        currentDate = format.format(new Date());
        listView = (ListView) view.findViewById(R.id.lv_account_detail);
        tvNullResult = (TextView) view.findViewById(R.id.null_data);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.account_detail_scrollview);

        mAdapter = new ListViewAdapter<AccountRecord>(getActivity(), R.layout.item_qindain_account_detail, accountRecordList) {
            @Override
            public void convert(ViewHolder holder, AccountRecord accountRecord) {
                holder.setText(R.id.tv_account_detail_title, "充电消费  " + accountRecord.getMoney()+"元");
                holder.getView(R.id.tv_account_detail_content).setVisibility(View.GONE);
                holder.setText(R.id.tv_account_detail_time, currentDate);
            }
        };

        listView.setAdapter(mAdapter);

        imageViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(getActivity(), listener, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }


    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int ar, int month, int dayOfMonth) {
            String date;
            month += 1;
            if (month < 10) {
                date = ar + "-0" + month;
            } else {
                date = ar + "-" + month;
            }
            tvDate.setText(date);
            currentDate = date;
            accountRecordList.clear();
            service.getConsumeData(currentDate, "1", handler);
        }
    };


    private void initEvent() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (accountRecordList != null && accountRecordList.size() > 0) {
                    service.getConsumeData(currentDate, accountRecordList.size()+ 1+"", handler);
                } else {
                    refreshLayout.setRefreshing(false);
                }
            }
        });
    }


    private void initData() {
        if (!TextUtils.isEmpty(currentDate)) {
            accountRecordList.clear();
            service.getConsumeData(currentDate, "1", handler);
        }
    }




    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshLayout.setRefreshing(false);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    tvNullResult.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    getConsuemList(msg);
                    mAdapter.notifyDataSetChanged();
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    if (accountRecordList != null && accountRecordList.size() > 0) {
                        Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_SHORT).show();
                    } else {
                        tvNullResult.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    private void getConsuemList(Message msg) {
        accountRecordList.clear();
        List<AccountRecord> allList = (List<AccountRecord>) msg.obj;
        for (AccountRecord accountRecord : allList) {
            if (accountRecord.getType() == 0) {         //type==0消费  type==1充值
                accountRecordList.add(accountRecord);
            }
        }
    }


}