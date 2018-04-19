package com.ldnet.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ldnet.utility.ViewHolder;

import java.util.List;

public abstract class ListViewAdapter<T> extends BaseAdapter {

    protected Context mContext;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    protected int mLayoutId;
//	private int mStart,mEnd;
//	private static String[] urls;


    // 构造函数
    public ListViewAdapter(Context context, int layoutId, List<T> datas) {
        this.mContext = context;
        this.mLayoutId = layoutId;
        this.mInflater = LayoutInflater.from(context);
        this.mDatas = datas;
    }

    public ListViewAdapter(Context mContext, int mLayoutId) {
        this.mContext = mContext;
        this.mLayoutId = mLayoutId;
        this.mInflater = LayoutInflater.from(mContext);

    }

    //	@Override
//	public void onScrollStateChanged(AbsListView view, int scrollState) {
//	}
//
//	@Override
//	public void onScroll(AbsListView view, int firstVisibleItem,
//			int visibleItemCount, int totalItemCount) {
//	}

    // Count


    public void setDatas(List<T> datas){
        mDatas.clear();
        if(datas!=null&&datas.size()>0){
            mDatas.addAll(datas);
            notifyDataSetChanged();
        }
    }


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
