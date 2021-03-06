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

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.StateSet;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

class CardButtonGingerbread extends CardButtonImpl {

    private final StateListAnimator mStateListAnimator;

    ShadowDrawableWrapper mShadowDrawable;

    CardButtonGingerbread(Button view, CardButtonDelegate shadowViewDelegate, ValueAnimatorCompat.Creator animatorCreator) {
        super(view, shadowViewDelegate, animatorCreator);

        mStateListAnimator = new StateListAnimator();

        // Elevate with translationZ when pressed or focused
        mStateListAnimator.addState(PRESSED_ENABLED_STATE_SET,
            createAnimator(new ElevateToTranslationZAnimation()));
        mStateListAnimator.addState(FOCUSED_ENABLED_STATE_SET,
            createAnimator(new ElevateToTranslationZAnimation()));
        // Reset back to elevation by default
        mStateListAnimator.addState(ENABLED_STATE_SET,
            createAnimator(new ResetElevationAnimation()));
        // Set to 0 when disabled
        mStateListAnimator.addState(EMPTY_STATE_SET,
            createAnimator(new DisabledElevationAnimation()));
    }

    @Override
    void setBackgroundDrawable(@Nullable ColorStateList backgroundTint,
                               @Nullable PorterDuff.Mode backgroundTintMode, @ColorInt int rippleColor, @IntRange(from = 0) int borderWidth,
                               @Nullable ColorStateList borderColor) {
        final float cornerRadius = mShadowViewDelegate.getRadius();

        final boolean hasBorder = borderWidth > 0 && isNotTransparent(borderColor);
        final boolean hasBackground = isNotTransparent(backgroundTint);

        if (hasBackground) {
            mShapeDrawable = createShapeDrawable(cornerRadius);
            DrawableCompat.setTintList(mShapeDrawable, backgroundTint);
            if (backgroundTintMode != null) {
                DrawableCompat.setTintMode(mShapeDrawable, backgroundTintMode);
            }
        } else {
            mShapeDrawable = null;
        }

        if (hasBorder) {
            mBorderDrawable = createBorderDrawable(borderWidth, cornerRadius, borderColor);
        } else {
            mBorderDrawable = null;
        }

        mRippleDrawable = createRippleDrawable(rippleColor, cornerRadius);

        makeAndSetBackground(cornerRadius);
    }

    private void makeAndSetBackground(final float radius) {
        final List<Drawable> layers = new ArrayList<>();
        if (mShapeDrawable != null) layers.add(mShapeDrawable);
        if (mBorderDrawable != null) layers.add(mBorderDrawable);
//        layers.add(mRippleDrawable);

        final int size = layers.size();
        if (size > 1) {
            mContentBackground = new LayerDrawable(layers.toArray(new Drawable[size]));
        } else if (size == 1) {
            mContentBackground = layers.get(0);
        } else {
            mContentBackground = new ColorDrawable(0);
        }

        mShadowDrawable = new ShadowDrawableWrapper(
            mView.getContext(),
            mContentBackground,
            radius,
            mElevation,
            mElevation + mPressedTranslationZ);
        mShadowDrawable.setAddPaddingForCorners(false);
        mShadowViewDelegate.setBackgroundDrawable(mShadowDrawable);

        mShadowViewDelegate.setForegroundDrawable(mRippleDrawable);
    }

    Drawable createShapeDrawable(float cornerRadius) {
        return CardButtonDrawableFactory.newRoundRectDrawableCompat(cornerRadius, Color.WHITE);
    }

    Drawable createBorderDrawable(@IntRange(from = 0) @Px final int borderWidth, @FloatRange(from = 0) final float cornerRadius, @NonNull final ColorStateList borderTint) {
        final Drawable drawable = CardButtonDrawableFactory.newBorderShapeDrawableCompat(borderWidth, cornerRadius);
        DrawableCompat.setTintList(drawable, borderTint);
        return drawable;
    }

    @Override
    void setBackgroundTintList(ColorStateList tint) {
        if (mShapeDrawable != null) {
            DrawableCompat.setTintList(mShapeDrawable, tint);
        }
    }

    @Override
    void setBackgroundTintMode(PorterDuff.Mode tintMode) {
        if (mShapeDrawable != null) {
            DrawableCompat.setTintMode(mShapeDrawable, tintMode);
        }
    }

    @Override
    void setRippleColor(@ColorInt int rippleColor) {
        final float radius = mShadowViewDelegate.getRadius();
        mRippleDrawable = createRippleDrawable(rippleColor, radius);
        makeAndSetBackground(radius);
    }

    @Override
    float getElevation() {
        return mElevation;
    }

    @Override
    void onElevationsChanged(float elevation, float pressedTranslationZ) {
        if (mShadowDrawable != null) {
            mShadowDrawable.setShadowSize(elevation, elevation + mPressedTranslationZ);
            updatePadding();
        }
    }

    @Override
    void onDrawableStateChanged(int[] state) {
        mStateListAnimator.setState(state);
    }

    @Override
    void jumpDrawableToCurrentState() {
        mStateListAnimator.jumpToCurrentState();
    }

    @Override
    void onCompatShadowChanged() {
        // Ignore pre-v21
    }

    @Override
    void getPadding(Rect rect) {
        mShadowDrawable.getPadding(rect);
    }

    private ValueAnimatorCompat createAnimator(@NonNull ShadowAnimatorImpl impl) {
        final ValueAnimatorCompat animator = mAnimatorCreator.createAnimator();
        animator.setInterpolator(ANIM_INTERPOLATOR);
        animator.setDuration(PRESSED_ANIM_DURATION);
        animator.addListener(impl);
        animator.addUpdateListener(impl);
        animator.setFloatValues(0, 1);
        return animator;
    }

    private abstract class ShadowAnimatorImpl extends ValueAnimatorCompat.AnimatorListenerAdapter
        implements ValueAnimatorCompat.AnimatorUpdateListener {
        private boolean mValidValues;
        private float mShadowSizeStart;
        private float mShadowSizeEnd;

        @Override
        public void onAnimationStart(ValueAnimatorCompat animator) {
//            if (mShadowDrawable == null) {
//                animator.cancel();
//            }
        }

        @Override
        public void onAnimationUpdate(ValueAnimatorCompat animator) {
            if (mShadowDrawable != null) {
                if (!mValidValues) {
                    mShadowSizeStart = mShadowDrawable.getShadowSize();
                    mShadowSizeEnd = getTargetShadowSize();
                    mValidValues = true;
                }

                mShadowDrawable.setShadowSize(mShadowSizeStart
                    + ((mShadowSizeEnd - mShadowSizeStart) * animator.getAnimatedFraction()));
            }
        }

        @Override
        public void onAnimationEnd(ValueAnimatorCompat animator) {
            if (mShadowDrawable != null) {
                mShadowDrawable.setShadowSize(mShadowSizeEnd);
            }
            mValidValues = false;
        }

        /**
         * @return the shadow size we want to animate to.
         */
        protected abstract float getTargetShadowSize();
    }

    private class ResetElevationAnimation extends ShadowAnimatorImpl {
        ResetElevationAnimation() {
        }

        @Override
        protected float getTargetShadowSize() {
            return mElevation;
        }
    }

    private class ElevateToTranslationZAnimation extends ShadowAnimatorImpl {
        ElevateToTranslationZAnimation() {
        }

        @Override
        protected float getTargetShadowSize() {
            return mElevation + mPressedTranslationZ;
        }
    }

    private class DisabledElevationAnimation extends ShadowAnimatorImpl {
        DisabledElevationAnimation() {
        }

        @Override
        protected float getTargetShadowSize() {
            return 0f;
        }
    }

    private Drawable createRippleDrawable(@ColorInt int rippleColor, float cornerRadius) {
        Drawable focused = CardButtonDrawableFactory.newRoundRectDrawableCompat(cornerRadius, rippleColor);
        Drawable pressed = CardButtonDrawableFactory.newRoundRectDrawableCompat(cornerRadius, rippleColor);
        Drawable other = CardButtonDrawableFactory.newRoundRectDrawableCompat(cornerRadius, Color.TRANSPARENT);
        StateListDrawable states = new StateListDrawable();
        states.addState(FOCUSED_ENABLED_STATE_SET, focused);
        states.addState(PRESSED_ENABLED_STATE_SET, pressed);
        states.addState(StateSet.WILD_CARD, other);
        if (Build.VERSION.SDK_INT >= 11) {
            states.setEnterFadeDuration(PRESSED_ANIM_DURATION);
            states.setExitFadeDuration(PRESSED_ANIM_DURATION);
        }
        return states;
    }

}
