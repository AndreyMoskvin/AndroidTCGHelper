package com.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;
import com.cards.database.CardGenerator;
import com.cards.database.CardsDBOperator;

import java.io.IOException;
import java.io.InputStream;
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
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);
    }

    private void generateDatabase(){
        Toast.makeText(this, "Adding cards to database...", Toast.LENGTH_SHORT).show();
        try {
            CardGenerator cardGenerator = new CardGenerator(getAssets().open("MyDatabase.csv"));
            List cards = cardGenerator.generateCards();

            mApplication.getDatabaseOperator().addCards(cards);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void databaseGenerationFinished(Boolean success, int itemsAdded) {
        Toast.makeText(this, "Added " + itemsAdded + "cards!", Toast.LENGTH_SHORT).show();
    }
}
