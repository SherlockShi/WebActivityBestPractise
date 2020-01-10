package com.sherlockshi.webactivitybestpractise;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Author:      SherlockShi
 * Email:       sherlock_shi@163.com
 * Date:        2020-01-09 15:27
 * Description:
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 一、基础功能
        findViewById(R.id.btn1_basic_function).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(WebActivity.getIntent(MainActivity.this, "http://www.jd.com"));
//                startActivity(WebActivity.getIntent(MainActivity.this, "http://www.jd.com", "一、基础功能"));
            }
        });

        // 二、JsInterface
        findViewById(R.id.btn2_jsinterface).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(WebActivity.getIntent(MainActivity.this, "file:///android_asset/function2_JavascriptInterface.html"));
            }
        });
    }
}
