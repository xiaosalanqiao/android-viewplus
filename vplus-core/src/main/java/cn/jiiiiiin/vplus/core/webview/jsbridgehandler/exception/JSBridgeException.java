package cn.jiiiiiin.vplus.core.webview.jsbridgehandler.exception;

import cn.jiiiiiin.vplus.core.dict.Err;
import cn.jiiiiiin.vplus.core.exception.ViewPlusException;

/**
 * @author jiiiiiin
 */

public class JSBridgeException extends ViewPlusException {

    public JSBridgeException() {
    }

    public JSBridgeException(String message) {
        super(message);
    }

    public JSBridgeException(String message, Throwable cause) {
        super(message, cause);
    }

    public JSBridgeException(Throwable cause) {
        super(cause);
    }

    public JSBridgeException(String message, String code) {
        super(message, code);
    }

//    public JSBridgeException(Err.CodeAndErrMsg codeAndErrMsg) {
//        super(codeAndErrMsg);
//    }
}
