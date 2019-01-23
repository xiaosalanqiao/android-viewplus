package cn.jiiiiiin.vplus.core.delegates.bottom;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import cn.jiiiiiin.vplus.core.R;
import cn.jiiiiiin.vplus.core.R2;
import cn.jiiiiiin.vplus.core.delegates.AbstractViewPlusDelegate;
import cn.jiiiiiin.vplus.core.exception.ViewPlusException;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * @author jiiiiiin
 */

@SuppressWarnings("AlibabaCommentsMustBeJavadocFormat")
public abstract class BaseBottomDelegate extends AbstractViewPlusDelegate implements View.OnClickListener {

    /**
     * 存储tabs实体
     */
    private final ArrayList<BottomTabBean> TAB_BEANS = new ArrayList<>();
    /**
     * 存储tabs视图
     */
    private final ArrayList<AbstractViewPlusDelegate> ITEM_DELEGATES = new ArrayList<>();
    protected final LinkedHashMap<BottomTabBean, AbstractViewPlusDelegate> ITEMS = new LinkedHashMap<>();
    /**
     * 初始化应用默认选中的tab idx
     */
    private int mIndexDelegate = 0;
    /**
     * 选中的tab高亮的颜色
     */
    private int mClickedColor = Color.RED;

    /**
     * 底部menu容器
     */
    @BindView(R2.id.bottom_bar)
    protected LinearLayoutCompat mBottomBar;

    /**
     * 底部menu容器
     */
    @BindView(R2.id.bottom_bar_line)
    protected View mBottomBarLine;

    /**
     * 设置tabs关联对象
     *
     * @param builder
     * @return
     */
    public abstract LinkedHashMap<BottomTabBean, AbstractViewPlusDelegate> setItems(ItemBuilder builder);

    @Override
    public Object setLayout() {
        return R.layout.delegate_bottom;
    }

    /**
     * 设置 初始化应用默认选中的tab idx
     *
     * @return
     */
    public abstract int setIndexDelegate();

    /**
     * 设置 选中的tab高亮的颜色
     *
     * @return
     */
    @ColorInt
    public abstract int setClickedColor();

    /**
     * 初始化tabs内容
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        if (setClickedColor() != Color.RED) {
            mClickedColor = setClickedColor();
        }
        final ItemBuilder builder = ItemBuilder.builder();
        // 需要子类手动设置items到builder中
        final LinkedHashMap<BottomTabBean, AbstractViewPlusDelegate> items = setItems(builder);
        mIndexDelegate = setIndexDelegate();
        if (mIndexDelegate > items.size()) {
            mIndexDelegate = 0;
        }
        // 存储实际使用的items
        ITEMS.putAll(items);
        for (Map.Entry<BottomTabBean, AbstractViewPlusDelegate> item : ITEMS.entrySet()) {
            final BottomTabBean key = item.getKey();
            final AbstractViewPlusDelegate value = item.getValue();
            TAB_BEANS.add(key);
            ITEM_DELEGATES.add(value);
        }
    }

    /**
     * 绑定ui
     */
    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {
        final int size = ITEMS.size();
        for (int i = 0; i < size; i++) {
            LayoutInflater.from(getContext()).inflate(R.layout.bottom_item_icon_text_layout, mBottomBar);
            final RelativeLayout item = (RelativeLayout) mBottomBar.getChildAt(i);
            // 设置每个item的点击事件
            item.setTag(i);
            item.setOnClickListener(this);
            final IconTextView itemIcon = (IconTextView) item.getChildAt(0);
            itemIcon.setTextSize(19);
            final AppCompatTextView itemTitle = (AppCompatTextView) item.getChildAt(1);
            final BottomTabBean bean = TAB_BEANS.get(i);
            // 初始化数据
            itemIcon.setText(bean.getIcon());
            itemTitle.setText(bean.getTitle());
            if (i == mIndexDelegate) {
                itemIcon.setTextColor(mClickedColor);
                itemTitle.setTextColor(mClickedColor);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final int size = ITEMS.size();
        final SupportFragment[] delegateArray = ITEM_DELEGATES.toArray(new SupportFragment[size]);
        Class<? extends AbstractViewPlusDelegate> firstItemClazz = getFirstItemClazz();
        if (findFragment(firstItemClazz) == null) {
            loadMultipleRootFragment(R.id.bottom_bar_delegate_container, mIndexDelegate, delegateArray);
        }
    }

    protected abstract Class<? extends AbstractViewPlusDelegate> getFirstItemClazz();

    private void resetColor() throws ViewPlusException {
        try {
            final int count = mBottomBar.getChildCount();
            // https://www.jianshu.com/p/00fa5b0c45a2
            int grayColor = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                grayColor = getResources().getColor(R.color.icon_no_selector, _mActivity.getTheme());
                // grayColor = ContextCompat.getColor(getContext(), R.color.icon_no_selector);
            } else {
                //noinspection AliDeprecation
                grayColor = getResources().getColor(R.color.icon_no_selector);
            }
            for (int i = 0; i < count; i++) {
                final RelativeLayout item = (RelativeLayout) mBottomBar.getChildAt(i);
                final IconTextView itemIcon = (IconTextView) item.getChildAt(0);
                itemIcon.setTextColor(grayColor);
                final AppCompatTextView itemTitle = (AppCompatTextView) item.getChildAt(1);
                itemTitle.setTextColor(grayColor);
            }
        } catch (Resources.NotFoundException e) {
            LoggerProxy.e(e, "resetColor 资源找不到错误");
        } catch (NullPointerException e) {
            LoggerProxy.e(e, "resetColor 空指针问题");
            throw new ViewPlusException("未能正常修改UI，请稍后尝试");
        }
    }

    /**
     * menu菜单点击事件
     */
    @Override
    public void onClick(View v) {
        final int tag = (int) v.getTag();
        try {
            resetColor();
        } catch (ViewPlusException e) {
            LoggerProxy.e(e, "点击菜单重置菜单颜色失败");
        }
        final RelativeLayout item = (RelativeLayout) v;
        final IconTextView itemIcon = (IconTextView) item.getChildAt(0);
        itemIcon.setTextColor(mClickedColor);
        final AppCompatTextView itemTitle = (AppCompatTextView) item.getChildAt(1);
        itemTitle.setTextColor(mClickedColor);
        showHideFragment(ITEM_DELEGATES.get(tag), ITEM_DELEGATES.get(mIndexDelegate));
        // 注意先后顺序
        mIndexDelegate = tag;
    }

    public void changeIndexDelegate(int idx) {
        onClick(mBottomBar.getChildAt(idx));
    }

    /**
     * start other BrotherFragment
     */
    public void startBrotherFragment(SupportFragment targetFragment) {
        start(targetFragment);
    }

    public void setBottomMenusVisible(int visible) {
        mBottomBar.setVisibility(visible);
    }
}
