/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package com.songguan.mdm.sample;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * The Utils for this Sample
 *
 * @author huawei mdm
 * @since 2019-10-23
 */
public class Utils {
    private static final String TAG = "SampleUtils";
    private static final String PREFIX = "en_";
    private static final int EXPECTED_BUFFER_DATA = 4096;
    private static final int MAX_LENGTH = 2048;
    private static final int MAX_LINE_LENGTH = 256;

    /**
     * Get help string from html file
     *
     * @param context Context object
     * @param filePath html file path
     * @return string in html
     */
    public static String getStringFromHtmlFile(Context context, String filePath) {
        String result = "";
        if (context == null || filePath == null) {
            return result;
        }
        try (InputStreamReader streamReader =
                new InputStreamReader(context.getAssets().open(filePath), "utf-8");
                BufferedReader reader = new BufferedReader(streamReader)) {
            StringBuilder builder = new StringBuilder(EXPECTED_BUFFER_DATA);
            String line = null;

            boolean isReadCurrentLine = true;

            // Read each line of the html file, and build a string.
            while ((line = reader.readLine()) != null && line.length() < MAX_LINE_LENGTH) {
                // Don't read the Head tags when CSS styling is not supporeted.
                if (line.contains("<style")) {
                    isReadCurrentLine = false;
                }
                if (line.contains("</style")) {
                    isReadCurrentLine = true;
                }
                if (isReadCurrentLine && builder.length() < MAX_LENGTH) {
                    builder.append(line).append(System.lineSeparator());
                }
            }
            result = builder.toString();
        } catch (IOException ex) {
            Log.e(TAG, ex.getMessage());
        }
        return result;
    }

    /**
     * Get corresponding file based on language
     *
     * @param fileName the original file name
     * @return corresponding file name
     */
    public static String getMatchedFile(String fileName) {
        if (Locale.CHINA.getLanguage().equals(Locale.getDefault().getLanguage())) {
            return fileName;
        } else {
            return PREFIX + fileName;
        }
    }

    /**
     * judge whether the device manager is activated
     *
     * @param activity Activty object
     * @return Whether the device manager is activated
     */
    public static boolean isActiveDeviceManagerProcess(Activity activity) {
        ComponentName mAdminName = new ComponentName(activity, SampleDeviceReceiver.class);
        DevicePolicyManager mDevicePolicyManager =
                (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (mDevicePolicyManager == null) {
            return false;
        } else {
            return mDevicePolicyManager.isAdminActive(mAdminName);
        }
    }
}