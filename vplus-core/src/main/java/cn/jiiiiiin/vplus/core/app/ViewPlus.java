package cn.jiiiiiin.vplus.core.app;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

/**
 * @author Created by jiiiiiin on 2017/8/4.
 */

public final class ViewPlus {

    public static Configurator init(Context context) {
        Configurator.getInstance()
                .getVPConfigs()
                .put(ConfigKeys.APPLICATION_CONTEXT, context.getApplicationContext());
        return Configurator.getInstance();
    }

    public static Configurator getConfigurator() {
        return Configurator.getInstance();
    }

    public static <T> T getConfiguration(Object key) {
        return getConfigurator().getConfiguration(key);
    }

    public static Context getApplicationContext() {
        return getConfiguration(ConfigKeys.APPLICATION_CONTEXT);
    }

    public static Handler getHandler() {
        return getConfiguration(ConfigKeys.HANDLER);
    }

    public static boolean IS_DEBUG(){
        return getConfiguration(ConfigKeys.DEBUG);
    }

    public static boolean IS_PROD(){
        return getConfiguration(ConfigKeys.MODE).equals(Configurator.PROD_MODE);
    }

    public static boolean IS_TEST() {
        return getConfiguration(ConfigKeys.MODE).equals(Configurator.TEST_MODE);
    }

    public static Activity getRootActivity() {
        return getConfiguration(ConfigKeys.ACTIVITY);
    }
}
