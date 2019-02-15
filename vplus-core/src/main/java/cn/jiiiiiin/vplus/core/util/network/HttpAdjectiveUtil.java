package cn.jiiiiiin.vplus.core.util.network;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;

import cn.jiiiiiin.vplus.core.R;
import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.exception.ViewPlusRuntimeException;
import cn.jiiiiiin.vplus.core.ui.loader.LoaderCreatorProxy;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.util.ui.ViewUtil;

/**
 * @author jiiiiiin
 */

public final class HttpAdjectiveUtil {

    /**
     * 是否使用代理(WiFi状态下的,避免被抓包)
     * https://www.jianshu.com/p/4a99f524e0dc
     */
    public static boolean isWifiProxy(Activity activity){
        final boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
        String proxyAddress;
        int proxyPort;
        if (IS_ICS_OR_LATER) {
            proxyAddress = System.getProperty("http.proxyHost");
            String portstr = System.getProperty("http.proxyPort");
            proxyPort = Integer.parseInt((portstr != null ? portstr : "-1"));
            System.out.println(proxyAddress + "~");
            System.out.println("port = " + proxyPort);
        }else {
            proxyAddress = android.net.Proxy.getHost(activity);
            proxyPort = android.net.Proxy.getPort(activity);
            LoggerProxy.e("address = ", proxyAddress + "~");
            LoggerProxy.e("port = ", proxyPort + "~");
        }
        return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
    }

    /**
     * 是否正在使用VPN
     */
    public static boolean isVpnUsed() {
        try {
            Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
            if(niList != null) {
                for (NetworkInterface intf : Collections.list(niList)) {
                    if(!intf.isUp() || intf.getInterfaceAddresses().size() == 0) {
                        continue;
                    }
                    LoggerProxy.e("isVpnUsed() NetworkInterface Name: " + intf.getName());
                    if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())){
                        return true; // The VPN is up
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isJsRes(Uri url) {
        if (url != null) {
            final String path = url.getPath();
            if (!StringUtils.isTrimEmpty(path) && path.contains(".")) {
                // 获得后缀
                final String temp = path.substring(path.lastIndexOf(".") + 1);
                return "JS".equals(temp.toUpperCase());
            } else {
                return url.toString().toUpperCase().endsWith(".JS");
            }
        } else {
            return false;
        }
    }

    public static boolean isMediaRes(Uri url) {
        if (url != null) {
            final String path = url.getPath();
            if (!StringUtils.isTrimEmpty(path) && path.contains(".")) {
                // 获得后缀
                final String temp = path.substring(path.lastIndexOf("."));
                return ImageUtils.isImage(temp);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean canAccess2NewWork() {
        // https://www.jianshu.com/p/fa877daf7406 http://blog.csdn.net/songjunyan/article/details/41456847
        ConnectivityManager connectivityManager = (ConnectivityManager) ViewPlus.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            // http://www.picksomething.cn/?p=560
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null) {
                return false;
            } else {
                // TODO 待测试
                return networkInfo.isConnected();
                // return networkInfo.isAvailable();
            }
        } else {
            return NetworkUtils.isAvailableByPing();
        }
    }

    public static boolean isEqualsHost(String url, String originalUrl) {
        if (RegexUtils.isURL(url) && RegexUtils.isURL(originalUrl)) {
            Uri uri = Uri.parse(url);
            Uri originalUri = Uri.parse(originalUrl);
            return uri.getHost().equals(originalUri.getHost()) && uri.getPort() == originalUri.getPort();
        } else {
            throw new ViewPlusRuntimeException("待对比的url参数不是一个正确的链接地址");
        }
    }

    public static boolean isHttpOrHttpsUrl(String url) {
        return !StringUtils.isTrimEmpty(url) && (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url));
    }

    public static boolean isCSSRes(Uri url) {
        if (url != null) {
            final String path = url.getPath();
            if (!StringUtils.isTrimEmpty(path) && path.contains(".")) {
                // 获得后缀
                final String temp = path.substring(path.lastIndexOf(".") + 1);
                return "JS".equals(temp.toUpperCase());
            } else {
                return url.toString().toUpperCase().endsWith(".CSS");
            }
        } else {
            return false;
        }
    }

    public interface DownloadImageListener {

        void success(Bitmap bitmap);

        void failed(Exception e);

    }

    public static void downloadImage(Activity activity, String url, boolean showLoading, int width, int height, DownloadImageListener downloadImageListener) {
        if (showLoading) {
            LoaderCreatorProxy.showLoading(activity, "分享资源加载中...");
        }
        // TODO 改成线程池
        new Thread(() -> {
            try {
                RequestOptions options = new RequestOptions()
                        .override(width, height);
                FutureTarget<Bitmap> target = Glide.with(activity)
                        .asBitmap()
                        .apply(ViewUtil.RECYCLER_OPTIONS4GLIDE_ICON)
                        .apply(options)
                        .load(url)
                        .submit();
                final Bitmap bitmap = target.get();
                activity.runOnUiThread(() -> downloadImageListener.success(bitmap));
            } catch (Exception e) {
                LoggerProxy.e(e, "转换图片失败");
                downloadImageListener.failed(e);
            } finally {
                if (showLoading) {
                    LoaderCreatorProxy.stopLoading();
                }
            }
        }).start();
    }

}
    