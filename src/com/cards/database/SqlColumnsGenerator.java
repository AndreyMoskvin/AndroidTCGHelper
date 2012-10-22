package com.cards.database;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/22/12
 * Time: 3:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class SqlColumnsGenerator {
    private static final String ID_COLUMN = "id";

    private ArrayList<String> mParameters;

    public SqlColumnsGenerator(ArrayList<String> mParameters) {
        this.mParameters = new ArrayList<String>(mParameters.size());
        for (String parameter : mParameters){
            if (parameter.equalsIgnoreCase("set")) parameter = "from_set";
            this.mParameters.add(parameter.toLowerCase());
        }
    }

    public String getSqlFields(){
        StringBuilder builder = new StringBuilder();
        builder.append(ID_COLUMN).append(" INTEGER PRIMARY KEY, ");
        String sqlType = " TEXT,";
        for (int i=0; i< mParameters.size(); i++) {
            if (i == mParameters.size() - 1) {
                sqlType = " TEXT";
            }
            builder.append(mParameters.get(i)).append(sqlType);
        }
        return builder.toString();
    }

    public String[] getArrayOfSqlColumns(){
        return mParameters.toArray(new String[mParameters.size()]);
    }

    public ArrayList<String> getListOfSqlColumns(){
         return mParameters;
    }

    public String getFTSColumns(){
        String separator = ", ";
        StringBuilder builder = new StringBuilder();
        builder.append(ID_COLUMN).append(separator);
        for (String parameter : mParameters){
            builder.append(parameter).append(separator);
        }
        builder.setLength(builder.length() - separator.length());
        return builder.toString();
    }
}
