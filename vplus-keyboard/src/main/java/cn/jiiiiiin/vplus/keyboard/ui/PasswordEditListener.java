package cn.jiiiiiin.vplus.keyboard.ui;

/**
 * Created by jiiiiiin on 2017/9/12.
 */

public interface PasswordEditListener {

    String TAP_DELETE = "tapdelete";
    String TAP_OK = "tapok";
    String TAP_CHARACTER = "tapcharacter";

    /**
     * 只有eventName=PasswordEditListener.TAP_OK的时候才有level属性，否则level为null，遗留问题
     * @param evenName
     * @param level
     */
    void onKeyPressed(String evenName, String level);
}
