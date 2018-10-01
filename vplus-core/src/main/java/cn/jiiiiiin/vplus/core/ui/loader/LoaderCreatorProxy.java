package cn.jiiiiiin.vplus.core.ui.loader;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;

import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.util.ui.ViewUtil;

/**
 * loading创建器
 *
 * @author jiiiiiin
 */

public class LoaderCreatorProxy {

    /**
     * 管理所有创建的loading
     */
    private static final ArrayList<KProgressHUD> LOADERS = new ArrayList<>();

    /**
     * loader样式，可以参考插件官网进行替换（LoaderStyle中具有的选项）
     */
    public static final KProgressHUD.Style DEFAULT_LOADER = KProgressHUD.Style.SPIN_INDETERMINATE;
    public static final String DEFAULT_LABEL = "加载中...";

    /**
     * 创建dialog承载loading ui
     * 不建议传递application context，否则在web view的视图中会报错
     *
     * @param activity
     * @param type
     */
    public static void showLoading(Activity activity, KProgressHUD.Style type, String label) {
        ViewUtil.activityIsLivingCanByRun(activity, new ViewUtil.IActivityIsLivingCanByRunCallBack() {
            @Override
            public void doIt(@NonNull Activity activity) {
                try {
                    KProgressHUD dialog = KProgressHUD.create(activity)
                            .setStyle(type)
                            .setLabel(label)
                            .setCancellable(false)
                            //.setAnimationSpeed(2)
                            .setDimAmount(0.5f);
                    LOADERS.add(dialog);
                    dialog.show();
                } catch (Exception e) {
                    LoggerProxy.e(e, "showLoading err");
                }
            }

            @Override
            public void onActivityIsNotLiving() {
                LoggerProxy.w("! showLoading onActivityIsNotLiving err");
            }
        });
    }

    /**
     * 自己需要确认activity是活着的
     *
     * @param activity
     * @param type
     * @param label
     * @return
     */
    public static @Nullable
    KProgressHUD showLoadingSigle(@NonNull Activity activity, KProgressHUD.Style type, String label) {
        try {
            KProgressHUD dialog = KProgressHUD.create(activity)
                    .setStyle(type)
                    .setLabel(label)
                    .setCancellable(false)
                    //.setAnimationSpeed(2)
                    .setDimAmount(0.5f);
            dialog.show();
            return dialog;
        } catch (Exception e) {
            LoggerProxy.e(e, "showLoading err");
        }
        return null;
    }

    public static @Nullable
    KProgressHUD showLoadingSigle(@NonNull Activity activity, String label) {
        try {
            KProgressHUD dialog = KProgressHUD.create(activity)
                    .setStyle(DEFAULT_LOADER)
                    .setLabel(label)
                    .setCancellable(false)
                    //.setAnimationSpeed(2)
                    .setDimAmount(0.5f);
            return dialog;
        } catch (Exception e) {
            LoggerProxy.e(e, "showLoading err");
        }
        return null;
    }

    public static void showLoading(Activity activity) {
        showLoading(activity, DEFAULT_LABEL);
    }

    public static void showLoading(Activity activity, String label) {
        showLoading(activity, DEFAULT_LOADER, label);
    }

    public static void stopLoading() {
        for (KProgressHUD dialog : LOADERS) {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    // 会触发on cancel回调
                    dialog.dismiss();
                }
            }
        }
    }

}
