package cn.jiiiiiin.vplus.ui.widget;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;

import ch.ielse.view.SwitchView;
import cn.jiiiiiin.vplus.core.util.ui.ViewUtil;

/**
 * @author jiiiiiin
 * @version 1.0
 */
public final class UICreator {

    private static final ViewGroup.LayoutParams DEF_LAYOUTPARAMS_SWITCHVIEW = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 80);

    public static SwitchView getSwitchViewInstance(FragmentActivity activity, boolean isOpened) {
        final SwitchView switchView = new SwitchView(activity);
        switchView.setOpened(isOpened);
        switchView.setLayoutParams(DEF_LAYOUTPARAMS_SWITCHVIEW);
        return switchView;
    }

    // icons size
    public static final int QMUI_COMMON_LIST_ITEM_VIEW_ICON_SIZE = 55;
    public static final int QMUI_COMMON_LIST_ITEM_VIEW_ICON_MARGIN_END = 15;
    public static final RelativeLayout.LayoutParams ICONS_LAYOUT_PARAMS = new RelativeLayout.LayoutParams(QMUI_COMMON_LIST_ITEM_VIEW_ICON_SIZE, QMUI_COMMON_LIST_ITEM_VIEW_ICON_SIZE);

    static {
        ICONS_LAYOUT_PARAMS.addRule(RelativeLayout.CENTER_VERTICAL);
        ICONS_LAYOUT_PARAMS.setMarginEnd(QMUI_COMMON_LIST_ITEM_VIEW_ICON_MARGIN_END);
    }

    public static ImageView getQMUICommListItemViewImageViewIcons(Activity activity, String url) {
        final ImageView imageView = new ImageView(activity);
        return setQMUICommListItemViewImageViewIconsStyleAndImg(activity, imageView, url);
    }

    public static ImageView setQMUICommListItemViewImageViewIconsStyleAndImg(Activity activity, ImageView imageView, String url) {
        imageView.setLayoutParams(UICreator.ICONS_LAYOUT_PARAMS);
        if(!StringUtils.isTrimEmpty(url)) {
            Glide.with(activity).
                    load(url).
                    apply(ViewUtil.RECYCLER_OPTIONS4GLIDE_ICON).
                    into(imageView);
        }
        return imageView;
    }

}
