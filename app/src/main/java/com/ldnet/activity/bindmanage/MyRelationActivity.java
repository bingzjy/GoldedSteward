package com.ldnet.activity.bindmanage;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.OwnerRoomRelation;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.BindingService;
import com.ldnet.service.HouseRelationService;
import com.ldnet.utility.CustomListView;
import com.ldnet.utility.CustomListView2;
import com.ldnet.utility.ListViewAdapter;
import com.ldnet.utility.Services;
import com.ldnet.utility.UserInformation;
import com.ldnet.utility.Utility;
import com.ldnet.utility.ViewHolder;
import com.library.PullToRefreshBase;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tendcloud.tenddata.TCAgent;
import com.third.SwipeListView.SwipeListView;
import com.third.SwipeListView2.SwipeListViewWrap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.ldnet.goldensteward.R.id.lv_relation_detail;
import static com.ldnet.goldensteward.R.id.slv_realtion_content;

/*
* 我的家属
* */
public class MyRelationActivity extends BaseActionBarActivity {
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.tv_page_title)
    TextView tvPageTitle;
    @BindView(R.id.iv_share)
    ImageView ivAdd;
    @BindView(R.id.lv_relation_detail)
    ListView lvRealtion;
    @BindView(R.id.tv_null_data_title)
    TextView tvNullDataTitle;


    private HouseRelationService relationService;
    private BindingService bindingService;
    private ArrayList<OwnerRoomRelation> relationsList = new ArrayList<>();
    private ArrayList<OwnerRoomRelation.ResidentBean> itemList = new ArrayList<>();
    private ListViewAdapter<OwnerRoomRelation> mAdapter;
    private ListViewAdapter<OwnerRoomRelation.ResidentBean> itemAdapter;
    private Unbinder unbinder;
    private static final String TAG = "MyRelationActivity";
    private String aa = Services.timeFormat();
    private String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");


    public DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.me_thumbnail_n)     //url爲空會显示该图片，自己放在drawable里面的
            .showImageOnFail(R.drawable.me_thumbnail_n)                //加载图片出现问题，会显示该图片
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .resetViewBeforeLoading(true)
            .extraForDownloader(UserInformation.getUserInfo().UserPhone + "," + aa + "," + aa1)
            .build();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_relation);
        unbinder = ButterKnife.bind(this);
        initView();
        relationService = new HouseRelationService(this);
        bindingService = new BindingService(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        getData();
        TCAgent.onPageStart(this, "门禁管理-我的门禁关系表：" + this.getClass().getSimpleName());
    }

    private void initView() {
        tvPageTitle.setText(getString(R.string.bind_relation_main_title));
        ivAdd.setImageResource(R.drawable.green_add_icon);
        ivAdd.setVisibility(View.VISIBLE);

        initAdapter();
    }


    private void initAdapter() {
        mAdapter = new ListViewAdapter<OwnerRoomRelation>(MyRelationActivity.this, R.layout.item_my_relation_content, relationsList) {
            @Override
            public void convert(ViewHolder holder, OwnerRoomRelation ownerRoomRelation) {

                holder.setText(R.id.tv_realtion_community_name, ownerRoomRelation.Abbreviation);

                if (ownerRoomRelation.Resident != null && ownerRoomRelation.Resident.size() > 0) {
                    final SwipeListViewWrap slistView = holder.getView(slv_realtion_content);
                    int deviceWidth = Utility.getScreenWidthforPX(MyRelationActivity.this);
                    slistView.setOffsetLeft(deviceWidth - Utility.dip2px(MyRelationActivity.this, 55));
                    slistView.setAdapter(getItemAdapter(ownerRoomRelation.Resident, ownerRoomRelation, slistView));

                    slistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            slistView.closeOpenedItems();
                        }
                    });

                }
            }
        };
        lvRealtion.setAdapter(mAdapter);
    }

    private ListViewAdapter<OwnerRoomRelation.ResidentBean> getItemAdapter(List<OwnerRoomRelation.ResidentBean> list, final OwnerRoomRelation ownerRoomRelation, final SwipeListViewWrap slistView) {
        itemAdapter = new ListViewAdapter<OwnerRoomRelation.ResidentBean>(this, R.layout.item_my_realtion_detail_content, list) {
            @Override
            public void convert(ViewHolder holder, final OwnerRoomRelation.ResidentBean residentBean) {
                CircleImageView ivHead = holder.getView(R.id.iv_item_relation_head_icon);
                ImageLoader.getInstance().displayImage(Services.getImageUrl(residentBean.Image), ivHead, imageOptions);
                holder.setText(R.id.tv_item_relation_name, residentBean.Name);

                holder.getView(R.id.tv_item_relation_date_title).setVisibility(View.VISIBLE);
                TextView tvDate = holder.getView(R.id.tv_item_relation_date);

                if (residentBean.ResidentType == 1) {
                    holder.setText(R.id.tv_item_relation_date_title, "绑定日期：");   //有效期内
                    holder.getView(R.id.tv_item_relation_date_title).setVisibility(View.VISIBLE);

                    holder.setImage(R.id.iv_item_relation_type, R.drawable.relation_family);

                    tvDate.setTextColor(Color.parseColor("#9B9B9B"));
                    tvDate.setVisibility(View.VISIBLE);
                    tvDate.setText(residentBean.bindTime);
                    tvDate.setCompoundDrawables(null, null, null, null);
                } else {
                    holder.setImage(R.id.iv_item_relation_type, R.drawable.relation_resident);
                    if (!TextUtils.isEmpty(residentBean.Leasedatee)) {

                        if (residentBean.residentState() == 1) {   //当天显示预警
                            holder.getView(R.id.tv_item_relation_date_title).setVisibility(View.GONE);
                            tvDate.setVisibility(View.VISIBLE);
                            tvDate.setText(residentBean.Leasedatee + " 失效");
                            tvDate.setTextColor(Color.parseColor("#FF0C2A"));
                            Drawable drawable = getResources().getDrawable(R.drawable.ic_error);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            tvDate.setCompoundDrawables(drawable, null, null, null);
                        } else if (residentBean.residentState() == 2) {    //已失效
                            holder.getView(R.id.tv_item_relation_date_title).setVisibility(View.VISIBLE);
                            holder.setText(R.id.tv_item_relation_date_title, "已失效");

                            tvDate.setCompoundDrawables(null, null, null, null);
                            tvDate.setVisibility(View.GONE);
                        } else {
                            holder.setText(R.id.tv_item_relation_date_title, "绑定日期：");   //有效期内
                            holder.getView(R.id.tv_item_relation_date_title).setVisibility(View.VISIBLE);

                            tvDate.setText(residentBean.bindTime);
                            tvDate.setCompoundDrawables(null, null, null, null);
                            tvDate.setVisibility(View.VISIBLE);
                            tvDate.setTextColor(Color.parseColor("#9B9B9B"));
                        }
                    }
                }

                //更多点击
                ImageView ivMore = holder.getView(R.id.iv_item_relation_more);
                ivMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        showMorePop(residentBean, ownerRoomRelation);

                    }
                });
                //删除
                Button btnDel = holder.getView(R.id.slv_btn_delete);
                btnDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {    //解除绑定
                        bindingService.RemoveHouse(ownerRoomRelation.CommunityID, ownerRoomRelation.RoomID, residentBean.Id, handlerDel);
                        slistView.closeOpenedItems();
                    }
                });
            }
        };
        return itemAdapter;
    }


    private void getData() {
        showProgressDialog();
        relationService.getMyRoomBindRelation(handlerGetData);
    }


    @OnClick({R.id.btn_back, R.id.iv_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.iv_share:
                Intent intent = new Intent(MyRelationActivity.this, AddRelationActivity.class);
                startActivity(intent);
                break;
        }
    }

    Handler handlerGetData = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    tvNullDataTitle.setVisibility(View.GONE);
                    lvRealtion.setVisibility(View.VISIBLE);
                    relationsList.clear();
                    relationsList.addAll((ArrayList<OwnerRoomRelation>) msg.obj);
                    mAdapter.notifyDataSetChanged();
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    tvNullDataTitle.setVisibility(View.VISIBLE);
                    lvRealtion.setVisibility(View.GONE);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    Handler handlerDel = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    getData();
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    //弹出框
    private void showMorePop(final OwnerRoomRelation.ResidentBean resident, final OwnerRoomRelation ownerRoomRelation) {
        final LayoutInflater inflater = LayoutInflater.from(MyRelationActivity.this);
        View popView = inflater.inflate(R.layout.pop_bind_realtion, null);
        final PopupWindow popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        View root = inflater.inflate(R.layout.main, null);
        popupWindow.showAtLocation(root, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        LinearLayout llRelationContract = (LinearLayout) popView.findViewById(R.id.ll_relation_contract);
        TextView tvRelationCall = (TextView) popView.findViewById(R.id.tv_relation_call);
        TextView tvRelationDelete = (TextView) popView.findViewById(R.id.tv_relation_delete);
        LinearLayout llRelationCancel = (LinearLayout) popView.findViewById(R.id.ll_relation_cancel);

        if (resident.ResidentType == 2) {
            llRelationContract.setVisibility(View.VISIBLE);
        } else {
            llRelationContract.setVisibility(View.GONE);
        }

        tvRelationCall.setText("呼叫 " + resident.Tel);

        tvRelationDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //解除绑定
                bindingService.RemoveHouse(ownerRoomRelation.CommunityID, ownerRoomRelation.RoomID,
                        resident.Id, handlerDel);
                popupWindow.dismiss();
            }
        });

        tvRelationCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拨打电话
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + resident.Tel));
                startActivity(intent);
                popupWindow.dismiss();
            }
        });

        llRelationContract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //续约
                Intent intent = new Intent(MyRelationActivity.this, ContractExtensionActivity.class);
                intent.putExtra("ROOM_ID", ownerRoomRelation.RoomID);
                intent.putExtra("RESIDENT_ID", resident.Id);
                intent.putExtra("SDATE", resident.Leasedates);
                intent.putExtra("EDATE", resident.Leasedatee);
                intent.putExtra("STATE",resident.residentState());
                startActivity(intent);
                popupWindow.dismiss();
            }
        });

        llRelationCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭
                popupWindow.dismiss();
            }
        });
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }


    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "门禁管理-我的门禁关系表：" + this.getClass().getSimpleName());
    }
}
