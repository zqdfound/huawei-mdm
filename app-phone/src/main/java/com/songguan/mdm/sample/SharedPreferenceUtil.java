/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package com.songguan.mdm.sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * The SharedPreferenceUtil for this Sample
 *
 * @author huawei mdm
 * @since 2019-10-23
 */
public class SharedPreferenceUtil {
    private static final String TAG = "SharedPreferenceUtil";
    private static final int DEFAULT_VERSION_CODE = 0;
    private static final String EULA_PREFIX = "eula_useraccepted_";
    private static final String ACTIVE_LICENSE_KEY = "mdmsample_activate_license";

    private int mVersionCode;
    private String mEulaKey = null;
    private Context mContext = null;
    private SharedPreferences mSharedPreferences = null;

    public SharedPreferenceUtil(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // the eulaKey changes every time you increment the version number in AndroidManifest.xml
        mVersionCode = getVersionCodeInner();
        mEulaKey = EULA_PREFIX + mVersionCode;
    }

    /**
     * Whether the user select not show again
     *
     * @return boolean true if selected otherwise false.
     */
    public boolean hasUserAccepted() {
        return mSharedPreferences.getBoolean(mEulaKey, false);
    }

    /**
     * Save user's choice to SharedPreferences.
     *
     * @param hasAccepted Whether the user select not show again.
     */
    public void saveUserChoice(boolean hasAccepted) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(mEulaKey, hasAccepted);
        editor.commit();
    }

    /**
     * Save license active status to SharedPreferences.
     *
     * @param isActivated Whether the license is activated.
     */
    public void saveActiveLicenseStatus(boolean isActivated) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(ACTIVE_LICENSE_KEY, isActivated);
        editor.commit();
    }

    /**
     * Get license status
     *
     * @return boolean true if license activated otherwise false.
     */
    public boolean getActiveLicenseStatus() {
        return mSharedPreferences.getBoolean(ACTIVE_LICENSE_KEY, false);
    }
    private int getVersionCodeInner() {
        PackageInfo pi = null;
        try {
            pi = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Name not found exception");
        }
        return pi == null ? DEFAULT_VERSION_CODE : pi.versionCode;
    }
}