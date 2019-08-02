package cn.jiiiiiin.vplus.core.net;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ToastUtils;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import cn.jiiiiiin.vplus.core.app.ConfigKeys;
import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.net.callback.IError;
import cn.jiiiiiin.vplus.core.net.callback.IFailure;
import cn.jiiiiiin.vplus.core.net.callback.IRequest;
import cn.jiiiiiin.vplus.core.net.callback.IRespStateHandler;
import cn.jiiiiiin.vplus.core.net.callback.ISuccess;
import cn.jiiiiiin.vplus.core.net.callback.RestOkHttpUtilsCallbacks;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.util.network.HttpAdjectiveUtil;
import okhttp3.MediaType;

import static cn.jiiiiiin.vplus.core.net.HttpMethod.DOWNLOAD;

/**
 * 进行具体请求的一个实际调用类
 * <p>每次请求都会创建一个新的RestClient实例</p>
 * <p>参数为一次构建完毕，后期不允许更改</p>
 *
 * @author jiiiiiin
 */

public final class RestOkHttpUtilsClient {

    static final String FILE_NAME_SPLIT_FLAG = ",";
    private final IRespStateHandler RESP_STATE_HANDLER;
    private final WeakHashMap<String, String> PARAMS;
    private final String URL;
    private final IRequest REQUEST;
    private final ISuccess SUCCESS;
    private final IFailure FAILURE;
    private final IError ERROR;
    private final FileCallBack FILE_CALL_BACK;
    private final boolean IGNORECOMMONCHECK;
    /**
     * 用于创建loader/dialog等ui需要使用的上下文，详见loading创建方法说明
     */
    private final Activity ACTIVITY;
    /**
     * 待上传的文件
     */
    private List<Map<String, File>> FILES;
    private final KProgressHUD.Style LOADER_STYLE;
    private final String LOADER_TXT;
    /**
     * 每次发送请求的call，用于在在组件(Actvity, Fragment)生命周期结束的时候去cancel掉网络请求。
     */
    private RequestCall CALL;
    private String JSON_PARAMS;

    RestOkHttpUtilsClient(String url,
                          WeakHashMap<String, String> params,
                          @NonNull String jsonParams,
                          IRequest request,
                          ISuccess success,
                          IFailure failure,
                          IError error,
                          Activity activity,
                          KProgressHUD.Style loaderStyle,
                          String loaderTxt,
                          boolean ignoreCommonCheck,
                          List<Map<String, File>> files,
                          FileCallBack fileCallBack,
                          IRespStateHandler respStateHandler) {
        if (HttpAdjectiveUtil.isHttpOrHttpsUrl(url)) {
            this.URL = url;
        } else {
            this.URL = ((String) ViewPlus.getConfiguration(ConfigKeys.API_HOST)).concat(url);
        }
        this.PARAMS = params;
        this.JSON_PARAMS = jsonParams;
        this.REQUEST = request;
        this.SUCCESS = success;
        this.FAILURE = failure;
        this.ERROR = error;
        this.ACTIVITY = activity;
        this.LOADER_STYLE = loaderStyle;
        this.LOADER_TXT = loaderTxt;
        this.IGNORECOMMONCHECK = ignoreCommonCheck;
        this.FILES = files;
        this.FILE_CALL_BACK = fileCallBack;
        this.RESP_STATE_HANDLER = respStateHandler;
    }

    public static RestOkHttpUtilsBuilder builder(Activity activity) {
        return new RestOkHttpUtilsBuilder(activity);
    }

    public static RestOkHttpUtilsBuilder builder() {
        return new RestOkHttpUtilsBuilder();
    }

    /**
     * 发送请求根据请求的类型
     * <p>call.execute();此方法是在主线程执行，如果调用这个方法，还需要新起一个线程进行包裹</p>
     */
    private void request(HttpMethod method) {
        if (!HttpAdjectiveUtil.canAccess2NewWork()) {
            ToastUtils.showLong("请求失败,请检查网络状态");
            if (FAILURE != null) {
                // TODO 之后要传一个异常对象下去
                FAILURE.onFailure();
            }
            return;
        }
        try {
            // https://work.bugtags.com/apps/1598731013063315/issues/1603343192427029/tags/1603343192430857?types=3&versions=1600310568035606&page=2
            // TODO 换库解决
            RestOkHttpUtilsCreator.getOkHttpUtils();
            RequestCall call = null;
            switch (method) {
                case GET:
                    call = OkHttpUtils
                            .get()
                            .url(URL)
                            .params(PARAMS)
                            .build();
                    break;
                case POST:
                    call = OkHttpUtils
                            .post()
                            .url(URL)
                            .params(PARAMS)
                            .build();
                    break;
                case POST_FILE:
                    final PostFormBuilder postFormBuilder = OkHttpUtils.post()
                            .url(URL)
                            .params(PARAMS);
                    if (FILES != null && !FILES.isEmpty()) {
                        for (Map<String, File> item : FILES) {
                            final String key = item.keySet().iterator().next();
                            String[] names = key.split(FILE_NAME_SPLIT_FLAG);
                            postFormBuilder.addFile(names[0], names[1], item.get(key));
                        }
                    }
                    call = postFormBuilder.build();
                    break;
                case POST_JSON_STR:
                    call = OkHttpUtils
                            .postString()
                            .url(URL)
                            .content(JSON_PARAMS)
                            .mediaType(MediaType.parse("application/json; charset=utf-8"))
                            .build();
                    break;
                case DOWNLOAD:
                    // 比较特殊
                    OkHttpUtils
                            .get()
                            .url(URL)
                            .build()
                            .execute(FILE_CALL_BACK);
                    break;
                default:
            }

            LoggerProxy.i("准备发送[%s] [%s]请求 %s", method, URL, PARAMS.isEmpty() ? "" : "请求参数: \n".concat(PARAMS.toString()));

            if (call != null && !method.equals(DOWNLOAD)) {
                this.CALL = call;
                call.execute(getRequestCallback());
            }
        } catch (Exception e) {
            LoggerProxy.e(e, "build okhttp请求出错");
            final String errMsg = String.format("发起请求出错[%s]", e.getMessage());
            if (ERROR != null) {
                ERROR.onError(errMsg);
            } else if (FAILURE != null) {
                ToastUtils.showLong(errMsg);
                FAILURE.onFailure();
            } else {
                ToastUtils.showLong(errMsg);
            }
        }

    }

    /**
     * cancel掉网络请求
     */
    public void cancel() {
        this.CALL.cancel();
    }

    private Callback<String> getRequestCallback() {
        return new RestOkHttpUtilsCallbacks(
                ACTIVITY,
                URL,
                REQUEST,
                SUCCESS,
                FAILURE,
                ERROR,
                IGNORECOMMONCHECK,
                LOADER_STYLE,
                LOADER_TXT,
                RESP_STATE_HANDLER
        );
    }

    public final RestOkHttpUtilsClient get() {
        request(HttpMethod.GET);
        return this;
    }

    public final RestOkHttpUtilsClient post() {
        request(HttpMethod.POST);
        return this;
    }

    public final RestOkHttpUtilsClient postJson() {
        request(HttpMethod.POST_JSON_STR);
        return this;
    }

    public final RestOkHttpUtilsClient postAndUploadFile() {
        request(HttpMethod.POST_FILE);
        return this;
    }

    public RestOkHttpUtilsClient download() {
        request(HttpMethod.DOWNLOAD);
        return this;
    }

}
