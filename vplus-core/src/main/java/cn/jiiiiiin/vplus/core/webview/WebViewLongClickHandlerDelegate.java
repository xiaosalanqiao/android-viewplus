package cn.jiiiiiin.vplus.core.webview;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import cn.jiiiiiin.vplus.core.R;
import cn.jiiiiiin.vplus.core.util.Intent.RouterUtil;

/**
 * @author jiiiiiin
 * @version 1.0
 */
public abstract class WebViewLongClickHandlerDelegate extends AbstractWebViewDelegate implements View.OnClickListener {

    private DialogPlus mDialogPlus = null;
    private String mOnLongClickListenerUrl = "";
    private OnChooseImageListener mOnChooseImageListener = null;

    public void setOnChooseImageListener(OnChooseImageListener onChooseImageListener) {
        this.mOnChooseImageListener = onChooseImageListener;
    }

    public interface OnChooseImageListener {
        void onViewImage(String url);

        void onShareImage(String url);

        void onSaveToAlbum(String url);
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        super.onBindView(savedInstanceState, rootView);
        if (mOnChooseImageListener != null) {
            mWebView.setOnLongClickListener(v -> {
                WebView.HitTestResult result = ((WebView) v).getHitTestResult();
                if (null == result) {
                    return false;
                }
                int type = result.getType();
                if (type == WebView.HitTestResult.UNKNOWN_TYPE) {
                    return false;
                }
                boolean res = false;
                mOnLongClickListenerUrl = result.getExtra();
                // 这里可以拦截很多类型，我们只处理图片类型就可以了
                switch (type) {
                    // 处理拨号
                    case WebView.HitTestResult.PHONE_TYPE:
                        RouterUtil.start4ActionDialProtocol(_mActivity, mOnLongClickListenerUrl);
                        break;
                    // 处理Email
                    case WebView.HitTestResult.EMAIL_TYPE:
                        break;
                    // 地图类型
                    case WebView.HitTestResult.GEO_TYPE:
                        break;
                    // 超链接
                    case WebView.HitTestResult.SRC_ANCHOR_TYPE:
                        break;
                    case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
                        break;
                    // 处理长按图片的菜单项
                    case WebView.HitTestResult.IMAGE_TYPE:
                        mDialogPlus = DialogPlus.newDialog(_mActivity)
                                .setContentHolder(new ViewHolder(R.layout.dialogplus_webview_onlongclick_image))
                                .setGravity(Gravity.BOTTOM)
                                .create();
                        final View view = mDialogPlus.getHolderView();
                        view.findViewById(R.id.dialogplus_webview_save_to_album).setOnClickListener(this);
                        view.findViewById(R.id.dialogplus_webview_share).setOnClickListener(this);
                        view.findViewById(R.id.dialogplus_webview_view).setOnClickListener(this);
                        view.findViewById(R.id.dialogplus_webview_cancel).setOnClickListener(this);
                        mDialogPlus.show();
                        res = true;
                        break;
                    default:
                        break;
                }
                return res;
            });
        }
    }


    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (mOnChooseImageListener != null) {
            mDialogPlus.dismiss();
            if (vId == R.id.dialogplus_webview_view) {
                mOnChooseImageListener.onViewImage(mOnLongClickListenerUrl);
            } else if (vId == R.id.dialogplus_webview_share) {
                mOnChooseImageListener.onShareImage(mOnLongClickListenerUrl);
            } else if (vId == R.id.dialogplus_webview_save_to_album) {
                mOnChooseImageListener.onSaveToAlbum(mOnLongClickListenerUrl);
            }

        }
    }
}
