package com.ldnet.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.ldnet.utility.ViewHolder;

import java.util.List;

public abstract class AddPopAdapter<T> extends BaseAdapter {

    protected Context mContext;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    protected int mLayoutId;

    // 构造函数
    public AddPopAdapter(Context context, int layoutId, List<T> datas) {
        this.mContext = context;
        this.mLayoutId = layoutId;
        this.mInflater = LayoutInflater.from(context);
        this.mDatas = datas;
    }

    // Count
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mDatas == null ? 0 : mDatas.size();
    }

    // Item
    @Override
    public T getItem(int position) {
        // TODO Auto-generated method stub
        return mDatas.get(position);
    }

    // ItemId
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    // View
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mContext, convertView, parent, mLayoutId, position);
        // 公布用户需要重写的方法
        convert(holder, getItem(position));
        return holder.getConvertView();
    }

    // 用户实现控件和数据绑定的方法
    public abstract void convert(ViewHolder holder, T t);


}
