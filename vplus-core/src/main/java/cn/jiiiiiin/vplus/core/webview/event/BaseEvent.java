package cn.jiiiiiin.vplus.core.webview.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebView;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.StringUtils;

import java.util.Arrays;

import cn.jiiiiiin.vplus.core.exception.ViewPlusRuntimeException;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.webview.AbstractWebViewWrapperCommUIDelegate;
import cn.jiiiiiin.vplus.core.webview.event.model.EventParams;
import cn.jiiiiiin.vplus.core.webview.event.model.EventResData;
import cn.jiiiiiin.vplus.core.webview.jsbridgehandler.exception.JSBridgeException;

/**
 * 优先使用execute+doAction
 *
 * @author jiiiiiin
 * @version 1.0
 */

public abstract class BaseEvent extends AbstractEvent {

    @Override
    public String execute2(EventParams params) throws JSBridgeException {
        final String action = params.getAction();
        if (StringUtils.isEmpty(action)) {
            throw new JSBridgeException("调用桥接接口，没有传递action错误", "call_jsbridge_action_is_empty_on_execute2");
        } else {
            try {
                checkAction(params.getAction());
                return doAction2(params);
            } catch (JSBridgeException e) {
                throw e;
            }
        }
    }

    protected String doAction2(EventParams params) throws JSBridgeException {
        return null;
    }

    @Override
    public EventResData execute(EventParams params) throws JSBridgeException {
        final String action = params.getAction();
        if (StringUtils.isEmpty(action)) {
            throw new JSBridgeException("调用桥接接口，没有传递action错误", "call_jsbridge_action_is_empty_on_execute");
        } else {
            try {
                checkAction(params.getAction());
                return doAction(params);
            } catch (JSBridgeException e) {
                throw e;
            }
        }
    }

    /**
     * 检查event模块是否支持该action
     */
    protected void checkAction(String action) throws JSBridgeException {
        String[] actions = getSupportActions();
        if (!Arrays.asList(actions).contains(action)) {
            throw new JSBridgeException(String.format("当前[%s]事件组不支持[%s]事件", getClass().getSimpleName(), action), "un_support_action_in_event_obj");
        }
    }

    protected String[] getSupportActions() {
        return new String[0];
    }

    /**
     * 执行交互
     *
     * @param eventParams
     * @return
     */
    protected EventResData doAction(final EventParams eventParams) throws JSBridgeException {
        return null;
    }

    /**
     * 当前方法是在ui线程被调用
     */
    @Override
    public void onWebDelegateDestroy() {
    }

    @Override
    public void onWebDelegatePause() {
    }

    @Override
    public void onWebViewTouchedListener(View webView) {
    }

    protected boolean parseBool(final JSONObject params, final String key, final boolean def) {
        Boolean res = params.getBoolean(key);
        return res == null ? def : res;
    }

    protected Integer parseInteger(JSONObject params, String key, Integer def) {
        final Integer res = params.getInteger(key);
        return res == null ? def : res;
    }

    protected String parseStr(JSONObject params, String key, String def) {
        final String res = params.getString(key);
        return StringUtils.isEmpty(res) ? def : res;
    }

    public @Nullable WebView getWebViewOrNullllll() {
        // ！获取webview有可能为空，在delFinanceWebViewDelegateegate被销毁之后
        return mDelegate.getWebViewOrNullllll();
    }


    // ===== 工具函数 辅助编写和webview的交互

    public void safetyCallH5(String listener, String params) {
        getWrapperDelegate().safetyCallH5(listener, params);
    }

    public void safetyUseWebView(@NonNull AbstractWebViewWrapperCommUIDelegate.ISafetyUseWebViewCallBack safetyUseWebViewCallBack) {
        getWrapperDelegate().safetyUseWebView(safetyUseWebViewCallBack);
    }
}
