package cn.jiiiiiin.vplus.core.delegates;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.KeyboardUtils;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jiiiiiin.vplus.core.activites.BaseActivity;
import cn.jiiiiiin.vplus.core.exception.ViewPlusRuntimeException;
import cn.jiiiiiin.vplus.core.ui.loader.LoaderCreatorProxy;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import me.yokeyword.fragmentation.anim.FragmentAnimator;
import me.yokeyword.fragmentation_swipeback.SwipeBackFragment;

/**
 * @author jiiiiiin
 */
public abstract class BaseDelegate extends SwipeBackFragment {

    protected FragmentActivity _mActivity = null;
    protected View _mRootView = null;
    protected Bundle _mArguments = null;
    protected ImmersionBar mImmersionBar = null;
    @SuppressWarnings("SpellCheckingInspection")
    private Unbinder mUnbinder = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _mActivity = getActivity();
        _mArguments = getArguments();
    }

    /**
     * 设置布局：
     *
     * @return int/view  布局文件
     */
    public abstract Object setLayout();

    /**
     * 界面初始化完毕之后调用
     * <p>在onCreateView之后调用</p>
     * <p>在ButterKnife成功绑定根布局之后调用</p>
     */
    public abstract void onBindView(@Nullable Bundle savedInstanceState, View rootView);

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (setLayout() instanceof Integer) {
            _mRootView = inflater.inflate((int) setLayout(), container, false);
        } else if (setLayout() instanceof View) {
            _mRootView = (View) setLayout();
        } else {
            throw new ViewPlusRuntimeException("设置布局（文件）错误");
        }
        // 视图绑定
        mUnbinder = ButterKnife.bind(this, _mRootView);
        onBindView(savedInstanceState, _mRootView);
        hideSoftInput();
        // 点击屏幕空白区域隐藏软键盘 https://github.com/Blankj/AndroidUtilCode/issues/294
        KeyboardUtils.clickBlankArea2HideSoftInput();
        // TODO 下面的代码待研究
        // ! https://github.com/YoKeyword/Fragmentation/blob/master/fragmentation_swipeback/README.md
        // ! https://github.com/YoKeyword/Fragmentation/issues/884
//        getSwipeBackLayout().addSwipeListener(new SwipeBackLayout.OnSwipeListener() {
//            @Override
//            public void onDragStateChange(int state) {
//                switch (state) {
//                    case STATE_FINISHED:
//                        LoggerProxy.e("侧滑返回回调");
//                        // 关闭时触发
//                        onBackPressedSupport();
//                        break;
//                    default:
//                }
//            }
//        });
        return attachToSwipeBack(_mRootView);
    }

    protected View titleBar() {
        if (isImmersionBarEnabled()) {
            LoggerProxy.w("如果需要使用沉浸式，默认是使用，则必须要重写当前方法[titleBar]，返回当前的titleBar组件");
        }
        return null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view != null) {
            final View titleBar = titleBar();
            if (titleBar != null) {
                ImmersionBar.setTitleBar(_mActivity, titleBar);
            }
        }
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        uiUpdate();
    }

    private void uiUpdate() {
        initImmersionBar();
        // 防止上一个页面启动了loading但是返回之后没有地方调用销毁，导致loading一直存在
        LoaderCreatorProxy.stopLoading();
    }

    protected boolean isImmersionBarEnabled() {
        return true;
    }

    protected void initImmersionBar() {
        if (isImmersionBarEnabled()) {
            try {
                mImmersionBar = ImmersionBar.with(this);
                mImmersionBar
                        // 字体状态栏颜色由应用自己控制
                         .fitsSystemWindows(false)
//                        // 当白色背景状态栏遇到不能改变状态栏字体为深色的设备时，解决方案 https://github.com/Jiiiiiin/ImmersionBar
//                        // 原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
                        .statusBarColor(android.R.color.white, 1f)
                        .statusBarDarkFont(true, 0.2f)
//                        .transparentStatusBar()
                        //解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
                        .keyboardEnable(true)
                        //单独指定软键盘模式
                        .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                        .init();
            } catch (Exception e) {
                LoggerProxy.e(e, "抽象webview delegate的包裹对象初始化沉浸式状态栏出错");
            }
        }
    }

//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        if (!hidden && mImmersionBar != null) {
//            mImmersionBar.init();
//        }
//    }

    @Override
    public void hideSoftInput() {
        super.hideSoftInput();
        KeyboardUtils.hideSoftInput(_mActivity);
    }

    @Override
    public void onPause() {
        // 关闭软键盘
        hideSoftInput();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    public final BaseActivity getProxyActivity() {
        return (BaseActivity) _mActivity;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _destroyImmersionBar();
    }

    private void _destroyImmersionBar() {
        //升级新版后不需要再调用destroy()
//        if (mImmersionBar != null) {
//            try {
//                mImmersionBar.destroy();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置默认Fragment动画  默认竖向(和安卓5.0以上的动画相同)
        return super.onCreateFragmentAnimator();
        // 设置横向(和安卓4.x动画相同)
//        return new DefaultHorizontalAnimator();
        // 设置自定义动画
//        return new FragmentAnimator(enter,exit,popEnter,popExit);
    }


}
