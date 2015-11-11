package com.greentech.ixuanxiu.util;

import android.content.Context;
import android.util.DisplayMetrics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xjh1994 on 2015/8/4.
 */
public class Utils {

    /**
     * 把两个数组合并为一个
     * @param a
     * @param b
     * @return
     */
    public static String[] concat(String[] a, String[] b) {
        String[] c= new String[a.length+b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    /**
     * 验证手机号码
     *
     * @param mobiles
     * @return [0-9]{5,9}
     */
    public static boolean isMobileNO(String mobiles) {
        boolean flag = false;
        try {
            mobiles = mobiles.replace(" ", "");
            mobiles = mobiles.replace("+86", "");
            Pattern p = Pattern
                    .compile("^((13[0-9])|(15[^4,\\D])|(18[0,2,5-9]))\\d{8}$");
            Matcher m = p.matcher(mobiles);
            flag = m.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 手机号验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * This method converts device specific pixels to device independent pixels.
     *
     * @param px
     *            A value in px (pixels) unit. Which we need to convert into db
     * @param context
     *            Context to get resources and device specific display metrics
     * @return A float value to represent db equivalent to px value
     */
    public float convertPixelsToDp(Context ctx, float px) {
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;

    }

    public static int convertDpToPixelInt(Context context, float dp) {

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int px = (int) (dp * (metrics.densityDpi / 160f));
        return px;
    }

    public static float convertDpToPixel(Context context, float dp) {

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float px = (float) (dp * (metrics.densityDpi / 160f));
        return px;
    }
}
