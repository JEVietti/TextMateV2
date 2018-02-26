package com.textmate.dovaj.textmate.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SentimentScore {

    @SerializedName("SentimentStruct")
    @Expose
    private SentimentStruct sentimentStruct;
    @SerializedName("Score")
    @Expose
    private Double score;
    @SerializedName("BestMessage")
    @Expose
    private String bestMessage;
    @SerializedName("WorstMessage")
    @Expose
    private String worstMessage;

    public SentimentStruct getSentimentStruct() {
        return sentimentStruct;
    }

    public void setSentimentStruct(SentimentStruct sentimentStruct) {
        this.sentimentStruct = sentimentStruct;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getBestMessage() {
        return bestMessage;
    }

    public void setBestMessage(String bestMessage) {
        this.bestMessage = bestMessage;
    }

    public String getWorstMessage() {
        return worstMessage;
    }

    public void setWorstMessage(String worstMessage) {
        this.worstMessage = worstMessage;
    }

}