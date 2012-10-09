package com.views;

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
public class tcgHelperApplication extends Application
{
    private static tcgHelperApplication mInstance;

    public static tcgHelperApplication getInstance(){
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
    }
}
