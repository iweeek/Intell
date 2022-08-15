package com.example.intell.ui;


import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.intell.R;
import com.example.intell.common.CommonUtil;
import com.example.intell.common.ImageUtils;
import com.example.intell.common.MyGlideEngine;
import com.example.intell.common.SDCardUtil;
import com.example.intell.entry.EnvironmentData;
import com.example.intell.network.EnvironmentService;
import com.example.intell.network.ServiceCreator;
import com.example.intell.tool.AddingTable34;
import com.example.intell.tool.Utils;
import com.google.android.material.card.MaterialCardView;
import com.rex.editor.common.EssFile;
import com.rex.editor.common.FilesUtils;
import com.rex.editor.view.RichEditorNew;
import com.sendtion.xrichtext.RichTextEditor;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.ViewById;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * 建设用地土壤污染状况调查现场采样检查记录表
 */
@EActivity(R.layout.activity_review_34_form)
public class ReviewForm34Activity extends AppCompatActivity {

    private static final String TAG = ReviewForm34Activity.class.getSimpleName();
    private static final int CAMERA_PERMISSION_CODE = 1;
    private static final int WRITE_PERMISSION_CODE = 2;
    private static final int READ_PERMISSION_CODE = 3;
    private static final int REQUEST_CODE_CHOOSE = 23;//定义请求码常量
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
    RichEditorNew currentRichEditor;

    @NonConfigurationInstance
    Uri uri;

    private boolean rejectedFlag;
    private Integer[] checkList = new Integer[55]; // 否决项结果
    //    private Integer[] scoreList = new Integer[126]; // 打分项结果
    private EditText[] reviewNotes = new EditText[20];
    private String[] reviewNoteStr = new String[20];
    //    private ArrayList<CheckBox> checkboxList[] = new ArrayList[42]; // checkbox结果列表
    private MaterialCardView allMaterialCardView[] = new MaterialCardView[20];
    private LinearLayout ll_photos[] = new LinearLayout[20];
    private boolean choose[] = new boolean[20];
    private Integer top[] = new Integer[20];
    RichEditorNew[] richEditors = new RichEditorNew[20];
    ArrayList<List<String>> imgList = new ArrayList<>(20);
    private int baseNo = -1;

    private Vibrator mVibrator;

    private File outputImage;
    private Uri imageUri;
    private int takePhoto = 1;
    private static final int CAMERA_RESULT = 1;
    private ActivityResultLauncher<Uri> mGetContent;

    private int px_16dp;
    private int px_20dp;

    private int screenWidth;
    private int screenHeight;

    LinearLayout.LayoutParams mcv_dimensions;
    LinearLayout.LayoutParams ll_dimensions;
    LinearLayout.LayoutParams tv_dimensions;
    LinearLayout.LayoutParams tv_left_dimensions;
    LinearLayout.LayoutParams tv_middle_dimensions;
    LinearLayout.LayoutParams tv_right_dimensions;
    LinearLayout.LayoutParams rg_dimensions;
    LinearLayout.LayoutParams rb_dimensions;
    LinearLayout.LayoutParams rb3_dimensions;
    LinearLayout.LayoutParams cb_dimensions;
    LinearLayout.LayoutParams et_dimensions;
    LinearLayout.LayoutParams bt_dimensions;

    @AfterViews
    void updateViews() {
        init();
        topView.setText(getResources().getString(R.string.check_form_34_title));
        getEnvironmentByNetwork();

        screenWidth = CommonUtil.getScreenWidth(this);
        screenHeight = CommonUtil.getScreenHeight(this);

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

        //////////////////////////////////////////////////////////////////////////////////////
        outputImage = new File(getExternalCacheDir(), "output_image.jpg");
//        File externalStorageDirectory = Environment.getExternalStorageDirectory();
//        File dataDirectory = Environment.getDataDirectory();
//        File downloadCacheDirectory = Environment.getDownloadCacheDirectory();
//        File rootDirectory = Environment.getRootDirectory();
//        File externalCacheDir = getExternalCacheDir();
//        File dataDir = getDataDir();
//        File cacheDir = getCacheDir();
//        File filesDir = getFilesDir();
//        File codeCacheDir = getCodeCacheDir();

//        System.out.println("牛啊");
//        System.out.println(externalStorageDirectory);
//        System.out.println(dataDirectory);
//        System.out.println(downloadCacheDirectory);
//        System.out.println(rootDirectory);
//        System.out.println(externalCacheDir);
//        System.out.println(dataDir);
//        System.out.println(cacheDir);
//        System.out.println(filesDir);
//        System.out.println(codeCacheDir);

        /*
        Utils.requestPermission(ReviewForm34Activity.this);

        if (outputImage.exists()) {
            outputImage.delete();
        }
        try {
            outputImage.createNewFile();
            //  如果运行设备的系统高于Android 7.0
            //  就调用FileProvider的getUriForFile()方法将File对象转换成一个封装过的Uri对象。
            //  该方法接收3个参数：Context对象， 任意唯一的字符串， 创建的File对象。
            //  这样做的原因：Android 7.0 开始，直接使用本地真实路径的Uri是被认为是不安全的，会抛出FileUriExposedException异常；
            //      而FileProvider是一种特殊的ContentProvider，他使用了和ContentProvider类似的机制对数据进行保护，可以选择性地将封装过的Uri共享给外部。
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imageUri = FileProvider.getUriForFile(this, "com.example.intell.provider", outputImage);
            } else {
                //  否则，就调用Uri的fromFile()方法将File对象转换成Uri对象
                imageUri = Uri.fromFile(outputImage);
            }
            //  启动相机
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            //  指定图片的输出地址,这样拍下的照片会被输出到output_image.jpg中。
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);


            //打开照相机
//            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            imageUri = Utils.getOutputMediaFileUri(this);
//            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//
//            //Android7.0添加临时权限标记，此步千万别忘了
//            openCameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            startActivityForResult(openCameraIntent, CAMERA_RESULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

         */


        mGetContent = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        System.out.println("返回了哦");
                    }
                });
    }

    int tops = 0;

    @Click({R.id.bt_preview31, R.id.bt_preview31_1})
    void ButtonPreviewWasClicked() {
        System.out.println("click!");
//        int top = allLinearLayout[7].getTop();
        for (int i = 0; i < 20; i++) {
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
            filePath = "/Download/" + getResources().getString(R.string.check_form_34_title) + ".pdf";
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

    @Click(R.id.bt_photo)
    void ButtonTakePhoto() {
        /*outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        if (outputImage.exists()) {
            outputImage.delete();
        }
        try {
            outputImage.createNewFile();
            //  如果运行设备的系统高于Android 7.0
            //  就调用FileProvider的getUriForFile()方法将File对象转换成一个封装过的Uri对象。
            //  该方法接收3个参数：Context对象， 任意唯一的字符串， 创建的File对象。
            //  这样做的原因：Android 7.0 开始，直接使用本地真实路径的Uri是被认为是不安全的，会抛出FileUriExposedException异常；
            //      而FileProvider是一种特殊的ContentProvider，他使用了和ContentProvider类似的机制对数据进行保护，可以选择性地将封装过的Uri共享给外部。
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imageUri = FileProvider.getUriForFile(this, "com.example.intell.provider", outputImage);
            } else {
                //  否则，就调用Uri的fromFile()方法将File对象转换成Uri对象
                imageUri = Uri.fromFile(outputImage);
            }
            //  启动相机
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            //  指定图片的输出地址,这样拍下的照片会被输出到output_image.jpg中。
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//            registerForActivityResult(intent, takePhoto);
//            ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
//                    new ActivityResultCallback<Uri>() {
//                @Override
//                public void onActivityResult(Uri result) {
//                    System.out.println("返回了哦");
//                }
//            });
//            File file = new File(getFilesDir(), "picFromCamera");
//            Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);


//            ActivityResultLauncher<Void> mTakePicture =
//                    registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), new ActivityResultCallback<Bitmap>() {
//                        @Override
//                        public void onActivityResult(Bitmap thumbnail) {
//                            mThumbnailLiveData.setValue(thumbnail);
//                        }
//                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
        */

//        mGetContent.launch(imageUri);
        /*
        //打开照相机
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = Utils.getOutputMediaFileUri(this, 0);
        System.out.println("hahaha = " + imageUri);
        System.out.println(Environment.getExternalStorageDirectory().getAbsolutePath());
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        //Android7.0添加临时权限标记，此步千万别忘了
        openCameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(openCameraIntent, CAMERA_RESULT);
        */
        getReviewNotes();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_RESULT:
                if (resultCode != RESULT_OK) {
                    return;
                }
                System.out.println("hahah" + imageUri);
                Bitmap bitmap;
                int id = -1;
                try {
                    //这里imageUri就是上面获取到的url，下面会讲到
                    bitmap = Utils.getBitmapFormUri(this, imageUri);
                    String[] split = imageUri.toString().split("_");
                    for (String s : split) {
                        System.out.println(s);
                    }
//                    if (split.length > 1 && split[split.length - 1] != null)
//                        id = Integer.parseInt(split[split.length - 1].split("\\.")[0]);
//
//                    if (id != -1) {
//                        ImageView iv = new ImageView(this);
//                        iv.setLayoutParams(iv_dimensions);
//                        iv.setImageBitmap(bitmap);
//                        ll_photos[id].addView(iv);
//                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case REQUEST_CODE_CHOOSE:
                //异步方式插入图片
//                insertImagesSync(data);
                break;
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
                // 使用EXTRA_ALLOW_MULTIPLE时，当用户选择的内容不止一个时，intent.getExtra()intent中的数据不返回，而是返回intent ClipData，仅SDK 18及更高版本支持。
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) && (null == data.getData())) {
                    ClipData clipdata = data.getClipData();
                    for (int i = 0; i < clipdata.getItemCount(); i++) {
//                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), clipdata.getItemAt(i).getUri());
                        Uri uri = clipdata.getItemAt(i).getUri();
                        System.out.println("uri = " + uri.getPath());
                        if (uri != null) {
                            String abUrl = FilesUtils.getPath(ReviewForm34Activity.this, uri);
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
                        String abUrl = FilesUtils.getPath(ReviewForm34Activity.this, uri);
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
    void createPdf() {
        try {
            getReviewNotes();
            new AddingTable34(this, checkList, rejectedFlag, reviewNoteStr, imgList).manipulatePdf(dir + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        for (int i = 0; i < 20; i++) {
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

        ArrayList<String> contentList = ReadTxtFile(R.raw.review_form_34);

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
        rb3_dimensions = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cb_dimensions = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        et_dimensions = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bt_dimensions = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // 否决项 TODO
        for (int i = 0; i < 20; i++) {
            System.out.println("运行 " + i);

            MaterialCardView mcv = new MaterialCardView(this);
            LinearLayout ll = new LinearLayout(this);
            TextView tv = new TextView(this);
            RadioGroup rg = new RadioGroup(this);
            RadioButton rb = new RadioButton(this);
            RadioButton rb2 = new RadioButton(this);
            RadioButton rb3 = null;
            boolean threeOption = false;
            if (!(i < 4 || i == 13)) {
                rb3 = new RadioButton(this);
                threeOption = true;
            }
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

//            rb.setLayoutParams(rb_dimensions);
//            rb2.setLayoutParams(rb_dimensions);
//            rb_dimensions.setMargins(px_16dp * 2, px_16dp / 2, 0, px_16dp / 2);
            rb.setText("是");
            rb2.setText("否");
//            rb_dimensions.weight = 1;
            rb.setTextSize(16);
            rb2.setTextSize(16);
            rg.addView(rb);
            rg.addView(rb2);
            if (rb3 != null) {
                rb3.setText("材料不支撑判断");
                rb3.setTextSize(16);
                rg.addView(rb3);
                rb3_dimensions.setMargins(0, px_16dp / 2, 0, px_16dp / 2);
                rb3_dimensions.weight = 1;
                rb.setLayoutParams(rb3_dimensions);
                rb2.setLayoutParams(rb3_dimensions);
                rb3.setLayoutParams(rb3_dimensions);
            } else {
                rb.setLayoutParams(rb_dimensions);
                rb2.setLayoutParams(rb_dimensions);
                rb_dimensions.setMargins(px_16dp * 2, px_16dp / 2, 0, px_16dp / 2);
                rb_dimensions.weight = 1;
            }

            int finalI = i;

            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    int radioButtonID = group.getCheckedRadioButtonId();
                    View radioButton = group.findViewById(radioButtonID);
                    int position = group.indexOfChild(radioButton);
                    System.out.println("position = " + position);

                    int n = 2; // 单选组个数
                    if (finalI < 4) {
                        n = 2;
                        baseNo = (checkedId + (n - 1)) - (finalI + 1) * n - position;
                    } else if (finalI == 13) {
                        baseNo = checkedId - 36 - position;
                    } else {
                        n = 3;
                        if (finalI >= 4 && finalI <= 12) { // 第5至13题
                            baseNo = (checkedId + (n - 1)) - (finalI - 4 + 1) * n - 8 - position;
                        } else {
                            baseNo = (checkedId + (n - 1)) - (finalI - 14 + 1) * n - 37 - position;
                        }
                    }

                    choose[finalI] = true;
                    mVibrator.vibrate(30);
                    System.out.println("当前累计 checkedId +++++ " + checkedId);
                    System.out.println("baseNo ++++" + baseNo);
                    if (baseNo == -1)
                        System.out.println("出问题了。");
                    int id = (checkedId - 1) - baseNo;
                    System.out.println(id);
                    System.out.println("finalI = " + finalI);
                    if (finalI < 4) {
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
                    } else if (finalI == 13) {
                        if (id == 35) {
                            checkList[id] = 1;
                            checkList[id + 1] = 0;
                        }
                        if (id == 36) {
                            checkList[id] = 1;
                            checkList[id - 1] = 0;
                        }
                    } else {
                        if (finalI >= 4 && finalI <= 12) { // 第5至13题
                            switch ((id - 8) % 3) {
                                case 0:
                                    checkList[id] = 1;
                                    checkList[id + 1] = 0;
                                    checkList[id + 2] = 0;
                                    break;
                                case 1:
                                    checkList[id] = 1;
                                    checkList[id - 1] = 0;
                                    checkList[id + 1] = 0;
                                    break;
                                case 2:
                                    checkList[id] = 1;
                                    checkList[id - 1] = 0;
                                    checkList[id - 2] = 0;
                                    break;
                            }
                        } else {
                            switch ((id - 37) % 3) {
                                case 0:
                                    checkList[id] = 1;
                                    checkList[id + 1] = 0;
                                    checkList[id + 2] = 0;
                                    break;
                                case 1:
                                    checkList[id] = 1;
                                    checkList[id - 1] = 0;
                                    checkList[id + 1] = 0;
                                    break;
                                case 2:
                                    checkList[id] = 1;
                                    checkList[id - 1] = 0;
                                    checkList[id - 2] = 0;
                                    break;
                            }
                        }
                    }

                    //更新mcv的top
                    for (int i = 0; i < 20; i++) {
                        top[i] = allMaterialCardView[i].getTop();
                    }

                    // 点击选项后隐藏错误提示框
                    ll.setBackground(null);

                    if (needShowEditText(id + 1)) {
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
                        currentRichEditor = new RichEditorNew(ReviewForm34Activity.this);
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

                        TextView textView = new TextView(ReviewForm34Activity.this);
                        EditText et = new EditText(ReviewForm34Activity.this);
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
                        et_dimensions.setMargins(px_16dp, 0, px_20dp, px_16dp / 2);
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
            linearLayout.addView(mcv, i + 2);
        }
    }

    private boolean needShowEditText(int checkedId) {
        if (checkedId <= 8) {
            if (checkedId % 2 == 0)
                return true;
        } else if (checkedId == 36 || checkedId == 37) {
            if (checkedId == 37) return true;
        } else {
            if (checkedId > 8 && checkedId <= 35) {
                if ((checkedId - 8) % 3 == 0 || (checkedId - 8) % 3 == 2) return true;
            } else {
                if ((checkedId - 37) % 3 == 0 || (checkedId - 37) % 3 == 2) return true;
            }
        }
        System.out.println("返回false");
        return false;
    }

    public void updateViewTop(int finalI) {
        // 更新mcv的top
        allMaterialCardView[finalI].post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 20; i++) {
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
        } catch (FileNotFoundException e) {
            Log.d("TestFile", "The File doesn't not exist.");
        } catch (IOException e) {
            Log.d("TestFile", e.getMessage());
        }
        return contentList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        if (requestCode == CAMERA_PERMISSION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                //打开照相机
//                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                imageUri = Utils.getOutputMediaFileUri(ReviewForm34Activity.this);
//                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//
//                //Android7.0添加临时权限标记，此步千万别忘了
//                openCameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                startActivityForResult(openCameraIntent, CAMERA_RESULT);
//            } else {
//                Toast.makeText(ReviewForm34Activity.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
//            }
//        }
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
     * 调用图库选择
     */
    private void callGallery() {
//        //调用系统图库
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");// 相片类型
//        startActivityForResult(intent, 1);

        Matisse.from(this)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.GIF))//照片视频全部显示MimeType.allOf()
                .countable(true)//true:选中后显示数字;false:选中后显示对号
                .maxSelectable(3)//最大选择数量为9
                //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))//图片显示表格的大小
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//图像选择和预览活动所需的方向
                .thumbnailScale(0.85f)//缩放比例
                .theme(R.style.Matisse_Zhihu)//主题  暗色主题 R.style.Matisse_Dracula
                .imageEngine(new MyGlideEngine())//图片加载方式，Glide4需要自定义实现
                .capture(true) //是否提供拍照功能，兼容7.0系统需要下面的配置
                //参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
                .captureStrategy(new CaptureStrategy(true, "com.sendtion.matisse.fileprovider"))//存储到哪里
                .forResult(REQUEST_CODE_CHOOSE);//请求码
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
        System.out.println("进来了！！");
        startActivityForResult(intent, RESULT_CHOOSE);
    }

    /**
     * 这里采用系统自带相机
     */
    public void takePhoto(int finalI) {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = Utils.getOutputMediaFileUri(ReviewForm34Activity.this, finalI);
        System.out.println("imageUri = " + imageUri);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        //Android7.0添加临时权限标记，此步千万别忘了
        openCameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                                startActivityForResult(openCameraIntent, CAMERA_RESULT);
        startActivityForResult(openCameraIntent, RESULT_CAMERA);
    }

    /**
     * 打开软键盘
     */
    private void openSoftKeyInput(){
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        //boolean isOpen=imm.isActive();//isOpen若返回true，则表示输入法打开
        if (imm != null && !imm.isActive() && currentRichEditor != null){
            currentRichEditor.requestFocus();
            //第二个参数可设置为0
            //imm.showSoftInput(et_content, InputMethodManager.SHOW_FORCED);//强制显示
            imm.showSoftInputFromInputMethod(currentRichEditor.getWindowToken(),
                    InputMethodManager.SHOW_FORCED);
        }
    }

    private void getReviewNotes() {
        for (int i = 0; i < 20; i++) {
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
        for (int j = 0; j < 20; j++) {
            System.out.println("reviewNoteStr[" + j + "] " + reviewNoteStr[j]);
        }
    }
}