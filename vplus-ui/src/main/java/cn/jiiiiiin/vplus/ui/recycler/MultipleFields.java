package cn.jiiiiiin.vplus.ui.recycler;

/**
 * Created by jiiiiiin
 * Recycler View 数据类型
 * MultipleItem的类型
 *
 * 而不同的业务可以构建自己的item type，如OrderItemFields
 */

public enum MultipleFields {
    // 标识每一个item的样式
    ITEM_TYPE,
    TITLE,
    TEXT,
    IMAGE_URL,
    BANNERS,
    // 功能
    FUNC,
    // 控制返回数据item占屏比（宽度）
    SPAN_SIZE,
    ID,
    NAME,
    // 可选（扩展字段）
    TAG
}
