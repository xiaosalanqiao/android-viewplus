package cn.jiiiiiin.vplus.core.net.callback;

import com.alibaba.fastjson.JSONObject;

import cn.jiiiiiin.vplus.core.exception.ViewPlusException;
import cn.jiiiiiin.vplus.core.webview.jsbridgehandler.exception.JSBridgeException;

/**
 * @author jiiiiiin
 */

public interface ISuccess {

    /**
     * 请求成功时候的回调
     * @param response
     */
    void onSuccess(JSONObject response);
}
