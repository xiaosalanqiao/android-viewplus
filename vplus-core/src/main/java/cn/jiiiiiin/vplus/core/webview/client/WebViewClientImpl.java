package cn.jiiiiiin.vplus.core.webview.client;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ImageUtils;

import java.io.BufferedInputStream;
import java.util.Arrays;

import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.ui.dialog.DialogUtil;
import cn.jiiiiiin.vplus.core.util.ui.ViewUtil;
import cn.jiiiiiin.vplus.core.webview.AbstractWebViewDelegate;
import cn.jiiiiiin.vplus.core.webview.AbstractWebViewInteractiveDelegate;
import cn.jiiiiiin.vplus.core.webview.loader.IPageLoadListener;
import cn.jiiiiiin.vplus.core.webview.route.Router;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;

/**
 * WebViewClient可以拿到WebView在访问网络各个阶段的回调，包括加载前后，失败等
 *
 * @author Created by jiiiiiin
 */

public class WebViewClientImpl extends WebViewClient {

    private final AbstractWebViewDelegate DELEGATE;
    private IPageLoadListener mIPageLoadListener = null;
    private long mStart = 0;

    public WebViewClientImpl(AbstractWebViewDelegate delegate, IPageLoadListener listener) {
        this.DELEGATE = delegate;
        this.mIPageLoadListener = listener;
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
        return Router.getInstance().handleWebViewUrlReq(DELEGATE, view, url, mIPageLoadListener);
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
        if (mIPageLoadListener != null && mIPageLoadListener.isHandlerOnReceivedErrorRes(Uri.parse(failingUrl))) {
            // TODO 如果是第三方应用或者说这里必须要清空，只是考虑怎么刷新的问题 @zhaojin
            mIPageLoadListener.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    /**
     * WebView 访问 url 出错
     */
    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        if (mIPageLoadListener != null && mIPageLoadListener.isHandlerOnReceivedErrorRes(request.getUrl())) {
            mIPageLoadListener.onReceivedError(view, error.getErrorCode(), error.getDescription().toString(), request.getUrl().toString());
        }
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mIPageLoadListener != null && request.isForMainFrame() && mIPageLoadListener.isHandlerOnReceivedErrorRes(request.getUrl())) {
                view.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
                mIPageLoadListener.onReceivedHttpError(view, request, errorResponse.getStatusCode());
            }
        }
    }

    // 处理HTTP认证请求，默认行为是取消请求
    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        handler.cancel();
    }

    /**
     * > [Android 让WebView完美支持https双向认证(SSL)](https://blog.csdn.net/kpioneer123/article/details/51491739)
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
        final Activity activity = DELEGATE.getActivity();
        ViewUtil.activityIsLivingCanByRun(activity, new ViewUtil.AbstractActivityIsLivingCanByRunCallBack() {
            @Override
            public void doIt(@NonNull Activity activity) {
                SslCertificate sslCertificate = error.getCertificate();
                LoggerProxy.d("sslCertificate %s", sslCertificate.toString());
                DialogUtil.confirmDialog(activity, "SSL 认证错误", "无法验证服务器SSL证书。\n是否继续访问？", "继续", "取消", new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        switch (which) {
                            case POSITIVE:
                                handler.proceed();
                                break;
                            case NEGATIVE:
                                handler.cancel();
                                break;
                            default:
                        }
                    }
                });
            }
        });

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
