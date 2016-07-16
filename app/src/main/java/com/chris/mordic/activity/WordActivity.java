package com.chris.mordic.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.ImageView;
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
    @InjectView(R.id.arrow_pre)
    ImageView iv_arrow;
    @InjectView(R.id.iv_shift)
    ImageView iv_shift;
    @InjectView(R.id.tv_title)
    TextView tv_title;
    private List<String> mDatas;
    private WordbookBean mWordbookBean;
    private String mWordbookName;
    private int mWordsState=UNLEARNED;
    public static final int UNLEARNED=1<<0;
    public static final int RECITE=1<<1;
    public static final int LEARNED=1<<2;
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

        mWordbookName = this.getIntent().getStringExtra("wordbook");
        mWordbookBean = new WordbookListDao(this).getData(mWordbookName);
        mDatas = new WordbookDao(this).getAllWords(mWordbookName);
        //mV_word.setOffscreenPageLimit(3);
        mV_word.setPageMargin(10);
        iv_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
/*        mRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mV_word.dispatchTouchEvent(event);
            }
        });*/
        PagerAdapter pagerAdapter =new MyPagerAdapter();
        mV_word.setAdapter(pagerAdapter);
        //这个是设置viewPager切换过度时间的类
        ViewPagerScroller scroller = new ViewPagerScroller(this);
        scroller.setScrollDuration(0);
        scroller.initViewPagerScroll(mV_word);  //这个是设置切换过渡时间为0毫秒
        iv_shift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mWordsState==UNLEARNED){
                    mDatas = new WordbookDao(WordActivity.this).getUnlearnedWords(mWordbookName);
                    PagerAdapter pagerAdapter =new MyPagerAdapter();
                    mV_word.setAdapter(pagerAdapter);
                    tv_title.setText("待复习");
                    mWordsState=RECITE;
                }else if (mWordsState==RECITE){
                    mDatas = new WordbookDao(WordActivity.this).getReciteWords(mWordbookName);
                    PagerAdapter pagerAdapter =new MyPagerAdapter();
                    mV_word.setAdapter(pagerAdapter);
                    tv_title.setText("已掌握");
                    mWordsState=LEARNED;
                }else {
                    mDatas = new WordbookDao(WordActivity.this).getAllWords(mWordbookName);
                    PagerAdapter pagerAdapter =new MyPagerAdapter();
                    mV_word.setAdapter(pagerAdapter);
                    mWordsState=UNLEARNED;
                }
            }
        });
    }
    private class MyPagerAdapter extends PagerAdapter{

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

            View item_word = View.inflate(WordActivity.this,R.layout.item_word,null);
            //ButterKnife.inject(item_word);
            TextView tv_wordIndex = (TextView) item_word.findViewById(R.id.tv_wordIndex);
            tv_wordIndex.setText(""+(position+1)+"/"+mDatas.size());
            TextView tv_word = (TextView) item_word.findViewById(R.id.tv_word);
            tv_word.setText(mDatas.get(position));
            final TextView tv_translation = (TextView) item_word.findViewById(R.id.translation);
            final TextView tv_pronounce = (TextView) item_word.findViewById(R.id.tv_pronounce);
            tv_translation.setText("点击查看释义");
            tv_translation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    byte[] beanByte = new WordDao(WordActivity.this).getBean(mDatas.get(position));
                    //反序列化,将该对象恢复(存储到字节数组)
                    try {
                        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(beanByte));
                        WordBean w = (WordBean) ois.readObject();
                        String pronounce = "英:[" +w.symbols.get(0).ph_en+"]    美:["+w.symbols.get(0).ph_am+"]\n";
                        tv_pronounce.setVisibility(View.VISIBLE);
                        tv_pronounce.setText(pronounce);
                        String trans ="";
                        List<WordBean.Part> parts = w.symbols.get(0).parts;
                        for(WordBean.Part part : parts){
                            trans=trans+part.part+"  "+part.means.toString()+"\n";
                        }
                        tv_translation.setText(trans);
                        tv_translation.setGravity(Gravity.NO_GRAVITY);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
            final TextView mtv_learned = (TextView) item_word.findViewById(R.id.learned);
            final WordbookDao wordbookDao = new WordbookDao(WordActivity.this);
            if(wordbookDao.getLearnState(mWordbookName,mDatas.get(position)).equals("unlearned")){
                mtv_learned.setTextColor(getResources().getColor(R.color.lightGrey));
            }
            mtv_learned.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(wordbookDao.getLearnState(mWordbookName,mDatas.get(position)).equals("unlearned")){
                        mtv_learned.setTextColor(getResources().getColor(R.color.lightGrey));
                        wordbookDao.update(mWordbookName,mDatas.get(position),"recite");
                    }else {
                        mtv_learned.setTextColor(getResources().getColor(R.color.kingblue));
                        wordbookDao.update(mWordbookName,mDatas.get(position),"unlearned");
                    }
                }
            });
            container.addView(item_word);
            return item_word;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

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
