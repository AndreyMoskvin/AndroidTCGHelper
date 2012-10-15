package com.cards.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/10/12
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class CardsDBOperator extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TCGCardsDatabase";
    private static final String TABLE_NAME = "cards";
    private static final String SELECT_ALL_QUERY = "SELECT  * FROM " + TABLE_NAME;

    private static final String KEY_NAME = "name";
    private static final String KEY_COST = "cost";
    private static final String KEY_TYPE = "type";

    private GenerateDatabaseTask mAddTask;
    private Callback mCallback;
    private CardsGenerator mCardGenerator;
    private ArrayList<Card> mCards = new ArrayList<Card>();

    public CardsDBOperator(Context context, Callback callback, InputStream sourceStream) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mAddTask = new GenerateDatabaseTask();
        mCallback = callback;
        mCardGenerator = new CardsGenerator(sourceStream);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CARDS_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + mCardGenerator.getSqlColumns()+ ")";
        sqLiteDatabase.execSQL(CREATE_CARDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    public void addCards(){
        List<Card> cards = mCardGenerator.generateCards();
        if (cards != null) {
            mAddTask.execute(cards, null, null);
        }
    }

    public boolean hasData(){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(SELECT_ALL_QUERY, null);

        return cursor.getCount() > 0;
    }

    public ArrayList<Card> getAllCards(){
        ArrayList<Card> cardArrayList = new ArrayList<Card>();

        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(SELECT_ALL_QUERY, null);

        if (cursor.moveToFirst()) {
            do{
                int columns = cursor.getColumnCount();
                String[] fields = new String[columns];
                for (int i=0; i < columns; i++) {
                    fields[i] = cursor.getString(i);
                }
                Card card = new Card(mCardGenerator.getCardParameters(), fields);
                cardArrayList.add(card);
            }while (cursor.moveToNext());
        }
        mCards = cardArrayList;
        return mCards;
    }

    private ArrayList<Card> getCardsByKey(String sqlColumn, String type){
        ArrayList<Card> cardArrayList = new ArrayList<Card>();

        SQLiteDatabase database = getReadableDatabase();

        String[] cardParams = mCardGenerator.getStringCardParameters();

        Cursor cursor = database.query(TABLE_NAME, cardParams, sqlColumn + " = ?", new String[] { type }, null, null, null, null);
        if (cursor.moveToFirst()) {
            do{
                int columns = cursor.getColumnCount();
                String[] fields = new String[columns];
                for (int i=0; i < columns; i++) {
                    fields[i] = cursor.getString(i);
                }
                Card card = new Card(mCardGenerator.getCardParameters(), fields);
                cardArrayList.add(card);
            }while (cursor.moveToNext());
        }

        mCards = cardArrayList;
        return mCards;
    }

    public ArrayList<Card> getAllyCards(){
        return getCardsByKey(KEY_TYPE, "Ally");
    }

    public ArrayList<Card> getQuestCards(){
        return getCardsByKey(KEY_TYPE, "Quest");
    }

    public ArrayList<Card> getEquipmentCards(){
        return getCardsByKey(KEY_TYPE, "Equipment");
    }

    public ArrayList<Card> getAbilityCards(){
        return getCardsByKey(KEY_TYPE, "Ability");
    }

    private class GenerateDatabaseTask extends AsyncTask<List<Card>,Void,Void> {
        private  int mAddedCardsCount;
        @Override
        protected Void doInBackground(List<Card>... lists) {
            List<Card> cards = lists[0];

            SQLiteDatabase database = getWritableDatabase();

            for (Card card : cards) {
                ContentValues values = card.toContentValues();
                database.insert(TABLE_NAME, null, values);
            }
            database.close();

            mAddedCardsCount = cards.size();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(mCallback != null) {
                mCallback.databaseGenerationFinished(true, mAddedCardsCount);
            }
            super.onPostExecute(aVoid);
        }
    }
    public static interface Callback{
        void databaseGenerationFinished(Boolean success, int itemsAdded);
    }
}
