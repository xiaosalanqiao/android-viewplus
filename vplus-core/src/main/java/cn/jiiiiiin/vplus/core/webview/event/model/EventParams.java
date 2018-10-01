package cn.jiiiiiin.vplus.core.webview.event.model;

import com.alibaba.fastjson.JSONObject;

import cn.jiiiiiin.vplus.core.exception.ViewPlusRuntimeException;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;

/**
 * @author jiiiiiin
 * @version 1.0
 */

public class EventParams {

    private String event;
    private String action;
    private String callback;
    private String listener;
    private JSONObject params;
    private String strParams;

    public static EventParams newInstance(String params) {
        try {
            final JSONObject jsonObj = JSONObject.parseObject(params);
            EventParams eventParams = new EventParams();
            eventParams.event = jsonObj.getString("event");
            eventParams.action = jsonObj.getString("action");
            eventParams.callback = jsonObj.getString("callback");
            eventParams.listener = jsonObj.getString("listener");
            eventParams.params = jsonObj.getJSONObject("params");
            eventParams.strParams = jsonObj.getString("params");
            return eventParams;
        } catch (Exception e) {
            LoggerProxy.e(e, "格式化[JSBRIDGE]交互信息失败");
            throw new ViewPlusRuntimeException(String.format("格式化[JSBRIDGE]交互信息失败 [%s]", e.getMessage()));
        }
    }

    public EventParams() {
    }

    public String getEvent() {
        return event;
    }


    public String getAction() {
        return action;
    }


    public String getCallback() {
        return callback;
    }


    public JSONObject getParams() {
        return params;
    }

    public String getListener() {
        return listener;
    }

    public String getStrParams() {
        return strParams;
    }

    @Override
    public String toString() {
        return "EventParams{" +
                "event='" + event + '\'' +
                ", action='" + action + '\'' +
                ", callback='" + callback + '\'' +
                ", listener='" + listener + '\'' +
                ", params=" + params +
                '}';
    }
}
