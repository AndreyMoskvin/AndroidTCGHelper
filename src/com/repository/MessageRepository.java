package com.repository;

import com.example.TestApplication;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: andrey.moskvin
 * Date: 10/2/12
 * Time: 1:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class MessageRepository {
    private static MessageRepository ourInstance = new MessageRepository();

    public static MessageRepository getInstance() {
        return ourInstance;
    }

    public void addMessage(String message)
    {
        TestApplication.getInstance().getHelper().addMessage(message);
    }

    public static ArrayList<String> getMessages() {
        return TestApplication.getInstance().getHelper().getAllMessages();
    }
}
