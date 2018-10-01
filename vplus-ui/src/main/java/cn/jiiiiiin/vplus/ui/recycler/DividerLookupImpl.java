package cn.jiiiiiin.vplus.ui.recycler;

import com.choices.divider.Divider;
import com.choices.divider.DividerItemDecoration;

/**
 * Created by jiiiiiin
 * setDividerLookup(new DividerLookupImpl(color, size)); 所需LookupImpl
 * 处理分割线横向和竖线的属性
 */

public class DividerLookupImpl implements DividerItemDecoration.DividerLookup {

    // 颜色
    private final int COLOR;
    // 大小
    private final int SIZE;

    public DividerLookupImpl(int color, int size) {
        this.COLOR = color;
        this.SIZE = size;
    }

    @Override
    public Divider getVerticalDivider(int position) {
        return new Divider.Builder()
                .size(SIZE)
                .color(COLOR)
                .build();
    }

    @Override
    public Divider getHorizontalDivider(int position) {
        return new Divider.Builder()
                .size(SIZE)
                .color(COLOR)
                .build();
    }
}
