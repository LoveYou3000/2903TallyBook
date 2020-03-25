package com.example.tallybook.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.tu.circlelibrary.CirclePercentBar;
import com.example.tallybook.Bean.Budget;
import com.example.tallybook.Bean.Detail;
import com.example.tallybook.Bean.Saving;
import com.example.tallybook.Bean.User;
import com.example.tallybook.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class FragmentBudget extends Fragment {

    private User user;
    private Saving saving;

    private SwipeRefreshLayout budget_swipe;

    private TextView budget_edit;
    private CirclePercentBar circle_bar;
    private TextView remain_amount;
    private TextView budget_amount;
    private TextView month_in;
    private TextView month_out;

    private TextView saving_edit;
    private TextView saving_purpose;
    private TextView saving_amount;
    private TextView saving_already;
    private TextView saving_need;
    private TextView saving_status;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_budget,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        Bmob.initialize(getActivity(),"4c0d8bc51d99076175282cb6010f0f85");

        initView();
        setListener();

        showData();

    }

    private void setListener() {

        budget_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder change_budget = new AlertDialog.Builder(getActivity());
                View budget_view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_change_budget,null);

                change_budget.setView(budget_view).create();
                final AlertDialog show = change_budget.show();

                final EditText new_budget = budget_view.findViewById(R.id.edit_change_budget);
                final Button change_budget_cancel = budget_view.findViewById(R.id.change_budget_cancel);
                final Button change_budget_confirm = budget_view.findViewById(R.id.change_budget_confirm);

                change_budget_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show.dismiss();
                    }
                });

                change_budget_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final double new_budget_d = Double.parseDouble(new_budget.getText().toString());

                        BmobQuery<Budget> budgetBmobQuery = new BmobQuery<>();
                        budgetBmobQuery.addWhereEqualTo("user",new BmobPointer(user));
                        budgetBmobQuery.addWhereEqualTo("year",new Date().getYear() + 1900);
                        budgetBmobQuery.addWhereEqualTo("month",new Date().getMonth());
                        budgetBmobQuery.findObjects(new FindListener<Budget>() {
                            @Override
                            public void done(List<Budget> list, BmobException e) {
                                if (e == null) {
                                    Budget budget = list.get(0);

                                    budget.setBudget_amount(new_budget_d);

                                    budget.update(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                Toast.makeText(getActivity(), "更新成功", Toast.LENGTH_SHORT).show();

                                                //将新信息显示在界面中
                                                showData();
                                            } else {
                                                Toast.makeText(getActivity(), "更新失败", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getActivity(), "查询失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        show.dismiss();
                    }
                });

            }
        });

        saving_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder edit_saving = new AlertDialog.Builder(getActivity());
                View set_saving_view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_change_saving,null);

                edit_saving.setView(set_saving_view).create();
                final AlertDialog show = edit_saving.show();

                final EditText set_saving_purpose = set_saving_view.findViewById(R.id.set_saving_purpose);
                final EditText set_saving_amount = set_saving_view.findViewById(R.id.set_saving_amount);
                final Button change_saving_cancel = set_saving_view.findViewById(R.id.change_saving_cancel);
                final Button change_saving_confirm = set_saving_view.findViewById(R.id.change_saving_confirm);

                if (saving != null) {
                    set_saving_purpose.setText(saving.getSaving_purpose());
                    set_saving_amount.setText(String.valueOf(saving.getSaving_amount()));
                }

                change_saving_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show.dismiss();
                    }
                });

                change_saving_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saving.setSaving_purpose(set_saving_purpose.getText().toString());
                        saving.setSaving_amount(Double.parseDouble(set_saving_amount.getText().toString()));

                        saving.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Toast.makeText(getActivity(), "更新成功", Toast.LENGTH_SHORT).show();
                                    showData();
                                } else {
                                    Toast.makeText(getActivity(), "修改失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        show.dismiss();
                    }
                });
            }
        });

        budget_swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                budget_swipe.setRefreshing(false);
                showData();
            }
        });

    }

    private void showData() {
        BmobQuery<Budget> budgetBmobQuery = new BmobQuery<>();
        budgetBmobQuery.addWhereEqualTo("user",new BmobPointer(user));
        budgetBmobQuery.addWhereEqualTo("year",new Date().getYear() + 1900);
        budgetBmobQuery.addWhereEqualTo("month",new Date().getMonth() + 1);
        budgetBmobQuery.findObjects(new FindListener<Budget>() {
            @Override
            public void done(List<Budget> list, BmobException e) {
                if (e == null) {
                    Budget budget = list.get(0);

                    double budget_amount_num = budget.getBudget_amount();
                    double remain_amount_num = budget.getRemain_amount();

                    budget_amount.setText(String.valueOf(budget_amount_num) + "元");
                    remain_amount.setText(String.valueOf(remain_amount_num) + "元");

                    float remain_percent = (float) (remain_amount_num / budget_amount_num) * 100;
                    System.out.println(remain_amount);

                    circle_bar.setPercentData(remain_percent,new DecelerateInterpolator());

                } else {
                    Toast.makeText(getActivity(), "查询失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(new Date());
        startCal.set(Calendar.DAY_OF_MONTH, 1);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(new Date());
        endCal.add(Calendar.MONTH,1);
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
                    double sum_in = 0d;
                    double sum_out = 0d;
                    for (Detail detail : list) {
                        if (detail.getDirection().equals("收入")) {
                            sum_in += detail.getAmount();
                        } else if (detail.getDirection().equals("支出")) {
                            sum_out += detail.getAmount();
                        }
                    }
                    month_in.setText(sum_in + "元");
                    month_out.setText(sum_out + "元");

                } else {
                    Toast.makeText(getActivity(), "查询明细失败_detail", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BmobQuery<Saving> savingBmobQuery = new BmobQuery<>();
        savingBmobQuery.addWhereEqualTo("user",new BmobPointer(user));
        savingBmobQuery.addWhereEqualTo("saving_status",false);
        savingBmobQuery.findObjects(new FindListener<Saving>() {
            @Override
            public void done(List<Saving> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        saving = null;
                        saving_purpose.setText("");
                        saving_amount.setText("");
                        saving_already.setText("");
                        saving_need.setText("");
                        saving_status.setText("恭喜你完成了省钱目标!");
                    }
                    else {
                        saving = list.get(0);
                        saving_purpose.setText(saving.getSaving_purpose());
                        saving_amount.setText(saving.getSaving_amount() + "元");
                        saving_already.setText(saving.getSaving_already() + "元");
                        saving_need.setText(saving.getSaving_amount() - saving.getSaving_already() + "元");
                        saving_status.setText("革命尚未成功,同志仍需努力!");
                    }
                } else {
                    Toast.makeText(getActivity(), "查询失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initView() {

        user = BmobUser.getCurrentUser(User.class);

        budget_swipe = getActivity().findViewById(R.id.budget_swipe);

        budget_edit = getActivity().findViewById(R.id.budget_edit);
        circle_bar = getActivity().findViewById(R.id.circle_bar);
        remain_amount = getActivity().findViewById(R.id.remain_amount);
        budget_amount = getActivity().findViewById(R.id.budget_amount);
        month_in = getActivity().findViewById(R.id.month_in);
        month_out = getActivity().findViewById(R.id.month_out);

        saving_edit = getActivity().findViewById(R.id.saving_edit);
        saving_purpose = getActivity().findViewById(R.id.saving_purpose);
        saving_amount = getActivity().findViewById(R.id.saving_amount);
        saving_already = getActivity().findViewById(R.id.saving_already);
        saving_status = getActivity().findViewById(R.id.saving_status);
        saving_need = getActivity().findViewById(R.id.saving_need);

    }
}
