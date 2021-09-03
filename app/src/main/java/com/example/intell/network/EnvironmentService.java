package com.example.intell.network;

import com.example.intell.entry.EnvironmentData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface EnvironmentService {

    @FormUrlEncoded
    @POST("select-sensordata")
    Call<List<EnvironmentData>> getEnvironmentData(@Field("device_id") String deviceId);

}
