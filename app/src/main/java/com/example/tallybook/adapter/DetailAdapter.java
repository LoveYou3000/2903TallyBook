package com.example.tallybook.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tallybook.Bean.Detail;
import com.example.tallybook.R;

import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Detail> data;

    private static final int NORMAL_TYPE = 0;
    private static final int FOOT_TYPE = 1;

    private int Max_num = 15;

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
                recyclerViewHolder.detail_amount.setText("+" + String.valueOf(detail.getAmount()) + "元");
            else
                recyclerViewHolder.detail_amount.setText("-" + String.valueOf(detail.getAmount()) + "元");
            recyclerViewHolder.detail_category.setText(detail.getCategory());
            recyclerViewHolder.detail_time.setText(detail.getFullDate());
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
