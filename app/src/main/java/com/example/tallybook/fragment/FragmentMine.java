package com.example.tallybook.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import com.example.tallybook.activity.Login;
import com.example.tallybook.activity.PersonalInfo;

import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class FragmentMine extends Fragment {

    private User user;

    private SwipeRefreshLayout mine_swipe;

    private ImageView mine_head;
    private TextView mine_name;
    private TextView mine_count_day;
    private TextView mine_count_detail;
    private ImageView mine_more;
    private TextView logout;

    private TextView mine_budget_amount;
    private TextView mine_remain_amount;

    private TextView mine_saving_complete;
    private TextView mine_saving_need;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bmob.initialize(getActivity(),"4c0d8bc51d99076175282cb6010f0f85");

        initView();
        setListeners();

        ShowData();

    }

    private void ShowData() {

        mine_name.setText(user.getUsername());

        BmobQuery<Detail> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("user",new BmobPointer(user));
        bmobQuery.findObjects(new FindListener<Detail>() {
            @Override
            public void done(List<Detail> list, BmobException e) {
                if (e == null) {
                    int day_num = 1;
                    for (int i = 1; i < list.size(); i++) {
                        if (list.get(i).getFullDate().equals(list.get(i - 1).getFullDate())) {
                            continue;
                        } else {
                            day_num++;
                        }
                    }

                    mine_count_day.setText("已记账 " + day_num + " 天");
                    mine_count_detail.setText("已记账 " + list.size() + " 笔");
                } else {
                    Toast.makeText(getActivity(), "查询失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BmobQuery<Budget> budgetBmobQuery = new BmobQuery<>();
        budgetBmobQuery.addWhereEqualTo("user",new BmobPointer(user));
        budgetBmobQuery.addWhereEqualTo("year",new Date().getYear() + 1900);
        budgetBmobQuery.addWhereEqualTo("month",new Date().getMonth() + 1);
        budgetBmobQuery.findObjects(new FindListener<Budget>() {
            @Override
            public void done(List<Budget> list, BmobException e) {
                if (e == null) {
                    Budget budget = list.get(0);

                    mine_budget_amount.setText(budget.getBudget_amount() + "元");
                    mine_remain_amount.setText(budget.getRemain_amount() + "元");
                } else {
                    Toast.makeText(getActivity(), "查询失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BmobQuery<Saving> saving_complete_timeBmobQuery = new BmobQuery<>();
        saving_complete_timeBmobQuery.addWhereEqualTo("user",new BmobPointer(user));
        saving_complete_timeBmobQuery.addWhereEqualTo("saving_status",true);
        saving_complete_timeBmobQuery.count(Saving.class, new CountListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    mine_saving_complete.setText(integer + "次");
                } else {
                    Toast.makeText(getActivity(), "查询失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BmobQuery<Saving> saving_needBmonQuery = new BmobQuery<>();
        saving_needBmonQuery.addWhereEqualTo("user",new BmobPointer(user));
        saving_needBmonQuery.addWhereEqualTo("saving_status",false);
        saving_needBmonQuery.findObjects(new FindListener<Saving>() {
            @Override
            public void done(List<Saving> list, BmobException e) {
                if (e == null) {
                    mine_saving_need.setText(list.get(0).getSaving_amount() - list.get(0).getSaving_already() + "元");
                } else {
                    Toast.makeText(getActivity(), "查询失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setListeners() {

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder confirm_logout = new AlertDialog.Builder(getActivity());
                confirm_logout.setMessage("确认退出吗?").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BmobUser.logOut();
                        startActivity(new Intent(getActivity(),Login.class));
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();

            }
        });

        mine_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMineMoreMenu(v);
            }
        });

        mine_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),PersonalInfo.class));
                mine_name.setText(BmobUser.getCurrentUser(User.class).getUsername());
            }
        });

        mine_swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mine_swipe.setRefreshing(false);
                ShowData();
            }
        });

    }

    private void initView() {
        user = BmobUser.getCurrentUser(User.class);

        mine_swipe = getActivity().findViewById(R.id.mine_swipe);

        mine_head = getActivity().findViewById(R.id.mine_head);
        mine_name = getActivity().findViewById(R.id.mine_name);
        mine_count_day = getActivity().findViewById(R.id.mine_count_day);
        mine_count_detail = getActivity().findViewById(R.id.mine_count_detail);
        mine_more = getActivity().findViewById(R.id.mine_more);
        logout = getActivity().findViewById(R.id.logout);

        mine_budget_amount = getActivity().findViewById(R.id.mine_budget_amount);
        mine_remain_amount = getActivity().findViewById(R.id.mine_remain_amount);

        mine_saving_complete = getActivity().findViewById(R.id.mine_saving_complete);
        mine_saving_need = getActivity().findViewById(R.id.mine_saving_need);
    }

    public void showMineMoreMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(),view);
        popupMenu.getMenuInflater().inflate(R.menu.mine_more,popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals("修改密码")) {
                    final AlertDialog.Builder change_password = new AlertDialog.Builder(getActivity());
                    View password_view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_change_password,null);

                    EditText old_password = password_view.findViewById(R.id.old_password);
                    EditText new_password = password_view.findViewById(R.id.new_password);
                    TextView change_password_cancel = password_view.findViewById(R.id.change_password_cancel);
                    TextView change_password_confirm = password_view.findViewById(R.id.change_password_confirm);

                    change_password.setView(password_view).create();
                    AlertDialog show = change_password.show();

                    change_password_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            show.dismiss();
                        }
                    });

                    change_password_confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            BmobUser.updateCurrentUserPassword(old_password.getText().toString(),
                                    new_password.getText().toString(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        Toast.makeText(getActivity(), "更新成功,请重新登陆", Toast.LENGTH_SHORT).show();
                                        BmobUser.logOut();
                                        startActivity(new Intent(getActivity(),Login.class));
                                        getActivity().finish();
                                    } else {
                                        Toast.makeText(getActivity(), "更新失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            show.dismiss();
                        }
                    });

                }
                return false;
            }
        });

        popupMenu.show();
    }
}
