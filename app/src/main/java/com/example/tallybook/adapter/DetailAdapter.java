package com.example.tallybook.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

import static com.example.tallybook.common.ShowToast.showToast;

/**
 * 明细适配器
 *
 * @author MACHENIKE
 */
public class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * 上下文
     */
    private Context context;

    /**
     * 数据列表
     */
    private List<Detail> data;

    /**
     * 显示正常
     */
    private static final int NORMAL_TYPE = 0;

    /**
     * 显示底部加载
     */
    private static final int FOOT_TYPE = 1;

    /**
     * 最大显示数量
     */
    private int maxNum = 15;

    /**
     * @param context 上下文
     * @param data    数据
     * @Author MACHENIKE
     * @Description TODO 构造函数
     **/
    public DetailAdapter(Context context, List<Detail> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //初始化view：明细
        View detailView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail, parent, false);
        //初始化view：底部加载
        View footView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_foot, parent, false);
        //根据viewType选择接下来加载的view
        if (viewType == NORMAL_TYPE) {
            return new RecyclerViewHolder(detailView, NORMAL_TYPE);
        } else {
            return new RecyclerViewHolder(footView, FOOT_TYPE);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
        if (getItemViewType(position) == FOOT_TYPE) {
            //底部加载  最大显示数量+8  即多显示8个
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                maxNum += 8;
                notifyDataSetChanged();
            }, 500);
        } else {
            Detail detail = data.get(position);
            //设置明细中的值
            setDetailInfo(detail, recyclerViewHolder);

            //每一个明细长按事件
            recyclerViewHolder.itemView.setOnLongClickListener(v -> {
                AlertDialog.Builder detailMore = new AlertDialog.Builder(context);
                @SuppressLint("InflateParams")
                View detailMoreView = LayoutInflater.from(context).inflate(R.layout.dialog_detail_more, null);
                detailMore.setView(detailMoreView).create();

                AlertDialog show = detailMore.show();

                TextView detailMoreChange = detailMoreView.findViewById(R.id.detail_more_change);
                TextView detailMoreDelete = detailMoreView.findViewById(R.id.detail_more_delete);

                //修改明细点击事件
                detailMoreChange.setOnClickListener(v12 -> {
                    AlertDialog.Builder detailMoreChange1 = new AlertDialog.Builder(context);
                    @SuppressLint("InflateParams")
                    View detailMoreChangeView = LayoutInflater.from(context).inflate(R.layout.dialog_detail_more_change, null);

                    detailMoreChange1.setView(detailMoreChangeView).create();
                    AlertDialog showChange = detailMoreChange1.show();

                    NiceSpinner detailMoreDirection = detailMoreChangeView.findViewById(R.id.detail_more_direction);
                    EditText detailMoreAmount = detailMoreChangeView.findViewById(R.id.detail_more_amount);
                    Button detailMoreChangeCancel = detailMoreChangeView.findViewById(R.id.detail_more_change_cancel);
                    Button detailMoreChangeConfirm = detailMoreChangeView.findViewById(R.id.detail_more_change_confirm);

//                    //英文的去向
//                    String[] enDir;
//                    //按照去向获取
//                    if ("支出".equals(detail.getDirection())) {
//                        enDir = context.getResources().getStringArray(R.array.out_category);
//                    } else {
//                        enDir = context.getResources().getStringArray(R.array.in_category);
//                    }
//                    //按照英文去向获取中文去向
//                    List<String> chDir = new ArrayList<>();
//                    for (String tEnDir : enDir) {
//                        try {
//                            Field chField = R.string.class.getDeclaredField(tEnDir);
//                            chDir.add(context.getResources().getString(Integer.parseInt(Objects.requireNonNull(chField.get(null)).toString())));
//                        } catch (NoSuchFieldException | IllegalAccessException e) {
//                            e.printStackTrace();
//                        }
//                    }
                    List<String> chDir = getChDir(detail);

                    //设置NiceSpinner的选项值
                    detailMoreDirection.attachDataSource(chDir);
                    //设置首选值为当前去向
                    for (int i = 0; i < chDir.size(); i++) {
                        if (chDir.get(i).equals(detail.getCategory())) {
                            detailMoreDirection.setSelectedIndex(i);
                            break;
                        }
                    }

                    //设置金额输入框的默认值为当前金额
                    detailMoreAmount.setText(String.valueOf(detail.getAmount()));

                    //选择监听  ->  将选好的去向设置到detail中
                    detailMoreDirection.setOnSpinnerItemSelectedListener((parent, view, position1, id) -> detail.setCategory(chDir.get(position1)));

                    //取消按钮的点击事件
                    detailMoreChangeCancel.setOnClickListener(v1 -> showChange.dismiss());

                    //确认按钮的点击事件

                    detailMoreChangeConfirm.setOnClickListener(v1 -> {
                        //修改金额
                        detail.setAmount(Double.valueOf(detailMoreAmount.getText().toString()));
                        //更新明细
                        detail.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    showToast(context, "修改成功");
                                } else {
                                    showToast(context, "修改失败", e);
                                }
                            }
                        });

                        showChange.dismiss();
                    });

                    show.dismiss();
                });

                //删除按钮点击事件
                detailMoreDelete.setOnClickListener(v2 -> {
                    AlertDialog.Builder detailMoreDelete1 = new AlertDialog.Builder(context);
                    detailMoreDelete1.setTitle("确认").setMessage("确认删除吗?\n\n注意:删除后不可恢复")
                            .setPositiveButton("确认", (dialog, which) -> detail.delete(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        showToast(context, "删除成功");
                                    } else {
                                        showToast(context, "删除失败", e);
                                    }
                                }
                            })).setNegativeButton("取消", null).show();

                    show.dismiss();
                });
                //return true 即响应完长按事件不响应点击事件
                return true;
            });
        }
    }

    /**
     * @param detail 长按的目标明细
     * @return java.util.List<java.lang.String>
     * @Author MACHENIKE
     * @Description TODO 按照目标明细的去向获取所有类别
     **/
    private List<String> getChDir(Detail detail) {
        //英文的去向
        String[] enDir;
        //按照去向获取
        if ("支出".equals(detail.getDirection())) {
            enDir = context.getResources().getStringArray(R.array.out_category);
        } else {
            enDir = context.getResources().getStringArray(R.array.in_category);
        }
        //按照英文去向获取中文去向
        List<String> chDir = new ArrayList<>();
        for (String tEnDir : enDir) {
            try {
                Field chField = R.string.class.getDeclaredField(tEnDir);
                chDir.add(context.getResources().getString(Integer.parseInt(Objects.requireNonNull(chField.get(null)).toString())));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return chDir;
    }

    /**
     * @param detail             明细信息
     * @param recyclerViewHolder 容纳recyclerView
     * @return void
     * @Author MACHENIKE
     * @Description TODO 显示明细信息
     **/
    @SuppressLint("SetTextI18n")
    private void setDetailInfo(Detail detail, RecyclerViewHolder recyclerViewHolder) {
        if ("收入".equals(detail.getDirection())) {
            recyclerViewHolder.detailAmount.setText("+" + detail.getAmount() + "元");
        } else {
            recyclerViewHolder.detailAmount.setText("-" + detail.getAmount() + "元");
        }
        recyclerViewHolder.detailCategory.setText(detail.getCategory());
        recyclerViewHolder.detailTime.setText(detail.getFullDate());
    }

    /**
     * @param position 当前明细的位置
     * @return int
     * @Author MACHENIKE
     * @Description TODO 根据position返回类型
     **/
    @Override
    public int getItemViewType(int position) {
        if (position == maxNum + 2) {
            return FOOT_TYPE;
        } else {
            return NORMAL_TYPE;
        }
    }

    /**
     * @return int
     * @Author MACHENIKE
     * @Description TODO 获取数据的长度
     **/
    @Override
    public int getItemCount() {
        return data.size();
    }

    private static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView detailAmount;
        TextView detailCategory;
        TextView detailTime;

        RecyclerViewHolder(View itemView, int type) {
            super(itemView);

            if (type == NORMAL_TYPE) {
                detailAmount = itemView.findViewById(R.id.detail_amount);
                detailCategory = itemView.findViewById(R.id.detail_category);
                detailTime = itemView.findViewById(R.id.detail_time);
            }

        }
    }
}
