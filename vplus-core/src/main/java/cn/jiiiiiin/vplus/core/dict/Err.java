package cn.jiiiiiin.vplus.core.dict;

/**
 * @author jiiiiiin
 * @date 2017/8/4
 */
@Deprecated
public final class Err {
    // ！出现非中文错误提示，就需要修正代码了，也就是说这些提示是非正常状态的错误位置定位调试flag
    public static final String VPLUS_NOT_CONFIG_READY = "VPLUS_NOT_CONFIG_READY";
    public static final String PROTOCAL_HANDLE_JS_CALL = "protocal_handle_js_call";
    public static final String ON_REQ_SERVER_ERR_4XX = "未找到您访问的资源，请稍后尝试";
    public static final String ON_REQ_SERVER_ERR_5XX = "请求服务器出错，请稍后尝试[%s]";
    public static final String ON_REQ_SERVER_ERR_DEF_MSG = "请求服务器失败，请稍后尝试[%s]";
    public static final String PROTOCAL_HANDLE_JS_CALL_MSG = "处理前端请求出错，协议方式[%s]";

    public enum CodeAndErrMsg {

        // 示例不要直接使用DEF_ERR
        PARSE_EVENTPARAMS_ERR("parse_eventparams_err", "解析交互[EventParams]参数出错"),
        EVENTPARAMS_ISNULL_ERR("eventparams_isnull_err", "交互事件参数不能为空"),
        PARAMS_ISNULL_ERR("params_isnull_err", "请求参数不能为空"),
        DEF_ERR("def_err", "默认的错误消息");

        private String code;
        private String msg;

        CodeAndErrMsg(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public String getCode() {
            return this.code;
        }

        public String getMsg() {
            return this.msg;
        }
    }


}
