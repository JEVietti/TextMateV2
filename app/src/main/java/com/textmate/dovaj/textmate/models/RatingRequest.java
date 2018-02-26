package com.textmate.dovaj.textmate.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dovaj on 1/16/2018.
 */

public class RatingRequest {

    @SerializedName("Count")
    int messageCount;
    @SerializedName("Input")
    ArrayList<String> messageList;

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public ArrayList<String> getMessageList() {
        return messageList;
    }

    public void setMessageList(ArrayList<String> messageList) {
        this.messageList = messageList;
    }
    //Don't forget to create/generate the getter and setter
}