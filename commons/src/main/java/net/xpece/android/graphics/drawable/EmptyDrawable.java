package net.xpece.android.graphics.drawable;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Dimension;

/**
 * Created by Eugen on 28.12.2016.
 */

public class EmptyDrawable extends ColorDrawable {
    private final int widthPx;
    private final int heightPx;

    public EmptyDrawable(@Dimension int sizePx) {
        this(sizePx, sizePx);
    }

    public EmptyDrawable(@Dimension int widthPx, @Dimension int heightPx) {
        super(Color.TRANSPARENT);
        this.widthPx = widthPx;
        this.heightPx = heightPx;
    }

    @Override
    public int getIntrinsicWidth() {
        return widthPx;
    }

    @Override
    public int getIntrinsicHeight() {
        return heightPx;
    }
}
