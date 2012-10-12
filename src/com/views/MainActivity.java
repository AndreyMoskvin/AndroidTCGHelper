package com.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.cards.database.CardsDBOperator;

public class MainActivity extends Activity implements CardsDBOperator.Callback {

    private TCGHelperApplication mApplication = TCGHelperApplication.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getActionBar().hide();

        mApplication.initializeDatabaseWithCallbackHandler(this);

        if (!mApplication.getDatabaseOperator().hasData()) {
            generateDatabase();
        }
    }

    public void openActivity(View view){
        Intent intent = new Intent(this, CardGalleryActivity.class);
        startActivity(intent);
    }

    public void openListActivity(View view){
        Intent intent = new Intent(this, CardsListActivity.class);
        startActivity(intent);
    }

    private void generateDatabase(){
        Toast.makeText(this, "Adding cards to database...", Toast.LENGTH_SHORT).show();
        mApplication.getDatabaseOperator().addCards();
    }

    public void databaseGenerationFinished(Boolean success, int itemsAdded) {
        Toast.makeText(this, "Added " + itemsAdded + " cards!", Toast.LENGTH_SHORT).show();
    }
}
