package www.znq.com.myapplication.proxy;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.View;

import www.znq.com.myapplication.util.ActivityUtil;

/**
 * @author jiiiiiin
 * @version 1.0
 */
public class ActivityKeyBoardProxyBuild {

    private Activity mActivity = null;
    private int[] mHideSoftByEditViewIds = null;
    private View[] mFilterViewByIds = null;
    private boolean _initialized = false;
    private ActivityKeyBoardProxy mActivityKeyBoardProxy = null;
    private ActivityKeyBoardProxy.OnHideInputForceListener mOnHideInputForceListener = null;

    private ActivityKeyBoardProxyBuild() {
    }

    private static class Holder {
        private static final ActivityKeyBoardProxyBuild INSTANCE = new ActivityKeyBoardProxyBuild();
    }

    public static ActivityKeyBoardProxyBuild getInstance() {
        return Holder.INSTANCE;
    }

    public ActivityKeyBoardProxyBuild withActivity(@NonNull Activity activity) {
        this.mActivity = activity;
        return this;
    }

    public ActivityKeyBoardProxyBuild withHideSoftByEditViewIds(@NonNull int[] hideSoftByEditViewIds) {
        this.mHideSoftByEditViewIds = hideSoftByEditViewIds;
        return this;
    }

    public ActivityKeyBoardProxyBuild withFilterViewByIds(@NonNull View[] filterViewByIds) {
        this.mFilterViewByIds = filterViewByIds;
        return this;
    }

    public ActivityKeyBoardProxyBuild withOnHideInputForceListener(@NonNull ActivityKeyBoardProxy.OnHideInputForceListener onHideInputForceListener) {
        this.mOnHideInputForceListener = onHideInputForceListener;
        return this;
    }

    public final ActivityKeyBoardProxy build() throws Exception {
        if (ActivityUtil.activityIsLiving(mActivity)) {
            if (!_initialized) {
                mActivityKeyBoardProxy = ActivityKeyBoardProxy.newInstance(mActivity, mHideSoftByEditViewIds, mFilterViewByIds, mOnHideInputForceListener);
                _initialized = true;
            }
            return mActivityKeyBoardProxy;
        } else {
            throw new Exception("Activity is not living err");
        }
    }

}
