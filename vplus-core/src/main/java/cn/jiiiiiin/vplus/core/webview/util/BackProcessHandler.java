package cn.jiiiiiin.vplus.core.webview.util;

import androidx.annotation.NonNull;
import android.webkit.WebView;

import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.util.ui.ViewUtil;
import cn.jiiiiiin.vplus.core.webview.AbstractWebViewDelegate;
import cn.jiiiiiin.vplus.core.webview.AbstractWebViewWrapperCommUIDelegate;
import cn.jiiiiiin.vplus.core.webview.WebViewDelegateImpl;
import lombok.val;

/**
 * 处理webview的返回
 * 处理webview容器fragment的返回
 *
 * @author jiiiiiin
 * @version 1.0
 */
public class BackProcessHandler {

    public static boolean onBack(@NonNull AbstractWebViewWrapperCommUIDelegate wrapperDelegate, AbstractWebViewDelegate.ILifeCycleListener lifeCycleListener) {
        wrapperDelegate.hideSoftInput();
        val activity = wrapperDelegate.getActivity();
        // 解决#205 java.lang.NullPointerException
        if (ViewUtil.activityIsLiving(activity)) {
            final WebViewDelegateImpl webDelegate = wrapperDelegate.getWebDelegate();
            WebView webView = null;
            try {
                webView = webDelegate.getWebView();
            } catch (Exception e) {
                LoggerProxy.e(e, "获取webview出错");
                return false;
            }
            if (webView == null || wrapperDelegate.ismWebViewIsDestroy()) {
                return false;
            }
            if (wrapperDelegate.isWebViewIsLoading()) {
                // https://github.com/YoKeyword/Fragmentation/issues/575
                // TODO 这个问题可能只能换内核了！ zhaojin
                WebView finalWebView = webView;
                ViewPlus.getHandler().postDelayed(() -> {
                    LoggerProxy.d("没有初始化网页完毕，但是webview已经实例化了，直接pop wrapperDelegate");
                    _simpleBack(wrapperDelegate, finalWebView);
                }, 500);
            } else if (wrapperDelegate.isShowErrorLocalPage()) {
                // 如果加载的是错误页面，那么就直接弹掉页面
                wrapperDelegate.setShowErrorLocalPage(false);
                wrapperDelegate.pop();
                // !如果是isShowErrorLocalPage那就说明是渲染了本地的错误页面，那么不需要判断webview是否可以goback
            } else {
                boolean lifeCycleListenerHandlerFlag = false;
                if (lifeCycleListener != null) {
                    // 不同的webvidew dalegate可以做不同的拦截处理
                    lifeCycleListenerHandlerFlag = lifeCycleListener.onWebViewDelegateBackPressedSupport();
                }
                if (!lifeCycleListenerHandlerFlag) {
                    AbstractWebViewWrapperCommUIDelegate.ITitleBarEventListener titleBarEventListener = wrapperDelegate.getTitleBarEventListener();
                    boolean isNotidy = false;
                    if (titleBarEventListener != null) {
                        // 目前只有加载我们自身的wrapperDelegate具有该listener
                        // 交给前端去处理
                        // 标识是否通知前端【ON_HEADER_BAR_TAP_BCK_LISTENER】去处理返回点击事件
                        isNotidy = titleBarEventListener.onBackBtnClick();
                    }
                    if (!isNotidy) {
                        _simpleBack(wrapperDelegate, webView);
                    }
                } else {
                    LoggerProxy.d("mLifeCycleListener 已经自己处理了返回事件");
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private static void _simpleBack(@NonNull AbstractWebViewWrapperCommUIDelegate wrapperDelegate, @NonNull WebView webView) {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            wrapperDelegate.pop();
        }
    }
}

