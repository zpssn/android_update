package com.tec.zenyan.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.tec.zenyan.R;
import com.tec.zenyan.common.AppMessage;

/**
 * Created by kisss on 2016/12/29.
 */

public class ShowAppMessageActivity extends Activity {

    private TextView mVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_app_message);

        setTitle(R.string.more_about);

        mVersion = (TextView) findViewById(R.id.version);
        mVersion.setText(AppMessage.getVersionName(ShowAppMessageActivity.this));
    }

}
