package cn.jiiiiiin.viewplus.icon;


import com.joanzapata.iconify.Icon;
import com.joanzapata.iconify.IconFontDescriptor;

/**
 * "iconfont.ttf"; -> MobileBank/ynrcc-pmobilebank/src/main/assets/iconfont.ttf
 * @author jiiiiiin
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
