package cn.jiiiiiin.vplus.core.util.device;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.orhanobut.hawk.Hawk;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.UUID;

import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.dict.HawkKey;
import cn.jiiiiiin.vplus.core.exception.ViewPlusException;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.util.ui.ViewUtil;
import cn.jiiiiiin.vplus.core.webview.jsbridgehandler.exception.JSBridgeException;
import io.reactivex.functions.Consumer;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


/**
 * @author jiiiiiin
 */
@SuppressLint("CheckResult")
public class DeviceUtil {

    private static final Handler HANDLER = ViewPlus.getHandler();
    /**
     * 存储路径（在取不到设备id时存储自己生成的id路径）
     */
    private static final String DIR_PATH = "/sdcard/.Android/";
    /**
     * 文件名
     */
    private static final String ID_FILE_NAME = "deviceId";


    public interface IGenerateDeviceIdCallBack {
        void setDeviceId(String id);

        void userNoGranted();

        void generateDeviceIdFail(String msg);
    }

    public interface IGeneratePhoneNumberCallBack {
        void setPhone(Boolean boo) throws JSBridgeException;
    }

    public interface IGeneratePhotoCallBack {
        void setPhoto(Boolean boo);
    }

    public interface IGenerateDeviceStateCallBack {
        void setDeviceState(Boolean boo);
    }

    public interface IGenerateReadSmsCallBack {
        void setReadSms(Boolean boo);
    }

    public interface IGenerateLocationCallBack {
        void setLocation(Boolean boo);
    }

    public interface IGeneratePermissionsCallBack {
        void setPermissions(Boolean boo);
    }

    /**
     * 请求麦克风权限
     */
    public static String GENERATE_PERMISSIONS_ERR_MSG_AUDIO = "应用获取麦克风权限被拒绝，相关功能将不能使用";

    public static void generateAudio(Activity activity, IGeneratePermissionsCallBack callBack) {
        ViewUtil.activityIsLivingCanByRun(activity, new ViewUtil.AbstractActivityIsLivingCanByRunCallBack() {
            @Override
            public void doIt(@NonNull Activity activity) {
                HANDLER.post(() -> {
                    RxPermissions rxPermissions = new RxPermissions(activity);
                    rxPermissions.request(Manifest.permission.RECORD_AUDIO)
                            .subscribe(callBack::setPermissions);
                });
            }
        });
    }

    /**
     * 请求位置权限
     */
    public static String GENERATE_PERMISSIONS_ERR_MSG_LOCATION_INFO = "应用获取位置权限被拒绝，相关功能将不能使用";

    public static void generateLocationInfo(Activity activity, IGenerateLocationCallBack callBack) {
        ViewUtil.activityIsLivingCanByRun(activity, new ViewUtil.AbstractActivityIsLivingCanByRunCallBack() {
            @Override
            public void doIt(@NonNull Activity activity) {
                HANDLER.post(() -> {
                    RxPermissions rxPermissions = new RxPermissions(activity);
                    rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                            .subscribe(callBack::setLocation);
                });
            }
        });
    }

    /**
     * 请求通讯录权限
     */
    public static String GENERATE_PERMISSIONS_ERR_MSG_PHONE_NUMBER = "应用获取通讯录的相关权限被拒绝，相关功能将不能使用";

    public static void genertePhoneNumber(Activity activity, IGeneratePhoneNumberCallBack callBack) {
        ViewUtil.activityIsLivingCanByRun(activity, new ViewUtil.AbstractActivityIsLivingCanByRunCallBack() {
            @Override
            public void doIt(@NonNull Activity activity) {
                HANDLER.post(() -> {
                    RxPermissions rxPermissions = new RxPermissions(activity);
                    rxPermissions.request(Manifest.permission.READ_CONTACTS,
                            Manifest.permission.WRITE_CONTACTS)
                            .subscribe(callBack::setPhone);
                });
            }
        });
    }

    /**
     * 读取短信验证码
     */
    public static String GENERATE_PERMISSIONS_ERR_READ_SMS = "应用读取短信的权限被拒绝，相关功能将不能使用";

    public static void generateReadSms(Activity activity, IGenerateReadSmsCallBack callBack) {
        ViewUtil.activityIsLivingCanByRun(activity, new ViewUtil.AbstractActivityIsLivingCanByRunCallBack() {
            @Override
            public void doIt(@NonNull Activity activity) {
                HANDLER.post(() -> {
                    RxPermissions rxPermissions = new RxPermissions(activity);
                    rxPermissions.request(Manifest.permission.READ_SMS)
                            .subscribe(callBack::setReadSms);
                });
            }
        });
    }

    public static String GENERATE_PERMISSIONS_ERR_MSG_CAMERA = "应用获取拍照的相关权限被拒绝，相关功能将不能使用";

    public static void generateCamera(Activity activity, IGeneratePhotoCallBack callback) {
        ViewUtil.activityIsLivingCanByRun(activity, new ViewUtil.AbstractActivityIsLivingCanByRunCallBack() {
            @Override
            public void doIt(@NonNull Activity activity) {
                HANDLER.post(() -> {
                    RxPermissions rxPermissions = new RxPermissions(activity);
                    rxPermissions.request(Manifest.permission.CAMERA).subscribe(callback::setPhoto);
                });
            }
        });
    }

    public static String GENERATE_PERMISSIONS_ERR_MSG_WRITEEXTERNALSTORAGE = "应用写入内存卡权限被拒绝，相关功能将不能使用";

    public static void generateWriteExternalStorage(Activity activity, Consumer<Boolean> onNext) {
        ViewUtil.activityIsLivingCanByRun(activity, new ViewUtil.AbstractActivityIsLivingCanByRunCallBack() {
            @Override
            public void doIt(@NonNull Activity activity) {
                HANDLER.post(() -> {
                    // https://work.bugtags.com/apps/1598731013063315/issues/1603332308217894/tags/1603332308233675?types=3&versions=1600310568035606&page=2
                    try {
                        RxPermissions rxPermissions = new RxPermissions(activity);
                        rxPermissions.request(WRITE_EXTERNAL_STORAGE).subscribe(onNext);
                    } catch (Exception e) {
                        LoggerProxy.e(e, "generateWriteExternalStorage err");
                        ToastUtils.showShort("检测应用写入内存卡权限失败");
                    }
                });
            }
        });
    }

    public static String GENERATE_PERMISSIONS_ERR_MSG_READ_EXTERNAL_STORAGE = "应用读取内存卡权限被拒绝，相关功能将不能使用";
    public static String GENERATE_PERMISSIONS_ERR_MSG_READ_EXTERNAL_STORAGE_EXPAND = "应用读取内存卡权限被拒绝，%s";

    public static void generateReadExternalStorage(Activity activity, Consumer<Boolean> onNext) {
        ViewUtil.activityIsLivingCanByRun(activity, new ViewUtil.AbstractActivityIsLivingCanByRunCallBack() {
            @Override
            public void doIt(@NonNull Activity activity) {
                HANDLER.post(() -> {
                    // https://work.bugtags.com/apps/1598731013063315/issues/1603332308217894/tags/1603332308233675?types=3&versions=1600310568035606&page=2
                    try {
                        RxPermissions rxPermissions = new RxPermissions(activity);
                        rxPermissions.request(READ_EXTERNAL_STORAGE).subscribe(onNext);
                    } catch (Exception e) {
                        LoggerProxy.e(e, "generateWriteExternalStorage err");
                    }
                });
            }
        });
    }

    /**
     * 涉及到动态权限，id必须异步获取，回调得到的可能为空
     *
     * @param activity
     * @param generateDeviceIdCallBack
     */
    @SuppressLint({"MissingPermission", "CheckResult"})
    public static void generateDeviceId(Activity activity, IGenerateDeviceIdCallBack generateDeviceIdCallBack) {
        activity.runOnUiThread(() -> {
            // ！需要动态权限
            RxPermissions rxPermissions = new RxPermissions(activity);
            rxPermissions
                    .request(Manifest.permission.READ_PHONE_STATE)
                    .subscribe(granted -> {
                        if (granted) {
                            // ! 使用2.0的算法获取设备id
//                        String temp = PhoneUtils.getIMEI();
//                        if (StringUtils.isEmpty(temp)) {
//                            // 获取mac地址 https://www.jianshu.com/p/92acd8d028aa
//                            temp = DeviceUtils.getMacAddress();
//                        }
//                        // please open wifi 来自DeviceUtils.getMacAddress();
//                        if (StringUtils.isEmpty(temp) || temp.equals("please open wifi")) {
//                            temp = PhoneUtils.getIMSI();
//                        }
//                        if (temp == null || StringUtils.isEmpty(temp)) {
//                            temp = generateDeviceId(activity);
//                        }
                            String temp = Hawk.get(HawkKey.HAWK_KEY_DEVICEID, null);
//                            LoggerProxy.e("temp device id : " + temp);
                            try {
                                if (StringUtils.isTrimEmpty(temp)) {
                                    temp = generateDeviceId(activity);
                                    // 缓存
                                    Hawk.put(HawkKey.HAWK_KEY_DEVICEID, temp);
                                }
                                if (StringUtils.isEmpty(temp)) {
                                    generateDeviceIdCallBack.generateDeviceIdFail("获取不到设备唯一标示");
                                } else {
                                    generateDeviceIdCallBack.setDeviceId(temp);
                                }
                            } catch (ViewPlusException e) {
                                generateDeviceIdCallBack.generateDeviceIdFail(e.getMessage());
                            }
                        } else {
                            generateDeviceIdCallBack.userNoGranted();
                        }
                    });
        });
    }

    /**
     * 涉及到动态权限，id必须异步获取，回调得到的可能为空
     *
     * @param activity
     * @param generateDeviceIdCallBack
     */
    @SuppressLint({"MissingPermission", "CheckResult"})
    public static void generateRealDeviceId(Activity activity, IGenerateDeviceIdCallBack generateDeviceIdCallBack) {
        activity.runOnUiThread(() -> {
            // ！需要动态权限
            RxPermissions rxPermissions = new RxPermissions(activity);
            rxPermissions
                .request(Manifest.permission.READ_PHONE_STATE)
                .subscribe(granted -> {
                    if (granted) {
                        String temp = Hawk.get(HawkKey.HAWK_KEY_REAL_DEVICEID, null);
//                            LoggerProxy.e("temp device id : " + temp);
                        try {
                            if (StringUtils.isTrimEmpty(temp)) {
                                temp = generateRealDeviceId(activity);
                                // 缓存
                                Hawk.put(HawkKey.HAWK_KEY_REAL_DEVICEID, temp);
                            }
                            if (StringUtils.isEmpty(temp)) {
                                generateDeviceIdCallBack.generateDeviceIdFail("获取不到设备唯一标示");
                            } else {
                                generateDeviceIdCallBack.setDeviceId(temp);
                            }
                        } catch (ViewPlusException e) {
                            generateDeviceIdCallBack.generateDeviceIdFail(e.getMessage());
                        }
                    } else {
                        generateDeviceIdCallBack.userNoGranted();
                    }
                });
        });
    }

    public static void generateDevice(Activity activity, IGenerateDeviceStateCallBack callback) {
        ViewUtil.activityIsLivingCanByRun(activity, new ViewUtil.AbstractActivityIsLivingCanByRunCallBack() {
            @Override
            public void doIt(@NonNull Activity activity) {
                HANDLER.post(() -> {
                    RxPermissions rxPermissions = new RxPermissions(activity);
                    rxPermissions.request(Manifest.permission.READ_PHONE_STATE).subscribe(callback::setDeviceState);
                });
            }
        });

    }

    /**
     * 老方法
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String generateDeviceId(Activity context) throws ViewPlusException {
        try {
            String id = "";
            TelephonyManager TelephonyMgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String szImei = "";
            /*This method was deprecated in API level 26. Use (@link getImei}
            which returns IMEI for GSM or (@link getMeid} which returns MEID for CDMA.
            getDeviceId()在API 26已经弃用，所以API 26以上的使用getImei()*/
            if (null != TelephonyMgr) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    try {
                        //SDK<26,使用TelephonyMgr.getDeviceId()获取IMEI
                        szImei = TelephonyMgr.getDeviceId() == null ? "" : TelephonyMgr.getDeviceId();
//                    LoggerProxy.e("old %s", szImei);
                    } catch (Exception e) {
                        e.printStackTrace();
                        szImei = null;
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {
                        //Android Q（Android 10，SDK 29）获取不到IMEI，使用TelephonyMgr.getImei()不会返回null，会抛出异常
                        //getImeiForSlot: The user 10130 does not meet the requirements to access device identifiers.
                        szImei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//                    LoggerProxy.e("sdk_int >= 29 %s", szImei);
                    } catch (Exception e) {
                        e.printStackTrace();
                        szImei = null;
                    }
                } else {
                    try {
                        //sdk 版本26到28采用TelephonyMgr.getImei()获取IMEI
                        szImei = TelephonyMgr.getImei();
//                    LoggerProxy.e("new  %s", szImei);
                    } catch (Exception e) {
                        e.printStackTrace();
                        szImei = null;
                    }
                }
            }
            if (null != szImei && !"".equals(szImei)) {
                id = szImei;
            } else {
                //原来的逻辑是IMEI获取不到就获取MAC地址，但是在6.0之后MAC地址和蓝牙地址获取不到
                //WifiInfo.getMacAddress() 方法和 BluetoothAdapter.getAddress() 方法现在会返回常量值 02:00:00:00:00:00
                //所以这里修改为6.0之后获取ANDROID_ID
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    try {
                        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        assert wm != null;
                        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress() == null ? ""
                                : wm.getConnectionInfo().getMacAddress();
                        id = m_szWLANMAC;
//                    LoggerProxy.e("wlanmac  %s", id);
                    } catch (Exception e) {
                        e.printStackTrace();
                        id = null;
                    }
                } else {
                    try {
                        id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//                    LoggerProxy.e("android id  %s", id);
                    } catch (Exception e) {
                        e.printStackTrace();
                        id = null;
                    }
                }
            }
            MessageDigest m = null;
            if (null == id) {
                id = getIdentity(context);
            }
            m = MessageDigest.getInstance("MD5");
            m.update(id.getBytes(), 0, id.length());
            // get md5 bytes
            byte[] p_md5Data = m.digest();
            // create a hex string
            String m_szUniqueID = new String();
            for (int i = 0; i < p_md5Data.length; i++) {
                int b = (0xFF & p_md5Data[i]);
                // if it is a single digit, make sure it have 0 in front (proper
                // padding)
                if (b <= 0xF) {
                    m_szUniqueID += "0";
                }
                // add number to string
                m_szUniqueID += Integer.toHexString(b);
            }
            // hex string to uppercase
            m_szUniqueID = m_szUniqueID.toUpperCase();
            return m_szUniqueID;
        } catch (Exception e) {
            LoggerProxy.e("error:" + e.getMessage());
            throw new ViewPlusException(String.format("获取设备唯一标示错误[%s]", e.getMessage()));
        }
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String generateRealDeviceId(Activity context) throws ViewPlusException {
        try {
            String id = "";
            TelephonyManager TelephonyMgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
            String szImei = "";
            /*This method was deprecated in API level 26. Use (@link getImei}
            which returns IMEI for GSM or (@link getMeid} which returns MEID for CDMA.
            getDeviceId()在API 26已经弃用，所以API 26以上的使用getImei()*/
            if (null != TelephonyMgr) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    try {
                        //SDK<26,使用TelephonyMgr.getDeviceId()获取IMEI
                        szImei = TelephonyMgr.getDeviceId() == null ? "" : TelephonyMgr.getDeviceId();
//                    LoggerProxy.e("old %s", szImei);
                    } catch (Exception e) {
                        e.printStackTrace();
                        szImei = null;
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {
                        //Android Q（Android 10，SDK 29）获取不到IMEI，使用TelephonyMgr.getImei()不会返回null，会抛出异常
                        //getImeiForSlot: The user 10130 does not meet the requirements to access device identifiers.
                        szImei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//                    LoggerProxy.e("sdk_int >= 29 %s", szImei);
                    } catch (Exception e) {
                        e.printStackTrace();
                        szImei = null;
                    }
                } else {
                    try {
                        //sdk 版本26到28采用TelephonyMgr.getImei()获取IMEI
                        szImei = TelephonyMgr.getImei();
//                    LoggerProxy.e("new  %s", szImei);
                    } catch (Exception e) {
                        e.printStackTrace();
                        szImei = null;
                    }
                }
            }
            if (null != szImei && !"".equals(szImei)) {
                id = szImei;
            } else {
                //原来的逻辑是IMEI获取不到就获取MAC地址，但是在6.0之后MAC地址和蓝牙地址获取不到
                //WifiInfo.getMacAddress() 方法和 BluetoothAdapter.getAddress() 方法现在会返回常量值 02:00:00:00:00:00
                //所以这里修改为6.0之后获取ANDROID_ID
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    try {
                        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        assert wm != null;
                        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress() == null ? ""
                            : wm.getConnectionInfo().getMacAddress();
                        id = m_szWLANMAC;
//                    LoggerProxy.e("wlanmac  %s", id);
                    } catch (Exception e) {
                        e.printStackTrace();
                        id = null;
                    }
                } else {
                    try {
                        id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//                    LoggerProxy.e("android id  %s", id);
                    } catch (Exception e) {
                        e.printStackTrace();
                        id = null;
                    }
                }
            }
            if (null == id) {
                id = getIdentity(context);
            }

            return id;
        } catch (Exception e) {
            LoggerProxy.e("error:" + e.getMessage());
            throw new ViewPlusException(String.format("获取设备唯一标示错误[%s]", e.getMessage()));
        }
    }

    @SuppressLint("CommitPrefEdits")
    private static String getIdentity(Context context) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        String identity = preference.getString("identity", "");
        if ("".equals(identity)) {
            identity = readIdFromFile();
            if ("".equals(identity)) {
                identity = UUID.randomUUID().toString();
                preference.edit().putString("identity", identity);
                writeIdToFile(identity);
            }
            preference.edit().putString("identity", identity);
        }
//        LoggerProxy.e("test identity %s", identity);
        return identity;
    }

    private static void writeIdToFile(String identity) {
        File path = new File(DIR_PATH);
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(path, ID_FILE_NAME);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file, false);
            out.write(identity.getBytes(), 0, identity.length());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != out) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String readIdFromFile() {
        String id = "";
        File file = new File(DIR_PATH, ID_FILE_NAME);
        if (file.exists()) {
            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int ret = in.read(bytes, 0, 1024);
                if (ret != -1) {
                    id = Arrays.toString(bytes).trim();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != in) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return id;
    }

    // TODO ！！！ 上线必须注释掉
    // executes a command on the system
//    private static boolean canExecuteCommand(String command) {
//        Process process = null;
//        BufferedReader in = null;
//        try {
//            process = Runtime.getRuntime().exec(command);
//            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String info = in.readLine();
//            if (info != null) {
//                return true;
//            } else {
//                return false;
//            }
//        } catch (Exception e) {
//            //do noting
//        } finally {
//            if (process != null) process.destroy();
//            if (in != null) {
//                try {
//                    in.close();
//                } catch (IOException e) {
//                    if (ViewPlus.IS_DEBUG()) {
//                        LoggerProxy.e("canExecuteCommand err %s", e.getMessage());
//                    }
//                }
//            }
//        }
//        return false;
//    }

}
