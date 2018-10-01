package cn.jiiiiiin.vplus.core.webview.event;

import android.os.Handler;
import android.support.annotation.NonNull;

import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.delegates.AbstractViewPlusDelegate;
import cn.jiiiiiin.vplus.core.exception.ViewPlusRuntimeException;
import cn.jiiiiiin.vplus.core.webview.AbstractWebViewDelegate;
import cn.jiiiiiin.vplus.core.webview.AbstractWebViewWrapperCommUIDelegate;
import cn.jiiiiiin.vplus.core.webview.event.model.EventParams;


/**
 * 映射前端和native的通讯消息实体
 *
 * @author Created by jiiiiiin
 */

public abstract class AbstractEvent implements IEvent {

    public static final String GET_WEBVIEW_RETURN_NULL_MUST_STOP_JSBRIDGE = "get_webview_return_null_must_stop_jsbridge";
    protected AbstractWebViewDelegate mDelegate = null;
    private String mUrl = null;
    private EventParams mEventParams;
    private IEventManager mEventManager;
    /**
     * Handler 尽力声明为static类型，以避免内存泄露
     */
    protected static final Handler HANDLER = ViewPlus.getHandler();

//    public WebView getWebView() {
//        WebView webView = mDelegate.getWebView();
//        if (webView == null) {
//            //! 很特殊
//            throw new ViewPlusRuntimeException("webview尚未初始化完毕/已经销毁了，应该终止和h5的一切交互", GET_WEBVIEW_RETURN_NULL_MUST_STOP_JSBRIDGE);
//        } else {
//            return webView;
//        }
//    }

    public AbstractWebViewDelegate getDelegate() {
        return mDelegate;
    }

    protected AbstractWebViewWrapperCommUIDelegate getWrapperDelegate() {
        final AbstractViewPlusDelegate abstractViewPlusDelegate = mDelegate.getParentDelegate();
        if (abstractViewPlusDelegate == null) {
            throw new ViewPlusRuntimeException("WEBVIEW DELEGATE NOT SET TOPDELEGATE(AbstractWebViewWrapperCommUIDelegate) ERROR");
        } else {
            return (AbstractWebViewWrapperCommUIDelegate) abstractViewPlusDelegate;
        }
    }

    public AbstractEvent setDelegate(AbstractWebViewDelegate delegate) {
        this.mDelegate = delegate;
        return this;
    }

    public String getUrl() {
        return mUrl;
    }

    public AbstractEvent setUrl(String url) {
        this.mUrl = url;
        return this;
    }

    public void setEventParams(@NonNull EventParams mEventParams) {
        this.mEventParams = mEventParams;
    }

    public EventParams getEventParams() {
        return mEventParams;
    }

    public void setEventManager(IEventManager mEventManager) {
        this.mEventManager = mEventManager;
    }

    public IEventManager getEventManager() {
        return mEventManager;
    }
}
