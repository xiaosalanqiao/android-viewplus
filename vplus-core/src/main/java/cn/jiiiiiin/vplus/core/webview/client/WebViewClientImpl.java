package cn.jiiiiiin.vplus.core.webview.client;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.os.Build;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afollestad.materialdialogs.MaterialDialog;

import cn.jiiiiiin.vplus.core.ui.dialog.DialogUtil;
import cn.jiiiiiin.vplus.core.util.ui.ViewUtil;
import cn.jiiiiiin.vplus.core.webview.AbstractWebViewDelegate;
import cn.jiiiiiin.vplus.core.webview.AbstractWebViewInteractiveDelegate;
import cn.jiiiiiin.vplus.core.webview.loader.IPageLoadListener;
import cn.jiiiiiin.vplus.core.webview.route.Router;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * WebViewClient可以拿到WebView在访问网络各个阶段的回调，包括加载前后，失败等
 *
 * https://juejin.im/post/5a94fb046fb9a0635865a2d6
 *
 * TODO WebViewClient.shouldInterceptRequest(webview, request)，无论是普通的页面请求(使用GET/POST)，还是页面中的异步请求，或者页面中的资源请求，都会回调这个方法，给开发一次拦截请求的机会。在这个方法中，我们可以进行静态资源的拦截并使用缓存数据代替，也可以拦截页面，使用自己的网络框架来请求数据。包括后面介绍的WebView免流方案，也和此方法有关。
 *
 * 作者：网易考拉移动端团队
 * 链接：https://juejin.im/post/5a94fb046fb9a0635865a2d6
 * 来源：掘金
 *
 *
 * @author Created by jiiiiiin
 */

public class WebViewClientImpl extends WebViewClient {

    private final AbstractWebViewDelegate DELEGATE;
    private IPageLoadListener mIPageLoadListener = null;
    private long mStart = 0;
    /**
     * 标识是否要忽略白名单
     */
    private boolean mIgnoreWhiteURL = false;

    public WebViewClientImpl(AbstractWebViewDelegate delegate, IPageLoadListener listener, boolean isIgnoreWhiteURL) {
        this.DELEGATE = delegate;
        this.mIPageLoadListener = listener;
        this.mIgnoreWhiteURL = isIgnoreWhiteURL;
    }

    /**
     * 该方法在WebView开始加载页面且仅在Main frame loading（即整页加载）时回调，一次Main frame的加载只会回调该方法一次。我们可以在这个方法里设定开启一个加载的动画，告诉用户程序在等待网络的响应。
     */
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        mStart = System.currentTimeMillis();
        if (mIPageLoadListener != null) {
            mIPageLoadListener.onLoadStart(view);
        }
    }

    // TODO 暂时用不上，保留 @zhaojin
//    /**
//     * WebView 可以拦截某一次的 request 来返回我们自己加载的数据，这个方法在后面缓存会有很大作用。
//     * 拦截资源请求并返回响应数据，返回null时WebView将继续加载资源
//     * https://www.jianshu.com/p/5e7075f4875f?hmsr=toutiao.io&utm_medium=toutiao.io&utm_source=toutiao.io
//     */
//    @Override
//    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//        // https://www.jianshu.com/p/3508789b3de5
//        if (url.toLowerCase().contains("/favicon.ico")) {
//            try {
//                // TODO 后期改造favicon.ico来源
//                return new WebResourceResponse("image/png", null,
//                        new BufferedInputStream(view.getContext().getAssets().open("favicon.ico")));
//            } catch (Exception e) {
//                LoggerProxy.e(e, "替换favicon.ico出错");
//            }
//        }
//        // TODO 后期再这里处理将前端的一些公共第三方模块加载请求替换
//        return null;
//    }

    @SuppressWarnings("AliDeprecation")
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        LoggerProxy.w("===shouldOverrideUrlLoading %s", url);
        if (!mIgnoreWhiteURL) {
            //校验白名单
            return Router.getInstance().handleWebViewUrlReq(DELEGATE, view, url, mIPageLoadListener);
        } else {
            //忽略白名单
            return false;
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        // 待测试：!在Android4.4的手机上onPageFinished()回调会多调用一次(具体原因待追查)
        // 需要尽量避免在onPageFinished()中做业务操作，否则会导致重复调用，还有可能会引起逻辑上的错误.
        if (mIPageLoadListener != null) {
            LoggerProxy.i("===h5页面[%s]加载完毕 onPageFinished 花费： %s", url, (System.currentTimeMillis() - mStart));
            mIPageLoadListener.onLoadEnd(false);
        }
    }

    /**
     * WebView 访问 url 出错
     */
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        LoggerProxy.d("onReceivedError %s %s", errorCode, failingUrl);
        if(!_isUnHandler(view, errorCode, failingUrl) && !TextUtils.isEmpty(failingUrl) && failingUrl.equals(view.getUrl())) {
            if (mIPageLoadListener != null && mIPageLoadListener.isHandlerOnReceivedErrorRes(Uri.parse(failingUrl))) {
                LoggerProxy.e("onReceivedError handler %s %s", errorCode, failingUrl);
                // TODO 如果是第三方应用或者说这里必须要清空，只是考虑怎么刷新的问题 @zhaojin
                mIPageLoadListener.onReceivedError(view, errorCode, description, failingUrl);
            }
        }
    }

    /**
     * 作者：网易考拉移动端团队
     * 链接：https://juejin.im/post/5a94fb046fb9a0635865a2d6
     *
     * @param view
     * @param errorCode
     * @param failingUrl
     * @return
     */
    private boolean _isUnHandler(WebView view, int errorCode, String failingUrl) {
        // -12 == EventHandle.ERROR_BAD_URL, a hide return code inside android.net.http package
        if ((failingUrl != null && !failingUrl.equals(view.getUrl()) && !failingUrl.equals(view.getOriginalUrl())) /* not subresource error*/
                || (failingUrl == null && errorCode != -12) /*not bad url*/
                || errorCode == -1) { //当 errorCode = -1 且错误信息为 net::ERR_CACHE_MISS
            return true;
        }
        return false;
    }

    /**
     * WebViewClient.onReceivedError(webView, webResourceRequest, webResourceError)
     *
     * 只有在主页面加载出现错误时，才会回调这个方法。这正是展示加载错误页面最合适的方法。然鹅，如果不管三七二十一直接展示错误页面的话，那很有可能会误判，给用户造成经常加载页面失败的错觉。由于不同的WebView实现可能不一样，所以我们首先需要排除几种误判的例子：
     *
     * 1.加载失败的url跟WebView里的url不是同一个url，排除；
     * 2.errorCode=-1，表明是ERROR_UNKNOWN的错误，为了保证不误判，排除
     * 3.`failingUrl=null和errorCode=-12`，由于错误的url是空而不是ERROR_BAD_URL，排除
     *
     * 作者：网易考拉移动端团队
     * 链接：https://juejin.im/post/5a94fb046fb9a0635865a2d6
     *
     * https://jiandanxinli.github.io/2016-08-31.html
     * 1.这个方法只在与服务器无法正常连接时调用，类似于服务器返回错误码的那种错误（即HTTP ERROR），该方法是不会回调的，因为你已经和服务器正常连接上了（全怪官方文档(︶^︶)）；
     * 2.这个方法是新版本的onReceivedError()方法，从API23开始引进，与旧方法onReceivedError(WebView view,int errorCode,String description,String failingUrl)不同的是，新方法在页面局部加载发生错误时也会被调用（比如页面里两个子Tab或者一张图片）。这就意味着该方法的调用频率可能会更加频繁，所以我们应该在该方法里执行尽量少的操作。
     */
//    @Override
//    @TargetApi(Build.VERSION_CODES.M)
//    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//        if(!_isUnHandler(view, error.getErrorCode(), failingUrl) && !TextUtils.isEmpty(failingUrl) && failingUrl.equals(view.getUrl())) {
//            if (mIPageLoadListener != null && request.isForMainFrame() && mIPageLoadListener.isHandlerOnReceivedErrorRes(request.getUrl())) {
//                mIPageLoadListener.onReceivedError(view, error.getErrorCode(), error.getDescription().toString(), request.getUrl().toString());
//            }
//        }
//    }

//    /**
//    WebViewClient.onReceivedHttpError(webView, webResourceRequest, webResourceResponse)
//
//    任何HTTP请求产生的错误都会回调这个方法，包括主页面的html文档请求，iframe、图片等资源请求。在这个回调中，由于混杂了很多请求，不适合用来展示加载错误的页面，而适合做监控报警。当某个URL，或者某个资源收到大量报警时，说明页面或资源可能存在问题，这时候可以让相关运营及时响应修改。
//
//    作者：网易考拉移动端团队
//    链接：https://juejin.im/post/5a94fb046fb9a0635865a2d6
//    来源：掘金
//     * https://jiandanxinli.github.io/2016-08-31.html
//     *
//     * API23便引入了该方法。当服务器返回一个HTTP ERROR并且它的status code>=400时，该方法便会回调。
//     * 这个方法的作用域并不局限于Main Frame，任何资源的加载引发HTTP ERROR都会引起该方法的回调，所以我们也应该在该方法里执行尽量少的操作，只进行非常必要的错误处理等。
//     */
//    @Override
//    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            if (mIPageLoadListener != null && request.isForMainFrame() && mIPageLoadListener.isHandlerOnReceivedErrorRes(request.getUrl())) {
//                view.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
//                mIPageLoadListener.onReceivedHttpError(view, request, errorResponse.getStatusCode());
//            }
//        }
//    }

    // 处理HTTP认证请求，默认行为是取消请求
    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        handler.cancel();
    }

    /**
     * [Android 让WebView完美支持https双向认证(SSL)](https://blog.csdn.net/kpioneer123/article/details/51491739)
     * WebViewClient.onReceivedSslError(webview, sslErrorHandler, sslError)
     *
     * 任何HTTPS请求，遇到SSL错误时都会回调这个方法。比较正确的做法是让用户选择是否信任这个网站，这时候可以弹出信任选择框供用户选择（大部分正规浏览器是这么做的）。但人都是有私心的，何况是遇到自家的网站时。我们可以让一些特定的网站，不管其证书是否存在问题，都让用户信任它。在这一点上，分享一个小坑。考拉的SSL证书使用的是GeoTrust的GeoTrust SSL CA - G3，但是在某些机型上，打开考拉的页面都会提示证书错误。这时候就不得不使用“绝招”——让考拉的所有二级域都是可信任的。
     *
     * 作者：网易考拉移动端团队
     * 链接：https://juejin.im/post/5a94fb046fb9a0635865a2d6
     * @param view
     * @param handler
     * @param error
     */
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        // 接受信任所有网站的证书
        // https://support.google.com/faqs/answer/7071387?hl=en
        // https://www.cnblogs.com/liyiran/p/7011317.html

        // 安全的方案是当出现了证书问题的时候，读取 asserts 中保存的的根证书，然后与服务器校验，假如通过了，继续执行 handler.proceed()，否则执行 handler.cancel()。
        // https://juejin.im/entry/57b586d35bbb50006303c7e7
        // https://mp.weixin.qq.com/s/FyxuOuTFyZ_F8D0jQ8w5bg
        // handler.proceed();
        // https://mickey-tang.blogspot.com/2017/03/android-webview-ssl.html

        // 20190215 add
//        if(AbstractWebViewInteractiveDelegate.checkAllowHostWhiteList(view.getUrl())){
//            handler.proceed();
//        } else {
//            final Activity activity = DELEGATE.getActivity();
//            ViewUtil.activityIsLivingCanByRun(activity, new ViewUtil.AbstractActivityIsLivingCanByRunCallBack() {
//                @Override
//                public void doIt(@NonNull Activity activity) {
//                    SslCertificate sslCertificate = error.getCertificate();
//                    LoggerProxy.d("sslCertificate %s", sslCertificate.toString());
//                    handler.proceed();
//                    DialogUtil.confirmDialog(activity, "SSL 认证错误", "无法验证服务器SSL证书。\n是否继续访问？", "继续", "取消", (positive) -> {
//                        handler.proceed();
//                        return Unit.INSTANCE;
//                    }, negative -> {
//                        handler.cancel();
//                        return Unit.INSTANCE;
//                    });
//
//                }
//            });
//        }

        // 防止和甲方解释不清
        // 出现这个错误，一般是服务器端证书到期所致
        handler.proceed();

    }

    // 此方法添加于API21，在UI线程被调用
    // 处理SSL客户端证书请求，必要的话可显示一个UI来提供KEY。
    // 有三种响应方式：proceed()/cancel()/ignore()，默认行为是取消请求
    // 如果调用proceed()或cancel()，Webview 将在内存中保存响应结果且对相同的"host:port"不会再次调用 onReceivedClientCertRequest
    // 多数情况下，可通过KeyChain.choosePrivateKeyAlias启动一个Activity供用户选择合适的私钥
    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        request.cancel();
    }


//    // 通知应用可以将当前的url存储在数据库中，意味着当前的访问url已经生效并被记录在内核当中。
//    // 此方法在网页加载过程中只会被调用一次，网页前进后退并不会回调这个函数。
//    @Override
//    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
//    }


}
