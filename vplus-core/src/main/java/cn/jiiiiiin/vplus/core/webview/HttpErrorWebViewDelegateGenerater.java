package cn.jiiiiiin.vplus.core.webview;

import android.webkit.WebView;

import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.webview.route.Router;

import static android.webkit.WebViewClient.ERROR_CONNECT;
import static android.webkit.WebViewClient.ERROR_HOST_LOOKUP;
import static android.webkit.WebViewClient.ERROR_TIMEOUT;

/**
 * @author jiiiiiin
 * @version 1.0
 */
@Deprecated
public class HttpErrorWebViewDelegateGenerater {

    public static final int ERROR_NOSUPPORTURL = 999;

//    public static AbstractWebViewDelegate newInstance(int statusCode) {
//        WebViewDelegateImpl delegate = WebViewDelegateImpl.newInstance("h5/500.html", true, true);
//        // TODO 需要设置桥接方法支持页面的交互
//        switch (statusCode) {
//            case 404:
//                delegate = WebViewDelegateImpl.newInstance("h5/404.html", true, true);
//                break;
//            case ERROR_HOST_LOOKUP:
//            case ERROR_CONNECT:
//            case ERROR_TIMEOUT:
//                // 判断断网和链接超时
//                // http://blog.csdn.net/lsyz0021/article/details/56677132
//                delegate = WebViewDelegateImpl.newInstance("h5/err_network.html", true, true);
//                break;
//            case ERROR_NOSUPPORTURL:
//                delegate = WebViewDelegateImpl.newInstance("h5/500.html", true, true);
//            default:
//        }
//        return delegate;
//    }

    public static void handlerErrorByStatusCode(WebView webView, int statusCode) {
        LoggerProxy.e("TODO handlerErrorByStatusCode %s", statusCode);
        String pageName = "h5/err_network.html";
        switch (statusCode) {
            case 404:
                pageName = "h5/404.html";
                break;
            case ERROR_HOST_LOOKUP:
            case ERROR_CONNECT:
            case ERROR_TIMEOUT:
                // 判断断网和链接超时
                // http://blog.csdn.net/lsyz0021/article/details/56677132
                pageName = "h5/err_network.html";
                break;
            case ERROR_NOSUPPORTURL:
                pageName = "h5/500.html";
            default:
        }
        Router.getInstance().loadLocalPage(webView, pageName, null);
    }
}
