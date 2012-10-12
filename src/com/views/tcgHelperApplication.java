package com.views;

import android.app.Application;
import com.cards.database.CardsDBOperator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/5/12
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class TCGHelperApplication extends Application
{
    private static TCGHelperApplication mInstance;

    public CardsDBOperator mDatabaseOperator;

    public static TCGHelperApplication getInstance(){
        return  mInstance;
    }

    public CardsDBOperator getDatabaseOperator() {
        return mDatabaseOperator;
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
    public void initializeDatabaseWithCallbackHandler(Object handler){
        try {
            InputStream stream =  getAssets().open("MyDatabase.csv");
            mDatabaseOperator = new CardsDBOperator(this, (CardsDBOperator.Callback) handler, stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
