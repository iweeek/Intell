package com.example.intell.network;

import com.example.intell.entry.AccessToken;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface TokenService {

    @POST("api/lapp/token/get")
//     https://open.ys7.com/api/lapp/token/get?appKey=df21b714ee1a4941984137eae76e1245&appSecret=b1be440054c3fabb71d03743c290d99a
    Call<AccessToken> accessToken(@Query("appKey") String appKey, @Query("appSecret") String appSecret);

}

