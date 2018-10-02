package cn.jiiiiiin.vplus.ui.recycler.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author jiiiiiin
 * @version 1.0
 */
public abstract class EqualSectionEntity<T> implements Serializable {
    public boolean isHeader;
    public T t;
    public String header;

    public EqualSectionEntity(boolean isHeader, String header) {
        this.isHeader = isHeader;
        this.header = header;
        this.t = null;
    }

    public EqualSectionEntity(T t) {
        this.isHeader = false;
        this.header = null;
        this.t = t;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EqualSectionEntity<?> that = (EqualSectionEntity<?>) o;
        return isHeader == that.isHeader &&
                Objects.equals(t, that.t) &&
                Objects.equals(header, that.header);
    }

    @Override
    public int hashCode() {

        return Objects.hash(isHeader, t, header);
    }
}
