package cn.jiiiiiin.vplus.ui.recycler;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

/**
 * Created by jiiiiiin
 * 实体/BaseMultiItemQuickAdapter所需数据结构
 */

public class MultipleItemEntity implements MultiItemEntity {

    /**
     * ReferenceQueue：防止渲染数据过多导致内存问题
     */
    private final ReferenceQueue<LinkedHashMap<Object, Object>> ITEM_QUEUE = new ReferenceQueue<>();

    /**
     * 缓存Recycler View中所有Item的渲染数据
     */
    private final LinkedHashMap<Object, Object> MULTIPLE_FIELDS = new LinkedHashMap<>();

    private final SoftReference<LinkedHashMap<Object, Object>> FIELDS_REFERENCE = new SoftReference<>(MULTIPLE_FIELDS, ITEM_QUEUE);

    MultipleItemEntity(LinkedHashMap<Object, Object> fields) {
        FIELDS_REFERENCE.get().putAll(fields);
    }

    public static MultipleEntityBuilder builder() {
        return new MultipleEntityBuilder();
    }

    /**
     *
     * @return 每一个item的样式
     */
    @Override
    public int getItemType() {
        // FIELDS_REFERENCE.get()-> MULTIPLE_FIELDS
        return (int) FIELDS_REFERENCE.get().get(MultipleFields.ITEM_TYPE);
    }

    /**
     * 获取item的具体（MultipleFields）数据
     *
     * @param key
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public final <T> T getField(Object key) {
        return (T) FIELDS_REFERENCE.get().get(key);
    }

    /**
     *
     * @return 整个LinkedHashMap
     */
    public final LinkedHashMap<?, ?> getFields() {
        return FIELDS_REFERENCE.get();
    }

    /**
     * 设置Recycler View Item的类型和数据（渲染数据、标识数据）
     *
     * @param key
     * @param value
     * @return
     */
    public final MultipleItemEntity setField(Object key, Object value) {
        FIELDS_REFERENCE.get().put(key, value);
        return this;
    }
}
