package com.ldnet.activity.yellowpage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
//import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.adapter.ListViewAdapter;
import com.ldnet.activity.base.BaseFragment;
import com.ldnet.activity.commen.Services;
import com.ldnet.entities.PageSortDetail;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by zxs on 2016/3/30.
 * 黄页的列表
 */
public class YellowPageFragmentContent extends BaseFragment implements PoiSearch.OnPoiSearchListener {

    private List<PageSortDetail> mSortDetails;
    private Services service;
    private Handler mHandler;
    private ListView xlv_yellowpage;
    private ListViewAdapter mAdapter;

    private String mSortId;
    private String mSortKeywords;
    private String mSortTypes;
    private String flag;
    private String communityId;
    private List<PageSortDetail> datas;
    private String dataString;
    private PullToRefreshScrollView mPullToRefreshScrollView;
    private String mCityCode = "西安";
    private TextView mOrderEmpty;
    private boolean up = false;

    private boolean permissGranted;
    HashMap<String, String> currentExtras = new HashMap<String, String>();

    private String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};

    public static Fragment getInstance(Bundle bundle) {
        YellowPageFragmentContent fragment = new YellowPageFragmentContent();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(getActivity(), "生活黄页-内容：" + this.getClass().getSimpleName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_yellowpage, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

    }

    @Override
    public void closeProgressDialog1() {
        super.closeProgressDialog1();
    }

    //初始化视图

    public void initView(View view) {
        //初始化
        service = new Services();

        //获取分类的id ，key和类型
        mSortId = getArguments().getString("titleId");
        mSortKeywords = getArguments().getString("titleKeywords");
        mSortTypes = getArguments().getString("titleTypes");
        flag = getArguments().getString("flag");
        communityId = UserInformation.getUserInfo().CommunityId;
        mCityCode = UserInformation.getUserInfo().CommuntiyCityId;
        mOrderEmpty = (TextView) view.findViewById(R.id.order_empty);
        if (flag != null && !flag.equals("")) {
            if (flag.equals("edu")) {
                Drawable rightDrawable = getResources().getDrawable(R.drawable.edu);
                rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                mOrderEmpty.setCompoundDrawables(null, rightDrawable, null, null);
            } else if (flag.equals("yellow")) {
                Drawable rightDrawable = getResources().getDrawable(R.drawable.yellow);
                rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                mOrderEmpty.setCompoundDrawables(null, rightDrawable, null, null);
            }
        }

        mPullToRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.main_act_scrollview);
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshScrollView.setHeaderLayout(new HeaderLayout(getActivity()));
        mPullToRefreshScrollView.setFooterLayout(new FooterLayout(getActivity()));
        xlv_yellowpage = (ListView) view.findViewById(R.id.xlv_yellow_page);
        xlv_yellowpage.setFocusable(false);
        mSortDetails = new ArrayList<PageSortDetail>();
        mSortDetails.clear();
        getYellowPageList(communityId, mSortId, "true", "");
        initEvents();
        mAdapter = new ListViewAdapter<PageSortDetail>(getActivity(), R.layout.item_training_list, mSortDetails) {
            @Override
            public void convert(ViewHolder holder, final PageSortDetail p) {
                if (p != null) {
                    holder.setText(R.id.tv_training_title, p.Title);
                    holder.setText(R.id.tv_training_address, p.Address);
                    //点击地址，开启导航
                    holder.getView(R.id.tv_training_address).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!TextUtils.isEmpty(p.Latitude) && !TextUtils.isEmpty(p.Longitude)) {

                                HashMap<String, String> extras = new HashMap<String, String>();
                                extras.put("LATITUDE", p.Latitude);
                                extras.put("LONGITUDE", p.Longitude);
                                extras.put("LEFT", "LEFT");

                                if (requestPermission()) {
                                    try {
                                        gotoActivity(YellowPages_Map.class.getName(), extras);
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "请手动开启定位权限", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                showToast(R.string.position_on);
                            }
                        }
                    });
                    //点击电话，拨打电话给商家
                    holder.getView(R.id.tel_training).setFocusable(false);
                    holder.getView(R.id.tel_training).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + p.Tel));
                            mContext.startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                        }
                    });
                }
            }
        };
        xlv_yellowpage.setAdapter(mAdapter);
        Services.setListViewHeightBasedOnChildren(xlv_yellowpage);


        xlv_yellowpage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                {

                    Log.e("asd", "map--item---click");
                    PageSortDetail p = mSortDetails.get(i);
                    if (!TextUtils.isEmpty(p.Latitude) && !TextUtils.isEmpty(p.Longitude)) {
                        HashMap<String, String> extras = new HashMap<String, String>();
                        extras.put("LATITUDE", p.Latitude);
                        extras.put("LONGITUDE", p.Longitude);
                        extras.put("LEFT", "LEFT");

                        if (requestPermission()) {
                            try {
                                gotoActivity(YellowPages_Map.class.getName(), extras);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getActivity(), "请手动开启定位权限", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        showToast(R.string.position_on);
                    }
                }
            }
        });

    }


    private void initEvents() {
        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                up = false;
                mSortDetails.clear();
                getYellowPageList(communityId, mSortId, "true", "");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                up = true;
                if (mSortDetails != null && mSortDetails.size() > 0) {
                    getYellowPageList(communityId, mSortId, "true", mSortDetails.get(mSortDetails.size() - 1).Id);
                } else {
                    mPullToRefreshScrollView.onRefreshComplete();
                }
            }
        });
    }

    //根据分类查看当前分类下黄页
    public void getYellowPageList(String communityId, String sortId, String hasSub, String lastId) {
        String url = Services.mHost + "API/YellowPages/GetListYellowPagesBySortId/0?communityId=" + communityId + "&sortId=" + sortId + "&hasSub=" + hasSub + "&lastId=" + lastId + "&pageSize=" + Services.PAGE_SIZE;
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
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                        mPullToRefreshScrollView.onRefreshComplete();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        mPullToRefreshScrollView.onRefreshComplete();
                        Log.e("yellow", "黄页数据" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<PageSortDetail>>() {
                                    }.getType();
                                    datas = gson.fromJson(jsonObject.getString("Obj"), type);
                                    if (datas != null && datas.size() > 0) {
                                        mSortDetails.addAll(datas);
                                        mAdapter.notifyDataSetChanged();
                                        Services.setListViewHeightBasedOnChildren(xlv_yellowpage);
                                    } else {
                                        if (!up) {   //刷新
                                                Log.e("yellow", "黄页数据经纬度" + UserInformation.getUserInfo().CommuntiyLatitude+UserInformation.getUserInfo().CommuntiyLongitude);
                                                //高德地图拉取数据
                                                int currentPage = 0;
                                                PoiSearch.Query query = new PoiSearch.Query(mSortKeywords, mSortTypes);
                                                query.setPageSize(50);// 设置每页最多返回多少条poiitem
                                                query.setPageNum(currentPage);//设置查第一页
                                                PoiSearch poiSearch = new PoiSearch(getActivity(), query);
                                                //获取当前小区的地理位置信息
                                                User user = UserInformation.getUserInfo();
                                                if (Services.isNotNullOrEmpty(user.getCommuntiyLatitude()) && Services.isNotNullOrEmpty(user.getCommuntiyLongitude())) {
                                                    poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(Double.valueOf(user.CommuntiyLatitude),
                                                            Double.valueOf(user.CommuntiyLongitude)), 5000));//设置周边搜索的中心点以及区域
                                                    poiSearch.setOnPoiSearchListener(YellowPageFragmentContent.this);//设置数据返回的监听器
                                                    poiSearch.searchPOIAsyn();
                                                } else {
                                                    showToast("小区位置信息获取失败");
                                                }
                                        } else {         //加载更多
                                            if (datas != null && datas.size() > 0) {
                                                mAdapter.notifyDataSetChanged();
                                                Services.setListViewHeightBasedOnChildren(xlv_yellowpage);
                                            } else {
                                                showToast("沒有更多数据");
                                            }
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
    public void onPoiSearched(PoiResult poiResult, int i) {
        Log.e("yellow", "onPoiSearched-------------" + poiResult+"---"+i);
        if (i == 1000) {
            if (poiResult == null) {
                showToast("获取失败，请检查位置权限是否开启");
            } else {
                List<PoiItem> poiItems;
                poiItems = poiResult.getPois();
                List<PageSortDetail> infos = new ArrayList<>();
                for (int k = 0; k < poiItems.size(); k++) {
                    PoiItem item = poiItems.get(k);
                    if (Services.isNotNullOrEmpty(item.getTel())) {
                        PageSortDetail pd = new PageSortDetail();
                        pd.Tel = item.getTel();
                        pd.Address = item.getSnippet();
                        pd.Title = item.getTitle();
                        pd.Distance = item.getDistance();
                        pd.Latitude = String.valueOf(item.getLatLonPoint().getLatitude());
                        pd.Longitude = String.valueOf(item.getLatLonPoint().getLongitude());
                        infos.add(pd);
                    }
                }
                //排序
                Collections.sort(infos, new Comparator<PageSortDetail>() {
                    @Override
                    public int compare(PageSortDetail t1, PageSortDetail t2) {
                        return t1.Distance.compareTo(t2.Distance);
                    }
                });
                mSortDetails.clear();
                mSortDetails.addAll(infos);
                if (mSortDetails != null && mSortDetails.size() > 0) {
                    mAdapter.notifyDataSetChanged();
                    Services.setListViewHeightBasedOnChildren(xlv_yellowpage);
                } else {
                    mOrderEmpty.setVisibility(View.VISIBLE);
                }
            }
        } else {
            showToast("小区位置信息获取失败");
        }
    }



    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }


    //动态申请权限
    private boolean requestPermission() {
        permissGranted = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(getActivity(), permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(getActivity(), permissions, 321);
            } else {
                permissGranted = true;
            }
        } else {
            permissGranted = true;
        }
        return permissGranted;
    }


    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    boolean noRemaind = shouldShowRequestPermissionRationale(permissions[1]);
                    if (!noRemaind) {
                        Toast.makeText(getActivity(), "请手动开启定位权限", Toast.LENGTH_LONG).show();
                    }
                } else {
                    permissGranted = true;
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(getActivity(), "生活黄页-内容：" + this.getClass().getSimpleName());
    }
}
