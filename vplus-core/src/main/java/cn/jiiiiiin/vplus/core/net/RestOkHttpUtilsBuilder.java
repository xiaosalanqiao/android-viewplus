package cn.jiiiiiin.vplus.core.net;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import cn.jiiiiiin.vplus.core.app.ConfigKeys;
import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.exception.ViewPlusRuntimeException;
import cn.jiiiiiin.vplus.core.net.callback.IError;
import cn.jiiiiiin.vplus.core.net.callback.IFailure;
import cn.jiiiiiin.vplus.core.net.callback.IRequest;
import cn.jiiiiiin.vplus.core.net.callback.IRespStateHandler;
import cn.jiiiiiin.vplus.core.net.callback.ISuccess;
import cn.jiiiiiin.vplus.core.ui.loader.LoaderCreatorProxy;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.util.ui.ViewUtil;

import static cn.jiiiiiin.vplus.core.net.RestOkHttpUtilsClient.FILE_NAME_SPLIT_FLAG;

/**
 * 为创建RestClient实例而生的一个构造者
 * <p>接受请求时候RestClient实例所需要的参数</p>
 *
 * @author jiiiiiin
 */

public final class RestOkHttpUtilsBuilder {
    /**
     * 方便内存回收
     */
    private final WeakHashMap<String, String> mParams = new WeakHashMap<>();
    private String mUrl = null;
    private IRequest mIRequest = null;
    private ISuccess mISuccess = null;
    private IFailure mIFailure = null;
    private IError mIError = null;
    /**
     * 用于创建loader的上下文，详见loading创建方法说明
     */
    private Activity mActivity = null;
    private KProgressHUD.Style mLoaderStyle = null;

    /**
     * 针对某些第三方的请求,忽略检查返回码
     */
    private boolean mIgnoreCommonCheck = false;
    /**
     * 存储需要上传的文件集合信息
     */
    private final List<Map<String, File>> mFiles = new ArrayList<>();
    private FileCallBack mFileCallBack;
    private IRespStateHandler mRespStateHandler = null;
    private String mLoaderTxt = LoaderCreatorProxy.DEFAULT_LABEL;
    private String mJsonParams;

    /**
     * @param activity
     */
    RestOkHttpUtilsBuilder(Activity activity) {
        this.mActivity = activity;
    }

    RestOkHttpUtilsBuilder() {
    }

    /**
     * 设置请求url
     *
     * @param url
     * @return
     */
    public final RestOkHttpUtilsBuilder url(String url) {
        this.mUrl = url;
        return this;
    }

    /**
     * 设置请求参数
     *
     * @param params
     * @return
     */
    public final RestOkHttpUtilsBuilder params(WeakHashMap<String, String> params) {
        mParams.putAll(params);
        return this;
    }

    /**
     * 设置请求参数
     *
     * @param key
     * @param value
     * @return
     */
    public final RestOkHttpUtilsBuilder params(String key, String value) {
        if (value != null) {
            mParams.put(key, value);
        } else {
            LoggerProxy.w("设置请求参数%s字段的值为空，将被省略！", key);
        }
        return this;
    }

    public RestOkHttpUtilsBuilder addFile(String name, String filename, File file) {
        Map<String, File> item = new HashMap<>();
        // name和filename不能包含[,]
        item.put(name.concat(FILE_NAME_SPLIT_FLAG).concat(filename), file);
        mFiles.add(item);
        return this;
    }

    /**
     * 设置请求开始时候的环绕回调
     *
     * @param iRequest
     * @return
     */
    public final RestOkHttpUtilsBuilder onRequest(IRequest iRequest) {
        this.mIRequest = iRequest;
        return this;
    }

    public final RestOkHttpUtilsBuilder success(ISuccess iSuccess) {
        this.mISuccess = iSuccess;
        return this;
    }

    public final RestOkHttpUtilsBuilder failure(IFailure iFailure) {
        this.mIFailure = iFailure;
        return this;
    }

    public final RestOkHttpUtilsBuilder error(IError iError) {
        this.mIError = iError;
        return this;
    }

    public final RestOkHttpUtilsBuilder fileCallBack(FileCallBack fileCallBack) {
        this.mFileCallBack = fileCallBack;
        return this;
    }

    public final RestOkHttpUtilsBuilder loader(KProgressHUD.Style style) {
        this.mLoaderStyle = style;
        return this;
    }

    public RestOkHttpUtilsBuilder loader(String loaderTxt) {
        this.mLoaderStyle = LoaderCreatorProxy.DEFAULT_LOADER;
        this.mLoaderTxt = loaderTxt;
        return this;
    }

    public final RestOkHttpUtilsBuilder loader() {
        this.mLoaderStyle = LoaderCreatorProxy.DEFAULT_LOADER;
        return this;
    }

    public final RestOkHttpUtilsBuilder ignoreCommonCheck() {
        this.mIgnoreCommonCheck = true;
        return this;
    }

    public RestOkHttpUtilsBuilder setRespStateHandler(IRespStateHandler respStateHandler) {
        this.mRespStateHandler = respStateHandler;
        return this;
    }

    public RestOkHttpUtilsBuilder jparams(@NonNull String jsonParams) {
        this.mJsonParams = jsonParams;
        return this;
    }

    public final RestOkHttpUtilsClient build() {
        if (!mIgnoreCommonCheck && mRespStateHandler == null) {
            // !如果不是忽略检测，那么必须存在mRespStateHandler，如果没有配置就使用全局的
            mRespStateHandler = ViewPlus.getConfiguration(ConfigKeys.RESP_STATE_HANDLER);
        }
        return new RestOkHttpUtilsClient(mUrl,
                mParams,
                mJsonParams,
                mIRequest,
                mISuccess,
                mIFailure,
                mIError,
                mActivity,
                mLoaderStyle,
                mLoaderTxt,
                mIgnoreCommonCheck,
                mFiles,
                mFileCallBack,
                mRespStateHandler);
    }

}
