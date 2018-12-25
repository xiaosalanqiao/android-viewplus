package cn.jiiiiiin.viewplus.icon;

import com.joanzapata.iconify.Icon;

/**
 * &#xe61d; -》 \ue61d
 *
 * @author jiiiiiin
 */

@SuppressWarnings("AlibabaEnumConstantsMustHaveComment")
public enum YNRCCIcons implements Icon {
    icon_bottom_menu_service('\ue64a'),
    icon_bottom_menu_finance('\ue64b'),
    icon_bottom_menu_home('\ue650'),
    icon_bottom_menu_life('\ue64d'),
    icon_bottom_menu_user_center('\ue653'),
    icon_back('\ue689'),
    icon_close('\ue687'),
    icon_scan('\ue655'),
    icon_nearby('\ue64a'),
    icon_service('\ue68c'),
    icon_exit('\ue675'),
    icon_wallet('\ue68d'),
    icon_face_to_face('\ue66f'),
    icon_more('\ue686'),
    icon_dianhua('\ue697'),
    icon_tongzhi('\ue696'),
    icon_weizhi('\ue695'),
    icon_cunchu('\ue694'),
    icon_login_text('\ue69b'),
    icon_exit_text('\ue69c'),
    icon_bank('\ue6a2'),
    icon_atm('\ue6a3'),
    icon_self_bank('\ue6a4'),
    icon_dianhua_img('\ue698');

    private char character;

    YNRCCIcons(char character) {
        this.character = character;
    }

    @Override
    public String key() {
        return this.name().replace('_', '-');
    }

    @Override
    public char character() {
        return this.character;
    }
}
