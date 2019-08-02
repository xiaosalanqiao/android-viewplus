package cn.jiiiiiin.vplus.core.util.ui;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.blankj.utilcode.util.KeyboardUtils;

/**
 * @author jiiiiiin
 */

public class KeyboardUtil {

    /**
     * 隐藏系统键盘
     */
    public static void hideSystemKeyboard(Activity activity) {
        ViewUtil.activityIsLivingCanByRun(activity, new ViewUtil.IActivityIsLivingCanByRunCallBack() {
            @Override
            public void doIt(@NonNull Activity activity) {
                KeyboardUtils.hideSoftInput(activity);
            }

            @Override
            public void onActivityIsNotLiving() {

            }
        });

//            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//            if (imm != null) {
//                View view = activity.getCurrentFocus();
//                if (view == null) {
//                    view = new View(activity);
//                }
//                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//            }
//        if (getDelegate() != null) {
//            getDelegate().getSupportDelegate().hideSoftInput();
//        }
    }

}