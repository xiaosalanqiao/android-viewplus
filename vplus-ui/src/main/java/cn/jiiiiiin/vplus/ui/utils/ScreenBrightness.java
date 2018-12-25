package cn.jiiiiiin.vplus.ui.utils;

import android.app.Activity;
import android.view.WindowManager;

import cn.jiiiiiin.vplus.core.exception.ViewPlusException;
import cn.jiiiiiin.vplus.core.util.ui.ViewUtil;

/**
 * 屏幕亮度控制 工具类
 * https://developer.android.com/training/scheduling/wakelock#java
 * @author jiiiiiin
 * @version 1.0
 */
public class ScreenBrightness {

    private Activity mActivity;

    private ScreenBrightness() {
    }

    private static class Holder {
        private static final ScreenBrightness INSTANCE = new ScreenBrightness();
    }

    public static ScreenBrightness getInstance() {
        return Holder.INSTANCE;
    }

    public ScreenBrightness widthActivity(Activity activity) throws ViewPlusException {
        if (ViewUtil.activityIsLiving(activity)) {
            this.mActivity = activity;
            return getInstance();
        } else {
            throw new ViewPlusException("activity 状态检测异常");
        }
    }

    /**
     *
     * @param brightness -1 ~ 255
     */
    private void setLight(int brightness) {
        mActivity.runOnUiThread(() -> {
            WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
            lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
            mActivity.getWindow().setAttributes(lp);
        });
    }

    public void light() {
        setLight(255);
    }

    public void normal() {
        setLight(-1);
    }

}
