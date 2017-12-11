/*
 * Copyright 2017 lizhaotailang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ldnet.activity.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by zjy on 2017/9/29
 */

public class MainPagerAdapter extends FragmentPagerAdapter {

    private String[] titles;
    private final Context context;
    private List<Fragment> fragmentList;
    private FragmentManager fragmentManager;

    public MainPagerAdapter(FragmentManager fm, Context context, String[] titles, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentManager=fm;
        this.context = context;
        this.titles = titles;
        this.fragmentList = fragmentList;
    }


    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return fragmentList.get(0);
        } else if (position == 1) {
            return fragmentList.get(1);
        }
        return fragmentList.get(0);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

}
