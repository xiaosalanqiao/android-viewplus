package cn.jiiiiiin.vplus.core.net.interceptors;

import androidx.annotation.NonNull;

import com.orhanobut.hawk.Hawk;

import java.io.IOException;
import java.util.Map;

import cn.jiiiiiin.vplus.core.app.ConfigKeys;
import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.dict.HawkKey;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 设置后端通用请求头
 *
 * @author zhaojin
 */

public final class ReqHeadersSetInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        LoggerProxy.i("ReqHeadersSetInterceptor#intercept call");
        Request original = chain.request();
        String reqFlag = original.headers().get(HawkKey.HAWK_KEY_HAVE_REQ_HEADERS);
        LoggerProxy.i("ReqHeadersSetInterceptor flag %s", reqFlag);
        Request.Builder builder = original.newBuilder();
        //reqFlag为true标示不需要添加请求头，处理完后移除标志
        if ("true".equals(reqFlag)) {
            builder.removeHeader(HawkKey.HAWK_KEY_HAVE_REQ_HEADERS);
        } else {
            try {
                // #166 java.util.ConcurrentModificationException 应用重新进入前台
                Map<String, String> customHeaders = ViewPlus.getConfiguration(ConfigKeys.CUSTOM_HEADERS);
                if (customHeaders != null) {
                    for (Map.Entry<String, String> item : customHeaders.entrySet()) {
                        final String name = item.getKey();
                        final String value = item.getValue();
                        builder.header(name, value);
                    }
                }
                builder.removeHeader(HawkKey.HAWK_KEY_HAVE_REQ_HEADERS);
            } catch (Exception e) {
                LoggerProxy.e(e, "设置后端通用请求头出错");
            }
        }

        Request request = builder
                .method(original.method(), original.body())
                .build();
//        LoggerProxy.i("request: " + request.toString());
        return chain.proceed(request);
    }
}
