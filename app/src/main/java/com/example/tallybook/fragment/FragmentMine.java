package com.example.tallybook.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tallybook.Bean.Budget;
import com.example.tallybook.Bean.Detail;
import com.example.tallybook.Bean.Saving;
import com.example.tallybook.Bean.User;
import com.example.tallybook.R;
import com.example.tallybook.activity.PersonalInfo;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

/**
 * 我的页面
 *
 * @author MACHENIKE
 */
public class FragmentMine extends Fragment {

    /**
     * 当前用户
     */
    private User user;

    /**
     * 向下滑动刷新控件
     */
    private SwipeRefreshLayout mineSwipe;

    /**
     * 头像
     */
    private ImageView mineHead;

    /**
     * 用户名
     */
    private TextView mineName;

    /**
     * 记账天数
     */
    private TextView mineCountDay;

    /**
     * 记账笔数
     */
    private TextView mineCountDetail;

    /**
     * 本月预算金额
     */
    private TextView mineBudgetAmount;

    /**
     * 本月剩余金额
     */
    private TextView mineRemainAmount;

    /**
     * 存钱计划完成次数
     */
    private TextView mineSavingComplete;

    /**
     * 存钱计划剩余金额
     */
    private TextView mineSavingNeed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        setListeners();

        showData();

    }

    /**
     * @param msg 要显示的信息
     * @return void
     * @Author MACHENIKE
     * @Description TODO 显示Toast信息
     **/
    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * @param msg 要显示的信息
     * @param e   异常信息
     * @return void
     * @Author MACHENIKE
     * @Description TODO 显示Toast信息
     **/
    private void showToast(String msg, BmobException e) {
        Toast.makeText(getActivity(), msg + "\n" + e.getErrorCode() + "\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    /**
     * @return void
     * @Author MACHENIKE
     * @Description TODO 将数据展示到页面上
     **/
    private void showData() {

        mineName.setText(user.getUsername());

        BmobQuery<Detail> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("user", new BmobPointer(user));
        bmobQuery.findObjects(new FindListener<Detail>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void done(List<Detail> list, BmobException e) {
                if (e == null) {
                    //获取记账天数以及笔数
                    int dayNum = 1;
                    for (int i = 1; i < list.size(); i++) {
                        if (!list.get(i).getFullDate().equals(list.get(i - 1).getFullDate())) {
                            dayNum++;
                        }
                    }

                    mineCountDay.setText("已记账 " + dayNum + " 天");
                    mineCountDetail.setText("已记账 " + list.size() + " 笔");
                } else {
                    showToast("mine查询记账天数以及笔数失败", e);
                }
            }
        });

        BmobQuery<Budget> budgetBmobQuery = new BmobQuery<>();
        budgetBmobQuery.addWhereEqualTo("user", new BmobPointer(user));
        budgetBmobQuery.addWhereEqualTo("year", new Date().getYear() + 1900);
        budgetBmobQuery.addWhereEqualTo("month", new Date().getMonth() + 1);
        budgetBmobQuery.findObjects(new FindListener<Budget>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void done(List<Budget> list, BmobException e) {
                if (e == null) {
                    //获取预算
                    Budget budget = list.get(0);

                    mineBudgetAmount.setText(budget.getBudgetAmount() + "元");
                    mineRemainAmount.setText(budget.getRemainAmount() + "元");
                } else {
                    showToast("mine查询预算信息失败", e);
                }
            }
        });

        BmobQuery<Saving> savingCompleteTimeBmobQuery = new BmobQuery<>();
        savingCompleteTimeBmobQuery.addWhereEqualTo("user", new BmobPointer(user));
        savingCompleteTimeBmobQuery.addWhereEqualTo("savingStatus", true);
        savingCompleteTimeBmobQuery.count(Saving.class, new CountListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    //获取存钱完成次数
                    mineSavingComplete.setText(integer + "次");
                } else {
                    showToast("mine查询存钱完成次数失败", e);
                }
            }
        });

        BmobQuery<Saving> savingNeedBmonQuery = new BmobQuery<>();
        savingNeedBmonQuery.addWhereEqualTo("user", new BmobPointer(user));
        savingNeedBmonQuery.addWhereEqualTo("savingStatus", false);
        savingNeedBmonQuery.findObjects(new FindListener<Saving>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void done(List<Saving> list, BmobException e) {
                if (e == null) {
                    //获取剩余存钱金额
                    mineSavingNeed.setText(list.get(0).getSavingAmount() - list.get(0).getSavingAlready() + "元");
                } else {
                    showToast("mine查询剩余存钱金额失败", e);
                }
            }
        });

    }

    /**
     * @return void
     * @Author MACHENIKE
     * @Description TODO 各个控件设置监听器
     **/
    private void setListeners() {

        //头像点击  ->  跳转到个人信息页面
        mineHead.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), PersonalInfo.class));
        });

        //下拉刷新
        mineSwipe.setOnRefreshListener(() -> {
            mineSwipe.setRefreshing(false);
            showData();
        });

    }

    /**
     * @return void
     * @Author MACHENIKE
     * @Description TODO 获取控件并初始化
     **/
    private void initView() {
        user = BmobUser.getCurrentUser(User.class);

        mineSwipe = Objects.requireNonNull(getActivity()).findViewById(R.id.mine_swipe);

        mineHead = getActivity().findViewById(R.id.mine_head);
        mineName = getActivity().findViewById(R.id.mine_name);
        mineCountDay = getActivity().findViewById(R.id.mine_count_day);
        mineCountDetail = getActivity().findViewById(R.id.mine_count_detail);

        mineBudgetAmount = getActivity().findViewById(R.id.mine_budget_amount);
        mineRemainAmount = getActivity().findViewById(R.id.mine_remain_amount);

        mineSavingComplete = getActivity().findViewById(R.id.mine_saving_complete);
        mineSavingNeed = getActivity().findViewById(R.id.mine_saving_need);
    }
}
