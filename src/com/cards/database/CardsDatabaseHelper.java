package com.cards.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import com.views.Refreshable;

import java.io.InputStream;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/10/12
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class CardsDatabaseHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TCGCardsDatabase";
    private static final String TABLE_NAME = "cards";
    private static final String FTS_TABLE_NAME = "fts_cards";
    private static final String SELECT_ALL_QUERY = "SELECT  * FROM " + TABLE_NAME;

    private FillDatabaseTask mFillDatabaseTask;
    private DatabaseFetcher mDatabaseFetcher;
    private Cursor mCurrentCursor;

    public Cursor getCurrentCursor(){
        return mCurrentCursor;
    }

    private Refreshable mRefreshableView;

    public void setRefreshableView(Refreshable mRefreshableView) {
        this.mRefreshableView = mRefreshableView;
    }

    private CardsGenerator mCardGenerator;
    private ArrayList<Card> mCards = new ArrayList<Card>();
    private FilterBuilder mFilterBuilder;

    public static final String KEY_COST = "cost";
    public static final String KEY_TYPE = "type";
    public static final String KEY_SET = "from_set";
    public static final String KEY_RARITY = "rarity";

    private OnDatabaseGenerationFinished mDBGenerationFinishedListener;

    private static final String FILTER_ALL= "All";
    private Cursor mTypesCursor;
    private Cursor mCostsCursor;
    private Cursor mSetsCursor;
    private Cursor mRarityCursor;

    public void setOnDatabaseGenerationFinished(OnDatabaseGenerationFinished onDatabaseGenerationFinished){
        mDBGenerationFinishedListener = onDatabaseGenerationFinished;
    }

    public CardsDatabaseHelper(Context context,  InputStream sourceStream) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mFillDatabaseTask = new FillDatabaseTask();
        mDatabaseFetcher = new DatabaseFetcher();
        mCardGenerator = new CardsGenerator(sourceStream);
        mFilterBuilder = new FilterBuilder();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CARDS_TABLE = "CREATE  TABLE " + TABLE_NAME + "(" + mCardGenerator.getSqlColumns()+ ")";
        sqLiteDatabase.execSQL(CREATE_CARDS_TABLE);

        String CREATE_FTS_CARDS_TABLE = "CREATE VIRTUAL TABLE " + FTS_TABLE_NAME + " USING fts3(" + mCardGenerator.getFTSColumns() + ");";
        sqLiteDatabase.execSQL(CREATE_FTS_CARDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + FTS_TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    private void performRawFetchWithQuery(final String query){
        if (mRefreshableView != null) mRefreshableView.beginRefreshingActivity();
        mDatabaseFetcher.runFetch(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase database = getReadableDatabase();
                Cursor cursor = database.rawQuery(query, null, null);
                mCurrentCursor = cursor;
                if (mRefreshableView != null) mRefreshableView.refreshAdapterWithCursor(cursor);
            }
        });
    }

    private void performFetchWithQueryParameters(final String tableName, final String[] fetchColumns, final String query, final String[] fetchValues){
        if (mRefreshableView != null) mRefreshableView.beginRefreshingActivity();
        mDatabaseFetcher.runFetch(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase database = getReadableDatabase();
                Cursor cursor = database.query(tableName, fetchColumns, query, fetchValues, null ,null, null, null);
                mCurrentCursor = cursor;
                if (mRefreshableView != null) mRefreshableView.refreshAdapterWithCursor(cursor);
            }
        });
    }

    private Cursor extractFilterValuesForColumn(SQLiteDatabase database, final String column){
        return database.rawQuery("SELECT DISTINCT " + column + " FROM " + TABLE_NAME, null, null);
    }

    public boolean hasData(){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(SELECT_ALL_QUERY, null);

        return cursor.getCount() > 0;
    }

    public void getAllCards(){
        performRawFetchWithQuery(SELECT_ALL_QUERY);
        mDatabaseFetcher.runFetch(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase database = getReadableDatabase();
                mTypesCursor = extractFilterValuesForColumn(database, KEY_TYPE);
                mCostsCursor = extractFilterValuesForColumn(database, KEY_COST);
                mSetsCursor = extractFilterValuesForColumn(database, KEY_SET);
                mRarityCursor = extractFilterValuesForColumn(database, KEY_RARITY);
            }
        });

    }

    public Cursor getCardTypes(){
        return mTypesCursor;
    }
    public Cursor getCardCosts(){
        return mCostsCursor;
    }
    public Cursor getCardSets(){
        return mSetsCursor;
    }
    public Cursor getCardRarity(){
        return mRarityCursor;
    }

    public void resetCards(){
        mFilterBuilder.reset();
        performRawFetchWithQuery(SELECT_ALL_QUERY);
    }

    public void addFilter(String key, String value){
        mFilterBuilder.addFilters(key, value);
        mFilterBuilder.build();
        if (!mFilterBuilder.isEmpty()){
            mCards.clear();
            performFetchWithQueryParameters(TABLE_NAME, mCardGenerator.getStringCardParameters(), mFilterBuilder.getFilterString(), mFilterBuilder.getSqlFilterValuesArray());
        }
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
            build();
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

    public void addCards(){
        List<Card> cards = mCardGenerator.generateCards();
        if (cards != null) {
            mFillDatabaseTask.execute(cards, null, null);
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
                database.insert(FTS_TABLE_NAME, null, values);
                Log.d("DATABASE", "Inserted card " + values.get("name"));
            }
            database.close();

            mAddedCardsCount = cards.size();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(mDBGenerationFinishedListener != null) {
                mDBGenerationFinishedListener.databaseGenerationFinished(true, mAddedCardsCount);
            }
            super.onPostExecute(aVoid);
        }
    }

    private String appendWildcard(String query) {
        if (TextUtils.isEmpty(query)) return query;

        final StringBuilder builder = new StringBuilder();
        final String[] splits = TextUtils.split(query, " ");

        for (String split : splits)
            builder.append(split).append("*").append(" ");

        return builder.toString().trim();
    }

    public void searchCardsForQuery(String query){
            performFetchWithQueryParameters(FTS_TABLE_NAME, mCardGenerator.getStringCardParameters(), FTS_TABLE_NAME + " MATCH ? ", new String[]{appendWildcard(query)});
    }

    public static interface OnDatabaseGenerationFinished {
        void databaseGenerationFinished(Boolean success, int itemsAdded);
    }
}
