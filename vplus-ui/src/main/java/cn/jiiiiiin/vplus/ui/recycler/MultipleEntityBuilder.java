package cn.jiiiiiin.vplus.ui.recycler;

import java.util.LinkedHashMap;

/**
 * Created by jiiiiiin
 * MultipleItemEntity的建造者（builder构建模式）
 */

public class MultipleEntityBuilder {

    // 临时缓存渲染数据(MultipleFields:int样式）
    private static final LinkedHashMap<Object, Object> FIELDS = new LinkedHashMap<>();

    public MultipleEntityBuilder() {
        // 先清除之前的数据（避免出现重复添加）
        FIELDS.clear();
    }

    public final MultipleEntityBuilder setItemType(int itemType) {
        FIELDS.put(MultipleFields.ITEM_TYPE, itemType);
        return this;
    }

    /**
     * 设置MultipleItemEntity的数据项
     * @param key
     * @param value
     * @return
     */
    public final MultipleEntityBuilder setField(Object key, Object value) {
        FIELDS.put(key, value);
        return this;
    }

    public final MultipleEntityBuilder setFields(LinkedHashMap<?, ?> map) {
        FIELDS.putAll(map);
        return this;
    }

    public final MultipleItemEntity build() {
        return new MultipleItemEntity(FIELDS);
    }
}
