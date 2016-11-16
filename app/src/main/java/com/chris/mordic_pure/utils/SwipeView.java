package com.chris.mordic_pure.utils;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Timer;

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
    Context mContext;
    public SwipeView(Context context) {
        this(context, null);
    }

    public SwipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
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
    private float mDownX;
    private float mDownY;
    long timeClick=0;
    boolean longClickFlag=false;
    Timer timer=new Timer();//生成一个Timer对象
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

/*        TimerTask task =new TimerTask() {
            @Override
            public void run() {
                longClickFlag=true;
                if(mOnLongClickListener!=null){
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mOnLongClickListener.onLongClick(SwipeView.this);
                        }
                    });

                }
            }
        };*/
        mHelper.processTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                timeClick= System.currentTimeMillis();
                mDownX = ev.getX();
                mDownY = ev.getY();

                //longClickFlag=false;
                //timer.schedule(task, 1000);

                break;

/*            case MotionEvent.ACTION_MOVE:
                float moveX = ev.getX();
                float moveY = ev.getY();

                if (Math.abs(moveX - mDownX) > Math.abs(moveY - mDownY)) {
                    // 水平方向移动
                    return true;
                }
                break;*/
            case MotionEvent.ACTION_UP:
/*                timer.cancel();
                timer.purge();
                if(longClickFlag){
                    break;
                }*/
/*                longClickFlag =1;
                if((System.currentTimeMillis()-timeClick)>500){
                    if(mOnLongClickListener!=null){
                        mOnLongClickListener.onLongClick(this);
                    }
                    break;
                }*/
                float upX = ev.getX();
                float upY = ev.getY();
                if(Math.abs(upX-mDownX)<10 && Math.abs(upY -mDownY)<10){
                    if(mOnClickListener!=null){
                        mOnClickListener.onClick(this);
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }
    View.OnClickListener mOnClickListener =null;
    @Override
    public void setOnClickListener(OnClickListener l) {
        mOnClickListener=l;
    }
    View.OnLongClickListener mOnLongClickListener=null;

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        mOnLongClickListener=l;
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
