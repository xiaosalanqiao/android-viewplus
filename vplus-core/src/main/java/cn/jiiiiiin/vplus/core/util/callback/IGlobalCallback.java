package cn.jiiiiiin.vplus.core.util.callback;

import android.support.annotation.Nullable;

/**
 * Created by jiiiiiin
 */

public interface IGlobalCallback<T> {

    void executeCallback(@Nullable T args);
}
