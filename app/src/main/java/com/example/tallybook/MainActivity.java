package com.example.tallybook;

import android.os.Bundle;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.tallybook.adapter.SectionsPagerAdapter;
import com.example.tallybook.common.NoSlideViewPager;
import com.example.tallybook.fragment.FragmentBudget;
import com.example.tallybook.fragment.FragmentCharge;
import com.example.tallybook.fragment.FragmentDetail;
import com.example.tallybook.fragment.FragmentMine;
import com.example.tallybook.fragment.FragmentTable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;

public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener, ViewPager.OnPageChangeListener {

    private NoSlideViewPager viewPager;
    private BottomNavigationBar bottomNavigationBar;

    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bmob.initialize(this, "4c0d8bc51d99076175282cb6010f0f85");

        initView();
        initViewPager();
        initBottomNav();
    }

    private void initView() {

        viewPager = findViewById(R.id.viewPager);
        bottomNavigationBar = findViewById(R.id.bottom_nav);

    }

    private void initBottomNav() {

        bottomNavigationBar.setTabSelectedListener(this);
        bottomNavigationBar.clearAll();
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_DEFAULT);
        bottomNavigationBar.setBarBackgroundColor(R.color.md_grey_500)
                           .setActiveColor(R.color.colorPrimaryDark)
                           .setInActiveColor(R.color.md_black_1000);
        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.detail_fill,"明细").setInactiveIconResource(R.drawable.detail))
                           .addItem(new BottomNavigationItem(R.drawable.table_fill,"图表").setInactiveIconResource(R.drawable.table))
                           .addItem(new BottomNavigationItem(R.drawable.charge,"记账"))
                           .addItem(new BottomNavigationItem(R.drawable.budget_fill,"预算").setInactiveIconResource(R.drawable.budget))
                           .addItem(new BottomNavigationItem(R.drawable.mine_fill,"我的").setInactiveIconResource(R.drawable.mine))
                           .setFirstSelectedPosition(0).initialise();

    }

    private void initViewPager() {

        fragments = new ArrayList<>();
        fragments.add(new FragmentDetail());
        fragments.add(new FragmentTable());
        fragments.add(new FragmentCharge());
        fragments.add(new FragmentBudget());
        fragments.add(new FragmentMine());

        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager(),fragments));
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(0);

    }

    @Override
    public void onTabSelected(int position) {
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        bottomNavigationBar.selectTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
