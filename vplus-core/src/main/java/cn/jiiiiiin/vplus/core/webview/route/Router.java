package cn.jiiiiiin.vplus.core.webview.route;

import android.support.annotation.NonNull;
import android.webkit.URLUtil;
import android.webkit.WebView;

import com.blankj.utilcode.util.ToastUtils;

import java.util.Map;

import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.exception.ViewPlusException;
import cn.jiiiiiin.vplus.core.util.Intent.RouterUtil;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.webview.AbstractWebViewDelegate;
import cn.jiiiiiin.vplus.core.webview.jsbridgehandler.protocol.UriInfo;
import cn.jiiiiiin.vplus.core.webview.loader.IPageLoadListener;

/**
 * @author jiiiiiin
 */

public class Router {

    static final String ASSET_BASE = "file:///android_asset/";
    static final String RESOURCE_BASE = "file:///android_res/";
    static final String FILE_BASE = "file://";
    static final String PROXY_BASE = "file:///cookieless_proxy/";
    static final String CONTENT_BASE = "content:";
    private static final String AND = "&";
    static final String PROTOCOL_BS = "bs";
    static final String PROTOCOL_TEL = "tel";
    static final String PROTOCOL_HTTP = "http";
    static final String PROTOCOL_HTTPS = "https";

    private Router() {
    }

    private static class Holder {
        private static final Router INSTANCE = new Router();
    }

    public static Router getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 处理public boolean shouldOverrideUrlLoading(WebView view, String url)，拦截url
     * html页面a标签、window.location.href请求的url交给当前方法完成处理
     * <p>shouldOverrideUrlLoading的代理方法</p>
     *
     * @param delegate
     * @param url
     * @param pageLoadListener
     * @return 简单地说，就是返回true，那么url就已经由客户端处理了，WebView就不管了，如果返回false，那么当前的WebView实现就会去处理这个url。 https://juejin.im/post/5a94f9d15188257a63113a74
     */
    @SuppressWarnings({"AlibabaUndefineMagicConstant", "AlibabaRemoveCommentedCode"})
    public final boolean handleWebViewUrlReq(AbstractWebViewDelegate delegate, WebView webView, String url, IPageLoadListener pageLoadListener) {
        boolean res;
        final UriInfo uriInfo = new UriInfo(url);
        LoggerProxy.e("handleWebViewUrlReq %s %s", url, uriInfo);
        final String scheme = uriInfo.getScheme();
        switch (scheme) {
            case PROTOCOL_BS:
                // `bs://showKeyboard?{xxx:xxx}
                // ProtocolBridgeHandler.event(delegate, uriInfos);
                ToastUtils.showLong("屏幕右划返回手机银行");
                pageLoadListener.onInterceptorNoSupportProtocol(url);
                res = true;
                break;
            case PROTOCOL_TEL:
                RouterUtil.start4ActionDialProtocol(delegate.getActivity(), url);
                res = true;
                break;
            case PROTOCOL_HTTP:
            case PROTOCOL_HTTPS:
                // 如果需要，则区分本地和网络两种方式
                if (pageLoadListener != null) {
                    LoggerProxy.e("通知webview的包裹对象，页面发生了onShouldOverrideUrlLoading [%s]", url);
                    res = pageLoadListener.onShouldOverrideUrlLoading(url);
                } else {
                    LoggerProxy.e("pageLoadListener没有设置，让系统自己处理 %s", url);
                    webView.loadUrl(url);
                    res = true;
                }
                break;
            default:
                res = false;
        }
        return res;
//        if (URLUtil.isNetworkUrl(url) || URLUtil.isAssetUrl(url)) {
//            // 如果需要，则区分本地和网络两种方式
//            if (pageLoadListener != null) {
//                LoggerProxy.e("通知webview的包裹对象，页面发生了onShouldOverrideUrlLoading 【%s】", url);
//                return pageLoadListener.onShouldOverrideUrlLoading(url);
//            } else {
//                LoggerProxy.e("pageLoadListener没有设置，让系统自己处理 %s", url);
//                return false;
//            }
//        } else {
//            ToastUtils.showLong(String.format("拦截到不支持的协议，不做任何处理 %s", url));
//            LoggerProxy.w("拦截到不支持的协议，不做任何处理 %s", url);
//            return true;
//        }
    }

//    private void _callNoSupportPage(AbstractWebViewDelegate delegate, String url) {
//        DialogUtil.errDialog(delegate.getActivity(), String.format("拦截到不支持的协议[%s]", url), null);
//    }

    /**
     * 渲染带协议（http/file）页面
     */
    private void loadWebPage(@NonNull WebView webView, @NonNull String url, Map<String, String> additionalHttpHeaders) {
        if (null != additionalHttpHeaders && !additionalHttpHeaders.isEmpty()) {
            webView.loadUrl(url, additionalHttpHeaders);
        } else {
            webView.loadUrl(url);
        }
    }

    /**
     * 渲染asset中的页面
     */
    public void loadLocalPage(@NonNull WebView webView, @NonNull String url, Map<String, String> additionalHttpHeaders) {
        loadWebPage(webView, ASSET_BASE + url, additionalHttpHeaders);
    }

    private void loadPage(@NonNull WebView webView, @NonNull String url, Map<String, String> additionalHttpHeaders) {
        if (URLUtil.isNetworkUrl(url) || URLUtil.isAssetUrl(url)) {
            // 加载带协议的页面
            loadWebPage(webView, url, additionalHttpHeaders);
        } else {
            loadLocalPage(webView, url, additionalHttpHeaders);
        }
    }

    /**
     * 添加 HTTP 请求头(Header)
     */
    public final void loadPage(@NonNull WebView webView, String url, Map<String, String> additionalHttpHeaders, Map<String, String> commParams) {
        if (ViewPlus.IS_DEBUG()) {
            LoggerProxy.d("准备加载h5: %s \n请求头:%s \n通用参数:%s", url, additionalHttpHeaders, commParams);
        }
        if (commParams != null && !commParams.isEmpty()) {
            url += updateCommParams(commParams, url.indexOf("?")!=-1);
        }
        loadPage(webView, url, additionalHttpHeaders);
    }

    private static String updateCommParams(Map<String, String> params, boolean hasUrlParams) {
        StringBuffer res = hasUrlParams ? new StringBuffer("&") : new StringBuffer("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            res.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append(AND);
        }
        return res.substring(0, res.lastIndexOf(AND));
    }
}
