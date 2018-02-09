package com.ldnet.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.ldnet.entities.Fees;
import com.ldnet.entities.lstAPPFees;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.Arith;
import com.ldnet.utility.ViewHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex on 2015/9/22.
 */
public class FeeListViewAdapter extends BaseExpandableListAdapter {
    protected Context mContext;
    protected List<Fees> mDatas;
    protected LayoutInflater mInflater;
    protected int mGroupLayoutId;
    protected int mItemLayoutId;
    private TextView textView;
    private float allAmount;
    public Map<Integer, Boolean> isCheckMap = new HashMap<Integer, Boolean>();

    //构造函数
    public FeeListViewAdapter(Context context, List<Fees> datas, TextView view, float totalAmount) {
        mContext = context;
        mDatas = datas;
        textView = view;
        allAmount = totalAmount;
        mInflater = LayoutInflater.from(context);
        mGroupLayoutId = R.layout.item_fee_group_view;
        mItemLayoutId = R.layout.item_fee_view;
    }

    @Override
    public int getGroupCount() {
        return mDatas.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return mDatas.get(i).lstAPPFees.size();
    }

    @Override
    public Fees getGroup(int i) {
        return mDatas.get(i);
    }

    @Override
    public lstAPPFees getChild(int i, int index) {
        return mDatas.get(i).lstAPPFees.get(index);
    }

    @Override
    public long getGroupId(int groupPostion) {
        return groupPostion;
    }

    @Override
    public long getChildId(int groupPostion, int itemPostion) {
        return itemPostion;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpand, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mContext, convertView, parent, mGroupLayoutId, groupPosition);
        final Fees fees = getGroup(groupPosition);
        ((TextView) holder.getView(R.id.tv_date)).setText(fees.DateTime());
        ((TextView) holder.getView(R.id.tv_sum)).setText("￥" + String.valueOf(fees.Sum));
        TextView unpaid = holder.getView(R.id.tv_unpaid);
        CheckBox checkBox = holder.getView(R.id.cb_pay);
        if (!fees.isPaid()) {
            unpaid.setText("￥" + String.valueOf(fees.UnpaidSum()));
            unpaid.setTextColor(unpaid.getResources().getColor(R.color.red));

            //未付
            checkBox.setEnabled(true);
            if (fees.IsChecked != null && fees.IsChecked) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
            //CheckBox的点击事件
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((CheckBox) view).isChecked()) {
                        mDatas.get(groupPosition).IsChecked = true;
                        float add = mDatas.get(groupPosition).UnpaidSum().floatValue();
                        allAmount = Arith.add(allAmount, add);
                        textView.setText("总计:" + allAmount);
                    } else {
                        mDatas.get(groupPosition).IsChecked = false;
                        float sub = mDatas.get(groupPosition).UnpaidSum().floatValue();
                        allAmount = Arith.sub(allAmount, sub);
                        textView.setText("总计:" + allAmount);
                    }
                }
            });
        } else {
            checkBox.setVisibility(View.GONE);

            unpaid.setText("已缴清");
            unpaid.setTextColor(unpaid.getResources().getColor(R.color.green));
        }

        ImageView image = holder.getView(R.id.iv_icon);
        if (isExpand) {
            image.setImageResource(R.drawable.indicator_expand);
        } else {
            image.setImageResource(R.drawable.indicator_unexpand);
        }
        return holder.getConvertView();
    }

    public void configCheckMap(boolean bool) {
        for (int i = 0; i < mDatas.size(); i++) {
            isCheckMap.put(i, bool);
        }
    }

    @Override
    public View getChildView(int groupPosition, int itemPosition, boolean isExpand, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mContext, convertView, parent, mItemLayoutId, itemPosition);
        lstAPPFees fees = getChild(groupPosition, itemPosition);
        ((TextView) holder.getView(R.id.tv_title)).setText(fees.ItemTitle);
        ((TextView) holder.getView(R.id.tv_fee)).setText("￥" + String.valueOf(fees.Payable));
        return holder.getConvertView();
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

}
