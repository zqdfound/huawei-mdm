/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2021. All rights reserved.
 */

package com.songguan.mdm.sample;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.songguan.mdm.sample.R;

/**
 * The LicenseActivity for this Sample
 *
 * @author huawei mdm
 * @since 2019-10-23
 */
public class LicenseActivity extends Activity {
    private static final String LICENSE_FILE = "huawei_software_license.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.license_layout);
        Button acceptBtn = (Button) findViewById(R.id.cancelBtn);
        acceptBtn.setOnClickListener(view -> finish());
        TextView licenseText = (TextView) findViewById(R.id.license_content);
        String content = Utils.getStringFromHtmlFile(this, Utils.getMatchedFile(LICENSE_FILE));
        licenseText.setText(Html.fromHtml(content));
    }
}
