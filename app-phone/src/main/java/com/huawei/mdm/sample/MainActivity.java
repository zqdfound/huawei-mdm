/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package com.huawei.mdm.sample;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.BadParcelableException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.android.app.admin.DeviceApplicationManager;
import com.huawei.android.app.admin.DeviceHwSystemManager;
import com.huawei.android.app.admin.DeviceRestrictionManager;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * The MainActivity for this Sample
 *
 * @author huawei mdm
 * @since 2019-10-23
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final String PACKAGE_NAME = "com.huawei.mdm.sample";
    private static final int REQUEST_ENABLE = 1;

    private DeviceRestrictionManager mDeviceRestrictionManager = null;
    private DeviceApplicationManager mDeviceApplicationManager = null;
    private DeviceHwSystemManager mDeviceHwSystemManager = null;
    private ComponentName mAdminName = null;
    private ArrayList<String> packageNames = null;
    private TextView mWifiStatusText;
    private TextView mKeepAliveStatusText;
    private TextView mScreenCaptureStatusText;
    private Activity mActivity = null;
    private SharedPreferenceUtil sharedPreferenceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDeviceRestrictionManager = new DeviceRestrictionManager();
        mDeviceApplicationManager = new DeviceApplicationManager();
        mDeviceHwSystemManager = new DeviceHwSystemManager();
        mAdminName = new ComponentName(this, SampleDeviceReceiver.class);
        packageNames = new ArrayList<String>();
        packageNames.add(PACKAGE_NAME);
        mActivity = this;
        sharedPreferenceUtil = new SharedPreferenceUtil(this);

        initSampleView();
        updateState();
        initJPush();
        new SampleEula(this).show();
    }

    private void initJPush() {
        Log.i("极光","极光初始化");
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

    private void initWifiView() {
        mWifiStatusText = (TextView) findViewById(R.id.wifiStateTxt);
        Button wifiDisableBtn = (Button) findViewById(R.id.disableWifi);
        Button wifiEnableBtn = (Button) findViewById(R.id.enableWifi);
        wifiDisableBtn.setOnClickListener(new SampleOnClickListener());
        wifiEnableBtn.setOnClickListener(new SampleOnClickListener());
    }

    private void initKeepAliveView() {
        mKeepAliveStatusText = (TextView) findViewById(R.id.keepAliveStateTxt);
        Button keepAliveBtn = (Button) findViewById(R.id.keepAlive);
        Button cancelAliveBtn = (Button) findViewById(R.id.cancelAlive);
        keepAliveBtn.setOnClickListener(new SampleOnClickListener());
        cancelAliveBtn.setOnClickListener(new SampleOnClickListener());
    }

    private void initCaptureView() {
        mScreenCaptureStatusText = (TextView) findViewById(R.id.screenCaptureStateTxt);
        Button captureDisableBtn = (Button) findViewById(R.id.disableScreenCapture);
        Button captureEnableBtn = (Button) findViewById(R.id.enableScreenCapture);
        captureDisableBtn.setOnClickListener(new SampleOnClickListener());
        captureEnableBtn.setOnClickListener(new SampleOnClickListener());
    }

    private void initEnterActiveLicenseActivity() {
        Button activeBtn = (Button) findViewById(R.id.btn_active);
        activeBtn.setOnClickListener(new SampleOnClickListener());
    }

    private void initCloseButton() {
        Button closeBtn = (Button) findViewById(R.id.btn_close);
        closeBtn.setOnClickListener(new SampleOnClickListener());
        try {
            Intent intent = getIntent();
            if (intent != null && !intent.getBooleanExtra("ISOOBE", false)) {
                closeBtn.setVisibility(View.GONE);
            }
        } catch (BadParcelableException e) {
            Log.e(TAG, "illegal class name in intent");
        }
    }

    private void initSampleView() {
        initWifiView();
        initKeepAliveView();
        initCaptureView();
        initEnterActiveLicenseActivity();
        initCloseButton();
    }

    private void updateState() {
        if (!Utils.isActiveDeviceManagerProcess(this)) {
            mWifiStatusText.setText(getString(R.string.state_not_actived));
            mScreenCaptureStatusText.setText(getString(R.string.state_not_actived));
            mKeepAliveStatusText.setText(getString(R.string.state_not_actived));
            return;
        }
        if (!sharedPreferenceUtil.getActiveLicenseStatus()) {
            mWifiStatusText.setText(getString(R.string.license_not_actived));
            mScreenCaptureStatusText.setText(getString(R.string.license_not_actived));
            mKeepAliveStatusText.setText(getString(R.string.license_not_actived));
            return;
        }

        boolean isWifiDisabled = false;
        boolean isScreenCaptureDisabled = false;
        boolean isKeptAlive = false;
        try {
            isWifiDisabled = mDeviceRestrictionManager.isWifiDisabled(mAdminName);
            isScreenCaptureDisabled = mDeviceRestrictionManager.isScreenCaptureDisabled(mAdminName);
            isKeptAlive = isPackageKeptAlive(mAdminName, PACKAGE_NAME);
        } catch (NoSuchMethodError error) {
            Toast.makeText(getApplicationContext(), getString(R.string.not_support), Toast.LENGTH_SHORT).show();
        } catch (SecurityException securityException) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_permission), Toast.LENGTH_SHORT).show();
        }
        if (isWifiDisabled) {
            mWifiStatusText.setText(R.string.state_restricted);
        } else {
            mWifiStatusText.setText(getString(R.string.state_nomal));
        }
        if (isScreenCaptureDisabled) {
            mScreenCaptureStatusText.setText(R.string.state_restricted);
        } else {
            mScreenCaptureStatusText.setText(getString(R.string.state_nomal));
        }
        if (isKeptAlive) {
            mKeepAliveStatusText.setText(R.string.state_keep_alive);
        } else {
            mKeepAliveStatusText.setText(getString(R.string.state_cancel_alive));
        }
    }

    private boolean isPackageKeptAlive(ComponentName admin, String pacakgeName) {
        boolean isKeptAlive = false;
        List<String> persistentApps = mDeviceApplicationManager.getPersistentApp(admin);
        ArrayList<String> superTrustApps = mDeviceHwSystemManager.getSuperTrustListForHwSystemManger(admin);
        if (persistentApps != null && persistentApps.contains(pacakgeName)
                && superTrustApps != null && superTrustApps.contains(pacakgeName)) {
            isKeptAlive = true;
        }
        return isKeptAlive;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateState();
        super.onActivityResult(requestCode, resultCode, data);
    }
    //示例调用禁止API
    private class SampleOnClickListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            try {
                switch (view.getId()) {
                    case R.id.disableWifi:
                        mDeviceRestrictionManager.setWifiDisabled(mAdminName, true);
                        break;
                    case R.id.enableWifi:
                        mDeviceRestrictionManager.setWifiDisabled(mAdminName, false);
                        break;
                    case R.id.disableScreenCapture:
                        mDeviceRestrictionManager.setScreenCaptureDisabled(mAdminName, true);
                        break;
                    case R.id.enableScreenCapture:
                        mDeviceRestrictionManager.setScreenCaptureDisabled(mAdminName, false);
                        break;
                    case R.id.keepAlive:
                        mDeviceApplicationManager.addPersistentApp(mAdminName, packageNames);
                        mDeviceHwSystemManager.setSuperTrustListForHwSystemManger(mAdminName, packageNames);
                        break;
                    case R.id.cancelAlive:
                        mDeviceApplicationManager.removePersistentApp(mAdminName, packageNames);
                        mDeviceHwSystemManager.removeSuperTrustListForHwSystemManger(mAdminName, packageNames);
                        break;
                    case R.id.btn_active:
                        Intent intent = new Intent(mActivity, ActiveActivity.class);
                        startActivityForResult(intent, REQUEST_ENABLE);
                        break;
                    case R.id.btn_close:
                        finish();
                        break;
                    default:
                        break;
                }
            } catch (NoSuchMethodError error) {
                Toast.makeText(getApplicationContext(), getString(R.string.not_support), Toast.LENGTH_SHORT).show();
            } catch (SecurityException securityException) {
                Toast.makeText(getApplicationContext(), getString(R.string.no_permission), Toast.LENGTH_SHORT).show();
            }
            updateState();
        }
    }
}