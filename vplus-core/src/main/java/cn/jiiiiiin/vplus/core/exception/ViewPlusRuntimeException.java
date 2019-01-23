package cn.jiiiiiin.vplus.core.exception;

import cn.jiiiiiin.vplus.core.dict.Err;

/**
 *
 * @author jiiiiiin
 */

public class ViewPlusRuntimeException extends RuntimeException {

    private String code;

    public String getCode() {
        return code;
    }

    public ViewPlusRuntimeException() {
    }

    public ViewPlusRuntimeException(String message) {
        super(message);
    }

    public ViewPlusRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ViewPlusRuntimeException(Throwable cause) {
        super(cause);
    }

    public ViewPlusRuntimeException(String message, String code) {
        super(message);
        this.code = code;
    }

//    public ViewPlusRuntimeException(Err.CodeAndErrMsg codeAndErrMsg) {
//        this(codeAndErrMsg.getMsg(), codeAndErrMsg.getCode());
//    }

    // TODO
//    @TargetApi(Build.VERSION_CODES.N)
//    public ViewPlusException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
//        super(message, cause, enableSuppression, writableStackTrace);
//    }
}
