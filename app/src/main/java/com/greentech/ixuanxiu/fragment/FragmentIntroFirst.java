package com.greentech.ixuanxiu.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greentech.ixuanxiu.R;

/**
 * Created by xjh1994 on 2015/11/11.
 */
public class FragmentIntroFirst extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.intro_first, container, false);

        return view;
    }
}
