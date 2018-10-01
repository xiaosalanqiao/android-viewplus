package cn.jiiiiiin.vplus.core.net.interceptors;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 拦截原生的请求，获取Cookie
 * 给原生添加WebView中添加的Cookie
 *
 * @author jiiiiiin
 */

public final class AddCookieInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        final Request.Builder builder = chain.request().newBuilder();
//        Observable
//                .just(VPlusPreference.getCustomAppProfile("cookie"))
//                .subscribe(cookie -> {
//                    // 给原生API请求附带上WebView拦截下来的Cookie
//                    builder.addHeader("Cookie", cookie);
//                });
        return chain.proceed(builder.build());
    }
}
