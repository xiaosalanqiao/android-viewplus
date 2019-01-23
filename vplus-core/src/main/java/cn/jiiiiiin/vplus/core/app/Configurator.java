package cn.jiiiiiin.vplus.core.app;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.webkit.WebView;

import com.blankj.utilcode.util.Utils;
import com.joanzapata.iconify.IconFontDescriptor;
import com.joanzapata.iconify.Iconify;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import cn.jiiiiiin.vplus.core.delegates.AbstractViewPlusDelegate;
import cn.jiiiiiin.vplus.core.dict.Err;
import cn.jiiiiiin.vplus.core.exception.ViewPlusRuntimeException;
import cn.jiiiiiin.vplus.core.net.callback.IRespStateHandler;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import me.yokeyword.fragmentation.Fragmentation;
import okhttp3.Interceptor;

/**
 * 管理配置相关内容
 *
 * @author jiiiiiin
 */

public class Configurator {

    private static final HashMap<Object, Object> VP_CONFIGS = new HashMap<>();
    private static final Handler HANDLER = new Handler();
    private static final ArrayList<IconFontDescriptor> ICONS = new ArrayList<>();
    private static final ArrayList<Interceptor> INTERCEPTORS = new ArrayList<>();
    private static final ArrayList<InputStream> CERTIFICATES = new ArrayList<>();
    public static final String DEV_MODE = "DEV";
    public static final String TEST_MODE = "TEST";
    public static final String PROD_MODE = "PROD";

    private Configurator() {
        VP_CONFIGS.put(ConfigKeys.CONFIG_READY, false);
        VP_CONFIGS.put(ConfigKeys.HANDLER, HANDLER);
    }

    public Configurator withWebViewCurrentLoadUrl(String url) {
        VP_CONFIGS.put(ConfigKeys.WEBVIEW_CURRENT_LOAD_URL, url);
        return this;
    }

    private static class Holder {
        private static final Configurator INSTANCE = new Configurator();
    }

    static Configurator getInstance() {
        return Holder.INSTANCE;
    }


    final HashMap<Object, Object> getVPConfigs() {
        return VP_CONFIGS;
    }

    public Configurator withStartThirdWebViewDelegateFlag(boolean flag) {
        VP_CONFIGS.put(ConfigKeys.START_THIRD_WEBVIEW_DELEGATE, flag);
        return this;
    }

    public Configurator withStartOtherActivity(boolean flag) {
        VP_CONFIGS.put(ConfigKeys.START_OTHER_ACTIVITY, flag);
        return this;
    }

    public Configurator withVersionCode(int versionCode) {
        VP_CONFIGS.put(ConfigKeys.VERSION_CODE, versionCode);
        return this;
    }

    public Configurator withVersionName(String versionName) {
        VP_CONFIGS.put(ConfigKeys.VERSION_NAME, versionName);
        return this;
    }

    public Configurator withAppFirstLaunched(boolean isFirstLaunch) {
        VP_CONFIGS.put(ConfigKeys.APP_FIRST_LAUNCHED, isFirstLaunch);
        return this;
    }

    public Configurator withMode(@NonNull String mode) {
        VP_CONFIGS.put(ConfigKeys.MODE, mode);
        return this;
    }

    public Configurator withDebug(boolean isDebug) {
        VP_CONFIGS.put(ConfigKeys.DEBUG, isDebug);
        return this;
    }

    public Configurator withThemeColor(int appThemeColor) {
        VP_CONFIGS.put(ConfigKeys.APP_THEME_COLOR, appThemeColor);
        return this;
    }

    public Configurator withIsDeviceRooted(boolean deviceRooted) {
        VP_CONFIGS.put(ConfigKeys.IS_DEVICE_ROOTED, deviceRooted);
        return this;
    }

    public Configurator withExitAppWaitTime(long waitTime) {
        VP_CONFIGS.put(ConfigKeys.EXIT_APP_WAIT_TIME, waitTime);
        return this;
    }

    public Configurator withShareImagePath(String shareImagePath) {
        VP_CONFIGS.put(ConfigKeys.SHARE_IMAGE_PATH, shareImagePath);
        return this;
    }

    public Configurator withSSLSocketFactory(@NonNull SSLSocketFactory sSLSocketFactory) {
        VP_CONFIGS.put(ConfigKeys.SSL_SOCKET_FACTORY, sSLSocketFactory);
        return this;
    }

    public Configurator withTrustManager(@NonNull X509TrustManager sSLSocketFactory) {
        VP_CONFIGS.put(ConfigKeys.SSL_TRUST_MANAGER, sSLSocketFactory);
        return this;
    }

    public Configurator withSSLHostnameVerifier(@NonNull HostnameVerifier hostnameVerifier) {
        VP_CONFIGS.put(ConfigKeys.SSL_HOSTNAME_VERIFIER, hostnameVerifier);
        return this;
    }

    public final Configurator withApiHost(@NonNull String host) {
        VP_CONFIGS.put(ConfigKeys.API_HOST, host);
        return this;
    }

    public Configurator withApiConnectTimeout(long connectTimeOut) {
        VP_CONFIGS.put(ConfigKeys.API_CONNECT_TIME_OUT, connectTimeOut);
        return this;
    }

    public Configurator withApiReadTimeout(long connectTimeOut) {
        VP_CONFIGS.put(ConfigKeys.API_READ_TIME_OUT, connectTimeOut);
        return this;
    }

    public Configurator withCustomHeaders(@NonNull Map<String, String> customHeaders) {
        if(customHeaders.isEmpty()){
            throw new ViewPlusRuntimeException("customHeaders is empty err");
        }
        VP_CONFIGS.put(ConfigKeys.CUSTOM_HEADERS, customHeaders);
        return this;
    }

    public Configurator withCommonParams(@NonNull Map<String, String> commonParams) {
        if(commonParams.isEmpty()){
            throw new ViewPlusRuntimeException("commonParams is empty err");
        }
        VP_CONFIGS.put(ConfigKeys.COMMON_PARAMS, commonParams);
        return this;
    }

    public Configurator withSelfH5CommParams(@NonNull Map<String, String> selfH5CommParams) {
        if(selfH5CommParams.isEmpty()){
            throw new ViewPlusRuntimeException("selfH5CommParams is empty err");
        }
        VP_CONFIGS.put(ConfigKeys.SELF_H5_COMM_PARAMS, selfH5CommParams);
        return this;
    }

    public Configurator withIRespStateHandler(@NonNull IRespStateHandler respStateHandler) {
        VP_CONFIGS.put(ConfigKeys.RESP_STATE_HANDLER, respStateHandler);
        return this;
    }

    public final Configurator withPasswordModulus(@NonNull String passwordModulus) {
        VP_CONFIGS.put(ConfigKeys.PASSWORD_MODULUS, passwordModulus);
        return this;
    }

    public Configurator withCookie(@NonNull String sessionId) {
        VP_CONFIGS.put(ConfigKeys.SESSION_ID, sessionId);
        return this;
    }

    public Configurator withWebView(@NonNull WebView webView) {
        VP_CONFIGS.put(ConfigKeys.WEB_VIEW, webView);
        return this;
    }

    /**
     * 初始化Iconify
     */
    private void initIcons() {
        final int size = ICONS.size();
        if (size > 0) {
            final Iconify.IconifyInitializer initializer = Iconify.with(ICONS.get(0));
            for (int i = 1; i < size; i++) {
                initializer.with(ICONS.get(i));
            }
        }
    }

    /**
     * 设置需要使用的Iconify图标配置
     * 如：.withIcon(new FontAwesomeModule())
     *
     * @param descriptor
     * @return
     */
    public final Configurator withIcon(IconFontDescriptor descriptor) {
        ICONS.add(descriptor);
        return this;
    }

    public final Configurator withInterceptor(Interceptor interceptor) {
        INTERCEPTORS.add(interceptor);
        VP_CONFIGS.put(ConfigKeys.INTERCEPTOR, INTERCEPTORS);
        return this;
    }

    public final Configurator withInterceptors(ArrayList<Interceptor> interceptors) {
        INTERCEPTORS.addAll(interceptors);
        VP_CONFIGS.put(ConfigKeys.INTERCEPTOR, INTERCEPTORS);
        return this;
    }

    public Configurator withCertificates(InputStream certificates) {
        CERTIFICATES.add(certificates);
        VP_CONFIGS.put(ConfigKeys.CERTIFICATES, CERTIFICATES);
        return this;
    }

    /**
     * 微信在返回的时候需要拉取我们的一个活动
     */
    public final Configurator withActivity(Activity activity) {
        VP_CONFIGS.put(ConfigKeys.ACTIVITY, activity);
        return this;
    }

    public Configurator withRootDelegate(AbstractViewPlusDelegate viewPlusDelegate) {
        VP_CONFIGS.put(ConfigKeys.ROOT_DELEGATE, viewPlusDelegate);
        return this;
    }

    /**
     * 浏览器加载的HOST
     */
    public Configurator withWebHost(String host) {
        VP_CONFIGS.put(ConfigKeys.WEB_HOST, host);
        return this;
    }

    /**
     * 添加全局webview可以访问的url白名单，框架将会比对host {@link cn.jiiiiiin.vplus.core.webview.AbstractWebViewInteractiveDelegate#checkAllowAccessUrls}
     * @param strings
     * @return
     */
    public Configurator withAllowAccessUrlHosts(String[] strings) {
        VP_CONFIGS.put(ConfigKeys.ALLOW_ACCESS_URL_HOSTS, strings);
        return this;
    }


    public Configurator withServerStatusCodeKey(String serverStatusCodeKey) {
        VP_CONFIGS.put(ConfigKeys.SERVER_STATUS_CODE_KEY, serverStatusCodeKey);
        return this;
    }

    public Configurator withServerStatusMsgKey(String serverStatusMsgKey) {
        VP_CONFIGS.put(ConfigKeys.SERVER_STATUS_MSG_KEY, serverStatusMsgKey);
        return this;
    }

    public Configurator withServerStatusCodeSuccessFlag(String serverStatusCode) {
        VP_CONFIGS.put(ConfigKeys.SERVER_STATUS_CODE_SUCCESS_FLAG, serverStatusCode);
        return this;
    }

    public Configurator withOriginWebViewAppCachePath(String originWebCache) {
        VP_CONFIGS.put(ConfigKeys.ORIGIN_WEBVIEW_APP_CACHE_PATH, originWebCache);
        return this;
    }


    /**
     * 设置自定义浏览器的ua
     *
     * @param webviewUseragent
     * @return
     */
    public Configurator withWebUserAgent(@NonNull String webviewUseragent) {
        VP_CONFIGS.put(ConfigKeys.WEB_USER_AGENT, webviewUseragent);
        return this;
    }


    /**
     * 设置微信应用的AppID
     *
     * @param appId
     * @return
     */
    public final Configurator withWeChatAppId(String appId) {
        VP_CONFIGS.put(ConfigKeys.WE_CHAT_APP_ID, appId);
        return this;
    }

    /**
     * 设置微信应用的AppSecret
     *
     * @param appSecret
     * @return
     */
    public final Configurator withWeChatAppSecret(String appSecret) {
        VP_CONFIGS.put(ConfigKeys.WE_CHAT_APP_SECRET, appSecret);
        return this;
    }

    public final void configure() {
        initIcons();
        VP_CONFIGS.put(ConfigKeys.CONFIG_READY, true);
        final boolean isDebug = ViewPlus.IS_DEBUG();
        // 初始化Logger
        Logger.addLogAdapter(new AndroidLogAdapter());
        if (isDebug) {
            // 设置日志级别
            LoggerProxy.setLEVEL(LoggerProxy.DEBUG);
            // 建议在Application里初始化
            Fragmentation.builder()
                    // 显示悬浮球 ; 其他Mode:SHAKE: 摇一摇唤出   NONE：隐藏
                    .stackViewMode(Fragmentation.BUBBLE)
                    .debug(true)
                    // 在遇到After onSaveInstanceState时，不会抛出异常，会回调到下面的ExceptionHandler
                    .handleException(e -> {
                        // 建议在该回调处上传至我们的Crash监测服务器
                        // 以Bugtags为例子: 手动把捕获到的 Exception 传到 Bugtags 后台。
                        LoggerProxy.e(e, "Fragmentation 统一异常捕获发现错误");
                    })
                    .install();
        } else {
            // 设置日志级别
            LoggerProxy.setLEVEL(LoggerProxy.ERROR);
        }
        // 需要插件初始化完毕之后在进行初始化
        Utils.init((Application) ViewPlus.getApplicationContext());
    }

    /**
     * 在应用程序中获取配置信息时进行预检查
     */
    private void checkConfiguration() {
        final boolean isReady = (boolean) VP_CONFIGS.get(ConfigKeys.CONFIG_READY);
        if (!isReady) {
            throw new RuntimeException("viewplus库尚未初始化");
        }
    }

    /**
     * 获取全局配置
     */
    @SuppressWarnings("unchecked")
    final <T> T getConfiguration(Object key) {
        checkConfiguration();
        final Object value = VP_CONFIGS.get(key);
        if (value == null) {
            LoggerProxy.w("待获取的[%s]配置不存在！", key);
            if (ViewPlus.IS_DEBUG()) {
                throw new ViewPlusRuntimeException("待获取的配置不存在");
            }
        }
        return (T) value;
    }

}
