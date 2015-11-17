package com.greentech.ixuanxiu.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.greentech.ixuanxiu.Config;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.bean.MyUser;
import com.greentech.ixuanxiu.bean.ShareCode;
import com.greentech.ixuanxiu.util.AppManager;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by xjh1994 on 2015/10/27.
 */
public class PasswordShareCodeActivity extends AppCompatActivity {

    @Bind(R.id.appname)
    TextView appname;
    @Bind(R.id.logo)
    CircleImageView logo;
    @Bind(R.id.username)
    MaterialEditText username;
    @Bind(R.id.password)
    MaterialEditText password;
    @Bind(R.id.share_code)
    MaterialEditText shareCode;
    @Bind(R.id.register)
    ButtonRectangle register;

    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 取消标题
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        AppManager.getAppManager().addActivity(this);
        ButterKnife.bind(this);

        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        if (null != data) {
            phone = data.getString("phone");
        }
    }

    private void initView() {
        initListener();
    }

    private void initListener() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nick = username.getText().toString().trim();
                final String pass = password.getText().toString().trim();
                String code = shareCode.getText().toString().trim();
                if (TextUtils.isEmpty(nick)) {
                    toast(getString(R.string.username_to_fill));
                    return;
                } else if (nick.length() > Config.USERNAME_COUNT) {
                    toast("用户名不能超过" + Config.USERNAME_COUNT + "个字符");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    toast(getString(R.string.password_to_fill));
                    return;
                } else if (pass.length() > Config.PASSWORD_COUNT) {
                    toast("密码不能超过" + Config.PASSWORD_COUNT + "个字符");
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    toast(getString(R.string.code_to_fill));
                    return;
                }

                BmobQuery<ShareCode> query = new BmobQuery<ShareCode>();
                query.addWhereEqualTo("number", code);
                query.include("myUser");
                query.findObjects(PasswordShareCodeActivity.this, new FindListener<ShareCode>() {
                    @Override
                    public void onSuccess(List<ShareCode> list) {
                        if (list.size() > 0) {
                            ShareCode shareCode = list.get(0);
                            shareCode.increment("usedTimes");
                            shareCode.update(PasswordShareCodeActivity.this);

                            MyUser myUser = new MyUser();
                            myUser.setUsername(phone);
                            myUser.setPassword(pass);
                            myUser.setPhone(phone);
                            myUser.setNick(nick);
                            myUser.setStatus(0);
                            myUser.signUp(getApplicationContext(), new SaveListener() {
                                @Override
                                public void onSuccess() {
                                    toast("注册成功");
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), AvatarActivity.class));
                                    overridePendingTransition(android.R.anim.slide_in_left,
                                            android.R.anim.slide_out_right);
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    toast("注册失败 " + s);
                                }
                            });
                        } else {
                            toast(getString(R.string.code_not_exist));
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        toast(s);
                    }
                });
            }
        });
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
