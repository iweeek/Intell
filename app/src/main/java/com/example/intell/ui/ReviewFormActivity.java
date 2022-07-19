package com.example.intell.ui;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ethanco.circleprogresslibrary.TextOneCircleProgress;
import com.example.intell.R;
import com.example.intell.common.Constant;
import com.example.intell.databinding.ActivityDisplayPdfBinding;
import com.example.intell.entry.EnvironmentData;
import com.example.intell.network.EnvironmentService;
import com.example.intell.network.ServiceCreator;
import com.google.android.material.card.MaterialCardView;
import com.videogo.openapi.bean.req.GetCloudRecordListReq;

import org.androidannotations.annotations.EActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@EActivity(R.layout.activity_review_form)
public class ReviewFormActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    private static final String TAG = "EnvironmentActivity";

    private MaterialCardView materialCardView;
    private RadioGroup radioGroup1;
    private LinearLayout linearLayout;

    private boolean rejectedItems; // 0: no rejected items; 1: need to rejected
    private ArrayList<Boolean> rejectedList;
    private ArrayList<Boolean> checkboxList; // checkbox结果列表

    private SwipeRefreshLayout swipeRefreshLayout;
    private Vibrator mVibrator;

    private int px_16dp;
    private int px_20dp;

    LinearLayout.LayoutParams mcv_dimensions;
    LinearLayout.LayoutParams ll_dimensions;
    LinearLayout.LayoutParams tv_dimensions;
    LinearLayout.LayoutParams tv_left_dimensions;
    LinearLayout.LayoutParams tv_middle_dimensions;
    LinearLayout.LayoutParams tv_right_dimensions;
    LinearLayout.LayoutParams rg_dimensions;
    LinearLayout.LayoutParams rb_dimensions;
    LinearLayout.LayoutParams cb_dimensions;
    LinearLayout.LayoutParams et_dimensions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_review_form);

        init();

        radioGroup1 = findViewById(R.id.radio_group_1);

        rejectedList = new ArrayList<>(8);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        getEnvironmentByNetwork();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getEnvironmentByNetwork();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void init() {
        // 开启振动
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        linearLayout = findViewById(R.id.ll_content);
//        ViewStub stub = (ViewStub) findViewById(R.id.layout_cardview_review_content);
        View inflated = getLayoutInflater().inflate(R.layout.cardview_review_content, null);
//        stub.setLayoutResource(R.layout.cardview_review_content);
//        View inflated = stub.inflate();
//        if (inflated.getParent() != null) {
//            ((ViewGroup)inflated.getParent()).removeView(inflated);
//        }
//        linearLayout.addView(inflated);
//        linearLayout.addView(inflated);

        materialCardView = findViewById(R.id.materialCardView);
        Resources r = getResources();
        px_16dp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16,
                r.getDisplayMetrics()
        );
        px_20dp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                20,
                r.getDisplayMetrics()
        );

        ArrayList<String> contentList = ReadTxtFile(R.raw.review_form);

        mcv_dimensions = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll_dimensions = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tv_dimensions = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv_left_dimensions = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv_middle_dimensions = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv_right_dimensions = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rg_dimensions = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rb_dimensions = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cb_dimensions = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        et_dimensions = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        createCardView(1);
        createCardView(2);

        // 否决项 TODO
        for (int i = 0; i < 8; i++) {

            MaterialCardView mcv = new MaterialCardView(this);
            LinearLayout ll = new LinearLayout(this);
            TextView tv = new TextView(this);
            RadioGroup rg = new RadioGroup(this);
            RadioButton rb = new RadioButton(this);
            RadioButton rb2 = new RadioButton(this);
            LinearLayout ll_textView = new LinearLayout(this);
            TextView tv_star = new TextView(this);
            TextView tv_NO = new TextView(this);

            mcv.setLayoutParams(mcv_dimensions);
            mcv_dimensions.setMargins(px_16dp, 0, px_16dp, px_16dp);
            mcv.setRadius(4);
            mcv.setElevation(10);

            ll.setLayoutParams(ll_dimensions);
            ll.setOrientation(LinearLayout.VERTICAL);
            ll_textView.setLayoutParams(ll_dimensions);
            ll_textView.setOrientation(LinearLayout.HORIZONTAL);

            tv_star.setLayoutParams(tv_left_dimensions);
            tv_star.setText("*");
            tv_star.setTextColor(Color.RED);
            ll_textView.addView(tv_star);

            tv_NO.setLayoutParams(tv_middle_dimensions);
            tv_NO.setText((i + 1) + ".");
            ll_textView.addView(tv_NO);

            tv.setLayoutParams(tv_right_dimensions);
            tv_dimensions.setMargins(px_16dp, px_16dp, px_16dp, 0);
            tv_left_dimensions.setMargins(px_16dp, px_16dp, 0, 0);
            tv_middle_dimensions.setMargins(0, px_16dp, px_16dp/2, 0);
            tv_right_dimensions.setMargins(0, px_16dp, px_16dp, 0);
//            tv.setId(String.format("tv" + 2000 + i).hashCode());
            tv.setText(contentList.get(i));
//            tv.setTextSize(18);
            ll_textView.addView(tv);
            ll.addView(ll_textView);

            rg.setLayoutParams(rg_dimensions);
            rg_dimensions.setMargins(px_20dp*2, 0, px_20dp, 0);
            rg.setOrientation(LinearLayout.HORIZONTAL);

            rb.setLayoutParams(rb_dimensions);
            rb2.setLayoutParams(rb_dimensions);
            rb_dimensions.setMargins(0, px_16dp / 2, 0, px_16dp / 2);
            rb.setText("涉及");
            rb2.setText("不涉及");
            rb_dimensions.weight = 1;
            rb.setTextSize(16);
            rb2.setTextSize(16);
            rg.addView(rb);
            rg.addView(rb2);

            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    mVibrator.vibrate(30);
                    System.out.println("checkedId % 142 +++++ " + checkedId % 142);
                    switch (checkedId) {
                        case 1:
                            rejectedItems = Constant.REJECTED;
                            System.out.println("checkedId = " + checkedId);
                        case R.id.rb_not_involved_1:
                            rejectedList.add(false);
                    }
                }
            });

            ll.addView(rg);
            mcv.addView(ll);
            linearLayout.addView(mcv, i + 2);
        }
        //打分项
        for (int i = 0; i < 42; i++) {
            final int NO = i;
            MaterialCardView mcv = new MaterialCardView(this);
            LinearLayout ll = new LinearLayout(this);
            TextView tv = new TextView(this);
            RadioGroup rg = new RadioGroup(this);
            RadioButton rb = new RadioButton(this);
            RadioButton rb2 = new RadioButton(this);
            RadioButton rb3 = new RadioButton(this);
            CheckBox cb;
//            EditText et = new EditText(this);
            LinearLayout ll_editText = new LinearLayout(this);


            mcv.setLayoutParams(mcv_dimensions);
            mcv_dimensions.setMargins(px_16dp, px_16dp / 2, px_16dp, px_16dp);
            mcv.setRadius(4);

            ll.setLayoutParams(ll_dimensions);
            ll.setOrientation(LinearLayout.VERTICAL);

            tv.setLayoutParams(tv_dimensions);
            tv_dimensions.setMargins(px_16dp, px_16dp, px_16dp, 0);
            tv.setId(String.format("tv" + 2000 + i).hashCode());
            // 设置题目的同时，判断是否要加上复选框 TODO
            // 3.【地块基本情况】① 地块公告资料或数据地块公告资料或数据是否表述清楚，包含：□地块名称 □地块地址
            cb_dimensions.setMargins(px_20dp, 0, px_20dp, 0);
            if (contentList.get(i + 8).contains("□")) {
                String[] split = contentList.get(i + 8).split("□");
                tv.setText(split[0]);
//                TextViewCompat.setAutoSizeTextTypeWithDefaults(tv, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                ll.addView(tv);
                for (int j = 1; j < split.length; j++) {
                    cb = new CheckBox(this);
                    cb.setLayoutParams(cb_dimensions);
                    cb.setText(split[j]);
                    cb.setTextSize(16);
                    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            
                        }
                    });
                    ll.addView(cb);
                }
            } else {
                tv.setText(contentList.get(i + 8));
//                tv.setTextSize(18);
                ll.addView(tv);
            }

            rg.setLayoutParams(rg_dimensions);
            rg.setOrientation(LinearLayout.HORIZONTAL);

            rb.setLayoutParams(rb_dimensions);
            rb2.setLayoutParams(rb_dimensions);
            rb3.setLayoutParams(rb_dimensions);
            rb_dimensions.setMargins(0, px_16dp / 2, 0, px_16dp / 2);
            rb.setText("符合");
            rb2.setText("部分符合");
            rb3.setText("不符合");
            rb_dimensions.weight = 1;
            rb.setTextSize(16);
            rb2.setTextSize(16);
            rb3.setTextSize(16);
            rg.addView(rb);
            rg.addView(rb2);
            rg.addView(rb3);

            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    mVibrator.vibrate(30);
                    System.out.println("checkedId % 142 +++++ " + checkedId % 142);
                    if (contentList.get(11).contains("□")) {
                        System.out.println(contentList.get(11));
                    }
                    switch (checkedId % 142) {
                        case 1:
                            rejectedItems = Constant.REJECTED;
                            System.out.println("checkedId = " + checkedId);
                        case R.id.rb_not_involved_1:
                            rejectedList.add(false);
                    }
                    int hashcode = ("linearLayout" + checkedId % 142 % 3).hashCode();
                    if (checkedId % 142 % 3 == 0 || checkedId % 142 % 3 == 1) {
                        // 部分符合或者不符合，需要显示审查说明
                        if (ll_editText.getChildCount() != 0) {
                            ll_editText.setVisibility(View.VISIBLE);
                            return;
                        }
                        ll_editText.setLayoutParams(ll_dimensions);
                        ll_editText.setOrientation(LinearLayout.VERTICAL);
                        ll_editText.setId(100000 + NO);

                        TextView textView = new TextView(ReviewFormActivity.this);
                        EditText et = new EditText(ReviewFormActivity.this);
                        textView.setLayoutParams(tv_dimensions);
                        textView.setText("请输入审查说明");
                        et.setLayoutParams(et_dimensions);
                        et_dimensions.setMargins(px_16dp, px_16dp / 2, px_20dp, px_16dp / 2);
                        ll_editText.addView(textView);
                        ll_editText.addView(et);
                        ll.addView(ll_editText);
                    } else {
                        //符合
                        if (ll_editText != null)
//                            findViewById(100000 + NO).setVisibility(View.GONE);
                            ll_editText.setVisibility(View.GONE);
                    }
                }
            });

            ll.addView(rg);
            mcv.addView(ll);
            linearLayout.addView(mcv, i + 11);
        }
    }

    private void createCardView(int type) {
        switch (type) {
            case 1: // title
            case 2: // bar
            case 3:
        }
    }

    public ArrayList<String> ReadTxtFile(int file) {
        ArrayList<String> contentList = new ArrayList<>(); //文件内容字符串
        //如果path是传递过来的参数，可以做一个非目录的判断
        try {
            InputStream instream = getResources().openRawResource(file);
            if (instream != null) {
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                //分行读取
                while ((line = buffreader.readLine()) != null) {
                    contentList.add(line);
                }
                instream.close();
            }
        } catch (java.io.FileNotFoundException e) {
            Log.d("TestFile", "The File doesn't not exist.");
        } catch (IOException e) {
            Log.d("TestFile", e.getMessage());
        }
        return contentList;
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
            }

            @Override
            public void onFailure(Call<List<EnvironmentData>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        System.out.println("good " + buttonView.getText() + isChecked);
    }
}