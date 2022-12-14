package com.example.intell.ui;


import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.intell.R;
import com.example.intell.entry.EnvironmentData;
import com.example.intell.network.EnvironmentService;
import com.example.intell.network.ServiceCreator;
import com.example.intell.tool.AddingTable;
import com.example.intell.tool.Utils;
import com.google.android.material.card.MaterialCardView;
import com.rex.editor.common.EssFile;
import com.rex.editor.common.FilesUtils;
import com.rex.editor.view.RichEditorNew;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * ??????????????????????????????????????????????????????????????????
 */
@EActivity(R.layout.activity_review_form)
public class ReviewFormActivity extends AppCompatActivity {

    private static final String TAG = ReviewFormActivity.class.getSimpleName();
    public final static int RESULT_CHOOSE = 123;
    private static final int RESULT_CAMERA = 124;

    String dir;
    String filePath;

    @ViewById
    MaterialCardView materialCardView;
    @ViewById(R.id.radio_group_1)
    RadioGroup radioGroup1;
    @ViewById(R.id.ll_content)
    LinearLayout linearLayout;
    @ViewById(R.id.bt_pdf)
    Button btPdf;
    @ViewById(R.id.bt_preview)
    Button btPreview;
    @ViewById(R.id.pdf_progress)
    ProgressBar progressBar;
    @ViewById(R.id.scroll_progress)
    ProgressBar scrollProgressBar;
    @ViewById(R.id.environmentLayout)
    ScrollView scrollView;
    @ViewById(R.id.tv_top_view)
    TextView topView;
    @ViewById(R.id.ll_stick_view)
    LinearLayout stickView;
    @ViewById(R.id.ll_bottom_view)
    LinearLayout bottomView;
    @ViewById(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @ViewById(R.id.name)
    EditText nameTextView;
    @ViewById(R.id.ll_name)
    LinearLayout ll_name;
    RichEditorNew currentRichEditor;

    @NonConfigurationInstance
    Uri uri;

    private boolean rejectedFlag;
    private Integer[] rejectedList = new Integer[16]; // ???????????????
    private Integer[] scoreList = new Integer[126]; // ???????????????
    private EditText[] reviewNotes = new EditText[42];
    private String[] reviewNoteStr = new String[42];
    private ArrayList<CheckBox> checkboxList[] = new ArrayList[42]; // checkbox????????????
    private MaterialCardView allMaterialCardView[] = new MaterialCardView[50];
    private boolean choose[] = new boolean[50];  // ??????????????????
    private Integer top[] = new Integer[50];
    RichEditorNew[] richEditors = new RichEditorNew[42];
    ArrayList<List<String>> imgList = new ArrayList<>(42);
    private int baseNo;

    private Vibrator mVibrator;
    private Uri imageUri;

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
    LinearLayout.LayoutParams bt_dimensions;


    @AfterViews
    void updateViews() {
        init();

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

                    // ??????????????????????????????
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

        nameTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ll_name.setBackground(null);
            }
        });
    }

    int tops = 0;
    @Click({R.id.bt_preview, R.id.bt_preview1})
    void ButtonPreviewWasClicked() {
//        System.out.println("click!");
//        int top = allLinearLayout[7].getTop();
        if (nameTextView.getText().toString().trim().isEmpty()) {
            scrollView.smoothScrollTo(0,  0);
            Toast.makeText(this, "????????????????????? :)", Toast.LENGTH_SHORT).show();
            ll_name.setBackground(getResources().getDrawable(R.drawable.focus_error));
        } else {
            for (int i = 0; i < 50; i++) {
                if (choose[i] == false) {
                    System.out.println("top" + i + " = " + top[i]);
                    scrollView.smoothScrollTo(0, top[i] - bottomView.getHeight() - 20);

                    allMaterialCardView[i].getChildAt(0).setBackground(getResources().getDrawable(R.drawable.focus_error));
                    return;
                }
            }
        }
//        ButtonPdfWasClicked();
    }

    @Click({R.id.bt_pdf, R.id.bt_pdf1})
    void ButtonPdfWasClicked() {
        System.out.println("bt_pdf");
        if (nameTextView.getText().toString().trim().isEmpty()) {
            scrollView.smoothScrollTo(0,  0);
            Toast.makeText(this, "????????????????????? :)", Toast.LENGTH_SHORT).show();
            ll_name.setBackground(getResources().getDrawable(R.drawable.focus_error));
        } else {
            try {
                dir = Environment.getExternalStorageDirectory().getCanonicalPath();
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                filePath = "/Download/" + nameTextView.getText().toString().trim() + "_" +
                        getResources().getString(R.string.review_form_title) + "_" + sdf.format(date) + ".pdf";
                System.out.println("dir =" + dir); // str=/storage/emulated/0

                progressBar.setVisibility(View.VISIBLE);
                createPdf(nameTextView.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Click({R.id.bt_view_pdf, R.id.bt_view_pdf1})
    void ButtonViewPdfWasClicked() {
        Intent intent = new Intent(this, PDFViewActivity_.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_CAMERA:
                System.out.println("uri = " + imageUri.getPath());
                if (imageUri != null) {
//                    String abUrl = FilesUtils.getPath(ReviewForm34Activity.this, imageUri);
                    String abUrl = imageUri.getPath();
                    abUrl = abUrl.substring(5);
                    Log.i("rex", "abUrl:" + abUrl);
                    EssFile essFile = new EssFile(abUrl);
                    if (essFile.isImage() || essFile.isGif()) {
                        currentRichEditor.insertImage(essFile.getAbsolutePath());
                        currentRichEditor.setFontSize(4);
                        currentRichEditor.setEditorFontSize(18);
                    }
                }
                break;
            case RESULT_CHOOSE:
                if (data == null) return;
                // ??????EXTRA_ALLOW_MULTIPLE????????????????????????????????????????????????intent.getExtra()intent????????????????????????????????????intent ClipData??????SDK 18????????????????????????
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) && (null == data.getData())) {
                    ClipData clipdata = data.getClipData();
                    for (int i = 0; i < clipdata.getItemCount(); i++) {
//                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), clipdata.getItemAt(i).getUri());
                        Uri uri = clipdata.getItemAt(i).getUri();
                        System.out.println("uri = " + uri.getPath());
                        if (uri != null) {
                            String abUrl = FilesUtils.getPath(ReviewFormActivity.this, uri);
                            Log.i("rex", "abUrl:" + abUrl);
                            EssFile essFile = new EssFile(abUrl);
                            if (essFile.isImage() || essFile.isGif()) {
                                System.out.println("??? " + i + "???" + essFile.getAbsolutePath());
                                currentRichEditor.insertImage(essFile.getAbsolutePath());
                                currentRichEditor.setFontSize(4);
                                currentRichEditor.setEditorFontSize(18);
                            }
                        }
                    }
                } else {
                    Uri uri = data.getData();
                    System.out.println("uri = " + uri.getPath());
                    if (uri != null) {
                        String abUrl = FilesUtils.getPath(ReviewFormActivity.this, uri);
                        Log.i("rex", "abUrl:" + abUrl);
                        EssFile essFile = new EssFile(abUrl);
                        if (essFile.isImage() || essFile.isGif()) {
                            System.out.println("1 =" + essFile.getAbsolutePath());
                            currentRichEditor.insertImage(essFile.getAbsolutePath());
                            currentRichEditor.setFontSize(4);
                            currentRichEditor.setEditorFontSize(18);
                        } else if (essFile.isVideo()) {
                            System.out.println("2");
                            currentRichEditor.insertVideo(essFile.getAbsolutePath());
                        } else if (essFile.isAudio()) {
                            System.out.println("3");
                            currentRichEditor.insertAudio(essFile.getAbsolutePath());
                        } else {
                            System.out.println("4");
                            currentRichEditor.insertFileWithDown(essFile.getAbsolutePath(), "??????:" + essFile.getName());
                        }
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Background
    void createPdf(String name) {
        try {
            getReviewNotes();
            new AddingTable(this, rejectedList, rejectedFlag, scoreList, checkboxList, reviewNoteStr, imgList, name).manipulatePdf(dir + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        for (int i = 0; i < 50; i++) {
            top[i] = allMaterialCardView[i].getTop();
        }
//        System.out.println("top = " + top);
    }

    public void onPdfCreatedListener() {
        progressBar.setVisibility(View.INVISIBLE);
        AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                .setTitle("??????")
                .setMessage("?????????PDF?????????????????????????????????Download????????????????????????")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                            Toast.makeText(ReviewFormActivity.this, "", Toast.LENGTH_SHORT).show();
                        ButtonViewPdfWasClicked();
                    }
                })
                .setNegativeButton("???", new DialogInterface.OnClickListener() {//????????????
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create();
        alertDialog2.show();
    }

    private void init() {
        // ????????????
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
        bt_dimensions = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // ????????? TODO
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
//            tv.setId(String.format("tv" + 2000 + i).hashCode());
            tv.setText(contentList.get(i));
//            tv.setTextSize(18);
            ll_textView.addView(tv);
            ll.addView(ll_textView);
//            ll.setBackground(getResources().getDrawable(R.drawable.focus_error));

            rg.setLayoutParams(rg_dimensions);
            rg_dimensions.setMargins(px_20dp * 2, 0, px_20dp, 0);
            rg.setOrientation(LinearLayout.HORIZONTAL);

            rb.setLayoutParams(rb_dimensions);
            rb2.setLayoutParams(rb_dimensions);
            rb_dimensions.setMargins(0, px_16dp / 2, 0, px_16dp / 2);
            rb.setText("??????");
            rb2.setText("?????????");
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

                    int n = 2; // ???????????????
                    baseNo = (checkedId + (n-1)) - (finalI + 1) * n - position;

                    choose[finalI] = true;
                    mVibrator.vibrate(30);
                    System.out.println("???????????????checkedId +++++ " + checkedId);
                    System.out.println("baseNo +++++ " + baseNo);
                    int id = checkedId - baseNo - 1;
                    System.out.println(id);
                    switch (id % 2) {
                        case 0:
                            rejectedList[id] = 1;
                            rejectedList[id + 1] = 0;
                            break;
//                            rejectedFlag = Constant.REJECTED;
                        case 1:
                            rejectedList[id] = 1;
                            rejectedList[id - 1] = 0;
                            break;
                    }

                    //??????mcv???top
                    for (int i = 0; i < 50; i++) {
                        top[i] = allMaterialCardView[i].getTop();
                    }

                    // ????????????????????????????????????
                    ll.setBackground(null);
                }
            });
//            rejectedList[2 * i] = rb.isChecked() ? 1 : 0;
//            rejectedList[2 * i + 1] = rb2.isChecked() ? 1 : 0;
            ll.addView(rg);
            mcv.addView(ll);
            allMaterialCardView[i] = mcv;
            linearLayout.addView(mcv, i + 3);
        }
        //?????????
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
            LinearLayout ll_editText_inner = new LinearLayout(this);
            Button photoButton = new Button(this);
            Button imageButton = new Button(this);

            mcv.setLayoutParams(mcv_dimensions);
            mcv_dimensions.setMargins(px_16dp, px_16dp / 2, px_16dp, px_16dp);
            mcv.setRadius(4);

            ll.setLayoutParams(ll_dimensions);
            ll.setOrientation(LinearLayout.VERTICAL);

            tv.setLayoutParams(tv_dimensions);
            tv_dimensions.setMargins(px_16dp, px_16dp, px_16dp, 0);
//            tv.setId(String.format("tv" + 2000 + i).hashCode());
            // ??????????????????????????????????????????????????????
            // 3.??????????????????????????? ??????????????????????????????????????????????????????????????????????????????????????????????????? ???????????????
            cb_dimensions.setMargins(px_20dp, 0, px_20dp, 0);
            ArrayList<CheckBox> list = null;
            if (contentList.get(i + 8).contains("???")) {
                String[] split = contentList.get(i + 8).split("???");
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
            rb.setText("??????");
            rb2.setText("????????????");
            rb3.setText("?????????");
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

                    int radioButtonID = group.getCheckedRadioButtonId();
                    View radioButton = group.findViewById(radioButtonID);
                    int position = group.indexOfChild(radioButton);

                    int n = 3; // ???????????????
                    baseNo = (checkedId + (n-1)) - (finalI + 1) * n - position - 16;

                    choose[finalI + 8] = true;
                    mVibrator.vibrate(30);
                    System.out.println("position = " + position);
                    System.out.println("?????????????????????checkedId % 142 +++++ " + checkedId);
                    System.out.println("baseNo ++++" + baseNo);
                    int id = (checkedId - 1) - baseNo - 16;
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

                    if (position % 3 == 1 || position % 3 == 2) {
                        // ??????????????????????????????????????????????????????
                        if (ll_editText.getChildCount() != 0) {
                            ll_editText.setVisibility(View.VISIBLE);
                            return;
                        }
                        ll_editText.setLayoutParams(ll_dimensions);
                        ll_editText.setOrientation(LinearLayout.VERTICAL);
//                        ll_editText.setId(100000 + NO);
                        ll_editText_inner.setLayoutParams(ll_dimensions);
                        ll_editText_inner.setOrientation(LinearLayout.HORIZONTAL);

                        //////
                        currentRichEditor = new RichEditorNew(ReviewFormActivity.this);
//                        richEditor.setEditorFontSize(30);
                        currentRichEditor.setFontSize(4);
                        currentRichEditor.setEditorFontSize(18);
                        LinearLayout.LayoutParams ret_dimensions = new LinearLayout.LayoutParams
                                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        currentRichEditor.setLayoutParams(ret_dimensions);
                        ret_dimensions.setMargins(px_16dp, 0, px_16dp, px_16dp);
                        //??????????????????????????????
                        currentRichEditor.setNeedAutoPosterUrl(true);
                        currentRichEditor.focusEditor();
                        currentRichEditor.setEditorHeight(100);
                        currentRichEditor.setBackgroundColor(Color.LTGRAY);
                        currentRichEditor.setHint("??????????????????????????????+?????????");

                        currentRichEditor.setOnTextChangeListener(new RichEditorNew.OnTextChangeNewListener() {
                            @Override
                            public void onTextChange(String s) {
                                // TODO ??? <br> ??????????????????

                                String replaceHTML = Utils.replaceHTML(s);
                                System.out.println("replaceHTML = " + replaceHTML);
                                System.out.println("gaibian = " + s);
                                int count = 1;
                                int index = 0;
                                index = s.indexOf("<br>");
                                if (index != -1)
                                    count++;
                                while (index < s.length()) {
                                    index = s.indexOf("<br>", index + 1);
                                    if (index != -1)
                                        count++;
                                    else
                                        break;
                                }
                                if (s.length() > 8 && s.substring(s.length() - 8).equals("<br><br>"))
                                    count--;
                                System.out.println(count);
                                System.out.println("??? " + count + "???");
//                                int contentHeight = currentRichEditor.getContentHeight();
//                                int measuredHeight = currentRichEditor.getMeasuredHeight();
//                                int height = currentRichEditor.getHeight();
//                                int minimumHeight = currentRichEditor.getMinimumHeight();
//                                System.out.println("contentHeight = " + contentHeight);
//                                System.out.println("measuredHeight = " + measuredHeight);
//                                System.out.println("height = " + height);
//                                System.out.println("minimumHeight = " + minimumHeight);
                                // ??????????????????
                                String orginHtml = currentRichEditor.getHtml();
                                System.out.println("orginHtml = " + orginHtml + "   currentRichEditor = " + currentRichEditor);
//                                List<String> allSrcAndHref = currentRichEditor.getAllSrcAndHref();
//                                int max = allSrcAndHref.size();
//                                // ?????? img ?????????
//                                imgList.add(finalI, allSrcAndHref);

                                int threshold = 0;
                                int sum = 0;
                                int prev = 0;
                                prev = s.indexOf("<img");
                                if (prev != -1) {
                                    threshold = 1;

                                    while (prev < s.length()) {
                                        System.out.println("index = " + prev);
                                        int next = s.indexOf("<img", prev + 1);
                                        if (next == -1) {
                                            if (threshold == 1)
                                                sum++; // index
                                            break;
                                        }
                                        System.out.println("next = " + next);
                                        String middle = s.substring(prev, next);
                                        int middleIndex = middle.indexOf("<br>");
                                        if (prev != -1 && middleIndex != -1) { // ????????? <br>
                                            if (threshold == 1)
                                                sum++;
                                            threshold = 1;
                                        } else if (prev != -1 && middleIndex == -1) { // ???????????? <br>
                                            if (threshold == 1)
                                                sum++;
                                            threshold++;
                                        }

                                        if (threshold == 4) {
                                            threshold = 1;
                                        }
                                        prev = next;
                                    }
                                }

                                //  richEditor.setEditorHeight(100);
                                int height1 = count >= 4 ? 66 * (count - 4) + 275 : 275;
                                height1 = height1 + sum * 250;
                                System.out.println("???????????? = " + height1);
                                ret_dimensions.height = height1;
//                                richEditor.setMinimumHeight(24*i);
                                currentRichEditor.setLayoutParams(ret_dimensions);

                                updateViewTop(finalI);
                            }
                        });
                        currentRichEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View view, boolean b) {
                                // ??????
                                System.out.println(finalI + " hasFocus = " + b + "   currentRichEditor = " + currentRichEditor);

                                currentRichEditor.setFontSize(4);
                                currentRichEditor.setEditorFontSize(18);

                            }
                        });

                        TextView textView = new TextView(ReviewFormActivity.this);
                        EditText et = new EditText(ReviewFormActivity.this);
                        textView.setLayoutParams(tv_dimensions);
                        tv_dimensions.weight = 1;
                        tv_dimensions.setMargins(px_16dp, 0, px_16dp, 0);
                        textView.setText("?????????????????????");
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
                        ll_editText_inner.addView(textView);

                        // ????????????
                        photoButton.setLayoutParams(bt_dimensions);
                        bt_dimensions.setMargins(0, 0, px_20dp, 0);
                        bt_dimensions.weight = 1;
                        photoButton.setText("??????");
                        photoButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                richEditors[finalI].focusEditor();
                                currentRichEditor = richEditors[finalI];
                                //???????????????
                                takePhoto(finalI);
                            }
                        });
                        ll_editText_inner.addView(photoButton);
                        // ????????????
                        imageButton.setLayoutParams(bt_dimensions);
                        imageButton.setText("??????");
                        imageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                closeSoftKeyInput();//???????????????
//                                callGallery();
                                richEditors[finalI].focusEditor();
                                currentRichEditor = richEditors[finalI];
                                openDirChooseFile();
                            }
                        });
                        ll_editText_inner.addView(imageButton);

                        ll_editText.addView(ll_editText_inner);
                        ll_editText.addView(currentRichEditor);
                        richEditors[finalI] = currentRichEditor;
//                        ll_editText.addView(et);
                        ll_editText.setVisibility(View.VISIBLE);
                        reviewNotes[finalI] = et;
                        ll.addView(ll_editText);
                    } else {
                        //??????
                        if (ll_editText.getVisibility() == View.VISIBLE)
//                        if (ll_editText != null)
//                            findViewById(100000 + NO).setVisibility(View.GONE);
                            ll_editText.setVisibility(View.GONE);
                    }

                    // ??????mcv???top
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
            linearLayout.addView(mcv, i + 12);
        }
    }

    public void updateViewTop(int finalI) {
        // ??????mcv???top
        allMaterialCardView[finalI + 8].post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 50; i++) {
                    top[i] = allMaterialCardView[i].getTop();
                }
                System.out.println("??????top" + (finalI + 8) + " = " + top[finalI+8]);
                int mheight = allMaterialCardView[finalI + 8].getMeasuredHeight();
                System.out.println("w = " + mheight);
                int height = allMaterialCardView[finalI + 8].getHeight();
                System.out.println("w = " + height);
            }
        });
    }

    public ArrayList<String> ReadTxtFile(int file) {
        ArrayList<String> contentList = new ArrayList<>(); //?????????????????????
        //??????path????????????????????????????????????????????????????????????
        try {
            InputStream instream = getResources().openRawResource(file);
            if (instream != null) {
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                //????????????
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

        for (MaterialCardView cardView : allMaterialCardView) {
            cardView = null;
        }
        allMaterialCardView = null;

        System.out.println("destory...");
    }

    /**
     * ???????????????
     */
    private void closeSoftKeyInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        //boolean isOpen=imm.isActive();//isOpen?????????true???????????????????????????
        if (imm != null && imm.isActive() && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            //imm.hideSoftInputFromInputMethod();//????????????
            //imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0); //??????????????????
            //?????????????????????????????????????????????????????????????????????
            //imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????
     */
    public void openDirChooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);//??????
        String relativePath = "DCIM%2fCamera";
        Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:" + relativePath);
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        startActivityForResult(intent, RESULT_CHOOSE);
    }

    /**
     * ??????????????????????????????
     */
    public void takePhoto(int finalI) {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = Utils.getOutputMediaFileUri(ReviewFormActivity.this, finalI);
        System.out.println("imageUri = " + imageUri);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        //Android7.0????????????????????????????????????????????????
        openCameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                                startActivityForResult(openCameraIntent, CAMERA_RESULT);
        startActivityForResult(openCameraIntent, RESULT_CAMERA);
    }

    private void getReviewNotes() {
        for (int i = 0; i < 42; i++) {
            if (richEditors[i] != null) {
                // ??????
                String originHTML = richEditors[i].getHtml();
                if (originHTML != null) {
                    System.out.println("originHTML = " + originHTML);
                    String replaceHTML = Utils.replaceHTML(originHTML);
                    reviewNoteStr[i] = replaceHTML;
                }
                // ??????
                List<String> allSrcAndHref = richEditors[i].getAllSrcAndHref();
                imgList.add(i, allSrcAndHref);
            } else {
                imgList.add(i, null);
            }
        }
        for (int j = 0; j < 42; j++) {
            System.out.println("reviewNoteStr[" + j + "] " + reviewNoteStr[j]);
        }
    }
}