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

    public static final String KEY_NAME = "name";
    public static final String KEY_COST = "cost";
    public static final String KEY_TYPE = "type";

    private ArrayList<Attribute> mAttributes;

    public Card(ArrayList<String> types,String[] attributes){
        mAttributes = new ArrayList<Attribute>();

        for (int i=0; i < attributes.length; i++) {
            addAttribute(types.get(i) , attributes[i]);
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

    public String getValueFromAttributeType(String type){
        for (Attribute attribute : mAttributes) {
            if (attribute.getType().equals(type)) {
                return attribute.getValue();
            }
        }
        return null;
    }

    public ContentValues toContentValues(){
        ContentValues values = new ContentValues();

        for (Attribute attribute : mAttributes){
            if (!attribute.getType().equals("id")) {
                values.put(attribute.getType(), attribute.getValue());
            }
        }

        return values;
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
