package cn.jiiiiiin.vplus.core.webview;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cn.jiiiiiin.vplus.core.app.ConfigKeys;
import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.delegates.BaseDelegate;
import cn.jiiiiiin.vplus.core.exception.ViewPlusException;
import cn.jiiiiiin.vplus.core.exception.ViewPlusRuntimeException;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.webview.chromeclient.WebChromeClientImpl;
import cn.jiiiiiin.vplus.core.webview.client.WebViewClientImpl;
import cn.jiiiiiin.vplus.core.webview.loader.IPageLoadListener;
import cn.jiiiiiin.vplus.core.webview.route.RouteKeys;
import cn.jiiiiiin.vplus.core.webview.route.Router;
import cn.jiiiiiin.vplus.core.webview.util.WebViewUtil;


/**
 * @author Created by jiiiiiin
 */

public class WebViewDelegateImpl extends WebViewLongClickHandlerDelegate {

    private IPageLoadListener mIPageLoadListener = null;
    private boolean isSyncCookied = false;

    public boolean isSyncCookied() {
        return isSyncCookied;
    }

    public void setSyncCookied(boolean syncCookied) {
        isSyncCookied = syncCookied;
    }

    public static WebViewDelegateImpl newInstance(String url, boolean isUseCacheWebViewImpl, boolean needSwipeBack, boolean needSyncCookie) {
        final Bundle args = new Bundle();
        args.putString(RouteKeys.URL.name(), url);
        args.putBoolean(RouteKeys.IS_USE_CACHE_WEB_VIEW_IMPL.name(), isUseCacheWebViewImpl);
        args.putBoolean(RouteKeys.NEED_SWIPE_BACK.name(), needSwipeBack);
        args.putBoolean(RouteKeys.NEED_SYNC_COOKIE.name(), needSyncCookie);
        final WebViewDelegateImpl delegate = new WebViewDelegateImpl();
        delegate.setArguments(args);
        return delegate;
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return false;
    }

    @Override
    public Object setLayout() {
        try {
            return getWebView();
        } catch (ViewPlusException e) {
            LoggerProxy.e("setLayout getWebView ERR");
            return null;
        }
    }

    public AbstractWebViewDelegate setPageLoadListener(IPageLoadListener listener) {
        this.mIPageLoadListener = listener;
        return this;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {
        super.onBindView(savedInstanceState, rootView);
        String url = null;
        try {
            url = getUrl();
        } catch (ViewPlusException e) {
            LoggerProxy.e(e, "onBindView getUrl err");
        }
        if (mNeedSyncCookie) {
            try {
                final String cookie = ViewPlus.getConfiguration(ConfigKeys.SESSION_ID);
                LoggerProxy.w("webview 的delegate 同步全局配置中的cookie %s", cookie);
                if (cookie != null && url != null) {
                    isSyncCookied = WebViewUtil.syncCookie(url, cookie);
                }
            } catch (ViewPlusRuntimeException e) {
                isSyncCookied = false;
                LoggerProxy.e("同步 sync Cookie err %s", e.getMessage());
            }
        }
        // 用原生的方式模拟Web跳转并进行页面加载
        LoggerProxy.d("加载页面 %s %s %s", url, mHeaderParams, mUrlParams);
        ViewPlus.getConfigurator().withWebViewCurrentLoadUrl(url);
        Router.getInstance().loadPage(mWebView, url, mHeaderParams, mUrlParams);
    }

    @Override
    public IWebViewInitializer setInitializer() {
        return this;
    }

    @Override
    public WebView initWebView(WebView webView) {
        return WebViewInitializer.createWebView(webView);
    }

    @Override
    public WebViewClient initWebViewClient() {
        return new WebViewClientImpl(this, mIPageLoadListener);
    }

    @Override
    public WebChromeClient initWebChromeClient() {
        return new WebChromeClientImpl(_mActivity, mIPageLoadListener);
    }

    // 依赖wrapper完成pop
    @Override
    protected Class<? extends BaseDelegate> getRootClazz() {
        return null;
    }
}
