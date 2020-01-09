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

```JAVA
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
