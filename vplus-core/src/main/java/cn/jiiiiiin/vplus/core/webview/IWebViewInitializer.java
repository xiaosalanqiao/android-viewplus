package cn.jiiiiiin.vplus.core.webview;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * WebView相关初始化接口
 * 使用：
 * mWebView = initializer.initWebView(mWebView);
 * mWebView.setWebViewClient(initializer.initWebViewClient());
 * mWebView.setWebChromeClient(initializer.initWebChromeClient());
 *
 * @author jiiiiiin
 */

public interface IWebViewInitializer {

    WebView initWebView(WebView webView);

    WebViewClient initWebViewClient();

    WebChromeClient initWebChromeClient();
}
