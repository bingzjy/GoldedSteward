package com.ldnet.activity.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import com.ldnet.activity.find.InforFragmentContent;
import com.ldnet.entities.InformationType;

import java.util.List;

/**
 * @Title: MyPagerAdapter
 * @Package com.guxiuzhong.pagerslidingtabstrip.adapter
 * @Description:
 */
public class MyPagerAdapter extends FragmentPagerAdapter {

    private List<InformationType> titles;


    public MyPagerAdapter(FragmentManager fm, List<InformationType> list) {
        super(fm);
        this.titles = list;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position).Title;
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle b = new Bundle();
        b.putString("titleId", titles.get(position).ID);
        return InforFragmentContent.getInstance(b);
    }
}
