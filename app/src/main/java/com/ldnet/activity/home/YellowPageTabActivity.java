package com.ldnet.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.alipay.sdk.exception.NetErrorException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseActionBarFragmentActivity;
import com.ldnet.entities.Goods;
import com.ldnet.entities.YellowPageSort;
import com.ldnet.goldensteward.R;
import com.ldnet.service.AcountService;
import com.ldnet.utility.*;
import com.tendcloud.tenddata.TCAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by zxs on 2016/3/30.
 * 黄页
 */
public class YellowPageTabActivity extends BaseActionBarFragmentActivity implements View.OnClickListener {
    private String mYellowPageSortID;
    private String mYellowPageSortTitle;
    private List<YellowPageSort> mYellowPageSorts;
    private Services service;
    private TextView tv_page_title;
    private ImageView btn_back;

    // 黄页的标签
    private PagerSlidingTabStrip mYellowPageTabs;
    private ViewPager mYellowPagePager;
    private Integer mCurrentIndex = 0;

    private final String URL_YELLOW=Services.mHost+"API/YellowPages/GetSubSortLstById";
    private AcountService acountService;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //布局
        setContentView(R.layout.activity_yellowpage_tab);
        //获取传递的参数
        mYellowPageSortID = getIntent().getStringExtra("YELLOW_PAGE_SORT_ID");
        mYellowPageSortTitle = getIntent().getStringExtra("YELLOW_PAGE_SORT_NAME");

        //初始化
        service = new Services();
        mYellowPagePager = (ViewPager) findViewById(R.id.pager);
        mYellowPageTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        //标题
        tv_page_title = (TextView) findViewById(R.id.tv_page_title);
        tv_page_title.setText(mYellowPageSortTitle);
        btn_back = (ImageView) findViewById(R.id.btn_back);
        //初始化分类
        getYellowPageSortById(mYellowPageSortID);
        Utility.setTabsValue(mYellowPageTabs,this);

        acountService=new AcountService(this);
        acountService.setIntegralTip(new Handler(),URL_YELLOW);
    }


    //根据分类id获取子分类
    public void getYellowPageSortById(String id) {
        String url = Services.mHost + "API/YellowPages/GetSubSortLstById/" + id;
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
                .execute(new DataCallBack(this) {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        super.onError(call,e,i);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        super.onResponse(s, i);
                        Log.e("asdsdasd", "getYellowPageSortById:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            JSONObject jsonObject = new JSONObject(json.getString("Data"));
                            if (json.getBoolean("Status")) {
                                if (jsonObject.getBoolean("Valid")) {
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<YellowPageSort>>() {
                                    }.getType();
                                    mYellowPageSorts = gson.fromJson(jsonObject.getString("Obj"), listType);
                                    if (mYellowPageSorts != null) {
                                        mYellowPagePager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
                                            @Override
                                            public CharSequence getPageTitle(int position) {
                                                return mYellowPageSorts.get(position).Title;
                                            }

                                            @Override
                                            public int getCount() {
                                                return mYellowPageSorts == null ? 0 : mYellowPageSorts.size();
                                            }

                                            @Override
                                            public Object instantiateItem(ViewGroup container, int position) {
                                                return super.instantiateItem(container, position);
                                            }

                                            @Override
                                            public Fragment getItem(int position) {
                                                Bundle b = new Bundle();
                                                b.putString("titleId", mYellowPageSorts.get(position).Id);
                                                b.putString("titleKeywords", mYellowPageSorts.get(position).Keywords);
                                                b.putString("titleTypes", mYellowPageSorts.get(position).Types);
                                                b.putString("flag", getIntent().getStringExtra("flag"));
                                                return YellowPageFragmentContent.getInstance(b);
                                            }
                                        });
                                        mYellowPageTabs.setViewPager(mYellowPagePager);
                                        mYellowPagePager.setCurrentItem(mCurrentIndex);
                                        btn_back.setOnClickListener(YellowPageTabActivity.this);
                                    }
                                }
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right,R.anim.slide_out_to_left);
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "生活黄页-主页：" + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "生活黄页-主页：" + this.getClass().getSimpleName());
    }
}
