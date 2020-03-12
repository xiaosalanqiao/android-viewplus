package cn.jiiiiiin.vplus.core.webview;

import android.webkit.ConsoleMessage;

/**
 * created by YLG on 2020/2/27
 *
 * 将网页中的ConsoleMessage返回出去
 */

public interface IWebViewConsoleMessage {
    /**
     * 将ConsoleMessage回调出去，当出现一些错误时可以进行相应的处理
     *
     * @param consoleMessage
     */
    void onConsoleMessage(ConsoleMessage consoleMessage);
}
