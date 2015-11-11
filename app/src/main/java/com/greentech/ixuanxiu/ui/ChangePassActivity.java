package com.greentech.ixuanxiu.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.gc.materialdesign.views.ButtonRectangle;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.base.BaseActivity;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by xjh1994 on 2015/11/10.
 */
public class ChangePassActivity extends BaseActivity {

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_change_pass);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListeners() {
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change.setEnabled(false);
                if (isEmpty()) {
                    toast(getString(R.string.toast_pass_empty_not_allowed));
                    change.setEnabled(true);
                    return;
                }
                if (isNotSame()) {
                    toast(getString(R.string.toast_pass_not_same));
                    change.setEnabled(true);
                    return;
                }
                BmobUser.updateCurrentUserPassword(ChangePassActivity.this, password.getText().toString().trim(),
                        confirmNewPassword.getText().toString().trim(), new UpdateListener() {

                            @Override
                            public void onSuccess() {
                                toast(getString(R.string.toast_change_pass_success));
                                finish();
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                toast("密码修改失败：" + msg);
                                change.setEnabled(true);
                            }
                        });
            }
        });
    }

    private boolean isNotSame() {
        if (newPassword.getText().toString().trim().equals(confirmNewPassword.getText().toString().trim()))
            return false;
        return true;
    }

    private boolean isEmpty() {
        if (TextUtils.isEmpty(password.getText().toString().trim()) || TextUtils.isEmpty(newPassword.getText().toString().trim())
                || TextUtils.isEmpty(confirmNewPassword.getText().toString().trim())) {
            return true;
        }
        return false;
    }

    @Override
    public void initData() {

    }

    @Bind(R.id.password)
    MaterialEditText password;
    @Bind(R.id.new_password)
    MaterialEditText newPassword;
    @Bind(R.id.confirm_new_password)
    MaterialEditText confirmNewPassword;
    @Bind(R.id.change)
    ButtonRectangle change;

}
