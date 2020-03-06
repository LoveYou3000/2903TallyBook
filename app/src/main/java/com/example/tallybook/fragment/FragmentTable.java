package com.example.tallybook.fragment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tallybook.Bean.Detail;
import com.example.tallybook.Bean.User;
import com.example.tallybook.R;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;
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

public class FragmentTable extends Fragment {

    private User user;

    private NiceSpinner table_nice_spinner;
    private TextView table_show_selected;
    private Button table_select_week;
    private Button table_select_month;
    private Button table_select_year;

    private LineChartView table_line_chart;
//    private ListView table_listview;

    private Map<String, Double> table_data;
    private List<AxisValue> dateList;
    private List<PointValue> amountList;

    private final int WEEK = 0;
    private final int MONTH = 1;
    private final int YEAR = 2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_table, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bmob.initialize(getActivity(), "4c0d8bc51d99076175282cb6010f0f85");

        initView();
        setListeners();

        showWeekData(table_nice_spinner.getText().toString());
    }

    private void initView() {

        user = BmobUser.getCurrentUser(User.class);

        table_data = new HashMap<>();
        dateList = new ArrayList<>();
        amountList = new ArrayList<>();

        table_nice_spinner = getActivity().findViewById(R.id.table_nice_spinner);
        table_show_selected = getActivity().findViewById(R.id.table_show_selected);
        table_select_week = getActivity().findViewById(R.id.table_select_week);
        table_select_month = getActivity().findViewById(R.id.table_select_month);
        table_select_year = getActivity().findViewById(R.id.table_select_year);
        table_line_chart = getActivity().findViewById(R.id.table_line_chart);

    }

    private void setListeners() {

        table_select_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                table_show_selected.setText("本周");
                table_select_week.setBackgroundColor(Color.BLACK);
                table_select_week.setTextColor(Color.WHITE);
                table_select_month.setBackgroundColor(Color.WHITE);
                table_select_month.setTextColor(Color.BLACK);
                table_select_year.setBackgroundColor(Color.WHITE);
                table_select_year.setTextColor(Color.BLACK);
                showWeekData(table_nice_spinner.getText().toString());
            }
        });

        table_select_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                table_show_selected.setText("本月");
                table_select_month.setBackgroundColor(Color.BLACK);
                table_select_month.setTextColor(Color.WHITE);
                table_select_week.setBackgroundColor(Color.WHITE);
                table_select_week.setTextColor(Color.BLACK);
                table_select_year.setBackgroundColor(Color.WHITE);
                table_select_year.setTextColor(Color.BLACK);
                showMonthData(table_nice_spinner.getText().toString());
            }
        });

        table_select_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                table_show_selected.setText("本年");
                table_select_year.setBackgroundColor(Color.BLACK);
                table_select_year.setTextColor(Color.WHITE);
                table_select_week.setBackgroundColor(Color.WHITE);
                table_select_week.setTextColor(Color.BLACK);
                table_select_month.setBackgroundColor(Color.WHITE);
                table_select_month.setTextColor(Color.BLACK);
                showYearData(table_nice_spinner.getText().toString());
            }
        });

        table_nice_spinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                if (table_show_selected.getText().toString().equals("本周")) {
                    if (position == 0)
                        showWeekData("支出");
                    else
                        showWeekData("收入");
                } else if (table_show_selected.getText().toString().equals("本月")){
                    if (position == 0)
                        showMonthData("支出");
                    else
                        showMonthData("收入");
                } else if (table_show_selected.getText().toString().equals("本年")) {
                    if (position == 0)
                        showYearData("支出");
                    else
                        showYearData("收入");
                }
            }
        });

    }

    public void showWeekData(String direction) {

        Date startDate = getThisMonday(new Date());
        Date endDate = getNextMonday(new Date());

        createTable(direction,startDate,endDate,WEEK);

    }

    public void showMonthData(String direction) {

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
        endCal.set(Calendar.YEAR, year);
        endCal.set(Calendar.MONTH, month);
        endCal.set(Calendar.DAY_OF_MONTH, 1);
        endCal.set(Calendar.HOUR_OF_DAY, 0);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);

        createTable(direction,startCal.getTime(),endCal.getTime(),MONTH);

    }

    public void showYearData(String direction) {
//        SimpleDateFormat showYMD = new SimpleDateFormat("yyyy-MM-dd");
//
//        Date today = new Date();
//        int year = today.getYear() + 1900;
//        String start_str = year + "-01-01";
//        String end_str = (year + 1) + "-01-01";
//
//        Date startDate = null;
//        try {
//            startDate = showYMD.parse(start_str);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        Date endDate = null;
//        try {
//            endDate = showYMD.parse(end_str);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

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
        endCal.set(Calendar.YEAR, year + 1);
        endCal.set(Calendar.MONTH, 0);
        endCal.set(Calendar.DAY_OF_MONTH, 1);
        endCal.set(Calendar.HOUR_OF_DAY, 0);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);

        createTable(direction,startCal.getTime(),endCal.getTime(),YEAR);
    }

    public void createTable(String direction,Date startDate,Date endDate,int type) {

        BmobQuery<Detail> startQuery = new BmobQuery<>();
        startQuery.addWhereGreaterThan("createdAt",new BmobDate(startDate));

        BmobQuery<Detail> endQuery = new BmobQuery<>();
        endQuery.addWhereLessThan("createdAt",new BmobDate(endDate));

        List<BmobQuery<Detail>> bmobQueryList = new ArrayList<>();
        bmobQueryList.add(startQuery);
        bmobQueryList.add(endQuery);

        BmobQuery<Detail> periodQuery = new BmobQuery<>();
        periodQuery.addWhereEqualTo("user",new BmobPointer(user));
        periodQuery.addWhereEqualTo("direction",direction);
        periodQuery.order("createdAt");
        periodQuery.and(bmobQueryList);
        periodQuery.findObjects(new FindListener<Detail>() {
            @Override
            public void done(List<Detail> list, BmobException e) {
                if (e == null) {

                    if (list.size() == 0) {
                        Toast.makeText(getActivity(), "还没有信息呢", Toast.LENGTH_SHORT).show();
//                        return;
                    } else {

                        SimpleDateFormat showYMD = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat showMD = new SimpleDateFormat("MM-dd");

                        table_data.clear();
                        dateList.clear();
                        amountList.clear();

                        double sum = 0d;
                        for (int i = 0; i < list.size(); i++) {
                            sum += list.get(i).getAmount();
                            if (i == list.size() - 1) {
                                table_data.put(list.get(i).getFullDate(), sum);
                                sum = 0d;
                            } else {
                                if (list.get(i).getFullDate().equals(list.get(i + 1).getFullDate())) {

                                } else {
                                    table_data.put(list.get(i).getFullDate(), sum);
                                    sum = 0d;
                                }
                            }
                        }
//        for (String date : table_data.keySet()) {
//            System.out.println(date + " " + table_data.get(date));
//        }
                        Date it_date = startDate;
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(it_date);
                        for (; ; ) {

                            if (table_data.get(showYMD.format(it_date)) == null) {
                                table_data.put(showYMD.format(it_date), 0d);
                            }
                            cal.add(Calendar.DAY_OF_MONTH, 1);
                            it_date = cal.getTime();
                            if (it_date.getYear() == endDate.getYear() &&
                                    it_date.getMonth() == endDate.getMonth() && it_date.getDay() == endDate.getDay()) {
                                break;
                            }

                        }
                        for (String date : table_data.keySet()) {
                            System.out.println(date + " " + table_data.get(date));
                        }


                        int counter = 0;
                        int current_month = 0;
                        double period_sum = 0d;
                        it_date = startDate;
                        cal.setTime(it_date);
                        for (int i = 0; ; i++) {

                            if (type == WEEK) {
                                dateList.add(new AxisValue(i).setLabel(showMD.format(it_date)));
                                amountList.add(new PointValue(i, Float.parseFloat(String.valueOf(table_data.get(showYMD.format(it_date))))));
                            } else if (type == MONTH) {
                                period_sum += table_data.get(showYMD.format(it_date));
                                if (i % 5 == 0) {
                                    dateList.add(new AxisValue(counter).setLabel(showMD.format(it_date)));
                                    amountList.add(new PointValue(counter, Float.parseFloat(String.valueOf(period_sum))));
                                    period_sum = 0d;
                                    counter++;
                                }
                            } else if (type == YEAR) {
                                period_sum += table_data.get(showYMD.format(it_date));
                                if (it_date.getMonth() != current_month) {
                                    current_month++;
                                    dateList.add(new AxisValue(counter).setLabel(showMD.format(it_date)));
                                    amountList.add(new PointValue(counter, Float.parseFloat(String.valueOf(period_sum))));
                                    period_sum = 0d;
                                    counter++;
                                }
                            }

                            cal.add(Calendar.DAY_OF_MONTH, 1);
                            it_date = cal.getTime();
                            if (it_date.getYear() == endDate.getYear() &&
                                    it_date.getMonth() == endDate.getMonth() && it_date.getDay() == endDate.getDay()) {
                                if (type == MONTH && i % 5 != 0) {
                                    cal.add(Calendar.DAY_OF_MONTH, -1);
                                    dateList.add(new AxisValue(counter).setLabel(showMD.format(cal.getTime())));
                                    amountList.add(new PointValue(counter, Float.parseFloat(String.valueOf(period_sum))));
                                }
                                break;
                            }
                        }
                    }

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

                    table_line_chart.setInteractive(true);
                    table_line_chart.setZoomType(ZoomType.HORIZONTAL);
                    table_line_chart.setMaxZoom(3f);
                    table_line_chart.setLineChartData(lineChartData);
                    table_line_chart.setVisibility(View.VISIBLE);

                    //设置X轴数据的显示个数（x轴0-7个数据）
                    Viewport v = new Viewport(table_line_chart.getMaximumViewport());
                    v.left = 0;
                    v.right= 7;
                    table_line_chart.setCurrentViewport(v);

                } else {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println(e.getErrorCode() + " " + e.getMessage());
                }
            }
        });

    }

    private Date getNextMonday(Date today) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getThisMonday(today));
//        cal.set(today.getYear() + 1900,today.getMonth() + 1,today.getDay(),0,0,0);
        cal.add(Calendar.DATE, 7);
        return cal.getTime();
    }

    private Date getThisMonday(Date today) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.HOUR,-cal.get(Calendar.HOUR));
        cal.add(Calendar.MINUTE,-cal.get(Calendar.MINUTE));
        cal.add(Calendar.SECOND,-Calendar.SECOND);
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
