package cn.jiiiiiin.viewplus.jsbridge;

import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;

import cn.jiiiiiin.vplus.core.webview.event.AbstractEvent;
import cn.jiiiiiin.vplus.core.webview.event.BaseEvent;
import cn.jiiiiiin.vplus.core.webview.event.model.EventParams;
import cn.jiiiiiin.vplus.core.webview.event.model.EventResData;
import cn.jiiiiiin.vplus.core.webview.jsbridgehandler.exception.JSBridgeException;

import static cn.jiiiiiin.vplus.core.ui.loader.LoaderCreatorProxy.showLoading;

/**
 * @author jiiiiiin
 * @version 1.0
 */

@SuppressWarnings({"AlibabaClassNamingShouldBeCamel"})
// 1.继承`cn.jiiiiiin.vplus.core.webview.event.BaseEvent`
// 声明一个**UIEvent**
public class UIEvent extends BaseEvent {

    // 2.定义**toast**名字的action接口
    private static final String TOAST = "toast";

    public static AbstractEvent newInstance() {
        return new UIEvent();
    }

    @Override
    protected String[] getSupportActions() {
        return new String[]{
                // 3.注册接口：配置action接口
                TOAST
        };
    }

    @Override
    protected EventResData doAction(EventParams eventParams) throws JSBridgeException {
        // 4.解析前端需要调用的action接口名称
        final String action = eventParams.getAction();
        // 5.获取前端传递的json参数(可选)
        final JSONObject params = eventParams.getParams();
        final String listener = eventParams.getListener();
        EventResData eventResData = null;
        switch (action) {
            case TOAST:
                // 6.执行接口
                eventResData = _toast(params, listener);
                break;
            default:
        }
        return eventResData;
    }


    private EventResData _toast(final JSONObject params, final String listener) {
        // 7.获取特定参数(前端传递)
        final int duration = parseInteger(params, "duration", Toast.LENGTH_LONG);
        final String msg = params.getString("msg");
        switch (duration) {
            case Toast.LENGTH_LONG:
                // 8.打印toast
                ToastUtils.showLong(msg);
                if(!StringUtils.isTrimEmpty(listener)) {
                    // 模拟异步执行其他事情
                    safetyUseWebView(webView -> {
                        webView.postDelayed(() -> {

                        }, 2000);
                        // 10.异步通知前端，即java调用前端js
                        safetyCallH5(listener, String.format("{\"toastTask\":\"%s\"}", "模拟异步执行其他事情完成，通知前端"));
                    });
                }
                break;
            case Toast.LENGTH_SHORT:
                ToastUtils.showShort(msg);
                break;
            default:
        }
        // 9.返回action接口调用状态(即**同步**返回接口是否调用成功的状态)
        return EventResData.success();
    }

}
