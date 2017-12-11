package com.ldnet.activity.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.ldnet.activity.informationpublish.InfoShowContentFragment;
import com.ldnet.entities.InfoBarType;

import java.util.List;

/**
 * @Title: MyPagerAdapter
 * @Package com.guxiuzhong.pagerslidingtabstrip.adapter
 * @Description:
 */
public class InfoBarPagerAdapter extends FragmentPagerAdapter {

    private List<InfoBarType> list;
    private String bigType;


    public InfoBarPagerAdapter(FragmentManager fm, List<InfoBarType> list) {
        super(fm);
        this.list = list;
    }


    public InfoBarPagerAdapter(FragmentManager fm, List<InfoBarType> list, String bigType) {
        super(fm);
        this.list = list;
        this.bigType = bigType;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return list.get(position).name;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle b = new Bundle();
        b.putInt("value", list.get(position).value);
        return InfoShowContentFragment.getInstance(b);
    }


}
