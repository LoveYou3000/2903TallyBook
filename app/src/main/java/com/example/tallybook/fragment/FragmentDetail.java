package com.example.tallybook.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tallybook.Bean.Detail;
import com.example.tallybook.Bean.User;
import com.example.tallybook.R;
import com.example.tallybook.adapter.DetailAdapter;
import com.example.tallybook.common.DateTimeDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.example.tallybook.common.ShowToast.showToast;

public class FragmentDetail extends Fragment implements DateTimeDialog.MyOnDateSetListener {

    /**
     * 当前用户
     */
    private User user;

    /**
     * 用户头像
     */
    private ImageView userHeadImg;

    /**
     * 选择年月按钮
     */
    private Button selectYm;

    /**
     * 月收入
     */
    private TextView income;

    /**
     * 月支出
     */
    private TextView outcome;

    /**
     * 年月选择器
     */
    private DateTimeDialog dateTimeDialog;

    /**
     * 年
     */
    private int year;

    /**
     * 月
     */
    private int month;

    /**
     * 显示明细的控件
     */
    private RecyclerView recyclerView;

    /**
     * 下拉刷新控件
     */
    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * 数据
     */
    private List<Detail> data;

    /**
     * 适配器
     */
    private DetailAdapter detailAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        setListeners();

        onDateSet(new Date());
        refresh();
    }

    /**
     * @return void
     * @Author MACHENIKE
     * @Description TODO 设置控件的监听器
     **/
    private void setListeners() {

        selectYm.setOnClickListener(v -> dateTimeDialog.hideOrShow());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            refresh();
        });

    }

    /**
     * @return void
     * @Author MACHENIKE
     * @Description TODO 刷新主体View
     **/
    private void refresh() {
        //获取月收入与月支出
        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.YEAR, year);
        startCal.set(Calendar.MONTH, month - 1);
        startCal.set(Calendar.DAY_OF_MONTH, 1);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(startCal.getTime());
        endCal.add(Calendar.MONTH, 1);

        BmobQuery<Detail> startBmobQuery = new BmobQuery<>();
        startBmobQuery.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(startCal.getTime()));

        BmobQuery<Detail> endBmobQuery = new BmobQuery<>();
        endBmobQuery.addWhereLessThanOrEqualTo("createdAt", new BmobDate(endCal.getTime()));

        List<BmobQuery<Detail>> bmobQueries = new ArrayList<>();
        bmobQueries.add(startBmobQuery);
        bmobQueries.add(endBmobQuery);

        BmobQuery<Detail> detailBmobQuery = new BmobQuery<>();
        detailBmobQuery.addWhereEqualTo("user", new BmobPointer(user));
        detailBmobQuery.order("-createdAt");
        detailBmobQuery.and(bmobQueries);
        detailBmobQuery.findObjects(new FindListener<Detail>() {
            @Override
            public void done(List<Detail> list, BmobException e) {
                if (e == null) {
                    //设置适配器
                    data = list;
                    detailAdapter = new DetailAdapter(getActivity(), data);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(detailAdapter);

                    showMonthDetail(list);
                } else {
                    showToast(getActivity(), "查询明细失败", e);
                }
            }
        });

    }

    /**
     * @return void
     * @Author MACHENIKE
     * @Description TODO 显示月收入与月支出
     **/
    @SuppressLint("SetTextI18n")
    private void showMonthDetail(List<Detail> list) {

        double sumIn = 0f;
        double sumOut = 0f;

        for (Detail detail : list) {
            if ("收入".equals(detail.getDirection())) {
                sumIn += detail.getAmount();
            } else {
                sumOut += detail.getAmount();
            }
        }

        income.setText("收入:" + sumIn + "元");
        outcome.setText("支出:" + sumOut + "元");

    }

    /**
     * @return void
     * @Author MACHENIKE
     * @Description TODO 获取控件以及初始化
     **/
    private void initView() {
        user = BmobUser.getCurrentUser(User.class);

        userHeadImg = Objects.requireNonNull(getActivity()).findViewById(R.id.link_mine);
        selectYm = getActivity().findViewById(R.id.selectYM);
        income = getActivity().findViewById(R.id.income);
        outcome = getActivity().findViewById(R.id.outcome);

        dateTimeDialog = new DateTimeDialog(getActivity(), this);

        recyclerView = Objects.requireNonNull(getActivity()).findViewById(R.id.detailDay_recyclerView);
        swipeRefreshLayout = getActivity().findViewById(R.id.swipe);

        swipeRefreshLayout.setColorSchemeResources(R.color.md_green_400, R.color.md_red_400, R.color.md_blue_400);
    }

    @Override
    public void onDateSet(Date date) {
        //在按钮上设置日期
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy年 MM月");
        String str = mFormatter.format(date);
        selectYm.setText(str);

        //获取年月
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
    }
}
