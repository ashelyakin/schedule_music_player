package com.ashelyakin.schedulemusicplayer;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.lang.reflect.Method;

public class BackgroundStart {

    public static boolean canBackgroundStart(Context context) {
        AppOpsManager ops = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        try {
            int op = 10021; // >= 23
            // ops.checkOpNoThrow(op, uid, packageName)
            Method method = ops.getClass().getMethod("checkOpNoThrow", new Class[]
                    {int.class, int.class, String.class}
            );
            Integer result = (Integer) method.invoke(ops, op, android.os.Process.myUid(), context.getPackageName());
            return result == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            Log.e("BackgroundStart", "not support", e);
        }
        return false;
    }

    public static boolean isAccessGranted(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
