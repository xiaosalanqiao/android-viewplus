package com.csii.mobilebank;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.luck.picture.lib.config.PictureConfig;
import com.vector.update_app.utils.AppUpdateUtils;

import cn.jiiiiiin.vplus.core.activites.AbstractOnTouchMngProxyActivity;
import cn.jiiiiiin.vplus.core.app.ConfigKeys;
import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.delegates.AbstractViewPlusDelegate;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.ui.launcher.ILauncherListener;
import cn.jiiiiiin.vplus.ui.launcher.OnLauncherFinishTag;

/**
 * @author jiiiiiin
 */
@SuppressWarnings("ALL")
public class MainActivity extends AbstractOnTouchMngProxyActivity implements
        ILauncherListener {

    private boolean mIsFistLaunch = true;

    @Override
    public AbstractViewPlusDelegate setRootDelegate() {
        return LauncherWelcomeDelegate.newInstance();
    }

    private void _initVPAgain() {
        ViewPlus.getConfigurator()
                .withStartOtherActivity(false)
                .withActivity(this)
                .withAppFirstLaunched(mIsFistLaunch)
                .withStartThirdWebViewDelegateFlag(false);
    }

    private void _initVPAgainNeedPermissionCheck() {
//        ViewPlus.getConfigurator()
//                // ！下面几个初始化参数的顺序建议不要随便动
//                .withCustomHeaders(BaseConfig.getCustomHeaders(this))
//                .withCommonParams(BaseConfig.getCommParams())
//                .withSelfH5CommParams(BaseConfig.getSelfH5CommParams(false));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _initVPAgain();
        _needPermissionCheckInit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        final boolean startFlag = ViewPlus.getConfiguration(ConfigKeys.START_OTHER_ACTIVITY);
        if (!startFlag) {
            ToastUtils.showLong("应用已转到后台运行");
        }
    }

    private void _needPermissionCheckInit() {
        _initVPAgainNeedPermissionCheck();
    }

    @Override
    protected void onDestroy() {
        try {
            // https://github.com/Blankj/AndroidUtilCode/issues/294
            KeyboardUtils.fixSoftInputLeaks(this);
            // TODO 银联云闪付 放开
            // UPQuickPassProxy.destory();
        } catch (Exception e) {
            LoggerProxy.w("MainActivity onDestroy 销毁出现错误 %s", e.getMessage());
        }
        super.onDestroy();
    }


    @Override
    public void onLauncherFinish(OnLauncherFinishTag tag) {
        switch (tag) {
            case IS_FIRST_LAUNCH:
//                setFistLaunch(true);
                break;
            case IS_NOT_FIRST_LAUNCH:
                // 设置标识用于表示用户已经启动过app
                ViewPlus.getConfigurator().withAppFirstLaunched(true);
            default:
        }
    }



}
