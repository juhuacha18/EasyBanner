package com.juhuacha.easybanner.library;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import java.util.List;


public class EasyBannerOnPageChangeListener implements ViewPager.OnPageChangeListener {

    /*指示器点view*/
    private List<ImageView> mDotViews;
    /*指示器点resource*/
    private int []                                      mIndicateResource;
    /*viewpager切换监听器*/
    private ViewPager.OnPageChangeListener              mOnPageChangeListener;


    public EasyBannerOnPageChangeListener(List<ImageView> mDotViews, int[] mIndicateResource) {
        this.mDotViews = mDotViews;
        this.mIndicateResource = mIndicateResource;
    }

    public void setOnPageChangeListener(@NonNull ViewPager.OnPageChangeListener onPageChangeListener){
        this.mOnPageChangeListener = onPageChangeListener;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(mOnPageChangeListener != null){
            mOnPageChangeListener.onPageScrolled(position % mDotViews.size(),positionOffset,positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < mDotViews.size(); i++) {
            mDotViews.get(position % mDotViews.size()).setImageResource(mIndicateResource[1]);
            if (position% mDotViews.size() != i) {
                mDotViews.get(i).setImageResource(mIndicateResource[0]);
            }
        }
        if(mOnPageChangeListener != null){
            mOnPageChangeListener.onPageSelected(position % mDotViews.size());
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(mOnPageChangeListener != null){
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
    }
}
