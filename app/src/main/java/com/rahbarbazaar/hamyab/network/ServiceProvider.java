package com.rahbarbazaar.hamyab.network;

import android.content.Context;
import android.content.Intent;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.rahbarbazaar.hamyab.utilities.Cache;
import com.rahbarbazaar.hamyab.utilities.ClientConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceProvider {

    private Service mService;

    public ServiceProvider(Context context) {

        //config client and retrofit:

        OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder();
        clientBuilder.connectTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES);
        clientBuilder.cache(null);

        String accessToken = Cache.getString(context,"access_token");
        if(accessToken==null){
            accessToken ="";
        }

        if (!accessToken.equals("")) {
            clientBuilder.addInterceptor(chain -> {


                Request request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + Cache.getString(context,"access_token"))
                        .addHeader("Accept", "application/json")
                        .build();
                return chain.proceed(request);
            });
        } else {

            // for login request
            clientBuilder.addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .addHeader("Accept", "application/json")
                        .build();
                return chain.proceed(request);
            });
        }

        // handle error 401
//        clientBuilder.authenticator(new Authenticator() {
//            @Override
//            public Request authenticate(Route route, Response response) throws IOException {
//                String access_token = Cache.getString(context,"access_token");
//                String refresh_token = Cache.getString(context,"refresh_token");
//
//                Service service = new ServiceProvider(context).mService;
//                Call<RefreshTokenModel> call = service.refreshToken(access_token,refresh_token);
//                retrofit2.Response<RefreshTokenModel> tokenModelResponse = call.execute();
//                //sync request
//                if (tokenModelResponse.isSuccessful()) {
//
//                    //save token
//                    Cache.setString(context,"access_token",tokenModelResponse.body().accessToken);
//                    Cache.setString(context,"refresh_token",tokenModelResponse.body().refreshToken);
//                    Cache.setInt(context,"expireAt",tokenModelResponse.body().expireAt);
//
//                    return response.request().newBuilder()
//                            .removeHeader("Authorization")
//                            .removeHeader("Accept")
//                            .addHeader("Authorization", "Bearer " + Cache.getString(context,"access_token"))
//                            .addHeader("language", "fa")
//                            .addHeader("Accept", "application/json")
//                            .build();
//                } else {
//
//                    context.startActivity(new Intent(context, LoginActivity.class));
//                    return null;
//
//                }
//
//            }
//        });



        //error handlong
        clientBuilder.addInterceptor(chain -> {
            Request request = chain.request();
            Response response = chain.proceed(request);
//            int a = response.code();
            return response;
        }).build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ClientConfig.ServerURL).client(clientBuilder.build()).
                addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
        mService = retrofit.create(Service.class);
    }
    public Service getmService() {
        return mService;
    }
}
