package com.cards.database;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/10/12
 * Time: 4:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class CardGenerator {
    private CardsParser mParser;

    public CardGenerator(InputStream stream) {
        try {
            mParser = new CardsParser(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<Card> generateCards(){
        try {
            List<String[]>parsedCards = mParser.allCards();

            // In csv file zero element contains types of fields for following cards
            String[] keyStrings = parsedCards.get(0);
            ArrayList<String> keys = new ArrayList<String>();
            Collections.addAll(keys, keyStrings);
            Card.setAttributeKeys(keys);

            // removing map and stub row
            parsedCards.remove(0);
            parsedCards.remove(0);

            ArrayList<Card> cards = new ArrayList<Card>();
            for (String[] cardString : parsedCards) {
                if (cardString.length > 0) {
                    Card aCard = new Card(cardString);
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
