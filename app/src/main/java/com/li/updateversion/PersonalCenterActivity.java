package com.li.updateversion;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.li.updateversion.util.CheckUpdataUtil;
import com.li.updateversion.util.Util;

/**
 * 个人中心或者设置页面
 * 在这个页面可以添加当前应用版本好展示，用于手动点击更新版本
 */
public class PersonalCenterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.center_activity);
        RelativeLayout version_update_layout = findViewById(R.id.version_update_layout);
        version_update_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckUpdataUtil checkUpdataUtil = new CheckUpdataUtil(PersonalCenterActivity.this);
                checkUpdataUtil.IsUpdate();
            }
        });
        //展示当前版本名称
        TextView local_version_name = findViewById(R.id.local_version_name);
        local_version_name.setText(Util.getVersionName(PersonalCenterActivity.this));

    }
}
