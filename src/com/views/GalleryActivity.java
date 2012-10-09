package com.views;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.io.InputStream;

import static android.graphics.BitmapFactory.decodeStream;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/9/12
 * Time: 11:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class GalleryActivity extends Activity {

    private ViewPager mPager;
    private ImageLoader mImageLoader = ImageLoader.getInstance();
    private String[] mCardImages;

    private DisplayImageOptions mOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory()
                .build();

        try {
           mCardImages = getAssets().list("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(new GalleryPagerAdapter());
    }

    @Override
    protected void onStop() {
        mImageLoader.stop();
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gallery_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return super.onMenuItemSelected(featureId, item);
    }

    private class GalleryPagerAdapter extends PagerAdapter {
        private LayoutInflater mInflater;

        private Bitmap getBitmapFromAsset(String strName) throws IOException
        {
            AssetManager assetManager = getAssets();

            InputStream istr = assetManager.open(strName);

            return decodeStream(istr);
        }

        private GalleryPagerAdapter() {
            mInflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return mCardImages.length;
        }

        @Override
        public void startUpdate(View view) {
        }

        @Override
        public Object instantiateItem(View view, int i) {
            final View imageLayout = mInflater.inflate(R.layout.gallery_image_item, null);
            final ImageView imageView = (ImageView)imageLayout.findViewById(R.id.cardImage);

            String currentImage = mCardImages[i];
            try {
                imageView.setImageBitmap(getBitmapFromAsset(currentImage));
            } catch (IOException e) {
                e.printStackTrace();
            }

            ((ViewPager) view).addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public void destroyItem(View view, int i, Object o) {
            ((ViewPager) view).removeView((View) o);
        }

        @Override
        public void finishUpdate(View view) {
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view.equals(o);
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void restoreState(Parcelable parcelable, ClassLoader classLoader) {
        }
    }
}
