package com.example.tallybook.fragment;

import android.annotation.SuppressLint;
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
import java.util.Objects;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 明细页面
 *
 * @author MACHENIKE
 */
public class FragmentBudget extends Fragment {

    /**
     * 当前用户
     */
    private User user;

    /**
     * 存钱
     */
    private Saving saving;

    /**
     * 预算
     */
    private Budget budget;

    /**
     * 显示月份控件
     */
    private TextView budgetMonth;

    /**
     * 下拉刷新布局对象
     */
    private SwipeRefreshLayout budgetSwipe;

    /**
     * 百分比控件对象
     */
    private CirclePercentBar circleBar;

    /**
     * 预算编辑
     */
    private TextView budgetEdit;

    /**
     * 剩余金额
     */
    private TextView remainAmount;

    /**
     * 预算金额
     */
    private TextView budgetAmount;

    /**
     * 月收入
     */
    private TextView monthIn;

    /**
     * 月支出
     */
    private TextView monthOut;

    /**
     * 存钱编辑
     */
    private TextView savingEdit;

    /**
     * 存钱目的
     */
    private TextView savingPurpose;

    /**
     * 存钱金额
     */
    private TextView savingAmount;

    /**
     * 已存金额
     */
    private TextView savingAlready;

    /**
     * 仍需金额
     */
    private TextView savingNeed;

    /**
     * 状态显示
     */
    private TextView savingStatus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_budget, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        setListener();

//        showData();

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
     * @Description TODO 设置各个控件的监听器
     **/
    private void setListener() {

        //预算编辑点击事件
        budgetEdit.setOnClickListener(v -> {
            AlertDialog.Builder changeBudget = new AlertDialog.Builder(getActivity());
            @SuppressLint("InflateParams")
            View budgetView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_change_budget, null);

            changeBudget.setView(budgetView).create();
            AlertDialog show = changeBudget.show();

            EditText newBudget = budgetView.findViewById(R.id.edit_change_budget);
            Button changeBudgetCancel = budgetView.findViewById(R.id.change_budget_cancel);
            Button changeBudgetConfirm = budgetView.findViewById(R.id.change_budget_confirm);

            //取消编辑点击事件
            changeBudgetCancel.setOnClickListener(v1 -> show.dismiss());

            //确认编辑点击事件
            changeBudgetConfirm.setOnClickListener(v12 -> {

                double newBudgetD = Double.parseDouble(newBudget.getText().toString());

                budget.setBudgetAmount(newBudgetD);
                budget.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            showToast("更新成功");
                            //将新信息显示在界面中
                            showData();
                        } else {
                            showToast("更新失败", e);
                        }
                    }
                });

                show.dismiss();
            });

        });

        //存钱编辑点击事件
        savingEdit.setOnClickListener(v -> {
            AlertDialog.Builder editSaving = new AlertDialog.Builder(getActivity());
            @SuppressLint("InflateParams")
            View setSavingView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_change_saving, null);

            editSaving.setView(setSavingView).create();
            AlertDialog show = editSaving.show();

            EditText setSavingPurpose = setSavingView.findViewById(R.id.set_saving_purpose);
            EditText setSavingAmount = setSavingView.findViewById(R.id.set_saving_amount);
            Button changeSavingCancel = setSavingView.findViewById(R.id.change_saving_cancel);
            Button changeSavingConfirm = setSavingView.findViewById(R.id.change_saving_confirm);

            if (saving != null) {
                setSavingPurpose.setText(saving.getSavingPurpose());
                setSavingAmount.setText(String.valueOf(saving.getSavingAmount()));
            }

            //取消编辑点击事件
            changeSavingCancel.setOnClickListener(v1 -> show.dismiss());

            //确认编辑点击事件
            changeSavingConfirm.setOnClickListener(v2 -> {
                if (saving == null) {
                    //当前不存在未完成的存钱计划
                    saving = new Saving();
                    saving.setUser(user);
                    saving.setSavingAlready(0d);
                    saving.setSavingStatus(false);

                    saving.setSavingPurpose(setSavingPurpose.getText().toString());
                    saving.setSavingAmount(Double.parseDouble(setSavingAmount.getText().toString()));

                    saving.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                showToast("插入存钱成功");
                                showData();
                            } else {
                                showToast("插入预算失败", e);
                            }
                        }
                    });
                } else {
                    saving.setSavingPurpose(setSavingPurpose.getText().toString());
                    saving.setSavingAmount(Double.parseDouble(setSavingAmount.getText().toString()));

                    saving.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                showToast("更新成功");
                                showData();
                            } else {
                                showToast("修改失败", e);
                            }
                        }
                    });
                }
                show.dismiss();
            });
        });

        //下拉刷新事件
        budgetSwipe.setOnRefreshListener(() -> {
            budgetSwipe.setRefreshing(false);
            showData();
        });

    }

    /**
     * @return void
     * @Author MACHENIKE
     * @Description TODO 显示数据
     **/
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void showData() {

        budgetMonth.setText(String.format("%02d",new Date().getMonth() + 1) + "月总预算");
        //显示预算信息
        double budgetAmountNum = budget.getBudgetAmount();
        double remainAmountNum = budget.getRemainAmount();

        budgetAmount.setText(budgetAmountNum + "元");
        remainAmount.setText(remainAmountNum + "元");

        float remainPercent = (float) (remainAmountNum / budgetAmountNum) * 100;

        circleBar.setPercentData(remainPercent, new DecelerateInterpolator());

        //计算月收入与月支出
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(new Date());
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
        detailBmobQuery.order("createdAt");
        detailBmobQuery.and(bmobQueries);
        detailBmobQuery.findObjects(new FindListener<Detail>() {
            @Override
            public void done(List<Detail> list, BmobException e) {
                if (e == null) {

                    double sumIn = 0d;
                    double sumOut = 0d;

                    for (Detail detail : list) {
                        if ("收入".equals(detail.getDirection())) {
                            sumIn += detail.getAmount();
                        } else {
                            sumOut += detail.getAmount();
                        }
                    }
                    monthIn.setText(sumIn + "元");
                    monthOut.setText(sumOut + "元");

                } else {
                    showToast("查询明细失败", e);
                }
            }
        });

    }

    /**
     * @return void
     * @Author MACHENIKE
     * @Description TODO 获取控件及初始化
     **/
    @SuppressLint("SetTextI18n")
    private void initView() {

        user = BmobUser.getCurrentUser(User.class);

        budgetMonth = Objects.requireNonNull(getActivity()).findViewById(R.id.budget_month);
        budgetSwipe = Objects.requireNonNull(getActivity()).findViewById(R.id.budget_swipe);

        budgetEdit = getActivity().findViewById(R.id.budget_edit);
        circleBar = getActivity().findViewById(R.id.circle_bar);
        remainAmount = getActivity().findViewById(R.id.remain_amount);
        budgetAmount = getActivity().findViewById(R.id.budget_amount);
        monthIn = getActivity().findViewById(R.id.month_in);
        monthOut = getActivity().findViewById(R.id.month_out);

        savingEdit = getActivity().findViewById(R.id.saving_edit);
        savingPurpose = getActivity().findViewById(R.id.saving_purpose);
        savingAmount = getActivity().findViewById(R.id.saving_amount);
        savingAlready = getActivity().findViewById(R.id.saving_already);
        savingStatus = getActivity().findViewById(R.id.saving_status);
        savingNeed = getActivity().findViewById(R.id.saving_need);

        BmobQuery<Budget> budgetBmobQuery = new BmobQuery<>();
        budgetBmobQuery.addWhereEqualTo("user", new BmobPointer(user));
        budgetBmobQuery.addWhereEqualTo("year", new Date().getYear() + 1900);
        budgetBmobQuery.addWhereEqualTo("month", new Date().getMonth() + 1);
        budgetBmobQuery.findObjects(new FindListener<Budget>() {
            @Override
            public void done(List<Budget> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        budget = new Budget();
                        budget.setUser(user);
                        budget.setYear(new Date().getYear() + 1900);
                        budget.setMonth(new Date().getMonth() + 1);
                        budget.setBudgetAmount(1500d);
                        budget.setRemainAmount(1500d);

                        budget.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    showToast("自动写入预算成功");
                                } else {
                                    showToast("自动写入预算失败", e);
                                }
                            }
                        });
                    } else {
                        budget = list.get(0);
                    }

                    showData();

                } else {
                    showToast("budget页面查询预算信息失败", e);
                }
            }
        });

        BmobQuery<Saving> savingBmobQuery = new BmobQuery<>();
        savingBmobQuery.addWhereEqualTo("user", new BmobPointer(user));
        savingBmobQuery.addWhereEqualTo("savingStatus", false);
        savingBmobQuery.findObjects(new FindListener<Saving>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void done(List<Saving> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        saving = null;
                        savingPurpose.setText("");
                        savingAmount.setText("");
                        savingAlready.setText("");
                        savingNeed.setText("");
                        savingStatus.setText("恭喜你完成了省钱目标!");

                        savingEdit.setText("新增存钱计划");
                    } else {
                        saving = list.get(0);
                        savingPurpose.setText(saving.getSavingPurpose());
                        savingAmount.setText(saving.getSavingAmount() + "元");
                        savingAlready.setText(saving.getSavingAlready() + "元");
                        savingNeed.setText(saving.getSavingAmount() - saving.getSavingAlready() + "元");
                        savingStatus.setText("革命尚未成功,同志仍需努力!");

                        savingEdit.setText("编辑存钱计划");
                    }
                } else {
                    showToast("budget查询存钱信息失败", e);
                }
            }
        });

    }
}
