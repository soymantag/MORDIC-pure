package com.chris.mordic.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.chris.mordic.R;

import java.lang.reflect.Field;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chris on 7/14/16.
 * Email: soymantag@163.coom
 */
public class WordActivity extends Activity{
    @InjectView(R.id.vp_word)
    ViewPager mV_word;
    @InjectView(R.id.rl_word)
    RelativeLayout mRelativeLayout;
    //    @InjectView(R.id.tv_wordIndex)
    //    TextView mTv_wordIndex;
    //    @InjectView(R.id.tv_word)
    //    TextView mTv_word;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
/*            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);*/
        }
        setContentView(R.layout.activity_word);
        ButterKnife.inject(this);
        //mV_word.setOffscreenPageLimit(3);
        mV_word.setPageMargin(10);
        mRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mV_word.dispatchTouchEvent(event);
            }
        });
        mV_word.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 10;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view==object;
            }
            @Override
            public Object instantiateItem(ViewGroup container, int position) {

                //                TextView textView = new TextView(MainActivity.this);
                //                textView.setText("第"+position+"页");
                //                textView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                //                textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                //                textView.setTextSize(50);
                //                container.addView(textView);
                View item_word = View.inflate(WordActivity.this,R.layout.item_word,null);
                //ButterKnife.inject(item_word);
                TextView mTv_word = (TextView) item_word.findViewById(R.id.tv_word);
                mTv_word.setText("apple");
                final TextView mtv_learned = (TextView) item_word.findViewById(R.id.learned);
                mtv_learned.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mtv_learned.setTextColor(getResources().getColor(R.color.lightGrey));
                    }
                });

                container.addView(item_word);
                return item_word;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View)object);
            }
        });
        //这个是设置viewPager切换过度时间的类
        ViewPagerScroller scroller = new ViewPagerScroller(this);
        scroller.setScrollDuration(0);
        scroller.initViewPagerScroll(mV_word);  //这个是设置切换过渡时间为0毫秒
    }
    /**
     * ViewPager 滚动速度设置
     *
     */
    public class ViewPagerScroller extends Scroller {
        private int mScrollDuration = 2000;             // 滑动速度

        /**
         * 设置速度速度
         * @param duration
         */
        public void setScrollDuration(int duration){
            this.mScrollDuration = duration;
        }

        public ViewPagerScroller(Context context) {
            super(context);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration);
        }



        public void initViewPagerScroll(ViewPager viewPager) {
            try {
                Field mScroller = ViewPager.class.getDeclaredField("mScroller");
                mScroller.setAccessible(true);
                mScroller.set(viewPager, this);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }



}
