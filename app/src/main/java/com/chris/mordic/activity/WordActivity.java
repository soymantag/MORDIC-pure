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
import com.chris.mordic.data.WordBean;
import com.chris.mordic.data.WordbookBean;
import com.chris.mordic.db.WordDao;
import com.chris.mordic.db.WordbookDao;
import com.chris.mordic.db.WordbookListDao;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.util.List;

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
    private List<String> mDatas;
    private WordbookBean mWordbookBean;

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

        String wordbookName = this.getIntent().getStringExtra("wordbook");
        mWordbookBean = new WordbookListDao(this).getData(wordbookName);
        mDatas = new WordbookDao(this).getAllWords(wordbookName);
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
                return mDatas.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view==object;
            }
            @Override
            public Object instantiateItem(ViewGroup container, final int position) {

                //                TextView textView = new TextView(MainActivity.this);
                //                textView.setText("第"+position+"页");
                //                textView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                //                textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                //                textView.setTextSize(50);
                //                container.addView(textView);
                View item_word = View.inflate(WordActivity.this,R.layout.item_word,null);
                //ButterKnife.inject(item_word);
                TextView tv_wordIndex = (TextView) item_word.findViewById(R.id.tv_wordIndex);
                tv_wordIndex.setText(""+mWordbookBean.getIndex_disordered()+"/"+mWordbookBean.getSum());
                TextView tv_word = (TextView) item_word.findViewById(R.id.tv_word);
                tv_word.setText(mDatas.get(position));
                final TextView tv_translation = (TextView) item_word.findViewById(R.id.translation);
                tv_translation.setText("点击查看释义");
                tv_translation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        byte[] beanByte = new WordDao(WordActivity.this).getBean(mDatas.get(position));
                        //反序列化,将该对象恢复(存储到字节数组)
                        try {
                            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(beanByte));
                            WordBean w = (WordBean) ois.readObject();
                            String trans = "英:[" +w.symbols.get(0).ph_en+"] 美:["+w.symbols.get(0).ph_am+"]\n";
                            List<WordBean.Part> parts = w.symbols.get(0).parts;
                            for(WordBean.Part part : parts){
                                trans=trans+part.part+"  "+part.means.toString()+"\n";
                            }
                            tv_translation.setText(trans);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
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
