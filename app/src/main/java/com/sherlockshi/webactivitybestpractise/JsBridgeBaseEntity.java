package com.sherlockshi.webactivitybestpractise;

/**
 * Author:      SherlockShi
 * Email:       sherlock_shi@163.com
 * Date:        2019-12-03 16:11
 * Description:
 */
public class JsBridgeBaseEntity<T> {

    /**
     * method : loginout
     * param : {}
     *
     * {"method":"loginout","param":"","callback":""}
     */

    private String method;
    private T param;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public T getParam() {
        return param;
    }

    public void setParam(T param) {
        this.param = param;
    }
}
