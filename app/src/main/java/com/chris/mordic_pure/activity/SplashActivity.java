package com.chris.mordic_pure.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.RelativeLayout;

import com.chris.mordic_pure.R;
import com.chris.mordic_pure.utils.LogUtils;
import com.umeng.analytics.MobclickAgent;

public class SplashActivity extends Activity {

/*    private ImageView mIv_spinner;*/
    private AnimationSet mAnimationSet;
    AlphaAnimation mAlphaAnimation;
    private RelativeLayout mRl_splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        LogUtils.v("start");
        initView();
        initData();
        initAnimation();
        initEvent();
    }

    private void initEvent() {
        mAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                LogUtils.v("finish");
                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void initAnimation() {
        mAlphaAnimation = new AlphaAnimation(0.5f,1.0f);
        mAlphaAnimation.setDuration(500);
        mAlphaAnimation.setFillAfter(true);
        mAnimationSet = new AnimationSet(false);
        mAnimationSet.addAnimation(mAlphaAnimation);
        mRl_splash.startAnimation(mAnimationSet);
    }


    private void initData() {



    }

    private void initView() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_splash);
/*        mIv_spinner = (ImageView) findViewById(R.id.iv_splash_spinner);*/
        mRl_splash = (RelativeLayout) findViewById(R.id.rl_splash);
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
