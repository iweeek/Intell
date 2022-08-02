package com.example.intell.ui;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.intell.R;
import com.example.intell.entry.EnvironmentData;
import com.example.intell.network.EnvironmentService;
import com.example.intell.network.ServiceCreator;
import com.example.intell.tool.AddingTable31;
import com.google.android.material.card.MaterialCardView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.ViewById;

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


/**
 * 建设用地土壤污染状况调查采样方案检查记录表
 */
@EActivity(R.layout.activity_review_31_form)
public class ReviewForm31Activity extends AppCompatActivity {

    private static final String TAG = ReviewForm31Activity.class.getSimpleName();

    String dir;
    String filePath;

    @ViewById
    MaterialCardView materialCardView;
    @ViewById(R.id.ll_content31)
    LinearLayout linearLayout;
    @ViewById(R.id.bt_pdf31)
    Button btPdf;
    @ViewById(R.id.bt_preview31)
    Button btPreview;
    @ViewById(R.id.pdf_progress31)
    ProgressBar progressBar;
    @ViewById(R.id.scroll_progress31)
    ProgressBar scrollProgressBar;
    @ViewById(R.id.environmentLayout31)
    ScrollView scrollView;
    @ViewById(R.id.tv_top_view31)
    TextView topView;
    @ViewById(R.id.ll_stick_view31)
    LinearLayout stickView;
    @ViewById(R.id.ll_bottom_view31)
    LinearLayout bottomView;
    @ViewById(R.id.swipeRefresh31)
    SwipeRefreshLayout swipeRefreshLayout;

    @NonConfigurationInstance
    Uri uri;

    private boolean rejectedFlag;
    private Integer[] checkList = new Integer[24]; // 否决项结果
//    private Integer[] scoreList = new Integer[126]; // 打分项结果
    private EditText[] reviewNotes = new EditText[12];
//    private ArrayList<CheckBox> checkboxList[] = new ArrayList[42]; // checkbox结果列表
    private MaterialCardView allMaterialCardView[] = new MaterialCardView[12];
    private boolean choose[] = new boolean[12];
    private Integer top[] = new Integer[12];
    private int baseNo = -1;

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

    @AfterViews
    void updateViews() {
        init();
        topView.setText(getResources().getString(R.string.check_form_31_title));
        getEnvironmentByNetwork();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getEnvironmentByNetwork();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    // 处理是否显示上方按钮
                    if (scrollY > topView.getHeight()) {
                        stickView.setVisibility(View.VISIBLE);
                        if (scrollY < bottomView.getBottom() - 2300)
                            stickView.setVisibility(View.VISIBLE);
                        else
                            stickView.setVisibility(View.GONE);
                    } else
                        stickView.setVisibility(View.GONE);

                    scrollProgressBar.setProgress((int) ((Float.valueOf(scrollY) / Float.valueOf(allMaterialCardView[allMaterialCardView.length - 1].getTop() - 1500)) * 10000));
//                    System.out.println("scrollY = " + scrollY );
//                    System.out.println("gettop = " + allMaterialCardView[allMaterialCardView.length - 1].getTop() );
//                    System.out.println("getbottom = " + allMaterialCardView[allMaterialCardView.length - 1].getBottom() );
//                    System.out.println("percent = " + (Float.valueOf(scrollY) / Float.valueOf(allMaterialCardView[allMaterialCardView.length - 1].getTop())) * 100);

                }
            });
        }
    }

    int tops = 0;
    @Click({R.id.bt_preview31, R.id.bt_preview31_1})
    void ButtonPreviewWasClicked() {
        System.out.println("click!");
//        int top = allLinearLayout[7].getTop();
        for (int i = 0; i < 12; i++) {
            if (choose[i] == false) {
                System.out.println("top" + i + " = " + top[i]);
                scrollView.smoothScrollTo(0, top[i] - bottomView.getHeight() - 20);

                allMaterialCardView[i].getChildAt(0).setBackground(getResources().getDrawable(R.drawable.focus_error));
                return;
            }
        }
        ButtonPdfWasClicked();
    }

    @Click({R.id.bt_pdf31, R.id.bt_pdf31_1})
    void ButtonPdfWasClicked() {
        System.out.println("bt_pdf");
        try {
            dir = Environment.getExternalStorageDirectory().getCanonicalPath();
            String fileName = getResources().getString(R.string.check_form_31_title);
            filePath = "/Download/" + fileName + ".pdf";
            System.out.println("dir =" + dir); // str=/storage/emulated/0

            progressBar.setVisibility(View.VISIBLE);
            createPdf();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Click({R.id.bt_view_pdf31, R.id.bt_view_pdf31_1})
    void ButtonViewPdfWasClicked() {
        Intent intent = new Intent(this, PDFViewActivity_.class);
        startActivity(intent);
    }

    @Background
    void createPdf() {
        try {
            new AddingTable31(this, checkList, rejectedFlag, reviewNotes).manipulatePdf(dir + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        for (int i = 0; i < 12; i++) {
            top[i] = allMaterialCardView[i].getTop();
        }
//        System.out.println("top = " + top);
    }

    public void onPdfCreatedListener() {
        progressBar.setVisibility(View.INVISIBLE);
        AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                .setTitle("完成")
                .setMessage("生成的PDF已经保存至手机存储空间Download目录。是否查看？")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                            Toast.makeText(ReviewFormActivity.this, "", Toast.LENGTH_SHORT).show();
                        ButtonViewPdfWasClicked();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {//添加取消
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create();
        alertDialog2.show();
    }

    private void init() {
        // 开启振动
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

//        ViewStub stub = (ViewStub) findViewById(R.id.layout_cardview_review_content);
        View inflated = getLayoutInflater().inflate(R.layout.cardview_review_content, null);
//        stub.setLayoutResource(R.layout.cardview_review_content);
//        View inflated = stub.inflate();
//        if (inflated.getParent() != null) {
//            ((ViewGroup)inflated.getParent()).removeView(inflated);
//        }
//        linearLayout.addView(inflated);
//        linearLayout.addView(inflated);

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

        ArrayList<String> contentList = ReadTxtFile(R.raw.review_form_31);

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

        // 否决项 TODO
        for (int i = 0; i < 12; i++) {

            MaterialCardView mcv = new MaterialCardView(this);
            LinearLayout ll = new LinearLayout(this);
            TextView tv = new TextView(this);
            RadioGroup rg = new RadioGroup(this);
            RadioButton rb = new RadioButton(this);
            RadioButton rb2 = new RadioButton(this);
            LinearLayout ll_textView = new LinearLayout(this);
            TextView tv_star = new TextView(this);
            TextView tv_NO = new TextView(this);
            LinearLayout ll_editText = new LinearLayout(this);

            mcv.setLayoutParams(mcv_dimensions);
            mcv_dimensions.setMargins(px_16dp, 0, px_16dp, px_16dp);
            mcv.setRadius(4);
            mcv.setElevation(10);

            ll.setLayoutParams(ll_dimensions);
            ll.setOrientation(LinearLayout.VERTICAL);
            ll_textView.setLayoutParams(ll_dimensions);
            ll_textView.setOrientation(LinearLayout.HORIZONTAL);

//            tv_star.setLayoutParams(tv_left_dimensions);
//            tv_star.setText("*");
//            tv_star.setTextColor(Color.RED);
//            ll_textView.addView(tv_star);

            tv_NO.setLayoutParams(tv_middle_dimensions);
            tv_NO.setText((i + 1) + ".");
            ll_textView.addView(tv_NO);

            tv.setLayoutParams(tv_right_dimensions);
            tv_dimensions.setMargins(px_16dp, px_16dp, px_16dp, 0);
//            tv_left_dimensions.setMargins(px_16dp, px_16dp, 0, 0);
            tv_middle_dimensions.setMargins(px_16dp, px_16dp, px_16dp / 2, 0);
            tv_right_dimensions.setMargins(0, px_16dp, px_16dp, 0);

            String[] split = contentList.get(i).split("@");
            StringBuilder builder = new StringBuilder();
            for (int p = 0; p < split.length; p++) {
                if (!split[p].isEmpty()) {
                    builder.append(split[p]);
                    if (p != split.length - 1)
                        builder.append("\n");
                }
            }
            SpannableString spannableString = new SpannableString(builder.toString());
            StyleSpan span = new StyleSpan(Typeface.BOLD);
            AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(40);
            for (int p = 0; p < split.length; p++) {
                if (!split[p].isEmpty()) {
                    if (p == 0) {
                        spannableString.setSpan(span, 0, split[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannableString.setSpan(sizeSpan, 0, split[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    if (p == 1)
                        spannableString.setSpan(span,0, split[0].length() + 1 + split[1].length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            tv.setText(spannableString);
            ll_textView.addView(tv);
            ll.addView(ll_textView);
//            ll.setBackground(getResources().getDrawable(R.drawable.focus_error));

            rg.setLayoutParams(rg_dimensions);
            rg_dimensions.setMargins(px_20dp * 2, 0, px_20dp, 0);
            rg.setOrientation(LinearLayout.HORIZONTAL);

            rb.setLayoutParams(rb_dimensions);
            rb2.setLayoutParams(rb_dimensions);
            rb_dimensions.setMargins(px_16dp * 2, px_16dp / 2, 0, px_16dp / 2);
            rb.setText("是");
            rb2.setText("否");
            rb_dimensions.weight = 1;
            rb.setTextSize(16);
            rb2.setTextSize(16);
            rg.addView(rb);
            rg.addView(rb2);

            int finalI = i;

            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    int radioButtonID = group.getCheckedRadioButtonId();
                    View radioButton = group.findViewById(radioButtonID);
                    int position = group.indexOfChild(radioButton);
                    System.out.println("position = " + position);

                    int n = 2; // 单选组个数
                    baseNo = (checkedId + (n-1)) - (finalI + 1) * n - position;

                    choose[finalI] = true;
                    mVibrator.vibrate(30);
                    System.out.println("当前累计 checkedId +++++ " + checkedId);
                    System.out.println("baseNo ++++" + baseNo);
                    if(baseNo == -1)
                        System.out.println("出问题了。");
                    int id = (checkedId - 1) - baseNo;
                    System.out.println(id);
                    switch (id % 2) {
                        case 0:
                            checkList[id] = 1;
                            checkList[id + 1] = 0;
                            break;
                        case 1:
                            checkList[id] = 1;
                            checkList[id - 1] = 0;
                            break;
                    }

                    //更新mcv的top
                    for (int i = 0; i < 12; i++) {
                        top[i] = allMaterialCardView[i].getTop();
                    }

                    // 点击选项后隐藏错误提示框
                    ll.setBackground(null);

                    if (id % 2 == 1) {
                        // 部分符合或者不符合，需要显示审查说明
                        if (ll_editText.getChildCount() != 0) {
                            ll_editText.setVisibility(View.VISIBLE);
                            return;
                        }
                        ll_editText.setLayoutParams(ll_dimensions);
                        ll_editText.setOrientation(LinearLayout.VERTICAL);
//                        ll_editText.setId(100000 + NO);

                        TextView textView = new TextView(ReviewForm31Activity.this);
                        EditText et = new EditText(ReviewForm31Activity.this);
                        textView.setLayoutParams(tv_dimensions);
                        textView.setText("请输入审查说明");
                        et.setLayoutParams(et_dimensions);
                        et.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                System.out.println("s = " + s);
                                updateViewTop(finalI);
                            }
                        });
                        et_dimensions.setMargins(px_16dp, px_16dp / 2, px_20dp, px_16dp / 2);
                        ll_editText.addView(textView);
                        ll_editText.addView(et);
                        ll_editText.setVisibility(View.VISIBLE);
                        reviewNotes[finalI] = et;
                        ll.addView(ll_editText);
                    } else {
                        //符合
                        if (ll_editText.getVisibility() == View.VISIBLE)
//                        if (ll_editText != null)
//                            findViewById(100000 + NO).setVisibility(View.GONE);
                            ll_editText.setVisibility(View.GONE);
                    }

                    // 更新mcv的top
                    updateViewTop(finalI);
                }
            });
            ll.addView(rg);
            mcv.addView(ll);
            allMaterialCardView[i] = mcv;
            linearLayout.addView(mcv, i + 1);
        }
        //打分项
        /*
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
            LinearLayout ll_editText = new LinearLayout(this);

            mcv.setLayoutParams(mcv_dimensions);
            mcv_dimensions.setMargins(px_16dp, px_16dp / 2, px_16dp, px_16dp);
            mcv.setRadius(4);

            ll.setLayoutParams(ll_dimensions);
            ll.setOrientation(LinearLayout.VERTICAL);

            tv.setLayoutParams(tv_dimensions);
            tv_dimensions.setMargins(px_16dp, px_16dp, px_16dp, 0);
            tv.setId(String.format("tv" + 2000 + i).hashCode());
            // 设置题目的同时，判断是否要加上复选框
            // 3.【地块基本情况】① 地块公告资料或数据地块公告资料或数据是否表述清楚，包含：□地块名称 □地块地址
            cb_dimensions.setMargins(px_20dp, 0, px_20dp, 0);
            ArrayList<CheckBox> list = null;
            if (contentList.get(i + 8).contains("□")) {
                String[] split = contentList.get(i + 8).split("□");
                tv.setText(split[0]);
//                TextViewCompat.setAutoSizeTextTypeWithDefaults(tv, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                ll.addView(tv);
                if (split.length > 1)
                    list = new ArrayList<CheckBox>();
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
                    list.add(cb);
                    ll.addView(cb);
                }
                checkboxList[i] = list;
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

            int finalI = i;
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    choose[finalI + 8] = true;
                    mVibrator.vibrate(30);
                    System.out.println("checkedId % 142 +++++ " + checkedId % 142);

                    int id = checkedId % 142 - 17;
                    switch (id % 3) {
                        case 0:
                            scoreList[id] = 1;
                            scoreList[id + 1] = 0;
                            scoreList[id + 2] = 0;
                            break;
//                            rejectedFlag = Constant.REJECTED;
                        case 1:
                            scoreList[id] = 1;
                            scoreList[id - 1] = 0;
                            scoreList[id + 1] = 0;
                            break;
                        case 2:
                            scoreList[id] = 1;
                            scoreList[id - 1] = 0;
                            scoreList[id - 2] = 0;
                            break;
                    }

                    if (checkedId % 142 % 3 == 0 || checkedId % 142 % 3 == 1) {
                        // 部分符合或者不符合，需要显示审查说明
                        if (ll_editText.getChildCount() != 0) {
                            ll_editText.setVisibility(View.VISIBLE);
                            return;
                        }
                        ll_editText.setLayoutParams(ll_dimensions);
                        ll_editText.setOrientation(LinearLayout.VERTICAL);
                        ll_editText.setId(100000 + NO);

                        TextView textView = new TextView(ReviewForm31Activity.this);
                        EditText et = new EditText(ReviewForm31Activity.this);
                        textView.setLayoutParams(tv_dimensions);
                        textView.setText("请输入审查说明");
                        et.setLayoutParams(et_dimensions);
                        et.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                System.out.println("s = " + s);
                                updateViewTop(finalI);
                            }
                        });
                        et_dimensions.setMargins(px_16dp, px_16dp / 2, px_20dp, px_16dp / 2);
                        ll_editText.addView(textView);
                        ll_editText.addView(et);
                        ll_editText.setVisibility(View.VISIBLE);
                        reviewNotes[finalI] = et;
                        ll.addView(ll_editText);
                    } else {
                        //符合
                        if (ll_editText.getVisibility() == View.VISIBLE)
//                        if (ll_editText != null)
//                            findViewById(100000 + NO).setVisibility(View.GONE);
                            ll_editText.setVisibility(View.GONE);
                    }

                    // 更新mcv的top
                    updateViewTop(finalI);
//
//                    allMaterialCardView[finalI].measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                    int mheight =  allMaterialCardView[finalI].getMeasuredHeight();
//                    System.out.println("w = " + mheight);
//                    int height =  allMaterialCardView[finalI].getHeight();
//                    System.out.println("w = " + height);

                }
            });

            ll.addView(rg);
            mcv.addView(ll);
            allMaterialCardView[8 + i] = mcv;
            linearLayout.addView(mcv, i + 11);
        }
        */
    }

    public void updateViewTop(int finalI) {
        // 更新mcv的top
        allMaterialCardView[finalI].post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 12; i++) {
                    top[i] = allMaterialCardView[i].getTop();
                }
                System.out.println("现在top" + (finalI) + " = " + top[finalI]);
                int mheight = allMaterialCardView[finalI].getMeasuredHeight();
                System.out.println("w = " + mheight);
                int height = allMaterialCardView[finalI].getHeight();
                System.out.println("w = " + height);
            }
        });
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
}