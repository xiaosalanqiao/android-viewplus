package cn.jiiiiiin.vplus.core.delegates.bottom;

import java.util.LinkedHashMap;

import cn.jiiiiiin.vplus.core.delegates.AbstractViewPlusDelegate;

/**
 * tab实体和对应的视图的构建对象
 *
 * @author jiiiiiin
 */

public final class ItemBuilder {

    private final LinkedHashMap<BottomTabBean, AbstractViewPlusDelegate> ITEMS = new LinkedHashMap<>();

    static ItemBuilder builder() {
        return new ItemBuilder();
    }

    public final ItemBuilder addItem(BottomTabBean bean, AbstractViewPlusDelegate delegate) {
        ITEMS.put(bean, delegate);
        return this;
    }

    public final ItemBuilder addItems(LinkedHashMap<BottomTabBean, AbstractViewPlusDelegate> items) {
        ITEMS.putAll(items);
        return this;
    }

    public final LinkedHashMap<BottomTabBean, AbstractViewPlusDelegate> build() {
        return ITEMS;
    }
}
