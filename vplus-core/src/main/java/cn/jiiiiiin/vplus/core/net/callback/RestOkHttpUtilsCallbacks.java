package cn.jiiiiiin.vplus.core.net.callback;

import android.app.Activity;
import android.os.Handler;
import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ToastUtils;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.zhy.http.okhttp.callback.StringCallback;

import javax.net.ssl.SSLHandshakeException;

import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.exception.ViewPlusException;
import cn.jiiiiiin.vplus.core.exception.ViewPlusRuntimeException;
import cn.jiiiiiin.vplus.core.ui.loader.LoaderCreatorProxy;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import okhttp3.Call;
import okhttp3.Request;

/**
 * @author jiiiiiin
 */

public final class RestOkHttpUtilsCallbacks extends StringCallback {
    /**
     * Handler 尽力声明为static类型，以避免内存泄露
     */
    private static final Handler HANDLER = ViewPlus.getHandler();
    private final Activity ACTIVITY;
    private final IRequest REQUEST;
    private final ISuccess SUCCESS;
    private final IFailure FAILURE;
    private final IError ERROR;
    private final boolean IGNORE_RESP_STATE_HANDLER;
    private final IRespStateHandler RESP_STATE_HANDLER;
    private final KProgressHUD.Style LOADER_STYLE;
    private final String LOADER_TXT;
    private final String URL;

    public RestOkHttpUtilsCallbacks(Activity activity, String url, IRequest request, ISuccess success, IFailure failure, IError error, boolean ignoreRespStateHandler, KProgressHUD.Style loaderStyle, String loaderTxt, IRespStateHandler respStateHandler) {
        this.ACTIVITY = activity;
        this.URL = url;
        this.REQUEST = request;
        this.SUCCESS = success;
        this.FAILURE = failure;
        this.ERROR = error;
        this.IGNORE_RESP_STATE_HANDLER = ignoreRespStateHandler;
        this.LOADER_STYLE = loaderStyle;
        this.LOADER_TXT = loaderTxt;
        this.RESP_STATE_HANDLER = respStateHandler;
    }

    @Override
    public void onBefore(Request request, int id) {
        if (REQUEST != null) {
            REQUEST.onRequestStart();
        }
        if (LOADER_STYLE != null) {
            LoaderCreatorProxy.showLoading(ACTIVITY, LOADER_STYLE, LOADER_TXT);
        }
    }

    @Override
    public void onAfter(int id) {
    }

    @Override
    public void onResponse(String response, int id) {
        LoggerProxy.d("==== 服务端响应[%s]请求: \n%s\n===", URL, response);
        if (IGNORE_RESP_STATE_HANDLER) {
            try {
                JSONObject jsonObject = getJsonObject(response);
                if (SUCCESS != null) {
                    SUCCESS.onSuccess(jsonObject);
                }
            } catch (Exception e) {
                LoggerProxy.e(e, "IGNORE_RESP_STATE_HANDLER Exception");
                _handlerFailureErr(e);
            }
        } else {
            if (RESP_STATE_HANDLER != null) {
                try {
                    final boolean success = RESP_STATE_HANDLER.onRespCheckStateIsOk(response);
                    if (success) {
                        JSONObject jsonObject = getJsonObject(response);
                        if (SUCCESS != null) {
                            SUCCESS.onSuccess(jsonObject);
                        }
                    } else {
                        RESP_STATE_HANDLER.onRespErrHandler(response);
                        _callErrorFunc(response);
                    }
                } catch (Exception e) {
                    LoggerProxy.e(e, "RESP_STATE_HANDLER err");
                    _handlerFailureErr(e);
                }
            } else {
                throw new ViewPlusRuntimeException("RESP_STATE_HANDLER IS NULL ERR!");
            }
        }
        _checkLoaderSetAndCloseIt();
    }

    private JSONObject getJsonObject(String response) throws ViewPlusException {
        try {
            return JSON.parseObject(response);
        } catch (Exception e) {
            LoggerProxy.e(e, "JSON.parseObject(response)格式化服务端返回的数据出错");
            throw new ViewPlusException("格式化服务端返回的数据出错[非JSON格式]");
        }
    }

    private boolean _callErrorFunc(String parseRespErr) {
        try {
            if (ERROR != null) {
                ERROR.onError(parseRespErr);
                return true;
            }
        } catch (Exception e) {
            LoggerProxy.e(e, "处理错误的回调函数执行时候出错");
            return false;
        }
        return false;
    }

    private void _checkLoaderSetAndCloseIt() {
        if (LOADER_STYLE != null) {
            HANDLER.post(LoaderCreatorProxy::stopLoading);
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        LoggerProxy.e(e, "发送请求失败[onError] %s", id);
        _handlerFailureErr(e);

        if (FAILURE != null) {
            FAILURE.onFailure();
        }

        if (REQUEST != null) {
            REQUEST.onRequestEnd();
        }
        _checkLoaderSetAndCloseIt();
    }

    @SuppressWarnings({"AlibabaLowerCamelCaseVariableNaming", "AlibabaAvoidStartWithDollarAndUnderLineNaming"})
    private void _handlerFailureErr(@NonNull Exception e) {
        // 处理特殊的异常
        if (e instanceof SSLHandshakeException && e.getMessage().contains("java.security.cert.CertPathValidatorException")) {
            // 在进行预埋证书检测过期 / 应用被使用代理抓包（在正常模式下）
            LoggerProxy.e("发起请求前进行SSL检测失败，请联系客服检测当前是否是最新版本客户端");
        } else if (!_callErrorFunc(e.getMessage())) {
            switch (e.getClass().getName()) {
                case "java.net.UnknownHostException":
                case "java.net.ConnectException":
                    ToastUtils.showLong("请求失败，请检查当前网络环境是否流畅");
                    break;
                case "java.net.SocketTimeoutException":
                    ToastUtils.showLong("链接服务器超时，请检查当前网络环境是否流畅");
                    break;
                case "java.io.IOException": {
                    final String msg = e.getMessage();
                    if (msg.contains("404")) {
                        ToastUtils.showLong("请求的资源不存在，请稍后尝试");
                    } else {
                        ToastUtils.showLong("链接服务器失败，请稍后尝试");
                    }
                    break;
                }
                case "cn.jiiiiiin.vplus.core.exception.ViewPlusException":
                    ToastUtils.showLong(e.getMessage());
                    break;
                default:
                    ToastUtils.showLong(String.format("当前请求链接服务器失败，请稍后尝试[%s]", URL));
            }
        }

    }

}
