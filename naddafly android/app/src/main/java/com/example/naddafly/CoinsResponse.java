package com.example.naddafly;

import com.google.gson.annotations.SerializedName;

import okhttp3.RequestBody;

public class CoinsResponse {
    @SerializedName("score")
    private String score;

    public String getCoins() {
        return score;
    }
}
