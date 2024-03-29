package com.greentech.ixuanxiu.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.bean.MyUser;
import com.greentech.ixuanxiu.ui.LoginActivity;

/**
 * Created by xjh1994 on 2015/10/26.
 */
public class FragmentBase extends Fragment {
    /**
     * Fragment基类
     */

    protected MyUser currentUser = null;

    public MyUser getCurrentUser() {
        currentUser = MyUser.getCurrentUser(getActivity(), MyUser.class);
        if (null == currentUser) {
            toast(getString(R.string.toast_need_login));
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return null;
        } else if (1 == currentUser.getStatus()) {
            toast(getString(R.string.toast_locked_account));
            return null;
        }

        return currentUser;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void toast(String text) {
        if (null != getActivity()) {
            Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
