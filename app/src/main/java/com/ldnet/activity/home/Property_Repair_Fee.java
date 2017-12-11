package com.ldnet.activity.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.ChargingItem;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.PropertyServeService;
import com.ldnet.utility.*;
import com.zhy.http.okhttp.OkHttpUtils;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lee on 2016/12/14.
 */
public class Property_Repair_Fee extends BaseActionBarActivity {

    private TextView tv_main_title,tv_text;
    private ImageButton btn_back;
    private ListViewAdapter adapter;
    private ListView listView;
    private List<ChargingItem> chargingItems=new ArrayList<>();
    private PropertyServeService service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_repair_fee);
        service=new PropertyServeService(this);
        findView();
        service.getSFOptionList(UserInformation.getUserInfo().CommunityId,handlerGet);
    }

    public void findView() {
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText("收费标准");
        listView = (ListView) findViewById(R.id.lv_charge);
        tv_text = (TextView) findViewById(R.id.tv_text);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);

        adapter = new ListViewAdapter<ChargingItem>(Property_Repair_Fee.this, R.layout.ly_pop_win_item, chargingItems) {
            @Override
            public void convert(ViewHolder holder, ChargingItem chargingItem) {
                holder.setText(R.id.tv_charge_name, chargingItem.getTITLE());
                holder.setText(R.id.tv_charge_money, chargingItem.getSFMONEY() + "元");
            }
        };
        listView.setAdapter(adapter);
        Services.setListViewHeightBasedOnChildren(listView);
    }


    Handler handlerGet=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    chargingItems.clear();
                    chargingItems.addAll((List<ChargingItem>) msg.obj);
                    tv_text.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    tv_text.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    tv_text.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_back) {
            finish();
        }
    }
}
