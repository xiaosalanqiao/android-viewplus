package cn.jiiiiiin.vplus.core.webview.event;

import android.view.View;

import cn.jiiiiiin.vplus.core.webview.event.model.EventParams;
import cn.jiiiiiin.vplus.core.webview.event.model.EventResData;
import cn.jiiiiiin.vplus.core.webview.jsbridgehandler.exception.JSBridgeException;

/**
 * @author Created by jiiiiiin
 */

public interface IEvent {

    EventResData execute(EventParams params) throws JSBridgeException;
    String execute2(EventParams params) throws JSBridgeException;
    void onWebDelegateDestroy();
    void onWebDelegatePause();
    void onWebViewTouchedListener(View webView);
}
