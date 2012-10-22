package com.cards.database;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/10/12
 * Time: 4:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class CardsGenerator {
    private CardsParser mParser;
    private ArrayList<String> mCardParameters;
    public void setCardParameters(ArrayList<String> mCardParameters) {
        this.mCardParameters = mCardParameters;
    }

    public CardsGenerator(InputStream stream) {
        try {
            mParser = new CardsParser(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public CardsParser getParser() {
        return mParser;
    }

    public List<Card> generateCards(){
        try {
            List<String[]>parsedCards = mParser.readAllCards();

            // removing map and stub row
            parsedCards.remove(0);
            parsedCards.remove(0);

            ArrayList<Card> cards = new ArrayList<Card>();
            for (String[] cardString : parsedCards) {
                if (cardString.length > 0) {
                    Card aCard = new Card(mCardParameters, cardString);
                    cards.add(aCard);
                }
            }
            return cards;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
