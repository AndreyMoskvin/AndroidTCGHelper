package com.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.cards.database.CardsGenerator;
import com.cards.database.CardsDBOperator;

import java.io.IOException;
import java.util.List;

public class MainActivity extends Activity implements CardsDBOperator.Callback {

    private tcgHelperApplication mApplication = tcgHelperApplication.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getActionBar().hide();

        mApplication.initializeDatabaseWithCallbackHandler(this);

        if (!mApplication.getDatabaseOperator().databaseIsEmpty()) {
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
        try {
            CardsGenerator cardsGenerator = new CardsGenerator(getAssets().open("MyDatabase.csv"));
            List cards = cardsGenerator.generateCards();

            mApplication.getDatabaseOperator().addCards(cards);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void databaseGenerationFinished(Boolean success, int itemsAdded) {
        Toast.makeText(this, "Added " + itemsAdded + " cards!", Toast.LENGTH_SHORT).show();
    }
}
