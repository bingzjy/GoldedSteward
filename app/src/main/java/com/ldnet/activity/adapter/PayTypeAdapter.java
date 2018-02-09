package com.ldnet.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ldnet.entities.PayTypeData;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lee on 2017/6/29.
 */
public class PayTypeAdapter extends BaseAdapter {

    public Context context;
    public LayoutInflater inflater;
    public List<PayTypeData> payTypeDataList = new ArrayList<PayTypeData>();
    public LinearLayout linearLayout;

    public PayTypeAdapter(Context context, List<PayTypeData> payTypeDataList) {
        this.context = context;
        this.payTypeDataList = payTypeDataList;
        inflater = LayoutInflater.from(context);
    }

    public PayTypeAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        payTypeDataList = new ArrayList<PayTypeData>();
        payTypeDataList.add(new PayTypeData("支付宝", "在线支付", R.drawable.alipay));
     //   payTypeDataList.add(new PayTypeData("微信支付", "在线支付", R.drawable.ic_weixin_logo));
     //   payTypeDataList.add(new PayTypeData("银联支付", "在线支付", R.drawable.unionpay));
    }

    @Override
    public int getCount() {
        return payTypeDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return payTypeDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_pop_pay_type, parent, false);
            holder.describe = (TextView) convertView.findViewById(R.id.tv_pay_type_describe);
            holder.title = (TextView) convertView.findViewById(R.id.tv_pay_type_title);
            holder.icon = (ImageView) convertView.findViewById(R.id.imgView_pay_type_icon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.icon.setImageResource(payTypeDataList.get(position).getImgResource());
        holder.describe.setText(payTypeDataList.get(position).getDescribe());
        holder.title.setText(payTypeDataList.get(position).getTitle());
        return convertView;
    }


    private class ViewHolder {
        public ImageView icon;
        public TextView title;
        public TextView describe;
    }
}
