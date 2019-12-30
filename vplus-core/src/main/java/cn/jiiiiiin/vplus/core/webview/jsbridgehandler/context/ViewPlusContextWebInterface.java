package cn.jiiiiiin.vplus.core.webview.jsbridgehandler.context;

import androidx.annotation.NonNull;
import android.webkit.JavascriptInterface;

import com.blankj.utilcode.util.StringUtils;

import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.exception.ViewPlusRuntimeException;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.webview.AbstractWebViewDelegate;
import cn.jiiiiiin.vplus.core.webview.event.AbstractEvent;
import cn.jiiiiiin.vplus.core.webview.event.model.EventParams;
import cn.jiiiiiin.vplus.core.webview.event.model.EventResData;
import cn.jiiiiiin.vplus.core.webview.jsbridgehandler.JsBridgeCommHandler;
import cn.jiiiiiin.vplus.core.webview.jsbridgehandler.exception.JSBridgeException;

/**
 * 客户端暴露给前端的代理接口
 * @author jiiiiiin
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public final class ViewPlusContextWebInterface {

    private final AbstractWebViewDelegate DELEGATE;
    private final IJsBridgeHandler JSBRIDGE_RES_HANDLER;

    public interface IJsBridgeHandler {

        /**
         * 在接收到h5请求之后调用，如果应用需要拦截处理特殊业务，请直接返回处理结果，否则返回null或者空字符串
         */
        String onJsCallInterceptor(EventParams eventParams) throws JSBridgeException;

        /**
         * 将返回给h5的交互消息进行格式化
         */
        String onRespH5(EventResData eventResData, EventParams eventParams);
    }

    private ViewPlusContextWebInterface(AbstractWebViewDelegate delegate, IJsBridgeHandler jsBridgeResHandler) {
        this.DELEGATE = delegate;
        this.JSBRIDGE_RES_HANDLER = jsBridgeResHandler;
    }

    public static ViewPlusContextWebInterface newInstance(@NonNull AbstractWebViewDelegate delegate, @NonNull IJsBridgeHandler jsBridgeResHandler) {
        return new ViewPlusContextWebInterface(delegate, jsBridgeResHandler);
    }


    /**
     * 暴露给web端的唯一公共接口
     */
    @JavascriptInterface
    public String event(String params) {
        if (ViewPlus.IS_DEBUG()) {
            LoggerProxy.i("客户端收到js[context]调用消息 -> %s %s", params, DELEGATE != null ? DELEGATE.getTopDelegate() : "DELEGATE 为null！！！！");
        }
        String res, event = "", action = "";
        EventParams eventParams = null;
        try {
            eventParams = EventParams.newInstance(params);
            // ！可能会抛出JSBridgeException
            final String appRes = JSBRIDGE_RES_HANDLER.onJsCallInterceptor(eventParams);
            if (StringUtils.isEmpty(appRes)) {
                event = eventParams.getEvent();
                action = eventParams.getAction();
                final EventResData eventResData = JsBridgeCommHandler.handleJsCall(DELEGATE, eventParams, DELEGATE.getEventManager());
                res = JSBRIDGE_RES_HANDLER.onRespH5(eventResData, eventParams);
            } else {
                res = appRes;
            }
        } catch (JSBridgeException e) {
            LoggerProxy.e(e, "调用桥接Event出错JSBridgeException");
            res = JSBRIDGE_RES_HANDLER.onRespH5(EventResData.error(e.getCode(), e.getMessage()), eventParams);
        } catch (ViewPlusRuntimeException e) {
            final String errCode = e.getCode();
            switch (errCode) {
                case AbstractEvent.GET_WEBVIEW_RETURN_NULL_MUST_STOP_JSBRIDGE:
                    // TODO 前端要针对AbstractEvent.GET_WEBVIEW_RETURN_NULL_MUST_STOP_JSBRIDGE 停止和webview的交互
                    // !如果是webview 对应的视图先被销毁了，那么就不返回错误，避免出现前端遇到错误反复调用dialog的问题
                    res = JSBRIDGE_RES_HANDLER.onRespH5(EventResData.success(), eventParams);
                    break;
                default:
                    res = JSBRIDGE_RES_HANDLER.onRespH5(EventResData.error(e.getCode(), e.getMessage()), eventParams);
            }
        } catch (Exception e) {
            LoggerProxy.e(e, "桥接调用出现意料之外的错误！");
            res = JSBRIDGE_RES_HANDLER.onRespH5(EventResData.error("def_un_catch_error", String.format("处理本次请求出现错误[%s]", e.getMessage())), eventParams);
        }
        if (ViewPlus.IS_DEBUG()) {
            LoggerProxy.i("客户端返回js[context]处理消息 -> %s return: %s", event + "-" + action, res);
        }
        return res;
    }

}
