package com.yidian.newssdk.utils;

import android.content.Context;
import android.view.ViewConfiguration;

/**
 * 限制最高最低速的工具类
 * Created by patrickleong on 4/13/16.
 */
public class VelocityHelper {

    private static boolean hasInit = false;

    private static int mMinimumVelocity;
    private static int mMaximumVelocity;

    /**
     * 控制速度在最在一定范围内
     */
    public static int adjustVelocity(Context context, int velocity) {
        if(!hasInit){
            final ViewConfiguration configuration = ViewConfiguration.get(context);
            mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
            mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
            hasInit = true;
        }

        //Avoid velocity too slow or too fast.
        int vAbs = Math.abs(velocity);
        if (vAbs > mMaximumVelocity) {
            velocity = mMaximumVelocity;
        }
        return velocity;
    }

}
