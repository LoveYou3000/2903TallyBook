package com.example.tallybook.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    /**
     * fragment列表
     */
    private List<Fragment> fragments;

    public SectionsPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
        super(fragmentManager);
        this.fragments = fragments;
    }

    /**
     * @param position 当前位置
     * @return androidx.fragment.app.Fragment
     * @Author MACHENIKE
     * @Description TODO 返回某一个fragment
     **/
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    /**
     * @return int
     * @Author MACHENIKE
     * @Description TODO 获取fragment列表的长度
     **/
    @Override
    public int getCount() {
        return fragments.size();
    }
}
