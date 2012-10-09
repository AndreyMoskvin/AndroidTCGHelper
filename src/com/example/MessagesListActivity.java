package com.example;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import com.repository.MessageRepository;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/2/12
 * Time: 11:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class MessagesListActivity extends ListActivity{

    private ArrayAdapter<String> mMessagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setContentView(R.layout.messages_list);

        mMessagesAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mMessagesAdapter.clear();
        mMessagesAdapter.addAll(MessageRepository.getMessages());
        setListAdapter(mMessagesAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();    //To change body of overridden methods use File | Settings | File Templates.

        mMessagesAdapter.clear();
        mMessagesAdapter.addAll(MessageRepository.getMessages());
    }
}
