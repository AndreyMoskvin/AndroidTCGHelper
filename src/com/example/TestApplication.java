package com.example;

import android.app.Application;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.repository.MessagesOpenHelper;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/5/12
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestApplication extends Application
{
    private static TestApplication mInstance;

    private MessagesOpenHelper mHelper;

    public static TestApplication getInstance(){
        return  mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCacheSize(1500000) // 1.5 Mb
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .enableLogging() // Not necessary in common
                .build();
        ImageLoader.getInstance().init(config);

        mHelper = new MessagesOpenHelper(this);
    }

    public MessagesOpenHelper getHelper() {
        return mHelper;
    }
}
