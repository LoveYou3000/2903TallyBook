package com.example.tallybook.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tallybook.MainActivity;
import com.example.tallybook.R;

import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

/**
 * 开始闪屏
 *
 * @author MACHENIKE
 */
public class Splash extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //初始化Bmob
        Bmob.initialize(this, "4c0d8bc51d99076175282cb6010f0f85");

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (BmobUser.isLogin()) {
                    startActivity(new Intent(Splash.this, MainActivity.class));
                } else {
                    startActivity(new Intent(Splash.this, Login.class));
                }
                finish();
            }
        }, 2000);
    }
}
