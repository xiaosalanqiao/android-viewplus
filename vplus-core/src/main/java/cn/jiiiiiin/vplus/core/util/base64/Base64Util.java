package cn.jiiiiiin.vplus.core.util.base64;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Base64;

import com.blankj.utilcode.util.EncodeUtils;

import java.io.ByteArrayOutputStream;

import cn.jiiiiiin.vplus.core.exception.ViewPlusException;
import cn.jiiiiiin.vplus.core.exception.ViewPlusRuntimeException;

/**
 * @author jiiiiiin
 * @version 1.0
 */

public class Base64Util {

    public static String base64DecodeToStr(final String input) {
        try {
            return new String(EncodeUtils.base64Decode(input));
        } catch (java.lang.IllegalArgumentException e) {
            throw new ViewPlusRuntimeException(String.format("解析base64字符串出错，不是一个正确的base64字符串[%s]", input));
        }
    }

    /**
     * 根据图片uri转成base64编码字符串
     *
     * @param context
     * @param uri
     * @return
     */
    public static String uriToBase64(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(context, uri, projection, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        byte[] encode = Base64.encode(bytes, Base64.DEFAULT);
        bitmap.recycle();
        cursor.close();
        return new String(encode);
    }

    public static String base64EncodeToStr(String msg) {
        return new String(EncodeUtils.base64Encode((msg)));
    }
}
