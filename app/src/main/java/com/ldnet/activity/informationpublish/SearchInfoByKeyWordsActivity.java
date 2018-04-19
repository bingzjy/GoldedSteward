package com.ldnet.activity.informationpublish;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.InfoBarData;
import com.ldnet.goldensteward.R;
import com.ldnet.service.BaseService;
import com.ldnet.service.InfoBarService;
import com.ldnet.activity.adapter.ListViewAdapter;
import com.ldnet.activity.commen.Services;
import com.ldnet.utility.sharepreferencedata.UserInformation;
import com.ldnet.utility.ViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.List;

public class SearchInfoByKeyWordsActivity extends BaseActionBarActivity {

    private SearchView searchView;
    private ListView listView;
    private InfoBarService service;
    private ListViewAdapter<InfoBarData> adapter;
    private List<InfoBarData> dataList=new ArrayList<>();
    private TextView tvTitle,tvSearch,tvNull;
    private DisplayImageOptions imageOptions;
    private String aa = Services.timeFormat();
    private String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
    private String keyWords;
    private ImageView btnBack;
    private int position;
    private String need;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_info_bar);

        imageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_info)     //url爲空會显示该图片，自己放在drawable里面的
                .showImageOnFail(R.drawable.default_info)                //加载图片出现问题，会显示该图片
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .extraForDownloader(UserInformation.getUserInfo().UserPhone + "," + aa + "," + aa1)
                .build();
        service=new InfoBarService(SearchInfoByKeyWordsActivity.this);
        initView();
        initEvent();
    }


    public void initView(){
        position=getIntent().getIntExtra("ITEM",0);
        need=getIntent().getStringExtra("NEED");
        tvTitle=(TextView)findViewById(R.id.tv_page_title);
        tvTitle.setText("搜索");
        btnBack=(ImageView)findViewById(R.id.btn_back);
        tvSearch=(TextView)findViewById(R.id.tv_search);
        tvSearch.setVisibility(View.GONE);
        searchView=(SearchView)findViewById(R.id.searchview);
        listView=(ListView)findViewById(R.id.listview_search_list);
        tvNull=(TextView)findViewById(R.id.tv_search_null);
        tvNull.setVisibility(View.GONE);
    }

    public void initEvent(){
        btnBack.setOnClickListener(this);
        tvSearch.setOnClickListener(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    tvSearch.setVisibility(View.VISIBLE);
                }else{
                    tvSearch.setVisibility(View.GONE);
                }
                keyWords=newText;
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                dataList.clear();
                adapter.notifyDataSetChanged();
                return false;
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InfoBarData data=dataList.get(position);
                Intent intent=new Intent(SearchInfoByKeyWordsActivity.this,InfoPublishDetailActivity.class);
                intent.putExtra("INFO_ID",data.Id);
                intent.putExtra("SHARE_URL",data.url);
                intent.putExtra("FROM_CLASS",SearchInfoByKeyWordsActivity.class.getName());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_left,R.anim.slide_out_to_right);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(this, "邻里通搜索：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this, "邻里通搜索：" + this.getClass().getSimpleName());
    }



    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_back:
                Intent intent=new Intent(SearchInfoByKeyWordsActivity.this,CommunityInfoBarMainActivity.class);
                intent.putExtra("ITEM",position);
                intent.putExtra("NEED",need);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_search:
                if (!TextUtils.isEmpty(keyWords)){
                    service.getInfoList(CommunityInfoBarMainActivity.currentBigType,"-1",keyWords,"",handler);
                }else{
                    Toast.makeText(SearchInfoByKeyWordsActivity.this,"请输入要查询的关键字",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BaseService.DATA_SUCCESS:

                    listView.setVisibility(View.VISIBLE);
                    tvNull.setVisibility(View.GONE);
                    dataList=(List<InfoBarData>)msg.obj;

                    adapter=new ListViewAdapter<InfoBarData>(SearchInfoByKeyWordsActivity.this,R.layout.item_info_bar,dataList) {
                        @Override
                        public void convert(ViewHolder holder, InfoBarData infoBarData) {
                            holder.setText(R.id.tv_item_info_bar_title,infoBarData.Title);
                            holder.setText(R.id.tv_item_info_bar_date,infoBarData.Created);
                            ImageView imageViewCover=holder.getView(R.id.iv_item_info_bar_cover);
                            if (!TextUtils.isEmpty(infoBarData.Cover)){
                                imageViewCover.setImageResource(R.drawable.default_info);
                                ImageLoader.getInstance().displayImage(Services.getImageUrl(infoBarData.Cover), imageViewCover, imageOptions);
                            }else{
                                imageViewCover.setImageResource(R.drawable.default_info);
                            }
                        }
                    };
                    listView.setAdapter(adapter);
                    break;
                case BaseService.DATA_SUCCESS_OTHER:
                    tvNull.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                    break;
                case BaseService.DATA_FAILURE:
                case BaseService.DATA_REQUEST_ERROR:
                    Toast.makeText(SearchInfoByKeyWordsActivity.this,msg.obj.toString(),Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent=new Intent(SearchInfoByKeyWordsActivity.this,CommunityInfoBarMainActivity.class);
            intent.putExtra("ITEM",position);
            intent.putExtra("NEED",need);
            startActivity(intent);
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
