package cn.jiiiiiin.vplus.ui.image;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.luck.picture.lib.PictureSelectionModel;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;

import cn.jiiiiiin.vplus.ui.R;

/**
 * @author jiiiiiin
 * @version 1.0
 */
public final class PictureSelectorUtil {

    /**
     * 相册/拍照基础配置
     *
     * @param activity
     * @return
     */
    public static PictureSelectionModel initBasePictureSelector(@NonNull Activity activity) {
        return PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage())
                .theme(R.style.picture_default_style)
                // 拍照之后存储的图片后缀
                .imageFormat(PictureMimeType.PNG)
                .isCamera(true);
    }

    /**
     * 相册/拍照/裁切基础配置
     * 示例：
     * PictureSelectorUtil.initCropPictureSelector(_mActivity, 1, 1, 120, 120, 50)
     * .selectionMode(PictureConfig.SINGLE)
     * .circleDimmedLayer(true)
     * .maxSelectNum(1)
     * .minSelectNum(1)
     * .selectionMedia(mLocalMediaList)
     * .forResult(ReqCodeDict.REQUEST_CODE_CHOOSE_REQUEST_UPLOAD_USER_AVATAR);
     * <p>
     * 建议选择完毕之后直接使用https://github.com/Curzibn/Luban 进行压缩，这里不要压缩
     *
     * @param activity
     * @param aspect_ratio_x
     * @param aspect_ratio_y
     * @param cropWidth
     * @param cropHeight
     * @param minimumCompressSize 小于minimumCompressSize的图片不压缩
     * @return
     */
    public static PictureSelectionModel initCropPictureSelector(@NonNull Activity activity, int aspect_ratio_x, int aspect_ratio_y, int cropWidth, int cropHeight, boolean enableCompress, int minimumCompressSize) {
        return PictureSelectorUtil.initBasePictureSelector(activity)
                .enableCrop(true)
                .withAspectRatio(aspect_ratio_x, aspect_ratio_y)
                .cropWH(cropWidth, cropHeight)
                .hideBottomControls(false)
                .freeStyleCropEnabled(true)
                .showCropFrame(false)
                .showCropGrid(true)
                .previewEggs(true)
                .compress(enableCompress)
                .cropCompressQuality(100)
                .minimumCompressSize(minimumCompressSize);
    }
}
