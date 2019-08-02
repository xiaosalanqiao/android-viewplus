package cn.jiiiiiin.vplus.core.util.callback;

import androidx.annotation.Nullable;

/**
 * Created by jiiiiiin
 */

public interface IGlobalCallback<T> {

    void executeCallback(@Nullable T args);
}
