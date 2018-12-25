package cn.jiiiiiin.vplus.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.support.annotation.ColorInt;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by jiiiiiin
 */

public class CircleTextView extends AppCompatTextView {

    // 背景画笔
    private final Paint PAINT;
    private final PaintFlagsDrawFilter FILTER;

    public CircleTextView(Context context) {
        this(context, null);
    }

    // 通过xml布局
    public CircleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        PAINT = new Paint();
        FILTER = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        PAINT.setColor(Color.WHITE);
        PAINT.setAntiAlias(true);
    }

    /**
     * 设置颜色
     */
    public final void setCircleBackground(@ColorInt int color) {
        PAINT.setColor(color);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取测量宽高
        final int width = getMeasuredWidth();
        final int height = getMaxHeight();
        final int max = Math.max(width, height);
        setMeasuredDimension(max, max);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.setDrawFilter(FILTER);
        // 绘制圆形
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, Math.max(getWidth(), getHeight()) / 2, PAINT);
        super.draw(canvas);
    }
}
