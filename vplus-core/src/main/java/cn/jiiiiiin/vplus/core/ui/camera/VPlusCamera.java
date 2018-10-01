package cn.jiiiiiin.vplus.core.ui.camera;

import android.net.Uri;

import cn.jiiiiiin.vplus.core.util.file.FileUtil;


/**
 * Created by jiiiiiin
 * 照相机调用类
 */

public class VPlusCamera {

    public static Uri createCropFile() {
        return Uri.parse
                (FileUtil.createFile("crop_image",
                        FileUtil.getFileNameByTime("IMG", "jpg")).getPath());
    }

//    /**
//     * 进行相册、拍照选择
//     * @param delegate
//     */
//    public static void start(AbstractPermissionCheckerDelegate delegate) {
//        new CameraHandler(delegate).beginCameraDialog();
//    }
}
