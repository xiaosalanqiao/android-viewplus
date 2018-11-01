# android-viewplus

一个安卓混合客户端开发库

# 目录

+ [特性](#特性)
+ [参考](#参考)
+ [模块](#模块)
+ [案例](#案例)
+ [示例](#示例)
+ [更新](#更新)

# 特性

+ 提供了一个简单实用的Http模块，支持**业务封装**，类[Kalle业务封装](https://www.yanzhenjie.com/Kalle/sample/business.html)
+ 提供了一个开发单Activity应用的模块，底层基于了[YoKeyword/Fragmentation](https://github.com/YoKeyword/Fragmentation)实现下面所要说的**Delegate视图**
+ 提供了一个自定义JSBridge模块，客户端只用实现自己的Action实例，并注入类库的管理方法，就可以简单的完成js和java的交互（建议使用上下文模式4.4以上，也支持协议模式）



# 参考：

> [Android通用框架设计与完整电商APP开发](https://coding.imooc.com/class/116.html)

主要参考了**傅猿猿**老师的上诉课程，感谢感谢！



# 模块

```cmd
├── demo 示例
├── vplus-core android-viewplus核心库，包含自定义jsbridge、单activity快速开发相关类
└── vplus-ui android-viewplus 可选、可选、可选！！！ UI库，包含Recycler View、九宫格手势、指纹等控件的支持库
```



# 更新

#### 20181101

- 修复主分支的toast接口前端没有调用问题
- 添加前端通过`AjaxEvent`对应接口发送代理请求，解决跨域问题示例

#### 20181015

- 修复为把`gradle`目录添加导致直接download项目无法编译的问题
- 更新`butterknife`依赖版本到最新**9.0.0-rc1**
- 剔除`signinfo.properties`文件的配置，让demo项目顺利运行



# 使用



+ project build.gradle添加`maven { url "https://dl.bintray.com/vplus/android-viewplus" }`仓库(可选，已上传至jenter)

```gr
allprojects {
    repositories {
        google()
        mavenCentral()
       	// ...
        maven { url "https://dl.bintray.com/vplus/android-viewplus" }
    }
}

```

+ module build.gradle 添加库依赖

```gr
dependencies {
	//...
    api 'cn.jiiiiiin:vplus-core:1.0.2'
    api 'cn.jiiiiiin:vplus-ui:1.0.1'
}
```



# 案例

+ [云南农信手机银行](http://sj.qq.com/myapp/detail.htm?apkName=com.csii.mobilebank)

![云南农信手机银行](http://a.app.qq.com/o/image/microQr.png?pkgName=com.csii.mobilebank)

|                                                              |                                                              |
| ------------------------------------------------------------ | :----------------------------------------------------------: |
| ![](https://ws2.sinaimg.cn/large/006tNbRwgy1fvu7rnokp1j30pi13mguh.jpg) | ![](https://ws2.sinaimg.cn/large/006tNbRwgy1fvu7s5hg1uj30pi13maki.jpg) |

虽然是Hybrid开发库，但是**vplus-ui android-viewplus 可选、可选、可选！！！** 也集成了：

+ [YoKeyword/Fragmentation](https://github.com/YoKeyword/Fragmentation) A powerful library that manage Fragment for Android!

+ [CymChad/BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper) BRVAH:Powerful and flexible RecyclerAdapter <http://www.recyclerview.org/>
+ [QMUI/QMUI_Android](https://github.com/QMUI/QMUI_Android) 提高 Android UI 开发效率的 UI 库 <http://qmuiteam.com/android>

 等，故开发原生应用也是很ok的 ：）



# 示例

```java
package com.csii.mobilebank;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.blankj.utilcode.util.DeviceUtils;
import com.csii.mobilebank.icon.YNRCCIconFontModule;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.ui.refresh.DynamicTimeFormat;

import static com.csii.mobilebank.BaseConfig.IS_DEBUG;


/**
 * @author jiiiiiin
 * @date 2017/8/4
 */

public class MainApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

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

```

其他更多配置，请看`cn.jiiiiiin.vplus.core.app.Configurator`;

## 单Activity开发示例

+ 定义根Activity

```java
package com.csii.mobilebank;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;

import cn.jiiiiiin.vplus.core.activites.AbstractOnTouchMngProxyActivity;
import cn.jiiiiiin.vplus.core.activites.BaseActivity;
import cn.jiiiiiin.vplus.core.app.ConfigKeys;
import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.delegates.AbstractViewPlusDelegate;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.ui.launcher.ILauncherListener;
import cn.jiiiiiin.vplus.ui.launcher.OnLauncherFinishTag;

/**
 * 1、这里是一个单Activity应用的Demo中的唯一Activity，其实单Activity多Fragment这里依赖[YoKeyword/Fragmentation](https://github.com/YoKeyword/Fragmentation)，
 * 并集成了ButterKnife，提供了简化开发，详见 {@link cn.jiiiiiin.vplus.core.delegates.BaseDelegate}和{@link cn.jiiiiiin.vplus.core.activites.BaseActivity}
 * <p>
 * 根Activity可以直接继承{@link cn.jiiiiiin.vplus.core.activites.BaseActivity}通过{@link BaseActivity#setRootDelegate()}设置根Delegate（Fragment）
 *
 * @author jiiiiiin
 */
@SuppressWarnings("ALL")
public class MainActivity extends AbstractOnTouchMngProxyActivity {

    @Override
    public AbstractViewPlusDelegate setRootDelegate() {
        // 2.设置根Delegate
        return LauncherWelcomeDelegate.newInstance();
    }

    private void _initVPAgain() {
        // 3.设置ViewPlus（可选）
        ViewPlus.getConfigurator()
                .withStartOtherActivity(false)
                // 设置之后，便于类库使用，或者自身通过{@link ViewPlus#getConfiguration(Object)}获取初始化的配置
                .withActivity(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _initVPAgain();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        final boolean startFlag = ViewPlus.getConfiguration(ConfigKeys.START_OTHER_ACTIVITY);
        if (!startFlag) {
            ToastUtils.showLong("应用已转到后台运行");
        }
    }

}

```

## 混合客户端快速开发示例

+ 客户端：

  1. 定义客户端接口
  比如客户端提供前端一个toast接口：

  ```java
  package com.csii.mobilebank.jsbridge;
  
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
          return new String[]{
                  // 3.注册接口：配置action接口
                  TOAST
          };
      }
  
      @Override
      protected EventResData doAction(EventParams eventParams) throws JSBridgeException {
          // 4.解析前端需要调用的action接口名称
          final String action = eventParams.getAction();
          // 5.获取前端传递的json参数(可选)
          final JSONObject params = eventParams.getParams();
          final String listener = eventParams.getListener();
          EventResData eventResData = null;
          switch (action) {
              case TOAST:
                  // 6.执行接口
                  eventResData = _toast(params, listener);
                  break;
              default:
          }
          return eventResData;
      }
  
  
      private EventResData _toast(final JSONObject params, final String listener) {
          // 7.获取特定参数(前端传递)
          final int duration = params.getInteger("duration");
          final String msg = params.getString("msg");
          switch (duration) {
              case Toast.LENGTH_LONG:
                  // 8.打印toast
                  ToastUtils.showLong(msg);
                  // 模拟异步执行其他事情
                  new Thread(() -> {
                      // 10.异步通知前端，即java调用前端js
                      safetyCallH5(listener, String.format("{\"toastTask\":\"%s\"}", "模拟异步执行其他事情完成，通知前端"));
                  }).start();
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

  2. 定义包裹WebView的**Delegate**：

    Delegate即Fragment，这里集成了[YoKeyword/Fragmentation](https://github.com/YoKeyword/Fragmentation)，并做了简单的封装以便快速开发，单Activity应用，下面就定义一个包裹一个内建WebDelegate的父Delegate，方便控制WebView之外的UI，比如HeaderBar；
    `AbstractWebViewWrapperCommUIDelegate`可以处理和子Delegate（Fragment）的交互，持有子Delegate实例，并监听WebView相关生命周期钩子；
    继承`AbstractWebViewWrapperCommUIDelegate`可以快速的进行混合客户端的开发。

    这里就是一个定义根Activity的根Delegate示例：

  ```java
  package com.csii.mobilebank;
  
  import android.os.Bundle;
  
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
  
      public static LauncherWelcomeDelegate newInstance() {
          final Bundle args = new Bundle();
          final LauncherWelcomeDelegate fragment = new LauncherWelcomeDelegate();
          // 设置标题，由基类处理
          args.putString(ARG_TITLE, "JSBridge测试");
          // 设置待加载的url，可以是本地assets目录下的html，也可以直接是一个类"https://github.com/这样的域名
          // 这里会在jsbridge-context.html中编写js call java action的示例，详见"jsbridge-context.html"
          args.putString(ARG_URL, "jsbridge-context.html");
          fragment.setArguments(args);
          return fragment;
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
  }
  
  ```

  3. 前端（如SPA应用需要调用客户端事件）：

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



## Http模块示例：



+ 配置SSL单项认证（客户端认证服务器端证书）示例

  ```java
  final HttpsCheckUtils.SSLParams sslParams = MainApplicationInitUtil.getSSLParams(this, BaseConfig.SERVER_SERVER_CERTIFICATE_FILE_NAME, IS_DEBUG);
  
  ViewPlus.init(this)
      			// 配置SSLSocketFactory和X509TrustManager代理对象
                  .withSSLSocketFactory(sslParams.sSLSocketFactory)
                  .withTrustManager(sslParams.trustManager)
                  // 对服务器证书预埋进行强校验允许域名
               .withSSLHostnameVerifier(MainApplicationInitUtil.getHostnameVerifier(Uri.parse(serverUrl).getHost(), IS_DEBUG));
  ```

  ```java
  public static HttpsCheckUtils.SSLParams getSSLParams(@NonNull Application mainApplication, @NonNull String serverCertificateFileName, boolean isDebug) {
          HttpsCheckUtils.SSLParams sslParams = null;
          boolean isServerCertificateValidOK = _checkServerCertificate(mainApplication, serverCertificateFileName);
          Log.d("getSSLParams", "ssl证书有效期检测: " + isServerCertificateValidOK);
          if (!isDebug && isServerCertificateValidOK) {
              InputStream serverCertificateInputStream = null;
              // 方法三：使用预埋证书，校验服务端证书（自签名证书）
              // java.security.cert.CertPathValidatorException: Trust anchor for certification path not found.
              // https://developer.android.com/training/articles/security-ssl?hl=zh-cn#UnknownCa
              try {
                  serverCertificateInputStream = new BufferedInputStream(mainApplication.getAssets().open(serverCertificateFileName));
                  sslParams = HttpsCheckUtils.getSslSocketFactory(serverCertificateInputStream);
              } catch (IOException e) {
                  e.printStackTrace();
              } finally {
                  if (serverCertificateInputStream != null) {
                      CloseUtils.closeIO(serverCertificateInputStream);
                  }
              }
  
          } else {
              LoggerProxy.w("测试模式模式，没有对数据进行ssl加密配置，存在安全隐患！");
              // 方法一：信任所有证书,不安全有风险
              sslParams = HttpsCheckUtils.getSslSocketFactory();
              if (!isDebug) {
                  ToastUtils.showLong("SLL检测失败，请检查客户端是否是最新版");
              }
          }
  
          return sslParams;
      }
  
      /**
       * 检查证书目前是否有效。即当前的日期和时间是否仍在证书中所给定的有效期内。
       * 有效期由两个日期/时间值组成：证书有效的起始和终止日期（和时间）。在 ASN.1 中定义如下：
       * <p>
       * 抛出：
       * CertificateExpiredException - 如果证书已过期。
       * CertificateNotYetValidException - 如果证书不再有效。
       *
       * @param context
       * @param serverCertificateFileName
       */
      private static boolean _checkServerCertificate(@NonNull Context context, @NonNull String serverCertificateFileName) {
          boolean isOK;
          try {
              final BufferedInputStream serverCertificateInputStream = new BufferedInputStream(context.getAssets().open(serverCertificateFileName));
              final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
              final X509Certificate serverCertificate = (X509Certificate) certificateFactory.generateCertificate(serverCertificateInputStream);
              serverCertificate.checkValidity();
              isOK = true;
          } catch (IOException e) {
              isOK = false;
              Log.e("_checkServerCertificate", "_checkServerCertificate 读取待检测的证书失败");
              e.printStackTrace();
          } catch (CertificateException e) {
              isOK = false;
              Log.e("_checkServerCertificate", "_checkServerCertificate ssl证书检测出当前应用证书已经失效");
              e.printStackTrace();
          }
          return isOK;
      }
  
      /**
       * https://blog.csdn.net/u010142437/article/details/42296557
       *
       * @param serverHostName
       * @return
       */
      public static HostnameVerifier getHostnameVerifier(@NonNull String serverHostName, boolean isdebug) {
          if (!isdebug) {
              return (hostname, session) -> {
                  if (serverHostName.equals(hostname)) {
                      return true;
                  } else {
                      HostnameVerifier hv =
                              HttpsURLConnection.getDefaultHostnameVerifier();
                      return hv.verify(serverHostName, session);
                  }
              };
          } else {
              return (hostname, session) -> true;
          }
      }
  ```

+ 发起请求示例

  一个获取时间戳示例，这里需要提一下，单Activity在进行“异步”操作时建议使用`ViewUtil.activityIsLivingCanByRun`检测根Activity是否被销毁

  ```java
  
  // http模块发送请求，很简单可以看RestOkHttpUtilsClient的构建方法
  RestOkHttpUtilsClient.builder(activity)
  .loader()
  .url("QryMobileClientVerNew.do")
  //.success(设置成功处理函数)
  //.failure(设置业务失败处理函数)
  //.error(设置请求错误处理函数)
  .build()
  .post();
  
  ```

+ 业务封装示例

  需要实现以下接口：

  ```java
  public interface IRespStateHandler {
  
      /**
       * 校验服务器返回结果是否是业务级别的成功
       *
       * @param res 服务端返回的数据
       * @return true 标识成功 反之为失败
       */
      boolean onRespCheckStateIsOk(String res) throws ViewPlusException;
  
      /**
       * 处理错误消息，在校验服务器返回结果是错误的情况下
       *
       * @param res
       */
      void onRespErrHandler(String res);
  }
  
  
  public class RespStateHandlerProxy {
  
      public static final String ON_HANDLER_SERVER_RETURNCODE_IS_EMPTY = "服务端响应状态码为空";
      public static final String ON_HANDLER_SERVER_RESSTR_IS_EMPTY = "服务端返回结果为空 [%s]";
      public static final String ON_HANDLER_SERVER_RESSTR_IS_VALID_JSONOBJ = "服务端返回的不是一个正确的json字符串";
      private static final Handler HANDLER = ViewPlus.getHandler();
  
      /**
       * 解析服务器响应状态码
       *
       * @param resStrData
       * @return 默认视为失败 false, 业务成功返回true
       */
      @SuppressWarnings({"AlibabaLowerCamelCaseVariableNaming", "AlibabaAvoidStartWithDollarAndUnderLineNaming"})
      public static boolean stateIsSuccess(String resStrData) throws ViewPlusException {
          boolean res = false;
          if (!TextUtils.isEmpty(resStrData)) {
              try {
                  JSONObject jsonObject = JSON.parseObject(resStrData);
                  if (!jsonObject.isEmpty()) {
                      final String code = jsonObject.getString(BaseConfig.SERVER_STATUS_CODE_KEY);
                      if (!TextUtils.isEmpty(code) && code.equals(BaseConfig.SERVER_STATUS_CODE)) {
                          res = true;
                      }
                  }
              } catch (Exception e) {
                  LoggerProxy.e(e, "解析服务端返回数据（需要json字符串）出错");
                  throw new ViewPlusException("解析服务端返回数据[json字符串]出错");
              }
          }
          return res;
      }
  
      @SuppressWarnings({"AlibabaLowerCamelCaseVariableNaming", "AlibabaAvoidStartWithDollarAndUnderLineNaming"})
      private static void _err(Activity mainActivity, String msg) {
          ViewUtil.activityIsLivingCanByRun(mainActivity, new ViewUtil.AbstractActivityIsLivingCanByRunCallBack() {
              @Override
              public void doIt(@NonNull Activity activity) {
                  HANDLER.post(() -> new MaterialDialog.Builder(mainActivity)
                          .title(R.string.err_dialog_title)
  //                .content(Base64Util.base64DecodeToStr(msg))
                          .content(msg)
                          .positiveText(R.string.confirm)
                          .show());
              }
          });
  
  
      }
  
      public static void handlerRespErrMsg(Activity mainActivity, IRespCommErrHandler respCommErrHandler, String res) {
          if (!TextUtils.isEmpty(res)) {
              try {
                  JSONObject jsonObject = JSON.parseObject(res);
                  if (!jsonObject.isEmpty()) {
                      final String code = jsonObject.getString(BaseConfig.SERVER_STATUS_CODE_KEY);
                      if (!TextUtils.isEmpty(code)) {
                          switch (code) {
                              case "role.invalid_user":
                                  respCommErrHandler.onSessionTimeOut(jsonObject);
                                  break;
                              case "validation.user.force.logout.exception":
                                  respCommErrHandler.onSessionTimeOut(jsonObject);
                                  break;
                              case "core_error_unauthorized":
                                  respCommErrHandler.onUnauthorized(jsonObject);
                                  break;
                              case "gesturelogin.locked":
                                  respCommErrHandler.onGestureLoginLocked(jsonObject);
                                  break;
                              default:
                                  _err(mainActivity, ServerMsgHandlerUtil.getReturnMsg(jsonObject, Err.SESSION_DEF_ERR_MSG));
                          }
                      } else {
                          _err(mainActivity, ON_HANDLER_SERVER_RETURNCODE_IS_EMPTY);
                      }
                  } else {
                      _err(mainActivity, String.format(ON_HANDLER_SERVER_RESSTR_IS_EMPTY, "json"));
                  }
              } catch (Exception e) {
                  LoggerProxy.e(e, "处理服务端返回字符串出错");
                  _err(mainActivity, ON_HANDLER_SERVER_RESSTR_IS_VALID_JSONOBJ);
              }
          } else {
              _err(mainActivity, String.format(ON_HANDLER_SERVER_RESSTR_IS_EMPTY, "str"));
          }
      }
  
  }
  
  ```

