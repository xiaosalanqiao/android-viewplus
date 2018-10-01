package cn.jiiiiiin.vplus.core.app;

/**
 * 判断用户是否登录
 *
 * @author jiiiiiin
 */

public interface IUserChecker {

    void onSignIn();

    void onNotSignIn();
}
