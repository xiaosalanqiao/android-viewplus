package cn.jiiiiiin.vplus.core.net.callback;

/**
 * 请求开始时候的环绕回调
 * @author jiiiiiin
 */

public interface IRequest {

    /**
     * 请求开始回调
     */
    void onRequestStart();

    /**
     * 请求结束时候的回调
     */
    void onRequestEnd();
}
