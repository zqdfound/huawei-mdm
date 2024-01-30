/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.mdm.sample;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.BadParcelableException;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.huawei.android.app.admin.DeviceControlManager;
import com.huawei.hem.license.HemLicenseManager;
import com.huawei.hem.license.HemLicenseStatusListener;

/**
 * MdmActivateLicenseActivity This activity is used to activate the license and device manager. Noted that MDM
 * capability can be used only after the license and device manager are activated.
 *
 * @author huawei mdm
 * @since 2022-07-13
 */
public class ActiveActivity extends AppCompatActivity {
    private static final String TAG = "ActivateLicenseActivity";

    private static final String USER_SETUP_COMPLETE = "user_setup_complete";

    private static final int LICENSE_ACTIVATE_SUCCESS = 2000;

    private static final int LICENSE_ACTIVATE_NOT_SUCCESS = 3000;

    // HemLicenseManager is used to activate or deactivate the license
    private HemLicenseManager hemInstance;

    private TextView textLicenseStatus;

    private TextView textAdviceManagerStatus;

    private Button buttonActiveLicense;

    private Button buttonActiveDeviceManager;

    private Button buttonExitLicense;

    private SharedPreferenceUtil sharedPreferenceUtil;

    private Activity mActivity;

    private boolean isOobe = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active);
        hemInstance = HemLicenseManager.getInstance(this);
        sharedPreferenceUtil = new SharedPreferenceUtil(this);
        mActivity = this;

        setStatusListener();
        initIds();
        dealBeLaunched();
        updateComponentStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBtnStatus(false, Utils.isActiveDeviceManagerProcess(mActivity));
    }

    private void setStatusListener() {
        hemInstance.setStatusListener(new MyHemLicenseStatusListener());
    }

    private void initIds() {
        buttonActiveLicense = findViewById(R.id.active);
        buttonActiveDeviceManager = findViewById(R.id.active_device_manager);
        buttonExitLicense = findViewById(R.id.exit_license);
        textLicenseStatus = findViewById(R.id.license_status);
        textAdviceManagerStatus = findViewById(R.id.device_manager_status);

        buttonActiveLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferenceUtil.getActiveLicenseStatus()) {
                    // deactive License
                    hemInstance.deActiveLicense();
                } else {
                    // active License
                    hemInstance.activeLicense();
                }
            }
        });

        buttonActiveDeviceManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isActiveDeviceManagerProcess(mActivity)) {
                    deactiveDeviceManagerProcess();
                } else {
                    activeDeviceManagerProcess();
                }
            }
        });

        buttonExitLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNavigataNext();
            }
        });
    }

    /**
     * Executed when the DPC(this Activity) is launched
     */
    private void dealBeLaunched() {
        Intent intent = getIntent();
        try {
            if (intent != null && isDuringSetupWizard(mActivity)
                && intent.hasExtra(DevicePolicyManager.EXTRA_PROVISIONING_SERIAL_NUMBER)
                && intent.hasExtra(DevicePolicyManager.EXTRA_PROVISIONING_ADMIN_EXTRAS_BUNDLE)) {
                // Obtains device SN and cloud-defined data.
                String sn = intent.getStringExtra(DevicePolicyManager.EXTRA_PROVISIONING_SERIAL_NUMBER);
                PersistableBundle bundle =
                    intent.getParcelableExtra(DevicePolicyManager.EXTRA_PROVISIONING_ADMIN_EXTRAS_BUNDLE);

                isOobe = true;
                hemInstance.activeLicense();
                Log.i(TAG, "current activity is launched during the setup wizard flow");
            }
        } catch (BadParcelableException e) {
            Log.e(TAG, "illegal class name in intent");
        }
    }

    private void onNavigataNext() {
        setResult(RESULT_OK);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("ISOOBE", true);
        startActivity(intent);
        finish();
    }

    /**
     * active device manager
     */
    private void activeDeviceManagerProcess() {
        Intent intent = new Intent(this, ActiveModeActivity.class);
        startActivity(intent);
    }

    /**
     * deactive device manager
     */
    private void deactiveDeviceManagerProcess() {
        DeviceControlManager mDeviceControlManager = new DeviceControlManager();
        DevicePolicyManager mDevicePolicyManager =
            (DevicePolicyManager) mActivity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mAdminName = new ComponentName(this, SampleDeviceReceiver.class);
        try {
            if (mDevicePolicyManager.isDeviceOwnerApp(this.getPackageName())) {
                mDevicePolicyManager.clearDeviceOwnerApp(this.getPackageName());
                Toast.makeText(this, R.string.text_device_manager_deactivated, Toast.LENGTH_LONG).show();
                updateBtnStatus(false, false);
            } else {
                if (mDeviceControlManager.removeActiveDeviceAdmin(mAdminName)) {
                    Toast.makeText(this, R.string.text_device_manager_deactivated, Toast.LENGTH_LONG).show();
                    updateBtnStatus(false, false);
                }
            }
        } catch (NoSuchMethodError error) {
            Toast.makeText(getApplicationContext(), getString(R.string.not_support), Toast.LENGTH_SHORT).show();
        } catch (SecurityException securityException) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_permission), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Returns whether we are running during the setup wizard flow, this does not include the deferred setup case.
     *
     * @param context context
     * @return boolean true if running during the setup wizard flow
     */
    public boolean isDuringSetupWizard(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), USER_SETUP_COMPLETE, 0) == 0;
    }

    private void updateComponentStatus() {
        updateBtnStatus(true, sharedPreferenceUtil.getActiveLicenseStatus());
        if (!isOobe) {
            setToolbar();
            buttonExitLicense.setVisibility(View.GONE);
        }
    }

    private void updateBtnStatus(boolean isLicenseStatus, boolean isActive) {
        if (isLicenseStatus) {
            if (isActive) {
                buttonActiveLicense.setText(R.string.text_call_deactive);
                textLicenseStatus.setText(R.string.text_active);
            } else {
                buttonActiveLicense.setText(R.string.text_call_active);
                textLicenseStatus.setText(R.string.text_not_active);
            }
        } else {
            if (isActive) {
                buttonActiveDeviceManager.setText(R.string.text_deactive_device_manager);
                textAdviceManagerStatus.setText(R.string.text_active);
            } else {
                buttonActiveDeviceManager.setText(R.string.text_active_device_manager);
                textAdviceManagerStatus.setText(R.string.text_not_active);
            }
        }
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.active_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private class MyHemLicenseStatusListener implements HemLicenseStatusListener {
        @Override
        public void onStatus(final int errorCode, final String msg) {
            if (isOobe) {
                if (errorCode == LICENSE_ACTIVATE_SUCCESS && Utils.isActiveDeviceManagerProcess(mActivity)) {
                    sharedPreferenceUtil.saveActiveLicenseStatus(true);
                    updateBtnStatus(true, true);
                    onNavigataNext();
                } else {
                    isOobe = false;
                }
            } else {
                String returnMsg = getString(R.string.text_return_code) + errorCode + System.lineSeparator()
                    + getString(R.string.text_result_description) + msg;
                Toast.makeText(mActivity, returnMsg, Toast.LENGTH_LONG).show();
                if (errorCode == LICENSE_ACTIVATE_SUCCESS) {
                    sharedPreferenceUtil.saveActiveLicenseStatus(true);
                    updateBtnStatus(true, true);
                } else if (errorCode == LICENSE_ACTIVATE_NOT_SUCCESS) {
                    sharedPreferenceUtil.saveActiveLicenseStatus(false);
                    updateBtnStatus(true, false);
                } else {
                    Log.v(TAG, "Other errorCode do not need to handle.");
                }
            }
        }
    }
}