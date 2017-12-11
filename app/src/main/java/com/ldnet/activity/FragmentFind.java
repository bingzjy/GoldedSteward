package com.ldnet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.base.BaseFragment;
import com.ldnet.activity.find.FreaMarket;
import com.ldnet.activity.find.InforTabActivity;
import com.ldnet.activity.find.Weekend;
import com.ldnet.activity.qindian.QinDianMain;
import com.ldnet.entities.*;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.QinDianService;
import com.ldnet.utility.*;
import com.ldnet.view.FooterLayout;
import com.ldnet.view.HeaderLayout;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.third.autoscrollviewpager.AutoScrollViewPager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * ***************************************************
 * 主框架 - 发现
 * **************************************************
 */
public class FragmentFind extends BaseFragment implements OnClickListener {

    private TextView tv_main_title;
    private Services services;
    private List<View> mAds;
    private com.ldnet.view.ImageCycleView mViewPager;

    private MyListView lv_find_informations;
    private ListViewAdapter<Information> mAdapter;
    private List<Information> mDatas;

    private LinearLayout ll_find_information;
    private LinearLayout ll_find_freamarket;
    private LinearLayout ll_find_weekend,ll_find_cza;

    private ReadInfoIDs read = ReadInfoIDs.getInstance();
    private List<String> isReadIds;
    private View ads1;
    private List<Advertisement> mAdvertisements;
    private Handler mHandler;
    private TextView mTvFindDetailTitle;
    private List<Information> datas;
    private ArrayList<String> mImageUrl = null;
    private PullToRefreshScrollView mPullToRefreshScrollView;
    private QinDianService qinDianService;
    protected DisplayImageOptions imageOptions;
    String aa = Services.timeFormat();
    String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";

    // onCreate
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_find, container,
                false);
        imageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_info)     //url爲空會显示该图片，自己放在drawable里面的
                .showImageOnFail(R.drawable.default_info)                //加载图片出现问题，会显示该图片
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .extraForDownloader(UserInformation.getUserInfo().UserPhone + "," + aa + "," + aa1)
                .build();

        qinDianService=new QinDianService(getActivity());
        // 初始化视图
        initView(view, inflater, container);
        // 初始化事件
        initEvents(view);
        return view;
    }

    //    // 初始化事件
    private void initEvents(View view) {
        ll_find_freamarket.setOnClickListener(this);
        ll_find_weekend.setOnClickListener(this);
        ll_find_information.setOnClickListener(this);
        ll_find_cza.setOnClickListener(this);
    }


    // 初始化视图
    private void initView(View view, final LayoutInflater inflater,
                          ViewGroup container) {

        // 标题
        tv_main_title = (TextView) view.findViewById(R.id.tv_main_title);
        tv_main_title.setText(R.string.module_title_find);
        services = new Services();
        mPullToRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.main_act_scrollview);
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mPullToRefreshScrollView.setHeaderLayout(new HeaderLayout(getActivity()));
        // 广告
        mImageUrl = new ArrayList<String>();
        mAds = new ArrayList<View>();
        advertisement();
        mViewPager = (com.ldnet.view.ImageCycleView) view.findViewById(R.id.vp_find_ads);
        // 改线ViewPager的高度
        LayoutParams linearParams = (LayoutParams) mViewPager
                .getLayoutParams();
        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        linearParams.height = (int) (dm.widthPixels / 2.4f);
        mViewPager.setLayoutParams(linearParams);

        //跳蚤市场
        ll_find_freamarket = (LinearLayout) view.findViewById(R.id.ll_find_freamarket);
        //周末去哪儿
        ll_find_weekend = (LinearLayout) view.findViewById(R.id.ll_find_weekend);
        // 资讯列表
        ll_find_information = (LinearLayout) view.findViewById(R.id.ll_find_information);

        //充智安
        ll_find_cza=(LinearLayout)view.findViewById(R.id.ll_find_cza);

        //获取已读
        isReadIds = read.getRead(read.TYPE_INFORMATION);
        //ListView
        lv_find_informations = (MyListView) view.findViewById(R.id.lv_find_informations);
        mDatas = new ArrayList<Information>();
        lv_find_informations.setFocusable(false);
        mDatas.clear();
        GetInformations("");
        lv_find_informations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Information information = mDatas.get(i);
                User user = UserInformation.getUserInfo();
                Intent intent = new Intent(getActivity(), Browser.class);
                intent.putExtra("PAGE_IMAGE", information.TitleImageID);
                intent.putExtra("PAGE_TITLE", R.string.information_details);
                intent.putExtra("PAGE_URL", information.InfoUrl + "&IsApp=1&UID=" + user.UserId + "&UName=" + user.UserName + "&UImgID=" + (!TextUtils.isEmpty(user.UserThumbnail) ? user.UserThumbnail : ""));
                intent.putExtra("FROM_CLASS_NAME", MainActivity.class.getName());
                //分享 - 标题、描述、URL
                intent.putExtra("PAGE_TITLE_ORGIN", information.Title);
                intent.putExtra("PAGE_DESCRIPTION_ORGIN", information.Description);
                intent.putExtra("PAGE_URL_ORGIN", information.InfoUrl);
                //标记已读
                read.setRead(information.ID, read.TYPE_INFORMATION);
                isReadIds = read.getRead(read.TYPE_INFORMATION);
                if (isReadIds.contains(information.ID)) {
                    mTvFindDetailTitle.setTextColor(getResources().getColor(R.color.gray_light_1));
                } else {
                    mTvFindDetailTitle.setTextColor(getResources().getColor(R.color.gray_deep));
                }
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });
        initEvents();
    }

    private void initEvents() {
        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                mDatas.clear();
                mImageUrl.clear();
                mViewPager.mImageIndex = 0;
                APPGetJpushNotification(MsgInformation.getMsg().getCallbackId());
                advertisement();
                GetInformations("");
            }
        });
    }



    private com.ldnet.view.ImageCycleView.ImageCycleViewListener mAdCycleViewListener = new com.ldnet.view.ImageCycleView.ImageCycleViewListener() {

        @Override
        public void onImageClick(int position, View imageView) {
            Intent intent = new Intent(getActivity(), Browser.class);
            intent.putExtra("PAGE_TITLE", mAdvertisements.get(position).Title);
            intent.putExtra("PAGE_URL", mAdvertisements.get(position).Url);
            intent.putExtra("FROM_CLASS_NAME", getActivity().getClass().getName());
            //分享数据
            intent.putExtra("PAGE_TITLE_ORGIN", mAdvertisements.get(position).Title);
            intent.putExtra("PAGE_URL_ORGIN", mAdvertisements.get(position).Url);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        }

        @Override
        public void displayImage(String imageURL, ImageView imageView) {
            ImageLoader.getInstance().displayImage(imageURL, imageView, imageOptions);// 此处本人使用了ImageLoader对图片进行加装！
        }
    };



    // onClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //跳蚤市场
            case R.id.ll_find_freamarket:
                Intent intent = new Intent(getActivity(), FreaMarket.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                break;
            //周末去哪儿
            case R.id.ll_find_weekend:
                Intent intent1 = new Intent(getActivity(), Weekend.class);
                startActivity(intent1);
                getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                break;
            //生活资讯
            case R.id.ll_find_information:
                Intent intent2 = new Intent(getActivity(), InforTabActivity.class);
                startActivity(intent2);
                getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                break;
            case R.id.ll_find_cza:
                Intent intent3 = new Intent(getActivity(), QinDianMain.class);
                startActivity(intent3);
                getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(UserInformation.getUserInfo().CZAID)) {
            ll_find_cza.setVisibility(View.VISIBLE);
        } else {
            ll_find_cza.setVisibility(View.GONE);
        }
    }

    private void loadData(Boolean isFirst) {
        if (!isFirst) {
            GetInformations(mDatas.get(mDatas.size() - 1).ID);
        } else {
            mDatas.clear();
            GetInformations("");
        }
    }



    //获取广告信息
    public void advertisement() {
        String url = Services.mHost + "API/Property/GetAppAdvertiseOrEvent?CityId=%s&propertyId=%s";
        url = String.format(url, UserInformation.getUserInfo().CommuntiyCityId, UserInformation.getUserInfo().getPropertyId());
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .build()
                .execute(new DataCallBack(getActivity()) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e("asdsdasd---", "111111111广告" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<Advertisement>>() {
                                    }.getType();
                                    mAdvertisements = gson.fromJson(jsonObject.getString("Obj"), type);
                                    for (int j = 0; j < mAdvertisements.size(); j++) {
                                        mImageUrl.add(Services.getImageUrl(mAdvertisements.get(j).getCover()));
                                    }
                                    mViewPager.setImageResources(mImageUrl, mAdCycleViewListener);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //生活资讯 - 首页
    public void GetInformations(String lastId) {
        //请求的URL
        String url = Services.mHost + "Information/Sel_HomePageList?LastID=%s&PageCnt=%s";
        url = String.format(url, lastId, String.valueOf(Services.PAGE_SIZE));
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5)).build()
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
                        Log.d("asdsdasd", "111111111" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<List<Information>>() {
                                    }.getType();
                                    datas = gson.fromJson(jsonObject.getString("Obj"), type);
                                    if (datas != null) {
                                        mDatas.addAll(datas);
                                        mAdapter = new ListViewAdapter<Information>(getActivity(), R.layout.item_find_detail_infos, mDatas) {
                                            @Override
                                            public void convert(ViewHolder holder, Information information) {
                                                //加载图片
                                                ImageView image = holder.getView(R.id.iv_infos_detail_image);
                                                ImageLoader.getInstance().displayImage(services.getImageUrl(information.TitleImageID), image,imageOptions);
                                                //标题和描述
                                                holder.setText(R.id.tv_find_detail_title, information.Title)
                                                        .setText(R.id.tv_find_detail_desc, information.Description);

                                                //获取标题,设置已读状态的标题颜色
                                                mTvFindDetailTitle = holder.getView(R.id.tv_find_detail_title);
                                                if (isReadIds.contains(information.ID)) {
                                                    mTvFindDetailTitle.setTextColor(getResources().getColor(R.color.gray_light_1));
                                                } else {
                                                    mTvFindDetailTitle.setTextColor(getResources().getColor(R.color.gray_deep));
                                                }
                                            }
                                        };
                                        lv_find_informations.setAdapter(mAdapter);
                                        Utility.setListViewHeightBasedOnChildren(lv_find_informations);
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            APPGetJpushNotification(MsgInformation.getMsg().getCallbackId());
        }
    }

    public void APPGetJpushNotification(int id) {
        // 请求的URL
//        String url = Services.mHost + "API/Property/APPGetNotification/%s?communityId=%s";
        String url = Services.mHost + "API/Property/APPGetJpushNotification/%s?communityId=%s&resultId=%s";
        url = String.format(url, UserInformation.getUserInfo().UserId, UserInformation.getUserInfo().CommunityId, id);
        String aa = Services.timeFormat();
        String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String aa2 = url;
        String md5 = UserInformation.getUserInfo().getUserPhone() + aa + aa1 + aa2 + Services.TOKEN;
        OkHttpUtils.get().url(url)
                .addHeader("Cookie", CookieInformation.getUserInfo().getCookieinfo())
                .addHeader("phone", UserInformation.getUserInfo().getUserPhone())
                .addHeader("timestamp", aa)
                .addHeader("nonce", aa1)
                .addHeader("signature", Services.textToMD5L32(md5))
                .build()
                .execute(new DataCallBack(getActivity()) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call, e, i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.d("asdsdasd31---31231", "111111111" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    MessageCallBack messageCallBack = gson.fromJson(jsonObject.getString("Obj"), MessageCallBack.class);
                                    if (messageCallBack != null) {
                                        Msg msg = MsgInformation.getMsg();
                                        msg.setCallbackId(Integer.parseInt(messageCallBack.getCallbackId()));
                                        msg.setCOMPLAIN(Boolean.parseBoolean(messageCallBack.getN().getCOMPLAIN()));
                                        msg.setCOMMUNICATION(Boolean.parseBoolean(messageCallBack.getN().getCOMMUNICATION()));
                                        msg.setFEE(Boolean.parseBoolean(messageCallBack.getN().getFEE()));
                                        msg.setFEEDBACK(Boolean.parseBoolean(messageCallBack.getN().getFEEDBACK()));
                                        msg.setMESSAGE(Boolean.parseBoolean(messageCallBack.getN().getMESSAGE()));
                                        msg.setNOTICE(Boolean.parseBoolean(messageCallBack.getN().getNOTICE()));
                                        msg.setORDER(Boolean.parseBoolean(messageCallBack.getN().getORDER()));
                                        msg.setPAGE(Boolean.parseBoolean(messageCallBack.getN().getPAGE()));
                                        msg.setREPAIRS(Boolean.parseBoolean(messageCallBack.getN().getREPAIRS()));
                                        msg.setOTHER(Boolean.parseBoolean(messageCallBack.getN().getOTHER()));
                                        MsgInformation.setMsgInfo(msg);
                                        if (msg.isNOTICE() || msg.isCOMMUNICATION() || msg.isFEE() || msg.isCOMPLAIN() || msg.isREPAIRS()) {
                                            getActivity().findViewById(R.id.iv_zc1).setVisibility(View.VISIBLE);
                                        }
                                        if (msg.isMESSAGE() || msg.isFEEDBACK() || msg.isORDER()) {
                                            getActivity().findViewById(R.id.iv_dc1).setVisibility(View.VISIBLE);
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


}
