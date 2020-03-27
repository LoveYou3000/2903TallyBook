package com.example.tallybook.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tallybook.Bean.Budget;
import com.example.tallybook.Bean.Detail;
import com.example.tallybook.Bean.User;
import com.example.tallybook.R;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 记账页面
 * @author MACHENIKE
 */
public class FragmentCharge extends Fragment {

    /**
     * 当前用户
     */
    private User user;

    /**
     * 当月预算
     */
    private Budget budget;

    /**
     * 下拉列表控件
     */
    private NiceSpinner chargeNiceSpinner;

    /**
     * 显示分类布局控件
     */
    private LinearLayout chargeCategoryArea;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_charge,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        setListeners();

        showCategory(Objects.requireNonNull(getActivity()).getResources().getStringArray(R.array.out_category));
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

    private void setListeners() {

        chargeNiceSpinner.setOnSpinnerItemSelectedListener((parent, view, position, id) -> {
            if (position == 0) {
                showCategory(Objects.requireNonNull(getActivity()).getResources().getStringArray(R.array.out_category));
            } else {
                showCategory(Objects.requireNonNull(getActivity()).getResources().getStringArray(R.array.in_category));
            }
        });

    }

    /**
     * @return void
     * @Author MACHENIKE
     * @Description TODO 获取控件以及初始化
     **/
    private void initView() {

        user = BmobUser.getCurrentUser(User.class);

        chargeNiceSpinner = Objects.requireNonNull(getActivity()).findViewById(R.id.charge_nice_spinner);
        chargeCategoryArea = getActivity().findViewById(R.id.charge_category_area);

        BmobQuery<Budget> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("user",new BmobPointer(user));
        bmobQuery.addWhereEqualTo("year",new Date().getYear() + 1900);
        bmobQuery.addWhereEqualTo("month",new Date().getMonth() + 1);
        bmobQuery.findObjects(new FindListener<Budget>() {
            @Override
            public void done(List<Budget> list, BmobException e) {
                if (e == null) {
                    budget = list.get(0);
                } else {
                    Toast.makeText(getActivity(), "查询当月预算失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * @param names 类别名
     * @return void
     * @Author MACHENIKE
     * @Description TODO 按照名字显示类别
     **/
    private void showCategory(String[] names) {
        chargeCategoryArea.removeAllViews();

        //循环添加
        LinearLayout line = null;
        for (int i = 0; i < names.length; i++) {

            LinearLayout block = new LinearLayout(getActivity());
            block.setOrientation(LinearLayout.VERTICAL);

            //每4个为一行
            if  (i % 4 == 0) {
                if (line != null) {
                    chargeCategoryArea.addView(line);
                }
                line = new LinearLayout(getActivity());
            }

            try {
                ImageView imageView = new ImageView(getActivity());

                Field field = R.drawable.class.getDeclaredField(names[i]);
                imageView.setImageResource(Integer.parseInt(Objects.requireNonNull(field.get(null)).toString()));

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.width = (Objects.requireNonNull(getActivity()).getResources().getDisplayMetrics().widthPixels - 160) / 4;
                layoutParams.height = (Objects.requireNonNull(getActivity()).getResources().getDisplayMetrics().widthPixels - 160) / 4;
                layoutParams.setMargins(0,20,20,20);

                imageView.setLayoutParams(layoutParams);

                TextView textView = new TextView(getActivity());

                Field fieldCn = R.string.class.getDeclaredField(names[i]);
                textView.setText(getActivity().getResources().getString(Integer.parseInt(Objects.requireNonNull(fieldCn.get(null)).toString())));
                textView.setGravity(Gravity.CENTER);

                //图片与文字加在一起
                block.addView(imageView);
                block.addView(textView);

                //某一个图片或文字点击事件
                block.setOnClickListener(v -> {
                    AlertDialog.Builder inputNum = new AlertDialog.Builder(getActivity());
                    @SuppressLint("InflateParams")
                    View inputView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_input_amount,null);

                    inputNum.setView(inputView).create();
                    AlertDialog show = inputNum.show();

                    EditText inputDetailAmount = inputView.findViewById(R.id.input_detail_amount);
                    Button inputCancel = inputView.findViewById(R.id.input_cancel);
                    Button inputConfirm = inputView.findViewById(R.id.input_confirm);

                    //取消
                    inputCancel.setOnClickListener(v1 -> show.dismiss());

                    //确认
                    inputConfirm.setOnClickListener(v2 -> {
                        Detail detail = new Detail();
                        detail.setUser(user);
                        detail.setDirection(chargeNiceSpinner.getText().toString());
                        detail.setCategory(textView.getText().toString());
                        detail.setAmount(Double.parseDouble(inputDetailAmount.getText().toString()));

                        detail.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    showToast("记录成功");

                                    //更新预算信息
                                    if ("收入".equals(chargeNiceSpinner.getText().toString())) {
                                        budget.setRemainAmount(budget.getRemainAmount() +
                                                Double.parseDouble(inputDetailAmount.getText().toString()));
                                    } else {
                                        budget.setRemainAmount(budget.getRemainAmount() -
                                                Double.parseDouble(inputDetailAmount.getText().toString()));
                                    }

                                    budget.update(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                showToast("更新预算成功");
                                            } else {
                                                showToast("更新预算失败", e);
                                            }
                                        }
                                    });
                                } else {
                                    showToast("记录失败", e);
                                }
                            }
                        });

                        show.dismiss();
                    });

                });

                line.addView(block);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        chargeCategoryArea.addView(line);
    }

}
