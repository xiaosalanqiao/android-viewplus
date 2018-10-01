package cn.jiiiiiin.vplus.ui.recycler;

import android.support.annotation.ColorInt;

import com.choices.divider.DividerItemDecoration;

/**
 * Created by jiiiiiin
 * ChoicesWang/RecyclerView_Divider 分割线的基类
 */

public class BaseDecoration extends DividerItemDecoration {

    /**
     * @param color 颜色
     * @param size 粗细
     */
    private BaseDecoration(@ColorInt int color, int size) {
        setDividerLookup(new DividerLookupImpl(color, size));
    }

    public static BaseDecoration create(@ColorInt int color, int size) {
        return new BaseDecoration(color, size);
    }
}
