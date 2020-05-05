package com.rahbarbazaar.hamyab.api_error;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ErrorsMessage422 {

    @SerializedName("name")
    @Expose
    public List<String> name = null;
    @SerializedName("password")
    @Expose
    public List<String> password = null;
}
