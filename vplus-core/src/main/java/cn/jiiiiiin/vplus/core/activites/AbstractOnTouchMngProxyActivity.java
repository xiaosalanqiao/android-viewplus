package cn.jiiiiiin.vplus.core.activites;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.MotionEvent;

import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import www.znq.com.myapplication.proxy.FragmentOnTouchMng;

/**
 * @author jiiiiiin
 */

public abstract class AbstractOnTouchMngProxyActivity extends BaseActivity implements FragmentOnTouchMng.ITouchMngProxy {

    private FragmentOnTouchMng FRAGMENT_ON_TOUCH_MNG;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FRAGMENT_ON_TOUCH_MNG = FragmentOnTouchMng.newInstance();
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {
        try {
            FRAGMENT_ON_TOUCH_MNG.dispatchTouchEvent(ev);
        } catch (FragmentOnTouchMng.FragmentOnTouchMngException e) {
            LoggerProxy.e(e, "dispatchTouchEvent err");
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void registerOnTouchListener(@NonNull FragmentOnTouchMng.OnTouchListener listener) {
        FRAGMENT_ON_TOUCH_MNG.registerOnTouchListener(listener);
    }
}
