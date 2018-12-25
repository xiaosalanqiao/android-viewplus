package cn.jiiiiiin.vplus.core.util.log;

import com.blankj.utilcode.util.ToastUtils;

import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.exception.ViewPlusRuntimeException;

/**
 * 对于程序性错误区别mode（debug状态）,进行不同的提示
 * @author jiiiiiin
 * @version 1.0
 */
public class DebugUtil {

    public static void toastErr(String msg) {
        if (ViewPlus.IS_DEBUG()) {
            throw new ViewPlusRuntimeException(msg);
        } else {
            LoggerProxy.e(msg);
            ToastUtils.showLong(msg);
        }
    }
}
