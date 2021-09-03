package com.example.intell.network;


import com.example.intell.entry.EnvironmentData;

import retrofit2.Call;
import retrofit2.Callback;

public class EnvironmentNetwork {

    private static volatile EnvironmentNetwork instance = null;

    private EnvironmentService environmentService = ServiceCreator.create(EnvironmentService.class);

    private EnvironmentNetwork() {

    }

    public static EnvironmentNetwork instance() {
        if (instance == null) {
            synchronized (EnvironmentNetwork.class) {
                if (instance == null) {
                    instance = new EnvironmentNetwork();
                }
            }
        }
        return instance;
    }

    public void queryEnvironment(String deviceId, Callback callback) {
        environmentService.getEnvironmentData(deviceId).enqueue(callback);
    }

}
