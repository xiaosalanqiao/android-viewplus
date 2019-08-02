package cn.jiiiiiin.vplus.core.update;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONException;
import com.vector.update_app.HttpManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;

import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by Vector
 * on 2017/6/19 0019.
 */

public class UpdateAppHttpUtilBck implements HttpManager {
    /**
     * 异步get
     *
     * @param url      get请求地址
     * @param params   get参数
     * @param callBack 回调
     */
    @Override
    public void asyncGet(@NonNull String url, @NonNull Map<String, String> params, @NonNull final Callback callBack) {
        OkHttpUtils.get()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Response response, Exception e, int id) {
//                        callBack.onError(validateError(e, response));
//                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LoggerProxy.e(e, "get err");
                        callBack.onError(validateError(e));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LoggerProxy.e("onResponse %s", response);
                        callBack.onResponse(response);
                    }
                });
    }

    /**
     * 异步post
     *
     * @param url      post请求地址
     * @param params   post请求参数
     * @param callBack 回调
     */
    @Override
    public void asyncPost(@NonNull String url, @NonNull Map<String, String> params, @NonNull final Callback callBack) {
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    //                    @Override
//                    public void onError(Call call, Response response, Exception e, int id) {
//                        callBack.onError(validateError(e, response));
//                    }
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LoggerProxy.e(e, "get err");
                        callBack.onError(validateError(e));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LoggerProxy.e("onResponse %s", response);
                        callBack.onResponse(response);
                    }
                });

    }

    /**
     * 下载
     *
     * @param url      下载地址
     * @param path     文件保存路径
     * @param fileName 文件名称
     * @param callback 回调
     */
    @Override
    public void download(@NonNull String url, @NonNull String path, @NonNull String fileName, @NonNull final FileCallback callback) {
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new FileCallBack(path, fileName) {
                    @Override
                    public void inProgress(float progress, long total, int id) {
                        LoggerProxy.e("inProgress %s", progress / total);
                        callback.onProgress(progress, total);
                    }

                    //                    @Override
//                    public void onError(Call call, Response response, Exception e, int id) {
//                        callback.onError(validateError(e, response));
//                    }
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LoggerProxy.e(e, "get err");
                        callback.onError(validateError(e));
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        LoggerProxy.e("onResponse %s", response);
                        callback.onResponse(response);

                    }

                    @Override
                    public void onBefore(Request request, int id) {
                        LoggerProxy.e("onBefore %s", request.url());
                        super.onBefore(request, id);
                        callback.onBefore();
                    }
                });

    }


    private String validateError(Exception error) {
        if (error != null) {
            if (error instanceof SocketTimeoutException) {
                return "网络连接超时，请稍候重试";
            } else if (error instanceof JSONException) {
                return "json转化异常";
            } else if (error instanceof ConnectException) {
                return "服务器网络异常或宕机，请稍候重试";
            } else {
                return String.format("未知异常，请稍候重试[%s]", error.getMessage());
            }
        } else {
            return "请求出错请稍后尝试";
        }
    }
}