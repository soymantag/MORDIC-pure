package com.chris.mordic.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.chris.mordic.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chris on 7/14/16.
 * Email: soymantag@163.coom
 */
public class WordActivity extends Activity{
    @InjectView(R.id.vp_word)
    ViewPager mV_word;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        mV_word.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 0;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view==object;
            }
        });
    }

}
