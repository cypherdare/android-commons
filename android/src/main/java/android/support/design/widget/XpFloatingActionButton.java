/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.support.design.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.TintContextWrapper;
import android.support.v7.widget.TintableImageView;
import android.support.v7.widget.XpAppCompatImageHelper;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Floating action buttons are used for a special type of promoted action. They are distinguished
 * by a circled icon floating above the UI and have special motion behaviors related to morphing,
 * launching, and the transferring anchor point.
 *
 * <p>Floating action buttons come in two sizes: the default and the mini. The size can be
 * controlled with the {@code fabSize} attribute.</p>
 *
 * <p>As this class descends from {@link ImageView}, you can control the icon which is displayed
 * via {@link #setImageDrawable(Drawable)}.</p>
 *
 * <p>The background color of this view defaults to the your theme's {@code colorAccent}. If you
 * wish to change this at runtime then you can do so via
 * {@link #setBackgroundTintList(ColorStateList)}.</p>
 */
public class XpFloatingActionButton extends FloatingActionButton implements TintableImageView {

    private XpAppCompatImageHelper mImageTintHelper;

    public XpFloatingActionButton(Context context) {
        this(context, null);
    }

    public XpFloatingActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XpFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(TintContextWrapper.wrap(context), attrs, defStyleAttr);

        mImageTintHelper = new XpAppCompatImageHelper(this);
        mImageTintHelper.loadFromAttributes(attrs, defStyleAttr);
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        if (mImageTintHelper != null) {
            mImageTintHelper.onSetImageResource(resId);
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (mImageTintHelper != null) {
            mImageTintHelper.onSetImageDrawable(drawable);
        }
    }

    /**
     * @hide
     */
    @Override
    public void setSupportImageTintList(@Nullable ColorStateList tint) {
        if (mImageTintHelper != null) {
            mImageTintHelper.setSupportTintList(tint);
        }
    }

    /**
     * @hide
     */
    @Override
    @Nullable
    public ColorStateList getSupportImageTintList() {
        return mImageTintHelper != null
            ? mImageTintHelper.getSupportTintList() : null;
    }

    /**
     * @hide
     */
    @Override
    public void setSupportImageTintMode(@Nullable PorterDuff.Mode tintMode) {
        if (mImageTintHelper != null) {
            mImageTintHelper.setSupportTintMode(tintMode);
        }
    }

    /**
     * @hide
     */
    @Override
    @Nullable
    public PorterDuff.Mode getSupportImageTintMode() {
        return mImageTintHelper != null
            ? mImageTintHelper.getSupportTintMode() : null;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mImageTintHelper != null) {
            mImageTintHelper.applySupportTint();
        }
    }
}