package cn.jiiiiiin.vplus.core.webview.chromeclient;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.afollestad.materialdialogs.WhichButton;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;

import cn.jiiiiiin.vplus.core.app.ViewPlus;
import cn.jiiiiiin.vplus.core.ui.dialog.DialogUtil;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.webview.loader.IPageLoadListener;
import kotlin.Unit;

/**
 * @author jiiiiiin
 */

public class WebChromeClientImpl extends WebChromeClient {

    // TODO 这个配置最好做成动态
    private static final int WEBVIEW_PROGRESS_OK = 80;
    private final Activity mActivity;
    private IPageLoadListener mPageLoadListener;
    private boolean mIsNotifyOnprogresschangedI00 = false;
    public static ValueCallback<Uri> uploadMessage;
    public static ValueCallback<Uri[]> uploadMessageAboveL;

    public WebChromeClientImpl(Activity activity, IPageLoadListener pageLoadListener) {
        this.mActivity = activity;
        this.mPageLoadListener = pageLoadListener;
    }

    /**
     * WebChromeClient.onProgressChanged(webview, progress)，根据这个回调，可以控制进度条的进度（包括显示与隐藏）。一般情况下，想要达到100%的进度需要的时间较长（特别是首次加载），用户长时间等待进度条不消失必定会感到焦虑，影响体验。其实当progress达到80的时候，加载出来的页面已经基本可用了。事实上，国内厂商大部分都会提前隐藏进度条，让用户以为网页加载很快。
     *
     * 作者：网易考拉移动端团队
     * 链接：https://juejin.im/post/5a94fb046fb9a0635865a2d6
     * @param newProgress 表示当前页面加载的进度，为1至100的整数
     */
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (newProgress < WEBVIEW_PROGRESS_OK) {
            mIsNotifyOnprogresschangedI00 = false;
        }
        if (mPageLoadListener != null && !mIsNotifyOnprogresschangedI00) {
            mIsNotifyOnprogresschangedI00 = newProgress >= WEBVIEW_PROGRESS_OK;
            mPageLoadListener.onProgressChanged(view, newProgress);
            if (mIsNotifyOnprogresschangedI00) {
                if (!view.getSettings().getLoadsImagesAutomatically()) {
                    view.getSettings().setLoadsImagesAutomatically(true);
                }
                LoggerProxy.i("===h5页面[%s]加载完毕 onProgressChanged", newProgress);
                mPageLoadListener.onLoadEnd(true);
            }
        }
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        DialogUtil.dialog(mActivity, "提示", message, dialog -> result.confirm());
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        DialogUtil.confirmDialog(mActivity, message, (positive) -> {
            result.confirm();
            return Unit.INSTANCE;
        }, negative -> {
            result.cancel();
            return Unit.INSTANCE;
        });
        return true;
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        DialogUtil.promptDialog(mActivity, message, "", defaultValue, (dialog, input) -> {
            result.confirm(String.valueOf(input));
            dialog.dismiss();
            return Unit.INSTANCE;
        });
        return true;
    }

    /**
     * 声明以处理Android6.0以下判断404或者500
     * http://blog.csdn.net/lsyz0021/article/details/56677132
     */
    @SuppressWarnings("AlibabaUndefineMagicConstant")
    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        // android 6.0 以下通过title获取
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // !一下这种做法依赖的是html的title被设置为下面的集中判断情况
            if (!TextUtils.isEmpty(title)) {
                if (title.contains("404") || title.contains("找不到网页") || title.contains("网页无法打开")) {
                    LoggerProxy.e("TTTTT 待调试情况出现 %s %s", view.getUrl(), view.getOriginalUrl());
                    this.mPageLoadListener.onReceivedError(view, 404, "404", view.getUrl());
                } else if (title.contains("500") || title.contains("Error")) {
                    LoggerProxy.e("TTTTT 待调试情况出现 %s %s", view.getUrl(), view.getOriginalUrl());
                    this.mPageLoadListener.onReceivedError(view, 500, "404", view.getUrl());
                }
            }
        }
    }

    // ==== 支持h5图片选择 https://mp.weixin.qq.com/s/FyxuOuTFyZ_F8D0jQ8w5bg
    //  android 3.0以下：用的这个方法
    public void openFileChooser(ValueCallback<Uri> valueCallback) {
        uploadMessage = valueCallback;
        openImageChooserActivity();
    }

    // android 3.0以上，android4.0以下：用的这个方法
    public void openFileChooser(ValueCallback valueCallback, String acceptType) {
        uploadMessage = valueCallback;
        openImageChooserActivity();
    }

    //android 4.0 - android 4.3  安卓4.4.4也用的这个方法
    public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType,
                                String capture) {
        uploadMessage = valueCallback;
        openImageChooserActivity();
    }

    //android4.4 无方法。。。
    // Android 5.0及以上用的这个方法
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]>
            filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        uploadMessageAboveL = filePathCallback;
        openImageChooserActivity();
        return true;
    }

    private void openImageChooserActivity() {
//        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//        i.addCategory(Intent.CATEGORY_OPENABLE);
//        i.setType("image/*");
//        mActivity.startActivityForResult(Intent.createChooser(i, "Image Chooser"),
//                FILE_CHOOSER_RESULT_CODE);
        // TODO req code 待设置
        // TODO 待测试
        ViewPlus.getConfigurator().withStartOtherActivity(true);
        PictureSelector.create(mActivity)
                .openGallery(PictureMimeType.ofImage())
                .selectionMode(PictureConfig.MULTIPLE)
                // 每行显示个数 int
                .maxSelectNum(1)
                .minSelectNum(1)
                .imageSpanCount(4)
                .selectionMode(PictureConfig.MULTIPLE)
                .previewImage(true)
                .isCamera(true)
                .isGif(false)
                .compress(true)
                .synOrAsy(false)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        if (ViewPlus.IS_DEBUG()) {
            LoggerProxy.w("前端console信息 %s %s %s", consoleMessage.message(), consoleMessage.lineNumber(), consoleMessage.sourceId());
        }
        return super.onConsoleMessage(consoleMessage);
    }
}
