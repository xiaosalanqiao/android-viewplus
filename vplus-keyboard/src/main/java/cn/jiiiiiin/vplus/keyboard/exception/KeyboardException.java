package cn.jiiiiiin.vplus.keyboard.exception;

import cn.jiiiiiin.vplus.security.exception.SecurityCypherException;

/**
 * @version 1.0
 * @auther Created by jiiiiiin on 2018/3/6.
 */

public class KeyboardException  extends SecurityCypherException {
    public KeyboardException() {
    }

    public KeyboardException(String message) {
        super(message);
    }

    public KeyboardException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyboardException(Throwable cause) {
        super(cause);
    }

    public KeyboardException(String message, String code) {
        super(message, code);
    }

}
