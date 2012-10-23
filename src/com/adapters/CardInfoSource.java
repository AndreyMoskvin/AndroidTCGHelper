package com.adapters;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/23/12
 * Time: 9:39 AM
 * To change this template use File | Settings | File Templates.
 */
public interface CardInfoSource {
    public void setCurrentPosition(int position);
    public int getId();
    public String getName();
    public String getType();
    public String getCost();
    public String getNumber();
    public int getCardsCount();
}
