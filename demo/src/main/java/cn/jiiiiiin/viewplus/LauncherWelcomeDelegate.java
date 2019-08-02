package cn.jiiiiiin.viewplus;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.Nullable;


import com.gyf.immersionbar.ImmersionBar;

import cn.jiiiiiin.viewplus.jsbridge.AjaxEvent;
import cn.jiiiiiin.viewplus.jsbridge.UIEvent;
import cn.jiiiiiin.vplus.core.app.ConfigKeys;
import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.delegates.BaseDelegate;
import cn.jiiiiiin.vplus.core.exception.ViewPlusException;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.webview.AbstractWebViewWrapperCommUIDelegate;
import cn.jiiiiiin.vplus.core.webview.WebViewDelegateImpl;
import cn.jiiiiiin.vplus.core.webview.event.IEventManager;
import cn.jiiiiiin.vplus.core.webview.event.StandAloneEventManager;
import cn.jiiiiiin.vplus.core.webview.event.model.EventParams;
import cn.jiiiiiin.vplus.core.webview.event.model.EventResData;
import cn.jiiiiiin.vplus.core.webview.jsbridgehandler.context.ViewPlusContextWebInterface;
import cn.jiiiiiin.vplus.core.webview.jsbridgehandler.exception.JSBridgeException;
import cn.jiiiiiin.vplus.core.webview.util.WebViewUtil;
import lombok.val;

import static cn.jiiiiiin.vplus.core.delegates.bottom.BaseBottomItemDelegate.finishApp;

/**
 * 1.继承{@link AbstractWebViewWrapperCommUIDelegate}提供的layout，需要有一个`android:id="@+id/llc_root_container"`的根android.support.v7.widget.LinearLayoutCompat容器
 * 参考`delegate_comm_h5_wrapper_layout.xml`布局；
 * <p>
 * 2.实现{@link ViewPlusContextWebInterface.IJsBridgeHandler}接口，该接口将会被{@link ViewPlusContextWebInterface#event(String)}在收到前端调用后分别在调用自定义处理接口（见{@link UIEvent#doAction(EventParams)}）之前和将返回结果给前端之前调用响应待实现接口，
 * 以便WebView包裹Delegate具有“全局处理”的机会
 *
 * @auther Created by jiiiiiin on 2018/10/1.
 */
public class LauncherWelcomeDelegate extends AbstractWebViewWrapperCommUIDelegate implements ViewPlusContextWebInterface.IJsBridgeHandler {

    // 3.设置布局，如继承AbstractWebViewWrapperCommUIDelegate，将会对布局有特殊控件id要求，如需自定义包裹Delegate可以继承AbstractWebViewInteractiveDelegate，
    // 自己实现AbstractWebViewWrapperCommUIDelegate可能需要的逻辑
    @Override
    public Object setLayout() {
        return R.layout.delegate_comm_h5_wrapper_layout;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        super.onBindView(savedInstanceState, rootView);
        mIsBackContainerVisibleVal = View.GONE;
        setTitleBarVisible(false);
    }

    public static LauncherWelcomeDelegate newInstance() {
        final Bundle args = new Bundle();
        final LauncherWelcomeDelegate fragment = new LauncherWelcomeDelegate();
        // 设置标题，由基类处理
        args.putString(ARG_TITLE, "JSBridge测试示例");
        // 设置待加载的url，可以是本地assets目录下的html，也可以直接是一个类"https://github.com/这样的域名
        // 这里会在jsbridge-context.html中编写js call java action的示例，详见"jsbridge-context.html"
//        args.putString(ARG_URL, "jsbridge-context.html");
        args.putString(ARG_URL, ViewPlus.getConfiguration(ConfigKeys.WEB_HOST));
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 是否可以滑动返回
     *
     * @return
     */
    @Override
    protected boolean setWebDelegateSwipeBackEnable() {
        return false;
    }

    @Override
    public boolean onBackPressedSupport() {
        try {
            val webview = getWebDelegate().getWebView();
            // TODO 因为演示demo加载的是一个spa应用，路由在其内部，需要进行相互桥接才能正确的控制
            if (webview.canGoBack()) {
                webview.goBack();
            } else {
                finishApp(null);
            }
        } catch (ViewPlusException e) {
            e.printStackTrace();
            finishApp(null);
        }
        // return true，标识应用受理该事件，无需系统在进行传递
        return true;
    }

    // 4.因为Fragment或者说Fragmention框架支持嵌套Fragment，基类提供了一个popToRoot（弹出到根Delegate，即如果是单activity应用，则是回到第一个view），
    // 故需要应用提供根视图（Delegate）的clazz
    @Override
    protected Class<? extends BaseDelegate> getRootClazz() {
        return LauncherWelcomeDelegate.class;
    }

    // 5.初始化子WebView Delegate，提供初始化WebView相关的可配置参数，更多参数详见{@link WebViewDelegateImpl}及其基类
    @Override
    protected WebViewDelegateImpl initWebViewDelegateImpl() {
        // 实例化类库中专门封装WebView的Delegate
        final WebViewDelegateImpl webDelegate = WebViewDelegateImpl.newInstance(mURL, false, false, true);
        // 注册Events，这里将提供给前端的调用接口以Event组分类，是便于按业务管理不同的交互接口
        final IEventManager manager = StandAloneEventManager.newInstance()
                // 防止使用proguard混淆，所以不直接使用`UIEvent.class.getSimpleName()`
                .addEvent("AjaxEvent", AjaxEvent.newInstance())
                .addEvent("UIEvent", UIEvent.newInstance());
        webDelegate
                // 设置PageLoadListener，默认由基类实现，子类可以按需求复写，以便处理WebView相应生命周期钩子
                .setPageLoadListener(this)
                .setWrapperDelegate(this)
                //.setUrlParams(urlParams)
                //.setHeaderParams(headerParams)
                // 设置自定义Events，webDelegate将会在收到前端调用时，自动查找前端调用的event->action进行调用
                .setEventManager(manager)
                // 指定暴露在下面设置的全局上下文（浏览器windows对象下的全局对象）的名称，如这里的"ViewPlus"
                .setJavascriptInterface(ViewPlusContextWebInterface.newInstance(webDelegate, this), "ViewPlus");
        return webDelegate;
    }

    /**
     * 举例注册 {@link android.webkit.WebChromeClient#onProgressChanged(WebView, int)} 到100监听事件
     *
     * @param isMainUiThreadCall
     */
    @Override
    public void onLoadEnd(boolean isMainUiThreadCall) {
        super.onLoadEnd(isMainUiThreadCall);
        try {
            WebViewUtil.clearWebViewCache(getWebDelegate().getWebView());
        } catch (ViewPlusException e) {
            LoggerProxy.e("清理webview缓存失败");
        }
    }

    @Override
    public String onJsCallInterceptor(EventParams eventParams) throws JSBridgeException {
        // 前端调用客户端参数前处理器
        return null;
    }

    @Override
    public String onRespH5(EventResData eventResData, EventParams eventParams) {
        // 前端调用客户端参数后处理器
        return eventResData.toJson();
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        ImmersionBar.with(this).fitsSystemWindows(true).init();
    }
}
