package com.example.tallybook.activity;

import android.content.DialogInterface;
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

public class PersonalInfo extends AppCompatActivity {

    private User user;

    private ImageView personal_back;
    private LinearLayout personal_head;
    private LinearLayout personal_username;
    private LinearLayout personal_sex;

    private ImageView personal_head_show;
    private TextView personal_username_show;
    private TextView personal_sex_show;

    private TextView logout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        Bmob.initialize(this,"4c0d8bc51d99076175282cb6010f0f85");

        initView();
        setListener();
    }

    private void setListener() {

        personal_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        personal_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PersonalInfo.this, "修改头像", Toast.LENGTH_SHORT).show();
            }
        });

        personal_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder change_usernameDialog = new AlertDialog.Builder(PersonalInfo.this);
                View username_view = View.inflate(PersonalInfo.this,R.layout.dialog_change_username,null);

                change_usernameDialog.setView(username_view).create();
                final AlertDialog show = change_usernameDialog.show();

                final EditText edit_username = username_view.findViewById(R.id.edit_username);
                final TextView change_confirm = username_view.findViewById(R.id.changename_confirm);
                final TextView change_cancel = username_view.findViewById(R.id.changename_cancel);

                change_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String new_username = edit_username.getText().toString();
                        //判断是否符合规则
                        if (2 <= new_username.length() && 16 >= new_username.length()) {
                            //更新后台用户信息
                            final User user = BmobUser.getCurrentUser(User.class);
                            user.setUsername(new_username);
                            user.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        Toast.makeText(PersonalInfo.this, "修改成功", Toast.LENGTH_SHORT).show();

                                        //将新的用户名更新到显示用户名的地方
                                        personal_username_show.setText(user.getUsername());

                                    } else {
                                        Toast.makeText(PersonalInfo.this, "修改失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            show.dismiss();
                        } else {
                            Toast.makeText(PersonalInfo.this, "用户名不合法", Toast.LENGTH_SHORT).show();
                            edit_username.setText("");
                        }

                    }
                });

                change_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show.dismiss();
                    }
                });

            }
        });

        personal_sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PersonalInfo.this, "修改性别", Toast.LENGTH_SHORT).show();

                final AlertDialog.Builder change_sexeDialog = new AlertDialog.Builder(PersonalInfo.this);
                View sex_view = LayoutInflater.from(PersonalInfo.this).inflate(R.layout.dialog_change_sex,null);

                change_sexeDialog.setView(sex_view).create();
                final AlertDialog show = change_sexeDialog.show();

                final Button set_sex_male = sex_view.findViewById(R.id.set_sex_male);
                final Button set_sex_female = sex_view.findViewById(R.id.set_sex_female);

                final User user = BmobUser.getCurrentUser(User.class);

                set_sex_male.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user.setSex("男");

                        user.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Toast.makeText(PersonalInfo.this, "修改成功" + user.getSex(), Toast.LENGTH_SHORT).show();

                                    //更新信息到界面
                                    personal_sex_show.setText(user.getSex());
                                } else {
                                    Toast.makeText(PersonalInfo.this, "修改失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        show.dismiss();
                    }
                });

                set_sex_female.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user.setSex("女");

                        user.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Toast.makeText(PersonalInfo.this, "修改成功" + user.getSex(), Toast.LENGTH_SHORT).show();

                                    //更新信息到界面
                                    personal_sex_show.setText(user.getSex());
                                } else {
                                    Toast.makeText(PersonalInfo.this, "修改失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        show.dismiss();
                    }
                });

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder confirm_logout = new android.app.AlertDialog.Builder(PersonalInfo.this);
                confirm_logout.setMessage("确认退出吗?").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BmobUser.logOut();
                        startActivity(new Intent(PersonalInfo.this,Login.class));
                        finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();

            }
        });

    }

    private void initView() {
        user = BmobUser.getCurrentUser(User.class);

        personal_back = findViewById(R.id.personal_back);
        personal_head = findViewById(R.id.personal_head);
        personal_username = findViewById(R.id.personal_username);
        personal_sex = findViewById(R.id.personal_sex);

        personal_head_show = findViewById(R.id.personal_head_show);
        personal_username_show = findViewById(R.id.personal_username_show);
        personal_sex_show = findViewById(R.id.personal_sex_show);

        personal_username_show.setText(user.getUsername());
        personal_sex_show.setText(user.getSex());

        logout = findViewById(R.id.logout);
    }
}
