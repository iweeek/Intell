package com.example.intell.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.intell.R;
import com.example.intell.common.Constant;
import com.example.intell.databinding.ActivityMainBinding;
import com.example.intell.entry.EnvironmentData;
import com.example.intell.network.EnvironmentService;
import com.example.intell.network.ServiceCreator;
import com.google.android.material.card.MaterialCardView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DisplayPdfActivity extends AppCompatActivity {

    private static final String TAG = "DisplayPdfActivity";

    com.example.intell.databinding.ActivityDisplayPdfBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_form);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_display_pdf);
        binding.setLifecycleOwner(this);
        WebSettings webSettings = binding.webView.getSettings();
        //支持缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        //自适应屏幕
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        //允许js 并读取文件
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
//        pickFile();
//        display("file:///android_asset/addingTable.pdf");
        display("file:///android_asset/demo.pdf");
        display("content://com.android.providers.media.documents/document/document%3A10284");

    }

    void display(String url) {
        Log.e(DisplayPdfActivity.class.getSimpleName(), "url>" + url);
        binding.webView.loadUrl("file:///android_asset/viewer.html?file=" + url);
    }

    void pickFile() {
        int checkSelfPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int checkSelfPermission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (checkSelfPermission == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission1 == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");

            //指定类型
//            var types = arrayOf(pdf)
//            intent.putExtra(Intent.EXTRA_MIME_TYPES,types)
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, 10);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case 10:
                System.out.println("hahaha " + data.getDataString());
                if (null == data.getData()) {
                    Log.e(DisplayPdfActivity.class.getSimpleName(), "onActivityResult: 没有加载到文件");
                    return;
                }
                Uri returnUri = data.getData();
                System.out.println("returnUri  " + returnUri.toString());
                display(returnUri.toString());
                break;
        }
    }
}