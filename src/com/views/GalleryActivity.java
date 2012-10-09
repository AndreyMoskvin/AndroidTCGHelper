package com.views;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewParent;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;

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

    private DisplayImageOptions mOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory()
                .build();

        AssetManager manager = getAssets();
        String[] assets = null;
        try {
           assets = manager.list("");
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

    private class GalleryPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void startUpdate(View view) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Object instantiateItem(View view, int i) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void destroyItem(View view, int i, Object o) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void finishUpdate(View view) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Parcelable saveState() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void restoreState(Parcelable parcelable, ClassLoader classLoader) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
