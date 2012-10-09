package com.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.repository.MessageRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {
    public static final String IMAGE_URLS_FILES_KEY =  "IMAGE_URLS";
    private TextView mUserTextView;

    private void showNextActivity() {
        Intent intent = new Intent(this, MessagesListActivity.class);
        startActivity(intent);
    }
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mUserTextView = (TextView)findViewById(R.id.userTextEntrance);
    }

    public void openActivity(View view){
        String mUserText = mUserTextView.getText().toString();
        MessageRepository.getInstance().addMessage(mUserText);
        showNextActivity();
    }
}
