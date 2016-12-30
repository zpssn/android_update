package com.tec.zenyan.common;

import android.content.Context;

/**
 * Created by kisss on 2016/12/29.
 */

public class AppMessage {
    public static int getVersionCode(Context mContext) {
        try {
            String pkName = mContext.getPackageName();
            int versionCode = mContext.getPackageManager().getPackageInfo(pkName, 0).versionCode;
            return versionCode;
        } catch (Exception e) {
        }
        return 0;
    }

    public static String getVersionName(Context mContext) {
        try {
            String pkName = mContext.getPackageName();
            String versionName = mContext.getPackageManager().getPackageInfo(pkName, 0).versionName;
            return versionName;
        } catch (Exception e) {

        }
        return null;
    }

}
