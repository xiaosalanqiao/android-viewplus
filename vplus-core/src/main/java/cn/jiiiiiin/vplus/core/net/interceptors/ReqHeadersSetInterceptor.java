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
        Request original = chain.request();
        Request.Builder builder = original.newBuilder();
        if (!Hawk.get(HawkKey.HAWK_KEY_HAVE_REQ_HEADERS, false)) {
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
            } catch (Exception e) {
                LoggerProxy.e(e, "设置后端通用请求头出错");
            }
        }
        Request request = builder
                .method(original.method(), original.body())
                .build();
        return chain.proceed(request);
    }
}
