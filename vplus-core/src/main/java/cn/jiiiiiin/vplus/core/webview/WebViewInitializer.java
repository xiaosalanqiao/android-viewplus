package cn.jiiiiiin.vplus.core.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Build.VERSION;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.blankj.utilcode.util.ToastUtils;

import java.io.File;

import cn.jiiiiiin.vplus.core.app.ConfigKeys;
import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.util.network.HttpAdjectiveUtil;

/**
 * 对webview进行设置
 * <p>
 * https://mp.weixin.qq.com/s/wFwlVYx5zsoGmjsOfPJckg
 * https://jiandanxinli.github.io/2016-08-31.html
 * https://kaolamobile.github.io/2018/02/16/design-an-elegant-and-powerful-android-webview-part-one/#fn:1
 *
 * https://github.com/Jiiiiiin/WebViewStudy
 *
 * @author jiiiiiin
 */

class WebViewInitializer {

    private static final String LOAD_CACHE_ELSE_NETWORK_TOAST_MSG = "请检查当前网络环境是否流畅";

    @SuppressLint("SetJavaScriptEnabled")
    static WebView createWebView(WebView webView) {
        if (ViewPlus.IS_DEBUG()) {
            // 开启chrome调试功能:Enable remote debugging via chrome://inspect
            if (VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }

        // cookie TODO 待测试
        final CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 设置接收第三方Cookie
            // cookieManager.setAcceptThirdPartyCookies(webView, true);
            cookieManager.setAcceptThirdPartyCookies(webView, false);
        }
        CookieManager.setAcceptFileSchemeCookies(false);

        // https://www.jianshu.com/p/2b2e5d417e10
        // webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // 隐藏滚动条
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        //不能横向滚动
        webView.setHorizontalScrollBarEnabled(false);
        //不能纵向滚动
        webView.setVerticalScrollBarEnabled(false);
        //允许截图
        webView.setDrawingCacheEnabled(ViewPlus.IS_DEBUG());
        //屏蔽长按事件 导致不能输入框进行粘贴
        // webView.setOnLongClickListener(v -> true);

        // 初始化WebSettings
        final WebSettings settings = webView.getSettings();
        if (settings == null) {
            return webView;
        }
        settings.setDefaultTextEncodingName("utf-8");
        // 设置WebView是否可以运行JavaScript
        settings.setJavaScriptEnabled(true);
        // 设置WebView的UserAgent值。
        final String useragent = settings.getUserAgentString().concat(";").concat(ViewPlus.getConfiguration(ConfigKeys.WEB_USER_AGENT));
        settings.setUserAgentString(useragent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 5.0以上允许加载http和https混合的页面(5.0以下默认允许，5.0+默认禁止)
            // https://mp.weixin.qq.com/s/FyxuOuTFyZ_F8D0jQ8w5bg
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 是否在离开屏幕时光栅化(会增加内存消耗)，默认值 false
            settings.setOffscreenPreRaster(false);
        }

        // 资源访问
        // 是否可访问Content Provider的资源，默认值 true
        settings.setAllowContentAccess(false);
        // 文件权限:启用或禁用WebView访问文件数据
        // 是否可访问本地文件，默认值 true
        // !存在漏洞风险：https://zhuanlan.zhihu.com/p/21787366 Android中默认mWebView.setAllowFileAccess(true)，在File域下，能够执行任意的JavaScript代码，同源策略跨域访问能够对私有目录文件进行访问等。APP对嵌入的WebView未对file:/// 形式的URL做限制，会导致隐私信息泄露，针对IM类软件会导致聊天信息、联系人等等重要信息泄露，针对浏览器类软件，则更多的是cookie信息泄露。
        settings.setAllowFileAccess(false);
        // 是否允许通过file url加载的Javascript读取本地文件，默认值 false
        settings.setAllowFileAccessFromFileURLs(false);
        // 是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
        settings.setAllowUniversalAccessFromFileURLs(false);

        // 资源加载
        // https://blog.csdn.net/huang3513/article/details/79103924
        // 加快HTML网页加载完成速度（默认情况html代码下载到WebView后，webkit开始解析网页各个节点，发现有外部样式文件或者外部脚本文件时，会异步发起网络请求下载文件，但如果在这之前也有解析到image节点，那势必也会发起网络请求下载相应的图片。在网络情况较差的情况下，过多的网络请求就会造成带宽紧张，影响到css或js文件加载完成的时间，造成页面空白loading过久。解决的方法就是告诉WebView先不要自动加载图片，等页面finish后再发起图片加载。）
        // 1.首先在WebView初始化时添加如下代码
        // 对系统API在19以上的版本作了兼容。因为4.4以上系统在onPageFinished时再恢复图片加载时,如果存在多张图片引用的是相同的src时，会只有一个image标签得到加载，因而对于这样的系统我们就先直接加载。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            webView.getSettings().setLoadsImagesAutomatically(false);
        }

        // TODO 研究缓存相关
        // 缓存相关
        if (VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //10M缓存，api 18后，系统自动管理。
            settings.setAppCacheMaxSize(10 * 1024 * 1024);
        }
        // 启用或禁用应用缓存。 启用Application Caches API，必需设置有效的缓存路径才能生效，默认值 false
        settings.setAppCacheEnabled(true);
        //设置  Application Caches 缓存目录
//        settings.setAppCachePath(ViewPlus.getConfiguration(ConfigKeys.ORIGIN_WEBVIEW_APP_CACHE_PATH));
        // 启用或禁用数据库缓存。
        // 启用Web SQL Database API，这个设置会影响同一进程内的所有WebView，默认值 false
        // 此API已不推荐使用，参考：https://www.w3.org/TR/webdatabase/
        settings.setDatabaseEnabled(false);
        // 启用或禁用DOM缓存。启用HTML5 DOM storage API，默认值 false
        settings.setDomStorageEnabled(true);
        // 用来设置WebView的缓存模式。当我们加载页面或从上一个页面返回的时候，会按照设置的缓存模式去检查并使用（或不使用）缓存。
        // LOAD_DEFAULT 默认的缓存使用模式。在进行页面前进或后退的操作时，如果缓存可用并未过期就优先加载缓存，否则从网络上加载数据。这样可以减少页面的网络请求次数。
        if (HttpAdjectiveUtil.canAccess2NewWork()) {
            // 根据cache-control等浏览器缓存机制决定是否从网络上取数据。
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            // 没网，则从本地获取，即离线加载
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            ToastUtils.showLong(LOAD_CACHE_ELSE_NETWORK_TOAST_MSG);
        }

        // 关闭webview组件的保存密码功能。
        // !存在漏洞风险：https://zhuanlan.zhihu.com/p/21787366 如果该功能未关闭，在用户输入密码时，会弹出提示框，询问用户是否保存密码，如果选择"是"，密码会被明文保到/data/data/com.package.name/databases/webview.db
        settings.setSavePassword(false);
        // 是否保存表单数据
        settings.setSaveFormData(true);
        // 是否当webview调用requestFocus时为页面的某个元素设置焦点，默认值 true
        settings.setNeedInitialFocus(true);

        // 打开 WebView 的 LBS 功能，这样 JS 的 geolocation 对象才可以使用
        // settings.setGeolocationEnabled(true);

        // 设置是否 WebView 支持 “viewport” 的 HTML meta tag，这个标识是用来屏幕自适应的
        settings.setUseWideViewPort(true);
        // 是否使用overview mode加载页面，默认值 false
        // 当页面宽度大于WebView宽度时，缩小使页面宽度等于WebView宽度
        settings.setLoadWithOverviewMode(true);
        // 布局算法
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

        // 多窗口的问题
        //html中的_bank标签就是新建窗口打开，有时会打不开，需要加以下
        //然后 复写 WebChromeClient的onCreateWindow方法
        // 是否支持多窗口，默认值false
        settings.setSupportMultipleWindows(false);
        // 是否可用Javascript(window.open)打开窗口&设置允许js弹出alert对话框，默认值 false
        settings.setJavaScriptCanOpenWindowsAutomatically(false);

//        // 默认文本编码，默认值 "UTF-8"
        settings.setDefaultTextEncodingName("UTF-8");
//        settings.setDefaultFontSize(16);        // 默认文字尺寸，默认值16，取值范围1-72
//        settings.setDefaultFixedFontSize(16);   // 默认等宽字体尺寸，默认值16
//        settings.setMinimumFontSize(8);         // 最小文字尺寸，默认值 8
//        settings.setMinimumLogicalFontSize(8);  // 最小文字逻辑尺寸，默认值 8
//
//        // 字体
//        settings.setStandardFontFamily("sans-serif");   // 标准字体，默认值 "sans-serif"
//        settings.setSerifFontFamily("serif");           // 衬线字体，默认值 "serif"
//        settings.setSansSerifFontFamily("sans-serif");  // 无衬线字体，默认值 "sans-serif"
//        settings.setFixedFontFamily("monospace");       // 等宽字体，默认值 "monospace"
//        settings.setCursiveFontFamily("cursive");       // 手写体(草书)，默认值 "cursive"
//        settings.setFantasyFontFamily("fantasy");       // 幻想体，默认值 "fantasy"

        // 缩放(zoom)
        // 隐藏缩放控件
        // 是否使用内置缩放机制
        settings.setBuiltInZoomControls(false);
        // 是否显示内置缩放控件
        settings.setDisplayZoomControls(false);
        // 是否支持缩放
        settings.setSupportZoom(false);
        // 文字缩放百分比，默认值 100
        //禁用文字缩放
        settings.setTextZoom(100);
        removeJavascriptInterfaces(webView);
        return webView;
    }

    /**
     * 需要移除一些私有的桥接 removeJavascriptInterface 移除已注入的Javascript对象，下次加载或刷新页面时生效
     * https://mp.weixin.qq.com/s/wFwlVYx5zsoGmjsOfPJckg
     *
     * @param webView
     */
    @TargetApi(11)
    private static void removeJavascriptInterfaces(WebView webView) {
        try {
            if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 17) {
                webView.removeJavascriptInterface("searchBoxJavaBridge_");
                webView.removeJavascriptInterface("accessibility");
                webView.removeJavascriptInterface("accessibilityTraversal");
            }
        } catch (Throwable tr) {
            LoggerProxy.e(tr, "清理私有的桥接出错");
        }
    }
}
