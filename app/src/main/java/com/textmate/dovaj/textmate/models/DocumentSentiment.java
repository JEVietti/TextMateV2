package com.textmate.dovaj.textmate.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DocumentSentiment {

    @SerializedName("magnitude")
    @Expose
    private Double magnitude;
    @SerializedName("score")
    @Expose
    private Double score;

    public Double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(Double magnitude) {
        this.magnitude = magnitude;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

}