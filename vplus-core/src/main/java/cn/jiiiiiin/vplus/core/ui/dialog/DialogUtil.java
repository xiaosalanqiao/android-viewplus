package cn.jiiiiiin.vplus.core.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.WhichButton;
import com.afollestad.materialdialogs.actions.DialogActionExtKt;
import com.afollestad.materialdialogs.input.DialogInputExtKt;
import com.afollestad.materialdialogs.list.DialogListExtKt;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.List;

import cn.jiiiiiin.vplus.core.R;
import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.util.ui.ViewUtil;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;

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
//        new MaterialDialog.Builder(activity)
//                .title(R.string.err_dialog_title)
//                .content(content)
//                .positiveText(R.string.confirm)
//                .cancelable(false)
//                .dismissListener(listener)
//                .show();
        MaterialDialog materialDialog = new MaterialDialog(activity, MaterialDialog.getDEFAULT_BEHAVIOR());
        materialDialog.title(R.string.err_dialog_title, null)
                .message(null, content, null)
                .positiveButton(R.string.confirm, null, null)
                .cancelable(false);
        materialDialog.setOnDismissListener(listener);
        materialDialog.show();
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
//        new MaterialDialog.Builder(activity)
//                .title(title)
//                .content(content)
//                .positiveText(R.string.confirm)
//                .cancelable(false)
//                .dismissListener(listener)
//                .show();
        MaterialDialog materialDialog = new MaterialDialog(activity, MaterialDialog.getDEFAULT_BEHAVIOR());
        materialDialog.title(null, title)
                .message(null, content, null)
                .cancelable(false)
                .positiveButton(R.string.confirm, null, null);
        materialDialog.setOnDismissListener(listener);
        materialDialog.show();
    }

    public static void confirmDialog(Activity activity, String content,
                                     Function1<? super MaterialDialog, Unit> positiveCallback,
                                     Function1<? super MaterialDialog, Unit> negativeCallback) {
        confirmDialog(activity, "请确认", content, "确认",
                "取消", positiveCallback, negativeCallback);
    }

    public static void confirmDialog(Activity activity, String title, String content, String confirm,
                                     String cancel, Function1<? super MaterialDialog, Unit> positiveCallback,
                                     Function1<? super MaterialDialog, Unit> negativeCallback) {
        try {
//            MaterialDialog.Builder builder = getBuilder(activity);
//            builder.title(title)
//                    .content(content)
//                    .positiveText(confirm)
//                    .negativeText(cancel)
//                    .onAny(singleButtonCallback)
//                    .show();
            MaterialDialog materialDialog = getMaterialDialog(activity);
            materialDialog.title(null, title)
                    .message(null, content, null)
                    .positiveButton(null, confirm, positiveCallback)
                    .negativeButton(null, cancel, negativeCallback)
                    .cancelable(false)
                    .show();
        } catch (Exception e) {
            LoggerProxy.e(e, "promptDialog 弹窗出错");
        }
    }

    @NonNull
    public static MaterialDialog getMaterialDialog(Activity activity) {
        MaterialDialog materialDialog = null;
        if (ViewUtil.activityIsLiving(activity)) {
            KeyboardUtils.hideSoftInput(activity);
            materialDialog = new MaterialDialog(activity,
                    MaterialDialog.getDEFAULT_BEHAVIOR());
        } else {
            materialDialog = new MaterialDialog(ViewPlus.getApplicationContext(),
                    MaterialDialog.getDEFAULT_BEHAVIOR());
        }
        return materialDialog;
    }

    public static void promptDialog(Activity activity,
                                    String content,
                                    @Nullable CharSequence hint,
                                    @Nullable CharSequence prefill,
                                    Function2<? super MaterialDialog, CharSequence, Unit> inputCallback) {
        try {
//            MaterialDialog.Builder builder = getBuilder(activity);
//            builder.title(R.string.input)
//                    .content(content)
//                    .inputType(InputType.TYPE_CLASS_TEXT)
//                    .input(hint, prefill, inputCallback)
//                    .show();
            MaterialDialog materialDialog = getMaterialDialog(activity);
            materialDialog.title(R.string.input, null)
                    .message(null, content, null);
            materialDialog = DialogInputExtKt.input(materialDialog, ((String) hint), null, prefill,
                    null, InputType.TYPE_CLASS_TEXT, 100, true, false, inputCallback);
            materialDialog.show();
        } catch (Exception e) {
            LoggerProxy.e(e, "promptDialog 弹窗出错");
        }
    }

    public static void setGestureDialog(Activity activity, String content, String confirm
            , String cancel, Function1<? super MaterialDialog, Unit> positiveCallback,
                                        Function1<? super MaterialDialog, Unit> negativeCallback) {
        try {
//            MaterialDialog.Builder builder = getBuilder(activity);
//            builder
//                    .content(content)
//                    .positiveText(confirm)
//                    .negativeText(cancel)
//                    .cancelable(false)
//                    .onAny(singleButtonCallback)
//                    .show();
            MaterialDialog materialDialog = getMaterialDialog(activity);
            materialDialog.message(null, content, null)
                    .positiveButton(null, confirm, positiveCallback)
                    .negativeButton(null, cancel, negativeCallback)
                    .cancelable(false)
                    .show();
        } catch (Exception e) {
            LoggerProxy.e(e, "setGestureDialog 弹窗出错");
        }
    }

    public static void showMaterialDialog(Activity activity, String title, String content,
                                          String confirm, String cancel,
                                          Function1<? super MaterialDialog, Unit> positiveCallback,
                                          Function1<? super MaterialDialog, Unit> negativeCallback) {
        try {
//            MaterialDialog.Builder builder = getBuilder(activity);
//            builder.title(title)
//                    .content(content)
//                    .positiveText(confirm)
//                    .negativeText(cancel)
//                    .cancelable(false)
//                    .onAny(singleButtonCallback)
//                    .show();
            MaterialDialog materialDialog = getMaterialDialog(activity);
            materialDialog.title(null, title)
                    .message(null, content, null)
                    .positiveButton(null, confirm, positiveCallback)
                    .negativeButton(null, cancel, negativeCallback)
                    .cancelable(false)
                    .show();
        } catch (Exception e) {
            LoggerProxy.e(e, "showMaterialDialog 弹窗出错");
        }
    }

    public static void showMaterialDialog(Activity activity, String content, boolean cancelVisibility,
                                          Function1<? super MaterialDialog, Unit> positiveCallback,
                                          Function1<? super MaterialDialog, Unit> negativeCallback) {
        try {
//            MaterialDialog.Builder builder = getBuilder(activity);
//            MaterialDialog dialog = builder.title("提示")
//                    .content(content)
//                    .positiveText("确定")
//                    .negativeText("取消")
//                    .cancelable(false)
//                    .onAny(singleButtonCallback)
//                    .show();
            MaterialDialog materialDialog = getMaterialDialog(activity);
            materialDialog.title(null, "提示")
                    .message(null, content, null)
                    .positiveButton(null, "确定", positiveCallback)
                    .negativeButton(null, "取消", negativeCallback)
                    .cancelable(false)
                    .show();
            if (!cancelVisibility) {
                DialogActionExtKt.getActionButton(materialDialog,
                        WhichButton.NEGATIVE).setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            LoggerProxy.e(e, "showMaterialDialog 弹窗出错");
        }
    }

    public static void listMaterialDialog(Activity activity, List<String> items, Function3<? super MaterialDialog, Integer, String,Unit> selection){
        MaterialDialog materialDialog = getMaterialDialog(activity);
        DialogListExtKt.listItems(materialDialog, null, items,
                null, true, selection);
        materialDialog.title(null, "请选择")
                .show();
    }
}
