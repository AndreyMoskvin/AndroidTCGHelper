package com.views;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.adapters.CardItemAdapter;
import com.cards.database.CardsDatabaseHelper;
import com.devspark.collapsiblesearchmenu.CollapsibleMenuUtils;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/12/12
 * Time: 9:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class CardsListActivity extends SherlockActivity implements Refreshable{

    private CardItemAdapter mCardItemAdapter;
    private ProgressBar mProgress;
    private HashMap<String, Integer> mFilters;

    @Override
    public void refreshAdapter() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCardItemAdapter.notifyDataSetChanged();
                mProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void beginRefreshingActivity() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_list);

        mProgress = (ProgressBar)findViewById(R.id.listProgressBar);
        if (mFilters == null){
            mFilters = new HashMap<String, Integer>();
            mFilters.put(CardsDatabaseHelper.KEY_TYPE, 0);
            mFilters.put(CardsDatabaseHelper.KEY_COST, 0);
            mFilters.put(CardsDatabaseHelper.KEY_SET, 0);
            mFilters.put(CardsDatabaseHelper.KEY_RARITY, 0);
        }

        TCGHelperApplication.getInstance().getDatabaseOperator().setRefreshableView(this);

        ListView listView = (ListView)findViewById(R.id.cardListView);
        mCardItemAdapter = new CardItemAdapter(getLayoutInflater());
        listView.setAdapter(mCardItemAdapter);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void updateWithQuery(String query){
        TCGHelperApplication.getInstance().getDatabaseOperator().searchCardsForQuery(query);
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, com.actionbarsherlock.view.Menu menu) {
        getSupportMenuInflater().inflate(R.menu.list_menu, menu);

        CollapsibleMenuUtils.addSearchMenuItem(menu, false, new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {
                if (!sequence.toString().isEmpty()) {
                    updateWithQuery(sequence.toString());
                } else {
                    TCGHelperApplication.getInstance().getDatabaseOperator().resetCards();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return super.onCreatePanelMenu(featureId, menu);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            updateWithQuery(query);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuFilterOptionsButton:
                Intent intent = new Intent(this, FilterActivity.class);
                intent.putExtra(FilterActivity.FILTERS, mFilters);
                startActivityForResult(intent, 10);
                overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                return true;
            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            mFilters = (HashMap<String, Integer>) data.getSerializableExtra(FilterActivity.FILTERS);
        }
    }
}
