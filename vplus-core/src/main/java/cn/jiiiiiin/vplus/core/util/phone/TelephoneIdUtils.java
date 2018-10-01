package cn.jiiiiiin.vplus.core.util.phone;

import android.content.Context;
import android.telephony.TelephonyManager;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by pll on 2017/11/27.
 */

public class TelephoneIdUtils {
    public static String getPhoneId(Context context){
        TelephonyManager telephonyMgr = (TelephonyManager)context.getSystemService(TELEPHONY_SERVICE);
        String phoneId = telephonyMgr.getDeviceId();
        if(phoneId!=null && phoneId.equals("")!=true){
            return phoneId;
        }else{
            throw new RuntimeException("设备ID不正常");
        }
    }
}
