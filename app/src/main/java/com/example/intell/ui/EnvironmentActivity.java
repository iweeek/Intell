package com.example.intell.ui;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ethanco.circleprogresslibrary.TextOneCircleProgress;
import com.example.intell.R;
import com.example.intell.entry.EnvironmentData;
import com.example.intell.network.EnvironmentService;
import com.example.intell.network.ServiceCreator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EnvironmentActivity extends AppCompatActivity {

    private static final String TAG = "EnvironmentActivity";

    private TextView tvTemperature;
    private TextView tvHumidity;
    private TextView tvPM25;
    private TextView tvPM10;
    private TextView tvNoise;
    private TextView tvTvoc;
    private TextView tvShine;
    private TextView tvSpeed;
    private TextView tvCO;
    private TextView tvNO2;
    private TextView tvSO2;
    private TextView tvO3;
    private TextView tvTemperatureWater;
    private TextView tvPH;
    private TextView tvTurbidity;
    private TextView tvEC;

    private SwipeRefreshLayout swipeRefreshLayout;

    private TextOneCircleProgress mEnvironmentCircleProgress;
    private TextOneCircleProgress mWaterCircleProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_environment);
        tvTemperature = findViewById(R.id.temperatureText);
        tvHumidity = findViewById(R.id.humidityText);
        tvPM25 = findViewById(R.id.pm25Text);
        tvPM10 = findViewById(R.id.pm10Text);
        tvNoise = findViewById(R.id.noiseText);
        tvTvoc = findViewById(R.id.tvocText);
        tvShine = findViewById(R.id.shineText);
        tvSpeed = findViewById(R.id.speedText);
        tvCO = findViewById(R.id.coText);
        tvNO2 = findViewById(R.id.no2Text);
        tvSO2 = findViewById(R.id.so2Text);
        tvO3 = findViewById(R.id.o3Text);

        tvTemperatureWater = findViewById(R.id.temperatureWaterText);
        tvPH = findViewById(R.id.phText);
        tvTurbidity = findViewById(R.id.turbidityText);
        tvEC = findViewById(R.id.ecText);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        mEnvironmentCircleProgress = (TextOneCircleProgress) findViewById(R.id.environmentTextCircleProgress);
        mWaterCircleProgress = (TextOneCircleProgress) findViewById(R.id.waterTextCircleProgress);

        getEnvironmentByNetwork();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getEnvironmentByNetwork();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void getEnvironmentByNetwork() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServiceCreator.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        EnvironmentService service = retrofit.create(EnvironmentService.class);

        Call<List<EnvironmentData>> environmentDataCall = service.getEnvironmentData("867996056752810");
        environmentDataCall.enqueue(new Callback<List<EnvironmentData>>() {
            @Override
            public void onResponse(Call<List<EnvironmentData>> call, Response<List<EnvironmentData>> response) {
                List<EnvironmentData> data = response.body();

                double AQI = (Double.parseDouble(data.get(0).getPM25()) + Double.parseDouble(data.get(0).getPM10())) / 2;

                mEnvironmentCircleProgress.setSubProgress((int) Math.round(AQI * 0.2));
                mEnvironmentCircleProgress.setHead(String.valueOf(Math.round(AQI)));
                mEnvironmentCircleProgress.setSubHead("");
                mEnvironmentCircleProgress.setBottomHead("空气质量参数");

                tvTemperature.setText(data.get(0).getAir_T() +" ℃");
                tvHumidity.setText(data.get(0).getAir_H() + " %");

                String pm25 = data.get(0).getPM25() + " ug/m3";
                int start = pm25.length() - 1;
                int end = pm25.length();
                SpannableStringBuilder cs = new SpannableStringBuilder(pm25);
                cs.setSpan(new SuperscriptSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cs.setSpan(new RelativeSizeSpan(0.75f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvPM25.setText(cs);

                String pm10 = data.get(0).getPM10() + " ug/m3";
                start = pm25.length() - 1;
                end = pm25.length();
                cs = new SpannableStringBuilder(pm10);
                cs.setSpan(new SuperscriptSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cs.setSpan(new RelativeSizeSpan(0.75f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvPM10.setText(cs);

                tvNoise.setText(data.get(0).getNoise() + " dB");
                tvTvoc.setText(data.get(0).getTvoc() + " ppm");
                tvShine.setText(data.get(0).getShine() + " lx");
                tvSpeed.setText(data.get(0).getSpeed() + " m/s");
                tvCO.setText(data.get(0).getCO() + " ppm");
                tvNO2.setText(data.get(0).getNO2() + " ppm");
                tvSO2.setText(data.get(0).getSO2() + " ppm");
                tvO3.setText(data.get(0).getO3() + " ppm");

            }

            @Override
            public void onFailure(Call<List<EnvironmentData>> call, Throwable t) {
                t.printStackTrace();
            }
        });

        Call<List<EnvironmentData>> waterDataCall = service.getEnvironmentData("867996056628713");
        waterDataCall.enqueue(new Callback<List<EnvironmentData>>() {
            @Override
            public void onResponse(Call<List<EnvironmentData>> call, Response<List<EnvironmentData>> response) {
                List<EnvironmentData> data = response.body();

                double ph = Double.parseDouble(data.get(0).getPH());
                double turbidity = Double.parseDouble(data.get(0).getTurbidity());
                double ec = Double.parseDouble(data.get(0).getEC());

                tvTemperatureWater.setText(data.get(0).getTemperature() +" ℃");
                tvPH.setText(data.get(0).getPH() + " %");

                tvTurbidity.setText(data.get(0).getTurbidity() + " JTU");
                tvEC.setText(data.get(0).getEC() + " S/m");

                Double WQI = (turbidity + Math.abs(ph - 7) * 100 + ec * 1000) / 30;

                mWaterCircleProgress.setSubProgress((int) Math.round(WQI * 0.2));
                mWaterCircleProgress.setHead(String.valueOf(Math.round(WQI)));
                mWaterCircleProgress.setSubHead("");
                mWaterCircleProgress.setBottomHead("水质质量参数");
            }

            @Override
            public void onFailure(Call<List<EnvironmentData>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}