package com.ldnet.activity.me;

import android.os.*;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.AddressSimple;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AddressService;
import com.ldnet.service.BaseService;
import com.ldnet.utility.*;
import com.tendcloud.tenddata.TCAgent;
import com.third.SwipeListView.BaseSwipeListViewListener;
import com.third.SwipeListView.SwipeListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Address extends BaseActionBarActivity {

    private TextView tv_main_title;
    private ImageButton btn_back;
    private ImageButton btn_address_create;
    private Services services;

    private ListViewAdapter mAdapter;
    private List<AddressSimple> mDatas= new ArrayList<AddressSimple>();
    private SwipeListView slv_me_address;
    private TextView mAddressEmpty;

    private List<AddressSimple> address;
    private List<AddressSimple> ass;
    private AddressService addressService;
    private String currentType="";
    private AddressSimple currentAddress;
    //初始化视图
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_address);

        //初始化服务
        addressService=new AddressService(this);
        services = new Services();

        initView();
        initEvent();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //获取地址列表
        showProgressDialog();
        addressService.getAddressList(handlerGetAddress);
        TCAgent.onPageStart(this, "收货地址：" + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "收货地址：" + this.getClass().getSimpleName());
    }

    public void initView(){
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.fragment_me_address);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_address_create = (ImageButton) findViewById(R.id.btn_custom);
        btn_address_create.setImageResource(R.drawable.plus);
        btn_address_create.setVisibility(View.VISIBLE);
        mAddressEmpty = (TextView) findViewById(R.id.address_empty);

        slv_me_address = (SwipeListView) findViewById(R.id.slv_me_address);

        mAdapter = new ListViewAdapter<AddressSimple>(Address.this, R.layout.item_me_address, mDatas) {
            @Override
            public void convert(ViewHolder holder, final AddressSimple addressSimple) {
                holder.setText(R.id.tv_me_address_title, addressSimple.AD)
                        .setText(R.id.tv_me_address_zipcode, addressSimple.ZC)
                        .setText(R.id.tv_me_address_name, addressSimple.NP);

                TextView btn_address_default = holder.getView(R.id.btn_address_default);
                TextView btn_delete = holder.getView(R.id.btn_delete);
                TextView tv_me_address_title = holder.getView(R.id.tv_me_address_title);
                if (addressSimple.ISD) {
                    tv_me_address_title.setTextColor(getResources().getColor(R.color.green));
                    btn_address_default.setEnabled(false);
                } else {
                    tv_me_address_title.setTextColor(getResources().getColor(R.color.gray_deep));
                }
                //设置默认
                btn_address_default.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        currentAddress=addressSimple;
                        addressService.setDefaultAddress(addressSimple.ID,handlerSetAddress);
                    }
                });

                //删除地址
                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentAddress=addressSimple;
                        addressService.deleteAddress(addressSimple.ID,handlerDeleteAddress);
                    }
                });
            }
        };
        slv_me_address.setAdapter(mAdapter);

        slv_me_address.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onClickFrontView(int position) {
                super.onClickFrontView(position);
                AddressSimple address = mDatas.get(position);
                HashMap<String, String> extras = new HashMap<String, String>();
                extras.put("ADDRESS_ID", address.ID);
                try {
                    gotoActivityAndFinish(AddressEdit.class.getName(), extras);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        slv_me_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                slv_me_address.closeOpenedItems();
            }
        });
    }
    //初始化事件
    public void initEvent() {
        btn_back.setOnClickListener(this);
        btn_address_create.setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btn_back://判断是否返回主页
                    finish();
                    break;
                case R.id.btn_custom://编辑地址
                    gotoActivityAndFinish(AddressEdit.class.getName(), null);
                    break;
                default:
                    break;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

  Handler handlerGetAddress=new Handler(){
      @Override
      public void handleMessage(Message msg) {
          super.handleMessage(msg);
          closeProgressDialog();
          switch (msg.what){
              case BaseService.DATA_SUCCESS:
                  mDatas.clear();
                  mDatas.addAll((List<AddressSimple>) msg.obj);
                  mAdapter.notifyDataSetChanged();
                  break;
              case BaseService.DATA_SUCCESS_OTHER:
                  showToast("请先添加收货地址");
                  mAddressEmpty.setVisibility(View.VISIBLE);
                  break;
              case BaseService.DATA_FAILURE:
              case BaseService.DATA_REQUEST_ERROR:
                  showToast(msg.obj.toString());
                  break;
          }
      }
      };


    Handler handlerSetAddress=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    addressService.getAddressList(handlerGetAddress);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    Handler handlerDeleteAddress=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:
                    mDatas.remove(currentAddress);
                    mAdapter.notifyDataSetChanged();
                    slv_me_address.closeOpenedItems();
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

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
