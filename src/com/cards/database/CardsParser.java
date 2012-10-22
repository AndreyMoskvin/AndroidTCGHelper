package com.cards.database;

import au.com.bytecode.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/10/12
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class CardsParser {

    private CSVReader mReader;

    CardsParser(InputStream stream) throws FileNotFoundException {
        mReader = new CSVReader(new InputStreamReader(stream), ';');
    }

    public ArrayList<String> readParameters(){
        ArrayList<String> result = new ArrayList<String>();
        try {
            Collections.addAll(result, mReader.readNext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<String[]> readAllCards() throws IOException {
        return mReader.readAll();
    }
}
