package com.juhuacha.easybanner.library;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.juhuacha.easybanner.R;
import com.squareup.picasso.Picasso;


public class NetworkImageHolderView implements Holder<String> {
    private ImageView imageView;

    @Override
    public View createView(Context context) {
        //你可以通过layout文件来创建
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, int position, String data) {
        imageView.setImageResource(R.drawable.ic_launcher);
        Picasso.with(context)
                .load(data)
                .error(R.drawable.ic_launcher)
                .placeholder(R.drawable.ic_launcher)
                .into(imageView);
    }
}
