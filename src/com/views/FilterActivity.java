package com.views;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.cards.database.CardsDatabaseHelper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/15/12
 * Time: 11:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class FilterActivity extends Activity implements AdapterView.OnItemSelectedListener{
    private List<String> mTypes;
    private List<String> mCosts;
    private List<String> mSets;
    private List<String> mRarity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter);

        mTypes = TCGHelperApplication.getInstance().getDatabaseOperator().getCardTypes();
        mCosts = TCGHelperApplication.getInstance().getDatabaseOperator().getCardCosts();
        mSets = TCGHelperApplication.getInstance().getDatabaseOperator().getCardSets();
        mRarity = TCGHelperApplication.getInstance().getDatabaseOperator().getCardRarity();

        Spinner typeSpinner = (Spinner)findViewById(R.id.filterTypeSpinner);
        typeSpinner.setTag(CardsDatabaseHelper.KEY_TYPE);
        typeSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mTypes.toArray(new String[mTypes.size()]));
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        Spinner costSpinner = (Spinner)findViewById(R.id.filterCostSpinner);
        costSpinner.setTag(CardsDatabaseHelper.KEY_COST);
        costSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> costAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mCosts.toArray(new String[mCosts.size()]));
        costAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        costSpinner.setAdapter(costAdapter);

        Spinner setSpinner = (Spinner)findViewById(R.id.filterSetSpinner);
        setSpinner.setTag(CardsDatabaseHelper.KEY_SET);
        setSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> setAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mSets.toArray(new String[mSets.size()]));
        setAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setSpinner.setAdapter(setAdapter);

        Spinner raritySpinner = (Spinner)findViewById(R.id.filterRaritySpinner);
        raritySpinner.setTag(CardsDatabaseHelper.KEY_RARITY);
        raritySpinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> rarityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mRarity.toArray(new String[mRarity.size()]));
        rarityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

    public void saveFilterSettings(View view){
        finish();
    }
}
