package com.views;

import android.database.Cursor;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/16/12
 * Time: 2:11 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Refreshable {
    public void beginRefreshingActivity();
    public void refreshAdapterWithCursor(Cursor cursor);
}
