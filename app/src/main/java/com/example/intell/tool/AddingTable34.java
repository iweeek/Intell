package com.example.intell.tool;


import com.example.intell.R;
import com.example.intell.ui.ReviewForm34Activity;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static com.itextpdf.forms.fields.PdfFormField.TYPE_CHECK;
import static com.itextpdf.forms.fields.PdfFormField.VISIBLE;

import android.app.Activity;
import android.util.Log;
import android.widget.EditText;

public class AddingTable34 {

    private Activity context;

    private static Color FONT_COLOR = new DeviceRgb(20, 20, 20);
    private static PdfFont pdfFontChinese;
    private static PdfFont font;
    private static String FONT_FILE_CHINESE = "res/raw/simsun.ttc,0";

    private boolean rejectedFlag;
    private Integer[] checkList = new Integer[55]; // 检查结果
    private Integer notMatch = 0;
    private Integer partialMatch = 0;
    private float totalScore = 0;
    private EditText[] reviewNotes = new EditText[20];
    private int seriousIssueCount = 0;
    private int otherIssueCount = 0;
    ArrayList<List<String>> imgList = new ArrayList<>(20);



    public AddingTable34(Activity context) {
        this.context = context;
    }

    public AddingTable34(Activity context, Integer[] checkList, boolean rejectedFlag, EditText[] reviewNotes, ArrayList<List<String>> imgList) {
        this.context = context;
        this.checkList = checkList;
        this.rejectedFlag = rejectedFlag;
        this.reviewNotes = reviewNotes;
        this.imgList = imgList;
    }

    public void manipulatePdf(String dest) throws Exception {
        // Creating a PdfWriter
        PdfWriter writer = new PdfWriter(dest);

        // Creating a PdfDocument
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(PageSize.A4.rotate());

        // Adding an empty page
        pdfDoc.addNewPage();

        // creating a Document
        Document document = new Document(pdfDoc);

        ArrayList<String> contentList = ReadTxtFile(R.raw.review_form_34_pdf);

        // 处理中文问题
        pdfFontChinese = PdfFontFactory.createFont(FONT_FILE_CHINESE, PdfEncodings.IDENTITY_H,
                PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);

        font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);

        ArrayList fonts = new ArrayList();
        fonts.add(pdfFontChinese);
        fonts.add(font);
        String title = "建设用地土壤污染状况调查报告审核记录表";
        document.add(new Paragraph(title + "\n\n").setBold().setFontFamily().setFont(pdfFontChinese).setFontSize(16).setFontColor(FONT_COLOR).setTextAlignment(TextAlignment.CENTER));

        // Creating a table
        Table table = new Table(UnitValue.createPercentArray(new float[]{5f, 10f, 12f, 30f, 12f, 30f, 18f, 18f}));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addCell(new Cell(1, 2).add(generateParagraphWithBold("地块名称", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell(1, 2).add(generateParagraphWithBold("", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold("所在省市", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold("", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold("调查时间", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold("", 10.5f, TextAlignment.CENTER, 2f)));

//        table.addCell(new Cell().add(generateParagraphWithBold("检验检测机构名称", 10.5f, TextAlignment.CENTER, 2f)));
//        table.addCell(new Cell().add(generateParagraphWithBold("", 10.5f, TextAlignment.CENTER, 2f)));

        table.addCell(new Cell(1, 2).add(generateParagraphWithBold("调查环节", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell(1, 2).add(generateParagraph("□第一阶段土壤污染状况调查 \n□初步采样分析   □详细采样分析 \n□第三阶段土壤污染状况调查", 10.5f, TextAlignment.CENTER)));
        table.addCell(new Cell().add(generateParagraphWithBold("业主单位名称", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().add(generateParagraphWithBold("", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().add(generateParagraphWithBold("报告编制\n单位名称", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().add(generateParagraphWithBold("", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));

        table.addCell(new Cell(1, 2).add(generateParagraphWithBold("采样单位名称", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell(1, 2).add(generateParagraph("", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().add(generateParagraphWithBold("检验检测\n机构名称", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().add(generateParagraphWithBold("", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().add(generateParagraphWithBold("检查日期", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().add(generateParagraphWithBold("", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));

        table.addCell(new Cell().add(generateParagraphWithBold("序号", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold("检查环节", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold("检查项目", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell(1, 3).add(generateParagraphWithBold("检查要点", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold("检查结果", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold("检查意见", 10.5f, TextAlignment.CENTER, 2f)));

//        int[] list = {4, 4, 4};
        int[] list = {3, 4, 13};
        String[] strList = {"完整性检查", "第一阶段土壤污染状况调查", "第二阶段土壤污染状况调查"};
        String[] itemList = {"报告完整性", "附件完整性", "图件完整性", "资料收集", "现场踏勘", "人员访谈", "信息分析及污染识别", "初步采样分析-点位布设", "初步采样分析-采样深度", "初步采样分析-检测项目", "详细采样分析-点位布设", "详细采样分析-采样深度", "详细采样分析-检测项目", "详细采样分析-水文地质", "现场采样", "样品保存、流转、运输", "检验检测机构检测", "质量保证与质量控制", "数据评估和结果分析", "结论和建议"};
        // 【第二阶段土壤污染状况调查-详细采样分析/第三阶段土壤污染状况调查】> 【点位数量】
        int k = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < list[i]; j++, k++) {
                table.addCell(new Cell().add(generateParagraphWithBold(String.valueOf(k + 1), 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                if (j == 0)
                    table.addCell(new Cell(list[i], 1).add(generateParagraph(strList[i], 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));

//                Pattern p = Pattern.compile("【[\\S]+】");
//                Matcher m = p.matcher(contentList.get(k));
//                m.find();
//                m.find();
//                m.group().substring(1, m.group().length()-1);

                table.addCell(new Cell().add(generateParagraph(itemList[k], 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));

                /////////////////////////////////////

                String[] split = contentList.get(k).split("@");
                Paragraph p1 = null;
                if (!split[0].isEmpty())
                    p1 = generateParagraphWithBold(split[0], 10.5f, TextAlignment.LEFT);

                // 处理要点说明这一段
//                Paragraph p2 = new Paragraph();
                String rest = contentList.get(k).substring(split[0].length() + 1);
//                System.out.println(rest);
//                Text t1 = new Text(rest.substring(0, 4)).setBold();
//                Text t2 = new Text(rest.substring(4));
//                p2.add(t1);
//                p2.add(t2);
//                table.addCell(new Cell().add(p1).add(p2));
                StringBuilder builder = new StringBuilder();
                for (int p = 1; p < split.length; p++) {
                    builder.append(split[p]);
                    if (p != split.length - 1)
                        builder.append("\n");
                }
                Paragraph p2 = generateParagraph(builder.toString(), 10.5f, TextAlignment.LEFT);
//                Style english = new Style();
//                Style chinese = new Style();
//                english.setFont(font).setFontSize(10.5f).setFontColor(FONT_COLOR)
//                        .setTextAlignment(TextAlignment.LEFT);
//                chinese.setFont(pdfFontChinese).setFontSize(10.5f).setFontColor(FONT_COLOR)
//                        .setTextAlignment(TextAlignment.LEFT);
//
//                Paragraph p2 = new Paragraph();
//                // 这些代码是为了 加粗部分和不加粗部分在同一段落
//                p2.add(new Text(rest.substring(0, 5)).addStyle(chinese).setBold());
////                p2.add(generateParagraph(rest.substring(5), 10.5f));
//                rest = rest.substring(5);
//                String regex = "[\u4E00-\u9FA5|\\、|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】|\\□" +
//                        "|\\①|\\②|\\③|\\④|\\⑤|\\⑥|\\⑦|\\⑧|\\⑨|\\⑩|\\⑪|\\/|\\ |\\(|\\)|\\√|\\＞|\\>|\\<|\\≤|\\≥]";
//                String copy = contentList.get(k).substring(split[0].length()+1).replaceAll(regex, "*");
//                StringTokenizer st = new StringTokenizer(copy, "*");
//                while (st.hasMoreElements()) {
//                    String element = (String) st.nextElement();
//                    String[] splits = rest.split(element, 2);
//                    p2.add(new Text(splits[0]).addStyle(chinese));
//                    p2.add(new Text(element).addStyle(english));
//                    rest = splits[1];
//                }
//                p2.add(new Text(rest).addStyle(chinese));
//                p2.setMultipliedLeading(1f);
                if (p1 != null)
                    table.addCell(new Cell(1, 3).add(p1).add(p2).setVerticalAlignment(VerticalAlignment.MIDDLE).setPaddings(4f, 8f, 4f, 8f));
                else
                    table.addCell(new Cell(1, 3).add(p2).setVerticalAlignment(VerticalAlignment.MIDDLE).setPaddings(4f, 8f, 4f, 8f));

                /////////////////////////////////////

                if (k < 4) {
                    if (checkList[2 * k] != null && checkList[2 * k] == 1) {
                        table.addCell(new Cell().add(generateParagraph("√是\n□否", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                    } else if (checkList[2 * k + 1] != null && checkList[2 * k + 1] == 1) {
                        table.addCell(new Cell().add(generateParagraph("□是\n√否", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                    } else {
                        table.addCell(new Cell().add(generateParagraph("□是\n√否", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                    }
                } else if (k == 13) {
                    if (checkList[35] != null && checkList[35] == 1) {
                        table.addCell(new Cell().add(generateParagraph("√是\n□否", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                    } else if (checkList[36] != null && checkList[36] == 1) {
                        table.addCell(new Cell().add(generateParagraph("□是\n√否", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                    } else {
                        table.addCell(new Cell().add(generateParagraph("□是\n√否", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                    }
                } else {
                    if (k >= 4 && k <= 12) { // 第5至13题
                        if (checkList[(k - 4) * 3 + 0 + 8] != null && checkList[(k - 4) * 3 + 0 + 8] == 1) {
                            table.addCell(new Cell().add(generateParagraph("√是\n□否\n□材料不支撑判断", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                        } else if (checkList[(k - 4) * 3 + 1 + 8] != null && checkList[(k - 4) * 3 + 1 + 8] == 1) {
                            table.addCell(new Cell().add(generateParagraph("□是\n√否\n□材料不支撑判断", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                        } else if (checkList[(k - 4) * 3 + 2 + 8] != null && checkList[(k - 4) * 3 + 2 + 8] == 1){
                            table.addCell(new Cell().add(generateParagraph("□是\n□否\n√材料不支撑判断", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                        } else {
                            table.addCell(new Cell().add(generateParagraph("□是\n□否\n√材料不支撑判断", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                        }
                    } else {
                        if (checkList[(k - 14) * 3 + 0 + 37] != null && checkList[(k - 14) * 3 + 0 + 37] == 1) {
                            table.addCell(new Cell().add(generateParagraph("√是\n□否\n□材料不支撑判断", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                        } else if (checkList[(k - 14) * 3 + 1 + 37] != null && checkList[(k - 14) * 3 + 1 + 37] == 1) {
                            table.addCell(new Cell().add(generateParagraph("□是\n√否\n□材料不支撑判断", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                        } else if (checkList[(k - 14) * 3 + 2 + 37] != null && checkList[(k - 14) * 3 + 2 + 37] == 1){
                            table.addCell(new Cell().add(generateParagraph("□是\n□否\n√材料不支撑判断", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                        } else {
                            table.addCell(new Cell().add(generateParagraph("□是\n□否\n√材料不支撑判断", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                        }
                    }
                }

                if (reviewNotes[k] != null) {
//                    table.addCell(new Cell().add(generateParagraph(String.valueOf(reviewNotes[k].getText()), 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                    Cell cell = new Cell();
                    List<String> imgs = imgList.get(k);
                    for (int p = 0; p < imgs.size(); p++) {
                        cell.add(new Image(ImageDataFactory.create(imgs.get(p))).setWidth(UnitValue.createPercentValue(100)));
                    }
                    table.addCell(cell);
//                    table.addCell(new Cell().add(new Image(ImageDataFactory.create(IMG)).setWidth(UnitValue.createPercentValue(100))));
                } else {
                    table.addCell(new Cell().add(generateParagraph("/", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                }
            }
        }

        // 质量评价结论
        checkScore();

        System.out.println("seriousIssueCount = " + seriousIssueCount);
        System.out.println("otherIssueCount = " + otherIssueCount);
        table.addCell(new Cell(1, 3).add(generateParagraphWithBold("质量评价结论", 10.5f, TextAlignment.CENTER, 2.5f)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        if (seriousIssueCount >= 3 || otherIssueCount + seriousIssueCount >= 6) {
            table.addCell(new Cell(1, 5).add(generateParagraph("□通过，暂未发现问题 \n□通过，发现一般质量问题，需修改完善 \n√不通过，发现严重质量问题，需补充调查）", 10.5f, TextAlignment.LEFT)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        } else if (otherIssueCount == 0 && seriousIssueCount == 0) {
            table.addCell(new Cell(1, 5).add(generateParagraph("√通过，暂未发现问题 \n□通过，发现一般质量问题，需修改完善 \n□不通过，发现严重质量问题，需补充调查）", 10.5f, TextAlignment.LEFT)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        } else {
            table.addCell(new Cell(1, 5).add(generateParagraph("□通过，暂未发现问题 \n√通过，发现一般质量问题，需修改完善 \n□不通过，发现严重质量问题，需补充调查）", 10.5f, TextAlignment.LEFT)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        }

        // 检查总体意见
        table.addCell(new Cell(1, 3).add(generateParagraphWithBold("检查总体意见", 10.5f, TextAlignment.CENTER, 3f)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell(1, 5).add(generateParagraph("", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));

        // 检查人员（签字）
        table.addCell(new Cell(1, 3).add(generateParagraphWithBold("检查人员（签字）", 10.5f, TextAlignment.CENTER, 3f)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell(1, 5).add(generateParagraph("", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        document.add(table);

        document.add(generateParagraph("注：（1）带*号项为重点检查项，3个（含）以上带*号的检查项目判定为否，或累计6项（含）以上检查项目判定为否或材料不支撑判断，则认为调查报告存在严重质量问题；所有检查项目判定为是，则认为暂未发现问题；其他情况为一般质量问题。\n（2）检查要点基于国家发布的相关技术导则设定。\n（3）第三阶段土壤污染状况调查检查要点同第二阶段土壤污染状况调查-详细采样分析。\n（4）对不同调查环节，不涉及的检查要点不判定检查结果；检查要点中不涉及的内容不作为检查结果的判定依据。", 10.5f, TextAlignment.LEFT));

        document.close();
        System.out.println("Paragraph added");

        // 回调
        ((ReviewForm34Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ReviewForm34Activity) context).onPdfCreatedListener();
            }
        });
    }

    private void checkScore() {
        for (int i = 0; i < 20; i++) {
            if (i < 4) {
                if (checkList[2 * i + 1] != null && checkList[(i + 1) * 2 - 1] == 1) {
                    if (i == 0)
                        seriousIssueCount++;
                    else
                        otherIssueCount++;
                }

            } else if (i == 13) {
                if (checkList[36] != null && checkList[36] == 1)
                    otherIssueCount++;
            } else {
                if (i >= 4 && i <= 12) { // 第5至13题
                    if ((checkList[(i - 4) * 3 + 1 + 8] != null && checkList[(i - 4) * 3 + 1 + 8] == 1) ||
                            (checkList[(i - 4) * 3 + 2 + 8] != null && checkList[(i - 4) * 3 + 2 + 8] == 1)) {
                        if (i == 6 || i == 7 || i == 8 || i == 9 || i == 10 || i == 11 || i == 12)
                            seriousIssueCount++;
                        else
                            otherIssueCount++;
                    }

                } else { // 第15至20题
                    if ((checkList[(i - 14) * 3 + 1 + 37] != null && checkList[(i - 14) * 3 + 1 + 37] == 1) ||
                            (checkList[(i - 14) * 3 + 2 + 37] != null && checkList[(i - 14) * 3 + 2 + 37] == 1)) {
                        if (i == 14 || i == 16 || i == 18)
                            seriousIssueCount++;
                        else
                            otherIssueCount++;
                    }
                }

            }

        }
    }

    private static Paragraph generateParagraph(String str, float size) {
        Style english = new Style();
        Style chinese = new Style();
        english.setFont(font).setFontSize(size).setFontColor(FONT_COLOR)
                .setTextAlignment(TextAlignment.LEFT);
        chinese.setFont(pdfFontChinese).setFontSize(size).setFontColor(FONT_COLOR)
                .setTextAlignment(TextAlignment.LEFT);

        Paragraph p = new Paragraph();
        String process = str;
        String regex = "[\u4E00-\u9FA5|\\、|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】|\\□" +
                "|\\①|\\②|\\③|\\④|\\⑤|\\⑥|\\⑦|\\⑧|\\⑨|\\⑩|\\⑪|\\/|\\ |\\(|\\)|\\√|\\＞|\\>|\\<|\\≤|\\≥]";
        String copy = str.replaceAll(regex, "*");
        StringTokenizer st = new StringTokenizer(copy, "*");
        while (st.hasMoreElements()) {
            String k = (String) st.nextElement();
//            System.out.println("当前匹对：" + k);
            String[] split = process.split(k, 2);
            p.add(new Text(split[0]).addStyle(chinese));
            p.add(new Text(k).addStyle(english));
            process = split[1];
        }
        p.add(new Text(process).addStyle(chinese));
        p.setMultipliedLeading(1f);
        return p;
    }

    private static Paragraph generateParagraph(String str, float size, TextAlignment textAlignment) {
        return generateParagraph(str, size).setTextAlignment(textAlignment);
    }

    private static Paragraph generateParagraph(String str, float size, TextAlignment textAlignment, float multipliedLeading) {
        return generateParagraph(str, size).setMultipliedLeading(multipliedLeading).setTextAlignment(textAlignment);
    }

    private static Paragraph generateParagraphWithBold(String str, float size, TextAlignment textAlignment) {
        return generateParagraph(str, size).setTextAlignment(textAlignment).setBold();
    }

    private static Paragraph generateParagraphWithBold(String str, float size, TextAlignment textAlignment, float multipliedLeading) {
        return generateParagraph(str, size).setMultipliedLeading(multipliedLeading).setTextAlignment(textAlignment).setBold();
    }

    private static Paragraph generateParagraph(String str, float size,
                                               Color color, TextAlignment textAlignment, float multipliedLeading) {
        return generateParagraph(str, size).setFontColor(color).setTextAlignment(textAlignment).setMultipliedLeading(multipliedLeading);
    }

    public ArrayList<String> ReadTxtFile(int file) {
        ArrayList<String> contentList = new ArrayList<>(); //文件内容字符串
        //如果path是传递过来的参数，可以做一个非目录的判断
        try {
            InputStream instream = context.getResources().openRawResource(file);
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

    private class CheckboxCellRenderer extends CellRenderer {

        // The name of the check box field
        protected String name;

        public CheckboxCellRenderer(Cell modelElement, String name) {
            super(modelElement);
            this.name = name;
        }

        // If a renderer overflows on the next area, iText uses #getNextRenderer() method to create a new renderer for the overflow part.
        // If #getNextRenderer() isn't overridden, the default method will be used and thus the default rather than the custom
        // renderer will be created
        @Override
        public IRenderer getNextRenderer() {
            return new CheckboxCellRenderer((Cell) modelElement, name);
        }

        @Override
        public void draw(DrawContext drawContext) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(drawContext.getDocument(), true);

            // Define the coordinates of the middle
            float x = (getOccupiedAreaBBox().getLeft() + getOccupiedAreaBBox().getRight()) / 2;
            float y = (getOccupiedAreaBBox().getTop() + getOccupiedAreaBBox().getBottom()) / 2;

            // Define the position of a check box that measures 20 by 20
//            Rectangle rect = new Rectangle(x - 10, y - 10, 20, 20);
            Rectangle rect = new Rectangle(x - 5, y - 5, 10, 10);

            // The 4th parameter is the initial value of checkbox: 'Yes' - checked, 'Off' - unchecked
            // By default, checkbox value type is cross.
            PdfButtonFormField checkBox = PdfFormField.createCheckBox(drawContext.getDocument(), rect, name, "Yes", TYPE_CHECK);
            checkBox.setVisibility(VISIBLE);
            form.addField(checkBox);
        }
    }

    public static boolean isEnglish(String charaString) {
        return charaString.matches("^[a-zA-Z]*");
    }
}
