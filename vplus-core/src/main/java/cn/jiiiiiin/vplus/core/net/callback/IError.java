package cn.jiiiiiin.vplus.core.net.callback;

/**
 * @author jiiiiiin
 */

public interface IError {

    /**
     * 请求出错时候的回调
     * @param res
     */
    void onError(String res);
}
