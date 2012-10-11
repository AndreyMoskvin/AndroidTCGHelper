package com.cards.database;

import au.com.bytecode.opencsv.CSVReader;

import java.io.*;
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

    public List<String[]> allCards() throws IOException {
        return mReader.readAll();
    }
}
