package com.me.myapplication;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/*
 *  @项目名：  APP
 *  @包名：    com.myun.helper.util
 *  @文件名:   ViewUtil
 *  @创建者:   cjf
 *  @创建时间:  2018/7/10 20:15
 *  @描述：
 */
public final class ViewUtil {

    public static int[] getSize(View view) {
        int width = view.getLayoutParams().width;
        if (width <= 0) {
            if (width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                view.measure(0, 0);
                width = view.getMeasuredWidth();
            } else {
                width = view.getWidth();
            }
        }
        int height = view.getLayoutParams().height;
        if (height <= 0) {
            if (height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                view.measure(0, 0);
                height = view.getMeasuredHeight();
            } else {
                height = view.getHeight();
            }
        }
        return new int[]{width, height};
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
