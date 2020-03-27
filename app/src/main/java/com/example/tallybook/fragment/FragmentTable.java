package com.example.tallybook.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tallybook.Bean.Detail;
import com.example.tallybook.Bean.User;
import com.example.tallybook.R;

import org.angmarch.views.NiceSpinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * 图表页面
 *
 * @author MACHENIKE
 */
public class FragmentTable extends Fragment {

    /**
     * 当前用户
     */
    private User user;

    /**
     * 下拉列表控件
     */
    private NiceSpinner tableNiceSpinner;

    /**
     * 选择 周\月\年 之后的提示语
     */
    private TextView tableShowSelected;

    /**
     * 选择周
     */
    private Button tableSelectWeek;

    /**
     * 选择月
     */
    private Button tableSelectMonth;

    /**
     * 选择年
     */
    private Button tableSelectYear;

    /**
     * 折线图
     */
    private LineChartView tableLineChart;

    /**
     * 数据
     */
    private Map<String, Double> tableData;

    /**
     * 日期列表  用于折线图的X轴
     */
    private List<AxisValue> dateList;

    /**
     * 金额列表  用于折线图的Y轴
     */
    private List<PointValue> amountList;

    /**
     * 周标志
     */
    private final int WEEK = 0;

    /**
     * 月标志
     */
    private final int MONTH = 1;

    /**
     * 年标志
     */
    private final int YEAR = 2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_table, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        setListeners();

        showWeekData(tableNiceSpinner.getText().toString());
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
     * @Description TODO 获取控件及初始化
     **/
    private void initView() {

        user = BmobUser.getCurrentUser(User.class);

        tableData = new HashMap<>();
        dateList = new ArrayList<>();
        amountList = new ArrayList<>();

        tableNiceSpinner = Objects.requireNonNull(getActivity()).findViewById(R.id.table_nice_spinner);
        tableShowSelected = getActivity().findViewById(R.id.table_show_selected);
        tableSelectWeek = getActivity().findViewById(R.id.table_select_week);
        tableSelectMonth = getActivity().findViewById(R.id.table_select_month);
        tableSelectYear = getActivity().findViewById(R.id.table_select_year);
        tableLineChart = getActivity().findViewById(R.id.table_line_chart);

    }

    /**
     * @return void
     * @Author MACHENIKE
     * @Description TODO 控件设置监听器
     **/
    private void setListeners() {

        //选择周
        tableSelectWeek.setOnClickListener(v -> {
            tableShowSelected.setText("本周");
            tableSelectWeek.setBackgroundColor(Color.BLACK);
            tableSelectWeek.setTextColor(Color.WHITE);
            tableSelectMonth.setBackgroundColor(Color.WHITE);
            tableSelectMonth.setTextColor(Color.BLACK);
            tableSelectYear.setBackgroundColor(Color.WHITE);
            tableSelectYear.setTextColor(Color.BLACK);
            showWeekData(tableNiceSpinner.getText().toString());
        });

        //选择月
        tableSelectMonth.setOnClickListener(v -> {
            tableShowSelected.setText("本月");
            tableSelectMonth.setBackgroundColor(Color.BLACK);
            tableSelectMonth.setTextColor(Color.WHITE);
            tableSelectWeek.setBackgroundColor(Color.WHITE);
            tableSelectWeek.setTextColor(Color.BLACK);
            tableSelectYear.setBackgroundColor(Color.WHITE);
            tableSelectYear.setTextColor(Color.BLACK);
            showMonthData(tableNiceSpinner.getText().toString());
        });

        //选择年
        tableSelectYear.setOnClickListener(v -> {
            tableShowSelected.setText("本年");
            tableSelectYear.setBackgroundColor(Color.BLACK);
            tableSelectYear.setTextColor(Color.WHITE);
            tableSelectWeek.setBackgroundColor(Color.WHITE);
            tableSelectWeek.setTextColor(Color.BLACK);
            tableSelectMonth.setBackgroundColor(Color.WHITE);
            tableSelectMonth.setTextColor(Color.BLACK);
            showYearData(tableNiceSpinner.getText().toString());
        });

        //下拉列表
        tableNiceSpinner.setOnSpinnerItemSelectedListener((parent, view, position, id) -> {
            if ("本周".equals(tableShowSelected.getText().toString())) {
                if (position == 0) {
                    showWeekData("支出");
                } else {
                    showWeekData("收入");
                }
            } else if ("本月".equals(tableShowSelected.getText().toString())) {
                if (position == 0) {
                    showMonthData("支出");
                } else {
                    showMonthData("收入");
                }
            } else if ("本年".equals(tableShowSelected.getText().toString())) {
                if (position == 0) {
                    showYearData("支出");
                } else {
                    showYearData("收入");
                }
            }
        });

    }

    //按照去向显示一周数据
    private void showWeekData(String direction) {

        Date startDate = getThisMonday(new Date());
        Date endDate = getNextMonday(new Date());

        createTable(direction, startDate, endDate, WEEK);

    }

    //按照去向显示一月数据
    private void showMonthData(String direction) {

        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH) + 1;

        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.YEAR, year);
        startCal.set(Calendar.MONTH, month - 1);
        startCal.set(Calendar.DAY_OF_MONTH, 1);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(startCal.getTime());
        endCal.add(Calendar.MONTH, 1);

        createTable(direction, startCal.getTime(), endCal.getTime(), MONTH);

    }

    //按照去向显示一年数据
    private void showYearData(String direction) {
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        int year = today.get(Calendar.YEAR);

        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.YEAR, year);
        startCal.set(Calendar.MONTH, 0);
        startCal.set(Calendar.DAY_OF_MONTH, 1);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(startCal.getTime());
        endCal.add(Calendar.YEAR, 1);

        createTable(direction, startCal.getTime(), endCal.getTime(), YEAR);
    }

    //创建图表
    private void createTable(String direction, Date startDate, Date endDate, int type) {

        //查询时间段内明细
        BmobQuery<Detail> startQuery = new BmobQuery<>();
        startQuery.addWhereGreaterThan("createdAt", new BmobDate(startDate));

        BmobQuery<Detail> endQuery = new BmobQuery<>();
        endQuery.addWhereLessThan("createdAt", new BmobDate(endDate));

        List<BmobQuery<Detail>> bmobQueryList = new ArrayList<>();
        bmobQueryList.add(startQuery);
        bmobQueryList.add(endQuery);

        BmobQuery<Detail> periodQuery = new BmobQuery<>();
        periodQuery.addWhereEqualTo("user", new BmobPointer(user));
        periodQuery.addWhereEqualTo("direction", direction);
        periodQuery.order("createdAt");
        periodQuery.and(bmobQueryList);
        periodQuery.findObjects(new FindListener<Detail>() {
            @Override
            public void done(List<Detail> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        showToast("还没有信息呢");
                    }

                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat showYmd = new SimpleDateFormat("yyyy-MM-dd");
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat showMd = new SimpleDateFormat("MM-dd");

                    //清空数据
                    tableData.clear();
                    dateList.clear();
                    amountList.clear();

                    //同一天内数据综合
                    double sum = 0d;
                    for (int i = 0; i < list.size(); i++) {
                        sum += list.get(i).getAmount();
                        if (i == list.size() - 1) {
                            tableData.put(list.get(i).getFullDate(), sum);
                            sum = 0d;
                        } else {
                            if (!list.get(i).getFullDate().equals(list.get(i + 1).getFullDate())) {
                                tableData.put(list.get(i).getFullDate(), sum);
                                sum = 0d;
                            }
                        }
                    }

                    //填充空白数据
                    Date itDate = startDate;
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(itDate);
                    do {
                        if (tableData.get(showYmd.format(itDate)) == null) {
                            tableData.put(showYmd.format(itDate), 0d);
                        }
                        cal.add(Calendar.DAY_OF_MONTH, 1);
                        itDate = cal.getTime();

                    } while (itDate.getYear() != endDate.getYear() ||
                            itDate.getMonth() != endDate.getMonth() || itDate.getDay() != endDate.getDay());

                    //按照 周\月\年 处理数据
                    int counter = 0;
                    int currentMonth = 0;
                    double periodSum = 0d;
                    itDate = startDate;
                    cal.setTime(itDate);
                    for (int i = 0; ; i++) {
                        if (type == WEEK) {
                            //一天一个
                            dateList.add(new AxisValue(i).setLabel(showMd.format(itDate)));
                            amountList.add(new PointValue(i, Float.parseFloat(String.valueOf(tableData.get(showYmd.format(itDate))))));
                        } else if (type == MONTH) {
                            //五天一个
                            periodSum += tableData.get(showYmd.format(itDate));
                            if (i % 5 == 0) {
                                dateList.add(new AxisValue(counter).setLabel(showMd.format(itDate)));
                                amountList.add(new PointValue(counter, Float.parseFloat(String.valueOf(periodSum))));
                                periodSum = 0d;
                                counter++;
                            }
                        } else if (type == YEAR) {
                            //一月一个
                            periodSum += tableData.get(showYmd.format(itDate));
                            if (itDate.getMonth() != currentMonth) {
                                currentMonth++;
                                dateList.add(new AxisValue(counter).setLabel(showMd.format(itDate)));
                                amountList.add(new PointValue(counter, Float.parseFloat(String.valueOf(periodSum))));
                                periodSum = 0d;
                                counter++;
                            }
                        }

                        //将最后的数据加入
                        cal.add(Calendar.DAY_OF_MONTH, 1);
                        itDate = cal.getTime();
                        if (itDate.getYear() == endDate.getYear() &&
                                itDate.getMonth() == endDate.getMonth() && itDate.getDay() == endDate.getDay()) {
                            if (type == MONTH && i % 5 != 0) {
                                cal.add(Calendar.DAY_OF_MONTH, -1);
                                dateList.add(new AxisValue(counter).setLabel(showMd.format(cal.getTime())));
                                amountList.add(new PointValue(counter, Float.parseFloat(String.valueOf(periodSum))));
                            }
                            break;
                        }
                    }

                    //生成折线图
                    Line line = new Line(amountList).setColor(Color.parseColor("#e00032"));
                    List<Line> lines = new ArrayList<>();
                    line.setShape(ValueShape.CIRCLE);
                    line.setCubic(false);
                    line.setFilled(false);
                    line.setHasLabels(true);
                    line.setHasLines(true);
                    line.setHasPoints(true);
                    lines.add(line);
                    LineChartData lineChartData = new LineChartData();
                    lineChartData.setLines(lines);

                    Axis axisX = new Axis();
                    axisX.setHasTiltedLabels(true);
                    axisX.setTextColor(Color.parseColor("#000000"));//设置字体颜色

                    axisX.setTextSize(8);//设置字体大小
                    axisX.setMaxLabelChars(12);//最多几个X轴坐标
                    axisX.setValues(dateList);
                    lineChartData.setAxisXBottom(axisX);
                    axisX.setHasLines(true);

                    Axis axisY = new Axis();
                    axisY.setTextColor(Color.parseColor("#000000"));//设置字体颜色
                    axisY.setTextSize(8);
                    lineChartData.setAxisYLeft(axisY);

                    tableLineChart.setInteractive(true);
                    tableLineChart.setZoomType(ZoomType.HORIZONTAL);
                    tableLineChart.setMaxZoom(3f);
                    tableLineChart.setLineChartData(lineChartData);
                    tableLineChart.setVisibility(View.VISIBLE);

                    //设置X轴数据的显示个数（x轴0-7个数据）
                    Viewport v = new Viewport(tableLineChart.getMaximumViewport());
                    v.left = 0;
                    v.right = 7;
                    tableLineChart.setCurrentViewport(v);

                } else {
                    showToast("查询明细失败", e);
                }
            }
        });

    }

    /**
     * @param today 今天
     * @return java.util.Date
     * @Author MACHENIKE
     * @Description TODO 获取下一个周一
     **/
    private Date getNextMonday(Date today) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getThisMonday(today));
//        cal.set(today.getYear() + 1900,today.getMonth() + 1,today.getDay(),0,0,0);
        cal.add(Calendar.DATE, 7);
        return cal.getTime();
    }

    /**
     * @param today 今天
     * @return java.util.Date
     * @Author MACHENIKE
     * @Description TODO 获取这个周一
     **/
    private Date getThisMonday(Date today) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
//        cal.set(today.getYear() + 1900,today.getMonth() + 1,today.getDay(),0,0,0);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal.getTime();
    }

}
