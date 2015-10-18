/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 XiNGRZ <xxx@oxo.ooo>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ooo.oxo.library.widget;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PullBackLayout extends FrameLayout {

    /**
     * Flag indicated pulling up is allowed
     *
     * @see #setDirection(int)
     */
    public static final int DIRECTION_UP = 1;

    /**
     * Flag indicated pulling down is allowed
     *
     * @see #setDirection(int)
     */
    public static final int DIRECTION_DOWN = 1 << 1;

    private final ViewDragHelper dragger;

    private final int minimumFlingVelocity;

    @Direction
    private int direction = DIRECTION_UP | DIRECTION_DOWN;

    @Nullable
    private Callback callback;

    public PullBackLayout(Context context) {
        this(context, null);
    }

    public PullBackLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dragger = ViewDragHelper.create(this, 1f / 8f, new ViewDragCallback());
        minimumFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }

    /**
     * @return Allowed pulling direction
     */
    @Direction
    public int getDirection() {
        return direction;
    }

    /**
     * Sets pulling directions allowed
     *
     * @param direction Directions allowed
     * @see #DIRECTION_UP
     * @see #DIRECTION_DOWN
     */
    public void setDirection(@Direction int direction) {
        this.direction = direction;
    }

    public void setCallback(@Nullable Callback callback) {
        this.callback = callback;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return dragger.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        dragger.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (dragger.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void onPullStart() {
        if (callback != null) {
            callback.onPullStart();
        }
    }

    private void onPull(@Direction int direction, float progress) {
        if (callback != null) {
            callback.onPull(direction, progress);
        }
    }

    private void onPullCancel(@Direction int direction) {
        if (callback != null) {
            callback.onPullCancel(direction);
        }
    }

    private void onPullComplete(@Direction int direction) {
        if (callback != null) {
            callback.onPullComplete(direction);
        }
    }

    private void reset() {
        dragger.settleCapturedViewAt(0, 0);
        invalidate();
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {DIRECTION_UP, DIRECTION_DOWN}, flag = true)
    @interface Direction {
    }

    public interface Callback {

        void onPullStart();

        void onPull(@Direction int direction, float progress);

        void onPullCancel(@Direction int direction);

        void onPullComplete(@Direction int direction);

    }

    private class ViewDragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return 0;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if ((direction & (DIRECTION_UP | DIRECTION_DOWN)) != 0) {
                return top;
            } else if ((direction & DIRECTION_UP) != 0) {
                return Math.min(0, top);
            } else if ((direction & DIRECTION_DOWN) != 0) {
                return Math.max(0, top);
            } else {
                return 0;
            }
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return 0;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            if (direction == 0) {
                return 0;
            } else if ((direction & (DIRECTION_UP | DIRECTION_DOWN)) != 0) {
                return getHeight() * 2;
            } else {
                return getHeight();
            }
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            onPullStart();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (top > 0) {
                onPull(DIRECTION_DOWN, (float) top / (float) getHeight());
            } else if (top < 0) {
                onPull(DIRECTION_UP, (float) -top / (float) getHeight());
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int top = releasedChild.getTop();
            int slop = Math.abs(yvel) > minimumFlingVelocity ? getHeight() / 6 : getHeight() / 3;

            if (top > 0) {
                if (top > slop) {
                    onPullComplete(DIRECTION_DOWN);
                } else {
                    onPullCancel(DIRECTION_DOWN);
                    reset();
                }
            } else if (top < 0) {
                if (top < -slop) {
                    onPullComplete(DIRECTION_UP);
                } else {
                    onPullCancel(DIRECTION_UP);
                    reset();
                }
            }
        }

    }

}
