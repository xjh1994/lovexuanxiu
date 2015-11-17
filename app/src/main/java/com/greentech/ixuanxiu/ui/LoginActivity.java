package com.greentech.ixuanxiu.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.greentech.ixuanxiu.Config;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.bean.MyUser;
import com.greentech.ixuanxiu.util.AppManager;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.listener.SaveListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by xjh1994 on 2015/10/27.
 */
public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.appname)
    TextView appname;
    @Bind(R.id.logo)
    ImageView logo;
    @Bind(R.id.username)
    MaterialEditText username;
    @Bind(R.id.password)
    MaterialEditText password;
    @Bind(R.id.login)
    ButtonRectangle login;
    @Bind(R.id.register)
    TextView register;
    @Bind(R.id.overview)
    TextView overview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 取消标题
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (null != MyUser.getCurrentUser(this, MyUser.class)) {
            toast(getString(R.string.toast_already_login));
            AppManager.getAppManager().finishAllActivity();
            startActivity(new Intent(this, HomeActivity.class));
        }

        AppManager.getAppManager().addActivity(this);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        initLogoAnimation();

        initListener();
    }

    private void initLogoAnimation() {
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(1000);
        animationSet.addAnimation(alphaAnimation);
        logo.startAnimation(animationSet);
        appname.startAnimation(animationSet);
    }

    private void initListener() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setEnabled(false);
                String phone = username.getText().toString().trim();
                String pass = password.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    toast("手机号未填写");
                    login.setEnabled(true);
                    return;
                } else if (phone.length() > 11) {
                    toast("手机号不能超过" + 11 + "个字符");;
                    login.setEnabled(true);
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    toast("密码未填写");;
                    login.setEnabled(true);
                    return;
                } else if (pass.length() > Config.PASSWORD_COUNT) {
                    toast("密码不能超过" + Config.PASSWORD_COUNT + "个字符");;
                    login.setEnabled(true);
                    return;
                }

                MyUser myUser = new MyUser();
                myUser.setUsername(phone);
                myUser.setPassword(pass);
                myUser.login(getApplicationContext(), new SaveListener() {
                    @Override
                    public void onSuccess() {
                        toast("登录成功");
                        finish();
                        if (null == AppManager.getAppManager().findActivity(HomeActivity.class)) {
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            overridePendingTransition(android.R.anim.slide_in_left,
                                    android.R.anim.slide_out_right);
                        }
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        toast("登录失败 " + s);;
                        login.setEnabled(true);
                    }
                });
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开注册页面
                RegisterPage registerPage = new RegisterPage();
                registerPage.setRegisterCallback(new EventHandler() {
                    public void afterEvent(int event, int result, Object data) {
                        // 解析注册结果
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            @SuppressWarnings("unchecked")
                            HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                            String country = (String) phoneMap.get("country");
                            String phone = (String) phoneMap.get("phone");

                            // 提交用户信息
                            registerUser(country, phone);
                        }
                    }
                });
                registerPage.show(getApplicationContext());
//                finish();
//                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        overview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
            }
        });
    }

    private void registerUser(String country, String phone) {
        finish();
        Bundle data = new Bundle();
        data.putString("phone", phone);
        Intent intent = new Intent(this, PasswordActivity.class);
        intent.putExtras(data);
        startActivity(intent);

    }

    protected void toast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
}
