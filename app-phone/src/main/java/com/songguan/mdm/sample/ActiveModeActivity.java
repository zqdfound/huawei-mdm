/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 */

package com.songguan.mdm.sample;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.huawei.android.app.admin.DeviceControlManager;
import com.songguan.mdm.sample.R;

/**
 * The ActiveModeActivity for this Sample
 *
 * @author huawei mdm
 * @since 2020-10-15
 */
public class ActiveModeActivity extends AppCompatActivity implements View.OnClickListener {
    private DeviceControlManager mDeviceControlManager = null;
    private ComponentName mAdminName = null;
    private EditText editDelayTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activemode_layout);
        mDeviceControlManager = new DeviceControlManager();
        mAdminName = new ComponentName(this, SampleDeviceReceiver.class);
        initView();
    }

    private void initView() {
        Button userActiveBtn = (Button) findViewById(R.id.btn_user_active_admin);
        Button forceActiveBtn = (Button) findViewById(R.id.btn_force_active_admin);
        Button delayActiveBtn = (Button) findViewById(R.id.btn_delay_active_admin);
        editDelayTime = (EditText) findViewById(R.id.edit_input);
        userActiveBtn.setOnClickListener(this);
        forceActiveBtn.setOnClickListener(this);
        delayActiveBtn.setOnClickListener(this);
        setToolbar();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_user_active_admin:
                userActiveAdmin();
                break;
            case R.id.btn_force_active_admin:
                forceActiveAdmin();
                break;
            case R.id.btn_delay_active_admin:
                delayDeactiveAdmin();
                break;
            default:
                break;
        }
    }

    private void userActiveAdmin() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
        ActiveModeActivity.this.startActivity(intent);
    }

    private void forceActiveAdmin() {
        try {
            mDeviceControlManager.setForcedActiveDeviceAdmin(mAdminName, ActiveModeActivity.this);
        } catch (NoSuchMethodError error) {
            Toast.makeText(ActiveModeActivity.this, getString(R.string.not_support), Toast.LENGTH_SHORT).show();
        } catch (SecurityException | IllegalArgumentException e) {
            Toast.makeText(ActiveModeActivity.this, getString(R.string.force_active_error), Toast.LENGTH_LONG).show();
        }
    }

    private void delayDeactiveAdmin() {
        try {
            String hourTime = editDelayTime.getText().toString();
            int hour = Integer.parseInt(hourTime);
            mDeviceControlManager.setDelayDeactiveDeviceAdmin(mAdminName, hour, ActiveModeActivity.this);
        } catch (NoSuchMethodError error) {
            Toast.makeText(ActiveModeActivity.this, getString(R.string.not_support), Toast.LENGTH_SHORT).show();
        } catch (SecurityException | IllegalArgumentException e) {
            Toast.makeText(ActiveModeActivity.this, getString(R.string.delay_deactive_error), Toast.LENGTH_LONG).show();
        }
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.active_mode_toolbar);
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
}
