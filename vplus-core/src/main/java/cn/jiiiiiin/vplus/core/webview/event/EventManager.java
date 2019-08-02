package cn.jiiiiiin.vplus.core.webview.event;

import androidx.annotation.NonNull;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * 映射前端和native的通讯消息实体和对应的key存储管理对象
 *
 * @author Created by jiiiiiin
 */
public class EventManager implements IEventManager {

    private static final HashMap<String, AbstractEvent> EVENTS = new HashMap<>();

    private EventManager() {
    }

    private static class Holder {
        private static final EventManager INSTANCE = new EventManager();
    }

    public static EventManager getInstance() {
        return Holder.INSTANCE;
    }

    @Deprecated
    @Override
    public EventManager addEvent(@NonNull String name, @NonNull AbstractEvent event) {
        EVENTS.put(name, event);
        return this;
    }

    @Override
    public <T extends AbstractEvent> T getEvent(@NonNull String name) {
        return (T) EVENTS.get(name);
    }

    @Override
    public <T extends AbstractEvent> T getEvent(Class<T> tClass) {
        return  (T) EVENTS.get(tClass.getSimpleName());
    }

    @Override
    public EventManager addEvent(AbstractEvent event) {
        EVENTS.put(event.getClass().getSimpleName(), event);
        return this;
    }

    @Override
    public AbstractEvent createEvent(@NonNull String action) {
        return EVENTS.get(action);
    }

    @Override
    public void onWebDelegateDestroy() {
        if (!EVENTS.isEmpty()) {
            for (Map.Entry<String, AbstractEvent> entry : EVENTS.entrySet()) {
                entry.getValue().onWebDelegateDestroy();
            }
        }
    }

    @Override
    public void onWebDelegatePause() {
        if (!EVENTS.isEmpty()) {
            for (Map.Entry<String, AbstractEvent> entry : EVENTS.entrySet()) {
                entry.getValue().onWebDelegatePause();
            }
        }
    }

    @Override
    public void onWebViewTouchedListener(View webView) {
        if (!EVENTS.isEmpty()) {
            for (Map.Entry<String, AbstractEvent> entry : EVENTS.entrySet()) {
                entry.getValue().onWebViewTouchedListener(webView);
            }
        }
    }

}
