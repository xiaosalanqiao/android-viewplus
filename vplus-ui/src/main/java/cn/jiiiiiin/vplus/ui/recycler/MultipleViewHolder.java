package cn.jiiiiiin.vplus.ui.recycler;

import android.view.View;

import com.chad.library.adapter.base.BaseViewHolder;

/**
 * Created by jiiiiiin
 * BaseMultiItemQuickAdapter的view holder
 */

public class MultipleViewHolder extends BaseViewHolder {

    private MultipleViewHolder(View view) {
        super(view);
    }

    // 简单工厂模式
    public static MultipleViewHolder create(View view) {
        return new MultipleViewHolder(view);
    }
}
