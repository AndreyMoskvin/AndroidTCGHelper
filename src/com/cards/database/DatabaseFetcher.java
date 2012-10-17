package com.cards.database;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/17/12
 * Time: 9:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseFetcher{

    private ExecutorService mPool;

    public DatabaseFetcher() {
        this.mPool = Executors.newFixedThreadPool(2);
    }

    public void runFetch(Runnable runnable){
        mPool.submit(runnable);
    }
}
