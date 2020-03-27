package com.example.tallybook.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tallybook.Bean.User;
import com.example.tallybook.R;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 个人信息界面
 * @author MACHENIKE
 */
public class PersonalInfo extends AppCompatActivity {

    /**
     * 当前登录用户
     */
    private User user;

    /**
     * 返回
     */
    private ImageView personalBack;

    /**
     * 修改头像
     */
    private LinearLayout personalHead;

    /**
     * 修改用户名
     */
    private LinearLayout personalUsername;

    /**
     * 修改用户性别
     */
    private LinearLayout personalSex;

    /**
     * 修改用户密码
     */
    private LinearLayout personalPassword;

    /**
     * 显示用户头像
     */
    private ImageView personalHeadShow;

    /**
     * 显示用户名
     */
    private TextView personalUsernameShow;

    /**
     * 显示用户性别
     */
    private TextView personalSexShow;

    /**
     * 退出登录
     */
    private TextView logout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        Bmob.initialize(this, "4c0d8bc51d99076175282cb6010f0f85");

        initView();
        setListener();
    }

    /**
     * @param msg 要显示的信息
     * @return void
     * @Author MACHENIKE
     * @Description TODO 显示Toast信息
     **/
    public void showToast(String msg) {
        Toast.makeText(PersonalInfo.this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * @return void
     * @Author MACHENIKE
     * @Description TODO 设置各控件的监听器
     **/
    private void setListener() {
        //返回按钮点击事件  ->  直接销毁当前页面
        personalBack.setOnClickListener(v -> finish());

        //修改头像点击事件
        personalHead.setOnClickListener(v -> {
            //插入修改头像代码
            showToast("修改头像");
        });

        //修改用户名点击事件
        personalUsername.setOnClickListener(v -> {
            //初始化一个dialog
            AlertDialog.Builder changeUsernameDialog = new AlertDialog.Builder(PersonalInfo.this);
            //初始化一个view
            View usernameView = View.inflate(PersonalInfo.this, R.layout.dialog_change_username, null);
            //设置dialog的view
            changeUsernameDialog.setView(usernameView).create();
            //展示dialog
            AlertDialog show = changeUsernameDialog.show();
            //获取view中的各个控件
            EditText editUsername = usernameView.findViewById(R.id.edit_username);
            TextView changeConfirm = usernameView.findViewById(R.id.changename_confirm);
            TextView changeCancel = usernameView.findViewById(R.id.changename_cancel);
            //确认按钮点击事件
            changeConfirm.setOnClickListener(v1 -> {
                String newUsername = editUsername.getText().toString();
                //判断是否符合规则
                if (2 <= newUsername.length() && 16 >= newUsername.length()) {
                    //更新后台用户信息
                    user.setUsername(newUsername);
                    user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                showToast("修改成功");
                                //将新的用户名更新到显示用户名的地方
                                personalUsernameShow.setText(user.getUsername());
                            } else {
                                showToast("修改失败\n" + e.getErrorCode() + "\n" + e.getMessage());
                            }
                        }
                    });
                    show.dismiss();
                } else {
                    showToast("用户名不合法");
                    editUsername.setText("");
                }

            });
            //取消按钮点击事件
            changeCancel.setOnClickListener(v2 -> show.dismiss());
        });

        //修改性别点击事件
        personalSex.setOnClickListener(v -> {
            AlertDialog.Builder changeSexeDialog = new AlertDialog.Builder(PersonalInfo.this);
            @SuppressLint("InflateParams")
            View sexView = LayoutInflater.from(PersonalInfo.this).inflate(R.layout.dialog_change_sex, null);

            changeSexeDialog.setView(sexView).create();
            AlertDialog show = changeSexeDialog.show();

            Button setSexMale = sexView.findViewById(R.id.set_sex_male);
            Button setSexFemale = sexView.findViewById(R.id.set_sex_female);

            setSexMale.setOnClickListener(v12 -> {
                user.setSex("男");

                user.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            showToast("修改性别成功");
                            //更新信息到界面
                            personalSexShow.setText(user.getSex());
                        } else {
                            showToast("修改失败\n" + e.getErrorCode() + "\n" + e.getMessage());
                        }
                    }
                });

                show.dismiss();
            });

            setSexFemale.setOnClickListener(v13 -> {
                user.setSex("女");

                user.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            showToast("修改性别成功");
                            //更新信息到界面
                            personalSexShow.setText(user.getSex());
                        } else {
                            showToast("修改失败\n" + e.getErrorCode() + "\n" + e.getMessage());
                        }
                    }
                });

                show.dismiss();
            });
        });

        //修改密码点击事件
        personalPassword.setOnClickListener(v -> {
            AlertDialog.Builder changePasswordDialog = new AlertDialog.Builder(PersonalInfo.this);
            @SuppressLint("InflateParams")
            View changePasswordView = LayoutInflater.from(PersonalInfo.this).inflate(R.layout.dialog_change_password, null);

            changePasswordDialog.setView(changePasswordView).create();

            EditText oldPassword = changePasswordView.findViewById(R.id.old_password);
            EditText newPassword = changePasswordView.findViewById(R.id.new_password);
            TextView changePasswordCancel = changePasswordView.findViewById(R.id.change_password_cancel);
            TextView changePasswordConfirm = changePasswordView.findViewById(R.id.change_password_confirm);

            AlertDialog show = changePasswordDialog.show();

            //取消按钮点击事件
            changePasswordCancel.setOnClickListener(v1 -> show.dismiss());

            changePasswordConfirm.setOnClickListener(v1 -> {

                BmobUser.updateCurrentUserPassword(oldPassword.getText().toString(),
                        newPassword.getText().toString(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    showToast("更新成功,请重新登陆");
                                    BmobUser.logOut();
                                    startActivity(new Intent(PersonalInfo.this, Login.class));
                                    finish();
                                } else {
                                    showToast("更新失败\n" + e.getErrorCode() + "\n" + e.getMessage());
                                }
                            }
                        });
                show.dismiss();
            });
        });

        //退出登录按钮点击事件
        logout.setOnClickListener(v -> {
            AlertDialog.Builder confirmLogout = new AlertDialog.Builder(PersonalInfo.this);
            confirmLogout.setTitle("确认").setMessage("确认退出吗?").setPositiveButton("确认", (dialog, which) -> {
                BmobUser.logOut();
                startActivity(new Intent(PersonalInfo.this, Login.class));
                finish();
            }).setNegativeButton("取消", null).create().show();
        });
    }

    /**
     * @return void
     * @Author MACHENIKE
     * @Description TODO 获取控件以及初始化
     **/
    private void initView() {
        user = BmobUser.getCurrentUser(User.class);

        personalBack = findViewById(R.id.personal_back);

        personalHead = findViewById(R.id.personal_head);
        personalUsername = findViewById(R.id.personal_username);
        personalSex = findViewById(R.id.personal_sex);
        personalPassword = findViewById(R.id.personal_password);

        personalHeadShow = findViewById(R.id.personal_head_show);
        personalUsernameShow = findViewById(R.id.personal_username_show);
        personalSexShow = findViewById(R.id.personal_sex_show);

        logout = findViewById(R.id.logout);

        personalUsernameShow.setText(user.getUsername());
        personalSexShow.setText(user.getSex());
    }
}
