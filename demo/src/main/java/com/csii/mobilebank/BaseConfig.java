package com.csii.mobilebank;

import cn.jiiiiiin.vplus.core.app.ConfigKeys;
import cn.jiiiiiin.vplus.core.app.Configurator;
import cn.jiiiiiin.vplus.core.app.ViewPlus;

/**
 * @author jiiiiiin
 * @version 1.0
 */

public class BaseConfig {

    // ========= 常用配置
    /**
     * 应用运行模式
     * DEV_MODE & TEST_MODE & PROD_MODE
     */
    public static final String MODE = Configurator.PROD_MODE;

    /**
     * 是否是debug模式，比如这里为true就标识调试上面任何默认
     */
    public static final boolean IS_DEBUG = true;

    /**
     * 应用h5自身的应用地址
     */
    public static final String TEST_WEB_HOST = "http://emobile.jiiiiiin.cn/";
    public static final String PROD_WEB_HOST = "http://emobile.jiiiiiin.cn/";

    public static final String SERVER_BASE_URL = "pweb/";

    // ========= 服务器相关配置
    public static final String SERVER_STATUS_CODE_KEY = "ReturnCode";
    public static final String SERVER_STATUS_CODE = "000000";
    public static final String SERVER_STATUS_MSG_KEY = "ReturnMessage";

    // ========= 第三方链接相关配置

    public static String getUrl(String path) {
        return ((String) ViewPlus.getConfiguration(ConfigKeys.WEB_HOST)).concat(path);
    }

}
