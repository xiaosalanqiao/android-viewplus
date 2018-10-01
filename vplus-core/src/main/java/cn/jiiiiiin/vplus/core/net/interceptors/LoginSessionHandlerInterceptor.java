package cn.jiiiiiin.vplus.core.net.interceptors;

import android.support.annotation.NonNull;

import com.blankj.utilcode.util.StringUtils;

import java.io.IOException;
import java.util.List;

import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @author jiiiiiin
 * @version 1.0
 */

public class LoginSessionHandlerInterceptor implements Interceptor {

    private final HttpUrl URL;
    private final String TRANSCODE_LOGIN;
    private String TRANSCODE_LOGIN_OUT;

    public LoginSessionHandlerInterceptor(String url, String transCodeLogin, String transCodeLogout) {
        this.URL = HttpUrl.parse(url);
        this.TRANSCODE_LOGIN = transCodeLogin;
        this.TRANSCODE_LOGIN_OUT = transCodeLogout;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response resp = chain.proceed(chain.request());
        try {
            final HttpUrl httpUrl = chain.request().url();
            if (httpUrl.host().equals(URL.host())) {
                final List<String> segements = httpUrl.pathSegments();
                if (segements.size() > 1) {
                    final String transCode = segements.get(1);
                    if (transCode.equals(TRANSCODE_LOGIN)) {
                        // 针对登录进行处理
                        final List<String> cookies = resp.headers("Set-Cookie");
                        LoggerProxy.w("拦截到登录成功之后的 Cookie %s", cookies);
                        if (cookies != null && cookies.size() > 0) {
                            int index = cookies.size() - 1;
                            final String temp = cookies.get(index);
                            if (!StringUtils.isEmpty(temp)) {
                                LoggerProxy.w("设置到登录成功之后的 Cookie %s", temp);
                                ViewPlus.getConfigurator().withCookie(temp);
                            }
                        }
                    } else if (transCode.equals(TRANSCODE_LOGIN_OUT)) {
                        // ！退出登录清空 ViewPlus Cookie
                        LoggerProxy.w("退出登录清空 ViewPlus Cookie");
                        ViewPlus.getConfigurator().withCookie(null);
                    }
                }
            }
        } catch (Exception e) {
            LoggerProxy.e(e, "LoginSessionHandlerInterceptor intercept出错");
        }

        return resp;
    }
}
