package com.csii.mobilebank;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;

import cn.jiiiiiin.vplus.core.activites.AbstractOnTouchMngProxyActivity;
import cn.jiiiiiin.vplus.core.activites.BaseActivity;
import cn.jiiiiiin.vplus.core.app.ConfigKeys;
import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.delegates.AbstractViewPlusDelegate;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.ui.launcher.ILauncherListener;
import cn.jiiiiiin.vplus.ui.launcher.OnLauncherFinishTag;

/**
 * 1、这里是一个单Activity应用的Demo中的唯一Activity，其实单Activity多Fragment这里依赖[YoKeyword/Fragmentation](https://github.com/YoKeyword/Fragmentation)，
 * 并集成了ButterKnife，提供了简化开发，详见 {@link cn.jiiiiiin.vplus.core.delegates.BaseDelegate}和{@link cn.jiiiiiin.vplus.core.activites.BaseActivity}
 * <p>
 * 根Activity可以直接继承{@link cn.jiiiiiin.vplus.core.activites.BaseActivity}通过{@link BaseActivity#setRootDelegate()}设置根Delegate（Fragment）
 *
 * @author jiiiiiin
 */
@SuppressWarnings("ALL")
public class MainActivity extends AbstractOnTouchMngProxyActivity {

    @Override
    public AbstractViewPlusDelegate setRootDelegate() {
        // 2.设置根Delegate
        return LauncherWelcomeDelegate.newInstance();
    }

    private void _initVPAgain() {
        // 3.设置ViewPlus（可选）
        ViewPlus.getConfigurator()
                .withStartOtherActivity(false)
                // 设置之后，便于类库使用，或者自身通过{@link ViewPlus#getConfiguration(Object)}获取初始化的配置
                .withActivity(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _initVPAgain();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        final boolean startFlag = ViewPlus.getConfiguration(ConfigKeys.START_OTHER_ACTIVITY);
        if (!startFlag) {
            ToastUtils.showLong("应用已转到后台运行");
        }
    }

}
