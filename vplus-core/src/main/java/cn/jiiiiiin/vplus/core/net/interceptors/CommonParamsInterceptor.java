package cn.jiiiiiin.vplus.core.net.interceptors;

import com.orhanobut.hawk.Hawk;

import java.io.IOException;
import java.util.Map;

import cn.jiiiiiin.vplus.core.app.ConfigKeys;
import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.dict.HawkKey;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * URL后面追加共同的参数
 *
 * @author jiiiiiin
 * @version 1.0
 */

public class CommonParamsInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        LoggerProxy.i("CommonParamsInterceptor#intercept call");
        Request original = chain.request();
        Request.Builder builder = original.newBuilder();
        String paramFlag = original.headers().get(HawkKey.HAWK_KEY_HAVE_COMMON_PARAMS);
        LoggerProxy.i("CommonParamsInterceptor flag %s", paramFlag);
        HttpUrl.Builder httpUrlBuilder = original.url().newBuilder();
        //paramFlag为true标示不需要添加公共请求参数，处理完后移除标志
        if ("true".equals(paramFlag)) {
            builder.removeHeader(HawkKey.HAWK_KEY_HAVE_COMMON_PARAMS);
        }else {
            try {
                final Map<String, String> commParams = ViewPlus.getConfiguration(ConfigKeys.COMMON_PARAMS);
                if (commParams != null) {
                    for (Map.Entry<String, String> entry : commParams.entrySet()) {
                        httpUrlBuilder.addEncodedQueryParameter(entry.getKey(), entry.getValue());
                    }
                }
                builder.removeHeader(HawkKey.HAWK_KEY_HAVE_COMMON_PARAMS);
            } catch (Exception e) {
                LoggerProxy.e(e, "CommonParamsInterceptor SET COMMON_PARAMS 2 REQ ERR!");
            }
        }
        HttpUrl httpUrl = httpUrlBuilder.build();
        Request request = builder
                .url(httpUrl)
                .method(original.method(), original.body())
                .build();
        if (ViewPlus.IS_DEBUG()) {
            LoggerProxy.i("CommonParamsInterceptor#intercept: " + request.toString());
        }
        return chain.proceed(request);
    }
}
