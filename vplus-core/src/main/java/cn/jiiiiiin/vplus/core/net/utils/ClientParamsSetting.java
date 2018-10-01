package cn.jiiiiiin.vplus.core.net.utils;

import java.util.WeakHashMap;

/**
 * Created by pll on 2017/11/16.
 */

public class ClientParamsSetting {
    public static final WeakHashMap<String,Object> paramsSetting(){
        WeakHashMap<String,Object> map = new WeakHashMap<>();
        map.put("BSMobileClientApp","MOBILEBANK");
        map.put("BSMobileClientVer","3.01");
        map.put("BSMobileDevice","IPHONE");
        map.put("BSMobileDeviceId","662375c0baae4489cb6716a9fa357e51");
        map.put("IsIphone3","3");
        map.put("IsPortal","0");
        return map;
    }

    /**
     * 请求拼接方式
     * @param url
     * @return
     */
    public static final String urlAdd(String url){
        url = url+"?BankId=9999&LoginType=K&_locale=zh_CN";
        return url;
    }
}
