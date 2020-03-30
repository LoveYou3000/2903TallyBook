package com.example.tallybook.common;

import android.content.Context;
import android.widget.Toast;

import cn.bmob.v3.exception.BmobException;

/**
 * 显示Toast信息
 *
 * @author MACHENIKE
 */
public class ShowToast {

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    public static void showToast(Context context, String msg, BmobException e) {
        Toast.makeText(context, msg + "\n" + e.getErrorCode() + "\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

}
