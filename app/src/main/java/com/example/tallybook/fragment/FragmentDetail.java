package com.example.tallybook.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;

public class FragmentDetail extends Fragment implements DateTimeDialog.MyOnDateSetListener {

    private User user;

    //AppBar上的控件
    private ImageView link_mine;
    private Button selectYM;
    private TextView income;
    private TextView outcome;

    private DateTimeDialog dateTimeDialog;

    private int year;
    private int month;

    //主体View的控件
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    //数据
    private List<Detail> data;

    //适配器
    private DetailAdapter detailAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bmob.initialize(getActivity(), "4c0d8bc51d99076175282cb6010f0f85");

        initAppBar();
        initView();

    }

    private void Refresh() {

        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.YEAR, year);
        startCal.set(Calendar.MONTH, month - 1);
        startCal.set(Calendar.DAY_OF_MONTH, 1);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);

        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.YEAR, year);
        endCal.set(Calendar.MONTH, month);
        endCal.set(Calendar.DAY_OF_MONTH, 1);
        endCal.set(Calendar.HOUR_OF_DAY, 0);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);

        BmobQuery<Detail> startBmobQuery = new BmobQuery<>();
        startBmobQuery.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(startCal.getTime()));

        BmobQuery<Detail> endBmobQuery = new BmobQuery<>();
        endBmobQuery.addWhereLessThanOrEqualTo("createdAt", new BmobDate(endCal.getTime()));

        List<BmobQuery<Detail>> bmobQueries = new ArrayList<>();
        bmobQueries.add(startBmobQuery);
        bmobQueries.add(endBmobQuery);

        BmobQuery<Detail> detailBmobQuery = new BmobQuery<>();
        detailBmobQuery.addWhereEqualTo("user", new BmobPointer(user));
        detailBmobQuery.order("createdAt");
        detailBmobQuery.and(bmobQueries);
        detailBmobQuery.findObjects(new FindListener<Detail>() {
            @Override
            public void done(List<Detail> list, BmobException e) {
                if (e == null) {
                    data = list;
                    detailAdapter = new DetailAdapter(getActivity(), data);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(detailAdapter);

                    showMonthDetail(list);
                } else {
                    Toast.makeText(getActivity(), "查询明细失败_detail", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void showMonthDetail(List<Detail> list) {

        double sum_in = 0f;
        double sum_out = 0f;

        for (Detail detail : list) {
            if (detail.getDirection().equals("收入")) {
                sum_in += detail.getAmount();
            } else {
                sum_out += detail.getAmount();
            }
        }

        income.setText("收入:" + sum_in + "元");
        outcome.setText("支出:" + sum_out + "元");

    }

    private void initView() {
        recyclerView = getActivity().findViewById(R.id.detailDay_recyclerView);
        swipeRefreshLayout = getActivity().findViewById(R.id.swipe);

        swipeRefreshLayout.setColorSchemeResources(R.color.md_green_400, R.color.md_red_400, R.color.md_blue_400);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                Refresh();
            }
        });
    }

    private void initAppBar() {
        user = BmobUser.getCurrentUser(User.class);

        //获取AppBar上的控件
        link_mine = getActivity().findViewById(R.id.link_mine);
        selectYM = getActivity().findViewById(R.id.selectYM);
        income = getActivity().findViewById(R.id.income);
        outcome = getActivity().findViewById(R.id.outcome);
        //选择年月的Dialog
        dateTimeDialog = new DateTimeDialog(getActivity(), this);

        selectYM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimeDialog.hideOrShow();
            }
        });

        //按当时时间初始化
        onDateSet(new Date());
    }

    @Override
    public void onDateSet(Date date) {
        //在按钮上设置日期
        SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy年 MM月");
        String str = mFormatter.format(date);
        selectYM.setText(str);
        //获取年月
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;

        Refresh();
    }
}
