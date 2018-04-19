package com.ldnet.activity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.ldnet.entities.Property;
import com.ldnet.goldensteward.R;
import com.ldnet.activity.commen.Services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TimeAdapter extends BaseAdapter {
    private Context context;
    private List<Property> list;

    public TimeAdapter(Context context, List<Property> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (list == null) {
            return null;
        }
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_time_line, parent, false);
            holder.date = (TextView) convertView
                    .findViewById(R.id.txt_date_time);
            holder.content = (TextView) convertView
                    .findViewById(R.id.txt_date_content);
            holder.tel = (TextView) convertView
                    .findViewById(R.id.txt_tel);
            holder.name = (TextView) convertView
                    .findViewById(R.id.txt_name);
            holder.tv_title = (TextView) convertView
                    .findViewById(R.id.tv_title);
            holder.flag = true;
            holder.title = (LinearLayout) convertView
                    .findViewById(R.id.rl_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // 时间轴竖线的layout
//		LayoutParams params = (LayoutParams) holder.line.getLayoutParams();
        // 第一条数据，肯定显示时间标题
        if (position == 0) {
            holder.title.setVisibility(View.VISIBLE);
            holder.tv_title.setText(list.get(position).getExplain());
            holder.date.setText(Services.subStr(list.get(position).getOperateDay()));
        } else { // 不是第一条数据
            // 本条数据和上一条数据的时间戳相同，时间标题不显示
            if (list.get(position).getOperateDay()
                    .equals(list.get(position - 1).getOperateDay())) {
                holder.title.setVisibility(View.GONE);
                holder.tv_title.setText(list.get(position - 1).getExplain());
            } else {
                // 本条数据和上一条的数据的时间戳不同的时候，显示数据
                holder.title.setVisibility(View.VISIBLE);
                holder.tv_title.setText(list.get(position).getExplain());
                holder.date.setText(Services.subStr(list.get(position).getOperateDay()));
            }
        }
        if (list.get(position).getOperateName() != null && !list.get(position).getOperateName().equals("")) {
            holder.content.setText(list.get(position).getOperateName());
        }else{
            holder.content.setText("");
        }
        if (list.get(position).getStaffTel() != null && !list.get(position).getStaffTel().equals("")) {
            holder.tel.setText(list.get(position).getStaffTel());
        }else{
            holder.tel.setText("");
        }
        if (list.get(position).getStaffName() != null && !list.get(position).getStaffName().equals("")) {
            holder.name.setText(list.get(position).getStaffName());
        }else{
            holder.name.setText("");
        }
        return convertView;
    }

    public static class ViewHolder {
        LinearLayout title;
        View line;
        TextView date;
        TextView content;
        TextView tel;
        TextView name;
        TextView tv_title;
        LinearLayout ll_ex;
        boolean flag;
    }

    @SuppressLint("SimpleDateFormat")
    public static String format(String format, String time) {
        String result = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HHmm");
        try {
            Date date = df.parse(time);
            SimpleDateFormat df1 = new SimpleDateFormat(format);
            result = df1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
