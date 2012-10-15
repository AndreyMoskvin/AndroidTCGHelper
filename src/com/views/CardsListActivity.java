package com.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
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
public class CardsListActivity extends Activity {

    private ArrayList<Card> mCardArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_list);

        mCardArrayList = TCGHelperApplication.getInstance().getDatabaseOperator().getAllyCards();

        ListView listView = (ListView)findViewById(R.id.cardListView);
        listView.setAdapter(new CardItemAdapter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuFilterOptionsButton:
                Intent intent = new Intent(this, FilterActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class CardItemAdapter extends BaseAdapter {

        private class ViewHolder {
            public TextView name;
            public TextView cost;
            public TextView type;
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
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.name.setText(mCardArrayList.get(i).getValueFromAttributeType("name"));
            holder.cost.setText(mCardArrayList.get(i).getValueFromAttributeType("cost"));
            holder.type.setText(mCardArrayList.get(i).getValueFromAttributeType("type"));

            return convertView;
        }
    }
}
