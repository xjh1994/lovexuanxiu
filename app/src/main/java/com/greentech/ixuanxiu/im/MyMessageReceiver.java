package com.greentech.ixuanxiu.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import cn.bmob.im.inteface.EventListener;
import com.orhanobut.logger.Logger;

/**
 * Created by xjh1994 on 2015/10/25.
 */
public class MyMessageReceiver extends BroadcastReceiver {

    // 事件监听
    public static ArrayList<EventListener> ehList = new ArrayList<EventListener>();

    @Override
    public void onReceive(Context context, Intent intent) {
        String json = intent.getStringExtra("msg");
        Logger.json("收到的message = ", json);
        //省略其他代码
    }
}
