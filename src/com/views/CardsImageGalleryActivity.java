package com.views;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/26/12
 * Time: 9:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class CardsImageGalleryActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_detail_image);
        String imageLoadUrl= (String) getIntent().getSerializableExtra(getString(R.string.cardImageUrlKey));
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.cardImageLoadingProgressBar);
        final ImageView cardImage = (ImageView) findViewById(R.id.cardDetailImageView);

        ImageLoader.getInstance().displayImage(imageLoadUrl, cardImage, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(FailReason failReason) {
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingComplete(Bitmap bitmap) {
                cardImage.setImageBitmap(bitmap);
                cardImage.setVisibility(View.VISIBLE);

                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingCancelled() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        ImageLoader.getInstance().stop();
    }
}
