package cn.jiiiiiin.vplus.core.webview.event;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * @author jiiiiiin
 * @version 1.0
 */

public interface IEventManager {

    IEventManager addEvent(@NonNull String name, @NonNull AbstractEvent event);

    <T extends AbstractEvent> T getEvent(@NonNull String name);

    <T extends AbstractEvent> T getEvent(Class<T> tClass);

    AbstractEvent createEvent(@NonNull String eventName);

    IEventManager addEvent(AbstractEvent event);

    void onWebDelegateDestroy();

    void onWebDelegatePause();

    void onWebViewTouchedListener(View webView);

}
