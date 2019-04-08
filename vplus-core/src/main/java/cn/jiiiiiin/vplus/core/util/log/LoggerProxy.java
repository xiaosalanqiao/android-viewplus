package cn.jiiiiiin.vplus.core.util.log;

import android.util.Log;

import com.orhanobut.logger.Logger;

/**
 * @author jiiiiiin
 */

public final class LoggerProxy {

    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;
    public static final String TAG = "===Log===";

    private static LoggerProxyLifecycleListener loggerProxyLifecycleListener = new LoggerProxyLifecycleListener() {
        @Override
        public void e(String message) {

        }

        @Override
        public void e(String message, Object... args) {

        }

        @Override
        public void e(Throwable exception, String message, Object... args) {

        }
    };

    /**
     * 控制log等级
     */
    private static int LEVEL = VERBOSE;

    public interface LoggerProxyLifecycleListener {
        void e(String message);
        void e(String message, Object... args);
        void e(Throwable exception, String message, Object... args);
    }

    public static void setLEVEL(int level) {
        LoggerProxy.LEVEL = level;
    }

    public static void setLoggerProxyLifecycleListener(LoggerProxyLifecycleListener loggerProxyLifecycleListener) {
        LoggerProxy.loggerProxyLifecycleListener = loggerProxyLifecycleListener;
    }

    public static void v(String tag, String message) {
        if (LEVEL <= VERBOSE) {
            try {
                Logger.v(message);
            } catch (Exception e) {
                Log.e(TAG, "打印日志出错", e);
            }
        }
    }

    public static void d(Object message) {
        if (LEVEL <= DEBUG) {
            try {
                Logger.d(message);
            } catch (Exception e) {
                Log.e(TAG, "打印日志出错", e);
            }
        }
    }


    public static void d(String message, Object... args) {
        if (LEVEL <= DEBUG) {
            try {
                Logger.d(message, args);
            } catch (Exception e) {
                Log.e(TAG, "打印日志出错", e);
            }
        }
    }

    public static void i(String message) {
        if (LEVEL <= INFO) {
            try {
                Logger.i(message);
            } catch (Exception e) {
                Log.e(TAG, "打印日志出错", e);
            }
        }
    }


    public static void i(String message, Object... args) {
        if (LEVEL <= INFO) {
            try {
                Logger.i(message, args);
            } catch (Exception e) {
                Log.e(TAG, "打印日志出错", e);
            }
        }
    }

    public static void w(String message) {
        if (LEVEL <= WARN) {
            try {
                Logger.w(message);
            } catch (Exception e) {
                Log.e(TAG, "打印日志出错", e);
            }
        }
    }

    public static void w(String message, Object... args) {
        if (LEVEL <= WARN) {
            try {
                Logger.w(message, args);
            } catch (Exception e) {
                Log.e(TAG, "打印日志出错", e);
            }
        }
    }

    public static void json(String message) {
        if (LEVEL <= WARN) {
            try {
                Logger.json(message);
            } catch (Exception e) {
                Log.e(TAG, "打印日志出错", e);
            }
        }
    }

    public static void e(String message) {
        if (LEVEL <= ERROR) {
            try {
                loggerProxyLifecycleListener.e(message);
                Logger.e(message);
            } catch (Exception e) {
                Log.e(TAG, "打印日志出错", e);
            }
        }
    }

    public static void e(String message, Object... args) {
        if (LEVEL <= ERROR) {
            try {
                loggerProxyLifecycleListener.e(message, args);
                Logger.e(message, args);
            } catch (Exception e) {
                Log.e(TAG, "打印日志出错", e);
            }
        }
    }

    public static void e(Throwable exception, String message) {
        if (LEVEL <= ERROR) {
            try {
                e(exception, message, new Object[]{});
            } catch (Exception e) {
                Log.e(TAG, "打印日志出错", e);
            }
        }
    }

    public static void e(Throwable exception, String message, Object... args) {
        if (LEVEL <= ERROR) {
            try {
                loggerProxyLifecycleListener.e(exception, message, args);
                Logger.e(exception, message, args);
            } catch (Exception e) {
                Log.e(TAG, "打印日志出错", e);
            }
        }
    }

}
