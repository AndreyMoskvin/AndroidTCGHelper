package com.views;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.repository.MessageRepository;

public class MainActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void openActivity(View view){
    }
}
