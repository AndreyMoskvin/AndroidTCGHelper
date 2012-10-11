package com.cards.database;

import android.content.ContentValues;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/10/12
 * Time: 4:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class Card {

    private static final String KEY_ID = "id";
    private static final String KEY_SET = "Set";

    private ArrayList<Attribute> mAttributes;
    private static ArrayList<String> AttributeKeys;

    public static void setAttributeKeys(ArrayList<String> attributeKeys) {
        AttributeKeys = attributeKeys;

        if (AttributeKeys.contains(KEY_SET)){
            int indexOfKeySet = AttributeKeys.indexOf(KEY_SET);
            AttributeKeys.remove(KEY_SET);
            AttributeKeys.add(indexOfKeySet, "from_set");
        }

        //TODO: Invalidate database on changes in this keys
    }

    public static ArrayList<String> getAttributeKeys() {
        return AttributeKeys;
    }

    Card(String[] attributes){
        mAttributes = new ArrayList<Attribute>();

        for (int i=0; i < attributes.length; i++) {
            addAttribute(AttributeKeys.get(i) , attributes[i]);
        }
    }
    public void addAttribute(String type, String value){
        Attribute attribute = new Attribute(type,value);
        if (!mAttributes.contains(attribute)) {
            mAttributes.add(attribute);
        }
        else {
            mAttributes.remove(attribute);
            mAttributes.add(attribute);
        }
    }

    public ArrayList<Attribute> getAttributes() {
        return mAttributes;
    }

    public ContentValues toContentValues(){
        ContentValues values = new ContentValues();

        for (Attribute attribute : mAttributes){
            values.put(attribute.getType(), attribute.getValue());
        }

        return values;
    }

    public static String keyTypeTable() {
        String result = "";
        if (!AttributeKeys.isEmpty()) {
            String sqlType = " TEXT,";
            for (String type : AttributeKeys) {
                if (AttributeKeys.indexOf(type) == AttributeKeys.size() - 1) {
                    sqlType = " TEXT";
                }

                result = result + type.toLowerCase() + sqlType;
            }
        }
        return result;
    }

    private class Attribute {
        private String mType;
        private String mValue;

        Attribute(String aType, String aValue){
            mType = aType;
            mValue = aValue;
        }

        public String getType() {
            return mType;
        }

        public String getValue() {
            return mValue;
        }
    }
}
