package com.cards.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import java.io.InputStream;
import java.util.*;

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

    private FillDatabaseTask mAddTask;
    private Callback mCallback;
    private CardsGenerator mCardGenerator;
    private ArrayList<Card> mCards = new ArrayList<Card>();
    private FilterBuilder mFilterBuilder;

    public static final String KEY_NAME = "name";
    public static final String KEY_COST = "cost";
    public static final String KEY_TYPE = "type";
    public static final String KEY_SET = "from_set";
    public static final String KEY_RARITY = "rarity";

    private static final String FILTER_ALL= "All";
    private ArrayList<String> mCardTypes;
    private ArrayList<String> mCardCosts;
    private ArrayList<String> mCardSets;
    private ArrayList<String> mCardRarity;

    public CardsDBOperator(Context context, Callback callback, InputStream sourceStream) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mAddTask = new FillDatabaseTask();
        mCallback = callback;
        mCardGenerator = new CardsGenerator(sourceStream);
        mFilterBuilder = new FilterBuilder();
        mFilterBuilder.build();
        mCardTypes = new ArrayList<String>();
        mCardCosts = new ArrayList<String>();
        mCardSets = new ArrayList<String>();
        mCardRarity = new ArrayList<String>();
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

    private ArrayList<Card>cardsFromCursor(Cursor cursor){
        ArrayList<Card> cardArrayList = new ArrayList<Card>();

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

        return cardArrayList;
    }

    public ArrayList<Card> getCards(){
        if(!mFilterBuilder.isEmpty()){
            SQLiteDatabase database = getReadableDatabase();

            Cursor cursor = database.query(TABLE_NAME, mCardGenerator.getStringCardParameters(), mFilterBuilder.getFilterString(), mFilterBuilder.getSqlFilterValuesArray(), null, null, null, null);

            mCards = cardsFromCursor(cursor);
            return mCards;
        } else {
            return getAllCards();
        }
    }

    private ArrayList<String> getFilterValuesForType(String type){
        Set<String> uniqueTypesSet = new HashSet<String>();
        for (Card card : mCards){
            String value = card.getValueFromAttributeType(type);
            uniqueTypesSet.add(value);
        }

        ArrayList<String> result = new ArrayList<String>();
        result.addAll(uniqueTypesSet);
        result.add(0, FILTER_ALL);

        return result;
    }

    public boolean hasData(){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(SELECT_ALL_QUERY, null);

        return cursor.getCount() > 0;
    }

    private ArrayList<Card> getAllCards(){
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(SELECT_ALL_QUERY, null);

        mCards = cardsFromCursor(cursor);
        return mCards;
    }

    public List<String> getCardTypes(){
        if (mCardTypes.isEmpty()){
            mCardTypes = getFilterValuesForType(KEY_TYPE);
        }
        return mCardTypes;
    }
    public List<String> getCardCosts(){
        if (mCardCosts.isEmpty()){
            mCardCosts = getFilterValuesForType(KEY_COST);
        }
        return mCardCosts;
    }
    public List<String> getCardSets(){
        if (mCardSets.isEmpty()){
            mCardSets = getFilterValuesForType(KEY_SET);
        }
        return mCardSets;
    }
    public List<String> getCardRarity(){
        if (mCardRarity.isEmpty()){
            mCardRarity = getFilterValuesForType(KEY_RARITY);
        }
        return mCardRarity;
    }

    public void addCards(){
        List<Card> cards = mCardGenerator.generateCards();
        if (cards != null) {
            mAddTask.execute(cards, null, null);
        }
    }

    public void addFilter(String key, String value){
        mFilterBuilder.addFilters(key, value);
        mFilterBuilder.build();
    }
    public void resetFilters(){
        mFilterBuilder.reset();
    }

    private class FilterBuilder{
        private HashMap<String, String> mFilters;
        private String mSqlFilterString;

        public Boolean isEmpty(){
            return mFilters.isEmpty();
        }

        public String getFilterString() {
            return mSqlFilterString;
        }

        public String[] getSqlFilterValuesArray(){
            return mFilters.values().toArray(new String[mFilters.size()]);
        }

        private FilterBuilder() {
            mFilters = new HashMap<String, String>();
        }

        public void addFilters(String key, String value){
            if (!value.equals(FILTER_ALL))
                mFilters.put(key, value);
        }

        public void build(){
            mSqlFilterString = SELECT_ALL_QUERY;
            if (!mFilters.isEmpty()){
                StringBuilder builder = new StringBuilder();
                String separator = " AND ";

                builder.append("( ");
                for (String key : mFilters.keySet()){
                    builder.append(key).append(" = ? ").append(separator);
                }
                builder.setLength(builder.length() - separator.length());
                builder.append(" )");
                mSqlFilterString = builder.toString();
            }
        }

        public void reset(){
            mFilters.clear();
        }
    }

    private class FillDatabaseTask extends AsyncTask<List<Card>,Void,Void> {
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
