package cn.jiiiiiin.vplus.keyboard.ui;

import android.webkit.WebView;

public interface PasswordKeyboard {
	void showKeyboard();
	void setRandom(boolean random);
	void setJsName(String jsName);
	void setJsObjectIndex(String jsObjectIndex);
	void setRelativeWebView(WebView webView);
}
