package cn.jiiiiiin.vplus.ui.date;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by jiiiiiin
 */

public class DateDialogUtil {

    private static final String PATTERN = "yyyy-MM-dd";

    /**
     * 接收用户选中的日期
     */
    public interface IDateListener {
        void onDateChange(String date);
    }

    private IDateListener mDateListener = null;

    public DateDialogUtil setDateListener(IDateListener listener) {
        this.mDateListener = listener;
        return this;
    }

    public void showDialog(Context context) {
        this.showDialog(context, 1990, 1, 1, PATTERN);
    }

    public void showDialog(Context context, String pattern) {
        this.showDialog(context, 1990, 1, 1, pattern);
    }

    public void showDialog(Context context, int initYear, int initMonthOfYear, int initDayOfMonth, String pattern) {
        final LinearLayout ll = new LinearLayout(context);
        final DatePicker picker = new DatePicker(context);
        final LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);

        picker.setLayoutParams(lp);

        // 设置初始时间
        picker.init(initYear, initMonthOfYear, initDayOfMonth, (view, year, monthOfYear, dayOfMonth) -> {
            final Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            final SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
            final String data = format.format(calendar.getTime());
            if (mDateListener != null) {
                // 通知
                mDateListener.onDateChange(data);
            }
        });

        ll.addView(picker);

        new AlertDialog.Builder(context)
                .setTitle("选择日期")
                .setView(ll)
                .setPositiveButton("确定", (dialog, which) -> {

                })
                .setNegativeButton("取消", (dialog, which) -> {

                })
                .show();
    }

}
