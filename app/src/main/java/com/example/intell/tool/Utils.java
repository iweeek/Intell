package com.example.intell.tool;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.rex.editor.view.RichEditorNew;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static Bitmap getBitmapFormUri(Context context, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);

        //这一段代码是不加载文件到内存中也得到bitmap的真是宽高，主要是设置inJustDecodeBounds为true
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;//不加载到内存
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.RGB_565;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;

        //图片分辨率以480x800为标准
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比，由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return compressImage(bitmap);//再进行质量压缩
    }

    public static Bitmap getBitmapFormUri(Context context, String path) throws FileNotFoundException, IOException {
        File file = new File(path);
        FileInputStream input = new FileInputStream(file);

        //这一段代码是不加载文件到内存中也得到bitmap的真是宽高，主要是设置inJustDecodeBounds为true
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;//不加载到内存
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.RGB_565;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;

        //图片分辨率以480x800为标准
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比，由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        input = new FileInputStream(file);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return compressImage(bitmap);//再进行质量压缩
    }

    public static Uri getOutputMediaFileUri(Context context, int id) {
        File mediaFile = null;
        String cameraPath;
        try {
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null;
                }
            }
            System.out.println(mediaStorageDir.getPath());
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            mediaFile = new File(mediaStorageDir.getPath()
                    + File.separator
                    + "DCIM/Camera/IMG_" + sdf.format(date) + "_" + id + ".jpg");//注意这里需要和filepaths.xml中配置的一样
            System.out.println(mediaFile.getAbsolutePath());
            mediaFile.createNewFile();
            cameraPath = mediaFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {// sdk >= 24  android7.0以上
            Uri contentUri = FileProvider.getUriForFile(context,
                    context.getApplicationContext().getPackageName() + ".provider",//与清单文件中android:authorities的值保持一致
                    mediaFile);//FileProvider方式或者ContentProvider。也可使用VmPolicy方式
            return contentUri;

        } else {
            return Uri.fromFile(mediaFile);//或者 Uri.isPaise("file://"+file.toString()

        }
    }

    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
            if (options <= 0)
                break;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static void checkPermission(Activity activity, String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        } else {
//            Toast.makeText(activity, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    public static void requestPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 24) {// 7.0
            String[] perms = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA};
            for (String p : perms) {
                int f = ContextCompat.checkSelfPermission(activity, p);
                Log.d("---", String.format("%s - %d", p, f));
                if (f != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(perms, 0XCF);
                    break;
                }
            }
        }
    }

    public static String delHTMLTag(String htmlStr) {
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式
        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); //过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); //过滤style标签
        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); //过滤html标签

        return htmlStr.trim(); //返回文本字符串
    }

    public static String replaceHTML(String html) {
        String regEx_font = "<font[^>]*>";
        String regEx_slashfont = "</font[^>]*>";
        String regEx_img = "<img[^>]*>";
        String regEx_br = "<br[^>]*>";

        Pattern p_font = Pattern.compile(regEx_font, Pattern.CASE_INSENSITIVE);
        Matcher m_font = p_font.matcher(html);
        html = m_font.replaceAll(""); //过滤font标签

        Pattern p_slashfont = Pattern.compile(regEx_slashfont, Pattern.CASE_INSENSITIVE);
        Matcher m_slashfont = p_slashfont.matcher(html);
        html = m_slashfont.replaceAll(""); //过滤</font>标签

        Pattern p_br = Pattern.compile(regEx_br, Pattern.CASE_INSENSITIVE);
        Matcher m_br = p_br.matcher(html);
        html = m_br.replaceAll("\n"); //过滤font标签

        Pattern p_img = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
        Matcher m_img = p_img.matcher(html);
        html = m_img.replaceAll("<img>"); //过滤font标签
        return html.trim();
    }

    /**
     * 自动弹软键盘
     *
     * @param context
     * @param et
     */
    public static void showSoftInput(final Context context, final RichEditorNew et) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        et.setFocusable(true);
                        et.setFocusableInTouchMode(true);
                        //请求获得焦点
                        et.requestFocus();
                        //调用系统输入法
                        InputMethodManager inputManager = (InputMethodManager) et
                                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.showSoftInput(et, 0);
                    }
                });
            }
        }, 200);
    }

    /**
     * 自动关闭软键盘
     *
     * @param activity
     */
    public static void closeKeybord(Activity activity, final RichEditorNew et) {
        et.setFocusable(false);
        et.setFocusableInTouchMode(false);
        et.clearFocusEditor();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    /**
     * 打开关闭相互切换
     *
     * @param activity
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            if (activity.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

}
