package cn.jiiiiiin.vplus.keyboard.ui;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;


/**
 *
 * @author jiiiiiin
 * @date 2017/9/13
 */

public class PasswordKeyboardBuild {

    private boolean isNum = true;
    private boolean isRandom = false;
    private boolean needHint = true;
    private int maxSize = 6;
    private String timestamp = null;
    private String modulus = null;
    private String name = null;
    private PasswordEditListener passwordEditListener;
    private Integer visible = null;
    private WebView webview = null;
    private View maskView = null;
    private PasswordShowOrHideListener passwordShowOrHideListener;

    private PasswordKeyboardBuild() {
    }

    private static class Holder {
        private static final PasswordKeyboardBuild INSTANCE = new PasswordKeyboardBuild();
    }

    public static PasswordKeyboardBuild getInstance() {
        return Holder.INSTANCE;
    }

    public PasswordKeyboardBuild withNum(Boolean isNum) {
        this.isNum = isNum;
        return this;
    }

    public PasswordKeyboardBuild withRandon(Boolean isRandom) {
        this.isRandom = isRandom;
        return this;
    }

    public PasswordKeyboardBuild withNeedHint(Boolean needHint) {
        this.needHint = needHint;
        return this;
    }

    public PasswordKeyboardBuild withTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public PasswordKeyboardBuild withModulus(String modulus) {
        this.modulus = modulus;
        return this;
    }

    public PasswordKeyboardBuild withMaxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }


    public PasswordKeyboardBuild withName(String name) {
        this.name = name;
        return this;
    }

    public PasswordKeyboardBuild withPasswordEditListener(PasswordEditListener passwordEditListener) {
        this.passwordEditListener = passwordEditListener;
        return this;
    }

    public PasswordKeyboardBuild withPasswordShowOrHideListener(PasswordShowOrHideListener passwordShowOrHideListener) {
        this.passwordShowOrHideListener = passwordShowOrHideListener;
        return this;
    }

    public PasswordKeyboardBuild withVisibility(int visible) {
        this.visible = visible;
        return this;
    }

    public PasswordKeyboardBuild withRelativeWebvView(WebView webview) {
        this.webview = webview;
        return this;
    }

    public PasswordKeyboardBuild withMaskView(View maskView) {
        this.maskView = maskView;
        return this;
    }

    public final PasswordEditText build(@NonNull PasswordEditText passwordEditText) {
        passwordEditText.setNum(this.isNum);
        passwordEditText.setRandom(this.isRandom);
        passwordEditText.setNeedHint(this.needHint);
        passwordEditText.setTimestamp(this.timestamp);
        passwordEditText.setModulus(this.modulus);
        passwordEditText.setMaxSize(this.maxSize);
        if (this.visible != null && (this.visible == View.VISIBLE || this.visible == View.GONE)) {
            passwordEditText.setVisibility(this.visible);
        }
        if (!TextUtils.isEmpty(this.name)) {
            passwordEditText.setJsName(this.name);
        }
        if (this.passwordEditListener != null) {
            passwordEditText.setPasswordListener(this.passwordEditListener);
        }
        if (this.passwordShowOrHideListener != null) {
            passwordEditText.setPasswordShowOrHideListener(this.passwordShowOrHideListener);
        }
        if(this.webview != null){
            passwordEditText.setRelativeWebView(this.webview);
        }
        if(this.maskView != null){
            passwordEditText.setMaskView(this.maskView);
        }
        _recover();
        return passwordEditText;
    }

    private void _recover() {
        this.isNum = true;
        this.isRandom = false;
        this.needHint = true;
        this.maxSize = 6;
        this.timestamp = null;
        this.modulus = null;
        this.name = null;
        this.visible = null;
        this.passwordEditListener = null;
        this.webview = null;
        this.maskView = null;
    }


}
