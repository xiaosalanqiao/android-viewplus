package cn.jiiiiiin.vplus.core.webview;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.Map;

import cn.jiiiiiin.vplus.core.R;
import cn.jiiiiiin.vplus.core.app.ConfigKeys;
import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.delegates.AbstractViewPlusDelegate;
import cn.jiiiiiin.vplus.core.exception.ViewPlusRuntimeException;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.util.network.HttpAdjectiveUtil;
import cn.jiiiiiin.vplus.core.webview.event.AbstractEvent;
import cn.jiiiiiin.vplus.core.webview.jsbridgehandler.JsBridgeCommHandler;
import cn.jiiiiiin.vplus.core.webview.loader.IPageLoadListener;
import cn.jiiiiiin.vplus.core.webview.util.WebViewUtil;

/**
 * @author jiiiiiin
 * @version 1.0
 */

public abstract class AbstractWebViewInteractiveDelegate extends AbstractViewPlusDelegate
        implements IPageLoadListener,
        AbstractWebViewDelegate.ILifeCycleListener {

    protected WebViewDelegateImpl mWebViewDelegate = null;
    protected Map<String, String> mHeaderParams = null;
    protected Map<String, String> mUrlParams = null;
    // 标识webview还在加载中，控制用户不能进行回退操作，已解决 https://link.zhihu.com/?target=https%3A//bugs.chromium.org/p/chromium/issues/detail%3Fid%3D539373 这个bug
    // https://www.zhihu.com/question/31316646 如果想更好解决这个问题应该只能期待换内核 @zhaojin
    protected boolean mWebViewIsLoading = true;
    protected boolean mWebViewIsDestroy = false;
    protected String mURL;

    protected AbstractWebViewDelegate.OnScrollChangeListener setOnScrollChangeListener() {
        return null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(setWebDelegateSwipeBackEnable());
    }

    protected boolean setWebDelegateSwipeBackEnable() {
        return true;
    }

    protected boolean isInitWebViewDelegateOnCreate() {
        return false;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        if (isInitWebViewDelegateOnCreate()) {
            mWebViewDelegate = initWebViewDelegateImpl();
            loadRootFragment(R.id.h5_container, mWebViewDelegate);
        }
    }

    protected abstract WebViewDelegateImpl initWebViewDelegateImpl();

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        if (!isInitWebViewDelegateOnCreate()) {
            mWebViewDelegate = initWebViewDelegateImpl();
            loadRootFragment(R.id.h5_container, mWebViewDelegate);
        }
    }

    public AbstractWebViewInteractiveDelegate setUrlParams(@NonNull Map<String, String> params) {
        this.mUrlParams = params;
        return this;
    }

    public Map<String, String> getUrlParams() {
        return mUrlParams;
    }

    public AbstractWebViewInteractiveDelegate setHeaderParams(@NonNull Map<String, String> params) {
        this.mHeaderParams = params;
        return this;
    }

    public Map<String, String> getHeaderParams() {
        return mHeaderParams;
    }

    @Override
    public void onLoadStart(WebView view) {
        mWebViewIsLoading = true;
    }

    @Override
    public void onLoadEnd(boolean isMainUiThreadCall) {
        mWebViewIsLoading = false;
    }


    @Override
    public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
        mWebViewIsLoading = false;
    }

    @Override
    public void onInterceptorNoSupportProtocol(String url) {
        LoggerProxy.e("拦截到不支持的协议 %s", url);
        mWebViewIsLoading = false;
    }

    @Override
    public boolean isHandlerOnReceivedErrorRes(Uri uri) {
        return false;
    }

    @Override
    public boolean onShouldOverrideUrlLoading(String url) {
        return checkAllowAccessUrls(url);
    }

    public WebViewDelegateImpl getWebDelegate() {
        return mWebViewDelegate;
    }

    public boolean isWebViewIsLoading() {
        return mWebViewIsLoading;
    }

    public boolean ismWebViewIsDestroy() {
        return mWebViewIsDestroy;
    }

    @Override
    public void onWebViewDelegateDestroy(WebView webView) {
        mWebViewIsLoading = false;
        mWebViewIsDestroy = true;
    }

    @Override
    public void onWebViewDelegateWebViewComponentInitialized(WebView webView) {
    }

    @Override
    public boolean onWebViewDelegateBackPressedSupport() {
        return false;
    }

    // ===== 工具函数 辅助编写和webview的交互
    public void safetyCallH5(String listener, String params) {
        if (!StringUtils.isTrimEmpty(listener)) {
            safetyUseWebView(webView -> {
                LoggerProxy.d("通知前端 listener [%s] params %s", listener, params);
                JsBridgeCommHandler.callJs(webView, listener.trim(), params);
            });
        } else {
            if (ViewPlus.IS_DEBUG()) {
                LoggerProxy.w("safetyCallH5 listener is empty err %s", params);
            }
        }
    }

    public interface ISafetyUseWebViewCallBack {
        void canDo(WebView webView);
    }

    public void safetyUseWebView(@NonNull ISafetyUseWebViewCallBack safetyUseWebViewCallBack) {
        if (mWebViewDelegate != null) {
            final WebView webView = mWebViewDelegate.getWebViewOrNullllll();
            if (webView != null && !mWebViewIsDestroy && !mWebViewIsLoading) {
                safetyUseWebViewCallBack.canDo(webView);
            }
        }
    }

    public void addEvent(String eventName, AbstractEvent event) {
        if (mWebViewDelegate != null) {
            mWebViewDelegate.addEvent(eventName, event);
        } else {
            throw new ViewPlusRuntimeException("mWebViewDelegate is null err");
        }
    }

    public String getURL() {
        return mURL;
    }

    public void updateURL(@NonNull String url) {
        this.mURL = url;
        mWebViewDelegate.updateURL(mURL);
        // TODO 等解决将刷新时间通知给前端来处理的问题再全面放开刷新功能
        // TODO 等待3.06上线
        // 这里的作用是判断如果当前访问的页面url和初始化时候的url相同那么就可以进行下拉刷新，否则就不可用
//        if (mURL.equals(mInitUrl)) {
//            mSmartRefreshLayout.setEnableRefresh(true);
//        } else {
//            mSmartRefreshLayout.setEnableRefresh(false);
//        }
    }

    /**
     * 检查待访问的资源的host是否在应用允许的白名单访问之内
     * <p>
     * 默认只支持访问第一次设置的url、ViewPlus配置的预制WEB_HOS其他T和debug模式下对本地的url的直接打开，其他url都会交给系统浏览器打开
     * <p>
     * 添加对{@link cn.jiiiiiin.vplus.core.app.Configurator#withAllowAccessUrlHosts(String[])}配置白名单的支持
     *
     * @param url
     * @return
     */
    protected boolean checkAllowAccessUrls(String url) {
        // TODO 和配置的白名单进行匹配
        if (HttpAdjectiveUtil.isEqualsHost(url, mURL)
                || HttpAdjectiveUtil.isEqualsHost(url, ViewPlus.getConfiguration(ConfigKeys.WEB_HOST))
                || (ViewPlus.IS_DEBUG() && URLUtil.isAssetUrl(url))
                || checkAllowHostWhiteList(url)) {
            updateURL(url);
            return false;
        } else {
            WebViewUtil.startSystemWebViewApp(_mActivity, url);
            return true;
        }
    }

    public static boolean checkAllowHostWhiteList(String url) {
        final String[] whitelist = ViewPlus.getConfiguration(ConfigKeys.ALLOW_ACCESS_URL_HOSTS);
        for (String allowUrl : whitelist) {
            if (HttpAdjectiveUtil.isEqualsHost(url, allowUrl)) {
                return true;
            }
        }
        return false;
    }

}
