# EasyBanner
这是一个基于Android简单封装的轮播banner工程。
EasyBanner使用了Rxjava来控制banner的滑动。


使用方法：
```
mBannerPager.init(new HolderCreator<NetworkImageHolderView>() {
                         @Override
                         public NetworkImageHolderView createHolder() {
                             return new NetworkImageHolderView();
                         }
                    },mImgs)
                    .setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            MallGoodsDetailActivity.start(getActivity());
                        }
                    })
                    .setBannerStyle(EasyBanner.BANNER_STYLE_NO_TEXT)
                    .setPageIndicator(new int[]{R.drawable.dot_ic,R.drawable.dot_current_ic})
                    .setPageIndicatorAlign(EasyBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
                    .setSwitchTime(3000);

```

这个工程完成度不高，如果要使用的话建议大家可以根据自己的需求自己修改。
