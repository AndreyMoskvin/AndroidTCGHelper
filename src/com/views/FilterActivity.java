package com.views;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.cards.database.CardsDatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/15/12
 * Time: 11:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class FilterActivity extends Activity implements AdapterView.OnItemSelectedListener{

    public final static String FILTERS = "filters";

    private HashMap<String, Integer> mSelectedFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter);

        mSelectedFilters = (HashMap<String, Integer>) getIntent().getSerializableExtra(FILTERS);

        Cursor types = TCGHelperApplication.getInstance().getDatabaseOperator().getCardTypes();
        Cursor costs = TCGHelperApplication.getInstance().getDatabaseOperator().getCardCosts();
        Cursor sets = TCGHelperApplication.getInstance().getDatabaseOperator().getCardSets();
        Cursor rarity = TCGHelperApplication.getInstance().getDatabaseOperator().getCardRarity();

        Spinner typeSpinner = (Spinner)findViewById(R.id.filterTypeSpinner);
        typeSpinner.setTag(CardsDatabaseHelper.KEY_TYPE);

        FilterAdapter typesAdapter = new FilterAdapter(types);
        typeSpinner.setAdapter(typesAdapter);
        typeSpinner.setSelection(mSelectedFilters.get(CardsDatabaseHelper.KEY_TYPE), true);
        typeSpinner.setOnItemSelectedListener(this);

        Spinner costSpinner = (Spinner)findViewById(R.id.filterCostSpinner);
        costSpinner.setTag(CardsDatabaseHelper.KEY_COST);

        FilterAdapter costAdapter = new FilterAdapter(costs);
        costSpinner.setAdapter(costAdapter);
        costSpinner.setSelection(mSelectedFilters.get(CardsDatabaseHelper.KEY_COST));
        costSpinner.setOnItemSelectedListener(this);

        Spinner setSpinner = (Spinner)findViewById(R.id.filterSetSpinner);
        setSpinner.setTag(CardsDatabaseHelper.KEY_SET);

        FilterAdapter setsAdapter = new FilterAdapter(sets);
        setSpinner.setAdapter(setsAdapter);
        setSpinner.setSelection(mSelectedFilters.get(CardsDatabaseHelper.KEY_SET));
        setSpinner.setOnItemSelectedListener(this);

        Spinner raritySpinner = (Spinner)findViewById(R.id.filterRaritySpinner);
        raritySpinner.setTag(CardsDatabaseHelper.KEY_RARITY);

        FilterAdapter rarityAdapter = new FilterAdapter(rarity);
        raritySpinner.setAdapter(rarityAdapter);
        raritySpinner.setSelection(mSelectedFilters.get(CardsDatabaseHelper.KEY_RARITY));
        raritySpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedFilter = (String) adapterView.getItemAtPosition(i);
        String filterType = (String) adapterView.getTag();

        mSelectedFilters.put(filterType, i);
        getIntent().putExtra(FILTERS, mSelectedFilters);

        TCGHelperApplication.getInstance().getDatabaseOperator().addFilter(filterType, selectedFilter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onStop() {
        setResult(Activity.RESULT_CANCELED);
        super.onStop();
    }

    public void saveFilterSettings(View view){
        setResult(Activity.RESULT_OK, getIntent());
        finish();
    }

    private class FilterAdapter extends BaseAdapter {
        private Cursor mCursor;
        private ArrayList<String> mFilters;

        private class ViewHolder {
            public TextView text;
        }

        private FilterAdapter(Cursor mCursor) {
            this.mCursor = mCursor;
            mFilters = new ArrayList<String>();
            mFilters.add("All");
            this.mCursor.moveToFirst();
            do{
                mFilters.add(mCursor.getString(0));
            } while (this.mCursor.moveToNext());
        }

        @Override
        public int getCount() {
            return mFilters.size();
        }

        @Override
        public Object getItem(int position) {
            return mFilters.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            final ViewHolder holder;
            if (convertView == null){
                view = getLayoutInflater().inflate(android.R.layout.simple_spinner_item, null);
                holder = new ViewHolder();
                holder.text = (TextView) view.findViewById(android.R.id.text1);
                holder.text.setTextSize(20.0f);
                view.setTag(holder);
            } else {
                holder = (ViewHolder)view.getTag();
            }

            holder.text.setText(mFilters.get(position));

            return view;
        }
    }
}
