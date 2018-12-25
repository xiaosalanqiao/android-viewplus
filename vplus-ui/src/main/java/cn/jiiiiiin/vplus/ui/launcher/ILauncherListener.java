package cn.jiiiiiin.vplus.ui.launcher;

/**
 * @author jiiiiiin
 */

public interface ILauncherListener {

    /**
     * 在应用初次安装完成之后回调
     * @param tag 返回用户是否登录标识
     */
    @SuppressWarnings("JavadocReference")
    void onLauncherFinish(OnLauncherFinishTag tag);
}
