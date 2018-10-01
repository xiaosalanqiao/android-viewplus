package cn.jiiiiiin.vplus.core.util.ui;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import cn.jiiiiiin.vplus.core.app.ConfigKeys;
import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;

/**
 * https://www.diycode.cc/topics/383
 * https://stackoverflow.com/questions/7417123/android-how-to-adjust-layout-in-full-screen-mode-when-softkeyboard-is-visible/19494006#19494006
 * https://juejin.im/post/5a25f6146fb9a0452405ad5b
 * 总结起来，就是这样：
 * <p>
 * 普通Activity（不带WebView），直接使用adjustpan或者adjustResize
 * 如果带WebView：
 * a) 如果非全屏模式，可以使用adjustResize
 * b) 如果是全屏模式，则使用AndroidBug5497Workaround进行处理。
 * OK，以上就是一段关于『软键盘挡住输入框』的爬坑之旅。
 *
 * @author jiiiiiin
 * @version 1.0
 */
public class AndroidBug5497Workaround {


    private static final int MOSU = 60;

    public static AndroidBug5497Workaround assistActivity(View content) {
        return new AndroidBug5497Workaround(content);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private ViewGroup.LayoutParams frameLayoutParams;
    private int realUsableHeight = 0;

    public interface AndroidBug5497WorkaroundChange {
        void resize();
    }

    private AndroidBug5497Workaround(View content) {
        if (content != null) {
            mChildOfContent = content;
            mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(this::possiblyResizeChildOfContent);
            frameLayoutParams = mChildOfContent.getLayoutParams();
        }
    }

    public void resize() {
        int temp = realUsableHeight + MOSU;
        frameLayoutParams.height = temp;
        mChildOfContent.requestLayout();
        usableHeightPrevious = temp;
    }

    private void possiblyResizeChildOfContent() {
        final boolean flagStartThirdWebViewDelegate = ViewPlus.getConfiguration(ConfigKeys.START_THIRD_WEBVIEW_DELEGATE);
        int usableHeightNow = computeUsableHeight();
        if (usableHeightPrevious == 0 && usableHeightNow > 0) {
            realUsableHeight = usableHeightNow;
        }
        if (!flagStartThirdWebViewDelegate) {
            if (usableHeightNow != usableHeightPrevious) {
                //如果两次高度不一致
                //将计算的可视高度设置成视图的高度
                frameLayoutParams.height = usableHeightNow;
                mChildOfContent.requestLayout();//请求重新布局
                usableHeightPrevious = usableHeightNow;
            }
        } else {
            final int temp = usableHeightNow - MOSU;
            if (temp != usableHeightPrevious) {
                frameLayoutParams.height = temp;
                mChildOfContent.requestLayout();
                usableHeightPrevious = usableHeightNow;
            }
        }
    }

    private int computeUsableHeight() {
        //计算视图可视高度
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return r.bottom;
    }

}
