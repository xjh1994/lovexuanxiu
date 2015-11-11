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
public class FragmentStartLesson extends FragmentBase {
    /**
     * 我要开课
     */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_lesson, container, false);

        initView(view);
        return view;
    }

    private void initView(View view) {

    }
}
