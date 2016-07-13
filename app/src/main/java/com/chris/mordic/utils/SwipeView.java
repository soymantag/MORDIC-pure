package com.chris.mordic.utils;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by chris on 7/13/16.
 * Email: soymantag@163.coom
 */
public class SwipeView extends ViewGroup {
    private View mLeftView;
    private View mRightView;
    private int mRightViewWidth;
    private ViewDragHelper mHelper;

    private boolean isOpen = false;
    private OnSwipeListener mListener;

    public SwipeView(Context context) {
        this(context, null);
    }

    public SwipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHelper = ViewDragHelper.create(this, new MyCallBack());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLeftView = getChildAt(0);
        mRightView = getChildAt(1);

        ViewGroup.LayoutParams params = mRightView.getLayoutParams();
        mRightViewWidth = params.width;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mLeftView.measure(widthMeasureSpec, heightMeasureSpec);

        int rightViewWidthMeasureSpec = MeasureSpec.makeMeasureSpec(mRightViewWidth, MeasureSpec.EXACTLY);
        mRightView.measure(rightViewWidthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mLeftView.layout(0, 0, mLeftView.getMeasuredWidth(), mLeftView.getMeasuredHeight());
        mRightView.layout(mLeftView.getMeasuredWidth(), 0, mLeftView.getMeasuredWidth() + mRightView.getMeasuredWidth(), mLeftView.getMeasuredHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mHelper.processTouchEvent(event);
        return true;
    }

    class MyCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {

            return child == mLeftView || child == mRightView;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mLeftView) {
                if (left < 0 && left < -mRightViewWidth) {
                    return -mRightViewWidth;
                } else if (left > 0) {
                    return 0;
                }
            } else if (child == mRightView) {
                if (left < mLeftView.getMeasuredWidth() - mRightViewWidth) {
                    return mLeftView.getMeasuredWidth() - mRightViewWidth;
                } else if (left > mLeftView.getMeasuredWidth()) {
                    return mLeftView.getMeasuredWidth();
                }
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == mLeftView) {
                mRightView.layout(mLeftView.getMeasuredWidth() + left, 0, mLeftView.getMeasuredWidth() + mRightViewWidth, mRightView.getMeasuredHeight());
            } else if (changedView == mRightView) {
                mLeftView.layout(left - mLeftView.getMeasuredWidth(), 0, left, mLeftView.getMeasuredHeight());
            }
            ViewCompat.postInvalidateOnAnimation(SwipeView.this);
            //invalidate();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int left = mLeftView.getLeft();
            if (!isOpen) {

                if (-left < mRightViewWidth / 4f) {
                    close();
                } else {
                    open();
                }
            } else {
                if (-left < mRightViewWidth * 3 / 4f) {
                    close();
                } else {
                    open();
                }
            }
        }
    }

    private void open() {
        isOpen = true;
        if (mListener != null) {
            mListener.onSwipeChanged(SwipeView.this, isOpen);
        }
        mHelper.smoothSlideViewTo(mLeftView, -mRightViewWidth, 0);
        mHelper.smoothSlideViewTo(mRightView, mLeftView.getMeasuredWidth() - mRightViewWidth, 0);
        //invalidate();
        ViewCompat.postInvalidateOnAnimation(SwipeView.this);
    }

    private void close() {
        isOpen = false;
        if (mListener != null) {
            mListener.onSwipeChanged(SwipeView.this, isOpen);
        }
        mHelper.smoothSlideViewTo(mLeftView, 0, 0);
        mHelper.smoothSlideViewTo(mRightView, mLeftView.getMeasuredWidth(), 0);
        //invalidate();
        ViewCompat.postInvalidateOnAnimation(SwipeView.this);
    }

    public void reset() {
        mLeftView.layout(0, 0, mLeftView.getMeasuredWidth(), mLeftView.getMeasuredHeight());
        mRightView.layout(mLeftView.getMeasuredWidth(), 0, mLeftView.getMeasuredWidth() + mRightView.getMeasuredWidth(), mLeftView.getMeasuredHeight());
    }

    @Override
    public void computeScroll() {
        if (mHelper.continueSettling(true)) {
            //invalidate();
            ViewCompat.postInvalidateOnAnimation(SwipeView.this);
        }
    }

    public void setOnSwipeListener(OnSwipeListener listener) {
        this.mListener = listener;
    }

    public interface OnSwipeListener {
        void onSwipeChanged(SwipeView view, boolean isOpen);
    }
}
