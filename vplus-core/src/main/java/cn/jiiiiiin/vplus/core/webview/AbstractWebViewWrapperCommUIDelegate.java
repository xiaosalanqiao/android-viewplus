package cn.jiiiiiin.vplus.core.webview;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.ScrollBoundaryDecider;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jiiiiiin.vplus.core.R;
import cn.jiiiiiin.vplus.core.R2;
import cn.jiiiiiin.vplus.core.app.ConfigKeys;
import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.util.ui.ViewUtil;
import cn.jiiiiiin.vplus.core.webview.util.BackProcessHandler;
import lombok.val;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * @author jiiiiiin
 * @version 1.0
 */

public abstract class AbstractWebViewWrapperCommUIDelegate extends AbstractWebViewInteractiveDelegate {

    /**
     * TITLE防止理财页面h5加载不出来，必须设置，名字最好和h5传递的保持一致
     */
    public static final String ARG_TITLE = "arg_title";
    public static final String ARG_URL = "arg_url";
    /**
     * 初始化时候加载的页面的url
     */
    private String mInitUrl;
    protected String mTitle;
    private ITitleBarEventListener mTitleBarEventListener;
    protected MaterialProgressBar progressBar;
    @BindView(R2.id.llc_root_container)
    protected ViewGroup mLLRootContainer;
    @BindView(R2.id.title_bar)
    protected CommonTitleBar mCommonTitleBar;
    protected boolean mIsToolbarInitialised = false;
    protected int mTitleBarVisibleVal = View.VISIBLE;
    @BindView(R2.id.title_bar_bottom_line)
    protected View bottomLine;
    protected TextView centerTextView;
    @BindView(R2.id.toolbar_close)
    protected View mToolBarCloseContainer;
    protected int mIsCloseContainerVisibleVal = View.GONE;
    @BindView(R2.id.toolbar_back)
    protected View mToolbarBackContainer;
    protected int mIsBackContainerVisibleVal = View.VISIBLE;
    @BindView(R2.id.h5_placeholder_container)
    protected ViewGroup mPlaceholderContainer;
    @BindView(R2.id.tv_empty_page_err_txt)
    protected TextView mErrTextView;
    @BindView(R2.id.rl_empty_page_container)
    protected ViewGroup mErrContainer;
    protected boolean isShowErrorLocalPage = false;
    @BindView(R2.id.srl_refresh_layout)
    protected SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R2.id.h5_toolbar_right_menu_box)
    protected ViewGroup mTitleBarRightContainer;
    private static final LinearLayout.LayoutParams TITLE_BAR_RIGHT_TV_LAYOUT_PARAMS = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    private static final LinearLayout.LayoutParams TITLE_BAR_RIGHT_ICON_LAYOUT_PARAMS = new LinearLayout.LayoutParams(45, 45);
    protected int mTitleBarBtnColor = Color.BLUE;
    /**
     * 标识是否要忽略白名单
     */
    protected boolean mIgnoreWhiteURL = false;

    @OnClick(R2.id.toolbar_back)
    public void onToolBarBackContainerClick() {
        onBackPressedSupport();
    }

    @OnClick(R2.id.toolbar_close)
    public void onToolBarCloseContainerClick() {
        // 解决用户点击了【解决EditText和软键盘的问题】导致的返回首页底部菜单会被拉伸的问题
        hideSoftInput();
        ViewPlus.getHandler().postDelayed(() -> {
            if (mTitleBarEventListener != null) {
                mTitleBarEventListener.onCloseBtnClick();
            } else {
                popToRoot();
            }
        }, 100);
    }

    @OnClick(R2.id.rl_empty_page_container)
    public void onErrRefreshBtnTap() {
        // ！刷新之后重新记录该标识
        isShowErrorLocalPage = false;
        mWebViewDelegate.refresh();
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_comm_h5_wrapper_layout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            mInitUrl = mURL = args.getString(ARG_URL);
            mTitle = args.getString(ARG_TITLE);
        }
        final View rootView = super.onCreateView(inflater, container, savedInstanceState);
        assert rootView != null;
        progressBar = rootView.findViewById(R.id.progress_bar);
        _initToolbar();
        _initSmartRefreshLayout();
        return rootView;
    }

    private void _initSmartRefreshLayout() {
        // https://github.com/scwang90/SmartRefreshLayout/blob/master/art/md_faq.md
        mSmartRefreshLayout.setEnableAutoLoadMore(false);
        // 禁止越界拖动（1.0.4以上版本）
        mSmartRefreshLayout.setEnableOverScrollDrag(false);
        // 关闭越界回弹功能
        mSmartRefreshLayout.setEnableOverScrollBounce(false);
        // 这个功能是本刷新库的特色功能：在列表滚动到底部时自动加载更多。 如果不想要这个功能，是可以关闭的：
        mSmartRefreshLayout.setEnableAutoLoadMore(false);
        final MaterialHeader mMaterialHeader = (MaterialHeader) mSmartRefreshLayout.getRefreshHeader();
        if (mMaterialHeader != null) {
            //noinspection RedundantArrayCreation
            mMaterialHeader.setColorSchemeColors(new int[]{ViewPlus.getConfiguration(ConfigKeys.APP_THEME_COLOR)});
        }
        mSmartRefreshLayout.setScrollBoundaryDecider(new ScrollBoundaryDecider() {
            @Override
            public boolean canRefresh(View content) {
                //webview滚动到顶部才可以下拉刷新
                try {
                    // https://blog.csdn.net/ahuyangdong/article/details/77773323
                    return !(mWebViewDelegate.getWebViewOrNullllll().getScrollY() > 0);
                } catch (Exception e) {
                    LoggerProxy.e(e, "canRefresh err");
                }
                return false;
            }

            @Override
            public boolean canLoadMore(View content) {
                return false;
            }

        });
        setPullRefresh(canPullRefresh());
    }

    private void _registerSmartRefreshLayoutListener() {
        mSmartRefreshLayout.setOnRefreshListener(refreshLayout -> safetyUseWebView(webView -> mWebViewDelegate.refresh()));
    }

    /**
     * 是否开启下拉刷新
     */
    protected boolean canPullRefresh() {
        return false;
    }

    /**
     * 动态设置当前页面是否可以下拉刷新
     *
     * @param canPullRefresh
     */
    public AbstractWebViewWrapperCommUIDelegate setPullRefresh(boolean canPullRefresh) {
        mSmartRefreshLayout.setEnableRefresh(canPullRefresh);
        if (canPullRefresh) {
            _registerSmartRefreshLayoutListener();
        }
        return this;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        super.onBindView(savedInstanceState, rootView);
        mPlaceholderContainer.setVisibility(isOpenH5PlaceholderPage());
    }

    @Override
    protected View titleBar() {
        return mCommonTitleBar;
    }

    protected int isOpenH5PlaceholderPage() {
        return View.VISIBLE;
    }

    @Override
    public boolean onBackPressedSupport() {
        val res = BackProcessHandler.onBack(this, this);
        if (res) {
            ViewPlus.getConfigurator().withWebViewCurrentLoadUrl(null);
        }
        return res;
    }

    protected void _initToolbar() {
        this.mCommonTitleBar.setVisibility(mTitleBarVisibleVal);
        bottomLine.setVisibility(mTitleBarVisibleVal);
        centerTextView = this.mCommonTitleBar.getCenterTextView();
        centerTextView.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(mTitle)) {
            centerTextView.setText(mTitle);
        } else {
            centerTextView.setVisibility(View.GONE);
        }
        mToolbarBackContainer.setVisibility(mIsBackContainerVisibleVal);
        mToolbarBackContainer.setOnClickListener(v -> onBackPressedSupport());
        mToolBarCloseContainer.setVisibility(mIsCloseContainerVisibleVal);
        mIsToolbarInitialised = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTitleBarBtnColor = getResources().getColor(R.color.link_color, _mActivity.getTheme());
        } else {
            mTitleBarBtnColor = getResources().getColor(R.color.link_color);
        }
    }

    public AbstractWebViewWrapperCommUIDelegate setTitleBarVisible(Boolean visible) {
        if (mIsToolbarInitialised) {
            ViewUtil.setVisibility(mCommonTitleBar, visible);
        } else {
            mTitleBarVisibleVal = visible ? View.VISIBLE : View.GONE;
        }
        return this;
    }

    public AbstractWebViewWrapperCommUIDelegate setBackBtnVisible(Boolean visible) {
        if (mIsToolbarInitialised) {
            ViewUtil.setVisibility2(mToolbarBackContainer, visible);
        } else {
            mIsBackContainerVisibleVal = visible ? View.VISIBLE : View.GONE;
        }
        return this;
    }

    public AbstractWebViewWrapperCommUIDelegate setCloseBtnVisible(Boolean visible) {
        if (mIsToolbarInitialised) {
            ViewUtil.setVisibility2(mToolBarCloseContainer, visible);
        } else {
            mIsCloseContainerVisibleVal = visible ? View.VISIBLE : View.GONE;
        }
        return this;
    }

    public AbstractWebViewWrapperCommUIDelegate setIgnoreWhiteURL(boolean isIgnoreWhiteURL){
        this.mIgnoreWhiteURL = isIgnoreWhiteURL;
        return this;
    }

    public void addTitleBarRightMenus(JSONArray finalRightMenusConfig) {
        ViewPlus.getHandler().post(() -> safetyUseWebView(webView -> {
            final int idx = finalRightMenusConfig.size();
            for (int i = 0; i < idx; i++) {
                final JSONObject menuInfo = finalRightMenusConfig.getJSONObject(i);
                final String listener = menuInfo.getString("listener");
                final String title = menuInfo.getString("title");
                final String iconUrl = menuInfo.getString("icon");
                if (StringUtils.isEmpty(listener)) {
                    LoggerProxy.e("addTitleBarRightMenus配置标题栏右边自定义菜单，传递的参数错误，监听函数名字为空导致");
                }
                if (!StringUtils.isEmpty(title)) {
                    final TextView tv = new TextView(_mActivity);
                    tv.setLayoutParams(TITLE_BAR_RIGHT_TV_LAYOUT_PARAMS);
                    tv.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    tv.setText(title);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    tv.setTextColor(mTitleBarBtnColor);
                    if (!StringUtils.isEmpty(listener)) {
                        tv.setOnClickListener(v -> safetyCallH5(listener, null));
                    }
                    mTitleBarRightContainer.addView(tv);
                } else if (!StringUtils.isEmpty(iconUrl)) {
                    final ImageView imageView = new ImageView(_mActivity);
                    imageView.setLayoutParams(TITLE_BAR_RIGHT_ICON_LAYOUT_PARAMS);
                    Glide.with(this)
                            .asBitmap()
                            .load(iconUrl)
                            .into(imageView);
                    imageView.setOnClickListener(v -> safetyCallH5(listener, null));
                    mTitleBarRightContainer.addView(imageView);
                } else {
                    LoggerProxy.e("配置标题栏右边自定义菜单，传递的参数错误，标题或者图标为空导致");
                }
            }
        }));
    }

    public void setAddTitleBarRightMenus() {
        mTitleBarRightContainer.removeAllViews();
    }

    public interface ITitleBarEventListener {
        boolean onBackBtnClick();

        void onCloseBtnClick();
    }

    public void setTitleBarEventListener(ITitleBarEventListener titleBarEventListener) {
        this.mTitleBarEventListener = titleBarEventListener;
    }

    @Override
    public void onLoadStart(WebView view) {
        super.onLoadStart(view);
        showProgressBar();
    }

    @Override
    public void onLoadEnd(boolean isMainUiThreadCall) {
        super.onLoadEnd(isMainUiThreadCall);
        // ！！！因为网页加载完毕的通知函数会和超时计时器在抢时间，这里的逻辑就是如果出现了错误页面那么就算是网页通知加载完毕也不恢复
        if (!isShowErrorLocalPage) {
            if (!isMainUiThreadCall) {
                post(this::_onLoadEndHandlerUI);
            } else {
                _onLoadEndHandlerUI();
            }
        }
    }

    private void _onLoadEndHandlerUI() {
        if (mSmartRefreshLayout != null) {
            mSmartRefreshLayout.finishRefresh();
        }
        post(this::hideProgressBar);
        if (mWebViewDelegate.isWebViewAvailable()) {
            isErrContainerVisible(false);
        }
    }

    @Override
    public void onInterceptorNoSupportProtocol(String url) {
        super.onInterceptorNoSupportProtocol(url);
    }

    @Override
    public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
        super.onReceivedError(webView, errorCode, description, failingUrl);
        LoggerProxy.d("webview代理被调用 onReceivedError %s %s %s", mURL, errorCode, failingUrl);
        // TODO 根据不同errorCode 渲染不同的错误UI

        String hintTxt = String.format("网络异常，请稍后尝试访问 [%s]", failingUrl, errorCode);
        // WebViewClient.ERROR_BAD_URL & ERROR_BAD_URL 在代理类已经被明确排除
        if (errorCode == WebViewClient.ERROR_HOST_LOOKUP
                || errorCode == WebViewClient.ERROR_UNSUPPORTED_AUTH_SCHEME
                || errorCode == WebViewClient.ERROR_CONNECT
                || errorCode == WebViewClient.ERROR_IO
                || errorCode == WebViewClient.ERROR_BAD_URL
                ) {
            hintTxt = String.format("与服务器连接发生异常，请稍后再试 [%s] ", errorCode);
        } else if (errorCode == WebViewClient.ERROR_AUTHENTICATION || errorCode == WebViewClient.ERROR_PROXY_AUTHENTICATION) {
            hintTxt = String.format("你没有权限访问 [%s]", errorCode);
        } else if (errorCode == WebViewClient.ERROR_TIMEOUT) {
            hintTxt = String.format("访问超时，请稍后再试 [%s]", errorCode);
        } else if (errorCode == WebViewClient.ERROR_REDIRECT_LOOP || errorCode == WebViewClient.ERROR_TOO_MANY_REQUESTS) {
            hintTxt = String.format("访问资源出现重复多次重定向或太多请求发送错误 [%s]", errorCode);
        } else if (errorCode == WebViewClient.ERROR_UNSUPPORTED_SCHEME) {
            hintTxt = String.format("访问不安全的协议资源错误 [%s]", errorCode);
        } else if (errorCode == WebViewClient.ERROR_FAILED_SSL_HANDSHAKE || errorCode == WebViewClient.ERROR_UNSAFE_RESOURCE) {
            hintTxt = String.format("访问不安全的SSL协议资源错误 [%s]", errorCode);
        } else if (errorCode == 404) {
            hintTxt = String.format("待访问的资源不存在 [%s]", errorCode);
        } else if (errorCode == 500) {
            hintTxt = String.format("待访问的资源发送服务器端错误 [%s]", errorCode);
        }
        onLoadPageErr(webView, hintTxt);
    }

    @Override
    public void onProgressChanged(WebView view, int progress) {
        setProgress(progress);
    }

    /**
     * 检查webview是否加载超时，如果子类觉得超时了，可以自行处理
     */
    protected void onLoadPageErr(WebView view, String hintTxt) {
        ToastUtils.showLong(hintTxt);
        // 子类如果需要自定义响应逻辑，可以直接覆写
        isShowErrorLocalPage = true;
        if (mWebViewDelegate.isWebViewAvailable()) {
            KeyboardUtils.hideSoftInput(Objects.requireNonNull(_mActivity));
            // 防止加载SPA h5没有通过桥接通知应用加载完毕
            hideProgressBar();
            mSmartRefreshLayout.finishRefresh();
            isErrContainerVisible(true);
            mErrTextView.setText(hintTxt);
        } else {
            LoggerProxy.w("网页代理对象已经被销毁，但是还调用了handlerWebViewLoadingTimeout %s", mURL);
        }
    }

    /**
     * 子类去控制 {@link AbstractWebViewWrapperCommUIDelegate#setTitleBarVisible} 状态栏样式
     *
     * @param errContainerVisible
     */
    protected void isErrContainerVisible(boolean errContainerVisible) {
        if (null != mSmartRefreshLayout) {
            mSmartRefreshLayout.finishRefresh();
        }
        hideProgressBar();
        if (mPlaceholderContainer != null && isOpenH5PlaceholderPage() == View.VISIBLE) {
            mPlaceholderContainer.setVisibility(View.GONE);
        }
        if (errContainerVisible) {
            isShowErrorLocalPage = true;
            if (null != mSmartRefreshLayout && mSmartRefreshLayout.getVisibility() == View.VISIBLE) {
                mSmartRefreshLayout.setVisibility(View.GONE);
            }
            if (null != mErrContainer && mErrContainer.getVisibility() == View.GONE) {
                mErrContainer.setVisibility(View.VISIBLE);
            }
        } else {
            isShowErrorLocalPage = false;
            if (null != mErrContainer && mErrContainer.getVisibility() == View.VISIBLE) {
                mErrContainer.setVisibility(View.GONE);
            }
            if (null != mSmartRefreshLayout && mSmartRefreshLayout.getVisibility() == View.GONE) {
                mSmartRefreshLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    public boolean isShowErrorLocalPage() {
        return isShowErrorLocalPage;
    }

    public void setShowErrorLocalPage(boolean isShowErrorLocalPage) {
        this.isShowErrorLocalPage = isShowErrorLocalPage;
    }

    public MaterialProgressBar hideProgressBar() {
        if (progressBar != null) {
            progressBar.setProgress(100);
            progressBar.setVisibility(View.GONE);
        }
        return progressBar;
    }

    public MaterialProgressBar showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        return progressBar;
    }

    public MaterialProgressBar setProgress(int progress) {
        progressBar.setProgress(progress);
        progressBar.setSecondaryProgress((10 + progress));
        return progressBar;
    }

    public void setTitle(@NonNull String title) {
        this.mTitle = title;
        if (centerTextView != null) {
            centerTextView.setText(this.mTitle);
        }
    }

    public ITitleBarEventListener getTitleBarEventListener() {
        return mTitleBarEventListener;
    }

    public SmartRefreshLayout getSmartRefreshLayout() {
        return mSmartRefreshLayout;
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();
    }
}
