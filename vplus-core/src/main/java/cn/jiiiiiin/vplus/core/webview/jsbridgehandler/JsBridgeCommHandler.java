package cn.jiiiiiin.vplus.core.webview.jsbridgehandler;

import android.os.Handler;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;

import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.exception.ViewPlusException;
import cn.jiiiiiin.vplus.core.exception.ViewPlusRuntimeException;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.webview.AbstractWebViewDelegate;
import cn.jiiiiiin.vplus.core.webview.event.AbstractEvent;
import cn.jiiiiiin.vplus.core.webview.event.IEventManager;
import cn.jiiiiiin.vplus.core.webview.event.model.EventParams;
import cn.jiiiiiin.vplus.core.webview.event.model.EventResData;
import cn.jiiiiiin.vplus.core.webview.jsbridgehandler.exception.JSBridgeException;

/**
 * @author jiiiiiin
 * @version 1.0
 */

public final class JsBridgeCommHandler {

    /**
     * Handler 尽力声明为static类型，以避免内存泄露
     */
    protected static final Handler HANDLER = ViewPlus.getHandler();

    /**
     * 调用内部event处理接口
     * 推荐：如果没有，则如果前端的调用方式是上下文的方式，那么可以直接得到返回结果，无需通过回调进行
     * 如果是协议方式且前端需要客户端处理完毕之后返回结果，必须通过回调，通过调用command声明的回调函数传递下去
     */
    public static EventResData handleJsCall(AbstractWebViewDelegate webDelegate, EventParams eventParams, IEventManager eventManager) throws JSBridgeException {
        if (eventParams == null) {
            throw new JSBridgeException("交互事件参数不能为空", "event_params_is_null");
        }
        final String eventKey = eventParams.getEvent();
        final AbstractEvent event = eventManager.createEvent(eventKey);
        if (event != null) {
            event.setEventParams(eventParams);
            event.setDelegate(webDelegate);
            try {
                event.setUrl(webDelegate.getUrl());
            } catch (ViewPlusException e) {
                LoggerProxy.e(e, "event.setUrl(webDelegate.getUrl()); err");
            }
            event.setEventManager(eventManager);
            // execute 会抛出JSBridgeException异常
            EventResData eventResData = event.execute(eventParams);
            if (eventResData == null) {
                throw new JSBridgeException(String.format("[%s]事件处理函数没有返回处理结果", eventKey), "event_not_resp_code_on_handlejscall");
            } else {
                return eventResData;
            }
        } else {
            throw new JSBridgeException(String.format("事件管理器中未注册[%s]事件组", eventKey), "event_undfound_in_eventmanager_on_handlejscall");
        }
    }

    /**
     * 调用js，无参数，无返回值方法
     */
    public static void callJs(WebView webView, String funcName) {
        // 传统的调用方式: webView.post(() -> webView.loadUrl("javascript:" + funcName + "()"));
        callJs(webView, funcName, "", null);
    }

    public static void callJs(WebView webView, String funcName, String params) {
        callJs(webView, funcName, params, null);
    }

    public static void callJs(WebView webView, String funcName, String params, ValueCallback<String> resultCallback) {
        if (StringUtils.isTrimEmpty(funcName)) {
            LoggerProxy.e("callJs调用警告，待调用的前端函数名为空");
            if (ViewPlus.IS_DEBUG()) {
                throw new ViewPlusRuntimeException("callJs funcName is empty");
            } else {
                ToastUtils.showLong("出错了[调用前端的方法名为空]");
            }
        } else if (webView == null) {
            LoggerProxy.e("callJs调用警告，webView为null，funcName: %s params: %s \n这种情况只出现在当前的webview delegate被销毁，那么是不需要再执行其注册的一些监听函数", funcName, params);
        } else {
            LoggerProxy.d("调用网页中的js -> funcName: %s , params: %s", funcName, params);
            try {
                HANDLER.post(() -> webView.evaluateJavascript(funcName + "('" + params + "');", resultCallback));
            } catch (Exception e){
                LoggerProxy.e(e, "webView.evaluateJavascript 出现错误");
            }
        }
    }
}
