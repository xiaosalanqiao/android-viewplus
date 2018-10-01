package cn.jiiiiiin.vplus.core.util.Intent;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

/**
 * @author jiiiiiin
 * @version 1.0
 */
public class RouterUtil {

    public static void start4ActionDialProtocol(Activity activity, @NonNull String url) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
        ContextCompat.startActivity(activity, intent, null);
    }
}
