package com.example.tallybook.common;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import com.example.tallybook.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * 选择日期对话框
 *
 * @author MACHENIKE
 */
public class DateTimeDialog extends AlertDialog implements View.OnClickListener {

    /**
     * 日期选择器
     */
    private DatePicker mDatePicker;

    /**
     * 选择监听器
     */
    private MyOnDateSetListener onDateSetListener;

    /**
     * 取消按钮
     */
    private Button cancleButton;

    /**
     * 确定按钮
     */
    private Button okButton;

    // 控制 日期
    private int measureWidth;

    /**
     * @param context 上下文
     * @Author MACHENIKE
     * @Description TODO 单个参数构造函数
     **/
    protected DateTimeDialog(Context context) {
        super(context);
    }

    /**
     * @param context  上下文
     * @param callBack 回调函数
     * @Author MACHENIKE
     * @Description TODO 两个参数构造函数
     **/
    public DateTimeDialog(Context context, MyOnDateSetListener callBack) {
        super(context);
        this.onDateSetListener = callBack;
        init();
    }

    /**
     * @return void
     * @Author MACHENIKE
     * @Description TODO 初始化
     **/
    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.dialog_date_picker, null);
        setView(view);
        mDatePicker = view.findViewById(R.id.datePicker);
        LinearLayout buttonGroup = view.findViewById(R.id.buttonGroup);
        cancleButton = view.findViewById(R.id.cancelButton);
        okButton = view.findViewById(R.id.okButton);

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTime(new Date());

        cancleButton.setOnClickListener(this);
        okButton.setOnClickListener(this);

        //取消日的显示
        ((ViewGroup) ((ViewGroup) mDatePicker.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);

        // 设置 显示 宽高
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        buttonGroup.measure(width, height);
        mDatePicker.measure(width, height);
        this.measureWidth = Math.max(buttonGroup.getMeasuredWidth(), mDatePicker.getMeasuredWidth());
    }

    /**
     * @return void
     * @Author MACHENIKE
     * @Description TODO 控制对话框的出现和消失
     **/
    public void hideOrShow() {
        if (!this.isShowing()) {
            this.show();
            //设置 显示 的 宽高
            WindowManager.LayoutParams attributes = Objects.requireNonNull(this.getWindow()).getAttributes();
            attributes.width = this.measureWidth + 100;
            this.getWindow().setAttributes(attributes);
        } else {
            this.dismiss();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelButton:
                dismiss();
                break;
            case R.id.okButton:
                onOkButtonClick();
                dismiss();
                break;
            default:
                break;
        }
    }

    /**
     * 确认 按钮 点击 事件
     */
    private void onOkButtonClick() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, mDatePicker.getYear());
        calendar.set(Calendar.MONTH, mDatePicker.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, mDatePicker.getDayOfMonth());
        calendar.getTime();
        if (onDateSetListener != null) {
            onDateSetListener.onDateSet(calendar.getTime());
        }

        Log.i("testss", mDatePicker.getYear() + "====" + (mDatePicker.getMonth() + 1) + "==" + mDatePicker.getDayOfMonth());
    }

    /**
     * 时间  改变 监听
     */
    public interface MyOnDateSetListener {
        void onDateSet(Date date);
    }

}
