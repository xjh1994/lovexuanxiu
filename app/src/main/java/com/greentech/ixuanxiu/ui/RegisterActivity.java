package com.greentech.ixuanxiu.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonRectangle;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.util.AppManager;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by xjh1994 on 2015/10/27.
 */
public class RegisterActivity extends AppCompatActivity {

    @Bind(R.id.appname)
    TextView appname;
    @Bind(R.id.logo)
    CircleImageView logo;
    @Bind(R.id.username)
    MaterialEditText username;
    @Bind(R.id.password)
    MaterialEditText password;
    @Bind(R.id.verify)
    MaterialEditText verify;
    @Bind(R.id.send_verify)
    ButtonFlat sendVerify;
    @Bind(R.id.layout_verify)
    LinearLayout layoutVerify;
    @Bind(R.id.register)
    ButtonRectangle register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 取消标题
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        AppManager.getAppManager().addActivity(this);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {

        initListener();
    }

    private void initListener() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
