package com.ldnet.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.adapter.ListViewAdapter;
import com.ldnet.activity.base.BaseFragment;
import com.ldnet.activity.commen.Services;
import com.ldnet.entities.Repair;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.*;
import com.ldnet.utility.http.DataCallBack;
import com.ldnet.utility.sharepreferencedata.CookieInformation;
import com.ldnet.utility.sharepreferencedata.UserInformation;
import com.ldnet.view.FooterLayout;
import com.ldnet.view.HeaderLayout;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;
import com.tendcloud.tenddata.TCAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import okhttp3.Call;
import okhttp3.Request;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/17 0017.
 */
public class RepairIngFragment extends BaseFragment implements View.OnClickListener {

    private ListView listView;
    private ListViewAdapter<Repair> adapter;
    private List<Repair> mDatas = new ArrayList<Repair>();
    private List<Repair> datas;
    private PullToRefreshScrollView mPullToRefreshScrollView;
    private TextView ing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ly_ing_fragment, null);
        findView(view);
        initEvents();
        return view;
    }

    public void findView(View view) {
        ing = (TextView)view.findViewById(R.id.ing);
        mPullToRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.main_act_scrollview);
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshScrollView.setHeaderLayout(new HeaderLayout(getActivity()));
        mPullToRefreshScrollView.setFooterLayout(new FooterLayout(getActivity()));
        listView = (ListView) view.findViewById(R.id.lv_home_repairs_ing);
        listView.setFocusable(false);
        mDatas.clear();
        Repairs("");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                try {
                if (i <= mDatas.size()) {
                    Integer index = i;
                    Intent intent = new Intent(getActivity(), Property_Repair_Details.class);
                    intent.putExtra("FLAG", "REPAIR");
                    intent.putExtra("REPAIR_ID", mDatas.get(index).ID);
                    intent.putExtra("REPAIR_STATUS", mDatas.get(index).getNodesName());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("REPAIR", mDatas.get(index));
                    intent.putExtras(bundle);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                }
            }
        });
    }


    @Override
    public void onClick(View view) {

    }

    private void initEvents() {
        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                mDatas.clear();
                Repairs("");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (mDatas != null && mDatas.size() > 0) {
                    Repairs(mDatas.get(mDatas.size() - 1).ID);
                } else {
                    mPullToRefreshScrollView.onRefreshComplete();
                }
            }
        });
    }

    //报修列表
    public void Repairs(String lastId) {
        // 请求的URL
        User user = UserInformation.getUserInfo();
//        String url = Services.mHost + "API/Property/GetRepairByResidentId/%s/%s?lastId=%s";
        String url = Services.mHost + "WFRepairs/APP_YZ_GetInfo_ByRoomID?Types=%s&RoomID=%s&LastID=%s&PageCnt=%s";
        url = String.format(url, 1, user.getHouseId(), lastId, Services.PAGE_SIZE);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get().url(url)
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo()).build()
                .execute(new DataCallBack(getActivity()) {

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                    }

                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                        mPullToRefreshScrollView.onRefreshComplete();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        mPullToRefreshScrollView.onRefreshComplete();
                        Log.d("asdsdasd", "111111111" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<Repair>>() {
                                    }.getType();
                                    datas = gson.fromJson(jsonObject.getString("Obj"), listType);
                                    if (datas != null && datas.size() > 0) {
                                        mDatas.addAll(datas);
                                        adapter = new ListViewAdapter<Repair>(getActivity(), R.layout.item_home_repair, mDatas) {
                                            @Override
                                            public void convert(ViewHolder holder, Repair repair) {
                                                holder.setText(R.id.tv_repair_content, repair.Content)
                                                        .setText(R.id.tv_repair_status, repair.getNodesName())
                                                        .setText(R.id.tv_repair_date, Services.subStr(repair.getCreateDay()))
                                                        .setText(R.id.tv_repair_type, "[" + repair.getRtypeName() + "]");
                                            }
                                        };
                                        listView.setAdapter(adapter);
                                        Services.setListViewHeightBasedOnChildren(listView);
                                    } else {
                                        if (mDatas != null && mDatas.size() > 0) {
                                            showToast("沒有更多数据");
                                        } else {
                                            ing.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(getActivity(), "物业服务-报修进行状态内容" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(getActivity(), "物业服务-报修进行状态内容" + this.getClass().getSimpleName());
    }
}
