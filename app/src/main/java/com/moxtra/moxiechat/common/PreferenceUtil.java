package com.moxtra.moxiechat.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.moxtra.moxiechat.model.DummyData;
import com.moxtra.moxiechat.model.User;

/**
 * Created by blade on 5/13/15.
 */
public class PreferenceUtil {

    private final static String PREF = "PREF";
    private final static String EMAIL = "EMAIL";
    private final static String GCM_REG_ID = "GCM_REG_ID";
    private final static String APP_VERSION = "APP_VERSION";
    private static final String TAG = "PreferenceUtil";

    private static String getClientId(Context context) {
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Can't get metadata.", e);
        }
        if (appInfo != null && appInfo.metaData != null) {
            String clientId = appInfo.metaData.getString("com.moxtra.sdk.ClientId");
            return clientId;
        }
        return null;
    }

    public static void saveUser(Context context, String email) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(EMAIL, email).commit();
    }

    public static void removeUser(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.remove(EMAIL).commit();
    }

    public static User getUser(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String email = sp.getString(EMAIL, null);
        if (email == null) {
            return null;
        }
        return DummyData.findByEmail(email);
    }

    public static boolean isUserInit(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String clientId = getClientId(context);
        boolean isInit = sp.getBoolean(clientId, false);
        return isInit;
    }

    public static void setUserInitDone(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(getClientId(context), true).commit();
    }

    public static String getGcmRegId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sp.getString(GCM_REG_ID, null);
    }

    public static void setGcmRegId(Context context, String gcmRegId) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(GCM_REG_ID, gcmRegId).commit();
    }

    public static int getAppVersion(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sp.getInt(APP_VERSION, -1);
    }

    public static void setAppVersion(Context context, int appVersion) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(APP_VERSION, appVersion).commit();
    }
}
