package cn.jiiiiiin.vplus.core.delegates.bottom;

import androidx.annotation.IntegerRes;

/**
 * 通用tab的实体类
 *
 * @author jiiiiiin
 */

public final class BottomTabBean {

  private final CharSequence ICON;
  private final CharSequence TITLE;
  private int iconid;
  private int iconidUnselected;
  private String iconUrl;
  private String iconUnselectedUrl;
  private int iconDefaultId;
  private int iconUnselectedDefaultId;

  /**
   * BaseBottomDelegate中需要的bean
   * @param icon 图标名
   * @param title 标题
   */
  public BottomTabBean(CharSequence icon, CharSequence title) {
    this.ICON = icon;
    this.TITLE = title;
  }

  /**
   * BaseBottomDelegateForTwoIcon中需要的bean
   * @param icon 选中时的icon资源id
   * @param iconUnselected 未选中的icon资源id
   * @param title 标题
   */
  public BottomTabBean(@IntegerRes int icon, @IntegerRes int iconUnselected, CharSequence title) {
    this.ICON = null;
    this.iconid = icon;
    this.iconidUnselected = iconUnselected;
    this.TITLE = title;
  }

  /**
   * BaseBottomDelegateForTwoIconUrl中需要的bean
   * @param icon 选中时的icon URL
   * @param iconUnselected 未选中时的icon URL
   * @param defaultResId 选中时默认的icon资源id
   * @param defaultUnselectedResId 未选中时默认的资源id
   * @param title 标题
   */
  public BottomTabBean(String icon, String iconUnselected, @IntegerRes int defaultResId, @IntegerRes int defaultUnselectedResId, CharSequence title){
    this.ICON = null;
    this.iconUrl = icon;
    this.iconUnselectedUrl = iconUnselected;
    this.iconDefaultId = defaultResId;
    this.iconUnselectedDefaultId = defaultUnselectedResId;
    this.TITLE = title;
  }

  public CharSequence getIcon() {
    return ICON;
  }

  public CharSequence getTitle() {
    return TITLE;
  }

  public int getIconid() {
    return iconid;
  }

  public int getIconidUnselected() {
    return iconidUnselected;
  }

  public String getIconUrl(){
    return iconUrl;
  }

  public String getIconUnselectedUrl(){
    return iconUnselectedUrl;
  }

  public int getIconDefaultId(){
    return iconDefaultId;
  }

  public int getIconUnselectedDefaultId(){
    return iconUnselectedDefaultId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    BottomTabBean that = (BottomTabBean) o;

    return (ICON != null ? ICON.equals(that.ICON) : that.ICON == null) && (TITLE != null ? TITLE
        .equals(that.TITLE) : that.TITLE == null);
  }

  @Override
  public int hashCode() {
    int result = ICON != null ? ICON.hashCode() : 0;
    result = 31 * result + (TITLE != null ? TITLE.hashCode() : 0);
    return result;
  }
}
