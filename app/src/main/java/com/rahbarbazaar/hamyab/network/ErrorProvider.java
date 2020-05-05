package com.rahbarbazaar.hamyab.network;

import com.rahbarbazaar.hamyab.utilities.ClientConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ErrorProvider {

    public static Retrofit.Builder builder =new Retrofit.Builder()
            .baseUrl(ClientConfig.ServerURL)
            .addConverterFactory(GsonConverterFactory.create());

    public static Retrofit retrofit = builder.build();
}
