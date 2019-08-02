package cn.jiiiiiin.vplus.core.net;

import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.MemoryCookieStore;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import cn.jiiiiiin.vplus.core.app.ConfigKeys;
import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.exception.ViewPlusException;
import cn.jiiiiiin.vplus.core.net.utils.HttpsCheckUtils;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 创建Retrofit全局实例
 *
 * @author jiiiiiin
 */

public class RestOkHttpUtilsCreator {

    /**
     * 构建OkHttpClient实例
     */
    @SuppressWarnings("SpellCheckingInspection")
    public static final class OkHttpHolder {

        private static final OkHttpClient.Builder BUILDER = new OkHttpClient.Builder();
        private static final ArrayList<Interceptor> INTERCEPTORS = ViewPlus.getConfiguration(ConfigKeys.INTERCEPTOR);
        private static final String TAG = "AJAX::";
        private static final long API_CONNECT_TIME_OUT = ViewPlus.getConfiguration(ConfigKeys.API_CONNECT_TIME_OUT);
        private static final long API_READ_TIME_OUT = ViewPlus.getConfiguration(ConfigKeys.API_READ_TIME_OUT);

        private static final CookieJarImpl COOKIE_JAR = new CookieJarImpl(new MemoryCookieStore());

        private static OkHttpClient.Builder addInterceptor() {
            if (BUILDER.interceptors() != null) {
                BUILDER.interceptors().clear();
            }
            if (INTERCEPTORS != null && !INTERCEPTORS.isEmpty()) {
                for (Interceptor interceptor : INTERCEPTORS) {
                    BUILDER.addInterceptor(interceptor);
                }
            }
            if (ViewPlus.IS_DEBUG()) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Log.d(TAG, message));
                // ! 如果需要自己调试更多信息使用下面这个级别
                //logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
                logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                BUILDER.addInterceptor(logging);
            }

            BUILDER
                    .connectTimeout(API_CONNECT_TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(API_READ_TIME_OUT, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .cookieJar(COOKIE_JAR);

            try {
                final SSLSocketFactory SSL_SOCKET_FACTORY = ViewPlus.getConfiguration(ConfigKeys.SSL_SOCKET_FACTORY);
                final X509TrustManager TRUST_MANAGER = ViewPlus.getConfiguration(ConfigKeys.SSL_TRUST_MANAGER);
                final HostnameVerifier HOSTNAME_VERIFIER = ViewPlus.getConfiguration(ConfigKeys.SSL_HOSTNAME_VERIFIER);
                if (SSL_SOCKET_FACTORY != null && TRUST_MANAGER != null) {
                    BUILDER.sslSocketFactory(SSL_SOCKET_FACTORY, TRUST_MANAGER);
                }
                if (HOSTNAME_VERIFIER != null) {
                    BUILDER.hostnameVerifier(HOSTNAME_VERIFIER);
                }
            } catch (Exception e) {
                LoggerProxy.e(e, "配置OK_HTTP_CLIENT SSL相关设置失败");
            }
            return BUILDER;
        }

        static final OkHttpClient OK_HTTP_CLIENT = addInterceptor().build();
    }

    public static OkHttpUtils getOkHttpUtils() {
        return OkHttpUtilsHolder.OK_HTTP_UTILS;
    }

    private static final class OkHttpUtilsHolder {
        private static final OkHttpUtils OK_HTTP_UTILS = OkHttpUtils.initClient(OkHttpHolder.OK_HTTP_CLIENT);
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
