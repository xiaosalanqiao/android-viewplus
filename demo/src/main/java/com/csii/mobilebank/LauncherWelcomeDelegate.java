package com.csii.mobilebank;

import com.csii.mobilebank.jsbridge.UIEvent;

import cn.jiiiiiin.vplus.core.delegates.BaseDelegate;
import cn.jiiiiiin.vplus.core.webview.AbstractWebViewWrapperCommUIDelegate;
import cn.jiiiiiin.vplus.core.webview.WebViewDelegateImpl;
import cn.jiiiiiin.vplus.core.webview.event.IEventManager;
import cn.jiiiiiin.vplus.core.webview.event.StandAloneEventManager;
import cn.jiiiiiin.vplus.core.webview.event.model.EventParams;
import cn.jiiiiiin.vplus.core.webview.event.model.EventResData;
import cn.jiiiiiin.vplus.core.webview.jsbridgehandler.context.ViewPlusContextWebInterface;
import cn.jiiiiiin.vplus.core.webview.jsbridgehandler.exception.JSBridgeException;

/**
 * 集成{@link AbstractWebViewWrapperCommUIDelegate}提供的layout，需要有一个`android:id="@+id/llc_root_container"`的根android.support.v7.widget.LinearLayoutCompat容器
 * 参考`delegate_comm_h5_wrapper_layout.xml`布局；
 *
 * @auther Created by jiiiiiin on 2018/10/1.
 */
public class LauncherWelcomeDelegate extends AbstractWebViewWrapperCommUIDelegate implements ViewPlusContextWebInterface.IJsBridgeHandler {
    @Override
    public Object setLayout() {
        return R.layout.delegate_comm_h5_wrapper_layout;
    }

    public static LauncherWelcomeDelegate newInstance() {
        return new LauncherWelcomeDelegate();
    }

    @Override
    protected Class<? extends BaseDelegate> getRootClazz() {
        return LauncherWelcomeDelegate.class;
    }

    @Override
    protected WebViewDelegateImpl initWebViewDelegateImpl() {
        final WebViewDelegateImpl webDelegate = WebViewDelegateImpl.newInstance("jsbridge-context.html", false, false, true);

        final IEventManager manager = StandAloneEventManager.newInstance()
                .addEvent("UIEvent", UIEvent.newInstance());
        webDelegate
                .setPageLoadListener(this)
                .setEventManager(manager)
                .setWrapperDelegate(this)
                //.setUrlParams(urlParams)
                //.setHeaderParams(headerParams)
                .setJavascriptInterface(ViewPlusContextWebInterface.newInstance(webDelegate, this), "ViewPlus");
        return webDelegate;
    }

    @Override
    public String onJsCallInterceptor(EventParams eventParams) throws JSBridgeException {
        // 前端调用客户端参数前处理器
        return null;
    }

    @Override
    public String onRespH5(EventResData eventResData, EventParams eventParams) {
        // 前端调用客户端参数后处理器
        return eventResData.toJson();
    }
}
