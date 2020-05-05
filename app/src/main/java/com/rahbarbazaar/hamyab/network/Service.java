package com.rahbarbazaar.hamyab.network;

import com.rahbarbazaar.hamyab.models.LoginModel;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Service {

    @POST("Auth/Login")
    Single<LoginModel> login(@Query("name") String name,
                             @Query("password") String password);

    @POST("GetData/getGPS")
    Call<Boolean> sendGPS(@Query("api_token") String api_token,
                          @Query("lat") String lat,
                          @Query("lng") String lng,
                          @Query("project_id") String project_id);


}
