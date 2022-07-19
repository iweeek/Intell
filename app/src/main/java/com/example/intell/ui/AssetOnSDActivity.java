/*
 * Copyright (C) 2016 Olmo Gallegos Hernández.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.intell.ui;

import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intell.R;

import java.io.File;
import java.io.IOException;

import es.voghdev.pdfviewpager.library.PDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.BasePDFPagerAdapter;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;

public class AssetOnSDActivity extends AppCompatActivity {

    PDFViewPager pdfViewPager;
    String str;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.asset_on_sd);
        setContentView(R.layout.activity_asset_on_sd);

        try {
            str = Environment.getExternalStorageDirectory().getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("str=" + str); // str=/storage/emulated/0
//        pdfViewPager = new PDFViewPager(this, getPdfPathOnSDCard()); // /storage/emulated/0/Download/WeiXin/addingTable.pdf
        pdfViewPager = new PDFViewPager(this, str + "/Download/WeiXin/1.pdf");
//        pdfViewPager = new PDFViewPager(this, str + "/addingTable.pdf");

        BasePDFPagerAdapter adapter = new PDFPagerAdapter(this, str + "/Download/WeiXin/1.pdf");
        pdfViewPager.setAdapter(adapter);

        setContentView(pdfViewPager);

    }

    protected String getPdfPathOnSDCard() {
        File f = new File(str, "/Download/WeiXin/1.pdf");
        System.out.println("f.getAbsolutePath()  " + f.getAbsolutePath());
        return f.getAbsolutePath();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pdfViewPager != null) {
            ((BasePDFPagerAdapter) pdfViewPager.getAdapter()).close();
        }
    }
}