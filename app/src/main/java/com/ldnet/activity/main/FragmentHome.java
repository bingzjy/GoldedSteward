package com.ldnet.activity.main;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.*;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.*;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.dh.bluelock.imp.BlueLockPubCallBackBase;
import com.dh.bluelock.imp.OneKeyInterface;
import com.dh.bluelock.object.LEDevice;
import com.dh.bluelock.pub.BlueLockPub;
import com.dh.bluelock.util.Constants;
import com.ldnet.activity.base.BaseFragment;
import com.ldnet.activity.commen.Services;
import com.ldnet.activity.home.*;
import com.ldnet.activity.informationpublish.CommunityInfoBarMainActivity;
import com.ldnet.activity.mall.GoodsList;
import com.ldnet.activity.mall.Goods_Details;
import com.ldnet.activity.me.*;
import com.ldnet.activity.yellowpage.YellowPageTabActivity;
import com.ldnet.entities.*;
import com.ldnet.goldensteward.R;
import com.ldnet.service.*;
import com.ldnet.utility.*;
import com.ldnet.activity.adapter.ListViewAdapter;
import com.ldnet.utility.sharepreferencedata.KeyCache;
import com.ldnet.utility.sharepreferencedata.PushMessage;
import com.ldnet.utility.sharepreferencedata.TokenInformation;
import com.ldnet.utility.sharepreferencedata.UserInformation;
import com.ldnet.view.HeaderLayout;
import com.ldnet.view.customview.ImageCycleView;
import com.ldnet.view.customview.BadgeView;
import com.ldnet.view.customview.BorderScrollView;
import com.ldnet.view.customview.MyGridView;
import com.ldnet.view.dialog.CustomAlertDialog;
import com.ldnet.view.dialog.DialogAlert;
import com.ldnet.view.dialog.MyDailogTag;
import com.library.PullToRefreshBase;
import com.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tendcloud.tenddata.TCAgent;

import java.util.*;

import static com.ldnet.goldensteward.R.id.ll_yellow_service;
import static com.ldnet.activity.commen.Services.CLASS_FROM;
import static com.ldnet.activity.commen.Services.COMMUNITY_ID;
import static com.ldnet.activity.commen.Services.COMMUNITY_NAME;
import static com.ldnet.activity.commen.Services.ROOM_ID;
import static com.ldnet.activity.commen.Services.ROOM_NAME;
import static com.ldnet.activity.commen.Services.TO_APPLY;
import static com.ldnet.utility.Utility.getScreenWidthforPX;

/**
 * ***************************************************
 * 主框架 - 首页
 * **************************************************
 */
public class FragmentHome extends BaseFragment implements OnClickListener, BorderScrollView.OnBorderListener {

    private TextView tv_main_title;
    private LinearLayout ll_yellow_rental;
    private LinearLayout ll_yellow_pages;
    private ImageView iv_home_ads, iv_home_property_thumbnail, unread_fuwu, unread_notification, unread_fee, imageTest;
    private LinearLayout ll_property_notification;
    private LinearLayout ll_property_services;
    private LinearLayout ll_property_notice;
    private LinearLayout ll_property_infobar;
    private LinearLayout ll_property_service_activity;
    private LinearLayout mAppHomePage;
    private RelativeLayout ll_home;
    private ImageButton bt_open_door;
    private Services services;
    private List<APPHomePage_Area> mAppHomePageArea;
    private List<APPHomePage_Column> mData = new ArrayList<>();
    private BadgeView badgeView;
    //分页常量
    static Integer PAGE_SIZE = Integer.MAX_VALUE;
    private MyGridView mGridViewGoods;
    private ListViewAdapter mAdapter;
    List<Goods> goods;
    private PullToRefreshScrollView mRefreshableView;
    private View view;
    private ArrayList<String> mImageUrl = new ArrayList<>();
    private ImageCycleView mAdView;
    private ImageView splash_iv;
    private LayoutInflater inflater1;
    private BluetoothAdapter bluetoothAdapter;    //本地蓝牙适配器
    private OneKeyInterface blueLockPub;
    private LockCallBack lockCallBack;
    private String deviceID = "";//设备id
    private List<KeyChain> keyChain = new ArrayList<>();
    private HashMap<String, KeyChain> keyChainMap = new HashMap<>();
    private String feeArrearage = "";
    private boolean openEntranceState;  //是否开通门禁 ，true开通
    private SensorManager sensorManager;
    private Vibrator vibrator;
    private boolean approvePass;
    private AcountService acountService;
    private BindingService bindingService;
    private PropertyFeeService propertyFeeService;
    private EntranceGuardService entranceGuardService;
    private LadderControlService ladderControlService;
    private HomeService homeService;
    private GoodsService goodsService;
    private String tag = FragmentHome.class.getSimpleName();
    private boolean openDoorBykeyChain;
    private LayoutInflater currentHomePageLayout;
    private TextView title;
    private TextView description;
    private ImageView image;
    private HashMap<String, LEDevice> scanDeviceResult = new HashMap<String, LEDevice>();  //存放扫描结果
    private List<KeyChain> deviceAvailable = new ArrayList<>();
    private HashSet<LEDevice> deviceSet = new HashSet<>();
    private Handler handlerDeleteRed = new Handler() {
    };
    private DisplayImageOptions imageOptions;
    private String aa = Services.timeFormat();
    private String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
    private MyDailogTag openDoorDialog;
    private LEDevice currentDevice;
    private static SoundPool soundPool;
    private Timer timer;
    private TimerTask timerTask;
    private HashMap<String, LCDevice> lcDeviceHashMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_home, container,
                false);
        imageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_info)     //url爲空會显示该图片，自己放在drawable里面的
                .showImageOnFail(R.drawable.default_info)                //加载图片出现问题，会显示该图片
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .extraForDownloader(UserInformation.getUserInfo().UserPhone + "," + aa + "," + aa1)
                .build();

        initService();
        initView(view, inflater);
        inflater1 = inflater;
        // 初始化事件
        initEvents();
        splash_iv.setVisibility(View.VISIBLE);
        //初始化传感器
        sensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        vibrator = (Vibrator) getActivity().getSystemService(getActivity().VIBRATOR_SERVICE);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(getActivity(), "首页-首页" + this.getClass().getSimpleName());

        //获取小红点
        homeService.getAppRedPoint(handlerGetRedPointPush);

        // 注册监听器
        setSensorManager(true);

        //获取最新用户信息
        SetCurrentInforamtion();

        //是否开通门禁
        checkOpenEntrance();

        //判断当前房屋是否通过验证
        if (UserInformation.getUserInfo().UserId != null && UserInformation.getUserInfo().getHouseId() != null
                && !UserInformation.getUserInfo().getHouseId().equals("")) {
            getApprove();
        }

        //判断用户是否有物业欠费
        if (UserInformation.getUserInfo().getHouseId() != null) {
            getArrearageAmount();
        }

        //获取当前房间钥匙串
        getKeyChain(true);

        // 标题
        tv_main_title.setText(UserInformation.getUserInfo().CommuntiyName);
        //物业图标
        User user = UserInformation.getUserInfo();
        if (!TextUtils.isEmpty(user.PropertyThumbnail)) {
            iv_home_property_thumbnail.setBackgroundColor(Color.WHITE);
            ImageLoader.getInstance().displayImage(Services.getImageUrl(user.PropertyThumbnail), iv_home_property_thumbnail, imageOptions);
        } else {
            iv_home_property_thumbnail.setBackgroundColor(Color.TRANSPARENT);
            iv_home_property_thumbnail.setImageResource(R.drawable.home_services_n);
        }
    }


    //初始化服务
    private void initService() {
        services = new Services();
        acountService = new AcountService(getActivity());
        bindingService = new BindingService(getActivity());
        propertyFeeService = new PropertyFeeService(getActivity());
        entranceGuardService = new EntranceGuardService(getActivity());
        homeService = new HomeService(getActivity());
        goodsService = new GoodsService(getActivity());
        ladderControlService = new LadderControlService(getActivity());
    }

    // 初始化事件
    private void initEvents() {
        ll_property_infobar.setOnClickListener(this);
        ll_property_service_activity.setOnClickListener(this);
        ll_property_notification.setOnClickListener(this);
        ll_property_services.setOnClickListener(this);
        ll_property_notice.setOnClickListener(this);
        ll_yellow_rental.setOnClickListener(this);
        ll_yellow_pages.setOnClickListener(this);
        tv_main_title.setOnClickListener(this);
        bt_open_door.setOnClickListener(this);
        iv_home_ads.setOnClickListener(this);

        //刷新
        mRefreshableView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                mAppHomePage.removeAllViews();
                getData(true);
                getHomePageArea(inflater1);
            }
        });
    }

    //设置小红点的显示
    private void showRedDot() {
        //物业通知
        if (PushMessage.getPushInfo().NOTICE) {
            unread_notification.setVisibility(View.VISIBLE);
        } else {
            unread_notification.setVisibility(View.GONE);
        }

        //物业服务模块
        if (PushMessage.getPushInfo().COMPLAIN || PushMessage.getPushInfo().REPAIRS || PushMessage.getPushInfo().COMMUNICATION) {
            unread_fuwu.setVisibility(View.VISIBLE);
        } else {
            unread_fuwu.setVisibility(View.GONE);
        }

        //物业交费
        if (PushMessage.getPushInfo().FEE) {
            unread_fee.setVisibility(View.VISIBLE);
        } else {
            unread_fee.setVisibility(View.GONE);
        }

        //物业通知、物业服务模块 ，首页底部显示小红点
        if (PushMessage.getPushInfo().COMPLAIN || PushMessage.getPushInfo().REPAIRS ||
                PushMessage.getPushInfo().COMMUNICATION || PushMessage.getPushInfo().NOTICE || PushMessage.getPushInfo().FEE) {
            getActivity().findViewById(R.id.iv_zc1).setVisibility(View.VISIBLE);
        } else {
            getActivity().findViewById(R.id.iv_zc1).setVisibility(View.GONE);
        }

        //意见反馈、物业消息、订单，我的模块显示小红点
        if (PushMessage.getPushInfo().FEEDBACK || PushMessage.getPushInfo().PROPERTY_MSG || PushMessage.getPushInfo().ORDER) {
            getActivity().findViewById(R.id.iv_dc1).setVisibility(View.VISIBLE);
        } else {
            getActivity().findViewById(R.id.iv_dc1).setVisibility(View.GONE);
        }
    }

    // 初始化视图
    private void initView(View view, final LayoutInflater inflater) {
        if (Services.TOKEN != null && !Services.TOKEN.equals("")) {
        } else {
            Services.TOKEN = TokenInformation.getTokenInfo().toString();
        }
        mData = new ArrayList<>();

        //初始化蓝牙适配器
        initBlueBooth();

        //外布局
        ll_home = (RelativeLayout) view.findViewById(R.id.ll_home);
        // 标题
        tv_main_title = (TextView) view.findViewById(R.id.tv_main_title);
        bt_open_door = (ImageButton) view.findViewById(R.id.bt_open_door);
        //物业图标
        iv_home_property_thumbnail = (ImageView) view.findViewById(R.id.iv_home_property_thumbnail);
        unread_fuwu = (ImageView) view.findViewById(R.id.unread_fuwu);
        unread_fee = (ImageView) view.findViewById(R.id.unread_fee);
        unread_notification = (ImageView) view.findViewById(R.id.unread_notification);
        //通知
        ll_property_notification = (LinearLayout) view.findViewById(R.id.ll_property_notification);
        //服务
        ll_property_services = (LinearLayout) view.findViewById(R.id.ll_property_services);

        //小红点
        RelativeLayout rl_home_property_thumbnail = (RelativeLayout) view.findViewById(R.id.rl_home_property_thumbnail);
        badgeView = new BadgeView(getActivity());
        badgeView.setWidth(Utility.dip2px(getActivity(), 10));
        badgeView.setHeight(Utility.dip2px(getActivity(), 10));
        badgeView.setBackground(R.drawable.round_tip, Color.RED);
        badgeView.setVisibility(View.GONE);
        badgeView.setTextColor(Color.RED);
        badgeView.setTargetView(rl_home_property_thumbnail);
        //缴费
        ll_property_notice = (LinearLayout) view.findViewById(R.id.ll_property_notice);
        //房屋租赁
        ll_yellow_rental = (LinearLayout) view.findViewById(R.id.ll_yellow_rental);
        //黄页
        ll_yellow_pages = (LinearLayout) view.findViewById(R.id.ll_yellow_pages);
        //邻里通
        ll_property_infobar = (LinearLayout) view.findViewById(R.id.ll_yellow_infobar);
        //周边惠
        ll_property_service_activity = (LinearLayout) view.findViewById(R.id.ll_yellow_service);

        // 刷新的控件
        mRefreshableView = (PullToRefreshScrollView) view.findViewById(R.id.refresh_root);
        mRefreshableView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mRefreshableView.setHeaderLayout(new HeaderLayout(getActivity()));
        splash_iv = (ImageView) view.findViewById(R.id.splash_iv);

        //社区小店
        iv_home_ads = (ImageView) view.findViewById(R.id.iv_home_ads);
        iv_home_ads.setImageResource(R.drawable.community_shop);

        //首页展示
        mAppHomePage = (LinearLayout) view.findViewById(R.id.app_home_page);
        //获取首页区域数据
        getHomePageArea(inflater);

        mGridViewGoods = (MyGridView) view.findViewById(R.id.grid_goods);
        mGridViewGoods.setFocusable(false);
        mGridViewGoods.setSelector(R.color.white);
        goods = new ArrayList<Goods>();
        mAdapter = new ListViewAdapter<Goods>(getActivity(), R.layout.item_home_goods, goods) {
            @Override
            public void convert(ViewHolder holder, Goods goods1) {
                // 商品图片
                // 改线ViewPager的高度
                ImageView thumbnail = holder.getView(R.id.iv_goods_image);
                //设置商品图片的高度
                LayoutParams linearParams_good = (LayoutParams) thumbnail.getLayoutParams();
                int height = (getScreenWidthforPX(getActivity()) - Utility.dip2px(getActivity(), 8.0f)) / 2;
                linearParams_good.height = height;
                thumbnail.setLayoutParams(linearParams_good);
                ImageLoader.getInstance().displayImage(Services.getImageUrl(goods1.getThumbnail()), thumbnail, imageOptions);
                // 商品标题
                ((TextView) holder.getView(R.id.tv_goods_name)).setText(goods1.T.trim());

                // 商品价格
                TextView tv_goods_price = holder.getView(R.id.tv_goods_price);
                if (goods1.Type.equals(2)) {
                    //tv_goods_price.setVisibility(View.GONE);
                    tv_goods_price.setText("报名" + " " + goods1.GP);
                    tv_goods_price.setTextColor(getResources().getColor(R.color.gray));
                } else {
                    tv_goods_price.setVisibility(View.VISIBLE);
                    tv_goods_price.setText("￥" + goods1.GP);
                }
            }
        };
        mGridViewGoods.setAdapter(mAdapter);
        mGridViewGoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), Goods_Details.class);
                intent.putExtra("GOODS", goods.get(position));
                intent.putExtra("PAGE_TITLE", "");
                intent.putExtra("FROM_CLASS_NAME", MainActivity.class.getName());
                intent.putExtra("URL", goods.get(position).URL);
                intent.putExtra("CID", goods.get(position).GID);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });

        //获取商品数据
        getData(true);
    }

    //初始化蓝牙适配器
    private void initBlueBooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        blueLockPub = BlueLockPub.bleLockInit(getActivity());

        if (lockCallBack == null) {
            lockCallBack = new LockCallBack();
        }
        if (bluetoothAdapter == null) {
            Toast.makeText(getActivity(), "本地蓝牙不可用", Toast.LENGTH_SHORT).show();
        }
        ((BlueLockPub) blueLockPub).setResultCallBack(lockCallBack);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 生活黄页
            case R.id.ll_yellow_pages:

                Intent intent_yellowpage = new Intent(getActivity(), YellowPageTabActivity.class);
                intent_yellowpage.putExtra("YELLOW_PAGE_SORT_ID", "8f1f1e4092784199bbec0229e1cca9b0");
                intent_yellowpage.putExtra("YELLOW_PAGE_SORT_NAME", getResources().getString(R.string.fragment_home_yellow_pages));
                intent_yellowpage.putExtra("flag", "yellow");
                startActivity(intent_yellowpage);
                getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                break;
            // 房屋租售
            case R.id.ll_yellow_rental:
                Intent intent_rent = new Intent(getActivity(), HouseRent_List.class);
                startActivity(intent_rent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                break;
            //物业通知
            case R.id.ll_property_notification:
                hintRed(0);
                if (!TextUtils.isEmpty(UserInformation.getUserInfo().PropertyId)) {
                    Intent intent = new Intent(getActivity(), Notification.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                } else {
                    Intent intent = new Intent(getActivity(), Browser.class);
                    intent.putExtra("PAGE_URL", "http://p.goldwg.com:88/mobile/yaoqingwuye");
                    intent.putExtra("FROM_CLASS_NAME", getActivity().getClass().getName());
                    intent.putExtra("PAGE_URL_ORGIN", "http://p.goldwg.com:88/mobile/yaoqingwuye");
                    intent.putExtra("PAGE_TITLE_ORGIN", "金牌管家邀请函");
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                }

                break;
            //物业服务
            case R.id.ll_property_services:

                if (!TextUtils.isEmpty(UserInformation.getUserInfo().PropertyId)) {
                    if (!TextUtils.isEmpty(UserInformation.getUserInfo().HouseId)) {
                        Intent intent = new Intent(getActivity(), Property_Services.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                    } else {
                        DialogAlert dialog = new DialogAlert(getActivity(), getString(R.string.nobind_room), new AlertGotoActivity());
                        dialog.show();
                    }
                } else {
                    Intent intent = new Intent(getActivity(), Browser.class);
                    intent.putExtra("PAGE_URL", "http://p.goldwg.com:88/mobile/yaoqingwuye");
                    intent.putExtra("FROM_CLASS_NAME", getActivity().getClass().getName());
                    intent.putExtra("PAGE_URL_ORGIN", "http://p.goldwg.com:88/mobile/yaoqingwuye");
                    intent.putExtra("PAGE_TITLE_ORGIN", "金牌管家邀请函");
                    startActivity(intent);
                }
                break;
            //物业缴费
            case R.id.ll_property_notice:
                hintRed(4);
                if (!TextUtils.isEmpty(UserInformation.getUserInfo().PropertyId)) {
                    if (!TextUtils.isEmpty(UserInformation.getUserInfo().HouseId)) {
                        Intent intent = new Intent(getActivity(), Property_Fee.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                    } else {
                        DialogAlert dialog = new DialogAlert(getActivity(), getString(R.string.nobind_room), new AlertGotoActivity());
                        dialog.show();
                    }
                } else {
                    Intent intent = new Intent(getActivity(), Browser.class);
                    intent.putExtra("PAGE_URL", "http://p.goldwg.com:88/mobile/yaoqingwuye");
                    intent.putExtra("FROM_CLASS_NAME", getActivity().getClass().getName());
                    intent.putExtra("PAGE_URL_ORGIN", "http://p.goldwg.com:88/mobile/yaoqingwuye");
                    intent.putExtra("PAGE_TITLE_ORGIN", "金牌管家邀请函");
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                }
                break;
            //我的小区
            case R.id.tv_main_title:
                try {
                    HashMap<String, String> extras = new HashMap<String, String>();
                    extras.put("NOT_FROM_ME", "105");
                    gotoActivity(com.ldnet.activity.me.Community.class.getName(), extras);
                    getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt_open_door:
                //开门条件：开启门禁、开启蓝牙、入住金管家、有房屋，再请求钥匙，在handle中做处理
                //1.门禁开门
                //  openClick();
                //2.梯控
                try {
                    gotoActivity(LadderControlActivity.class.getName(), null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                //3.新蓝牙
                // entranceGuardService.getGateInfo(new Handler());
                break;
            //邻里通
            case R.id.ll_yellow_infobar:
                try {
                    gotoActivity(CommunityInfoBarMainActivity.class.getName(), null);
                    getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            //周边惠
            case ll_yellow_service:
                Intent intent_housekeep = new Intent(getActivity(), CommunityServicesPageTabActivity.class);
                intent_housekeep.putExtra("YELLOW_PAGE_SORT_ID", "254c8473cd98410aa5d73001ad715ff4");
                intent_housekeep.putExtra("YELLOW_PAGE_SORT_NAME", getResources().getString(R.string.fragment_home_yellow_housekeeping));
                startActivity(intent_housekeep);
                getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                break;
            case R.id.iv_home_ads:
                //旧版社区小店
                goodsService.communityshops(handlerCommunityShop);
                //新版社区小店
//                startActivity(new Intent(getActivity(), CommunityShopMainActivity.class));
//                getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                break;
            default:
                break;
        }
    }


    //获取商品首页区域内容
    public void getHomePageArea(final LayoutInflater inflater) {
        currentHomePageLayout = inflater;
        goodsService.getHomePageArea(handlerGetHomePageArea);
    }

    //获取商品
    public void getData(Boolean isFirst) {
        String lastId;
        if (!isFirst) {
            lastId = goods.get(goods.size() - 1).GID;
        } else {
            goods.clear();
            lastId = "";
        }
        goodsService.getGoodsData(lastId, PAGE_SIZE, handlerGetGoodsData);
    }

    //获取钥匙串
    public void getKeyChain(boolean init) {
        keyChain.clear();
        keyChainMap.clear();
        openDoorBykeyChain = !init;
        showProgressDialog();
        entranceGuardService.getKeyChain(true, handerGetKeyChain);
    }

    //获取用户欠费记录
    private void getArrearageAmount() {
        feeArrearage = "";
        if (propertyFeeService != null) {
            showProgressDialog();
            propertyFeeService.getArrearageAmount(handlerGetArrageAmount);
        }
    }

    //获取用户最新信息
    public void SetCurrentInforamtion() {
        showProgressDialog();
        bindingService.SetCurrentInforamtion(UserInformation.getUserInfo().getCommunityId(), UserInformation.getUserInfo().HouseId, handlerSetCurrentInforamtion);
    }

    //判断是否验证
    public void getApprove() {
        showProgressDialog();
        acountService.getApprove(UserInformation.getUserInfo().getHouseId(), UserInformation.getUserInfo().UserId, handlerGetApprove);
    }


    //开门点击事件
    private void openClick() {
        if (openDoorEnable()) {
            if (openDoorDialog == null) {
                openDoorDialog = new MyDailogTag(getActivity(), feeArrearage);
            } else {
                openDoorDialog.startDialogShow(feeArrearage);
            }
            openDoorDialog.setType(0); //0开门等待状态 1开门成功 2开门失败
            openDoorDialog.setDialogcallback(openDoorDialogCallBack);
            openDoorDialog.show();
            if (keyChain == null || keyChain.size() == 0) {
                getKeyChain(false);
            } else { //获取钥匙串成功，开启蓝牙扫描
                blueStartScan();
            }
            //定时器,定时10s
            runTimer();
        }
    }


    //开门条件：入驻金管家、开启门禁、开启蓝牙、有房屋，再请求钥匙，在handle中做处理
    private boolean openDoorEnable() {
//        //未入驻金牌管家
//        if (TextUtils.isEmpty(UserInformation.getUserInfo().getPropertyId())) {      //有无物业入驻
//            Intent intent = new Intent(getActivity(), Browser.class);
//            intent.putExtra("PAGE_URL", "http://www.goldwg.com:88/mobile/yaoqingwuye");
//            intent.putExtra("FROM_CLASS_NAME", getActivity().getClass().getName());
//            intent.putExtra("PAGE_URL_ORGIN", "http://www.goldwg.com:88/mobile/yaoqingwuye");
//            intent.putExtra("PAGE_TITLE_ORGIN", "金牌管家邀请函");
//            startActivity(intent);
//            getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
//            return false;
//        }
//
//        //未开通门禁
//        if (!openEntranceState) {
//            openEntrance();
//            return false;
//        }

        //未绑定房屋
        if (TextUtils.isEmpty(UserInformation.getUserInfo().getHouseId())) {
            DialogAlert dialog = new DialogAlert(getActivity(), getString(R.string.not_exist_house), new AlertGotoActivity());
            dialog.show();
            return false;
        }

        //房屋未验证
        if (!approvePass) {
            CustomAlertDialog dialog2 = new CustomAlertDialog(getActivity(), false, getResources().getString(R.string.dialog_title), getResources().getString(R.string.dialog_verify));
            dialog2.show();
            dialog2.setDialogCallback(dialogcallback);

            return false;
        }

        //提示开启蓝牙
        if (bluetoothAdapter.isEnabled() == false) {
            showToast(getString(R.string.noopen_bulebooth));
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            blueLockPub = BlueLockPub.bleLockInit(getActivity());
            return false;
        }
        return true;
    }


    //是否启用摇一摇
    private void setSensorManager(boolean available) {
        if (available) {
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_GAME);
        } else {
            sensorManager.unregisterListener(sensorEventListener); //未审核禁用摇一摇
        }
    }


    //开门动作：当前设备和钥匙串匹配成功，即可开门
    private void openDoor() {
        deviceID = "";
        deviceAvailable.clear();
        closeProgressDialog1();

        if (scanDeviceResult != null && scanDeviceResult.size() > 0) {

            Iterator<String> iterator = scanDeviceResult.keySet().iterator();
            while (iterator.hasNext()) {
                KeyChain keyValue = keyChainMap.get(iterator.next());
                if (keyValue != null) {
                    deviceAvailable.add(keyValue);
                }
            }

            Log.e("aaa", "开门匹配设备：" + deviceAvailable.size());

            if (deviceAvailable.size() == 0) {
                Log.e("aaa", "开门失败，无匹配");
                openDoorDialog.setType(2); //开门失败
            } else if (deviceAvailable.size() == 1) {  //只有一个设备
                KeyChain key = deviceAvailable.get(0);
                currentDevice = scanDeviceResult.get(key.getId());
                deviceID = currentDevice.getDeviceId();
                currentDevice.setDevicePsw(key.getPassword());
                Log.e("aaa", "独立设备，开门预备：" + currentDevice.toString());

                blueLockPub.oneKeyOpenDevice(currentDevice, currentDevice.getDeviceId(), currentDevice.getDevicePsw());

            } else if (deviceAvailable.size() > 1) {  //多个设备
                int min = 0;
                KeyChain minKey = null;  //默认第一个最强
                for (KeyChain key : deviceAvailable) {
                    LEDevice device = scanDeviceResult.get(key.getId());
                    if (min == 0) {
                        min = Math.abs(device.getRssi());
                        minKey = key;
                    } else {
                        if (Math.abs(device.getRssi()) < min) {
                            min = Math.abs(device.getRssi());
                            minKey = key;
                        }
                    }
                }

                //选择信号强度最大的开启蓝牙门禁
                currentDevice = scanDeviceResult.get(minKey.getId());
                deviceID = currentDevice.getDeviceId();
                currentDevice.setDevicePsw(minKey.getPassword());
                Log.e("aaa", "多设备，信号最强开门" + currentDevice.toString() + "  RSSI:" + currentDevice.getRssi());
                blueLockPub.oneKeyOpenDevice(currentDevice, currentDevice.getDeviceId(), currentDevice.getDevicePsw());
            }
        } else {
            Log.e("aaa", "开门失败，无设备");
            openDoorDialog.setType(2); //开门失败
        }
    }


    //开启门禁开门定时器
    private void runTimer() {
        if (timer != null) {
            if (timerTask != null) {
                timerTask.cancel(); //销毁定时器
                timer.cancel();
            }
        }
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                //超时点击重试
                handlerOpenDoorTimer.sendEmptyMessage(111);
            }
        };
        timer.schedule(timerTask, 10000);
    }


    //开启蓝牙扫描
    private void blueStartScan() {
        scanDeviceResult.clear(); //清空已扫描的设备
        deviceSet.clear();

        ((BlueLockPub) blueLockPub).setLockMode(Constants.LOCK_MODE_MANUL, null, false);
        ((BlueLockPub) blueLockPub).scanDevice(1000);

        runTimer();
    }


    //判断业主是否通过认证
    private void checkOpenEntrance() {
        //true 表示未开通门禁；false表示开通门禁
        openEntranceState = false;
        entranceGuardService.checkOpenEntrance(UserInformation.getUserInfo().getCommunityId(), handlerCheckOpenEntrance);
    }

    //未开通门禁提示对话框
    private void openEntrance() {
        TextView log_off_cancel;
        TextView log_off_confirm;
        TextView tv_dialog_title;
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.ly_off);
        alertDialog.findViewById(R.id.line).setVisibility(View.VISIBLE);
        tv_dialog_title = (TextView) alertDialog.findViewById(R.id.tv_dialog_title);
        tv_dialog_title.setText(getString(R.string.no_entrance));
        log_off_cancel = (TextView) alertDialog.findViewById(R.id.log_off_cancel);
        log_off_confirm = (TextView) alertDialog.findViewById(R.id.log_off_confirm);
        log_off_confirm.setText("确定");
        log_off_cancel.setText("取消");
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setAttributes(lp);
        log_off_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.showCallPop(getActivity(), false);
                alertDialog.dismiss();
            }
        });
        log_off_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }


    //身份未通过认证时弹出对话框
    CustomAlertDialog.Dialogcallback dialogcallback = new CustomAlertDialog.Dialogcallback() {
        @Override
        public void dialogdo() {
            HashMap<String, String> extras = new HashMap<String, String>();
            extras.put(TO_APPLY, "PASS");
            extras.put(ROOM_ID, UserInformation.getUserInfo().getHouseId());
            extras.put(ROOM_NAME, UserInformation.getUserInfo().getHouseName());
            extras.put(CLASS_FROM, getActivity().getClass().getName());
            extras.put(COMMUNITY_ID, UserInformation.getUserInfo().getCommunityId());
            extras.put(COMMUNITY_NAME, UserInformation.getUserInfo().getCommuntiyName());
            try {
                gotoActivityAndFinish(VisitorValidComplete.class.getName(), extras);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void dialogDismiss() {

        }
    };

    //蓝牙门禁
    class LockCallBack extends BlueLockPubCallBackBase {
        @Override
        public void openCloseDeviceCallBack(int i, int i1, String... strings) {
            closeProgressDialog1();
            playSound(R.raw.open_door_action, getActivity());
            if (i == 0) {
                openDoorDialog.setType(1);
                //添加开门日志
                if (deviceID != null && !deviceID.equals("")) {
                    entranceGuardService.EGLog(deviceID, handlerEGlog);
                }

            } else {
                openDoorDialog.setType(2);
                showToast("开门失败，请靠近设备再试");
            }
        }

        @Override
        public void scanDeviceCallBack(LEDevice leDevice, int i, int i1) {
            scanDeviceResult.put(leDevice.getDeviceId(), leDevice);
            deviceSet.add(leDevice);
            Log.e("aaa", "门禁：" + leDevice.getDeviceId() + "  " + leDevice.getDeviceName() + " RSSI:" + leDevice.getRssi());
        }

        @Override
        public void scanDeviceEndCallBack(int i) {
            Log.e("aaa", "蓝牙扫描完毕" + scanDeviceResult.size());
            openDoor();
        }
    }

    //开门弹出框
    MyDailogTag.DialogOpenDoorCallBack openDoorDialogCallBack = new MyDailogTag.DialogOpenDoorCallBack() {
        @Override
        public void clickEvent(int type) {
            if (type == 1) {   //开门成功，点击查看物业费
                Intent intent = new Intent(getActivity(), Property_Fee.class);
                startActivity(intent);
                openDoorDialog.hide();
            } else if (type == 2) {  //开门失败，点击重新扫描
                openDoorDialog.setType(0);
                blueStartScan();
            }
        }
    };


    //重力感应监听
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            // 传感器信息改变时执行该方法
            float[] values = sensorEvent.values;
            //设备坐标系是固定于设备的，与设备的方向（在世界坐标系中的朝向）无关
            float x = values[0]; // x轴方向的重力加速度，向右为正
            float y = values[1]; // y轴方向的重力加速度，向前为正
            float z = values[2]; // z轴方向的重力加速度，向上为正

            // 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
            int medumValue = 19;// 三星 i9250怎么晃都不会超过20，没办法，只设置19了
            if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
                //设置震动时长
                vibrator.vibrate(200);
                Message msg = new Message();
                msg.what = 123;
                handler.sendMessage(msg);

                //播放摇一摇音效
                playSound(R.raw.shake, getActivity());
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

    };

    //图片轮播器
    private ImageCycleView.ImageCycleViewListener mAdCycleViewListener = new ImageCycleView.ImageCycleViewListener() {

        @Override
        public void onImageClick(int position, View imageView) {
            // TODO 单击图片处理事件

        }


        @Override
        public void onImageDataClick(int position, View imageView, List<APPHomePage_Column> mData) {
            // TODO 单击图片处理事件

            if (mData != null && mData.size() > 0 && mData.get(position) != null) {

                if (mData.get(position).TYPES == 3) {
                    Intent intent = new Intent(getActivity(), Browser.class);
                    intent.putExtra("PAGE_URL", mData.get(position).URL);
                    intent.putExtra("PAGE_TITLE", mData.get(position).TITLE);
                    intent.putExtra("FROM_CLASS_NAME", getActivity().getClass().getName());
                    startActivity(intent);
                } else if (mData.get(position).TYPES == 4) {
                    Intent intent = new Intent(getActivity(), Goods_Details.class);
                    intent.putExtra("FROM_CLASS_NAME", getActivity().getClass().getName());
                    intent.putExtra("GOODS_URL", mData.get(position).getGoodsUrl().toString());
                    intent.putExtra("GOODS_RID", mData.get(position).RID);
                    intent.putExtra("GOODS_ID", mData.get(position).GOODSID.toString());
                    intent.putExtra("CID", mData.get(position).ID);
                    intent.putExtra("PAGE_TITLE", mData.get(position).TITLE);
                    startActivity(intent);
                } else {
                    Intent goodsListIntent = new Intent(getActivity(), GoodsList.class);
                    goodsListIntent.putExtra("PAGE_TITLE", mData.get(position).TITLE);
                    goodsListIntent.putExtra("CID", mData.get(position).ID);
                    startActivity(goodsListIntent);
                }
            }
        }

        @Override
        public void displayImage(String imageURL, ImageView imageView) {
            ImageLoader.getInstance().displayImage(imageURL, imageView, imageOptions);// 此处本人使用了ImageLoader对图片进行加装！
        }
    };

    //跳转接口实现
    class AlertGotoActivity implements DialogAlert.OnAlertDialogListener {
        @Override
        public void GotoActivity() {
            HashMap<String, String> extras = new HashMap<String, String>();
            extras.put("IsFromRegister", "false");
            extras.put(COMMUNITY_ID, UserInformation.getUserInfo().getCommunityId());
            extras.put(COMMUNITY_NAME, UserInformation.getUserInfo().CommuntiyName);
            try {
                gotoActivityAndFinish(BindingHouse.class.getName(), extras);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    //绘制首页
    private void setHomeAreaData() {
        mImageUrl.clear();
        mData.clear();
        mAppHomePage.removeAllViews();

        if (mAppHomePageArea != null) {

            for (APPHomePage_Area areaData : mAppHomePageArea) {

                for (APPHomePage_Row rowData : areaData.APPHomePage_Row) {

                    mData = rowData.getAPPHomePage_Column();

                    if (rowData.getIsSlide().equals("true")) {    //采用幻灯片的,可以轮播播放

                        for (APPHomePage_Column columnData : rowData.APPHomePage_Column) {
                            mImageUrl.add(Services.getImageUrl(columnData.getIMGID()));
                        }

                        LinearLayout linearLayout = (LinearLayout) currentHomePageLayout.inflate(R.layout.ly_image_cycle, null);

                        Integer screenWidth = Utility.getScreenWidthforPX(getActivity());   //设置幻灯片的尺寸
                        Integer rowHeight = Float.valueOf(screenWidth * rowData.getRowHeightBI() / 100.00F).intValue();
                        LayoutParams row_lp = new LayoutParams(screenWidth, rowHeight);
                        row_lp.setMargins(0, 0, 0, Utility.dip2px(getActivity(), 1.0f));
                        linearLayout.setLayoutParams(row_lp);
                        mAdView = (ImageCycleView) linearLayout.findViewById(R.id.ad_view);
                        mAdView.setImageResources(mImageUrl, mAdCycleViewListener, mData);
                        mAppHomePage.addView(linearLayout);

                    } else if (rowData.getIsSlide().equals("false")) {     //不采用幻灯片的，初始化区域、行、列
                        //初始化行，给行填充数据，并添加到mAppHomePage
                        LinearLayout ll_row = initRow(rowData.APPHomePage_Column.size(), Float.parseFloat(rowData.HEIGHTBI));

                        for (final APPHomePage_Column column : rowData.APPHomePage_Column) {
                            LinearLayout ll_column = initColumn(rowData.APPHomePage_Column.size(), column);
                            if ((rowData.APPHomePage_Column.size() - 1) == rowData.APPHomePage_Column.indexOf(column)) {
                                LayoutParams column_lp = (LayoutParams) ll_column.getLayoutParams();
                                column_lp.setMargins(0, 0, 0, 0);
                                ll_column.setLayoutParams(column_lp);
                            }

                            ll_row.addView(ll_column);
                            ll_column.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (column.TYPES == 3) {
                                        Intent intent = new Intent(getActivity(), Browser.class);
                                        intent.putExtra("PAGE_URL", column.URL);
                                        intent.putExtra("PAGE_TITLE", column.TITLE);
                                        intent.putExtra("FROM_CLASS_NAME", getActivity().getClass().getName());
                                        startActivity(intent);
                                    } else if (column.TYPES == 4) {
                                        Intent intent = new Intent(getActivity(), Goods_Details.class);
                                        intent.putExtra("FROM_CLASS_NAME", getActivity().getClass().getName());
                                        intent.putExtra("GOODS_URL", column.GoodsUrl.toString());
                                        intent.putExtra("GOODS_RID", column.RID);
                                        intent.putExtra("GOODS_ID", column.GOODSID.toString());
                                        intent.putExtra("CID", column.ID);
                                        intent.putExtra("PAGE_TITLE", column.TITLE);
                                        startActivity(intent);
                                    } else {
                                        Intent goodsListIntent = new Intent(getActivity(), GoodsList.class);
                                        goodsListIntent.putExtra("PAGE_TITLE", column.TITLE);
                                        goodsListIntent.putExtra("CID", column.ID);
                                        startActivity(goodsListIntent);
                                    }
                                }
                            });
                        }
                        mAppHomePage.addView(ll_row);
                    }
                }
            }
        }
    }

    //首页区域，初始化区域
    private LinearLayout initArea(Integer rowCount, Float rowHeightBI) {
        Integer screenWidth = getScreenWidthforPX(getActivity());
        LinearLayout ll_area = new LinearLayout(getActivity());
        LayoutParams area_lp = new LayoutParams(
                screenWidth, Float.valueOf(screenWidth * rowHeightBI / 100.f).intValue());
        area_lp.setMargins(0, 20, 0, 0);
        ll_area.setLayoutParams(area_lp);
        ll_area.setPadding(0, 1, 0, 1);
        ll_area.setOrientation(LinearLayout.VERTICAL);
        ll_area.setBackgroundColor(getActivity().getResources().getColor(R.color.gray_light_2));
        return ll_area;
    }

    //首页区域设置，初始化行
    private LinearLayout initRow(Integer columnCount, Float heightBI) {
        Integer screenWidth = getScreenWidthforPX(getActivity());
        Integer rowHeight = Float.valueOf(screenWidth * heightBI / 100.00F).intValue();
        LinearLayout ll_row = new LinearLayout(getActivity());
        LayoutParams row_lp = new LayoutParams(screenWidth, rowHeight);
        row_lp.setMargins(0, 0, 0, Utility.dip2px(getActivity(), 1.0f));
        ll_row.setLayoutParams(row_lp);
        if (columnCount.equals(1)) {
            ll_row.setOrientation(LinearLayout.VERTICAL);
        } else {
            ll_row.setOrientation(LinearLayout.HORIZONTAL);
        }
        return ll_row;
    }

    //首页区域，初始化列
    private LinearLayout initColumn(Integer columnCount, APPHomePage_Column column) {
        Integer screenWidth = getScreenWidthforPX(getActivity());
        LinearLayout ll_column = new LinearLayout(getActivity());
        LayoutParams column_lp = new LayoutParams(screenWidth / columnCount, LayoutParams.MATCH_PARENT);
        column_lp.setMargins(0, 0, Utility.dip2px(getActivity(), 1.0f), 0);
        ll_column.setLayoutParams(column_lp);
        initContent(ll_column, screenWidth / columnCount, column);
        ll_column.setPadding(12, 12, 12, 12);
        ll_column.setBackgroundColor(Color.WHITE);
        return ll_column;
    }

    //首页区域，内容初始化
    private void initContent(LinearLayout ll_column, Integer column_width, APPHomePage_Column column) {

        title = new TextView(getActivity());
        description = new TextView(getActivity());

        title.setVisibility(View.VISIBLE);
        description.setVisibility(View.VISIBLE);
        //标题
        title.setText(column.TITLE);
        title.setTextColor(Color.parseColor(column.TITLECOLOR));
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0f);

        //描述
        description.setText(column.DESCRIPTION);
        description.setTextColor(Color.parseColor(column.DESCRIPTIONCOLOR));
        description.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.0f);

        //图片
        image = new ImageView(getActivity());
        Integer imageWidth;
        if (column.SHOWTITLE) {
            imageWidth = Double.valueOf(column_width * column.ImgWidthPro / 100.0f).intValue();
            title.setVisibility(View.VISIBLE);
            description.setVisibility(View.VISIBLE);
        } else {
            imageWidth = column_width;
            title.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
        }

        LayoutParams image_lp = new LayoutParams(imageWidth, Double.valueOf(imageWidth * column.ImgHeightPro).intValue());

        image.setLayoutParams(image_lp);
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageLoader.getInstance().displayImage(services.getImageUrl(column.IMGID), image,
                imageOptions);
        if (column.ImgPosition == 0 || column.ImgPosition == 2) {
            ll_column.setOrientation(LinearLayout.VERTICAL);
            if (column.ImgPosition == 0) {
                ll_column.addView(image);
                ll_column.addView(title);
                ll_column.addView(description);
            } else {
                ll_column.addView(title);
                ll_column.addView(description);
                ll_column.addView(image);
            }
        } else {
            ll_column.setOrientation(LinearLayout.HORIZONTAL);
            if (column.SHOWTITLE) {
                LinearLayout ll_column1 = new LinearLayout(getActivity());
                LayoutParams column1_lp = new LayoutParams(
                        Double.valueOf(column_width * column.ImgWidthPro / 100.0f).intValue(), LayoutParams.MATCH_PARENT);
                ll_column1.setLayoutParams(column1_lp);
                ll_column1.setOrientation(LinearLayout.HORIZONTAL);
                ll_column1.addView(image);
                LinearLayout ll_column2 = new LinearLayout(getActivity());
                LayoutParams column2_lp = new LayoutParams(
                        Double.valueOf(column_width * (1 - column.ImgWidthPro / 100.0f)).intValue(), LayoutParams.MATCH_PARENT);
                ll_column2.setLayoutParams(column2_lp);
                ll_column2.setOrientation(LinearLayout.VERTICAL);
                ll_column2.addView(title);
                ll_column2.addView(description);
                if (column.ImgPosition == 1) {
                    ll_column.addView(ll_column2);
                    ll_column.addView(ll_column1);
                } else {
                    ll_column.addView(ll_column1);
                    ll_column.addView(ll_column2);
                }
            } else {
                LinearLayout ll_column1 = new LinearLayout(getActivity());
                LayoutParams column1_lp = new LayoutParams(
                        column_width, LayoutParams.MATCH_PARENT);
                ll_column1.setLayoutParams(column1_lp);
                ll_column1.setOrientation(LinearLayout.HORIZONTAL);
                ll_column1.addView(image);
                ll_column.addView(ll_column1);
            }
        }
    }


    //开门超时
    Handler handlerOpenDoorTimer = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 111) {
                Log.e("aaa", "开门超时!");

                openDoorDialog.setType(2);

                if (currentDevice != null) {
                    blueLockPub.oneKeyCloseDevice(currentDevice, currentDevice.getDeviceId(), currentDevice.getDevicePsw());
                    blueLockPub.oneKeyDisconnectDevice(currentDevice);
                }
            }
        }
    };

    //摇摇动作
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 123:
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (openDoorDialog == null || (openDoorDialog != null && !openDoorDialog.isShowing())) {
                                openClick();
                            }
                        }
                    }, 2000);
                    break;
            }
        }
    };

    //添加开门日志返回
    Handler handlerEGlog = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    //获取钥匙串返回
    Handler handerGetKeyChain = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            closeProgressDialog();
            //清空缓存钥匙串、房屋信息
            KeyCache.saveKey(null, UserInformation.getUserInfo().getCommuntiyName() + "  " + UserInformation.getUserInfo().getHouseName());
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    keyChain = (List<KeyChain>) msg.obj;

                    for (KeyChain key : keyChain) {
                        keyChainMap.put(key.getId(), key);
                    }

                    //缓存钥匙串、房屋信息
                    KeyCache.saveKey(keyChain, UserInformation.getUserInfo().getCommuntiyName() + "  " + UserInformation.getUserInfo().getHouseName());
                    if (openDoorBykeyChain) { //如果门，需要开启扫描，否则只是获取钥匙串并保存
                        initBlueBooth();
                        blueStartScan();
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    if (openDoorBykeyChain) { //如果是用于开门，则显示错误提示，否则只是获取钥匙串
                        openDoorDialog.setKeyIsNullType(true);
                    }
                    break;
            }
        }
    };


    //获取周边小店返回
    Handler handlerCommunityShop = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    CommunityShopId communityShopId = (CommunityShopId) msg.obj;
                    if (communityShopId == null) {
                        Intent intent = new Intent(getActivity(), Browser.class);
                        String url = "http://b.goldwg.com/GoodsShop/investment";
                        intent.putExtra("PAGE_URL", url);
                        intent.putExtra("PAGE_TITLE", "");
                        intent.putExtra("PAGE_URL_ORGIN", url);
                        intent.putExtra("PAGE_TITLE_ORGIN", "社区小店");
                        intent.putExtra("FROM_CLASS_NAME", getActivity().getClass().getName());
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                    } else {
                        Intent intent1 = new Intent(getActivity(), CommunityShops.class);
                        intent1.putExtra("communityShopId", communityShopId);
                        startActivity(intent1);
                        getActivity().overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    //是否开通门禁返回
    Handler handlerCheckOpenEntrance = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:  //开通门禁
                    openEntranceState = true;
                    if (!TextUtils.isEmpty(UserInformation.getUserInfo().PropertyId)) {
                        bt_open_door.setVisibility(View.VISIBLE);
                    } else {
                        bt_open_door.setVisibility(View.GONE);
                        setSensorManager(false);
                    }
                    break;
                case BaseService.DATA_SUCCESS_OTHER: //未开通门禁
                    setSensorManager(false);
                    openEntranceState = false;
                    sensorManager.unregisterListener(sensorEventListener);
                    bt_open_door.setVisibility(View.GONE);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    //物业欠费返回
    Handler handlerGetArrageAmount = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    if (TextUtils.isEmpty(msg.obj.toString()) || msg.obj.equals("0") || msg.obj.equals("0.0") || msg.obj.equals("0.00")) {
                        feeArrearage = "";
                    } else {
                        feeArrearage = msg.obj.toString();
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    break;
            }
        }
    };


    //判断当前用户和当前房屋是否通过验证
    Handler handlerGetApprove = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:  //审核通过
                    approvePass = true;
                    break;
                case BaseService.DATA_SUCCESS_OTHER:  //审核未通过
                    CustomAlertDialog dialog2 = new CustomAlertDialog(getActivity(), false, getResources().getString(R.string.dialog_title), getResources().getString(R.string.dialog_verify));
                    dialog2.show();
                    dialog2.setDialogCallback(dialogcallback);

                    setSensorManager(false);
                    approvePass = false;
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    approvePass = false;
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    //获取用户最新信息返回
    Handler handlerSetCurrentInforamtion = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

    //货物商品返回
    Handler handlerGetGoodsData = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mRefreshableView.onRefreshComplete();
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    if (msg.obj != null) {
                        goods.addAll((List<Goods>) msg.obj);
                        mAdapter.notifyDataSetChanged();
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };


    //获取首页返回
    Handler handlerGetHomePageArea = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            splash_iv.setVisibility(View.GONE);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    if (msg.obj != null) {
                        mAppHomePageArea = (List<APPHomePage_Area>) msg.obj;
                        setHomeAreaData();
                    }
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    showToast(msg.obj.toString());
                    break;
            }
        }
    };

    //获取小红点推送
    Handler handlerGetRedPointPush = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BaseService.DATA_SUCCESS:
                    List<Type> data = (List<Type>) msg.obj;
                    Msg push = PushMessage.setMsg(data);
                    PushMessage.setPushInformation(push);
                    showRedDot();
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    List<Type> data2 = new ArrayList<Type>();
                    Msg push2 = PushMessage.setMsg(data2);
                    PushMessage.setPushInformation(push2);
                    showRedDot();
                    break;
            }
        }
    };



    @Override
    public void onBottom() {

    }

    @Override
    public void onTop() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        approvePass = false;
        if (sensorManager != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }

        if (openDoorDialog != null && openDoorDialog.isShowing()) {
            openDoorDialog.hide();
            openDoorDialog = null;
        }
        TCAgent.onPageEnd(getActivity(), "首页-首页" + this.getClass().getSimpleName());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (soundPool != null) {
            soundPool.release();
        }

    }

    //播放开门音效
    public static void playSound(int rawId, Context context) {
        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入音频的数量
            builder.setMaxStreams(1);
            //AudioAttributes是一个封装音频各种属性的类
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            builder.setAudioAttributes(attrBuilder.build());
            soundPool = builder.build();
        } else {
            //第一个参数是可以支持的声音数量，第二个是声音类型，第三个是声音品质
            soundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);
        }
        //第一个参数Context,第二个参数资源Id，第三个参数优先级
        soundPool.load(context, rawId, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(1, 1, 1, 0, 0, 1);
            }
        });
        //第一个参数id，即传入池中的顺序，第二个和第三个参数为左右声道，第四个参数为优先级，第五个是否循环播放，0不循环，-1循环
        //最后一个参数播放比率，范围0.5到2，通常为1表示正常播放
//        soundPool.play(1, 1, 1, 0, 0, 1);
        //回收Pool中的资源
        //soundPool.release();

    }

    //隐藏小红点
    private void hintRed(int type) {
        //消除小红点
        Msg msg = PushMessage.getPushInfo();
        msg.REPAIRS = false;
        PushMessage.setPushInformation(msg);
        homeService.deleteRedPoint(type, handlerDeleteRed);
    }


}





