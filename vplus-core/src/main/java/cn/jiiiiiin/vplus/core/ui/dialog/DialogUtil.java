package cn.jiiiiiin.vplus.core.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;

import cn.jiiiiiin.vplus.core.R;
import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.exception.ViewPlusException;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.util.ui.ViewUtil;

/**
 * @author jiiiiiin
 * @version 1.0
 */

public class DialogUtil {

    public static void errDialog(Activity activity, @NonNull String content, DialogInterface.OnDismissListener listener) {
        ViewUtil.activityIsLivingCanByRun(activity, new ViewUtil.IActivityIsLivingCanByRunCallBack() {
            @Override
            public void doIt(@NonNull Activity activity) {
                _err(activity, content, listener);
            }

            @Override
            public void onActivityIsNotLiving() {
                final Activity rootActivity = ViewPlus.getRootActivity();
                if (ViewUtil.activityIsLiving(rootActivity)) {
                    _err(rootActivity, content, listener);
                } else {
                    listener.onDismiss(null);
                    ToastUtils.showLong(content);
                }
            }
        });
    }

    private static void _err(@NonNull Activity activity, @NonNull String content, DialogInterface.OnDismissListener listener) {
        KeyboardUtils.hideSoftInput(activity);
        new MaterialDialog.Builder(activity)
                .title(R.string.err_dialog_title)
                .content(content)
                .positiveText(R.string.confirm)
                .cancelable(false)
                .dismissListener(listener)
                .show();
    }

    public static void dialog(Activity activity, String title, String content, DialogInterface.OnDismissListener listener) {
        ViewUtil.activityIsLivingCanByRun(activity, new ViewUtil.IActivityIsLivingCanByRunCallBack() {
            @Override
            public void doIt(@NonNull Activity activity) {
                _dialog(activity, title, content, listener);
            }

            @Override
            public void onActivityIsNotLiving() {
                final Activity rootActivity = ViewPlus.getRootActivity();
                if (ViewUtil.activityIsLiving(rootActivity)) {
                    _dialog(activity, title, content, listener);
                } else {
                    listener.onDismiss(null);
                    ToastUtils.showLong(title.concat("\n").concat(content));
                }
            }
        });
    }

    private static void _dialog(@NonNull Activity activity, String title, String content, DialogInterface.OnDismissListener listener) {
        KeyboardUtils.hideSoftInput(activity);
        new MaterialDialog.Builder(activity)
                .title(title)
                .content(content)
                .positiveText(R.string.confirm)
                .cancelable(false)
                .dismissListener(listener)
                .show();
    }

    public static void confirmDialog(Activity activity, String content, MaterialDialog.SingleButtonCallback singleButtonCallback) {
        confirmDialog(activity, "请确认", content, "确认", "取消", singleButtonCallback);
    }

    public static void confirmDialog(Activity activity, String title, String content, String confirm, String cancel, MaterialDialog.SingleButtonCallback singleButtonCallback) {
        try {
            MaterialDialog.Builder builder = getBuilder(activity);
            builder.title(title)
                    .content(content)
                    .positiveText(confirm)
                    .negativeText(cancel)
                    .onAny(singleButtonCallback)
                    .show();
        } catch (Exception e) {
            LoggerProxy.e(e, "promptDialog 弹窗出错");
        }
    }

    @NonNull
    public static MaterialDialog.Builder getBuilder(Activity activity) {
        MaterialDialog.Builder builder = null;
        if (ViewUtil.activityIsLiving(activity)) {
            KeyboardUtils.hideSoftInput(activity);
            builder = new MaterialDialog.Builder(activity);
        } else {
            builder = new MaterialDialog.Builder(ViewPlus.getApplicationContext());
        }
        return builder;
    }

    public static void promptDialog(Activity activity,
                                    String content,
                                    @Nullable CharSequence hint,
                                    @Nullable CharSequence prefill, MaterialDialog.InputCallback inputCallback) {
        try {
            MaterialDialog.Builder builder = getBuilder(activity);
            builder.title(R.string.input)
                    .content(content)
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input(hint, prefill, inputCallback)
                    .show();
        } catch (Exception e) {
            LoggerProxy.e(e, "promptDialog 弹窗出错");
        }
    }

    public static void setGestureDialog(Activity activity, String content, String confirm
            , String cancel, MaterialDialog.SingleButtonCallback singleButtonCallback) {
        try {
            MaterialDialog.Builder builder = getBuilder(activity);
            builder
                    .content(content)
                    .positiveText(confirm)
                    .negativeText(cancel)
                    .cancelable(false)
                    .onAny(singleButtonCallback)
                    .show();
        } catch (Exception e) {
            LoggerProxy.e(e, "setGestureDialog 弹窗出错");
        }
    }

    public static void showMaterialDialog(Activity activity, String title, String content, String confirm, String cancel, MaterialDialog.SingleButtonCallback singleButtonCallback) {
        try {
            MaterialDialog.Builder builder = getBuilder(activity);
            builder.title(title)
                    .content(content)
                    .positiveText(confirm)
                    .negativeText(cancel)
                    .cancelable(false)
                    .onAny(singleButtonCallback)
                    .show();
        } catch (Exception e) {
            LoggerProxy.e(e, "showMaterialDialog 弹窗出错");
        }
    }

    public static void showMaterialDialog(Activity activity, String content, boolean cancelVisibility, MaterialDialog.SingleButtonCallback singleButtonCallback) {
        try {
            MaterialDialog.Builder builder = getBuilder(activity);
            MaterialDialog dialog = builder.title("提示")
                    .content(content)
                    .positiveText("确定")
                    .negativeText("取消")
                    .cancelable(false)
                    .onAny(singleButtonCallback)
                    .show();
            if (!cancelVisibility) {
                dialog.getActionButton(DialogAction.NEGATIVE).setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            LoggerProxy.e(e, "showMaterialDialog 弹窗出错");
        }
    }
}
