package com.example.tallybook.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public SectionsPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
        super(fragmentManager);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
