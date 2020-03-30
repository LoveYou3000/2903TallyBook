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

import static com.example.tallybook.common.ShowToast.showToast;

/**
 * 注册页面
 *
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
     * @return void
     * @Author MACHENIKE
     * @Description TODO 设置各控件的监听器
     **/
    private void setListeners() {
        //注册按钮点击事件
        register.setOnClickListener(v -> {
            register();
        });
    }

    /**
     * @return void
     * @Author MACHENIKE
     * @Description TODO 用户注册
     **/
    private void register() {
        user.setUsername(username.getText().toString().trim());
        user.setPassword(password.getText().toString().trim());
        user.setSex("隐私");
        user.setPortraitId(0);

        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    showToast(Register.this, "注册成功，请登录");
                    startActivity(new Intent(Register.this, Login.class));
                    finish();
                } else {
                    showToast(Register.this, "注册失败", e);
                    username.setText("");
                    password.setText("");
                }
            }
        });
    }

    /**
     * @return void
     * @Author MACHENIKE
     * @Description TODO 获取控件及初始化
     **/
    private void initView() {
        user = new User();

        username = findViewById(R.id.username_reg);
        password = findViewById(R.id.password_reg);
        register = findViewById(R.id.register);
    }

}
