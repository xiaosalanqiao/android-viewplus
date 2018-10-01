package cn.jiiiiiin.vplus.core.webview.jsbridgehandler.protocol;

import android.webkit.WebView;

import com.blankj.utilcode.util.StringUtils;

import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.dict.Err;
import cn.jiiiiiin.vplus.core.webview.AbstractWebViewDelegate;
import cn.jiiiiiin.vplus.core.webview.event.IEventManager;
import cn.jiiiiiin.vplus.core.webview.event.model.EventParams;
import cn.jiiiiiin.vplus.core.webview.event.model.EventResData;
import cn.jiiiiiin.vplus.core.webview.jsbridgehandler.JsBridgeCommHandler;
import cn.jiiiiiin.vplus.core.webview.jsbridgehandler.context.ViewPlusContextWebInterface;
import cn.jiiiiiin.vplus.core.webview.jsbridgehandler.exception.JSBridgeException;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;


/**
 * @author Created by jiiiiiin on 2017/7/13.
 * 参考： https://juejin.im/post/58a037df86b599006b3fade4
 */
public class ProtocolBridgeHandler {

    /**
     * 处理webview的shouldOverrideUrlLoading中关于js协议相关内容
     *
     * @param webDelegate
     * @param infos
     * @return
     */
    public static void event(AbstractWebViewDelegate webDelegate, UriInfo infos, IEventManager eventManager, ViewPlusContextWebInterface.IJsBridgeHandler jsBridgeResHandler) {
        if (ViewPlus.IS_DEBUG()) {
            LoggerProxy.d("===默认处理bs协议： %s ", infos);
        }
        final EventParams eventParams = infos.getEventParams();
        final String callBack = eventParams.getCallback();
        String resData = "";
        try {
            EventResData eventResData = JsBridgeCommHandler.handleJsCall(webDelegate, infos.getEventParams(), eventManager);
            resData = jsBridgeResHandler.onRespH5(eventResData, eventParams);
        } catch (JSBridgeException e) {
            LoggerProxy.e(e, "ProtocolBridgeHandler处理js请求出错");
            resData = jsBridgeResHandler.onRespH5(EventResData.error(Err.PROTOCAL_HANDLE_JS_CALL, String.format(Err.PROTOCAL_HANDLE_JS_CALL_MSG, e.getMessage())), eventParams);
        }
        // 通知
        if (callBack != null && !StringUtils.isEmpty(callBack)) {
            final WebView webView = webDelegate.getWebViewOrNullllll();
            JsBridgeCommHandler.callJs(webView, callBack, resData, value -> {
                // 一般js调用客户端客户端通知其回调函数，这个回调函数都不会再有返回值，如果出现这种情况，那就是特例目前不予处理！
                LoggerProxy.w("调用 %s 得到的返回值：%s", callBack, value);
            });
        }
    }
}
