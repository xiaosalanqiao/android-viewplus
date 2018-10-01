package cn.jiiiiiin.vplus.ui.recycler;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cn.jiiiiiin.vplus.ui.R;

/**
 * @author jiiiiiin
 * @version 1.0
 */
@Deprecated
public class EmptyViewCreater {

    public static final LinearLayout.LayoutParams LINEAR_LAYOUT_LAYOUT_PARAMS = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    public static View createReqFailEmptyView(@NonNull Activity activity, @NonNull View contentView, @NonNull ViewGroup containerView, int idx, ErrorViewRetryListener listener) {
        return _getErrorView(activity, contentView, containerView, idx, listener, R.layout.network_fial_error_view);
    }


    public static View createReqErrEmptyView(@NonNull Activity activity, @NonNull View contentView, @NonNull ViewGroup containerView, int idx, ErrorViewRetryListener listener) {
        return _getErrorView(activity, contentView, containerView, idx, listener, R.layout.error_view);
    }

    public interface ErrorViewRetryListener {
        void onRetry();
    }

    private static View _getErrorView(@NonNull Activity activity, @NonNull View contentView, @NonNull ViewGroup containerView, int idx, ErrorViewRetryListener listener, int layoutId) {
        View res = activity.getLayoutInflater().inflate(layoutId, null, false);
        res.setOnClickListener(v -> {
            contentView.setVisibility(View.VISIBLE);
            res.setVisibility(View.GONE);
            listener.onRetry();
        });
        res.setLayoutParams(LINEAR_LAYOUT_LAYOUT_PARAMS);
        contentView.setVisibility(View.GONE);
        containerView.addView(res, idx);
        return res;
    }
}
