package cn.jiiiiiin.vplus.core.webview.event.model;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.jiiiiiin.vplus.core.app.ConfigKeys;
import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.exception.ViewPlusRuntimeException;

/**
 * @author jiiiiiin
 * @version 1.0
 */

public class EventResData {

    private String code;
    private String msg;
    private Map<String, Object> dataMap;

    public EventResData putData(@NonNull String key, Object val) {
        if (StringUtils.isEmpty(key)) {
            throw new ViewPlusRuntimeException("putdata_key_is_err", "设置返回内容的键为空错误");
        }
        if (val == null) {
            throw new ViewPlusRuntimeException("putdata_val_is_err", "设置返回内容的值为空错误");
        }
        if (this.dataMap == null) {
            this.dataMap = new HashMap<>();
        }
        this.dataMap.put(key, val);
        return this;
    }

    public EventResData() {
    }

    public EventResData(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public EventResData setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public EventResData setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public String toJson() {
        final JSONObject jsonObject = new JSONObject();
        if (dataMap != null && !dataMap.isEmpty()) {
            jsonObject.putAll(dataMap);
        }
        jsonObject.put(ViewPlus.getConfiguration(ConfigKeys.SERVER_STATUS_CODE_KEY), code);
        jsonObject.put(ViewPlus.getConfiguration(ConfigKeys.SERVER_STATUS_MSG_KEY), msg);
        return jsonObject.toJSONString();
    }

    public static EventResData success() {
        return new EventResData(ViewPlus.getConfiguration(ConfigKeys.SERVER_STATUS_CODE_SUCCESS_FLAG), "请求成功");
    }

    public static EventResData error(String code, String msg) {
        EventResData error = new EventResData();
        return error.setCode(code).setMsg(msg);
    }

    // TODO 是否使用Map<String, Object>
    public EventResData putData(Map<String, Object> data) {
        if (this.dataMap == null) {
            this.dataMap = new HashMap<>();
        }
        this.dataMap.putAll(data);
        return this;
    }

    public EventResData putData2(Map<String, String> args) {
        if (this.dataMap == null) {
            this.dataMap = new HashMap<>();
        }
        final Set<Map.Entry<String, String>> entrySet = args.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            dataMap.put(entry.getKey(), entry.getValue());
        }
        return this;
    }
}
