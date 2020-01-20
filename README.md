# WebActivity 最佳实践
本实践基于 [AgentWeb](https://github.com/Justson/AgentWeb)

# 一、基础功能
### 1、引入 AgentWeb
参考 [AgentWeb - 引入](https://github.com/Justson/AgentWeb#%E5%BC%95%E5%85%A5)

```groovy
api 'com.just.agentweb:agentweb:4.1.2'
```

### 2、基础用法
参考 [AgentWeb - 基础用法](https://github.com/Justson/AgentWeb#%E5%9F%BA%E7%A1%80%E7%94%A8%E6%B3%95)

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    ...

    mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent((LinearLayout) findViewById(R.id.llyt_container), new LinearLayout.LayoutParams(-1, -1))
            .useDefaultIndicator()
            .createAgentWeb()
            .ready()
            .go("http://www.jd.com");
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
```

# 二、JsInterface
参考 [AgentWeb - Javascript 调 Java ?](https://github.com/Justson/AgentWeb#javascript-%E8%B0%83-java-)

### 1、添加 AndroidInterface 类
### 2、定义原生方法
```java
// 不带参
@JavascriptInterface
public void logout() {

    deliver.post(new Runnable() {
        @Override
        public void run() {
            // do your work
        }
    });
}

// 带参
@JavascriptInterface
public void pay(String data) {

    deliver.post(new Runnable() {
        @Override
        public void run() {
            // do your work
        }
    });
}
```

### 3、配置
```java
// Javascript 调用方式：window.android.logout();
mAgentWeb.getJsInterfaceHolder().addJavaObject("android", new AndroidInterface(mAgentWeb,this));
```

# 三、JsBridge
### 1、引入 JsBridge
参考 [JsBridge - JitPack.io](https://github.com/lzyzsd/JsBridge#jitpackio)

```groovy
repositories {
    // ...
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation 'com.github.lzyzsd:jsbridge:1.0.4'
}
```

### 2、JsBridge 配合 AgentWeb 使用

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ...
    
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
            JsBridgeBaseEntity entity = GsonUtils.fromJson(data, JsBridgeBaseEntity.class);
            if (entity != null) {
                // 退出登录
                if (TextUtils.equals(entity.getMethod(), "logout")) {
//                    LoginHelper.getInstance().logout();
//                    startActivity(new Intent(WebActivity.this, LoginActivity.class));
//                    finish();
                }
            }
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
        }

    };
}
```
### 3、报错 ERR_UNKNOWN_URL_SCHEME
[AgentWeb调用JsBridge采坑记录](http://www.appblog.cn/2019/10/27/AgentWeb%E8%B0%83%E7%94%A8JsBridge%E9%87%87%E5%9D%91%E8%AE%B0%E5%BD%95/)

# 四、一些小功能

### 1、拨打电话

```java
private WebViewClient getWebViewClient(){
    return new WebViewClient() {
        BridgeWebViewClient mBridgeWebViewClient = new BridgeWebViewClient(mBridgeWebView);

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (WebActivity.this.shouldOverrideUrlLoading(url)) {
                return true;
            }
            return mBridgeWebViewClient.shouldOverrideUrlLoading(view, request);  //兼容高版本，必须设置
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (WebActivity.this.shouldOverrideUrlLoading(url)) {
                return true;
            }
            return mBridgeWebViewClient.shouldOverrideUrlLoading(view, url);    //兼容低版本，必须设置
        }

        ...
    };
}

private boolean shouldOverrideUrlLoading(String url) {
    if (TextUtils.equals(Uri.parse(url).getScheme(), "tel")) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
        return true;
    }
    return false;
}
```

### 2、打开应用商店搜索包名对应的应用

```java
private WebViewClient getWebViewClient(){
    return new WebViewClient() {
        BridgeWebViewClient mBridgeWebViewClient = new BridgeWebViewClient(mBridgeWebView);

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (WebActivity.this.shouldOverrideUrlLoading(url)) {
                return true;
            }
            return mBridgeWebViewClient.shouldOverrideUrlLoading(view, request);  //兼容高版本，必须设置
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (WebActivity.this.shouldOverrideUrlLoading(url)) {
                return true;
            }
            return mBridgeWebViewClient.shouldOverrideUrlLoading(view, url);    //兼容低版本，必须设置
        }

        ...
    };
}

private boolean shouldOverrideUrlLoading(String url) {
    if (TextUtils.equals(Uri.parse(url).getScheme(), "market")) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            // 解决Bug：模拟器未装应用商店，导致 ActivityNotFoundException 崩溃
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }
    return false;
}
```

### 3、拍照/选择文件

##### 3.1 引入 filechooser

```groovy
api 'com.just.agentweb:filechooser:4.1.2'
```

##### 3.2 清单文件添加权限

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

##### 3.3 代码动态请求权限

```java
private com.just.agentweb.WebChromeClient mWebChromeClient = new WebChromeClient() {
    ...

    @Override
    public void onPermissionRequest(PermissionRequest request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            request.grant(request.getResources());
        }
    }
};
```

# 附录
### 1、JsBridge 方法汇总

| 序号 | 方法名 | 功能描述 |
| :-: | --- | --- |
| 1 | logout | 退出登录 |
| 2 | hideToolbar | 隐藏标题栏 |
| 3 | showToolbar | 显示标题栏 |