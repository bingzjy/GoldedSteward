package com.ldnet.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ldnet.goldensteward.R;
import com.ldnet.activity.commen.Services;
import com.ldnet.utility.sharepreferencedata.UserInformation;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by lee on 2016/7/29.
 */
public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private List<String> timeBeen;
    String aa = Services.timeFormat();
    String aa1 = (int) ((Math.random() * 9 + 1) * 100000) + "";
    protected DisplayImageOptions imageOptions;

    public GridViewAdapter(Context context, List<String> timeBeen) {
        this.context = context;
        this.timeBeen = timeBeen;

        imageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_info)     //url爲空會显示该图片，自己放在drawable里面的
                .showImageOnFail(R.drawable.default_info)                //加载图片出现问题，会显示该图片
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .extraForDownloader(UserInformation.getUserInfo().UserPhone + "," + aa + "," + aa1)
                .build();
    }

    @Override
    public int getCount() {
        return timeBeen.size();
    }

    @Override
    public Object getItem(int position) {
        return timeBeen.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_gridview, null);
        }
        String timeBean = timeBeen.get(position);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_gridview);
        ImageLoader.getInstance().displayImage(Services.getImageUrl(timeBean),imageView, imageOptions);
        return convertView;
    }

}