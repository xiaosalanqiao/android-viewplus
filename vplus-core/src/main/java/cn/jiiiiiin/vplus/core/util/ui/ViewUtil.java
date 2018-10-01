package cn.jiiiiiin.vplus.core.util.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.PictureSelectionModel;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.io.ByteArrayOutputStream;

import cn.jiiiiiin.vplus.core.R;
import cn.jiiiiiin.vplus.core.app.ConfigKeys;
import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;

/**
 * @author jiiiiiin
 * @version 1.0
 */

public class ViewUtil {

    /**
     * 设置图片加载策略
     * https://muyangmin.github.io/glide-docs-cn/doc/options.html
     */
    public static final RequestOptions RECYCLER_OPTIONS4GLIDE_ICON =
            new RequestOptions()
                    .placeholder(R.drawable.icon_placeholder)
                    .error(R.drawable.err_not_load_img)
                    // 剪裁方式
                    .fitCenter()
                    // 缓存方式
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    // 动画方式
                    .dontAnimate();

    public static boolean activityIsLiving(Activity activity) {
        try {
            return activity != null && !activity.isFinishing() && !activity.isDestroyed();
        } catch (NoSuchMethodError e) {
            // 4.1没有isDestroyed方法
            return activity != null && !activity.isFinishing();
        }
    }

    public interface IActivityIsLivingCanByRunCallBack {
        void doIt(@NonNull Activity activity);

        void onActivityIsNotLiving();
    }

    public abstract static class AbstractActivityIsLivingCanByRunCallBack implements IActivityIsLivingCanByRunCallBack {
        @Override
        public void onActivityIsNotLiving() {

        }
    }

    /**
     * https://www.jianshu.com/p/e46b843b95f4
     *
     * @param activity
     * @param activityIsLivingCanByRunCallBack
     */
    public static void activityIsLivingCanByRun(Activity activity, @NonNull IActivityIsLivingCanByRunCallBack activityIsLivingCanByRunCallBack) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            LoggerProxy.w("activityIsLivingCanByRun activity is null can't call activityIsLivingCanByRunCallBack.doIt");
            activityIsLivingCanByRunCallBack.onActivityIsNotLiving();
        } else {
            activityIsLivingCanByRunCallBack.doIt(activity);
        }
    }


    public static void setVisibility(View view, Boolean visible) {
        if (view != null) {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public static void setVisibility2(View view, Boolean visible) {
        if (view != null) {
            view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public static void copy2Clipboard(Activity activity, String msg) {
        try {
            ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            assert clipboardManager != null;
            clipboardManager.setPrimaryClip(ClipData.newPlainText(msg, msg));
            ToastUtils.showShort("已复制");
        } catch (Exception e) {
            LoggerProxy.e(e, "复制到剪切板失败");
            ToastUtils.showLong("复制消息到剪切板失败");
        }
    }

    public static SmartRefreshLayout initMaterialSmartRefreshLayout(@NonNull SmartRefreshLayout smartRefreshLayout) {
        // https://github.com/scwang90/SmartRefreshLayout/blob/master/art/md_faq.md
        //使上拉加载具有弹性效果
        smartRefreshLayout.setEnableAutoLoadMore(false);
        //禁止越界拖动（1.0.4以上版本）
        smartRefreshLayout.setEnableOverScrollDrag(false);
        //关闭越界回弹功能
        smartRefreshLayout.setEnableOverScrollBounce(false);
        // 这个功能是本刷新库的特色功能：在列表滚动到底部时自动加载更多。 如果不想要这个功能，是可以关闭的：
        smartRefreshLayout.setEnableAutoLoadMore(false);
        final MaterialHeader mMaterialHeader = (MaterialHeader) smartRefreshLayout.getRefreshHeader();
        if (mMaterialHeader != null) {
            //noinspection RedundantArrayCreation
            mMaterialHeader.setColorSchemeColors(new int[]{ViewPlus.getConfiguration(ConfigKeys.APP_THEME_COLOR)});
        }
        return smartRefreshLayout;
    }

    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    // https://stackoverflow.com/questions/9224056/android-bitmap-to-base64-string
    public static Bitmap convert(String base64Str) throws IllegalArgumentException {
        byte[] decodedBytes = Base64.decode(
                base64Str.substring(base64Str.indexOf(",") + 1),
                Base64.DEFAULT
        );
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String convert(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

}
