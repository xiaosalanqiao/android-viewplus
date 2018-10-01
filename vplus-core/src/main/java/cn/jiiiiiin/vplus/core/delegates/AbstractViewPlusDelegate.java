package cn.jiiiiiin.vplus.core.delegates;

import cn.jiiiiiin.vplus.core.exception.ViewPlusRuntimeException;

/**
 * @author jiiiiiin
 */

public abstract class AbstractViewPlusDelegate extends BaseDelegate {

    /**
     * 获取父类视图（容器）实例
     *
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractViewPlusDelegate> T getParentDelegate() {
        return (T) getParentFragment();
    }

    protected void popToRoot() {
        hideSoftInput();
        Class rootClazz = getRootClazz();
        if (rootClazz == null) {
            throw new ViewPlusRuntimeException("GETROOTCLAZZ IS NULL");
        }
        popTo(rootClazz, false);
    }

    protected abstract Class<? extends BaseDelegate> getRootClazz();

}
