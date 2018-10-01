package com.csii.mobilebank.jsbridge;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;

import cn.jiiiiiin.vplus.core.ui.loader.LoaderCreatorProxy;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.util.ui.ViewUtil;
import cn.jiiiiiin.vplus.core.webview.AbstractWebViewWrapperCommUIDelegate;
import cn.jiiiiiin.vplus.core.webview.event.AbstractEvent;
import cn.jiiiiiin.vplus.core.webview.event.BaseEvent;
import cn.jiiiiiin.vplus.core.webview.event.model.EventParams;
import cn.jiiiiiin.vplus.core.webview.event.model.EventResData;
import cn.jiiiiiin.vplus.core.webview.jsbridgehandler.exception.JSBridgeException;

import static cn.jiiiiiin.vplus.core.ui.loader.LoaderCreatorProxy.showLoading;

/**
 * @author jiiiiiin
 * @version 1.0
 */

@SuppressWarnings({"AlibabaClassNamingShouldBeCamel"})
public class UIEvent extends BaseEvent {

    private static final String TOAST = "toast";

    public static AbstractEvent newInstance() {
        return new UIEvent();
    }

    @Override
    protected String[] getSupportActions() {
        return new String[]{TOAST};
    }

    @Override
    protected EventResData doAction(EventParams eventParams) throws JSBridgeException {
        final String action = eventParams.getAction();
        final JSONObject params = eventParams.getParams();
        final String listener = eventParams.getListener();
        EventResData eventResData = null;
        switch (action) {
            case TOAST:
                eventResData = _toast(params, listener);
                break;
            default:
        }
        return eventResData;
    }


    private EventResData _toast(final JSONObject params, final String listener) {
        final int duration = params.getInteger("duration");
        final String msg = params.getString("msg");
        switch (duration) {
            case Toast.LENGTH_LONG:
                ToastUtils.showLong(msg);
                // 模拟异步执行其他事情
                new Thread(() -> {
                    safetyCallH5(listener, String.format("{\"toastTask\":\"%s\"}", "模拟异步执行其他事情完成，通知前端"));
                }).start();
                break;
            case Toast.LENGTH_SHORT:
                ToastUtils.showShort(msg);
                break;
            default:
        }
        return EventResData.success();
    }


}
