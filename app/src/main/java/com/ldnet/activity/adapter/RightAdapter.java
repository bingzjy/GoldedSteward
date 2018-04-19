package com.ldnet.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * @author zhangjinye
 * @name GoldedSteward2
 * @class name：com.ldnet.activity.adapter
 * @class describe
 * @time 2018/4/11 11:58
 * @change
 * @chang time
 * @class describe
 */

public class RightAdapter extends BaseAdapter {

//    List<ParaseData.DataBean> data;
//    LayoutInflater inflater;
//    Context context;
//
//
//    public RightAdapter(Context context, List<ParaseData.DataBean> data) {
//        this.context=context;
//        inflater = LayoutInflater.from(context);
//        if (data != null) {
//            this.data=data;
//        }else {
//            this.data=new ArrayList<>();
//        }
//    }
//    public void addRes(List<ParaseData.DataBean> data){
//        if (data != null) {
//            this.data.clear();
//            this.data.addAll(data);
//            notifyDataSetChanged();
//        }
//    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
