package com.xxf.mymusic.util;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * author：xxf
 */
public class ServiceUtil {

    /**
     * 判断服务是否开启
     * "com.xxf.mymusic.service.MyMusicService"
     *
     * @return
     */
    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (("").equals(ServiceName) || ServiceName == null)
            return false;
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(ServiceName)) {
                Log.e("", "isServiceRunning: " + runningService.get(i).service.getClassName().toString());
                return true;
            }
        }
        return false;
    }
}
