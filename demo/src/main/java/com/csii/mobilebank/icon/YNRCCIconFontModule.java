package com.csii.mobilebank.icon;


import com.joanzapata.iconify.Icon;
import com.joanzapata.iconify.IconFontDescriptor;

/**
 * "iconfont.ttf"; -> MobileBank/ynrcc-pmobilebank/src/main/assets/iconfont.ttf
 * @author jiiiiiin
 * @date 2017/8/4
 */

@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class YNRCCIconFontModule implements IconFontDescriptor {

    private static final String FILE_NAME = "iconfont.ttf";

    @Override
    public String ttfFileName() {
        return FILE_NAME;
    }

    @Override
    public Icon[] characters() {
        return YNRCCIcons.values();
    }
}
