package com.example.intell.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.intell.R;
import com.example.intell.entry.AccessToken;
import com.example.intell.network.TokenService;
import com.example.intell.tool.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        // Setup the bottom navigation view with navController
        BottomNavigationView bottomNavigationView =
                (BottomNavigationView) findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        getTokenBySharedPreferences();
        Utils.requestPermission(MainActivity.this);

    }

    private void getTokenBySharedPreferences() {
        SharedPreferences sp = getApplicationContext().getSharedPreferences(AccessToken.ACCESS_TOKEN, Context.MODE_PRIVATE);
        String token = sp.getString("token", null);
        long expireTime = sp.getLong("expireTime", 0);

        if (token == null || expireTime < System.currentTimeMillis()) {
            getTokenByNetwork();
        }
    }

    private void getTokenByNetwork() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AccessToken.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TokenService service = retrofit.create(TokenService.class);

        Call<AccessToken> call = service.accessToken(AccessToken.APP_KEY, AccessToken.SECRET);
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if(response.isSuccessful()) {
                    AccessToken token = response.body();
                    if (token.code == 200) {
                        System.out.println("hello+++" + token.msg);
                        System.out.println("hello+++" + token.code);
                        System.out.println("hello+++" + token.data.accessToken);
                        System.out.println("hello+++" + token.data.expireTime);
                        SharedPreferences sp = getApplicationContext().getSharedPreferences(AccessToken.ACCESS_TOKEN, Context.MODE_PRIVATE);
                        sp.edit()
                                .putString("token", token.data.accessToken)
                                .putLong("expireTime", token.data.expireTime)
                                .apply();
                    } else {
                        Toast.makeText(getApplicationContext(), "网络访问异常", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}