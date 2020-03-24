package com.example.tallybook.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallybook.Bean.Detail;
import com.example.tallybook.R;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Detail> data;

    private static final int NORMAL_TYPE = 0;
    private static final int FOOT_TYPE = 1;

    private int Max_num = 15;

    private Point point;

    public DetailAdapter(Context context, List<Detail> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View detail_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail,parent,false);
        View foot_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_foot,parent,false);
        if (viewType == NORMAL_TYPE) {
            return new RecyclerViewHolder(detail_view,NORMAL_TYPE);
        } else {
            return new RecyclerViewHolder(foot_view,FOOT_TYPE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
        if (getItemViewType(position) == FOOT_TYPE) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Max_num += 8;
                    notifyDataSetChanged();
                }
            },500);
        } else {
            Detail detail = data.get(position);
            if (detail.getDirection().equals("收入"))
                recyclerViewHolder.detail_amount.setText("+" + detail.getAmount() + "元");
            else
                recyclerViewHolder.detail_amount.setText("-" + detail.getAmount() + "元");
            recyclerViewHolder.detail_category.setText(detail.getCategory());
            recyclerViewHolder.detail_time.setText(detail.getFullDate());

            recyclerViewHolder.itemView.setOnLongClickListener(v -> {
                AlertDialog.Builder detail_more = new AlertDialog.Builder(context);
                View detail_more_view = LayoutInflater.from(context).inflate(R.layout.dialog_detail_more,null);
                detail_more.setView(detail_more_view).create();

                AlertDialog show = detail_more.show();

                TextView detail_more_change = detail_more_view.findViewById(R.id.detail_more_change);
                TextView detail_more_delete = detail_more_view.findViewById(R.id.detail_more_delete);

                detail_more_change.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder detail_more_change = new AlertDialog.Builder(context);
                        View detail_more_change_view = LayoutInflater.from(context).inflate(R.layout.dialog_detail_more_change,null);
                        detail_more_change.setView(detail_more_change_view).create();

                        AlertDialog showChange = detail_more_change.show();

                        NiceSpinner detail_more_direction = detail_more_change_view.findViewById(R.id.detail_more_direction);
                        EditText detail_more_amount = detail_more_change_view.findViewById(R.id.detail_more_amount);
                        Button detail_more_change_cancel = detail_more_change_view.findViewById(R.id.detail_more_change_cancel);
                        Button detail_more_change_confirm = detail_more_change_view.findViewById(R.id.detail_more_change_confirm);

                        String[] enDir;
                        if ("支出".equals(detail.getDirection())) {
                            enDir = context.getResources().getStringArray(R.array.out_category);
                        } else {
                            enDir = context.getResources().getStringArray(R.array.in_category);
                        }
                        List<String> chDir = new ArrayList<>();
                        for (String tEnDir : enDir) {
                            try {
                                Field ch_field = R.string.class.getDeclaredField(tEnDir);
                                chDir.add(context.getResources().getString(Integer.parseInt(ch_field.get(null).toString())));
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        detail_more_direction.attachDataSource(chDir);
                        for (int i = 0; i < chDir.size(); i++) {
                            if (chDir.get(i).equals(detail.getCategory())) {
                                detail_more_direction.setSelectedIndex(i);
                                break;
                            }
                        }
                        detail_more_direction.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
                            @Override
                            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                                detail.setCategory(chDir.get(position));
                            }
                        });

                        detail_more_amount.setText(String.valueOf(detail.getAmount()));

                        detail_more_change_cancel.setOnClickListener(v1 -> showChange.dismiss());

                        detail_more_change_confirm.setOnClickListener(v1 -> {
                            //修改
                            detail.setAmount(Double.valueOf(detail_more_amount.getText().toString()));
                            detail.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "修改失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            showChange.dismiss();
                        });

                        show.dismiss();
                    }
                });

                detail_more_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show.dismiss();
                    }
                });
                return false;
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == Max_num + 2)
            return FOOT_TYPE;
        else
            return NORMAL_TYPE;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView detail_amount;
        public TextView detail_category;
        public TextView detail_time;

        public RecyclerViewHolder(View item_view, int Type) {
            super(item_view);

            if (Type == NORMAL_TYPE) {
                detail_amount = item_view.findViewById(R.id.detail_amount);
                detail_category = item_view.findViewById(R.id.detail_category);
                detail_time = item_view.findViewById(R.id.detail_time);
            }

        }
    }
}
