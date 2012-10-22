package com.views;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.cards.database.CardsDatabaseHelper;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/15/12
 * Time: 11:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class FilterActivity extends Activity implements AdapterView.OnItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter);

        Cursor types = TCGHelperApplication.getInstance().getDatabaseOperator().getCardTypes();
        Cursor costs = TCGHelperApplication.getInstance().getDatabaseOperator().getCardCosts();
        Cursor sets = TCGHelperApplication.getInstance().getDatabaseOperator().getCardSets();
        Cursor rarity = TCGHelperApplication.getInstance().getDatabaseOperator().getCardRarity();

        Spinner typeSpinner = (Spinner)findViewById(R.id.filterTypeSpinner);
        typeSpinner.setTag(CardsDatabaseHelper.KEY_TYPE);
        typeSpinner.setOnItemSelectedListener(this);

        FilterAdapter typesAdapter = new FilterAdapter(types);
        typeSpinner.setAdapter(typesAdapter);

        Spinner costSpinner = (Spinner)findViewById(R.id.filterCostSpinner);
        costSpinner.setTag(CardsDatabaseHelper.KEY_COST);
        costSpinner.setOnItemSelectedListener(this);

        FilterAdapter costAdapter = new FilterAdapter(costs);
        costSpinner.setAdapter(costAdapter);

        Spinner setSpinner = (Spinner)findViewById(R.id.filterSetSpinner);
        setSpinner.setTag(CardsDatabaseHelper.KEY_SET);
        setSpinner.setOnItemSelectedListener(this);

        FilterAdapter setsAdapter = new FilterAdapter(sets);
        setSpinner.setAdapter(setsAdapter);

        Spinner raritySpinner = (Spinner)findViewById(R.id.filterRaritySpinner);
        raritySpinner.setTag(CardsDatabaseHelper.KEY_RARITY);
        raritySpinner.setOnItemSelectedListener(this);

        FilterAdapter rarityAdapter = new FilterAdapter(rarity);
        raritySpinner.setAdapter(rarityAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedFilter = (String) adapterView.getItemAtPosition(i);
        String filterType = (String) adapterView.getTag();

        TCGHelperApplication.getInstance().getDatabaseOperator().addFilter(filterType, selectedFilter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
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

        private class ViewHolder {
            public TextView text;
        }

        private FilterAdapter(Cursor mCursor) {
            this.mCursor = mCursor;
        }

        @Override
        public int getCount() {
            return mCursor.getCount();
        }

        @Override
        public Object getItem(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getString(0);
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

            mCursor.moveToPosition(position);

            holder.text.setText(mCursor.getString(0));

            return view;
        }
    }
}
