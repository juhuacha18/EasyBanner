package com.juhuacha.easybanner.library;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;


import com.juhuacha.easybanner.R;

import java.util.List;


public class EasyBannerBaseAdapter <T> extends PagerAdapter {

    /*数据*/
    public List<T> mDatas;
    /*ViewCreator*/
    protected HolderCreator                 holderCreator;
    /*ViewPager*/
    private ViewPager mViewPager;
    /*banner的item点击事件*/
    private OnItemClickListener             mOnItemClickListener;

    public EasyBannerBaseAdapter(HolderCreator holderCreator, List<T> datas) {
        this.holderCreator = holderCreator;
        this.mDatas = datas;
    }

    /*设置item点击事件监听*/
    public void setOnItemClickListener(@NonNull OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return mDatas != null ? Integer.MAX_VALUE : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public int toRealPosition(int position) {
        int realCount = getRealCount();
        if (realCount == 0)
            return 0;
        int realPosition = position % realCount;
        return realPosition;
    }

    public int getRealCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public void setViewPager(@NonNull ViewPager viewPager){
        this.mViewPager = viewPager;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        int realPosition = toRealPosition(position);
        View view = getView(realPosition, null, container);
        if(mOnItemClickListener != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(toRealPosition(position));
                }
            });
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if(mViewPager == null || mDatas == null)
            return;
        int position = mViewPager.getCurrentItem();
        if (position == 0) {
            position = mDatas.size();
        } else if (position == getCount() - 1) {
            position = mDatas.size() - 1;
        }
        try {
            mViewPager.setCurrentItem(position, false);
        }catch (IllegalStateException e){}
    }

    public View getView(int position, View view, ViewGroup container) {
        Holder holder = null;
        if (view == null) {
            holder = (Holder) holderCreator.createHolder();
            view = holder.createView(container.getContext());
            view.setTag(R.id.item_tag, holder);
        } else {
            holder = (Holder<T>) view.getTag(R.id.item_tag);
        }
        if (mDatas != null && !mDatas.isEmpty())
            holder.UpdateUI(container.getContext(), position, mDatas.get(position));
        return view;
    }

     /**/
}
