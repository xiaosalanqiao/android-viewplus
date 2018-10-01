# android-viewplus

一个安卓混合客户端开发库

# 模块
```cmd
├── demo 示例
├── vplus-core android-viewplus核心库，包含jsbridge、单activity相关类
└── vplus-ui android-viewplus UI库，包含Recycler View、九宫格手势、指纹等控件的支持库
```

# 案例

+ [云南农信手机银行](http://sj.qq.com/myapp/detail.htm?apkName=com.csii.mobilebank)

![云南农信手机银行](http://a.app.qq.com/o/image/microQr.png?pkgName=com.csii.mobilebank)

# 配置

```java

public class MainApplication extends Application {

    @SuppressWarnings("AlibabaRemoveCommentedCode")
    @Override
    public void onCreate() {
        super.onCreate();
        // core模块集成Hawk，用于简化应用本地存储
        // Hawk初始化
        if (IS_DEBUG) {
            Hawk.init(this).setLogInterceptor(message -> Log.d("HAWK", message)).build();
        } else {
            Hawk.init(this).build();
        }

        final File originWebViewAppCacheFile = new File(getCacheDir().getAbsolutePath(), "origin_web_cache");

        final String webUrl = MainApplicationInitUtil.getWebUrl(MODE);
        final String serverUrl = MainApplicationInitUtil.getServerUrl(webUrl);
        final String passwordModule = MainApplicationInitUtil.getPasswordModule(MODE);
        final HttpsCheckUtils.SSLParams sslParams = MainApplicationInitUtil.getSSLParams(this, BaseConfig.SERVER_SERVER_CERTIFICATE_FILE_NAME, IS_DEBUG);
        ViewPlus.init(this)
                .withMode(BaseConfig.MODE)
            	// 配置是否是debug模式
                .withDebug(true)
            	// 应用主题色
	            .withThemeColor(getResources().getColor(R.color.colorPrimaryLight)))
            	// 应用webview的UserAgent
                .withWebUserAgent("Custom-WebUserAgent")
                .withIsDeviceRooted(DeviceUtils.isDeviceRooted())
            	// 关于http模块:
            	// 服务端url，如：http://emobile.jiiiiiin.cn/pweb/
                .withApiHost(serverUrl)
                .withServerStatusCodeKey(BaseConfig.SERVER_STATUS_CODE_KEY)
                .withServerStatusCodeSuccessFlag(BaseConfig.SERVER_STATUS_CODE)
                .withServerStatusMsgKey(BaseConfig.SERVER_STATUS_MSG_KEY)
            	// 请求链接超时配置(秒)
                .withApiConnectTimeout(60)
            	// 请求读取数据超时配置(秒)
                .withApiReadTimeout(60)
            	// 主要的webview前端应用url，如前后端分离中的某个前端SPA应用主域名
                .withWebHost(webUrl)
            	// webview允许自行加载的url白名单，在webview拦截跳转地址时候将会匹配每个url的host
                .withAllowAccessUrlHosts(IS_DEBUG ? new String[]{"https://mcashier.95516.com", "http://202.101.25.188:10533"} : new String[]{"https://mcashier.95516.com", "https://wallet.95516.com"})
                .configure();

        LoggerProxy.d("全局应用配置完毕 IS_PROD: %s IS_DEBUG: %s", ViewPlus.IS_PROD(), ViewPlus.IS_DEBUG());
    }
}
```

其他更多配置，请看`cn.jiiiiiin.vplus.core.app.Configurator`;



# 关于自定义的JSBridge

+ 客户端：

  比如客户端提供前端一个toast接口：

  ```java
  package com.csii.mobilebank.ynrcc.event.self;
  
  import android.app.Activity;
  import android.support.annotation.NonNull;
  import android.text.TextUtils;
  import android.widget.Toast;
  
  import com.afollestad.materialdialogs.DialogAction;
  import com.afollestad.materialdialogs.MaterialDialog;
  import com.alibaba.fastjson.JSONArray;
  import com.alibaba.fastjson.JSONObject;
  import com.blankj.utilcode.util.KeyboardUtils;
  import com.blankj.utilcode.util.StringUtils;
  import com.blankj.utilcode.util.ToastUtils;
  
  import cn.jiiiiiin.vplus.core.ui.loader.LoaderCreatorProxy;
  import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
  import cn.jiiiiiin.vplus.core.util.ui.ViewUtil;
  import cn.jiiiiiin.vplus.core.webview.AbstractWebViewWrapperCommUIDelegate;
  import cn.jiiiiiin.vplus.core.webview.event.AbstractEvent;
  import cn.jiiiiiin.vplus.core.webview.event.BaseEvent;
  import cn.jiiiiiin.vplus.core.webview.event.model.EventParams;
  import cn.jiiiiiin.vplus.core.webview.event.model.EventResData;
  import cn.jiiiiiin.vplus.core.webview.jsbridgehandler.exception.JSBridgeException;
  
  import static cn.jiiiiiin.vplus.core.ui.loader.LoaderCreatorProxy.showLoading;
  
  /**
   * @author jiiiiiin
   * @version 1.0
   */
  
  @SuppressWarnings({"AlibabaClassNamingShouldBeCamel"})
  // 1.继承`cn.jiiiiiin.vplus.core.webview.event.BaseEvent`
  // 声明一个**UIEvent**
  public class UIEvent extends BaseEvent {
  	
      // 2.定义**toast**名字的action接口
      private static final String TOAST = "toast";
  
      public static AbstractEvent newInstance() {
          return new UIEvent();
      }
  
      @Override
      protected String[] getSupportActions() {
          // 3.配置action接口
          return new String[]{TOAST};
      }
  
      @Override
      protected EventResData doAction(EventParams eventParams) throws JSBridgeException {
          // 4.解析前端需要调用的action接口名称
          final String action = eventParams.getAction();
          // 5.获取前端传递的json参数(可选)
          final JSONObject params = eventParams.getParams();
          EventResData eventResData = null;
          switch (action) {
              case TOAST:
                  // 6.执行接口
                  eventResData = _toast(params);
                  break;
              default:
          }
          return eventResData;
      }
  
      private EventResData _toast(final JSONObject params) {
          // 7.获取特定参数(前端传递)
          final int duration = params.getInteger("duration");
          final String msg = params.getString("msg");
          switch (duration) {
              case Toast.LENGTH_LONG:
                  // 8.打印toast
                  ToastUtils.showLong(msg);
                  break;
              case Toast.LENGTH_SHORT:
                  ToastUtils.showShort(msg);
                  break;
              default:
          }
          // 9.返回action接口调用状态(即**同步**返回接口是否调用成功的状态)
          return EventResData.success();
      }
  
  
  }
  
  ```

  关联：

  ```java
  package com.csii.mobilebank;
  
  import com.csii.mobilebank.jsbridge.UIEvent;
  
  import cn.jiiiiiin.vplus.core.delegates.BaseDelegate;
  import cn.jiiiiiin.vplus.core.webview.AbstractWebViewWrapperCommUIDelegate;
  import cn.jiiiiiin.vplus.core.webview.WebViewDelegateImpl;
  import cn.jiiiiiin.vplus.core.webview.event.IEventManager;
  import cn.jiiiiiin.vplus.core.webview.event.StandAloneEventManager;
  import cn.jiiiiiin.vplus.core.webview.event.model.EventParams;
  import cn.jiiiiiin.vplus.core.webview.event.model.EventResData;
  import cn.jiiiiiin.vplus.core.webview.jsbridgehandler.context.ViewPlusContextWebInterface;
  import cn.jiiiiiin.vplus.core.webview.jsbridgehandler.exception.JSBridgeException;
  
  /**
   * 集成{@link AbstractWebViewWrapperCommUIDelegate}提供的layout，需要有一个`android:id="@+id/llc_root_container"`的根android.support.v7.widget.LinearLayoutCompat容器
   * 参考`delegate_comm_h5_wrapper_layout.xml`布局；
   *
   * @auther Created by jiiiiiin on 2018/10/1.
   */
  public class LauncherWelcomeDelegate extends AbstractWebViewWrapperCommUIDelegate implements ViewPlusContextWebInterface.IJsBridgeHandler {
      @Override
      public Object setLayout() {
          return R.layout.delegate_comm_h5_wrapper_layout;
      }
  
      public static LauncherWelcomeDelegate newInstance() {
          return new LauncherWelcomeDelegate();
      }
  
      @Override
      protected Class<? extends BaseDelegate> getRootClazz() {
          return LauncherWelcomeDelegate.class;
      }
  
      @Override
      protected WebViewDelegateImpl initWebViewDelegateImpl() {
          final WebViewDelegateImpl webDelegate = WebViewDelegateImpl.newInstance("jsbridge-context.html", false, false, true);
  		// 注册event，使用addEvent可以添加多个类似UIEvent的处理对象
          final IEventManager manager = StandAloneEventManager.newInstance()
                  .addEvent("UIEvent", UIEvent.newInstance());
          webDelegate
                  .setPageLoadListener(this)
                  .setEventManager(manager)
                  .setWrapperDelegate(this)
                  //.setUrlParams(urlParams)
                  //.setHeaderParams(headerParams)
                  .setJavascriptInterface(ViewPlusContextWebInterface.newInstance(webDelegate, this), "ViewPlus");
          return webDelegate;
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
  }
  
  ```

+ 前端（如SPA应用需要调用客户端事件）：

  ```html
  <html>
      <body>
          <li>
              <button id="toastMessage">调用原生弹出一个Toast</button>
          </li>
      </body>
  	
      <script type="text/javascript">
          
          var CONTEXT_NAME = undefined
          // 1.获取客户端提供的上下文对象，ViewPlus是客户端配置的上下文对象的名称，即：
          // .setJavascriptInterface(ViewPlusContextWebInterface.newInstance(webDelegate, jsBridgeResHandler), JAVASCRIPT_INTERFACE_NAME)
          if (typeof window['ViewPlus'] !== "undefined") {
              CONTEXT_NAME = 'ViewPlus'
          } else {
              alert("没有找到ViewPlus上下文接口");
          }
  
          // 2.可以封装一个基础接口，提供其他地方便捷调用
          function callClient(command) {
              if (CONTEXT_NAME) {
                  var context = window[CONTEXT_NAME]
                  if (typeof context !== "undefined" && context.event) {
                      try {
                          // 3.调用vplus-core模块中自定义默认交互接口**event**
                          var res = context.event(JSON.stringify(command))
                          // 4.解析客户端同步返回的消息，即`EventResData.success();`的json格式化数据
                          var data = JSON.parse(res)
                          // 5.判断是否请求成功
                          if (!data || !data.ReturnCode || data.ReturnCode !== '000000') {
                              alert('请求出错：' + JSON.stringify(res))
                          }
                          return data
                      } catch (e) {
                          alert('客户端抛出异常：' + e.message)
                      }
                  } else {
                      alert('没有找到上下文接口' + contextName)
                  }
              }
          }
  
             function toastMessage(message) {
                 // 6.调用封装接口
              callClient({
                  // 7.指定调用java的那个event，即上面的`com.csii.mobilebank.ynrcc.event.self.UIEvent`类，建议以类名作为event name，类比模块名
                  event: 'UIEvent',
                  // 8.指定调用那个模块的那个接口，如这里就是调用`UIEvent#toast`接口
                  action: 'toast',
                  // 9.指定传递的参数，后续会被传递到对应event的`doAction`接口，映射到`eventParams.getParams()`
                  params: {
                      msg: message,
                      duration: 1,
                      test: 2
                  }
              })
          }
      </script>
  </html>
  
  ```

  如何关联Event模块，想看demo实例；

  示例：

  ![](https://ws2.sinaimg.cn/large/006tNc79gy1fvt3ncma2pj30z01md7b6.jpg)