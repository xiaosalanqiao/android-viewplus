package cn.jiiiiiin.vplus.core.webview.loader;

import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

/**
 * 监听webview加载事件：
 * 1. 代理WebViewClient的
 * public void onPageStarted(WebView view, String url, Bitmap favicon)和
 * public void onPageFinished(WebView view, String url)
 *
 * @author jiiiiiin
 */

public interface IPageLoadListener {

    void onLoadStart(WebView view);

    void onLoadEnd(boolean isMainUiThreadCall);

    void onReceivedError(WebView view, int errorCode, String description, String failingUrl);

    void onReceivedHttpError(WebView webView, WebResourceRequest request, int stautsCode);

    void onProgressChanged(WebView view, int progress);

    /**
     * @param url
     * @return 返回true，则表示自己处理了该事件 {@link android.webkit.WebViewClient#shouldOverrideUrlLoading(WebView, WebResourceRequest)
     *             shouldOverrideUrlLoading(WebView, WebResourceRequest)}
     */
    boolean onShouldOverrideUrlLoading(String url);

    /**
     * 在拦截到webview加载资源出错之后，回调，如果返回true，则onReceivedError和onReceivedHttpError将会被调用
     * 无论是否是isForMainFrame
     */
    boolean isHandlerOnReceivedErrorRes(Uri uri);

    void onInterceptorNoSupportProtocol(String url);
}
