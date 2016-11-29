package net.xpece.android.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;
import android.widget.AbsoluteLayout;

import net.xpece.android.view.XpView;

/**
 * AbsoluteLayout that doesn't consume window insets.
 * Ideal as fragment container.
 */
@SuppressWarnings("deprecation")
public class IrresponsibleAbsoluteLayoutApi19 extends AbsoluteLayout {
    public IrresponsibleAbsoluteLayoutApi19(Context context) {
        super(context);
    }

    public IrresponsibleAbsoluteLayoutApi19(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IrresponsibleAbsoluteLayoutApi19(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(21)
    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        return insets;
    }

    @SuppressWarnings("deprecation")
    @Override
    @TargetApi(19)
    protected boolean fitSystemWindows(final Rect insets) {
        if (Build.VERSION.SDK_INT == 19 || Build.VERSION.SDK_INT == 20) {
            boolean done = false;
            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                final View child = getChildAt(i);
//                done = child.fitSystemWindows(insets);
                done = XpView.fitSystemWindows(child, insets);
                if (done) {
                    break;
                }
            }
            return done;
        } else {
            return super.fitSystemWindows(insets);
        }
    }
}
