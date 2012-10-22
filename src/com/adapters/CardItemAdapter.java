package com.adapters;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.views.R;
import com.views.TCGHelperApplication;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/22/12
 * Time: 10:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class CardItemAdapter extends BaseAdapter {

    private Cursor mCursor;
    private LayoutInflater mLayoutInflater;

    public void setCursor(Cursor cursor) {
        this.mCursor = cursor;
        this.notifyDataSetChanged();
    }

    public CardItemAdapter(LayoutInflater inflater) {
        this.mCursor = TCGHelperApplication.getInstance().getDatabaseOperator().getCurrentCursor();
        this.mLayoutInflater = inflater;
    }

    private class ViewHolder {
        public TextView name;
        public TextView cost;
        public TextView type;
        public TextView number;
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public Object getItem(int i) {
        mCursor.moveToPosition(i);
        return mCursor.getInt(0);
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
            convertView = mLayoutInflater.inflate(R.layout.card_list_item, null);
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

        mCursor.moveToPosition(i);

        //TODO:Remove hard-coded values
        holder.name.setText(mCursor.getString(1));//"name"
        holder.cost.setText(mCursor.getString(8));//"cost"
        holder.type.setText(mCursor.getString(7));//"type"
        holder.number.setText(mCursor.getString(6));//"number"

        return convertView;
    }
}
