package cn.jiiiiiin.vplus.core.webview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;


import com.blankj.utilcode.util.StringUtils;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.delegates.AbstractViewPlusDelegate;
import cn.jiiiiiin.vplus.core.exception.ViewPlusException;
import cn.jiiiiiin.vplus.core.exception.ViewPlusRuntimeException;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.webview.event.AbstractEvent;
import cn.jiiiiiin.vplus.core.webview.event.BaseEvent;
import cn.jiiiiiin.vplus.core.webview.event.IEventManager;
import cn.jiiiiiin.vplus.core.webview.route.RouteKeys;
import cn.jiiiiiin.vplus.core.webview.route.Router;
import cn.jiiiiiin.vplus.core.webview.util.WebViewUtil;

/**
 * @author jiiiiiin
 */

public abstract class AbstractWebViewDelegate extends AbstractViewPlusDelegate implements IWebViewInitializer {

    protected WebView mWebView = null;
    /**
     * 使用软引用存有WebView，避免内存泄露
     */
    private final ReferenceQueue<WebView> WEB_VIEW_QUEUE = new ReferenceQueue<>();
    private String mUrl = null;
    /**
     * 类WebViewFragment中的对应变量，标识mWebView的可用性
     */
    private boolean mIsWebViewAvailable = false;
    private AbstractWebViewWrapperCommUIDelegate mWrapperDelegate = null;
    private AbstractViewPlusDelegate mTopDelegate = null;
    private Map<String, Object> JS_BRIDGE_MAP = new HashMap<>();
    private ILifeCycleListener mLifeCycleListener;
    private IEventManager mEventManager = null;
    private boolean mIsUseCacheWebViewImpl = false;
    protected Map<String, String> mHeaderParams = null;
    protected Map<String, String> mUrlParams = null;
    private boolean mNeedSwipeBack = true;
    protected boolean mNeedSyncCookie = false;

    public interface OnScrollChangeListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    private OnScrollChangeListener mOnScrollChangeListener;

    public AbstractWebViewDelegate setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.mOnScrollChangeListener = onScrollChangeListener;
        return this;
    }

    public interface ILifeCycleListener {
        /**
         * 通知应用delegate将会被销毁
         */
        void onWebViewDelegateDestroy(WebView webView);

        void onWebViewDelegateWebViewComponentInitialized(WebView webView);

        boolean onWebViewDelegateBackPressedSupport();
    }


    public AbstractWebViewDelegate() {
    }

    /**
     * 提供webview初始化使用
     *
     * @return
     */
    public abstract IWebViewInitializer setInitializer();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        assert args != null;
        mUrl = args.getString(RouteKeys.URL.name());
        mIsUseCacheWebViewImpl = args.getBoolean(RouteKeys.IS_USE_CACHE_WEB_VIEW_IMPL.name());
        mNeedSwipeBack = args.getBoolean(RouteKeys.NEED_SWIPE_BACK.name());
        mNeedSyncCookie = args.getBoolean(RouteKeys.NEED_SYNC_COOKIE.name());
        setSwipeBackEnable(mNeedSwipeBack);
        initWebView();
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        // 检查：避免重复初始化、内存泄露
        if (mWebView != null) {
            WebViewUtil.destroyWebView(mWebView);
        }
        final IWebViewInitializer initializer = setInitializer();
        if (initializer != null) {
            WeakReference<WebView> webViewWeakReference = null;
            // 直接 new WebView 并传入 application context 代替在 XML 里面声明以防止 activity 引用被滥用，能解决90+%的 WebView 内存泄漏。
//            if (mIsUseCacheWebViewImpl) {
//                if (mOnScrollChangeListener != null) {
//                    webViewWeakReference = new WeakReference<>(new CacheWebView(getContext()) {
//                        @Override
//                        public void onScrollChanged(int l, int t, int oldl, int oldt) {
//                            super.onScrollChanged(l, t, oldl, oldt);
//                            mOnScrollChangeListener.onScrollChanged(l, t, oldl, oldt);
//                        }
//                    }, WEB_VIEW_QUEUE);
//                } else {
//                    webViewWeakReference = new WeakReference<>(new CacheWebView(getContext()), WEB_VIEW_QUEUE);
//                }
//                LoggerProxy.dd("初始化CacheWebView");
//            } else {
                if (mOnScrollChangeListener != null) {
                    webViewWeakReference = new WeakReference<>(new WebView(getContext()) {
                        @Override
                        public void onScrollChanged(int l, int t, int oldl, int oldt) {
                            super.onScrollChanged(l, t, oldl, oldt);
                            if (mOnScrollChangeListener != null) {
                                mOnScrollChangeListener.onScrollChanged(l, t, oldl, oldt);
                            }
                        }
                    }, WEB_VIEW_QUEUE);
                } else {
                    webViewWeakReference = new WeakReference<>(new WebView(getContext()));
                }
//                LoggerProxy.dd("初始化原生WebView");
//            }
            mWebView = webViewWeakReference.get();
            // 初始化
            mWebView = initializer.initWebView(mWebView);
            mWebView.setWebViewClient(initializer.initWebViewClient());
            mWebView.setWebChromeClient(initializer.initWebChromeClient());
            if (JS_BRIDGE_MAP != null && !JS_BRIDGE_MAP.isEmpty()) {
                for (Map.Entry<String, Object> entry : JS_BRIDGE_MAP.entrySet()) {
                    mWebView.addJavascriptInterface(entry.getValue(), entry.getKey());
                }
            }
            if (mLifeCycleListener != null) {
                mLifeCycleListener.onWebViewDelegateWebViewComponentInitialized(mWebView);
            }
            ViewPlus.getConfigurator().withWebView(mWebView);
            // 初始化完毕
            mIsWebViewAvailable = true;
            LoggerProxy.d("webview delegate初始化完毕");
        } else {
            throw new NullPointerException("WEBVIEW_INITIALIZER_IS_NULL");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        mWebView.setOnTouchListener((v, event) -> {
            mEventManager.onWebViewTouchedListener(v);
            return false;
        });
    }

    /**
     * @see WebView#addJavascriptInterface(Object, String)
     */
    public AbstractWebViewDelegate setJavascriptInterface(@NonNull Object object, @NonNull String name) {
        if (JS_BRIDGE_MAP.isEmpty()) {
            JS_BRIDGE_MAP.put(name, object);
        } else {
            throw new ViewPlusRuntimeException("目前仅仅支持一个WebView中设置一个上下文对象！");
        }
        return this;
    }

    public AbstractWebViewDelegate setEventManager(IEventManager eventManager) {
        this.mEventManager = eventManager;
        return this;
    }

    public IEventManager getEventManager() {
        return mEventManager;
    }

    /**
     * 预制event和动态event必须区分，以防止注册到event中的listener“被破坏”
     * @param eventName
     * @param event
     */
    public void addEvent(String eventName, AbstractEvent event) {
        if (mEventManager.getEvent(eventName) == null) {
            mEventManager.addEvent(eventName, event);
        }
    }

    //    public <T extends BaseEvent> T getEvent(Class<T> tClass) {
//        return this.mEventManager.getEvent(tClass.getSimpleName());
//    }

    /**
     * 设置嵌套（需要执行跳转）的delegate
     * https://coding.imooc.com/lesson/116.html#mid=5739
     */
    public AbstractWebViewDelegate setWrapperDelegate(@NonNull AbstractWebViewWrapperCommUIDelegate delegate) {
        mWrapperDelegate = delegate;
        return this;
    }

    /**
     * 设置嵌套（需要执行跳转）的delegate
     * https://coding.imooc.com/lesson/116.html#mid=5739
     */
    public AbstractWebViewDelegate setTopDelegate(@NonNull AbstractViewPlusDelegate delegate) {
        mTopDelegate = delegate;
        return this;
    }

    public AbstractViewPlusDelegate getTopDelegate() {
        if (mTopDelegate == null) {
            mTopDelegate = this;
        }
        return mTopDelegate;
    }

    public WebView getWebView() throws ViewPlusException {
        if (mWebView == null) {
            throw new ViewPlusException("WEBVIEW_IS_NULL");
        }
        return mIsWebViewAvailable ? mWebView : null;
    }

    public WebView getWebViewOrNullllll() {
        WebView webView = null;
        try {
            // ！获取webview有可能为空，在delegate被销毁之后
            webView = getWebView();
        } catch (Exception e) {
            LoggerProxy.e(e, "获取webview失败");
        }
        return webView;
    }

    public String getUrl() throws ViewPlusException {
        if (StringUtils.isTrimEmpty(mUrl)) {
            throw new ViewPlusException("URL_IS_NULL");
        }
        return mUrl;
    }

    public void updateURL(@NonNull String url) {
        mUrl = url;
    }

    @Override
    public void onPause() {
        if (mWebView != null) {
            // ！存在SPA就不要这样优化了，适得其反
            // mWebView.getSettings().setJavaScriptEnabled(false);
            // ！！！pauseTimers不能乱用，会导致spa应用卡顿
            // 当应用程序被切换到后台我们使用了webview， 这个方法不仅仅针对当前的webview而是全局的全应用程序的webview，它会暂停所有webview的layout，parsing，javascripttimer。降低CPU功耗。
            // mWebView.pauseTimers();
            mWebView.onPause();
        }
        if (mEventManager != null) {
            mEventManager.onWebDelegatePause();
        }
        super.onPause();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onResume() {
        if (mWebView != null) {
            // ！存在SPA就不要这样优化了，适得其反
            // mWebView.getSettings().setJavaScriptEnabled(true);
            // 恢复pauseTimers时的动作。
            // mWebView.resumeTimers();
            mWebView.onResume();
        }
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        mIsWebViewAvailable = false;
        super.onDestroyView();
    }

    @SuppressWarnings("AlibabaRemoveCommentedCode")
    @Override
    public void onDestroy() {
        hideSoftInput();
        if (mLifeCycleListener != null) {
            mLifeCycleListener.onWebViewDelegateDestroy(mWebView);
        }
        if (mEventManager != null) {
            mEventManager.onWebDelegateDestroy();
        }
        if (mWebView != null) {
            WebViewUtil.destroyWebView(mWebView);
        }
        super.onDestroy();
    }

    public AbstractWebViewDelegate setLifeCycleListener(ILifeCycleListener mLifeCycleListener) {
        this.mLifeCycleListener = mLifeCycleListener;
        return this;
    }

    public AbstractWebViewDelegate setHeaderParams(Map<String, String> headerParams) {
        this.mHeaderParams = headerParams;
        return this;
    }

    public AbstractWebViewDelegate setUrlParams(Map<String, String> urlParams) {
        this.mUrlParams = urlParams;
        return this;
    }

    public boolean isWebViewAvailable() {
        return mIsWebViewAvailable;
    }

    public void refresh() {
        final WebView webView = getWebViewOrNullllll();
        if (webView != null) {
            Router.getInstance().loadPage(webView, mUrl, mHeaderParams, mUrlParams);
        } else {
            // https://work.bugtags.com/apps/1598731013063315/issues/1603404686171189/tags/1603404687214900?types=3&flags=0&versions=1600310568035606&page=1
            // 应该是
            LoggerProxy.w("refresh() webView is null err!");
        }
    }
}
