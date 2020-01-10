package com.sherlockshi.webactivitybestpractise;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;

import com.blankj.utilcode.util.ToastUtils;
import com.just.agentweb.AgentWeb;

/**
 * Author:      SherlockShi
 * Email:       sherlock_shi@163.com
 * Date:        2019-11-29 15:32
 * Description:
 */
public class AndroidInterface {

    private Handler deliver = new Handler(Looper.getMainLooper());
    private AgentWeb agent;
    private Activity mActivity;

    public AndroidInterface(AgentWeb agent, Activity activity) {
        this.agent = agent;
        this.mActivity = activity;
    }

    @JavascriptInterface
    public void logout() {
        deliver.post(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showShort("账号被登录，需配置跳转到登录界面");
//                LoginHelper.getInstance().logout();
//                mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
//                mActivity.finish();
            }
        });
    }

    @JavascriptInterface
    public void showToast(final String msg) {
        deliver.post(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showShort(msg);
            }
        });
    }
}
