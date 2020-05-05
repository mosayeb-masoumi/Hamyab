package com.rahbarbazaar.hamyab.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginModel {

    @SerializedName("api_token")
    @Expose
    public Integer apiToken;
    @SerializedName("expire_at")
    @Expose
    public Integer expireAt;
}
