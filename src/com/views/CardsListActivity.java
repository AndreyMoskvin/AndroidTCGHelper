package com.views;

import android.app.Activity;
import android.os.Bundle;
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
        getActionBar().hide();

        mCardArrayList = TCGHelperApplication.getInstance().getDatabaseOperator().getAllCards();

        ListView listView = (ListView)findViewById(R.id.cardListView);
        listView.setAdapter(new CardItemAdapter());
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

            holder.name.setText(mCardArrayList.get(i).getValueFromAttributeType(Card.KEY_NAME));
            holder.cost.setText(mCardArrayList.get(i).getValueFromAttributeType(Card.KEY_COST));
            holder.type.setText(mCardArrayList.get(i).getValueFromAttributeType(Card.KEY_TYPE));

            return convertView;
        }
    }
}
