package com.chris.mordic_pure.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.chris.mordic_pure.R;
import com.chris.mordic_pure.data.WordBean;
import com.chris.mordic_pure.data.WordbookBean;
import com.chris.mordic_pure.db.WordDao;
import com.chris.mordic_pure.db.WordbookDao;
import com.chris.mordic_pure.db.WordbookListDao;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

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
    @InjectView(R.id.iv_popup)
    ImageView tv_popup;
    @InjectView(R.id.space)
    TextView tv_space;
    private List<String> mDatas;
    private WordbookBean mWordbookBean;
    private String mWordbookName;
    private int mWordsState=ALL;
    public static final int UNLEARNED=1<<0;
    public static final int RECITE=1<<1;
    public static final int LEARNED=1<<2;
    public static final int ALL=1<<3;
    private PopupWindow pw;
    private ScaleAnimation sa;
    private View mContentView;
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
        initPopupWindow();
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
        tv_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow();
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
    }
    private void initPopupWindow() {
        mContentView = View.inflate(getApplicationContext(), R.layout.popup,null);
        // 手动添加
        TextView tv_shoudong = (TextView) mContentView
                .findViewById(R.id.tv_popup_all);
        // 联系人添加
        TextView tv_contact = (TextView) mContentView
                .findViewById(R.id.tv_popup_unlearned);
        // 电话添加
        TextView tv_phonelog = (TextView) mContentView
                .findViewById(R.id.tv_popup_recite);
        // 短信添加
        TextView tv_smslog = (TextView) mContentView
                .findViewById(R.id.tv_popup_learned);
        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PagerAdapter pagerAdapter =null;
                switch (v.getId()) {
                    case R.id.tv_popup_unlearned:
                        mWordsState=UNLEARNED;
                        System.out.println("tv_popup_unlearned");
                        mDatas = new WordbookDao(WordActivity.this).getUnlearnedWords(mWordbookName);
                        if(mDatas.size()==0){
                            tv_space.setVisibility(View.VISIBLE);
                        }else {
                            tv_space.setVisibility(View.GONE);
                        }
                        pagerAdapter =new MyPagerAdapter();
                        mV_word.setAdapter(pagerAdapter);
                        tv_title.setText("未学习");
                        break;
                    case R.id.tv_popup_recite:
                        mWordsState=RECITE;
                        System.out.println("tv_popup_recite");
                        mDatas = new WordbookDao(WordActivity.this).getReciteWords(mWordbookName);
                        if(mDatas.size()==0){
                            tv_space.setVisibility(View.VISIBLE);
                        }else {
                            tv_space.setVisibility(View.GONE);
                        }
                        pagerAdapter =new MyPagerAdapter();
                        mV_word.setAdapter(pagerAdapter);
                        tv_title.setText("待复习");
                        break;
                    case R.id.tv_popup_all:
                        mWordsState=ALL;
                        System.out.println("tv_popup_all");
                        mDatas = new WordbookDao(WordActivity.this).getAllWords(mWordbookName);
                        if(mDatas.size()==0){
                            tv_space.setVisibility(View.VISIBLE);
                        }else {
                            tv_space.setVisibility(View.GONE);
                        }
                        pagerAdapter =new MyPagerAdapter();
                        mV_word.setAdapter(pagerAdapter);
                        tv_title.setText("所有单词");
                        break;
                    case R.id.tv_popup_learned:
                        mWordsState=LEARNED;
                        System.out.println("tv_popup_learned");
                        mDatas = new WordbookDao(WordActivity.this).getLearnedWords(mWordbookName);
                        if(mDatas.size()==0){
                            tv_space.setVisibility(View.VISIBLE);
                        }else {
                            tv_space.setVisibility(View.GONE);
                        }
                        pagerAdapter =new MyPagerAdapter();
                        mV_word.setAdapter(pagerAdapter);
                        tv_title.setText("已掌握");
                        break;

                    default:
                        break;
                }

                // 关闭popupwindow
                closePopupWindow();
            }
        };
        // 给四个组件添加事件
        tv_smslog.setOnClickListener(listener);
        tv_contact.setOnClickListener(listener);
        tv_phonelog.setOnClickListener(listener);
        tv_shoudong.setOnClickListener(listener);

        // 弹出窗体
        pw = new PopupWindow(mContentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // 显示动画要有背景
        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // 窗体显示的动画
        sa = new ScaleAnimation(1, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0f);
        sa.setDuration(200);
    }
    private void showPopupWindow() {
        if (pw != null && pw.isShowing()) {
            pw.dismiss();// 关闭
        } else {
            int[] location = new int[2];
            // 获取添加按钮的坐标
            tv_popup.getLocationInWindow(location);

            // 显示动画
            mContentView.startAnimation(sa);
            // 设置右上角对齐
            pw.showAtLocation(tv_popup, Gravity.RIGHT | Gravity.TOP,
                    location[0]
                            - (getWindowManager().getDefaultDisplay()
                            .getWidth() - tv_popup.getWidth()),
                    location[1] + tv_popup.getHeight());

        }
    }
    private void closePopupWindow() {
        if (pw != null && pw.isShowing()) {
            pw.dismiss();// 关闭
        }
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

            switch (mWordsState){
                case ALL:
                    mtv_learned.setVisibility(View.GONE);
                    break;
                case UNLEARNED:
                    mtv_learned.setVisibility(View.VISIBLE);
                    mtv_learned.setText("移至待复习");
                    if(wordbookDao.getLearnState(mWordbookName,mDatas.get(position)).equals("recite")){
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
                    break;
                case RECITE:
                    mtv_learned.setVisibility(View.VISIBLE);
                    mtv_learned.setText("移至已掌握");
                    if(wordbookDao.getLearnState(mWordbookName,mDatas.get(position)).equals("learned")){
                        mtv_learned.setTextColor(getResources().getColor(R.color.lightGrey));
                    }
                    mtv_learned.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("RECITE onclick");
                            if(wordbookDao.getLearnState(mWordbookName,mDatas.get(position)).equals("recite")){
                                mtv_learned.setTextColor(getResources().getColor(R.color.lightGrey));
                                wordbookDao.update(mWordbookName,mDatas.get(position),"learned");
                            }else {
                                mtv_learned.setTextColor(getResources().getColor(R.color.kingblue));
                                wordbookDao.update(mWordbookName,mDatas.get(position),"recite");
                            }
                        }
                    });
                    break;
                case LEARNED:
                    mtv_learned.setVisibility(View.VISIBLE);
                    mtv_learned.setText("移至待复习");
                    if(wordbookDao.getLearnState(mWordbookName,mDatas.get(position)).equals("recite")){
                        mtv_learned.setTextColor(getResources().getColor(R.color.lightGrey));
                    }
                    mtv_learned.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(wordbookDao.getLearnState(mWordbookName,mDatas.get(position)).equals("learned")){
                                mtv_learned.setTextColor(getResources().getColor(R.color.lightGrey));
                                wordbookDao.update(mWordbookName,mDatas.get(position),"recite");
                            }else {
                                mtv_learned.setTextColor(getResources().getColor(R.color.kingblue));
                                wordbookDao.update(mWordbookName,mDatas.get(position),"learned");
                            }
                        }
                    });
                    break;
            }
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
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


}
