package com.textmate.dovaj.textmate.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Sentence {

    @SerializedName("text")
    @Expose
    private Text text;
    @SerializedName("sentiment")
    @Expose
    private Sentiment sentiment;

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public Sentiment getSentiment() {
        return sentiment;
    }

    public void setSentiment(Sentiment sentiment) {
        this.sentiment = sentiment;
    }

}