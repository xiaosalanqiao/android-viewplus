package com.csii.mobilebank;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.blankj.utilcode.util.DeviceUtils;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.io.File;

import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.ui.refresh.DynamicTimeFormat;

import static com.csii.mobilebank.BaseConfig.IS_DEBUG;
import static com.csii.mobilebank.BaseConfig.MODE;


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
        if (IS_DEBUG) {
            Hawk.init(this).setLogInterceptor(message -> Log.d("HAWK", message)).build();
        } else {
            Hawk.init(this).build();
        }

        ViewPlus.init(this)
                .withThemeColor(getResources().getColor(R.color.colorPrimaryLight))
                .withIsDeviceRooted(DeviceUtils.isDeviceRooted())
                .withMode(BaseConfig.MODE)
                .withDebug(BaseConfig.IS_DEBUG)
                .withWebUserAgent("Custom-WebUserAgent")
                .withServerStatusCodeKey(BaseConfig.SERVER_STATUS_CODE_KEY)
                .withServerStatusCodeSuccessFlag(BaseConfig.SERVER_STATUS_CODE)
                .withServerStatusMsgKey(BaseConfig.SERVER_STATUS_MSG_KEY)
                .configure();

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
