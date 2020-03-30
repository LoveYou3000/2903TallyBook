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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tallybook.Bean.User;
import com.example.tallybook.R;

import org.angmarch.views.NiceSpinner;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

import static com.example.tallybook.common.ShowToast.showToast;

/**
 * 个人信息界面
 *
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

        initView();
        setListener();

        showPersonalInfo();
    }

    /**
     * @return void
     * @Author MACHENIKE
     * @Description TODO 展示个人信息
     **/
    private void showPersonalInfo() {
        personalUsernameShow.setText(user.getUsername());
        personalSexShow.setText(user.getSex());
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
            showToast(PersonalInfo.this, "修改头像");
        });

        //修改用户名点击事件
        personalUsername.setOnClickListener(v -> {
            AlertDialog.Builder changeUsernameDialog = new AlertDialog.Builder(PersonalInfo.this);
            View usernameView = View.inflate(PersonalInfo.this, R.layout.dialog_change_username, null);

            changeUsernameDialog.setView(usernameView).create();
            AlertDialog show = changeUsernameDialog.show();

            EditText editUsername = usernameView.findViewById(R.id.edit_username);
            TextView changeConfirm = usernameView.findViewById(R.id.changename_confirm);
            TextView changeCancel = usernameView.findViewById(R.id.changename_cancel);

            changeCancel.setOnClickListener(v2 -> show.dismiss());

            changeConfirm.setOnClickListener(v1 -> {
                changeUsername(editUsername.getText().toString());
                showPersonalInfo();
                show.dismiss();

            });

        });

        //修改性别点击事件
        personalSex.setOnClickListener(v -> {
            AlertDialog.Builder changeSexeDialog = new AlertDialog.Builder(PersonalInfo.this);
            @SuppressLint("InflateParams")
            View sexView = LayoutInflater.from(PersonalInfo.this).inflate(R.layout.dialog_change_sex, null);

            changeSexeDialog.setView(sexView).create();
            AlertDialog show = changeSexeDialog.show();

            NiceSpinner gender = sexView.findViewById(R.id.change_sex_spinner);
            TextView changeGenderCancel = sexView.findViewById(R.id.change_gender_cancel);
            TextView changeGenderConfirm = sexView.findViewById(R.id.change_gender_confirm);

            changeGenderCancel.setOnClickListener(v1 -> show.dismiss());

            changeGenderConfirm.setOnClickListener(v1 -> {
                changeSex(gender.getText().toString());
                showPersonalInfo();
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

            changePasswordCancel.setOnClickListener(v1 -> show.dismiss());

            changePasswordConfirm.setOnClickListener(v1 -> {
                changePassword(oldPassword.getText().toString(), newPassword.getText().toString());
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
     * @param portraitId 头像id
     * @return void
     * @Author MACHENIKE
     * @Description TODO 修改头像
     **/
    public void changePortraitId(int portraitId) {
        user.setPortraitId(portraitId);

        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showToast(PersonalInfo.this, "修改头像成功");
                } else {
                    showToast(PersonalInfo.this, "修改头像失败", e);
                }
            }
        });
    }

    /**
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return void
     * @Author MACHENIKE
     * @Description TODO 修改密码
     **/
    private void changePassword(String oldPassword, String newPassword) {
        BmobUser.updateCurrentUserPassword(oldPassword, newPassword, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showToast(PersonalInfo.this, "修改密码成功,请重新登陆");
                    BmobUser.logOut();
                    startActivity(new Intent(PersonalInfo.this, Login.class));
                    finish();
                } else {
                    showToast(PersonalInfo.this, "修改密码失败", e);
                }
            }
        });
    }

    /**
     * @param sex 要修改的性别
     * @return void
     * @Author MACHENIKE
     * @Description TODO 修改性别
     **/
    private void changeSex(String sex) {
        user.setSex(sex);

        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showToast(PersonalInfo.this, "修改性别成功");
                } else {
                    showToast(PersonalInfo.this, "修改失败", e);
                }
            }
        });
    }

    /**
     * @param newUsername 新用户名
     * @return void
     * @Author MACHENIKE
     * @Description TODO 修改用户名
     **/
    private void changeUsername(String newUsername) {
        //判断是否符合规则
        if (2 <= newUsername.length() && 16 >= newUsername.length()) {
            //更新后台用户信息
            user.setUsername(newUsername);
            user.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        showToast(PersonalInfo.this, "修改用户名成功");
                    } else {
                        showToast(PersonalInfo.this, "修改用户名失败", e);
                    }
                }
            });
        } else {
            showToast(PersonalInfo.this, "用户名不合法");
        }
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
    }
}
