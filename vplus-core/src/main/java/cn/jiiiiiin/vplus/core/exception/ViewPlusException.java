package cn.jiiiiiin.vplus.core.exception;

import cn.jiiiiiin.vplus.core.dict.Err;

/**
 *
 * @author jiiiiiin
 */
public class ViewPlusException extends Exception {

    // TODO 设置为配置项目 @zhaojin
    private static final String PREFIEX = " [客户端]";
    private String code;

    public String getCode() {
        return code;
    }

    public ViewPlusException() {
    }

    public ViewPlusException(String message) {
        super(message.concat(PREFIEX));
    }

    public ViewPlusException(String message, Throwable cause) {
        super(message, cause);
    }

    public ViewPlusException(Throwable cause) {
        super(cause);
    }

    public ViewPlusException(String message, String code) {
        super(message);
        this.code = code;
    }

//    public ViewPlusException(Err.CodeAndErrMsg codeAndErrMsg) {
//        this(codeAndErrMsg.getMsg(), codeAndErrMsg.getCode());
//    }

    // TODO
//    @TargetApi(Build.VERSION_CODES.N)
//    public ViewPlusException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
//        super(message, cause, enableSuppression, writableStackTrace);
//    }


    public static void illegalArgument(String msg, Object... params)
    {
        throw new IllegalArgumentException(String.format(msg, params));
    }

}
