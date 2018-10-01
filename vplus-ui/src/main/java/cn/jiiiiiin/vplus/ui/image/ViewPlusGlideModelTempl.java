package cn.jiiiiiin.vplus.ui.image;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

/**
 * Glide模板，用于生成GlideApp
 * 配合：
 * private static final RequestOptions BANNER_OPTIONS = new RequestOptions()
 * .diskCacheStrategy(DiskCacheStrategy.ALL)
 * .dontAnimate()
 * .centerCrop();
 * 参考：ImageHolder
 * Glide.with(context)
 * .load(data)
 * .apply(BANNER_OPTIONS)
 * .into(mImageView);
 *
 * @author jiiiiiin
 */
@GlideModule
public class ViewPlusGlideModelTempl extends AppGlideModule {
}
