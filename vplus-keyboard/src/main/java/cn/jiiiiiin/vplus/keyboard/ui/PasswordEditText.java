package cn.jiiiiiin.vplus.keyboard.ui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.nio.file.LinkPermission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import cn.jiiiiiin.vplus.keyboard.R;
import cn.jiiiiiin.vplus.security.cypher.CSIICypher;
import cn.jiiiiiin.vplus.security.cypher.Cypher;
import cn.jiiiiiin.vplus.security.utils.EncryptUtils;

/**
 * TODO 一些涉及到webview交互的桥接事件需要修改
 */
public class PasswordEditText extends AppCompatEditText implements OnFocusChangeListener, OnClickListener, OnKeyboardActionListener {

    private static final String TAG = "xxx";
    private static final String CHAR_DELETE = "delete";
    private static final String CHAR_CLEAR = "clear";
    private static final String CHAR_OK = "ok";
    private static final String CHAR_PASSWORD = "passwordchar";
    private static final int KEYBOARD_SYMBOL = -103;
    private static final int KEYBOARD_NUM = -102;
    private static final int KEYBOARD_LETTER = -101;
    private static final int KEYBOARD_MORESYMBOL = -6;
    private static final int KEYBOARD_RETURNTOSYMBOL = -7;
    private static final int KEYCODE_SHIFT = 14;
    private static final int KEYCODE_BACKSPACE = 8;
    private static final int KEYCODE_RETURN = 13;
    private static final int KEYCODE_SPACE = 32;
    private Context mContext;
    private KeyboardView mInputView;
    private LatinKeyboard mCurrentKeyboard;
    private View mRootView;
    private FrameLayout mInputFrame;
    private SoftInputWindow mWindow;
    private Resources res;
    private Bitmap mBitmap;
    private Bitmap mBitmap_letter;
    private StringBuilder mComposing;
    // 判断是否是英文键盘 否--是符号键盘
    private Boolean isLetter;
    // 判断英文键盘是大写还是小写 false 小写 true 大写
    private Boolean isShift;
    private EditText mEditText;
    private String[] letter;
    private String[] number;
    private String[] symbol;
    private String[] moresymbol;
    private String[] str_letter;
    private String[] str_symbol;
    private String[] str_num;
    private boolean random;
    private boolean needHint;
    private boolean isNum;
    private boolean needKeyboardPic;
    private String jsName;
    private WebView relativeWebView;
    private View maskView;
    private Cypher cypher;
    private PasswordEditListener passwordListener;
    private PasswordShowOrHideListener mPasswordShowOrHideListener;
    private boolean realtimeCyphered;
    private String timestamp;
    private String modulus;
    private String encoding;
    private String displayChar;
    private int maxSize;
    private BSKeyboardRow currentKeyboard;
    private String keyEncryptValue;
    private Map<Integer, String> encryptValueMap;
    private boolean isFocus;

    public PasswordEditText(Context context) {
        this(context, (AttributeSet) null);
    }

    public PasswordEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCurrentKeyboard = null;
        this.mComposing = new StringBuilder();
        this.isLetter = Boolean.valueOf(true);
        this.isShift = Boolean.valueOf(false);
        this.letter = new String[]{"q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h", "j", "k", "l", "z", "x", "c", "v", "b", "n", "m"};
        this.number = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        this.symbol = new String[]{"@", "#", "$", "%", "&", "*", "(", ")", "-", "\\", "!", ";", ":", "\'", "\"", "?", "/", "|", "~", "_", "^", "[", "]", "{", "}", "<", ">", "+", "=", "`", ",", "."};
        this.moresymbol = new String[0];
        this.random = false;
        this.needHint = true;
        this.isNum = false;
        this.needKeyboardPic = false;
        //this.jsObjectIndex = "-1";
        this.jsName = UUID.randomUUID().toString();
        this.relativeWebView = null;
        this.maskView = null;
        this.cypher = CSIICypher.newInstance();
        this.passwordListener = null;
        this.mPasswordShowOrHideListener = null;
        this.realtimeCyphered = false;
        this.encoding = "UTF-8";
        this.displayChar = "●";
        this.maxSize = 0;
        this.currentKeyboard = null;
        this.keyEncryptValue = "keyaddfe4233!#";
        this.encryptValueMap = new HashMap();
        this.mContext = context;
        this.mEditText = this;
        this.res = this.mContext.getResources();
        this.mEditText.setLongClickable(false); // 禁止其他输入法
        this.mEditText.setInputType(InputType.TYPE_NULL); // 禁止其他输入法弹出
        this.mEditText.setCursorVisible(false);
        this.setOnFocusChangeListener(this);
        this.setOnClickListener(this);
    }

    public boolean isNum() {
        return this.isNum;
    }

    public void setNum(boolean isNum) {
        this.isNum = isNum;
    }

    public void setRandom(boolean random) {
        this.random = random;
    }

    public void setNeedHint(boolean hint) {
        this.needHint = hint;
        if (this.mInputView != null) {
            this.mInputView.setPreviewEnabled(this.needHint);
        }

    }

    public void setNeedKeyboardPic(boolean needpic) {
        this.needKeyboardPic = needpic;
    }

//    public void setJsObjectIndex(String jsObjectIndex) {
//        this.jsObjectIndex = jsObjectIndex;
//    }

    public String getJsName() {
        return this.jsName;
    }

    public String getPasswordName() {
        return this.getJsName();
    }

    public void setJsName(String jsName) {
        this.jsName = jsName;
    }

    public void setPasswordName(String pwdName) {
        this.setJsName(pwdName);
    }

    public void setRelativeWebView(WebView relativeWebView) {
        this.relativeWebView = relativeWebView;
    }

    public void setPasswordListener(PasswordEditListener passwordListener) {
        this.passwordListener = passwordListener;
    }

    public void setPasswordShowOrHideListener(PasswordShowOrHideListener passwordShowOrHideListener) {
        this.mPasswordShowOrHideListener = passwordShowOrHideListener;
    }

    public void setRealtimeCyphered(boolean realtimeCyphered) {
        this.realtimeCyphered = realtimeCyphered;
    }

    public PasswordEditText setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public void setModulus(String modulus) {
        this.modulus = modulus;
    }

    public void setDisplayChar(String displayChar) {
        this.displayChar = displayChar;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public String getPasswordCyphered() throws Exception {
        String res = null;
        if (this.cypher == null) {
            return null;
        } else {
            res = this.cypher.encryptWithoutRemove(this.jsName, this.modulus, this.timestamp, this.encoding, 1);
            return res;
        }
    }

    public String getPasswordLevel() {
        return this.cypher == null ? "W" : this.cypher.checkLevel(this.getJsName());
    }

    public int getPasswordLength() {
        return this.cypher.getPasswordLength(this.jsName);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.isFocus = hasFocus;
        if (hasFocus) {
            InputMethodManager inputMethodManager = (InputMethodManager) mContext
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager == null) {
                return;
            }

            inputMethodManager.hideSoftInputFromWindow(this.mEditText.getWindowToken(), 0);
            if (this.mCurrentKeyboard == null) {
                try {
                    this.createKeyBoard();
                } catch (Exception e) {
                    e.fillInStackTrace();
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(getContext(), "创建键盘失败，请稍后尝试", Toast.LENGTH_LONG).show();
                }
            }

            this.showSoftInputWindow();
        } else {
            this.hideSoftInputWindow();
        }

    }

    // EditText点击事件 点击输入框弹出软键盘
    public void onClick(View v) {
        if (this.mCurrentKeyboard == null) {
            this.createKeyBoard();
        }

        this.showSoftInputWindow();
    }

    private void createKeyBoard() {
        // 乱序之后的字符串数组
        this.str_letter = this.shortchar(this.letter);
        // 乱序之后的符号数组
        this.str_symbol = this.shortchar(this.symbol);
        this.str_num = this.shortchar(this.number);
        // 实例化keyboardview
        this.mInputView = (KeyboardView) ((LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.input, null);
        this.mInputView.setPreviewEnabled(this.needHint);
        if (this.isNum) {
            // 字母键
            this.mCurrentKeyboard = new LatinKeyboard(this.mContext, R.xml.qwerty_number);
            this.currentKeyboard = new PasswordEditText.num_row(this.res, this.mCurrentKeyboard, this.getResources().getXml(R.xml.qwerty_number));
        } else {
            // 实例化keyboard
            // 小写英文键盘
            this.mCurrentKeyboard = new LatinKeyboard(this.mContext, R.xml.qwerty);
            // 默认为小写英文字母键盘,对键盘的键进行乱序处理
            this.currentKeyboard = new PasswordEditText.letter_row(this.res, this.mCurrentKeyboard, this.getResources().getXml(R.xml.qwerty));
        }

        this.mInputView.setOnKeyboardActionListener(this);
        this.mInputView.setOnLongClickListener(v -> true);

        // 添加keyboard
        mInputView.setKeyboard(mCurrentKeyboard);
        mRootView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.input_method, null);
        mInputFrame = (FrameLayout) mRootView.findViewById(android.R.id.inputArea);
        this.mInputFrame.addView(this.mInputView, new FrameLayout.LayoutParams(-1, -2));
        mInputFrame.setVisibility(View.VISIBLE);
        this.mWindow = new SoftInputWindow(this.mContext, R.layout.input_method);
        this.mWindow.setContentView(this.mRootView);
        this.mWindow.getWindow().setLayout(-1, -2);
    }

    // 显示软键盘
    public void showSoftInputWindow() {
        if (!this.isShowingKeyBoard()) {
            try {
                this.mWindow.show();
                if (this.mPasswordShowOrHideListener != null) {
                    this.mPasswordShowOrHideListener.onKeyboardShow(mCurrentKeyboard.getHeight());
                }
            } catch (Exception e) {
                Log.d(TAG, "showSoftInputWindow err", e);
                Toast.makeText(getContext(), "当前视图需要重构，请关闭键盘再尝试重新操作", Toast.LENGTH_LONG);
            }
        }
    }

    // 关闭软键盘
    public void hideSoftInputWindow() {
        if (this.relativeWebView == null) {
            if (this.isShowingKeyBoard()) {

                if (this.currentKeyboard != null) {
                    if (this.mPasswordShowOrHideListener != null) {
                        this.mPasswordShowOrHideListener.onKeyboardHide(mCurrentKeyboard.getHeight());
                    }

                    this.currentKeyboard.releaseResources();
                    this.currentKeyboard = null;
                }

                this.mWindow.dismiss();
                this.mCurrentKeyboard = null;
            }

        } else {
            if (this.isShowingKeyBoard()) {
                if (this.currentKeyboard != null) {
                    if (this.mPasswordShowOrHideListener != null) {
                        this.mPasswordShowOrHideListener.onKeyboardHide(mCurrentKeyboard.getHeight());
                    }

                    this.currentKeyboard.releaseResources();
                    this.currentKeyboard = null;
                }

                this.mWindow.dismiss();
            }

            this.relativeWebView.setFocusable(true);
            this.relativeWebView.setFocusableInTouchMode(true);
            this.relativeWebView.requestFocus();
            ViewGroup viewgroup = (ViewGroup) relativeWebView.getParent();
            if (this.maskView != null && viewgroup != null) {
                viewgroup.removeView(maskView);
            }
//            int maskIndex = 1;
//
//            for(int mask = 0; mask < viewgroup.getChildCount(); ++mask) {
//                if(viewgroup.getChildAt(mask) instanceof MXWebView) {
//                    maskIndex = mask + 1;
//                }
//            }
//
//            View var4 = null;
//            if(viewgroup != null) {
//                var4 = viewgroup.getChildAt(maskIndex);
//            }
//
//            if(var4 != null) {
//                viewgroup.removeView(var4);
//            }

            if (viewgroup != null) {
                viewgroup.removeView(this);
            }

        }
    }

    // 键盘当前状态 是显示还是隐藏
    public boolean isShowingKeyBoard() {
        return this.mWindow != null && this.mWindow.isShowing();
    }

    public void swipeUp() {
    }

    public void swipeRight() {
    }

    public void swipeLeft() {
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void onText(CharSequence text) {
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onPress(int primaryCode) {
    }

    public void closeKeyboard(boolean notifyListener) {
        try {
            this.cypher.putChar(this.jsName, CHAR_OK);
            if (notifyListener && this.passwordListener != null) {
                String level = this.cypher.checkLevel(this.jsName);
                this.passwordListener.onKeyPressed(PasswordEditListener.TAP_OK, level);
            }

            this.hideSoftInputWindow();
        } catch (Exception e) {
            Log.e(TAG, "关闭键盘出错", e);
        }
    }

    public void closeKeyboard() {
        this.cypher.putChar(this.jsName, CHAR_OK);
        // TODO
//        if(this.relativeWebView != null && !"-1".equals(this.jsObjectIndex) && !"".equals(this.jsName)) {
//            String level = this.cypher.checkLevel(this.jsName);
//            String js = "javascript:mobileXObj.jsFireSpecifiedEvent(\'tapok\', \'" + level + "\', \'" + this.jsObjectIndex + "\')";
//            this.relativeWebView.loadUrl(js);
//        }

        if (this.passwordListener != null) {
            String level = this.cypher.checkLevel(this.jsName);
            this.passwordListener.onKeyPressed(PasswordEditListener.TAP_OK, level);
        }

        this.hideSoftInputWindow();
    }

    private void changeInputText(int cnt) {
        if (cnt != 0) {
            Editable txt = this.getText();
            int i;
            if (cnt < 0) {
                if (txt.length() != 0) {
                    for (i = cnt; i < 0 && txt.length() > 0; ++i) {
                        txt = txt.delete(txt.length() - 1, txt.length());
                    }

                    if (txt.length() == 0) {
                        this.setText("");
                    } else {
                        this.setText(txt.toString());
                    }

                }
            } else {
                if (cnt > 0) {
                    if (this.maxSize <= 0) {
                        for (i = 0; i < cnt; ++i) {
                            txt = txt.append(this.displayChar);
                        }
                    } else {
                        if (txt.length() == this.maxSize) {
                            return;
                        }

                        for (i = 0; i < cnt && txt.length() < this.maxSize; ++i) {
                            txt = txt.append(this.displayChar);
                        }
                    }

                    this.setText(txt.toString());
                }

            }
        }
    }

    // 点击键盘按键出发事件
    public void onKey(int primaryCode, int[] keyCodes) {
        if (primaryCode != KEYCODE_RETURN && primaryCode != Keyboard.KEYCODE_CANCEL) {
            if (primaryCode == KEYBOARD_SYMBOL) {
                // 符号键盘和字符键盘切换键
                this.mCurrentKeyboard = null;
                if (this.currentKeyboard != null) {
                    this.currentKeyboard.releaseResources();
                    this.currentKeyboard = null;
                }

                System.gc();
                this.mCurrentKeyboard = new LatinKeyboard(this.mContext, R.xml.qwerty_symbol);
                this.currentKeyboard = new PasswordEditText.symbol_row(this.res, this.mCurrentKeyboard, this.getResources().getXml(R.xml.qwerty_symbol));
                this.mInputView.setKeyboard(this.mCurrentKeyboard);
                this.isLetter = Boolean.FALSE;
            } else if (primaryCode == KEYBOARD_NUM) {
                this.mCurrentKeyboard = null;
                if (this.currentKeyboard != null) {
                    this.currentKeyboard.releaseResources();
                    this.currentKeyboard = null;
                }

                System.gc();
                this.mCurrentKeyboard = new LatinKeyboard(this.mContext, R.xml.qwerty_number);
                this.currentKeyboard = new PasswordEditText.num_row(this.res, this.mCurrentKeyboard, this.getResources().getXml(R.xml.qwerty_number));
                this.mInputView.setKeyboard(this.mCurrentKeyboard);
                this.isLetter = Boolean.FALSE;
            } else if (primaryCode == KEYBOARD_LETTER) {
                this.mCurrentKeyboard = null;
                if (this.currentKeyboard != null) {
                    this.currentKeyboard.releaseResources();
                    this.currentKeyboard = null;
                }

                System.gc();
                this.mCurrentKeyboard = new LatinKeyboard(this.mContext, R.xml.qwerty);
                this.currentKeyboard = new PasswordEditText.letter_row(this.res, this.mCurrentKeyboard, this.getResources().getXml(R.xml.qwerty));
                this.mInputView.setKeyboard(this.mCurrentKeyboard);
                this.isLetter = Boolean.TRUE;
            } else if (primaryCode == KEYCODE_SHIFT) {
                this.mCurrentKeyboard = null;
                if (this.currentKeyboard != null) {
                    this.currentKeyboard.releaseResources();
                    this.currentKeyboard = null;
                }

                System.gc();
                if (!this.isShift.booleanValue()) {
                    this.mCurrentKeyboard = new LatinKeyboard(this.mContext, R.xml.qwerty);
                    this.currentKeyboard = new PasswordEditText.letter_shift_row(this.res, this.mCurrentKeyboard, this.getResources().getXml(R.xml.qwerty));
                    this.isShift = Boolean.TRUE;
                } else {
                    this.mCurrentKeyboard = new LatinKeyboard(this.mContext, R.xml.qwerty);
                    this.currentKeyboard = new PasswordEditText.letter_row(this.res, this.mCurrentKeyboard, this.getResources().getXml(R.xml.qwerty));
                    this.isShift = Boolean.FALSE;
                }

                this.mCurrentKeyboard.setShifted(this.isShift.booleanValue());
                this.mInputView.setKeyboard(this.mCurrentKeyboard);
                this.handleShift();
            } else {
                String outString;
                if (primaryCode == KEYCODE_BACKSPACE) {
                    // 点击删除
                    this.cypher.putChar(this.jsName, CHAR_DELETE);
                    this.changeInputText(-1);
                    // TODO
//                    if(this.relativeWebView != null && !"-1".equals(this.jsObjectIndex) && !"".equals(this.jsName)) {
//                        outString = "javascript:mobileXObj.jsFireSpecifiedEvent(\'tapdelete\', \'\', \'" + this.jsObjectIndex + "\')";
//                        this.relativeWebView.loadUrl(outString);
//                    }

                    if (this.passwordListener != null) {
                        this.passwordListener.onKeyPressed(PasswordEditListener.TAP_DELETE, null);
                    }
                } else {
                    // 点击软键盘按键 内容
                    outString = (String) this.encryptValueMap.get(primaryCode);
                    if (outString == null) {
                        if (this.mInputView.isShifted() && primaryCode > 96 && primaryCode < 123) {
                            outString = Character.toString((char) (primaryCode - 32));
                        } else {
                            outString = Character.toString((char) primaryCode);
                        }
                    }

                    if (this.maxSize > 0 && this.getText().length() < this.maxSize || this.maxSize <= 0) {
                        this.cypher.putChar(this.jsName, outString);
                        this.changeInputText(1);
                        // TODO
//                        if(this.relativeWebView != null && !"-1".equals(this.jsObjectIndex) && !"".equals(this.jsName)) {
//                            String js = "javascript:mobileXObj.jsFireSpecifiedEvent(\'tapcharacter\', \'●\', \'" + this.jsObjectIndex + "\')";
//                            this.relativeWebView.loadUrl(js);
//                        }

                        if (this.passwordListener != null) {
                            this.passwordListener.onKeyPressed(PasswordEditListener.TAP_CHARACTER, null);
                        }
                    }
                }
            }
        } else {
            // 完成按钮 关闭软键盘
            this.closeKeyboard();
        }

        this.mInputView.playSoundEffect(0);
    }

    // 判断英文大小写切换是否开启
    private void handleShift() {
        if (this.mInputView != null) {
            if (this.isLetter.booleanValue()) {
                this.mInputView.setShifted(this.isShift.booleanValue());
            }

        }
    }

    public StringBuilder getmComposing() {
        return this.mComposing;
    }

    public void setmComposing(StringBuilder mComposing) {
        this.mComposing = mComposing;
    }

    private boolean needKeyboardBitmap() {
        return this.needKeyboardPic;
    }

    // 相应back键事件
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (this.isShowingKeyBoard()) {
            this.hideSoftInputWindow();
            return true;
        } else {
            return this.isFocused()
                    && event.getKeyCode() != KeyEvent.KEYCODE_BACK
                    && event.getKeyCode() != KeyEvent.KEYCODE_MENU ? true : super.dispatchKeyEvent(event);
        }
    }

    // 获取英文字符相应的drawble 小写
    private Drawable getLetterDrawble(String str) {
        this.mBitmap = BitmapFactory.decodeResource(this.res, R.drawable.keybg);
        if (str.equals("a")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_a);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("b")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_b);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("c")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_c);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("d")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_d);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("e")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_e);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("f")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_f);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("g")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_g);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("h")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_h);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("i")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_i);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("j")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_j);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("k")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_k);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("l")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_l);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("m")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_m);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("n")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_n);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("o")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_o);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("p")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_p);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("q")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_q);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("r")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_r);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("s")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_s);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("t")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_t);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("u")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_u);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("v")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_v);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("w")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_w);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("x")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_x);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("y")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_y);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("z")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_z);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else {
            return null;
        }
    }

    // 获取英文字符相应的drawble 小写
    private Drawable getLetterDrawble(Key key, String str) {
        this.mBitmap = BitmapFactory.decodeResource(this.res, R.drawable.keybg);
        if (str.equals("a")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_a);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("b")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_b);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("c")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_c);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("d")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_d);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("e")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_e);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("f")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_f);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("g")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_g);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("h")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_h);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("i")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_i);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("j")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_j);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("k")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_k);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("l")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_l);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("m")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_m);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("n")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_n);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("o")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_o);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("p")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_p);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("q")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_q);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("r")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_r);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("s")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_s);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("t")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_t);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("u")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_u);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("v")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_v);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("w")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_w);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("x")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_x);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("y")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_y);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("z")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_z);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else {
            return null;
        }
    }

    // 获取英文字符相应的drawble 小写
    private Drawable getIconPreviewLetterDrawble(String str) {
        if (str.equals("a")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_a);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("b")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_b);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("c")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_c);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("d")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_d);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("e")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_e);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("f")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_f);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("g")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_g);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("h")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_h);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("i")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_i);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("j")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_j);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("k")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_k);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("l")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_l);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("m")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_m);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("n")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_n);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("o")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_o);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("p")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_p);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("q")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_q);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("r")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_r);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("s")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_s);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("t")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_t);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("u")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_u);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("v")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_v);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("w")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_w);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("x")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_x);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("y")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_y);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("z")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_z);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else {
            return null;
        }
    }

    // 获取英文字符相应的drawble 大写
    private Drawable getLetterShiftDrawble(String str) {
        this.mBitmap = BitmapFactory.decodeResource(this.res, R.drawable.keybg);
        if (str.equals("a")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_a);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("b")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_b);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("c")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_c);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("d")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_d);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("e")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_e);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("f")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_f);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("g")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_g);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("h")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_h);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("i")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_i);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("j")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_j);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("k")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_k);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("l")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_l);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("m")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_m);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("n")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_n);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("o")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_o);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("p")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_p);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("q")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_q);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("r")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_r);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("s")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_s);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("t")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_t);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("u")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_u);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("v")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_v);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("w")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_w);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("x")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_x);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("y")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_y);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("z")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_z);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else {
            return null;
        }
    }

    // 获取英文字符相应的drawble 大写
    private Drawable getLetterShiftDrawble(Key key, String str) {
        this.mBitmap = BitmapFactory.decodeResource(this.res, R.drawable.keybg);
        if (str.equals("a")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_a);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("b")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_b);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("c")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_c);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("d")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_d);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("e")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_e);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("f")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_f);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("g")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_g);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("h")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_h);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("i")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_i);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("j")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_j);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("k")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_k);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("l")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_l);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("m")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_m);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("n")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_n);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("o")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_o);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("p")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_p);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("q")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_q);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("r")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_r);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("s")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_s);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("t")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_t);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("u")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_u);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("v")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_v);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("w")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_w);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("x")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_x);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("y")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_y);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("z")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_z);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else {
            return null;
        }
    }

    // 获取英文字符相应的drawble 大写
    private Drawable getIconPreviewShiftDrawble(String str) {
        if (str.equals("a")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_a);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("b")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_b);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("c")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_c);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("d")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_d);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("e")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_e);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("f")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_f);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("g")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_g);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("h")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_h);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("i")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_i);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("j")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_j);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("k")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_k);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("l")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_l);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("m")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_m);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("n")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_n);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("o")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_o);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("p")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_p);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("q")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_q);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("r")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_r);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("s")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_s);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("t")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_t);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("u")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_u);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("v")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_v);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("w")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_w);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("x")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_x);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("y")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_y);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("z")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.letter_shift_z);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else {
            return null;
        }
    }

    // 获取英文字符相应的codes
    private int[] getLetterCodes(String str) {
        int[] letter_int;
        if (str.equals("a")) {
            letter_int = new int[]{97};
            return letter_int;
        } else if (str.equals("b")) {
            letter_int = new int[]{98};
            return letter_int;
        } else if (str.equals("c")) {
            letter_int = new int[]{99};
            return letter_int;
        } else if (str.equals("d")) {
            letter_int = new int[]{100};
            return letter_int;
        } else if (str.equals("e")) {
            letter_int = new int[]{101};
            return letter_int;
        } else if (str.equals("f")) {
            letter_int = new int[]{102};
            return letter_int;
        } else if (str.equals("g")) {
            letter_int = new int[]{103};
            return letter_int;
        } else if (str.equals("h")) {
            letter_int = new int[]{104};
            return letter_int;
        } else if (str.equals("i")) {
            letter_int = new int[]{105};
            return letter_int;
        } else if (str.equals("j")) {
            letter_int = new int[]{106};
            return letter_int;
        } else if (str.equals("k")) {
            letter_int = new int[]{107};
            return letter_int;
        } else if (str.equals("l")) {
            letter_int = new int[]{108};
            return letter_int;
        } else if (str.equals("m")) {
            letter_int = new int[]{109};
            return letter_int;
        } else if (str.equals("n")) {
            letter_int = new int[]{110};
            return letter_int;
        } else if (str.equals("o")) {
            letter_int = new int[]{111};
            return letter_int;
        } else if (str.equals("p")) {
            letter_int = new int[]{112};
            return letter_int;
        } else if (str.equals("q")) {
            letter_int = new int[]{113};
            return letter_int;
        } else if (str.equals("r")) {
            letter_int = new int[]{114};
            return letter_int;
        } else if (str.equals("s")) {
            letter_int = new int[]{115};
            return letter_int;
        } else if (str.equals("t")) {
            letter_int = new int[]{116};
            return letter_int;
        } else if (str.equals("u")) {
            letter_int = new int[]{117};
            return letter_int;
        } else if (str.equals("v")) {
            letter_int = new int[]{118};
            return letter_int;
        } else if (str.equals("w")) {
            letter_int = new int[]{119};
            return letter_int;
        } else if (str.equals("x")) {
            letter_int = new int[]{120};
            return letter_int;
        } else if (str.equals("y")) {
            letter_int = new int[]{121};
            return letter_int;
        } else if (str.equals("z")) {
            letter_int = new int[]{122};
            return letter_int;
        } else if (str.equals("A")) {
            letter_int = new int[]{65};
            return letter_int;
        } else if (str.equals("B")) {
            letter_int = new int[]{66};
            return letter_int;
        } else if (str.equals("C")) {
            letter_int = new int[]{67};
            return letter_int;
        } else if (str.equals("D")) {
            letter_int = new int[]{68};
            return letter_int;
        } else if (str.equals("E")) {
            letter_int = new int[]{69};
            return letter_int;
        } else if (str.equals("F")) {
            letter_int = new int[]{70};
            return letter_int;
        } else if (str.equals("G")) {
            letter_int = new int[]{71};
            return letter_int;
        } else if (str.equals("H")) {
            letter_int = new int[]{72};
            return letter_int;
        } else if (str.equals("I")) {
            letter_int = new int[]{73};
            return letter_int;
        } else if (str.equals("J")) {
            letter_int = new int[]{74};
            return letter_int;
        } else if (str.equals("K")) {
            letter_int = new int[]{75};
            return letter_int;
        } else if (str.equals("L")) {
            letter_int = new int[]{76};
            return letter_int;
        } else if (str.equals("M")) {
            letter_int = new int[]{77};
            return letter_int;
        } else if (str.equals("N")) {
            letter_int = new int[]{78};
            return letter_int;
        } else if (str.equals("O")) {
            letter_int = new int[]{79};
            return letter_int;
        } else if (str.equals("P")) {
            letter_int = new int[]{80};
            return letter_int;
        } else if (str.equals("Q")) {
            letter_int = new int[]{81};
            return letter_int;
        } else if (str.equals("R")) {
            letter_int = new int[]{82};
            return letter_int;
        } else if (str.equals("S")) {
            letter_int = new int[]{83};
            return letter_int;
        } else if (str.equals("T")) {
            letter_int = new int[]{84};
            return letter_int;
        } else if (str.equals("U")) {
            letter_int = new int[]{85};
            return letter_int;
        } else if (str.equals("V")) {
            letter_int = new int[]{86};
            return letter_int;
        } else if (str.equals("W")) {
            letter_int = new int[]{87};
            return letter_int;
        } else if (str.equals("X")) {
            letter_int = new int[]{88};
            return letter_int;
        } else if (str.equals("Y")) {
            letter_int = new int[]{89};
            return letter_int;
        } else if (str.equals("Z")) {
            letter_int = new int[]{90};
            return letter_int;
        } else {
            return null;
        }
    }

    private int[] getSymbolCodesEncrypt(String str) {
        if (str != null && !str.equals("")) {
            try {
                Map e = EncryptUtils.Encrypt3DES4Map(str, this.keyEncryptValue, EncryptUtils.randomIVBytes());
                int intValue = ((Integer) e.get("intValue")).intValue();
                int[] retIntArr = new int[]{intValue};
                this.encryptValueMap.put(Integer.valueOf(intValue), str);
                return retIntArr;
            } catch (Exception var5) {
                return null;
            }
        } else {
            return null;
        }
    }

    // 获取英文字符相应的codes
    private int[] getSymbolCodes(String str) {
        int[] letter_int;
        if (str.equals("0")) {
            letter_int = new int[]{48};
            return letter_int;
        } else if (str.equals("1")) {
            letter_int = new int[]{49};
            return letter_int;
        } else if (str.equals("2")) {
            letter_int = new int[]{50};
            return letter_int;
        } else if (str.equals("3")) {
            letter_int = new int[]{51};
            return letter_int;
        } else if (str.equals("4")) {
            letter_int = new int[]{52};
            return letter_int;
        } else if (str.equals("5")) {
            letter_int = new int[]{53};
            return letter_int;
        } else if (str.equals("6")) {
            letter_int = new int[]{54};
            return letter_int;
        } else if (str.equals("7")) {
            letter_int = new int[]{55};
            return letter_int;
        } else if (str.equals("8")) {
            letter_int = new int[]{56};
            return letter_int;
        } else if (str.equals("9")) {
            letter_int = new int[]{57};
            return letter_int;
        } else if (str.equals("#")) {
            letter_int = new int[]{35};
            return letter_int;
        } else if (str.equals("@")) {
            letter_int = new int[]{64};
            return letter_int;
        } else if (str.equals("$")) {
            letter_int = new int[]{36};
            return letter_int;
        } else if (str.equals("%")) {
            letter_int = new int[]{37};
            return letter_int;
        } else if (str.equals("&")) {
            letter_int = new int[]{38};
            return letter_int;
        } else if (str.equals("*")) {
            letter_int = new int[]{42};
            return letter_int;
        } else if (str.equals("-")) {
            letter_int = new int[]{45};
            return letter_int;
        } else if (str.equals("+")) {
            letter_int = new int[]{43};
            return letter_int;
        } else if (str.equals("(")) {
            letter_int = new int[]{40};
            return letter_int;
        } else if (str.equals(")")) {
            letter_int = new int[]{41};
            return letter_int;
        } else if (str.equals("!")) {
            letter_int = new int[]{33};
            return letter_int;
        } else if (str.equals("\"")) {
            letter_int = new int[]{34};
            return letter_int;
        } else if (str.equals("\'")) {
            letter_int = new int[]{39};
            return letter_int;
        } else if (str.equals(":")) {
            letter_int = new int[]{58};
            return letter_int;
        } else if (str.equals(";")) {
            letter_int = new int[]{59};
            return letter_int;
        } else if (str.equals("/")) {
            letter_int = new int[]{47};
            return letter_int;
        } else if (str.equals("_")) {
            letter_int = new int[]{95};
            return letter_int;
        } else if (str.equals("[")) {
            letter_int = new int[]{91};
            return letter_int;
        } else if (str.equals("\\")) {
            letter_int = new int[]{92};
            return letter_int;
        } else if (str.equals("]")) {
            letter_int = new int[]{93};
            return letter_int;
        } else if (str.equals("{")) {
            letter_int = new int[]{123};
            return letter_int;
        } else if (str.equals("}")) {
            letter_int = new int[]{125};
            return letter_int;
        } else if (str.equals("<")) {
            letter_int = new int[]{60};
            return letter_int;
        } else if (str.equals(">")) {
            letter_int = new int[]{62};
            return letter_int;
        } else if (str.equals("^")) {
            letter_int = new int[]{94};
            return letter_int;
        } else if (str.equals("~")) {
            letter_int = new int[]{126};
            return letter_int;
        } else if (str.equals("`")) {
            letter_int = new int[]{96};
            return letter_int;
        } else if (str.equals("|")) {
            letter_int = new int[]{124};
            return letter_int;
        } else if (str.equals("?")) {
            letter_int = new int[]{63};
            return letter_int;
        } else if (str.equals("=")) {
            letter_int = new int[]{61};
            return letter_int;
        } else if (str.equals(".")) {
            letter_int = new int[]{46};
            return letter_int;
        } else if (str.equals(",")) {
            letter_int = new int[]{44};
            return letter_int;
        } else {
            Log.i(TAG, "in getSymbolCodes, str: " + str + " but return null");
            return null;
        }
    }

    public String[] shortchar(String[] str) {
        if (!this.random) {
            return (String[]) str.clone();
        } else {
            String[] tmp = (String[]) str.clone();
            Random r = new Random();
            int a = str.length;
            String[] strarray = new String[a];
            boolean index = false;

            int i;
            for (int ch = 0; ch < a; ++ch) {
                int var9 = r.nextInt(tmp.length - ch);
                strarray[ch] = tmp[var9];

                for (i = var9; i < tmp.length - ch - 1; ++i) {
                    tmp[i] = tmp[i + 1];
                }
            }

            String[] var10 = new String[a];

            for (i = 0; i < strarray.length; ++i) {
                var10[i] = strarray[i];
            }

            return var10;
        }
    }

    private Drawable getIconPreviewSymbolDrawble(String str) {
        if (str.equals("0")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_0);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("1")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_1);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("2")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_2);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("3")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_3);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("4")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_4);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("5")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_5);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("6")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_6);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("7")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_7);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("8")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_8);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("9")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_9);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("#")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_02);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("@")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_01);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("$")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_03);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("%")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_04);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("&")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_05);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("*")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_06);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("-")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_07);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("+")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_08);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("(")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_09);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals(")")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_10);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("!")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_11);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("\"")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_12);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("\'")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_13);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals(":")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_14);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals(";")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_15);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("/")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_16);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("_")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_17);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("[")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_18);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("]")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_19);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("{")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_20);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("}")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_21);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("<")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_22);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals(">")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_23);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("^")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_24);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("~")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_25);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("`")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_26);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("|")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_27);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("?")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_28);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("=")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_29);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else if (str.equals("\\")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_30);
            return this.getIconPreviewDrawble(this.mBitmap_letter);
        } else {
            return null;
        }
    }

    private Drawable getSymbolDrawble(String str) {
        this.mBitmap = BitmapFactory.decodeResource(this.res, R.drawable.keybg);
        if (str.equals("0")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_0);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("1")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_1);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("2")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_2);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("3")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_3);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("4")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_4);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("5")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_5);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("6")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_6);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("7")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_7);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("8")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_8);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("9")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_9);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("#")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_02);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("@")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_01);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("$")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_03);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("%")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_04);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("&")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_05);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("*")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_06);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("-")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_07);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("+")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_08);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("(")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_09);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals(")")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_10);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("!")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_11);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("\"")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_12);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("\'")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_13);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals(":")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_14);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals(";")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_15);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("/")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_16);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("_")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_17);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("[")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_18);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("]")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_19);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("{")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_20);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("}")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_21);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("<")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_22);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals(">")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_23);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("^")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_24);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("~")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_25);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("`")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_26);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("|")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_27);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("?")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_28);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("=")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_29);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("\\")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_30);
            return this.getDrawable(this.mBitmap, this.mBitmap_letter);
        } else {
            return null;
        }
    }

    private Drawable getSymbolDrawble(Key key, String str) {
        this.mBitmap = BitmapFactory.decodeResource(this.res, R.drawable.keybg);
        if (str.equals("0")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_0);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("1")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_1);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("2")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_2);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("3")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_3);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("4")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_4);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("5")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_5);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("6")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_6);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("7")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_7);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("8")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_8);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("9")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_9);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("#")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_02);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("@")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_01);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("$")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_03);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("%")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_04);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("&")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_05);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("*")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_06);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("-")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_07);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("+")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_08);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("(")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_09);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals(")")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_10);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("!")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_11);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("\"")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_12);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("\'")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_13);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals(":")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_14);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals(";")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_15);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("/")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_16);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("_")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_17);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("[")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_18);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("]")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_19);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("{")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_20);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("}")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_21);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("<")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_22);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals(">")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_23);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("^")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_24);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("~")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_25);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("`")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_26);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("|")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_27);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("?")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_28);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("=")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_29);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else if (str.equals("\\")) {
            this.mBitmap_letter = BitmapFactory.decodeResource(this.res, R.drawable.symbol_30);
            return this.getDrawable(key, this.mBitmap, this.mBitmap_letter);
        } else {
            return null;
        }
    }

    private Drawable getDrawable(Key key, Bitmap mBitmap, Bitmap mBitmap2) {
        double xscale = (double) (key.width - 2) * 1.0D / (double) mBitmap.getWidth();
        double yscale = (double) (key.height - 2) * 1.0D / (double) mBitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale((float) xscale, (float) yscale);
        Bitmap newBmp = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(this.res, newBmp);
        Bitmap newbgbmp = bitmapDrawable.getBitmap();
        Paint mPaint = new Paint();
        Bitmap newBitmap = Bitmap.createBitmap(key.width - 2, key.height - 2, Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(newbgbmp, 1.0F, 1.0F, mPaint);
        canvas.drawBitmap(mBitmap2, (float) ((newbgbmp.getWidth() - mBitmap2.getWidth()) / 2), (float) ((newbgbmp.getHeight() - mBitmap2.getHeight()) / 2), mPaint);
        BitmapDrawable newbitmapDrawable = new BitmapDrawable(this.res, newBitmap);
        return newbitmapDrawable;
    }

    private Drawable getDrawable(Bitmap mBitmap, Bitmap mBitmap2) {
        Paint mPaint = new Paint();
        Bitmap newBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(mBitmap, 0.0F, 0.0F, mPaint);
        canvas.drawBitmap(mBitmap2, (float) ((mBitmap.getWidth() - mBitmap2.getWidth()) / 2), (float) ((mBitmap.getHeight() - mBitmap2.getHeight()) / 2), mPaint);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(this.res, newBitmap);
        return bitmapDrawable;
    }

    private Drawable getIconPreviewDrawble(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(2.0F, 2.0F);
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(this.res, newBmp);
        return bitmapDrawable;
    }

    private Drawable getDrawable(Key key, Drawable drawable) {
        if (!(drawable instanceof BitmapDrawable)) {
            return null;
        } else {
            BitmapDrawable bitmapdrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapdrawable.getBitmap();
            System.out.println("key w:" + key.width + "------- key h:" + key.height);
            System.out.println("img w:" + bitmap.getWidth() + "------- img h:" + bitmap.getHeight());
            float xscale = (float) (key.width / bitmap.getWidth());
            float yscale = (float) (key.height / bitmap.getHeight());
            Matrix matrix = new Matrix();
            matrix.postScale(xscale, yscale);
            Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(this.res, newBmp);
            return bitmapDrawable;
        }
    }

    public void clearText() {
        this.cypher.clearChar(this.jsName);
        this.setText("");
    }

    public String getPasswordCypheredCommon() throws Exception {
        String res = null;
        if (this.cypher == null) {
            return null;
        } else {
            res = this.cypher.encryptCommon(this.jsName, this.modulus);
            return res;
        }
    }

    public String getPasswordCypheredCommon(String plain) throws Exception {
        String res = null;
        if (this.cypher == null) {
            return null;
        } else {
            this.cypher.clearChar(this.jsName);
            this.cypher.putChar(this.jsName, plain);
            res = this.cypher.encryptCommon(this.jsName, this.modulus);
            this.cypher.clearChar(this.jsName);
            return res;
        }
    }

    public PasswordEditText setMaskView(@NonNull View maskView) {
        this.maskView = maskView;
        return this;
    }

    // 操作keyboard的row 可以重置key的内容 (英文字符 小写)
    class letter_row extends Keyboard.Row implements BSKeyboardRow {
        private ArrayList<Bitmap> bitmaps = new ArrayList();

        public letter_row(Resources arg0, Keyboard arg1, XmlResourceParser arg2) {
            super(arg0, arg1, arg2);
            PasswordEditText.this.encryptValueMap.clear();
            int count1 = 0;

            for (int i = 0; i < arg1.getKeys().size(); ++i) {
                ((Key) arg1.getKeys().get(i)).iconPreview = null;
//                if(((Key)arg1.getKeys().get(i)).codes[0] == 14 || ((Key)arg1.getKeys().get(i)).codes[0] == -1) {
//                    System.out.println();
//                }

                int[] letter_int;
                if (((Key) arg1.getKeys().get(i)).codes[0] == KEYCODE_SHIFT) {
                    ((Key) arg1.getKeys().get(i)).label = "shift";
                    ((Key) arg1.getKeys().get(i)).sticky = true;
                    ((Key) arg1.getKeys().get(i)).modifier = false;
                    letter_int = new int[]{KEYCODE_SHIFT};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] == 8) {
                    ((Key) arg1.getKeys().get(i)).label = "删除";
                    letter_int = new int[]{8};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] == -102) {
                    ((Key) arg1.getKeys().get(i)).label = "123";
                    letter_int = new int[]{-102};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] == -103) {
                    ((Key) arg1.getKeys().get(i)).label = "符号";
                    letter_int = new int[]{-103};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] == 32) {
                    ((Key) arg1.getKeys().get(i)).label = "空格";
                    letter_int = new int[]{32};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] == 13) {
                    ((Key) arg1.getKeys().get(i)).label = "完成";
                    letter_int = new int[]{13};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] == 44) {
                    ((Key) arg1.getKeys().get(i)).label = ",";
                    letter_int = new int[]{44};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] == 46) {
                    ((Key) arg1.getKeys().get(i)).label = ".";
                    letter_int = new int[]{46};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] != 0 && count1 < PasswordEditText.this.letter.length) {
                    ((Key) arg1.getKeys().get(i)).label = PasswordEditText.this.str_letter[count1];
                    if (count1 == 18) {
                        System.out.println();
                    }

                    ((Key) arg1.getKeys().get(i)).codes = PasswordEditText.this.getSymbolCodesEncrypt(PasswordEditText.this.str_letter[count1]);
                    ++count1;
                }
            }

        }

        public void releaseResources() {
            for (int i = 0; i < this.bitmaps.size(); ++i) {
                Bitmap bm = (Bitmap) this.bitmaps.get(i);
                if (!bm.isRecycled()) {
                    bm.recycle();
                }
            }

            this.bitmaps.clear();
            this.bitmaps = null;
            System.gc();
        }
    }

    // 操作keyboard的row 可以重置key的内容 (英文字符 大写)
    class letter_shift_row extends Keyboard.Row implements BSKeyboardRow {
        private ArrayList<Bitmap> bitmaps = new ArrayList();

        public letter_shift_row(Resources arg0, Keyboard arg1, XmlResourceParser arg2) {
            super(arg0, arg1, arg2);
            PasswordEditText.this.encryptValueMap.clear();
            int count1 = 0;

            for (int i = 0; i < arg1.getKeys().size(); ++i) {
                arg1.getKeys().get(i).iconPreview = null;
                int[] letter_int;
                if (arg1.getKeys().get(i).codes[0] == KEYCODE_SHIFT) {
                    // 大小写切换
                    arg1.getKeys().get(i).label = "shift";
                    arg1.getKeys().get(i).sticky = true;
                    arg1.getKeys().get(i).modifier = true;
                    letter_int = new int[]{KEYCODE_SHIFT};
                    arg1.getKeys().get(i).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] == KEYCODE_BACKSPACE) {
                    ((Key) arg1.getKeys().get(i)).label = "删除";
                    letter_int = new int[]{KEYCODE_BACKSPACE};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] == -102) {
                    ((Key) arg1.getKeys().get(i)).label = "123";
                    letter_int = new int[]{-102};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] == -103) {
                    ((Key) arg1.getKeys().get(i)).label = "符号";
                    letter_int = new int[]{-103};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] == 32) {
                    ((Key) arg1.getKeys().get(i)).label = "空格";
                    letter_int = new int[]{32};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] == 13) {
                    ((Key) arg1.getKeys().get(i)).label = "完成";
                    letter_int = new int[]{13};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] == 44) {
                    ((Key) arg1.getKeys().get(i)).label = ",";
                    letter_int = new int[]{44};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] == 46) {
                    ((Key) arg1.getKeys().get(i)).label = ".";
                    letter_int = new int[]{46};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] != 0 && count1 < PasswordEditText.this.letter.length) {
                    ((Key) arg1.getKeys().get(i)).label = PasswordEditText.this.str_letter[count1].toUpperCase();
                    ((Key) arg1.getKeys().get(i)).codes = PasswordEditText.this.getSymbolCodesEncrypt(PasswordEditText.this.str_letter[count1].toUpperCase());
                    ++count1;
                }
            }

        }

        public void releaseResources() {
            for (int i = 0; i < this.bitmaps.size(); ++i) {
                Bitmap bm = (Bitmap) this.bitmaps.get(i);
                if (!bm.isRecycled()) {
                    bm.recycle();
                }
            }

            this.bitmaps.clear();
            this.bitmaps = null;
            System.gc();
        }
    }

    // 操作keyboard的row 可以重置key的内容 (符号字符)
    class num_row extends Keyboard.Row implements BSKeyboardRow {
        private ArrayList<Bitmap> bitmaps = new ArrayList();

        public num_row(Resources arg0, Keyboard arg1, XmlResourceParser arg2) {
            super(arg0, arg1, arg2);
            PasswordEditText.this.encryptValueMap.clear();
            int count2 = 0;

            for (int i = 0; i < arg1.getKeys().size(); ++i) {
                ((Key) arg1.getKeys().get(i)).iconPreview = null;
                int[] letter_int;
                if (((Key) arg1.getKeys().get(i)).codes[0] == KEYCODE_BACKSPACE) {
                    ((Key) arg1.getKeys().get(i)).label = "删除";
                    letter_int = new int[]{KEYCODE_BACKSPACE};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] == -101) {
                    ((Key) arg1.getKeys().get(i)).label = "ABC";
                    letter_int = new int[]{-101};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] == -103) {
                    ((Key) arg1.getKeys().get(i)).label = "符号";
                    letter_int = new int[]{-103};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] == 32) {
                    ((Key) arg1.getKeys().get(i)).label = "空格";
                    letter_int = new int[]{32};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] == 13) {
                    ((Key) arg1.getKeys().get(i)).label = "完成";
                    letter_int = new int[]{13};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] == 46) {
                    ((Key) arg1.getKeys().get(i)).label = ".";
                    letter_int = new int[]{46};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] != 0 && count2 < PasswordEditText.this.number.length) {
                    ((Key) arg1.getKeys().get(i)).label = PasswordEditText.this.str_num[count2];
                    ((Key) arg1.getKeys().get(i)).codes = PasswordEditText.this.getSymbolCodesEncrypt(PasswordEditText.this.str_num[count2]);
                    ++count2;
                }
            }

        }

        public void releaseResources() {
            for (int i = 0; i < this.bitmaps.size(); ++i) {
                Bitmap bm = (Bitmap) this.bitmaps.get(i);
                if (!bm.isRecycled()) {
                    bm.recycle();
                }
            }

            this.bitmaps.clear();
            this.bitmaps = null;
            System.gc();
        }
    }

    // 操作keyboard的row 可以重置key的内容 (符号字符)
    class symbol_row extends Keyboard.Row implements BSKeyboardRow {
        private ArrayList<Bitmap> bitmaps = new ArrayList();

        public symbol_row(Resources arg0, Keyboard arg1, XmlResourceParser arg2) {
            super(arg0, arg1, arg2);
            PasswordEditText.this.encryptValueMap.clear();
            int count2 = 0;

            for (int i = 0; i < arg1.getKeys().size(); ++i) {
                ((Key) arg1.getKeys().get(i)).iconPreview = null;
                int[] letter_int;
                if (((Key) arg1.getKeys().get(i)).codes[0] == KEYCODE_BACKSPACE) {
                    ((Key) arg1.getKeys().get(i)).label = "删除";
                    letter_int = new int[]{KEYCODE_BACKSPACE};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] == -102) {
                    ((Key) arg1.getKeys().get(i)).label = "123";
                    letter_int = new int[]{-102};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] == -101) {
                    ((Key) arg1.getKeys().get(i)).label = "ABC";
                    letter_int = new int[]{-101};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] == 32) {
                    ((Key) arg1.getKeys().get(i)).label = "空格";
                    letter_int = new int[]{32};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] == 13) {
                    ((Key) arg1.getKeys().get(i)).label = "完成";
                    letter_int = new int[]{13};
                    ((Key) arg1.getKeys().get(i)).codes = letter_int;
                } else if (((Key) arg1.getKeys().get(i)).codes[0] != 0 && count2 < PasswordEditText.this.symbol.length) {
                    ((Key) arg1.getKeys().get(i)).label = PasswordEditText.this.str_symbol[count2];
                    ((Key) arg1.getKeys().get(i)).codes = PasswordEditText.this.getSymbolCodesEncrypt(PasswordEditText.this.str_symbol[count2]);
                    ++count2;
                }
            }

        }

        public void releaseResources() {
            for (int i = 0; i < this.bitmaps.size(); ++i) {
                Bitmap bm = (Bitmap) this.bitmaps.get(i);
                if (!bm.isRecycled()) {
                    bm.recycle();
                }
            }

            this.bitmaps.clear();
            this.bitmaps = null;
            System.gc();
        }
    }

}
