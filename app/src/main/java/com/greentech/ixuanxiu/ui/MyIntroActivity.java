package com.greentech.ixuanxiu.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.greentech.ixuanxiu.R;
import com.greentech.ixuanxiu.fragment.FragmentHomeTest;
import com.greentech.ixuanxiu.fragment.FragmentIntroFirst;

/**
 * Created by xjh1994 on 2015/11/11.
 */
public class MyIntroActivity extends AppIntro {
    // Please DO NOT override onCreate. Use init.
    @Override
    public void init(Bundle savedInstanceState) {

        // Add your slide's fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(new FragmentIntroFirst(), getApplicationContext());
        addSlide(new FragmentHomeTest(), getApplicationContext());

        // You can override bar/separator color if you want.
        setBarColor(getResources().getColor(R.color.primarycolor));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // You can also hide Skip button
        showSkipButton(true);
    }

    @Override
    public void onSkipPressed() {
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        finish();
        startActivity(new Intent(this, HomeActivity.class));
    }
}
