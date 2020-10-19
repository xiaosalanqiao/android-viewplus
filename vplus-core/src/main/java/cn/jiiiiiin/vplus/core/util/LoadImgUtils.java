package cn.jiiiiiin.vplus.core.util;

import android.annotation.SuppressLint;
import androidx.annotation.IntegerRes;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class LoadImgUtils {

  /**
   * 加载icon失败时使用默认资源
   * @param defaultResId 默认资源id
   * @return
   */
  @SuppressLint("ResourceType")
  public static RequestOptions options4Glide(@IntegerRes int defaultResId){
    return new RequestOptions()
        .placeholder(defaultResId)
        .error(defaultResId)
        // 剪裁方式
        .fitCenter()
        // 缓存方式
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        // 动画方式
        .dontAnimate();
  }

}
