package com.juhuacha.easybanner.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;


import com.juhuacha.easybanner.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class EasyBanner<T> extends LinearLayout {

    /*banner*/
    ViewPager mBannerPager;
    /*指示器的点*/
    LinearLayout mNaviDots;
    /*banner的提示信息*/
    TextView mTextInfo;
    /*视图*/
    View mBaseView;
    /*底部指示器点imageview*/
    private List<ImageView> mPointViews = new ArrayList<ImageView>();


    /*数据源*/
    private List<T> mDatas;
    /*viewpager的adapter*/
    private EasyBannerBaseAdapter                   mBannerAdapter;
    /*banner不显示文字*/
    public static final int                         BANNER_STYLE_NO_TEXT = 0;
    /*banner显示文字*/
    public static final int                         BANNER_STYLE_WITH_TEXT = 1;
    /*pageIndicator的背景样式*/
    private int[]                                   mPageIndicatorId;
    /*Banner是否显示Text*/
    private int                                     mBannerStyle;
    /*Text显示的文字*/
    private String mTextStr;
    /*ViewPager loop的订阅*/
    private Subscription                            mBannerSuscribe;
    /*滑动速度*/
    private static final int                        M_SCROLL_DURATION = 1200;
    /*banner切换时间(miliseconds)*/
    private int                                     mSwitchTime = 3000;
    /*easybanner封装的页面切换监听器*/
    private EasyBannerOnPageChangeListener          mOnPageChangeListener;
    /*viewpager切换*/
    private ViewPager.OnPageChangeListener          onPageChangeListener;
    /*Item点击事件*/
    private OnItemClickListener                     mOnItemClickListener;


    /*底部指示器位置*/
    public enum PageIndicatorAlign{
        ALIGN_PARENT_LEFT,ALIGN_PARENT_RIGHT,CENTER_HORIZONTAL
    }


    public EasyBanner(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }


    public EasyBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);

    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr){
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.EasyBanner);
        mBannerStyle = a.getInt(R.styleable.EasyBanner_bannerStyle,BANNER_STYLE_NO_TEXT);
        a.recycle();
        /*初始化view*/
        mBaseView = LayoutInflater.from(context).inflate(R.layout.easybanner_contentview,this,true);
        mBannerPager = (ViewPager) mBaseView.findViewById(R.id.viewpager);
        mNaviDots = (LinearLayout) mBaseView.findViewById(R.id.llDot);
        mTextInfo = (TextView) mBaseView.findViewById(R.id.tvRollInfo);
        /*自定义ViewPager滑动速度*/
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            scrollerField.set(mBannerPager, new ViewPagerScroller(context));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置TextView显示的文字
     * @param info
     * @return
     */
    public EasyBanner setTextInfo(@NonNull String info){
        mTextStr = info;
        if(mTextInfo != null){
            mTextInfo.setText(info);
        }
        return this;
    }

    /**
     * 设置页面角标指示器的的资源
     * @param pageIndicatorId
     * @return
     */
    public EasyBanner setPageIndicatorRecource(@NonNull int[] pageIndicatorId){
        mPageIndicatorId = pageIndicatorId;
        return this;
    }
    /**
     * 自定义翻页动画效果
     *
     * @param transformer
     * @return
     */
    public EasyBanner setPageTransformer(ViewPager.PageTransformer transformer) {
        if(mBannerPager != null) {
            mBannerPager.setPageTransformer(true, transformer);
        }
        return this;
    }
    /**
     * 设置Banner的样式
     * @param style
     * @return
     */
    public EasyBanner setBannerStyle(@NonNull int style){
        if(style != BANNER_STYLE_NO_TEXT && style != BANNER_STYLE_WITH_TEXT){
            mBannerStyle = BANNER_STYLE_NO_TEXT;
        }else{
            mBannerStyle = style;
        }
        if(mBannerStyle == BANNER_STYLE_NO_TEXT){
            mTextInfo.setVisibility(GONE);
        }else{
            mTextInfo.setVisibility(VISIBLE);
        }
        return this;
    }

    public EasyBanner setSwitchTime(@NonNull int miliseconds){
        if(miliseconds <= 0){
            mSwitchTime = 3000;
        }else{
            mSwitchTime = miliseconds;
        }
        return this;
    }
    /**
     * Viewpager自动轮播
     */
    private void startAutoLoop() {
        if (mBannerSuscribe == null || mBannerSuscribe.isUnsubscribed()) {
            mBannerSuscribe = Observable.interval(3000, mSwitchTime, TimeUnit.MILLISECONDS)
                    //延时3000 ，每间隔3000，时间单位
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            if(mBannerPager == null ||  mBannerPager.getAdapter() == null)
                                return;
                            int currentIndex = mBannerPager.getCurrentItem();

                            if (++currentIndex ==  mBannerPager.getAdapter().getCount()) {
                                mBannerPager.setCurrentItem(0);
                            } else {
                                mBannerPager.setCurrentItem(currentIndex, true);
                            }
                        }
                    });
        }
    }

    /**
     * ViewPager停止轮播
     */
    private void stopAutoLoop() {
        if (mBannerSuscribe != null && !mBannerSuscribe.isUnsubscribed()) {
            mBannerSuscribe.unsubscribe();
        }
    }


    /**
     *  触碰控件的时候，翻页应该停止，离开的时候如果之前是开启了翻页的话则重新启动翻页
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP||action == MotionEvent.ACTION_CANCEL||action == MotionEvent.ACTION_OUTSIDE) {
            // 开始翻页
            startAutoLoop();
        } else if (action == MotionEvent.ACTION_DOWN) {
            // 停止翻页
            stopAutoLoop();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //stopAutoLoop();
    }

    /**
     * 初始化EasyBanner 需首先调用，否则设置监听可能无效
     * @param holderCreator
     * @param datas
     * @return
     */
    public EasyBanner init(HolderCreator holderCreator, List<T> datas){
        this.mDatas = datas;
        mBannerAdapter = new EasyBannerBaseAdapter(holderCreator,mDatas);
        mBannerPager.setAdapter(mBannerAdapter);
        mBannerAdapter.setViewPager(mBannerPager);
        startAutoLoop();
        if (mPageIndicatorId != null)
            setPageIndicator(mPageIndicatorId);
        return this;
    }

    /**
     * 获取当前的页面index
     */
    public int getCurrentItem(){
        if (mBannerPager!=null || mDatas != null) {
            return mBannerPager.getCurrentItem() % mDatas.size();
        }
        return -1;
    }

    /**
     * 设置当前的页面index
     * @param index
     */
    public void setcurrentitem(int index){
        if (mBannerPager!=null) {
            mBannerPager.setCurrentItem(index);
        }
    }
    /**
     * 设置翻页监听器
     * @param onPageChangeListener
     * @return
     */
    public EasyBanner setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
        //如果有默认的监听器（即是使用了默认的翻页指示器）则把用户设置的依附到默认的上面，否则就直接设置
        if(mOnPageChangeListener != null){
            mOnPageChangeListener.setOnPageChangeListener(onPageChangeListener);
        }
        else mBannerPager.addOnPageChangeListener(onPageChangeListener);
        return this;
    }
    public EasyBanner setOnItemClickListener(@NonNull OnItemClickListener onItemClickListener){

        this.mOnItemClickListener = onItemClickListener;
        if(mBannerAdapter != null){
            mBannerAdapter.setOnItemClickListener(mOnItemClickListener);
        }
        return this;
    }
    /**
     * 底部指示器资源图片
     *
     * @param page_indicatorId
     */
    public EasyBanner setPageIndicator(int[] page_indicatorId) {
        mNaviDots.removeAllViews();
        mPointViews.clear();
        this.mPageIndicatorId = page_indicatorId;
        if(mDatas==null)return this;
        for (int count = 0; count < mDatas.size(); count++) {
            // 翻页指示的点
            ImageView pointView = new ImageView(getContext());
            pointView.setPadding(5, 0, 5, 0);
            if (mPointViews.isEmpty())
                pointView.setImageResource(page_indicatorId[1]);
            else
                pointView.setImageResource(page_indicatorId[0]);
            mPointViews.add(pointView);
            mNaviDots.addView(pointView);
        }
        mOnPageChangeListener = new EasyBannerOnPageChangeListener(mPointViews,
                mPageIndicatorId);
        mBannerPager.addOnPageChangeListener(mOnPageChangeListener);
        mOnPageChangeListener.onPageSelected(mDatas.size());
        if(onPageChangeListener != null){
            mOnPageChangeListener.setOnPageChangeListener(onPageChangeListener);
        }

        return this;
    }

    /**
     * 指示器的方向
     * @param align  三个方向：居左 （RelativeLayout.ALIGN_PARENT_LEFT），居中 （RelativeLayout.CENTER_HORIZONTAL），居右 （RelativeLayout.ALIGN_PARENT_RIGHT）
     * @return
     */
    public EasyBanner setPageIndicatorAlign(PageIndicatorAlign align) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mNaviDots.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, align == PageIndicatorAlign.ALIGN_PARENT_LEFT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, align == PageIndicatorAlign.ALIGN_PARENT_RIGHT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, align == PageIndicatorAlign.CENTER_HORIZONTAL ? RelativeLayout.TRUE : 0);
        mNaviDots.setLayoutParams(layoutParams);
        return this;
    }

    /**
     * 自定义Scroller，用于调节ViewPager滑动速度
     */
    class ViewPagerScroller extends Scroller {

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
            super.startScroll(startX, startY, dx, dy, M_SCROLL_DURATION);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, M_SCROLL_DURATION);
        }
    }



}
