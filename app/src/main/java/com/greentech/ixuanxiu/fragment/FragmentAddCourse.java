package com.greentech.ixuanxiu.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greentech.ixuanxiu.R;

/**
 * Created by xjh1994 on 2015/11/5.
 */
public class FragmentAddCourse extends FragmentBase {
    /**
     * 添加课程
     */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_course, container, false);

        initView(view);
        return view;
    }

    private void initView(View view) {

    }
}
