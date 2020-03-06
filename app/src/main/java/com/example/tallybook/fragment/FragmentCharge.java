package com.example.tallybook.fragment;

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

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class FragmentCharge extends Fragment {

    private User user;

    private NiceSpinner charge_nice_spinner;
    private LinearLayout charge_category_area;

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

        showCategory(getActivity().getResources().getStringArray(R.array.out_category));
    }

    private void setListeners() {

        charge_nice_spinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                if (position == 0)
                    showCategory(getActivity().getResources().getStringArray(R.array.out_category));
                else
                    showCategory(getActivity().getResources().getStringArray(R.array.in_category));
            }
        });

    }

    private void initView() {

        user = BmobUser.getCurrentUser(User.class);

        charge_nice_spinner = getActivity().findViewById(R.id.charge_nice_spinner);
        charge_category_area = getActivity().findViewById(R.id.charge_category_area);

    }

    public void showCategory(String[] names) {
        charge_category_area.removeAllViews();
        LinearLayout line = null;
        for (int i = 0; i < names.length; i++) {

            LinearLayout block = new LinearLayout(getActivity());
            block.setOrientation(LinearLayout.VERTICAL);

            if  (i % 4 == 0) {
                if (line != null) {
                    charge_category_area.addView(line);
                }
                line = new LinearLayout(getActivity());
            }

            ImageView imageView = new ImageView(getActivity());
            try {
                Field field = R.drawable.class.getDeclaredField(names[i]);
                imageView.setImageResource(Integer.parseInt(field.get(null).toString()));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.width = (getActivity().getResources().getDisplayMetrics().widthPixels - 160) / 4;
                params.setMargins(0,20,20,20);
//                    params.setMargins(40,20,40,20);
                params.height = params.width;
                imageView.setLayoutParams(params);

                TextView textView = new TextView(getActivity());
                Field field_cn = R.string.class.getDeclaredField(names[i]);
                textView.setText(getActivity().getResources().getString(Integer.parseInt(field_cn.get(null).toString())));
                textView.setGravity(Gravity.CENTER);

                block.addView(imageView);
                block.addView(textView);

                block.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), String.valueOf(textView.getText().toString()), Toast.LENGTH_SHORT).show();

                        final AlertDialog.Builder input_num = new AlertDialog.Builder(getActivity());
                        View input_view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_input_amount,null);

                        input_num.setView(input_view).create();
                        AlertDialog show = input_num.show();

                        EditText input_detail_amount = input_view.findViewById(R.id.input_detail_amount);
                        Button input_cancel = input_view.findViewById(R.id.input_cancel);
                        Button input_confirm = input_view.findViewById(R.id.input_confirm);

                        input_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                show.dismiss();
                            }
                        });

                        input_confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Detail detail = new Detail();
                                detail.setUser(user);
                                detail.setDirection(charge_nice_spinner.getText().toString());
                                detail.setCategory(textView.getText().toString());
                                detail.setAmount(Double.parseDouble(input_detail_amount.getText().toString()));

                                detail.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if (e == null) {
                                            Toast.makeText(getActivity(), "记帐成功", Toast.LENGTH_SHORT).show();

                                            BmobQuery<Budget> bmobQuery = new BmobQuery<>();
                                            bmobQuery.addWhereEqualTo("user",new BmobPointer(user));
                                            bmobQuery.addWhereEqualTo("year",new Date().getYear() + 1900);
                                            bmobQuery.addWhereEqualTo("month",new Date().getMonth() + 1);
                                            bmobQuery.findObjects(new FindListener<Budget>() {
                                                @Override
                                                public void done(List<Budget> list, BmobException e) {
                                                    if (e == null) {
                                                        Budget budget = list.get(0);

                                                        if (charge_nice_spinner.getText().toString().equals("收入")) {
                                                            budget.setRemain_amount(budget.getRemain_amount() +
                                                                    Double.parseDouble(input_detail_amount.getText().toString()));
                                                        } else {
                                                            budget.setRemain_amount(budget.getRemain_amount() -
                                                                    Double.parseDouble(input_detail_amount.getText().toString()));
                                                        }

                                                        budget.update(new UpdateListener() {
                                                            @Override
                                                            public void done(BmobException e) {
                                                                if (e == null) {
                                                                    Toast.makeText(getActivity(), "更新预算成功", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(getActivity(), "更新预算失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        Toast.makeText(getActivity(), "记录预算失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(getActivity(), "记帐失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                show.dismiss();
                            }
                        });

                    }
                });

                line.addView(block);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        charge_category_area.addView(line);
    }

}
