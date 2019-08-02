package cn.jiiiiiin.viewplus;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.WhichButton;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jiiiiiin.vplus.core.delegates.AbstractViewPlusDelegate;
import cn.jiiiiiin.vplus.core.delegates.BaseDelegate;
import cn.jiiiiiin.vplus.core.ui.dialog.DialogUtil;
import cn.jiiiiiin.vplus.core.util.log.LoggerProxy;
import cn.jiiiiiin.vplus.core.webview.AbstractWebViewWrapperCommUIDelegate;
import cn.jiiiiiin.vplus.core.webview.WebViewDelegateImpl;
import kotlin.Unit;

/**
 * created by YLG on 2019/7/12
 */

public class TestDialogDelegate extends AbstractViewPlusDelegate {
    @BindView(R2.id.button)
    Button button1;

    @BindView(R2.id.button2)
    Button button2;

    @BindView(R2.id.button3)
    Button button3;

    @BindView(R2.id.button4)
    Button button4;

    @BindView(R2.id.button5)
    Button button5;

    @BindView(R2.id.button6)
    Button button6;

    @BindView(R2.id.button7)
    Button button7;

    @BindView(R2.id.button8)
    Button button8;

    @Override
    protected Class<? extends BaseDelegate> getRootClazz() {
        return TestDialogDelegate.class;
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_test_dialog;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }

    public static TestDialogDelegate newInstance() {
        return new TestDialogDelegate();
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        ImmersionBar.with(this).fitsSystemWindows(true).init();
    }

    @OnClick(R2.id.button)
    void click() {
        DialogUtil.errDialog(getActivity(), "test error dialog!!!", dialog -> {
            LoggerProxy.e("test error dialog success");
        });
    }

    @OnClick(R2.id.button2)
    void click2() {
        DialogUtil.dialog(getActivity(), "Test2", "test dialog2 !!!", dialog -> {
            LoggerProxy.e("test dialog2 success");
        });
    }

    @OnClick(R2.id.button3)
    void click3() {
        DialogUtil.confirmDialog(getActivity(), "test dialog3 !!!", (positive) -> {
            LoggerProxy.e("positive button click");
            return Unit.INSTANCE;
        }, negative -> {
            LoggerProxy.e("negative button click");
            return Unit.INSTANCE;
        });
    }

    @OnClick(R2.id.button4)
    void click4() {
        DialogUtil.promptDialog(getActivity(), "test dialog4", "请输入...",
                "asdfg", (materialDialog, charSequence) -> {
                    LoggerProxy.e("test dialog4 input: %s", charSequence);
                    return Unit.INSTANCE;
                });
    }

    @OnClick(R2.id.button5)
    void click5() {
        DialogUtil.setGestureDialog(getActivity(), "test dialog5", "确定", "取消",
                positive -> {
                    LoggerProxy.e("test5 click positive");
                    return Unit.INSTANCE;
                },
                negative -> {
                    LoggerProxy.e("test5 click negative");
                    negative.dismiss();
                    return Unit.INSTANCE;
                });
    }


    @OnClick(R2.id.button6)
    void click6() {
        DialogUtil.showMaterialDialog(getActivity(), "测试", "test dialog6",
                "确定", "取消", positive -> {
                    LoggerProxy.e("test6 click positive");
                    return Unit.INSTANCE;
                },
                negative -> {
                    LoggerProxy.e("test6 click negative");
                    return Unit.INSTANCE;
                });
    }

    @OnClick(R2.id.button7)
    void click7(){
//        DialogUtil.showMaterialDialog(getActivity(), "test dialog7", false,
//                positive -> {
//                    LoggerProxy.e("test7 click positive");
//                    return Unit.INSTANCE;
//                },
//                negative -> {
//                    LoggerProxy.e("test7 click negative");
//                    return Unit.INSTANCE;
//                });

        DialogUtil.showMaterialDialog(getActivity(), "test dialog7", false,
                null,
                null);
    }
}
