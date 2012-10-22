package com.views;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import com.adapters.CardItemAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/12/12
 * Time: 9:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class CardsListActivity extends Activity implements Refreshable{

    private CardItemAdapter mCardItemAdapter;
    private ProgressBar mProgress;

    @Override
    public void refreshAdapterWithCursor(final Cursor cursor) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCardItemAdapter.setCursor(cursor);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menuSearchButton).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (!query.isEmpty()) {
                    updateWithQuery(query);
                } else {
                    TCGHelperApplication.getInstance().getDatabaseOperator().resetCards();
                }
                return true;
            }
        });
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
               if (!hasFocus){
                    TCGHelperApplication.getInstance().getDatabaseOperator().resetCards();
                }
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            updateWithQuery(query);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuFilterOptionsButton:
                Intent intent = new Intent(this, FilterActivity.class);
                startActivity(intent);
                return true;
            case R.id.menuSearchButton:
//                refreshAdapter();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
