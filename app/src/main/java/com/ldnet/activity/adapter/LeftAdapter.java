package com.ldnet.activity.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldnet.goldensteward.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangjinye
 * @name GoldedSteward2
 * @class name：com.ldnet.activity.adapter
 * @class describe
 * @time 2018/4/11 11:39
 * @change
 * @chang time
 * @class describe
 */

public class LeftAdapter extends BaseAdapter {

    List<String> data;
    LayoutInflater inflater;
    private int selectItem = 0;

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
        notifyDataSetChanged();
    }

    public LeftAdapter(Context context, List<String> allTypes) {
        this.data = allTypes;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return data!=null?data.size():0;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_left_shop_layout, parent);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mText.setText(data.get(position));

        if (selectItem==position){
            holder.mText.setTextColor(Color.parseColor("#4a4a4a"));
        }else{
            holder.mText.setTextColor(Color.parseColor("#9B9B9B"));
        }

        return convertView;
    }


    public static class ViewHolder {
        TextView mText;

        public ViewHolder(View itemView) {
            mText = (TextView) itemView.findViewById(R.id.tv_shop_type_name);
        }
    }


}
