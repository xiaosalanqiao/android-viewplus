package cn.jiiiiiin.vplus.keyboard.util;

import android.support.annotation.NonNull;

import cn.jiiiiiin.vplus.keyboard.ui.PasswordEditText;

/**
 *
 * @author jiiiiiin
 * @date 2017/9/13
 */

public class PasswordKeyboardUtil {

    /**
     * 隐藏系统密码键盘，不同机型会存在闪屏
     *
     * @param transPassword
     */
    public static void hidePasswordKeyboard(@NonNull PasswordEditText transPassword, boolean notifyListener) {
        transPassword.closeKeyboard(notifyListener);
    }

}
