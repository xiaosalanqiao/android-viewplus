package cn.jiiiiiin.viewplus;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import android.util.Log;

import com.blankj.utilcode.util.DeviceUtils;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import cn.jiiiiiin.viewplus.icon.YNRCCIconFontModule;
import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.ui.refresh.DynamicTimeFormat;


/**
 * @author jiiiiiin
 */

public class MainApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // core模块集成Hawk，用于简化应用本地存储
        // Hawk初始化
        if (BaseConfig.IS_DEBUG) {
            Hawk.init(this).setLogInterceptor(message -> Log.d("HAWK", message)).build();
        } else {
            Hawk.init(this).build();
        }

        /**
         * 初始化类库，可以设置属性参考
         * {@link cn.jiiiiiin.vplus.core.app.Configurator}
         * {@link cn.jiiiiiin.vplus.core.app.ConfigKeys}
         *
         * 设置之后，便于类库使用，或者自身通过{@link ViewPlus#getConfiguration(Object)}获取初始化的配置
         *
         * 一下是设置一些必要配置：
         */
        ViewPlus.init(this)
                // 设置模式DEV_MODE & TEST_MODE & PROD_MODE，以便应用或库根据模式进行优化
                .withMode(BaseConfig.MODE)
                // 配置是否是debug模式
                .withDebug(BaseConfig.IS_DEBUG)
                // 应用主题色
                .withThemeColor(getResources().getColor(R.color.colorPrimaryLight))
                // 配置应用是否允许在root环境
                .withIsDeviceRooted(DeviceUtils.isDeviceRooted())
                // 应用webview的UserAgent
                .withWebUserAgent("Custom-WebUserAgent")
                // 设置服务器的Base URL
                .withApiHost("https://easy-mock.com/mock/5abc903ff5c35b191f472d79/example/")
//                .withWebHost("http://vue_viewplus_demo.jiiiiiin.cn/")
                .withWebHost("https://github.com/Jiiiiiin/android-viewplus")
                // 类库提供了一个针对server response的“业务封装”，类似https://www.yanzhenjie.com/Kalle/sample/business.html这篇文档的功能
                // 设置进行http请求时候服务器端响应json中，标识业务响应是否“success”的key值，这里指的是业务的成功
                .withServerStatusCodeKey(BaseConfig.SERVER_STATUS_CODE_KEY)
                // 设置进行http请求时候服务器端响应json中，标识业务响应是否“success”的value值，即返回这个值，标识请求后台接口处理成功，否则为处理失败
                .withServerStatusCodeSuccessFlag(BaseConfig.SERVER_STATUS_CODE)
                // 设置进行http请求时候服务器端响应json中，标识业务响应是否“非success”的提示信息（一般是错误消息）的key值，这里指的是业务的错误
                .withServerStatusMsgKey(BaseConfig.SERVER_STATUS_MSG_KEY)
                // 设置自定义字体图标
                .withIcon(new YNRCCIconFontModule())
                // 设置点击返回退出应用的检测时间
                .withExitAppWaitTime(2000L)
                .configure();

        LoggerProxy.setLoggerProxyLifecycleListener(new LoggerProxy.LoggerProxyLifecycleListener() {
            @Override
            public void e(String message) {
                Log.e("TEST", "测试LoggerProxy.setLoggerProxyLifecycleListener " + message);
            }

            @Override
            public void e(String message, Object... args) {

            }

            @Override
            public void e(Throwable exception, String message, Object... args) {

            }
        });

        LoggerProxy.e("测试日志");

        LoggerProxy.d("全局应用配置完毕 IS_PROD: %s IS_DEBUG: %s", ViewPlus.IS_PROD(), ViewPlus.IS_DEBUG());
    }

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(R.color.windowBackground, android.R.color.white);
            return new ClassicsHeader(context).setTimeFormat(new DynamicTimeFormat("更新于 %s"));
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            //指定为经典Footer，默认是 BallPulseFooter
            return new ClassicsFooter(context).setDrawableSize(20);
        });
    }


}
