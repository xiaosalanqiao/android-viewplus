package cn.jiiiiiin.vplus.core.app;

/**
 * 管理用户登录状态信息
 *
 * @author jiiiiiin
 */

public class SignStatusManager {

    private static boolean signState = false;

    private enum SignTag {
        SIGN_TAG
    }

    /**
     * 不持久化登录状态到文件中,防止意外退出后,还处于登录状态
     *
     * @param state 登录状态
     */
    public static void setSignState(boolean state) {
        SignStatusManager.signState = state;
    }

    public static boolean isSignIn() {
        return SignStatusManager.signState;
    }

    public static void checkStatus(IUserChecker checker) {
        if (isSignIn()) {
            checker.onSignIn();
        } else {
            checker.onNotSignIn();
        }
    }
}
