package cn.jiiiiiin.vplus.core.net.utils;

import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.MemoryCookieStore;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import cn.jiiiiiin.vplus.core.app.ConfigKeys;
import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import lombok.Setter;
import lombok.experimental.Accessors;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 创建Retrofit全局实例
 *
 * @author jiiiiiin
 */

public class RestOkHttpUtilsCreatorNew {

    /**
     * 构建OkHttpClient实例
     */
    @SuppressWarnings("SpellCheckingInspection")
    @Setter
    @Accessors(chain = true)
    public static final class OkHttpInitBuild {

        private static class Holder {
            private static final OkHttpInitBuild INSTANCE = new OkHttpInitBuild();

            private Holder() {
            }
        }


        public static OkHttpInitBuild getInstance() {
            return OkHttpInitBuild.Holder.INSTANCE;
        }

        private static final String TAG = "AJAX::";

        private OkHttpClient.Builder builder = new OkHttpClient.Builder();
        private ArrayList<Interceptor> interceptors = ViewPlus.getConfiguration(ConfigKeys.INTERCEPTOR);
        private CookieJarImpl cookieJar = new CookieJarImpl(new MemoryCookieStore());
        private long apiConnectTimeOut = ViewPlus.getConfiguration(ConfigKeys.API_CONNECT_TIME_OUT);
        private long apiReadTimeOut = ViewPlus.getConfiguration(ConfigKeys.API_READ_TIME_OUT);
        // 默认的client
        OkHttpClient okHttpClient = OkHttpInitBuild.getInstance().init().build();

        private OkHttpClient.Builder addInterceptor() {
            if (builder.interceptors() != null) {
                builder.interceptors().clear();
            }
            if (interceptors != null && !interceptors.isEmpty()) {
                for (Interceptor interceptor : interceptors) {
                    builder.addInterceptor(interceptor);
                }
            }
            if (ViewPlus.IS_DEBUG()) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Log.d(TAG, message));
                // ! 如果需要自己调试更多信息使用下面这个级别
                //logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
                logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                builder.addInterceptor(logging);
            }
            return builder;
        }

        /**
         * 1.设置拦截器
         * 2.设置ssl
         *
         * @return
         */
        public OkHttpClient.Builder init() {
            addInterceptor();
            builder
                    .connectTimeout(apiConnectTimeOut, TimeUnit.SECONDS)
                    .readTimeout(apiReadTimeOut, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .cookieJar(cookieJar);

            try {
                final SSLSocketFactory sslSocketFactory = ViewPlus.getConfiguration(ConfigKeys.SSL_SOCKET_FACTORY);
                final X509TrustManager trustManager = ViewPlus.getConfiguration(ConfigKeys.SSL_TRUST_MANAGER);
                final HostnameVerifier hostnameVerifier = ViewPlus.getConfiguration(ConfigKeys.SSL_HOSTNAME_VERIFIER);
                if (sslSocketFactory != null && trustManager != null) {
                    builder.sslSocketFactory(sslSocketFactory, trustManager);
                }
                if (hostnameVerifier != null) {
                    builder.hostnameVerifier(hostnameVerifier);
                }
            } catch (Exception e) {
                LoggerProxy.e(e, "配置OK_HTTP_CLIENT SSL相关设置失败");
            }
            return builder;
        }

    }

    public static OkHttpUtils getOkHttpUtils() {
        return OkHttpUtilsHolder.OK_HTTP_UTILS;
    }

    private static final class OkHttpUtilsHolder {
        private static final OkHttpUtils OK_HTTP_UTILS = OkHttpUtils.initClient(OkHttpInitBuild.getInstance().okHttpClient);
    }

    /**
     * 请确认手动：
     * final SSLSocketFactory sslSocketFactory = ViewPlus.getConfiguration(ConfigKeys.SSL_SOCKET_FACTORY);
     * final X509TrustManager trustManager = ViewPlus.getConfiguration(ConfigKeys.SSL_TRUST_MANAGER);
     * final HostnameVerifier hostnameVerifier = ViewPlus.getConfiguration(ConfigKeys.SSL_HOSTNAME_VERIFIER);
     */
    public void reinitOkHttpUtils() {
        final OkHttpClient okHttpClient = OkHttpInitBuild.getInstance()
                .setBuilder(new OkHttpClient.Builder())
                .setApiConnectTimeOut(ViewPlus.getConfiguration(ConfigKeys.API_CONNECT_TIME_OUT))
                .setApiReadTimeOut(ViewPlus.getConfiguration(ConfigKeys.API_READ_TIME_OUT))
                .init()
                .build();
        OkHttpInitBuild.getInstance().setOkHttpClient(okHttpClient);
        OkHttpUtils.initClient(OkHttpInitBuild.getInstance().okHttpClient);
    }

//    /**
//     * List<Cookie> cookies = RestCreator.getCookies(HttpUrl.parse(ViewPlus.getConfiguration(ConfigKeys.API_HOST)));
//     *
//     * @param uri
//     * @return
//     */
//    public static List<Cookie> getCookies(HttpUrl uri) {
//        final CookieJar cookieJar = OkHttpInitBuild.okHttpClient.cookieJar();
//        if (cookieJar == null) {
//            ViewPlusException.illegalArgument("you should invoked okHttpClientBuilder.COOKIE_JAR() to set a COOKIE_JAR.");
//        }
//        return cookieJar.loadForRequest(uri);
//    }

}
