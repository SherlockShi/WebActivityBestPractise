package com.sherlockshi.webactivitybestpractise;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;

public class WebActivity extends AppCompatActivity {

    private static final String URL_KEY     = "url";
    private static final String TITLE_KEY   = "title";

    private LinearLayout llytToolBarLeft;
    private LinearLayout llytToolBarClose;
    private TextView tvToolBarTitle;

    private AgentWeb mAgentWeb;

    private BridgeWebView mBridgeWebView;

    private String mTitle;
    private String mUrl;

    public static Intent getIntent(Context context, String url) {
        return getIntent(context, url, "");
    }

    public static Intent getIntent(Context context, String url, String title) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(URL_KEY, url);
        intent.putExtra(TITLE_KEY, title);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        // 清空缓存
        AgentWebConfig.clearDiskCache(this);

        initView();

        mTitle = getIntent().getStringExtra(TITLE_KEY);
        tvToolBarTitle.setText(mTitle);

        mUrl = getIntent().getStringExtra(URL_KEY);

        mBridgeWebView = new BridgeWebView(WebActivity.this);
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent((LinearLayout) findViewById(R.id.llyt_container), new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(getWebViewClient())
                .setWebView(mBridgeWebView)
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)//打开其他应用时，弹窗咨询用户是否前往其他应用
                .interceptUnkownUrl() //拦截找不到相关页面的Scheme
                .createAgentWeb()
                .ready()
                .go(mUrl);

        setWebSetting();

        mAgentWeb.getJsInterfaceHolder().addJavaObject("android", new AndroidInterface(mAgentWeb,this));

        mBridgeWebView.registerHandler("YHJavaScriptCallHandler", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.d("JsBridge123", data);

                JsBridgeBaseEntity entity = GsonUtils.fromJson(data, JsBridgeBaseEntity.class);
                if (entity != null) {
                    // 退出登录
                    if (TextUtils.equals(entity.getMethod(), "logout")) {
                        ToastUtils.showShort("账号被登录，需配置跳转到登录界面");
//                        LoginHelper.getInstance().logout();
//                        startActivity(new Intent(WebActivity.this, LoginActivity.class));
//                        finish();
                    }
                }
            }
        });
    }

    private void initView() {
        llytToolBarLeft = findViewById(R.id.llyt_tool_bar_left);
        llytToolBarClose = findViewById(R.id.llyt_tool_bar_close);
        tvToolBarTitle = findViewById(R.id.tv_tool_bar_title);

        llytToolBarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        llytToolBarClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setWebSetting() {
        // H5 用于判断是否为 APP 的依据：COM_MSTPAY
        WebSettings webSettings = mAgentWeb.getAgentWebSettings().getWebSettings();
        String ua = webSettings.getUserAgentString();
        ua = ua + ";COM_MSTPAY(RYT)/" + AppUtils.getAppVersionCode();
        webSettings.setUserAgentString(ua);
    }

    private com.just.agentweb.WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (!TextUtils.isEmpty(title)) {
                tvToolBarTitle.setText(title);
            }
        }

        @Override
        public void onPermissionRequest(PermissionRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                request.grant(request.getResources());
            }
        }
    };

    private WebViewClient getWebViewClient(){
        return new WebViewClient() {
            BridgeWebViewClient mBridgeWebViewClient = new BridgeWebViewClient(mBridgeWebView);

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return mBridgeWebViewClient.shouldOverrideUrlLoading(view, request);  //兼容高版本，必须设置
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return mBridgeWebViewClient.shouldOverrideUrlLoading(view, url);    //兼容低版本，必须设置
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mBridgeWebViewClient.onPageStarted(view, url, favicon);  //必须设置
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mBridgeWebViewClient.onPageFinished(view, url); //必须设置

                if (mAgentWeb.getWebCreator().getWebView().canGoBack()) {
                    llytToolBarClose.setVisibility(View.VISIBLE);
                } else {
                    llytToolBarClose.setVisibility(View.GONE);
                }
            }
        };
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
    protected void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (mAgentWeb.back()) {
            mAgentWeb.getWebCreator().getWebView().goBack();
        } else {
            super.onBackPressed();
        }
    }
}
