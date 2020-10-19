package cn.jiiiiiin.vplus.core.util.intercept;

import android.text.TextUtils;

import com.blankj.utilcode.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import cn.jiiiiiin.vplus.core.app.ConfigKeys;
import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.exception.ViewPlusRuntimeException;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import okhttp3.HttpUrl;
import okhttp3.Response;

/**
 * created by YLG on 2019/10/9
 */

public class InteceptTools {
    private static HttpUrl URL = HttpUrl.parse((String) ViewPlus.getConfiguration(ConfigKeys.API_HOST));
    private static final String TRANSCODE_LOGIN = "login.do";
    private static final String TRANSCODE_LOGIN_OUT = "logout.do";

    /**
     * 添加请求头
     * @param headers 请求头的map
     */
    public static void addReqHead(WeakHashMap<String, String> headers) {
        if (headers != null) {
            try {
                // #166 java.util.ConcurrentModificationException 应用重新进入前台
                Map<String, String> customHeaders = ViewPlus.getConfiguration(ConfigKeys.CUSTOM_HEADERS);
                if (customHeaders != null) {
                    for (Map.Entry<String, String> item : customHeaders.entrySet()) {
                        final String name = item.getKey();
                        final Object value = item.getValue();
                        if (ViewPlus.IS_DEBUG()) {
                            LoggerProxy.i("test %s,%s", name, String.valueOf(value));
                        }
                        headers.put(name, String.valueOf(value));
                    }
                    //解决会话超时问题
                    setCookie(headers);
                }
            } catch (Exception e) {
                LoggerProxy.e(e, "设置后端通用请求头出错");
            }

        }
    }


    /**
     * 添加通用请求参数
     * @param url URL
     * @return 返回添加参数之后的URL
     */
    public static String addCommonParams(String url) {
        try {
            final Map<String, String> commParams = ViewPlus.getConfiguration(ConfigKeys.COMMON_PARAMS);
            if (commParams != null) {
                if (!url.contains("?")) {
                    url = url.concat("?");
                } else {
                    url = url.concat("&");
                }
                for (Map.Entry<String, String> entry : commParams.entrySet()) {
                    url = url.concat(entry.getKey())
                            .concat("=")
                            .concat(entry.getValue())
                            .concat("&");
                }
                url = url.substring(0, url.length() - 1);
            }
        } catch (Exception e) {
            LoggerProxy.e(e, "CommonParamsInterceptor SET COMMON_PARAMS 2 REQ ERR!");
        }
        if (ViewPlus.IS_DEBUG()) {
            LoggerProxy.i("InteceptTools addCommonParams url %s", url);
        }
        return url;
    }

    /**
     * 获取cookie
     * @param resp
     * @param url
     */
    public static void inteceptCookies(Response resp, String url) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        URL = HttpUrl.parse((String) ViewPlus.getConfiguration(ConfigKeys.API_HOST));
        if (httpUrl.host().equals(URL.host())) {
            final List<String> segements = httpUrl.pathSegments();
            if (segements.size() > 1) {
                final String transCode = segements.get(1);
                if (transCode.equals(TRANSCODE_LOGIN)) {
                    // 针对登录进行处理
                    final List<String> cookies = resp.headers("Set-Cookie");
                    if (ViewPlus.IS_DEBUG()) {
                        LoggerProxy.i("拦截到登录成功之后的 Cookie %s", cookies);
                    }
                    if (cookies != null && cookies.size() > 0) {
                        int index = cookies.size() - 1;
                        final String temp = cookies.get(index);
                        if (!StringUtils.isEmpty(temp)) {
//                            LoggerProxy.i("设置到登录成功之后的 Cookie %s", temp);
                            ViewPlus.getConfigurator().withCookie(temp);
                        }
                    }
                } else if (transCode.equals(TRANSCODE_LOGIN_OUT)) {
                    // ！退出登录清空 ViewPlus Cookie
                    LoggerProxy.i("退出登录清空 ViewPlus Cookie");
                    ViewPlus.getConfigurator().withCookie(null);
                }
            }
        }
    }

    /**
     * 设置cookie到请求头map
     * @param headers
     */
    public static void setCookie(WeakHashMap<String, String> headers) {
        if (null != headers) {
            try {
                final String cookie = ViewPlus.getConfiguration(ConfigKeys.SESSION_ID);
                if (!TextUtils.isEmpty(cookie)) {
                    headers.put("Cookie", cookie);
                }
            } catch (ViewPlusRuntimeException e) {
                e.printStackTrace();
            }
        }
    }

}
