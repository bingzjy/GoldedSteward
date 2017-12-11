package com.ldnet.activity.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.activity.find.FreaMarket_Create;
import com.ldnet.activity.find.Weekend_Create;
import com.ldnet.activity.home.HouseRentUpdate;
import com.ldnet.activity.home.HouseRent_Create;
import com.ldnet.activity.home.Property_Complain_Create;
import com.ldnet.activity.home.Property_Repair_Create;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.GSApplication;
import com.ldnet.utility.ImageDisplayer;
import com.ldnet.utility.IntentConstants;
import com.ldnet.utility.Services;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ImageZoomActivity extends BaseActionBarActivity {

    private ViewPager pager;
    private MyPageAdapter adapter;
    private int currentPosition;
    private List<ImageItem> mDataList = new ArrayList<ImageItem>();
    private String classname = "";

    private RelativeLayout photo_relativeLayout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_zoom);
        classname = getIntent().getStringExtra("classname");
        photo_relativeLayout = (RelativeLayout) findViewById(R.id.photo_relativeLayout);
        photo_relativeLayout.setBackgroundColor(0x70000000);

        initData();

        Button photo_bt_exit = (Button) findViewById(R.id.photo_bt_exit);
        photo_bt_exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        Button photo_bt_del = (Button) findViewById(R.id.photo_bt_del);
        photo_bt_del.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mDataList.size() == 1) {
                    removeImgs();
                    finish();
                } else {
                    removeImg(currentPosition);
                    pager.removeAllViews();
                    adapter.removeView(currentPosition);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setOnPageChangeListener(pageChangeListener);

        adapter = new MyPageAdapter(mDataList);
        pager.setAdapter(adapter);
        pager.setCurrentItem(currentPosition);
    }

    private void initData() {
        currentPosition = getIntent().getIntExtra(
                IntentConstants.EXTRA_CURRENT_IMG_POSITION, 0);
        if (classname.equals("FreaMarket_Create")) {
            mDataList = FreaMarket_Create.mDataList;
        } else if (classname.equals("Weekend_Create")) {
            mDataList = Weekend_Create.mDataList;
        }else if (classname.equals("HouseRent_Create")) {
            mDataList = HouseRent_Create.mDataList;
        }else if (classname.equals("HouseRentUpdate")) {
            mDataList = HouseRentUpdate.mDataList;
        }else if(classname.equals("Property_Complain_Create")){
            mDataList = Property_Complain_Create.mDataList;
        }else if(classname.equals("Property_Repair_Create")){
            mDataList = Property_Repair_Create.mDataList;
        }

    }

    private void removeImgs() {
        mDataList.clear();
        if (classname.equals("FreaMarket_Create")) {
            FreaMarket_Create.mImageIds = "";
        } else if (classname.equals("Weekend_Create")) {
            Weekend_Create.mImageIds = "";
        }else if (classname.equals("HouseRent_Create")) {
            HouseRent_Create.mImageIds = "";
        }else if (classname.equals("HouseRentUpdate")) {
            HouseRentUpdate.mImageIds = "";
        }else if (classname.equals("Property_Complain_Create")) {
            Property_Complain_Create.mImageIds = "";
        }else if (classname.equals("Property_Repair_Create")) {
            Property_Repair_Create.mImageIds = "";
        }
    }

    private void removeImg(int location) {
        if (location + 1 <= mDataList.size()) {
            mDataList.remove(location);
            List<String> list = new ArrayList<>();
            String[] strs = new String[0];
            if (classname.equals("FreaMarket_Create")) {
                strs = FreaMarket_Create.mImageIds.split(",");
                FreaMarket_Create.mImageIds = "";
            } else if (classname.equals("Weekend_Create")) {
                strs = Weekend_Create.mImageIds.split(",");
                Weekend_Create.mImageIds = "";
            }else if (classname.equals("HouseRent_Create")) {
                strs = HouseRent_Create.mImageIds.split(",");
                HouseRent_Create.mImageIds = "";
            }else if (classname.equals("HouseRentUpdate")) {
                strs = HouseRentUpdate.mImageIds.split(",");
                HouseRentUpdate.mImageIds = "";
            }else if (classname.equals("Property_Complain_Create")) {
                strs = Property_Complain_Create.mImageIds.split(",");
                Property_Complain_Create.mImageIds = "";
            }else if (classname.equals("Property_Repair_Create")) {
                strs = Property_Repair_Create.mImageIds.split(",");
                Property_Repair_Create.mImageIds = "";
            }
            for (int i = 0; i < strs.length; i++) {
                list.add(strs[i]);
            }
            list.remove(location);
            if (list.size() == 1) {
                if (classname.equals("FreaMarket_Create")) {
                    FreaMarket_Create.mImageIds = list.get(0);
                } else if (classname.equals("Weekend_Create")) {
                    Weekend_Create.mImageIds = list.get(0);
                }else if (classname.equals("HouseRent_Create")) {
                    HouseRent_Create.mImageIds = list.get(0);
                }else if (classname.equals("HouseRentUpdate")) {
                    HouseRentUpdate.mImageIds = list.get(0);
                }else if (classname.equals("Property_Complain_Create")) {
                    Property_Complain_Create.mImageIds = list.get(0);
                }else if (classname.equals("Property_Repair_Create")) {
                    Property_Repair_Create.mImageIds = list.get(0);
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    if (!"".equals(list.get(i))) {
                        if (classname.equals("FreaMarket_Create")) {
                            FreaMarket_Create.mImageIds += "," + list.get(i);
                        } else if (classname.equals("Weekend_Create")) {
                            Weekend_Create.mImageIds += "," + list.get(i);
                        }else if (classname.equals("HouseRent_Create")) {
                            HouseRent_Create.mImageIds += "," + list.get(i);
                        } else if (classname.equals("HouseRentUpdate")) {
                            HouseRentUpdate.mImageIds += "," + list.get(i);
                        }else if (classname.equals("Property_Complain_Create")) {
                            Property_Complain_Create.mImageIds += "," + list.get(i);
                        }else if (classname.equals("Property_Repair_Create")) {
                            Property_Repair_Create.mImageIds += "," + list.get(i);
                        }
                    }
                }
            }
            if (classname.equals("FreaMarket_Create")) {
                if (String.valueOf(FreaMarket_Create.mImageIds.charAt(FreaMarket_Create.mImageIds.length() - 1)).equals(",")) {
                    FreaMarket_Create.mImageIds = FreaMarket_Create.mImageIds.substring(0, FreaMarket_Create.mImageIds.length());
                }
                if (String.valueOf(FreaMarket_Create.mImageIds.charAt(0)).equals(",")) {
                    FreaMarket_Create.mImageIds = FreaMarket_Create.mImageIds.substring(1, FreaMarket_Create.mImageIds.length());
                }
            } else if (classname.equals("Weekend_Create")) {
                if (String.valueOf(Weekend_Create.mImageIds.charAt(Weekend_Create.mImageIds.length() - 1)).equals(",")) {
                    Weekend_Create.mImageIds = Weekend_Create.mImageIds.substring(0, Weekend_Create.mImageIds.length());
                }
                if (String.valueOf(Weekend_Create.mImageIds.charAt(0)).equals(",")) {
                    Weekend_Create.mImageIds = Weekend_Create.mImageIds.substring(1, Weekend_Create.mImageIds.length());
                }
            }else if (classname.equals("HouseRent_Create")) {
                if (String.valueOf(HouseRent_Create.mImageIds.charAt(HouseRent_Create.mImageIds.length() - 1)).equals(",")) {
                    HouseRent_Create.mImageIds = HouseRent_Create.mImageIds.substring(0, HouseRent_Create.mImageIds.length());
                }
                if (String.valueOf(HouseRent_Create.mImageIds.charAt(0)).equals(",")) {
                    HouseRent_Create.mImageIds = HouseRent_Create.mImageIds.substring(1, HouseRent_Create.mImageIds.length());
                }
            }else if (classname.equals("HouseRentUpdate")) {
                if (String.valueOf(HouseRentUpdate.mImageIds.charAt(HouseRentUpdate.mImageIds.length() - 1)).equals(",")) {
                    HouseRentUpdate.mImageIds = HouseRentUpdate.mImageIds.substring(0, HouseRentUpdate.mImageIds.length());
                }
                if (String.valueOf(HouseRentUpdate.mImageIds.charAt(0)).equals(",")) {
                    HouseRentUpdate.mImageIds = HouseRentUpdate.mImageIds.substring(1, HouseRentUpdate.mImageIds.length());
                }
            }else if (classname.equals("Property_Complain_Create")) {
                if (String.valueOf(Property_Complain_Create.mImageIds.charAt(Property_Complain_Create.mImageIds.length() - 1)).equals(",")) {
                    Property_Complain_Create.mImageIds = Property_Complain_Create.mImageIds.substring(0, Property_Complain_Create.mImageIds.length());
                }
                if (String.valueOf(Property_Complain_Create.mImageIds.charAt(0)).equals(",")) {
                    Property_Complain_Create.mImageIds = Property_Complain_Create.mImageIds.substring(1, Property_Complain_Create.mImageIds.length());
                }
            }else if (classname.equals("Property_Repair_Create")) {
                if (String.valueOf(Property_Repair_Create.mImageIds.charAt(Property_Repair_Create.mImageIds.length() - 1)).equals(",")) {
                    Property_Repair_Create.mImageIds = Property_Repair_Create.mImageIds.substring(0, Property_Repair_Create.mImageIds.length());
                }
                if (String.valueOf(Property_Repair_Create.mImageIds.charAt(0)).equals(",")) {
                    Property_Repair_Create.mImageIds = Property_Repair_Create.mImageIds.substring(1, Property_Repair_Create.mImageIds.length());
                }
            }
        }
    }

    private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

        public void onPageSelected(int arg0) {
            currentPosition = arg0;
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageScrollStateChanged(int arg0) {

        }
    };

    class MyPageAdapter extends PagerAdapter {
        private List<ImageItem> dataList = new ArrayList<ImageItem>();
        private ArrayList<ImageView> mViews = new ArrayList<ImageView>();

        public MyPageAdapter(List<ImageItem> dataList) {
            this.dataList = dataList;
            int size = dataList.size();
            for (int i = 0; i != size; i++) {
                ImageView iv = new ImageView(ImageZoomActivity.this);
                if( dataList.get(i).sourcePath.contains("/")){
                    ImageDisplayer.getInstance(ImageZoomActivity.this).displayBmp(
                            iv, null, dataList.get(i).sourcePath, false);
                }else{
                    ImageLoader.getInstance().displayImage(Services.getImageUrl(dataList.get(i).sourcePath), iv, imageOptions);
                }

                iv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT));
                mViews.add(iv);
            }
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public Object instantiateItem(View arg0, int arg1) {
            ImageView iv = mViews.get(arg1);
            ((ViewPager) arg0).addView(iv);
            return iv;
        }

        public void destroyItem(View arg0, int arg1, Object arg2) {
            if (mViews.size() >= arg1 + 1) {
                ((ViewPager) arg0).removeView(mViews.get(arg1));
            }
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        public void removeView(int position) {
            if (position + 1 <= mViews.size()) {
                mViews.remove(position);
            }
        }

    }
}