package com.moxtra.moxiechat;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.moxtra.sdk.MXAccountManager;
import com.moxtra.sdk.MXSDKException;

/**
 * Created by brad on 5/14/15.
 */
public class MoxieChatApplication extends MultiDexApplication {

    private static final String TAG = "MoxieChatApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            MXAccountManager.createInstance(this, "irej6RlLOBo", "uiwE8ZzymRs", true);
        } catch (MXSDKException.InvalidParameter invalidParameter) {
            Log.e(TAG, "Error when creating MXAccountManager instance.", invalidParameter);
        }
    }
}
