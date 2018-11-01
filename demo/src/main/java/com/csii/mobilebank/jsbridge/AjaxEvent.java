package com.csii.mobilebank.jsbridge;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.StringUtils;

import java.util.Set;
import java.util.WeakHashMap;

import cn.jiiiiiin.vplus.core.net.RestOkHttpUtilsBuilder;
import cn.jiiiiiin.vplus.core.net.RestOkHttpUtilsClient;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.webview.event.BaseEvent;
import cn.jiiiiiin.vplus.core.webview.event.model.EventParams;
import cn.jiiiiiin.vplus.core.webview.event.model.EventResData;
import cn.jiiiiiin.vplus.core.webview.jsbridgehandler.exception.JSBridgeException;

/**
 * @author jiiiiiin
 * @version 1.0
 */
public class AjaxEvent extends BaseEvent {
    /**
     * h5 command设置的参数key
     */
    private static final String PARAMS = "params";
    /**
     * 客户端代理发送原生请求接口
     */
    private static final String SEND_ORIGINAL_REQUEST = "sendOriginalRequest";

    public static AjaxEvent newInstance() {
        AjaxEvent fragment = new AjaxEvent();
        return fragment;
    }

    @Override
    protected String[] getSupportActions() {
        return new String[]{SEND_ORIGINAL_REQUEST};
    }

    @Override
    protected EventResData doAction(EventParams eventParams) throws JSBridgeException {
        final String action = eventParams.getAction();
        final String listener = eventParams.getListener();
        final JSONObject param = eventParams.getParams();
        final String transCode = param.getString("transcode");
        if (StringUtils.isEmpty(transCode)) {
            throw new JSBridgeException("调用发送原生代理请求，没有传递交易码错误", "on_js_call_interceptor_transcode_is_empty");
        }
        if (StringUtils.isEmpty(listener)) {
            throw new JSBridgeException("调用发送原生代理请求，没有传递监听函数名称错误", "on_js_call_interceptor_listener_is_empty");
        }
        EventResData eventResData = null;
        switch (action) {
            case SEND_ORIGINAL_REQUEST:
                switch (transCode) {
                    // 这里可以让客户端对某些交易做个性化处理
//                    case BaseConfig.TRANSCODE_UPLOAD_IMAGE:
//                        eventResData = _doUpload(param, listener, transCode);
//                        break;
                    default:
                        eventResData = _doPost(param, transCode, listener);
                }
                break;
            default:
        }
        return eventResData;
    }

    private EventResData _doPost(JSONObject params, String transCode, String listener) {

        final RestOkHttpUtilsBuilder build = RestOkHttpUtilsClient.builder(getDelegate().getActivity())
                // loader 由前端控制
                //.loader()
                .url(transCode);
        // 真正需要发送到后端的参数
        final JSONObject reqOriginData = params.getJSONObject(PARAMS);
        if (reqOriginData!=null && !reqOriginData.isEmpty()) {
            final WeakHashMap<String, String> reqParams = new WeakHashMap<>();
            final Set<String> keySet = reqOriginData.keySet();
            for (String key : keySet) {
                reqParams.put(key, String.valueOf(reqOriginData.get(key)));
            }
            build.params(reqParams);
        }

        final RestOkHttpUtilsClient client = build
                // 业务判断由前端完成
                .ignoreCommonCheck()
                .success(response -> {
                    LoggerProxy.i("请求成功调用 ajax success response %s %s %s", transCode, listener, response.toJSONString());
                    safetyCallH5(listener, response.toJSONString());
                })
                .failure(() -> safetyCallH5(listener, EventResData.error("send_original_request_post_req_failure", "链接服务器端失败，请稍后尝试").toJson()))
                .error((errRes) -> safetyCallH5(listener, errRes))
                .build();
        client.post();
        return EventResData.success();
    }

}
