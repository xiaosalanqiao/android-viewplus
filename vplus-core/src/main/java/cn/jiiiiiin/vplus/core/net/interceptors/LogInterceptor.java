package cn.jiiiiiin.vplus.core.net.interceptors;

import java.io.IOException;

import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * @author jiiiiiin
 */
public class LogInterceptor implements Interceptor {

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Response response = null;
        if(ViewPlus.IS_DEBUG()) {
            long t1 = System.nanoTime();
            LoggerProxy.d(String.format("准备发送请求 authority: %s \n path: %s \n query: %s \n connection:%n \n headers:%s",
                    request.url().uri().getAuthority(),
                    request.url().encodedPath(),
                    request.url().query(),
                    chain.connection(),
                    request.headers()));
            response = chain.proceed(request);
            long t2 = System.nanoTime();
            LoggerProxy.d(String.format("接收到响应 %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));
        } else {
            response = chain.proceed(request);
        }
        return response;
    }
}
