package com.example.tallybook;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.tallybook.adapter.SectionsPagerAdapter;
import com.example.tallybook.common.NoSlideViewPager;
import com.example.tallybook.fragment.FragmentBudget;
import com.example.tallybook.fragment.FragmentCharge;
import com.example.tallybook.fragment.FragmentDetail;
import com.example.tallybook.fragment.FragmentMine;
import com.example.tallybook.fragment.FragmentTable;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页面
 *
 * @author MACHENIKE
 */
public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener, NoSlideViewPager.OnPageChangeListener {

    /**
     * 禁止左右滑动的ViewPager
     */
    private NoSlideViewPager viewPager;

    /**
     * 底部导航栏
     */
    private BottomNavigationBar bottomNavigationBar;

    /**
     * fragment列表
     */
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    /**
     * @return void
     * @Author MACHENIKE
     * @Description TODO 初始化
     **/
    private void init() {
        initView();
        initViewPager();
        initBottomNav();
    }

    /**
     * @return void
     * @Author MACHENIKE
     * @Description TODO 获取View上的控件
     **/
    private void initView() {
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationBar = findViewById(R.id.bottom_nav);
    }

    /**
     * @return void
     * @Author MACHENIKE
     * @Description TODO 初始化底部导航栏
     **/
    private void initBottomNav() {
        bottomNavigationBar.setTabSelectedListener(this);
        bottomNavigationBar.clearAll();
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_DEFAULT);
        bottomNavigationBar.setBarBackgroundColor(R.color.md_grey_500)
                .setActiveColor(R.color.colorPrimaryDark)
                .setInActiveColor(R.color.md_black_1000);
        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.detail_fill, "明细")
                .setInactiveIconResource(R.drawable.detail))
                .addItem(new BottomNavigationItem(R.drawable.table_fill, "图表")
                        .setInactiveIconResource(R.drawable.table))
                .addItem(new BottomNavigationItem(R.drawable.charge, "记账"))
                .addItem(new BottomNavigationItem(R.drawable.budget_fill, "预算")
                        .setInactiveIconResource(R.drawable.budget))
                .addItem(new BottomNavigationItem(R.drawable.mine_fill, "我的")
                        .setInactiveIconResource(R.drawable.mine))
                .setFirstSelectedPosition(0)
                .initialise();
    }

    /**
     * @return void
     * @Author MACHENIKE
     * @Description TODO 初始化ViewPager
     **/
    private void initViewPager() {
        fragments = new ArrayList<>();

        fragments.add(new FragmentDetail());
        fragments.add(new FragmentTable());
        fragments.add(new FragmentCharge());
        fragments.add(new FragmentBudget());
        fragments.add(new FragmentMine());

        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager(), fragments));
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(0);
    }

    /**
     * @param position 选择的导航栏位置,从0开始
     * @return void
     * @Author MACHENIKE
     * @Description TODO 更新底部导航栏
     **/
    @Override
    public void onTabSelected(int position) {
        viewPager.setCurrentItem(position);
    }

    /**
     * @param position 选择的ViewPager的位置,从0开始
     * @return void
     * @Author MACHENIKE
     * @Description TODO 更新ViewPager
     **/
    @Override
    public void onPageSelected(int position) {
        bottomNavigationBar.selectTab(position);
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
    public void onPageScrollStateChanged(int state) {

    }
}
