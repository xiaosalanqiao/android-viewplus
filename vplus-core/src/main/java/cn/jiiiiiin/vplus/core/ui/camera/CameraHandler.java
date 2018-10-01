package cn.jiiiiiin.vplus.core.ui.camera;

import android.view.View;

/**
 * Created by jiiiiiin
 * 照片处理类
 */

public class CameraHandler implements View.OnClickListener {

//    private final AlertDialog DIALOG;
//    private final AbstractPermissionCheckerDelegate DELEGATE;
//
//    public CameraHandler(AbstractPermissionCheckerDelegate delegate) {
//        this.DELEGATE = delegate;
//        DIALOG = new AlertDialog.Builder(delegate.getContext()).create();
//    }
//
//    // 开启相机、相册选择或取消
//    final void beginCameraDialog() {
//        DIALOG.show();
//        final Window window = DIALOG.getWindow();
//        if (window != null) {
//            window.setContentView(R.layout.dialog_camera_panel);
//            window.setGravity(Gravity.BOTTOM);
//            window.setWindowAnimations(R.style.anim_panel_up_from_bottom);
//            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            //设置属性
//            final WindowManager.LayoutParams params = window.getAttributes();
//            params.width = WindowManager.LayoutParams.MATCH_PARENT;
//            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            params.dimAmount = 0.5f;
//            window.setAttributes(params);
//
//            window.findViewById(R.id.photodialog_btn_cancel).setOnClickListener(this);
//            window.findViewById(R.id.photodialog_btn_take).setOnClickListener(this);
//            window.findViewById(R.id.photodialog_btn_native).setOnClickListener(this);
//        }
//    }
//
//    // 创建拍照得到图片的文件名
//    private String getPhotoName() {
//        return FileUtil.getFileNameByTime("IMG", "jpg");
//    }
//
//    private void takePhoto() {
//        final String currentPhotoName = getPhotoName();
//        // 拍照意图
//        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        final File tempFile = new File(FileUtil.CAMERA_PHOTO_DIR, currentPhotoName);
//
//        // 兼容7.0及以上的写法
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            // 每次值传递一个数据
//            final ContentValues contentValues = new ContentValues(1);
//            contentValues.put(MediaStore.Images.Media.DATA, tempFile.getPath());
//            final Uri uri = DELEGATE.getContext().getContentResolver().
//                    insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
//            // 需要将Uri路径转化为实际路径，图片的路径
//            final File realFile =
//                    FileUtils.getFileByPath(FileUtil.getRealFilePath(DELEGATE.getContext(), uri));
//            final Uri realUri = Uri.fromFile(realFile);
//            CameraImageBean.getInstance().setPath(realUri);
//            // 设置拍完之后的照片的存储位置
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        } else {
//            final Uri fileUri = Uri.fromFile(tempFile);
//            CameraImageBean.getInstance().setPath(fileUri);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//        }
//        // 调起拍照，onActivityResult将会在PermissionCheckerDelegate中进行处理
//        DELEGATE.startActivityForResult(intent, RequestCodes.TAKE_PHOTO);
//    }
//
//    // 从相册选择（默认相册）
//    private void pickPhoto() {
//        final Intent intent = new Intent();
//        // 设置过滤文件类型
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        // TODO 标题？
//        DELEGATE.startActivityForResult
//                (Intent.createChooser(intent, "选择获取图片的方式"), RequestCodes.PICK_PHOTO);
//    }

    @Override
    public void onClick(View v) {
//        int id = v.getId();
//        if (id == R.id.photodialog_btn_cancel) {
//            DIALOG.cancel();
//        } else if (id == R.id.photodialog_btn_take) {
//            takePhoto();
//            DIALOG.cancel();
//        } else if (id == R.id.photodialog_btn_native) {
//            pickPhoto();
//            DIALOG.cancel();
//        }
    }
}
