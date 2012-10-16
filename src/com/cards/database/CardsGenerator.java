package com.cards.database;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
public class CardsGenerator {
    private CardsParser mParser;
    private String mSqlColumns;
    private ArrayList<String> mCardParameters = new ArrayList<String>();
    private ArrayList<String> mCardParametersWithoutId;

    public CardsGenerator(InputStream stream) {
        try {
            mParser = new CardsParser(stream);
            mSqlColumns = mParser.getSqlFields();
            Collections.addAll(mCardParameters, mParser.getParameters());

            mCardParametersWithoutId = (ArrayList<String>) mCardParameters.clone();
            mCardParametersWithoutId.remove("id");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getSqlColumns(){
        return mSqlColumns;
    }

    public ArrayList<String> getCardParameters(){
        return mCardParameters;
    }

    public String[] getStringCardParameters(){
        return mCardParameters.toArray(new String[mCardParameters.size()]);
    }

    public List<Card> generateCards(){
        try {
            List<String[]>parsedCards = mParser.readCards();

            // removing map and stub row
            parsedCards.remove(0);
            parsedCards.remove(0);

            ArrayList<Card> cards = new ArrayList<Card>();
            for (String[] cardString : parsedCards) {
                if (cardString.length > 0) {
                    Card aCard = new Card(mCardParametersWithoutId, cardString);
                    cards.add(aCard);
                }
            }
            return cards;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getFTSColumns(){
        String separator = ", ";
        StringBuilder builder = new StringBuilder();
        for (String parameter : mCardParameters){
            builder.append(parameter).append(separator);
        }
        builder.setLength(builder.length() - separator.length());
        return builder.toString();
    }
}
