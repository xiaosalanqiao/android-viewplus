package cn.jiiiiiin.vplus.core.net.interceptors;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;

import java.io.IOException;

import cn.jiiiiiin.vplus.core.util.file.FileUtil;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 模拟请求index交易
 * <p>
 * 测试数据存储在应用的资源目录row下
 * 使用：
 * .withInterceptor(new DebugInterceptor("index", R.raw.test))
 * 支持模拟返回针对：
 * http://localhost/index
 *
 * @author jiiiiiin
 */

public class DebugInterceptor extends BaseInterceptor {

    /**
     * 模拟（DEBUG）的url
     */
    private final String DEBUG_URL;
    /**
     * row目录中待模拟的数据文件id
     */
    private final int DEBUG_RAW_ID;

    public DebugInterceptor(String debugUrl, int rawId) {
        this.DEBUG_URL = debugUrl;
        this.DEBUG_RAW_ID = rawId;
    }

    /**
     * 模拟返回响应体
     *
     * @param chain
     * @param json
     * @return
     */
    private Response getResponse(Chain chain, String json) {
        return new Response.Builder()
                .code(200)
                .addHeader("Content-Type", "application/json")
                .body(ResponseBody.create(MediaType.parse("application/json"), json))
                .message("OK")
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .build();
    }

    /**
     * @param chain
     * @param rawId
     * @return
     * @RawRes 标识参数必须是R文件中的资源
     */
    private Response debugResponse(Chain chain, @RawRes int rawId) {
        final String json = FileUtil.getRawFile(rawId);
        return getResponse(chain, json);
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        final String url = chain.request().url().toString();
        // 如果是测试链接，就返回测试请求响应数据
        if (url.contains(DEBUG_URL)) {
            return debugResponse(chain, DEBUG_RAW_ID);
        }
        return chain.proceed(chain.request());
    }
}
