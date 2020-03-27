package com.example.tallybook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tallybook.Bean.User;
import com.example.tallybook.R;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * 注册页面
 * @author MACHENIKE
 */
public class Register extends AppCompatActivity {

    /**
     * 用户
     */
    private User user;

    /**
     * 用户名
     */
    private EditText username;

    /**
     * 密码
     */
    private EditText password;

    /**
     * 注册按钮
     */
    private TextView register;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        setListeners();
    }

    /**
     * @Author MACHENIKE
     * @Description TODO 显示Toast信息
     * @param msg 要显示的信息
     * @return void
     **/
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * @Author MACHENIKE
     * @Description TODO 设置各控件的监听器
     * @return void
     **/
    private void setListeners() {

        //注册按钮点击事件
        register.setOnClickListener(v -> {
            user.setUsername(username.getText().toString().trim());
            user.setPassword(password.getText().toString().trim());

            user.signUp(new SaveListener<User>() {
                @Override
                public void done(User user, BmobException e) {
                    if (e == null) {
                        showToast("注册成功，请登录");
                        startActivity(new Intent(Register.this, Login.class));
                        finish();
                    } else {
                        showToast("注册失败\n" + e.getErrorCode() + "\n" + e.getMessage());
                    }
                }
            });
        });

    }

    /**
     * @Author MACHENIKE
     * @Description TODO 获取控件及初始化
     * @return void
     **/
    private void initView() {
        user = new User();

        username = findViewById(R.id.username_reg);
        password = findViewById(R.id.password_reg);
        register = findViewById(R.id.register);
    }

}
