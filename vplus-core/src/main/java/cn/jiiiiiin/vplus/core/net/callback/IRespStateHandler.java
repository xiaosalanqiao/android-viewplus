package cn.jiiiiiin.vplus.core.net.callback;

import cn.jiiiiiin.vplus.core.exception.ViewPlusException;

/**
 * @author jiiiiiin
 * @version 1.0
 */

public interface IRespStateHandler {

    /**
     * 校验服务器返回结果是否是业务级别的成功
     *
     * @param res 服务端返回的数据
     * @return true 标识成功 反之为失败
     */
    boolean onRespCheckStateIsOk(String res) throws ViewPlusException;

    /**
     * 处理错误消息，在校验服务器返回结果是错误的情况下
     *
     * @param res
     */
    void onRespErrHandler(String res);
}
