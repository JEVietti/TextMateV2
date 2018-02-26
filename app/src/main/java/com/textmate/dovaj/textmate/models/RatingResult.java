package com.textmate.dovaj.textmate.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.textmate.dovaj.textmate.interfaces.BaseResponse;

public class RatingResult extends BaseResponse{

    @SerializedName("score")
    @Expose
    private Double score;
    @SerializedName("length")
    @Expose
    private Integer length;
    @SerializedName("sentimentScore")
    @Expose
    private SentimentScore sentimentScore;

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public SentimentScore getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(SentimentScore sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

}