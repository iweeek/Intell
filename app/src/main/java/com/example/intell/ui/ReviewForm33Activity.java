package com.example.intell.ui;


import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import com.example.intell.tool.AddingTable33;
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
import java.io.File;
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
 * 建设用地土壤污染状况调查现场采样检查记录表
 */
@EActivity(R.layout.activity_review_33_form)
public class ReviewForm33Activity extends AppCompatActivity {

    private static final String TAG = ReviewForm33Activity.class.getSimpleName();
    public final static int RESULT_CHOOSE = 123;
    private static final int RESULT_CAMERA = 124;

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
    @ViewById(R.id.name)
    EditText nameTextView;
    @ViewById(R.id.ll_name)
    LinearLayout ll_name;
    RichEditorNew currentRichEditor;

    @NonConfigurationInstance
    Uri uri;

    private boolean rejectedFlag;
    private Integer[] checkList = new Integer[46]; // 否决项结果
    //    private Integer[] scoreList = new Integer[126]; // 打分项结果
    private EditText[] reviewNotes = new EditText[23];
    private String[] reviewNoteStr = new String[23];
    //    private ArrayList<CheckBox> checkboxList[] = new ArrayList[42]; // checkbox结果列表
    private MaterialCardView allMaterialCardView[] = new MaterialCardView[23];
    private boolean choose[] = new boolean[23]; // 检查是否已经选择
    private Integer top[] = new Integer[23];
    RichEditorNew[] richEditors = new RichEditorNew[23];
    ArrayList<List<String>> imgList = new ArrayList<>(23);
    private int baseNo = -1;

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
        topView.setText(getResources().getString(R.string.check_form_33_title));
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

    @Click({R.id.bt_preview31, R.id.bt_preview31_1})
    void ButtonPreviewWasClicked() {
//        System.out.println("click!");
//        int top = allLinearLayout[7].getTop();
        if (nameTextView.getText().toString().trim().isEmpty()) {
            scrollView.smoothScrollTo(0, 0);
            Toast.makeText(this, "请输入项目名称 :)", Toast.LENGTH_SHORT).show();
            ll_name.setBackground(getResources().getDrawable(R.drawable.focus_error));
        } else {
            for (int i = 0; i < 23; i++) {
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

    @Click({R.id.bt_pdf31, R.id.bt_pdf31_1})
    void ButtonPdfWasClicked() {
        System.out.println("bt_pdf");
        if (nameTextView.getText().toString().trim().isEmpty()) {
            scrollView.smoothScrollTo(0, 0);
            Toast.makeText(this, "请输入项目名称 :)", Toast.LENGTH_SHORT).show();
            ll_name.setBackground(getResources().getDrawable(R.drawable.focus_error));
        } else {
            try {
                dir = Environment.getExternalStorageDirectory().getCanonicalPath();
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                filePath = "/Download/" + nameTextView.getText().toString().trim() + "_" +
                        getResources().getString(R.string.check_form_33_title) + "_" + sdf.format(date) + ".pdf";
                System.out.println("dir =" + dir); // str=/storage/emulated/0

                progressBar.setVisibility(View.VISIBLE);
                createPdf(nameTextView.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Click({R.id.bt_view_pdf31, R.id.bt_view_pdf31_1})
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
                if (resultCode == RESULT_OK) {
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
                } else if (resultCode == RESULT_CANCELED) {
                    File fdelete = new File(imageUri.getPath().substring(5));
                    if (fdelete.exists()) {
                        if (fdelete.delete()) {
                            System.out.println("file Deleted :" + imageUri.getPath());
                        } else {
                            System.out.println("file not Deleted :" + imageUri.getPath());
                        }
                    }
                }
                break;
            case RESULT_CHOOSE:
                if (data == null) return;
                // 使用EXTRA_ALLOW_MULTIPLE时，当用户选择的内容不止一个时，intent.getExtra()intent中的数据不返回，而是返回intent ClipData，仅SDK 18及更高版本支持。
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) && (null == data.getData())) {
                    ClipData clipdata = data.getClipData();
                    for (int i = 0; i < clipdata.getItemCount(); i++) {
//                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), clipdata.getItemAt(i).getUri());
                        Uri uri = clipdata.getItemAt(i).getUri();
                        System.out.println("uri = " + uri.getPath());
                        if (uri != null) {
                            String abUrl = FilesUtils.getPath(ReviewForm33Activity.this, uri);
                            Log.i("rex", "abUrl:" + abUrl);
                            EssFile essFile = new EssFile(abUrl);
                            if (essFile.isImage() || essFile.isGif()) {
                                System.out.println("第 " + i + "个" + essFile.getAbsolutePath());
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
                        String abUrl = FilesUtils.getPath(ReviewForm33Activity.this, uri);
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
                            currentRichEditor.insertFileWithDown(essFile.getAbsolutePath(), "文件:" + essFile.getName());
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
            new AddingTable33(this, checkList, rejectedFlag, reviewNoteStr, imgList, name).manipulatePdf(dir + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        for (int i = 0; i < 23; i++) {
            top[i] = allMaterialCardView[i].getTop();
        }
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

        ArrayList<String> contentList = ReadTxtFile(R.raw.review_form_33);

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

        // 否决项 TODO
        for (int i = 0; i < 23; i++) {

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
            LinearLayout ll_editText_inner = new LinearLayout(this);
            Button photoButton = new Button(this);
            Button imageButton = new Button(this);

            mcv.setLayoutParams(mcv_dimensions);
            mcv_dimensions.setMargins(px_16dp, 0, px_16dp, px_16dp);
            mcv.setRadius(4);
            mcv.setElevation(10);

            ll.setLayoutParams(ll_dimensions);
            ll.setOrientation(LinearLayout.VERTICAL);
            ll_textView.setLayoutParams(ll_dimensions);
            ll_textView.setOrientation(LinearLayout.HORIZONTAL);

            String content = contentList.get(i);
            if (contentList.get(i).charAt(0) == '*') {
                tv_star.setText("*");
                tv_star.setTextColor(Color.RED);
                tv_star.setTypeface(null, Typeface.BOLD);
                content = content.substring(1); // 过滤掉"*"
            }
            tv_star.setLayoutParams(tv_left_dimensions);
            ll_textView.addView(tv_star);

            tv_NO.setLayoutParams(tv_middle_dimensions);
            tv_NO.setText((i + 1) + ".");
            ll_textView.addView(tv_NO);

            tv.setLayoutParams(tv_right_dimensions);
            tv_dimensions.setMargins(px_16dp, px_16dp, px_16dp, 0);
            tv_left_dimensions.setMargins(px_16dp, px_16dp, 0, 0);
            tv_middle_dimensions.setMargins(0, px_16dp, px_16dp / 2, 0);
            tv_right_dimensions.setMargins(0, px_16dp, px_16dp, 0);

            String[] split = content.split("@");
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
                        spannableString.setSpan(span, 0, split[0].length() + 1 + split[1].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
                    baseNo = (checkedId + (n - 1)) - (finalI + 1) * n - position;

                    choose[finalI] = true;
                    mVibrator.vibrate(30);
                    System.out.println("当前累计 checkedId +++++ " + checkedId);
                    System.out.println("baseNo ++++" + baseNo);
                    if (baseNo == -1)
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
                    for (int i = 0; i < 23; i++) {
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
                        ll_editText_inner.setLayoutParams(ll_dimensions);
                        ll_editText_inner.setOrientation(LinearLayout.HORIZONTAL);

                        //////
                        currentRichEditor = new RichEditorNew(ReviewForm33Activity.this);
//                        richEditor.setEditorFontSize(30);
                        currentRichEditor.setFontSize(4);
                        currentRichEditor.setEditorFontSize(18);
                        LinearLayout.LayoutParams ret_dimensions = new LinearLayout.LayoutParams
                                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        currentRichEditor.setLayoutParams(ret_dimensions);
                        ret_dimensions.setMargins(px_16dp, 0, px_16dp, px_16dp);
                        //自动为视频添加缩略图
                        currentRichEditor.setNeedAutoPosterUrl(true);
                        currentRichEditor.focusEditor();
                        currentRichEditor.setEditorHeight(100);
                        currentRichEditor.setBackgroundColor(Color.LTGRAY);
                        currentRichEditor.setHint("请输入审查说明（文字+图片）");

                        currentRichEditor.setOnTextChangeListener(new RichEditorNew.OnTextChangeNewListener() {
                            @Override
                            public void onTextChange(String s) {
                                // TODO 将 <br> 转变为换行符

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
                                System.out.println("有 " + count + "行");
//                                int contentHeight = currentRichEditor.getContentHeight();
//                                int measuredHeight = currentRichEditor.getMeasuredHeight();
//                                int height = currentRichEditor.getHeight();
//                                int minimumHeight = currentRichEditor.getMinimumHeight();
//                                System.out.println("contentHeight = " + contentHeight);
//                                System.out.println("measuredHeight = " + measuredHeight);
//                                System.out.println("height = " + height);
//                                System.out.println("minimumHeight = " + minimumHeight);
                                // 计算图片数量
                                String orginHtml = currentRichEditor.getHtml();
                                System.out.println("orginHtml = " + orginHtml + "   currentRichEditor = " + currentRichEditor);
//                                List<String> allSrcAndHref = currentRichEditor.getAllSrcAndHref();
//                                int max = allSrcAndHref.size();
//                                // 获取 img 字符串
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
                                        if (prev != -1 && middleIndex != -1) { // 中间有 <br>
                                            if (threshold == 1)
                                                sum++;
                                            threshold = 1;
                                        } else if (prev != -1 && middleIndex == -1) { // 中间没有 <br>
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
                                System.out.println("设置高度 = " + height1);
                                ret_dimensions.height = height1;
//                                richEditor.setMinimumHeight(24*i);
                                currentRichEditor.setLayoutParams(ret_dimensions);

                                updateViewTop(finalI);
                            }
                        });
                        currentRichEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View view, boolean b) {
                                // 切换
                                System.out.println(finalI + " hasFocus = " + b + "   currentRichEditor = " + currentRichEditor);

                                currentRichEditor.setFontSize(4);
                                currentRichEditor.setEditorFontSize(18);

                            }
                        });

                        TextView textView = new TextView(ReviewForm33Activity.this);
                        EditText et = new EditText(ReviewForm33Activity.this);
                        textView.setLayoutParams(tv_dimensions);
                        tv_dimensions.weight = 1;
                        tv_dimensions.setMargins(px_16dp, 0, px_16dp, 0);
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
                        ll_editText_inner.addView(textView);

                        // 拍照按钮
                        photoButton.setLayoutParams(bt_dimensions);
                        bt_dimensions.setMargins(0, 0, px_20dp, 0);
                        bt_dimensions.weight = 1;
                        photoButton.setText("拍照");
                        photoButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                richEditors[finalI].focusEditor();
                                currentRichEditor = richEditors[finalI];
                                //打开照相机
                                takePhoto(finalI);
                            }
                        });
                        ll_editText_inner.addView(photoButton);
                        // 图片按钮
                        imageButton.setLayoutParams(bt_dimensions);
                        imageButton.setText("图片");
                        imageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                closeSoftKeyInput();//关闭软键盘
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
            linearLayout.addView(mcv, i + 3);
        }
    }

    public void updateViewTop(int finalI) {
        // 更新mcv的top
        allMaterialCardView[finalI].post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 23; i++) {
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

    /**
     * 关闭软键盘
     */
    private void closeSoftKeyInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        //boolean isOpen=imm.isActive();//isOpen若返回true，则表示输入法打开
        if (imm != null && imm.isActive() && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            //imm.hideSoftInputFromInputMethod();//据说无效
            //imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0); //强制隐藏键盘
            //如果输入法在窗口上已经显示，则隐藏，反之则显示
            //imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 这里采用系统自带方法，可替换为你更方便的自定义文件选择器
     */
    public void openDirChooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);//多选
        String relativePath = "DCIM%2fCamera";
        Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:" + relativePath);
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        startActivityForResult(intent, RESULT_CHOOSE);
    }

    /**
     * 这里采用系统自带相机
     */
    public void takePhoto(int finalI) {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = Utils.getOutputMediaFileUri(ReviewForm33Activity.this, finalI);
        System.out.println("imageUri = " + imageUri);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        //Android7.0添加临时权限标记，此步千万别忘了
        openCameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                                startActivityForResult(openCameraIntent, CAMERA_RESULT);
        startActivityForResult(openCameraIntent, RESULT_CAMERA);
    }

    private void getReviewNotes() {
        for (int i = 0; i < 23; i++) {
            if (richEditors[i] != null) {
                // 文字
                String originHTML = richEditors[i].getHtml();
                if (originHTML != null) {
                    System.out.println("originHTML = " + originHTML);
                    String replaceHTML = Utils.replaceHTML(originHTML);
                    reviewNoteStr[i] = replaceHTML;
                }
                // 图片
                List<String> allSrcAndHref = richEditors[i].getAllSrcAndHref();
                imgList.add(i, allSrcAndHref);
            } else {
                imgList.add(i, null);
            }
        }
        for (int j = 0; j < 23; j++) {
            System.out.println("reviewNoteStr[" + j + "] " + reviewNoteStr[j]);
        }
    }
}