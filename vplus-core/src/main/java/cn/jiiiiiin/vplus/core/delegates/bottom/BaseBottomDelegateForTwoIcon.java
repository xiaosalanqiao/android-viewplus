package cn.jiiiiiin.vplus.core.delegates.bottom;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import cn.jiiiiiin.vplus.core.R;
import cn.jiiiiiin.vplus.core.exception.ViewPlusException;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;

/**
 * created by YLG on 2020/6/15
 *
 * 通过资源id加载bottombar选中和未选中的图标
 */

public abstract class BaseBottomDelegateForTwoIcon extends BaseBottomDelegate {

  @Override
  public void onBindView(@Nullable Bundle savedInstanceState,
      @NonNull View rootView) {
    final int size = ITEMS.size();
    for (int i = 0; i < size; i++) {
      LayoutInflater.from(getContext()).inflate(R.layout.bottom_item_icon_img_layout, mBottomBar);
      final RelativeLayout item = (RelativeLayout) mBottomBar.getChildAt(i);
      // 设置每个item的点击事件
      item.setTag(i);
      item.setOnClickListener(this);
      final ImageView itemIcon = (ImageView) item.getChildAt(0);
      final AppCompatTextView itemTitle = (AppCompatTextView) item.getChildAt(1);
      final BottomTabBean bean = TAB_BEANS.get(i);
      // 初始化数据
      itemIcon
          .setImageDrawable(ContextCompat.getDrawable(getContext(), bean.getIconidUnselected()));
      itemTitle.setText(bean.getTitle());
      itemTitle.setTextSize(mTabTitleSize);
      if (i == mIndexDelegate) {
        itemIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), bean.getIconid()));
        itemTitle.setTextColor(mClickedColor);
      }
    }
  }

  @Override
  protected void resetColor() throws ViewPlusException {
    try {
      final int count = mBottomBar.getChildCount();
      for (int i = 0; i < count; i++) {
        final BottomTabBean bean = TAB_BEANS.get(i);
        final RelativeLayout item = (RelativeLayout) mBottomBar.getChildAt(i);
        final ImageView itemIcon = (ImageView) item.getChildAt(0);
        itemIcon
            .setImageDrawable(getContext().getResources().getDrawable(bean.getIconidUnselected()));
        final AppCompatTextView itemTitle = (AppCompatTextView) item.getChildAt(1);
        itemTitle.setTextColor(Color.parseColor("#333333"));
      }
    } catch (Resources.NotFoundException e) {
      LoggerProxy.e(e, "resetColor 资源找不到错误");
    } catch (NullPointerException e) {
      LoggerProxy.e(e, "resetColor 空指针问题");
      throw new ViewPlusException("未能正常修改UI，请稍后尝试");
    }
  }

  @Override
  public void onClick(View v) {
    final int tag = (int) v.getTag();
    try {
      resetColor();
    } catch (ViewPlusException e) {
      LoggerProxy.e(e, "点击菜单重置菜单颜色失败");
    }
    final BottomTabBean bean = TAB_BEANS.get(tag);
    final RelativeLayout item = (RelativeLayout) v;
    final ImageView itemIcon = (ImageView) item.getChildAt(0);
    itemIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), bean.getIconid()));
    final AppCompatTextView itemTitle = (AppCompatTextView) item.getChildAt(1);
    itemTitle.setTextColor(mClickedColor);
    showHideFragment(ITEM_DELEGATES.get(tag), ITEM_DELEGATES.get(mIndexDelegate));
    // 注意先后顺序
    mIndexDelegate = tag;
  }
}
