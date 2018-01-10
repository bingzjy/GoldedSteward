package com.ldnet.activity.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.ldnet.activity.me.PublishFragment;
import com.ldnet.entities.InfoBarType;

import java.util.List;

/**
 * @author zhangjinye
 * @name GoldedSteward2
 * @class name：com.ldnet.activity.adapter
 * @class describe
 * @time 2018/1/10 13:54
 * @change
 * @chang time
 * @class describe
 */

public class PublishPagerAdapter extends FragmentPagerAdapter {

    private List<InfoBarType> typesList;

    public PublishPagerAdapter(FragmentManager fm,List<InfoBarType> list) {
        super(fm);
        this.typesList=list;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return typesList.get(position).name;
    }

    @Override
    public int getCount() {
        return typesList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle=new Bundle();
        bundle.putInt("value",typesList.get(position).value);
        return PublishFragment.getInstance(bundle);
    }
}
