package com.views;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import com.cards.database.Card;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/12/12
 * Time: 9:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class CardsListActivity extends Activity{

    private CardItemAdapter mCardItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_list);

        ListView listView = (ListView)findViewById(R.id.cardListView);
        mCardItemAdapter = new CardItemAdapter(TCGHelperApplication.getInstance().getDatabaseOperator().getCards());
        listView.setAdapter(mCardItemAdapter);

        handleIntent(getIntent());
    }

    @Override
    protected void onStart() {
        super.onStart();

        refreshAdapter();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
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
                    TCGHelperApplication.getInstance().getDatabaseOperator().searchCardsForQuery(query);
                    refreshAdapter();
                } else {
                    TCGHelperApplication.getInstance().getDatabaseOperator().resetCards();
                    refreshAdapter();
                }

                return true;
            }
        });
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    private void refreshAdapter() {
        mCardItemAdapter.setCardArrayList(TCGHelperApplication.getInstance().getDatabaseOperator().getCards());
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            TCGHelperApplication.getInstance().getDatabaseOperator().searchCardsForQuery(query);
            refreshAdapter();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuFilterOptionsButton:
                TCGHelperApplication.getInstance().getDatabaseOperator().resetFilters();
                Intent intent = new Intent(this, FilterActivity.class);
                startActivity(intent);
                return true;
            case R.id.menuSearchButton:
                TCGHelperApplication.getInstance().getDatabaseOperator().resetCards();
                refreshAdapter();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class CardItemAdapter extends BaseAdapter {

        private ArrayList<Card> mCardArrayList;

        public void setCardArrayList(ArrayList<Card> mCardArrayList) {
            this.mCardArrayList = mCardArrayList;
            this.notifyDataSetChanged();
        }

        private CardItemAdapter(ArrayList<Card> mCardArrayList) {
            this.mCardArrayList = mCardArrayList;
        }

        private class ViewHolder {
            public TextView name;
            public TextView cost;
            public TextView type;
            public TextView number;
        }

        @Override
        public int getCount() {
            return mCardArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return mCardArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View convertView = view;
            final  ViewHolder holder;
            if (view == null) {
                convertView = getLayoutInflater().inflate(R.layout.card_list_item, null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.cardNameLabel);
                holder.cost = (TextView) convertView.findViewById(R.id.cardCostLabel);
                holder.type = (TextView) convertView.findViewById(R.id.cardTypeLabel);
                holder.number = (TextView) convertView.findViewById(R.id.cardNumberLabel);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder)convertView.getTag();
            }

            //TODO:Remove hard-coded values
            holder.name.setText(mCardArrayList.get(i).getValueFromAttributeType("name"));
            holder.cost.setText(mCardArrayList.get(i).getValueFromAttributeType("cost"));
            holder.type.setText(mCardArrayList.get(i).getValueFromAttributeType("type"));
            holder.number.setText(mCardArrayList.get(i).getValueFromAttributeType("number"));

            return convertView;
        }
    }
}
