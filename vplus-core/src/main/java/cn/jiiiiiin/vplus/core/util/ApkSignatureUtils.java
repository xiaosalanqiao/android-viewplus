package cn.jiiiiiin.vplus.core.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import androidx.annotation.NonNull;
import android.util.Base64;

import java.security.MessageDigest;

import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;

/**
 * 参考： https://www.cnblogs.com/goodhacker/p/4842215.html
 *
 * @author jiiiiiin
 * @version 1.0
 */
public final class ApkSignatureUtils {

    public static boolean checkAppSignature(@NonNull Context context, @NonNull String realSignature) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final String currentSignature = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                // 上面得到的会多一个/n，故要做截取
                final boolean res = realSignature.equals(currentSignature.substring(0, currentSignature.length()-1));
                if (ViewPlus.IS_DEBUG()) {
                    LoggerProxy.d("Include this string as a value for SIGNATURE:%s %s %s", currentSignature, realSignature, res);
                }
                // compare signatures
                return res;
            }
        } catch (Exception e) {
            //assumes an issue in checking signature., but we let the caller decide on what to do.
            LoggerProxy.e(e, "cas err");
        }
        return false;
    }
}
