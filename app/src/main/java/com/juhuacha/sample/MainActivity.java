package com.juhuacha.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.juhuacha.easybanner.library.EasyBanner;
import com.juhuacha.easybanner.library.HolderCreator;
import com.juhuacha.easybanner.library.NetworkImageHolderView;
import com.juhuacha.easybanner.library.OnItemClickListener;
import com.juhuacha.sample.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "easybanner";

    private EasyBanner<String> mBannerPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBannerPager = (EasyBanner<String>) findViewById(R.id.vpGallery);

        List<String> mImgs = new ArrayList<>();

        mImgs.add("http://dealer2.autoimg.cn/dealerdfs/g19/M10/37/4A/620x0_1_q87_autohomedealer__wKjBxFawFGmAXY3GAACeT_BdGUQ658.jpg");
        mImgs.add("http://img.heibaimanhua.com/wp-content/uploads/2015/06/20150628_558f641a2b020.jpg");
        mImgs.add("http://imgsrc.baidu.com/forum/w%3D580/sign=b9c133669658d109c4e3a9bae159ccd0/f669d6628535e5dd8384f5c772c6a7efcf1b62ad.jpg");


        mBannerPager.init(new HolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        },mImgs)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Log.i(TAG,"click : "+position);
                    }
                })
                .setBannerStyle(EasyBanner.BANNER_STYLE_NO_TEXT)
                .setPageIndicator(new int[]{R.drawable.dot_ic,R.drawable.dot_current_ic})
                .setPageIndicatorAlign(EasyBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
                .setSwitchTime(3000);
    }
}
