package cn.jiiiiiin.vplus.core.util.ui;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author jiiiiiin
 * @version 1.0
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class FileAndSDCardUtils {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File copyResource(Context context, String src, String dest, int flag) {
        File filesDir = null;
        try {
            if (flag == 0) {
                // copy to sdcard
                // 向SD卡的根目录下存放文件
                filesDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/ynrcc/" + dest);
                File parentDir = filesDir.getParentFile();
                if (!parentDir.exists()) {
                    parentDir.mkdirs();
                }
            } else {
                // copy to data
                // getFilesDir()获取你app的内部存储空间，相当于你的应用在内部存储上的根目录
                filesDir = new File(context.getFilesDir(), dest);
            }
            if (!filesDir.exists()) {
                filesDir.createNewFile();
                InputStream open = context.getAssets().open(src);
                FileOutputStream fileOutputStream = new FileOutputStream(filesDir);
                byte[] buffer = new byte[4 * 1024];
                int len = 0;
                while ((len = open.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                }
                open.close();
                fileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (flag == 0) {
                filesDir = copyResource(context, src, dest, 1);
            }
        }
        return filesDir;
    }

}
