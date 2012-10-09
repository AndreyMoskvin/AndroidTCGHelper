package com.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/8/12
 * Time: 12:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class MessagesOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MessagesDatabase";
    private static final String MESSAGE_TABLE_NAME = "messages";
    private static final String KEY_MESSAGE  = "message";
    private static final String MESSAGE_TABLE_CREATE =
            "CREATE TABLE " + MESSAGE_TABLE_NAME + " (" +
                    KEY_MESSAGE + " TEXT )";

    public MessagesOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(MESSAGE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE_NAME);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public void addMessage(String message) {
        if (message.length() == 0) {
            return;
        }

        SQLiteDatabase database = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE, message);

        database.insert(MESSAGE_TABLE_NAME, null, values);
        database.close();
    }

    public ArrayList<String> getAllMessages() {

        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(MESSAGE_TABLE_NAME, new String[] {KEY_MESSAGE}, null, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
        }
        else {
            cursor.close();
            database.close();
            return null;
        }
        ArrayList<String> strings = new ArrayList<String>();

        do{
            strings.add(cursor.getString(0));
        } while (cursor.moveToNext());

        return strings;
    }
}
