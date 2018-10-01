package cn.jiiiiiin.vplus.core.delegates.bottom;

/**
 * 通用tab的实体类
 *
 * @author jiiiiiin
 */

public final class BottomTabBean {

    private final CharSequence ICON;
    private final CharSequence TITLE;

    public BottomTabBean(CharSequence icon, CharSequence title) {
        this.ICON = icon;
        this.TITLE = title;
    }

    public CharSequence getIcon() {
        return ICON;
    }

    public CharSequence getTitle() {
        return TITLE;
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

        return (ICON != null ? ICON.equals(that.ICON) : that.ICON == null) && (TITLE != null ? TITLE.equals(that.TITLE) : that.TITLE == null);
    }

    @Override
    public int hashCode() {
        int result = ICON != null ? ICON.hashCode() : 0;
        result = 31 * result + (TITLE != null ? TITLE.hashCode() : 0);
        return result;
    }
}
