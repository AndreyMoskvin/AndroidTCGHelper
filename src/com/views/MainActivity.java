package com.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockActivity;
import com.cards.database.CardsDatabaseHelper;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends SherlockActivity {

    private TCGHelperApplication mApplication = TCGHelperApplication.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getSupportActionBar().hide();

        final Button showCardsList = (Button)findViewById(R.id.showCards);

        InputStream stream;
        try {
            stream = getAssets().open("csv/MyDatabase.csv");
            mApplication.setCardsDatabaseHelper(new CardsDatabaseHelper(this, stream));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mApplication.getDatabaseOperator().setOnDatabaseGenerationFinished(new CardsDatabaseHelper.OnDatabaseGenerationFinished() {
            @Override
            public void databaseGenerationFinished(Boolean success, int itemsAdded) {
                TCGHelperApplication.getInstance().getDatabaseOperator().getAllCards();
                showCardsList.setVisibility(View.VISIBLE);
                Toast.makeText(mApplication, "Added " + itemsAdded + " cards!", Toast.LENGTH_SHORT).show();
            }
        });

        if (!mApplication.getDatabaseOperator().hasData()) {
            generateDatabase();
        }
        else {
            showCardsList.setVisibility(View.VISIBLE);
        }

        TCGHelperApplication.getInstance().getDatabaseOperator().getAllCards();
    }

    public void openActivity(View view){
        Intent intent = new Intent(this, CardGalleryActivity.class);
        startActivity(intent);
    }

    public void openListActivity(View view){
        Intent intent = new Intent(this, CardsListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade, R.anim.hold);
    }

    private void generateDatabase(){
        Toast.makeText(mApplication, "Adding cards to database...", Toast.LENGTH_SHORT).show();
        mApplication.getDatabaseOperator().addCards();
    }
}
