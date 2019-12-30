package cn.jiiiiiin.vplus.core.webview.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.URLUtil;
import android.webkit.WebView;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;

import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;

/**
 * @author jiiiiiin
 */

public final class WebViewUtil {

    private static final String OPEN_SYSTEM_WEBVIEW_INTENT = "android.intent.action.VIEW";

    public static void startSystemWebViewApp(Activity activity, String url) {
        if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
            Intent intent = new Intent();
            intent.setAction(OPEN_SYSTEM_WEBVIEW_INTENT);
            Uri contentUrl = Uri.parse(url);
            intent.setData(contentUrl);
            activity.startActivity(intent);
        } else {
            ToastUtils.showLong(String.format("非法链接[%s]，不能进行跳转。", url));
        }
    }

    public static void clearWebViewCookies() {
        final CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeSessionCookies(value -> LoggerProxy.w("同步之前移除 removeSessionCookies %s", value));
            cookieManager.removeAllCookies(value -> LoggerProxy.w("同步之前移除removeAllCookies %s", value));
        }
    }

    public static void clearWebViewCache(WebView webView) {
        if (webView != null) {
            try {
                //删除数据库缓存
                try {
                    ViewPlus.getApplicationContext().deleteDatabase("webview.db");
                    ViewPlus.getApplicationContext().deleteDatabase("webviewCache.db");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 清除网页访问留下的缓存，由于内核缓存是全局的因此这个方法不仅仅针对webview而是针对整个应用程序.
                webView.clearCache(true);
                webView.clearHistory();// 清除当前webview访问的历史记录，只会webview访问历史记录里的所有记录除了当前访问记录.
                // 这个api仅仅清除自动完成填充的表单数据，并不会清除WebView存储到本地的数据。
                webView.clearFormData();
                // 清除SSL偏好
                // webView.clearSslPreferences();
            } catch (Exception e) {
                if (ViewPlus.IS_DEBUG()) {
                    LoggerProxy.e("clearWebViewCache error %s", e.getMessage());
                }
            }
        }
    }

    @SuppressWarnings("UnusedAssignment")
    public static void destroyWebView(WebView webview) {
        // https://stackoverflow.com/questions/17418503/destroy-webview-in-android
        if (webview != null) {
            //releaseAllWebViewCallback();
            // 清除当前webview访问的历史记录
            webview.stopLoading();
            webview.loadUrl("about:blank");
            ViewGroup containerView = (ViewGroup) webview.getParent();
            if (null != containerView) {
                containerView.removeView(webview);
                // https://stackoverflow.com/questions/11995270/error-webview-destroy-called-while-still-attached
                webview.removeAllViews();
                // 需要注意的是：这个方法的调用应在WebView从父容器中被remove掉之后。我们可以手动地调用
                webview.destroy();
                webview = null;
            }
        }
    }

//    /**
//     * https://juejin.im/entry/57b586d35bbb50006303c7e7
//     * 如果实在不想用开额外进程的方式解决webview 内存泄露的问题，那么下面的方法很大程度上可以避免这种情况
//     */
//    static void releaseAllWebViewCallback() {
//        try {
//            @SuppressLint("PrivateApi") Field sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback");
//            if (sConfigCallback != null) {
//                sConfigCallback.setAccessible(true);
//                sConfigCallback.set(null, null);
//            }
//        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
//            if (ViewPlus.IS_DEBUG()) {
//                LoggerProxy.w(e.getClass().getName() + " " + e.getMessage());
//            }
//        }
//    }

    /**
     * 将cookie同步到WebView
     * https://www.jianshu.com/p/24827940b21a
     *
     * @param url    WebView要加载的url
     * @param cookie 要同步的cookie
     * @return true 同步cookie成功，false同步cookie失败
     * <p>
     * 设置到登录成功之后的 Cookie JSESSIONID=0000_dtHkdGZRLeftAIEtbJwZOl:1c6asvfnf; Path=/; Secure
     * <p>
     * 注意：
     * 1.同步 cookie 要在 WebView 加载 url 之前，否则 WebView 无法获得相应的 cookie，也就无法通过验证。
     * 2.cookie应该被及时更新，否则很可能导致WebView拿的是旧的session id和服务器进行通信。
     * 第一步：登录时从服务器的返回头中取出cookie
     * 根据Http请求的客户端不同，取cookie的方式也不同，我就不一一罗列了，需要的网友可以自行Google，以HttpURLcollection为例：
     * String cookieStr = conn.getHeaderField("Set-Cookie");
     */
    public static boolean syncCookie(@NonNull String url, String cookie) {
        if (!StringUtils.isTrimEmpty(url)) {
            final CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            // 移除 cookies
            clearWebViewCookies();
            // 为指定的url设置一个Cookie
            // 参数value使用"Set-Cookie"响应头格式，参考：https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Set-Cookie
            // 如果没有特殊需求，这里只需要将session id以"key=value"形式作为cookie即可
            if (ViewPlus.IS_DEBUG()) {
                LoggerProxy.d("同步的 url %s cookie %s", url, cookie);
            }
            cookieManager.setCookie(url, cookie);
            // 获取指定url关联的所有Cookie
            // 返回值使用"Cookie"请求头格式："name=value; name2=value2; name3=value3"
            String newCookie = cookieManager.getCookie(url);
            return !TextUtils.isEmpty(newCookie);
        } else {
            return false;
        }
    }

//    /**
//     * ！！！不能像下面这样去截取之后再设置到
//     * cookieManager.setCookie(url, _subStrSessionStr(cookie));
//     * @param cookie
//     * @return
//     */
//    private static String _subStrSessionStr(String cookie) {
//        if (cookie.indexOf(";") > 1) {
//            return cookie.substring(0, cookie.indexOf(";"));
//        }
//        return cookie;
//    }

    /**
     * 获取指定 url 的cookie
     */
    public static String getCookie2Url(String url) {
        final CookieManager manager = CookieManager.getInstance();
        if (!TextUtils.isEmpty(url) && manager.hasCookies()) {
            // 从具体的域中获取对应站点的cookie
            // 获取指定url关联的所有Cookie
            // 返回值使用"Cookie"请求头格式："name=value; name2=value2; name3=value3"
            final String cookieStr = manager.getCookie(url);
            if (!TextUtils.isEmpty(cookieStr)) {
                return cookieStr;
            }
        }
        return null;
    }

    // 写入磁盘
    private static void flush() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().flush();
        } else {
            //noinspection deprecation
            CookieSyncManager.getInstance().sync();
        }
    }

    // 移除指定url关联的所有cookie
    public static void removeCookie(String url) {
        CookieManager cm = CookieManager.getInstance();
        for (String cookie : cm.getCookie(url).split("; ")) {
            cm.setCookie(url, cookie.split("=")[0] + "=");
        }
        flush();
    }

    /**
     * sessionOnly 为true表示移除所有会话cookie，否则移除所有cookie
     *
     * @param sessionOnly
     */
    public static void remove(boolean sessionOnly) {
        CookieManager cm = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (sessionOnly) {
                cm.removeSessionCookies(null);
            } else {
                cm.removeAllCookies(null);
            }
        } else {
            if (sessionOnly) {
                cm.removeSessionCookie();
            } else {
                cm.removeAllCookie();
            }
        }
        flush();
    }

    public static final boolean scrollInBottom(WebView webView) {
        // getScrollY() //方法返回的是当前可见区域的顶端距整个页面顶端的距离,也就是当前内容滚动的距离.
        // getHeight()或者getBottom() //方法都返回当前WebView这个容器的高度
        // getContentHeight()返回的是整个html的高度,但并不等同于当前整个页面的高度,因为WebView有缩放功能,所以当前整个页面的高度实际上应该是原始html的高度再乘上缩放比例.因此,更正后的结果,准确的判断方法应该是：
        return webView.getContentHeight() * webView.getScale() == (webView.getHeight() + webView.getScrollY());
    }

    public static final boolean scrollInTop(WebView webView) {
        // getScrollY() //方法返回的是当前可见区域的顶端距整个页面顶端的距离,也就是当前内容滚动的距离.
        return webView.getScrollY() == 0;
    }

    public static final void scroll2Top(WebView webView) {
        webView.pageUp(true);
    }

    public static final void scroll2Bottom(WebView webView) {
        webView.pageDown(true);
    }

    public static final void scroll2Refresh(WebView webView) {
        webView.reload();
    }


}
