package com.example.tallybook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tallybook.Bean.User;
import com.example.tallybook.MainActivity;
import com.example.tallybook.R;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * 登录页面
 * @author MACHENIKE
 */
public class Login extends AppCompatActivity {

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
     * 登录
     */
    private TextView login;

    /**
     * 注册
     */
    private TextView reg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
     * @Description TODO 设置控件的监听器
     * @return void
     **/
    private void setListeners() {

        //登录按钮点击事件
        login.setOnClickListener(v -> {
            user.setUsername(username.getText().toString().trim());
            user.setPassword(password.getText().toString().trim());

            user.login(new SaveListener<User>() {
                @Override
                public void done(User bmobUser, BmobException e) {
                    if (e == null) {
                        //登录成功 跳转到主页面
                        showToast("登录成功");
                        startActivity(new Intent(Login.this, MainActivity.class));
                        finish();
                    } else {
                        //登录失败 重置密码
                        showToast("登录失败\n" + e.getErrorCode() + "\n" + e.getMessage());
                        password.setText("");
                    }
                }
            });
        });

        //注册按钮点击事件
        reg.setOnClickListener(v -> {
            //跳转到注册页面
            startActivity(new Intent(Login.this,Register.class));
        });

    }
    
    /**
     * @Author MACHENIKE
     * @Description TODO 获取控件以及初始化
     * @return void
     **/
    private void initView() {
        user = new User();

        username = findViewById(R.id.name_login);
        password = findViewById(R.id.password_login);
        login = findViewById(R.id.login);
        reg = findViewById(R.id.reg);
    }

}
