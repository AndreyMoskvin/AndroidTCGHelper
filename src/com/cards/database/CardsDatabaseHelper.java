package com.cards.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import com.adapters.CardInfoSource;
import com.views.Refreshable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/10/12
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class CardsDatabaseHelper extends SQLiteOpenHelper{

    public static final String KEY_COST = "cost";
    public static final String KEY_TYPE = "type";
    public static final String KEY_SET = "from_set";
    public static final String KEY_RARITY = "rarity";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TCGCardsDatabase";
    private static final String TABLE_NAME = "cards";
    private static final String FTS_TABLE_NAME = "fts_cards";
    private static final String SELECT_ALL_QUERY = "SELECT  * FROM " + TABLE_NAME;

    private FillDatabaseTask mFillDatabaseTask;
    private DatabaseFetcher mDatabaseFetcher;
    private SqlColumnsGenerator mSqlColumnsGenerator;
    private InputStream mCsvFileStream;

    private Cursor mCurrentCursor;

    private CardInfoSource mCardInfoSource = new CardInfoSource() {
        @Override
        public void setCurrentPosition(int position) {
            mCurrentCursor.moveToPosition(position);
        }

        @Override
        public int getId() {
            return mCurrentCursor.getInt(0);
        }

        @Override
        public String getName() {
            return mCurrentCursor.getString(1);
        }

        @Override
        public String getType() {
            return mCurrentCursor.getString(7);
        }

        @Override

        public String getCost() {
            return mCurrentCursor.getString(8);
        }

        @Override
        public String getNumber() {
            return mCurrentCursor.getString(6);
        }

        @Override
        public int getCardsCount() {
            return mCurrentCursor.getCount();
        }
    };

    public CardInfoSource getCardInfoSource() {
        return mCardInfoSource;
    }

    private Refreshable mRefreshableView;
    public void setRefreshableView(Refreshable mRefreshableView) {
        this.mRefreshableView = mRefreshableView;
    }

    private CardsGenerator mCardGenerator;
    private FilterBuilder mFilterBuilder;

    private OnDatabaseGenerationFinished mDBGenerationFinishedListener;

    private static final String FILTER_ALL= "All";

    private Cursor mTypesCursor;
    public Cursor getCardTypes(){
        return mTypesCursor;
    }

    private Cursor mCostsCursor;
    public Cursor getCardCosts(){
        return mCostsCursor;
    }

    private Cursor mSetsCursor;
    public Cursor getCardSets(){
        return mSetsCursor;
    }

    private Cursor mRarityCursor;
    public Cursor getCardRarity(){
        return mRarityCursor;
    }

    public void setOnDatabaseGenerationFinished(OnDatabaseGenerationFinished onDatabaseGenerationFinished){
        mDBGenerationFinishedListener = onDatabaseGenerationFinished;
    }

    private void initSqlColumnsGenerator(){
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(SELECT_ALL_QUERY, null);
        ArrayList<String> columns = new ArrayList<String>();
        if (cursor != null && cursor.getColumnCount() > 0) {
            Collections.addAll(columns, cursor.getColumnNames());
        }

        if (!columns.isEmpty()) mSqlColumnsGenerator = new SqlColumnsGenerator(columns);
    }

    public CardsDatabaseHelper(Context context,  InputStream sourceStream) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mCsvFileStream = sourceStream;
        mFillDatabaseTask = new FillDatabaseTask();
        mDatabaseFetcher = new DatabaseFetcher();
        mFilterBuilder = new FilterBuilder();
        initSqlColumnsGenerator();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        mCardGenerator = new CardsGenerator(mCsvFileStream);
        mSqlColumnsGenerator =  new SqlColumnsGenerator(mCardGenerator.getParser().readParameters());
        mCardGenerator.setCardParameters(mSqlColumnsGenerator.getListOfSqlColumns());

        String CREATE_CARDS_TABLE = "CREATE  TABLE " + TABLE_NAME + "(" + mSqlColumnsGenerator.getSqlFields()+ ")";
        sqLiteDatabase.execSQL(CREATE_CARDS_TABLE);

        String CREATE_FTS_CARDS_TABLE = "CREATE VIRTUAL TABLE " + FTS_TABLE_NAME + " USING fts3(" + mSqlColumnsGenerator.getFTSColumns() + ");";
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
                Cursor cursor = database.rawQuery(query, null);
                mCurrentCursor = cursor;
                if (mRefreshableView != null) mRefreshableView.refreshAdapter();
            }
        });
    }

    private void performFetchWithQueryParameters(final String tableName, final String[] fetchColumns, final String query, final String[] fetchValues){
        if (mRefreshableView != null) mRefreshableView.beginRefreshingActivity();
        mDatabaseFetcher.runFetch(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase database = getReadableDatabase();
                Cursor cursor = database.query(tableName, fetchColumns, query, fetchValues, null, null, null, null);
                mCurrentCursor = cursor;
                if (mRefreshableView != null) mRefreshableView.refreshAdapter();
            }
        });
    }

    private Cursor extractFilterValuesForColumn(SQLiteDatabase database, final String column){
        return database.rawQuery("SELECT DISTINCT " + column + " FROM " + TABLE_NAME, null);
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

    public void resetCards(){
        mFilterBuilder.reset();
        performRawFetchWithQuery(SELECT_ALL_QUERY);
    }

    public void addFilter(String key, String value){
        mFilterBuilder.addFilters(key, value);
        mFilterBuilder.build();
    }
    public void resetFilters(){
        mFilterBuilder.reset();
    }

    public void applyFilters() {
        if (!mFilterBuilder.isEmpty()){
            performFetchWithQueryParameters(TABLE_NAME, mSqlColumnsGenerator.getArrayOfSqlColumns(), mFilterBuilder.getFilterString(), mFilterBuilder.getSqlFilterValuesArray());
        }
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
            if (!value.equals(FILTER_ALL)){
                mFilters.put(key, value);
            } else {
                mFilters.remove(key);
            }
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
            performFetchWithQueryParameters(FTS_TABLE_NAME, mSqlColumnsGenerator.getArrayOfSqlColumns(), FTS_TABLE_NAME + " MATCH ? ", new String[]{appendWildcard(query)});
    }

    public static interface OnDatabaseGenerationFinished {
        void databaseGenerationFinished(Boolean success, int itemsAdded);
    }
}
