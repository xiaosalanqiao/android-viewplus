package cn.jiiiiiin.vplus.core.activites;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.ContentFrameLayout;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.bugtags.library.Bugtags;
import com.gyf.barlibrary.ImmersionBar;

import cn.jiiiiiin.vplus.core.R;
import cn.jiiiiiin.vplus.core.delegates.AbstractViewPlusDelegate;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * TODO 将这个基类做成集成SwipeBackActivity
 * https://www.jianshu.com/p/626229ca4dc2
 *
 * @author jiiiiiin
 */

public abstract class BaseActivity extends SupportActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContainer(savedInstanceState);
        initImmersionBar();
    }

    protected void initImmersionBar() {
        initImmersionBar(this);
    }

    public static void initImmersionBar(Activity activity) {
        try {
            // 所有子类都将继承这些相同的属性
            ImmersionBar.with(activity).init();
        } catch (Exception e) {
            LoggerProxy.e(e, "初始化ImmersionBar设置出错");
        }
    }

    /**
     * @return 返回根Delegate
     */
    public abstract AbstractViewPlusDelegate setRootDelegate();

    private void initContainer(@Nullable Bundle savedInstanceState) {
        @SuppressLint("RestrictedApi") final ContentFrameLayout container = new ContentFrameLayout(this);
        container.setId(R.id.delegate_container);
        setContentView(container);
        // TODO https://work.bugtags.com/apps/1598731013063315/issues/1603309063387626/tags/1603309064438523?types=3&versions=1600310568035606&page=1
        if (savedInstanceState == null) {
            // 初次加载主入口activity的时候设置根Delegate
            loadRootFragment(R.id.delegate_container, setRootDelegate());
        }
    }


    /**
     * 集成bugtags
     * https://docs.bugtags.com/zh/start/integrate/android/maven.html
     */
    @Override
    protected void onResume() {
        super.onResume();
        Bugtags.onResume(this);
        // ! 解决应用重启之后，PartnerWebViewWrapperDelegate这样设置的ImmersionBar失效的问题
        initImmersionBar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Bugtags.onPause(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Bugtags.onDispatchTouchEvent(this, event);
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // 记录按键操作步骤
        Bugtags.onDispatchKeyEvent(this, event);
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        // TODO 在BaseActivity、BaseFragment的onDestory()里把当前Activity所发的所有请求取消掉。
        destroyImmersionBar();
        super.onDestroy();
        // 关注：https://github.com/YoKeyword/Fragmentation/issues/877#event-1665429707
        // 单Activity架构，故做以下优化
        // System.gc();
        // System.runFinalization();
    }

    /**
     * 必须调用该方法，防止内存泄漏，不调用该方法，如果界面bar发生改变，在不关闭app的情况下，退出此界面再进入将记忆最后一次bar改变的状态
     */
    protected void destroyImmersionBar() {
        //必须调用该方法，防止内存泄漏
        ImmersionBar.with(this).destroy();
    }

    /**
     * 设置动画，也可以使用setFragmentAnimator()设置
     */
    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        // return new DefaultHorizontalAnimator();
        // 设置无动画
        // return new DefaultNoAnimator();
        // 设置自定义动画
        // return new FragmentAnimator(enter,exit,popEnter,popExit);

        // 设置默认Fragment动画  默认竖向(和安卓5.0以上的动画相同)
        return super.onCreateFragmentAnimator();
    }

}
