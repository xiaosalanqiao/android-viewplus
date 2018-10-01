package cn.jiiiiiin.vplus.core.webview.jsbridgehandler.protocol;

import android.net.Uri;
import android.text.TextUtils;

import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.webview.event.model.EventParams;

/**
 * const command = {
 * event: 'test',
 * action: 'TestCallBack',
 * params: {
 * number1,
 * number2
 * },
 * callback: callback1.name
 * }
 *
 * @author jiiiiiin
 * @version 1.0
 */

public class UriInfo {

    private String scheme;
    private String host;
    private String authority;
    private EventParams eventParams;

    public UriInfo(String url) {
        Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();
        String host = Uri.parse(url).getHost();
        String authority = uri.getAuthority();
        this.scheme = scheme;
        this.host = host;
        this.authority = authority;
        try {
            // TODO 硬编码
            String params = uri.getQueryParameter("command");
            if (!TextUtils.isEmpty(params)) {
                this.eventParams = EventParams.newInstance(params);
            }
        } catch (Exception e) {
            LoggerProxy.e(e, "解析 url字符串构建UriInfo出错 【%s】", url);
        }
    }

    public String getScheme() {
        return scheme;
    }

    public String getHost() {
        return host;
    }

    public String getAuthority() {
        return authority;
    }

    public EventParams getEventParams() {
        return eventParams;
    }

    @Override
    public String toString() {
        return "UriInfo{" +
                "scheme='" + scheme + '\'' +
                ", host='" + host + '\'' +
                ", authority='" + authority + '\'' +
                ", eventParams=" + eventParams +
                '}';
    }
}
