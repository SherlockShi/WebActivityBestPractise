package com.sherlockshi.webactivitybestpractise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.just.agentweb.AgentWeb;

public class WebActivity extends AppCompatActivity {

    private static final String TITLE_KEY = "title";

    private AgentWeb mAgentWeb;

    private String mTitle;
    private String mUrl;

    public static Intent getIntent(Context context) {
        return getIntent(context, "");
    }

    public static Intent getIntent(Context context, String title) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(TITLE_KEY, title);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        mTitle = getIntent().getStringExtra(TITLE_KEY);

        initUrl();

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent((LinearLayout) findViewById(R.id.llyt_container), new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .createAgentWeb()
                .ready()
                .go(mUrl);
    }

    private void initUrl() {
        mUrl = "http://www.jd.com";
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }
}
