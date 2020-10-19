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
import cn.jiiiiiin.vplus.core.util.LoadImgUtils;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;

/**
 * created by YLG on 2020/6/15
 *
 * 通过URL加载bottombar选中和未选中的图标
 */

public abstract class BaseBottomDelegateForTwoIconUrl extends BaseBottomDelegate {

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
      // 初始化数据，URL为空时加载默认资源id
      if (!StringUtils.isTrimEmpty(bean.getIconUnselectedUrl())) {
        Glide.with(getContext())
            .load(bean.getIconUnselectedUrl())
            .apply(LoadImgUtils.options4Glide(bean.getIconUnselectedDefaultId()))
            .into(itemIcon);
      } else {
        itemIcon
            .setImageDrawable(ContextCompat.getDrawable(getContext(), bean.getIconUnselectedDefaultId()));
      }
      itemTitle.setText(bean.getTitle());
      itemTitle.setTextSize(mTabTitleSize);
      if (i == mIndexDelegate) {
        if (!StringUtils.isTrimEmpty(bean.getIconUrl())) {
          Glide.with(getContext())
              .load(bean.getIconUrl())
              .apply(LoadImgUtils.options4Glide(bean.getIconDefaultId()))
              .into(itemIcon);
        } else {
          itemIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), bean.getIconDefaultId()));
        }
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
        if (!StringUtils.isTrimEmpty(bean.getIconUnselectedUrl())) {
          Glide.with(getContext())
              .load(bean.getIconUnselectedUrl())
              .apply(LoadImgUtils.options4Glide(bean.getIconUnselectedDefaultId()))
              .into(itemIcon);
        } else {
          itemIcon
              .setImageDrawable(ContextCompat.getDrawable(getContext(), bean.getIconUnselectedDefaultId()));
        }
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
    if (!StringUtils.isTrimEmpty(bean.getIconUrl())) {
      Glide.with(getContext())
          .load(bean.getIconUrl())
          .apply(LoadImgUtils.options4Glide(bean.getIconDefaultId()))
          .into(itemIcon);
    } else {
      itemIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), bean.getIconDefaultId()));
    }
    final AppCompatTextView itemTitle = (AppCompatTextView) item.getChildAt(1);
    itemTitle.setTextColor(mClickedColor);
    showHideFragment(ITEM_DELEGATES.get(tag), ITEM_DELEGATES.get(mIndexDelegate));
    // 注意先后顺序
    mIndexDelegate = tag;
  }
}
