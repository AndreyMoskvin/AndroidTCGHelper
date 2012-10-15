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
    private static final String KEY_ID = "id";
    private ArrayList<String> mParameters = new ArrayList<String>();

    CardsParser(InputStream stream) throws FileNotFoundException {
        mReader = new CSVReader(new InputStreamReader(stream), ';');
        try {
            mParameters.add(KEY_ID);

            String[] rawTypes = mReader.readNext();
            for (int i = 0; i < rawTypes.length; i++){
                rawTypes[i] = rawTypes[i].toLowerCase();
                if (rawTypes[i].equals("set")) rawTypes[i] = "from_set";
            }
            Collections.addAll(mParameters,rawTypes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String[]> readCards() throws IOException {
        return mReader.readAll();
    }
    
    public String[] getParameters(){
        return mParameters.toArray(new String[mParameters.size()]);
    }

    public String getSqlFields(){
        String result = KEY_ID + " INTEGER PRIMARY KEY,";
        String sqlType = " TEXT,";
        for (int i=1; i< mParameters.size(); i++) {
            if (i == mParameters.size() - 1) {
                sqlType = " TEXT";
            }
            result = result + mParameters.get(i) + sqlType;
        }
        return result;
    }
}
