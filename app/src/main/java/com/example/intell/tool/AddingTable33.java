package com.example.intell.tool;


import static com.itextpdf.forms.fields.PdfFormField.TYPE_CHECK;
import static com.itextpdf.forms.fields.PdfFormField.VISIBLE;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.EditText;

import com.example.intell.R;
import com.example.intell.ui.ReviewForm33Activity;
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
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class AddingTable33 {

    private Activity context;

    private static Color FONT_COLOR = new DeviceRgb(20, 20, 20);
    private static PdfFont pdfFontChinese;
    private static PdfFont font;
    private static String FONT_FILE_CHINESE = "res/raw/simsun.ttc,0";

    private boolean rejectedFlag;
    private Integer[] checkList = new Integer[46]; // 检查结果
    private Integer notMatch = 0;
    private Integer partialMatch = 0;
    private float totalScore = 0;
    private EditText[] reviewNotes = new EditText[23];
    private String[] reviewNoteStr = new String[23];
    private boolean seriousIssue = false;
    private boolean otherIssue = false;
    ArrayList<List<String>> imgList = new ArrayList<>(23);
    private String name;
    private String samplingUnitName;
    private int surveyStep;
    private boolean hasAttachment = false;

    public AddingTable33() {
    }

    public AddingTable33(Activity context) {
        this.context = context;
    }

    public AddingTable33(Activity context, Integer[] checkList, boolean rejectedFlag,
                         String[] reviewNoteStr, ArrayList<List<String>> imgList, String name,
                         String samplingUnitName, int surveyStep) {
        this.context = context;
        this.checkList = checkList;
        this.rejectedFlag = rejectedFlag;
        this.reviewNoteStr = reviewNoteStr;
        this.imgList = imgList;
        this.name = name;
        this.samplingUnitName = samplingUnitName;
        this.surveyStep = surveyStep;
    }

    public AddingTable33(Activity context, Integer[] checkList, boolean rejectedFlag, EditText[] reviewNotes) {
        this.context = context;
        this.checkList = checkList;
        this.rejectedFlag = rejectedFlag;
        this.reviewNotes = reviewNotes;
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

        ArrayList<String> contentList = ReadTxtFile(R.raw.review_form_33_pdf);

        // 处理中文问题
        pdfFontChinese = PdfFontFactory.createFont(FONT_FILE_CHINESE, PdfEncodings.IDENTITY_H,
                PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);

        font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);

        ArrayList fonts = new ArrayList();
        fonts.add(pdfFontChinese);
        fonts.add(font);
        String title = "建设用地土壤污染状况调查检验检测机构检查记录表";
        document.add(new Paragraph(title + "\n\n").setBold().setFontFamily().setFont(pdfFontChinese).setFontSize(16).setFontColor(FONT_COLOR).setTextAlignment(TextAlignment.CENTER));

        // Creating a table
        Table table = new Table(UnitValue.createPercentArray(new float[]{5f, 8f, 12f, 44f, 12f, 18f}));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addCell(new Cell(1, 2).add(generateParagraphWithBold("地块名称", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell(1, 2).add(generateParagraphWithBold(name, 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold("检验检测机构名称", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold(samplingUnitName, 10.5f, TextAlignment.CENTER, 2f)));

        table.addCell(new Cell(1, 2).add(generateParagraphWithBold("调查环节", 10.5f, TextAlignment.CENTER, 2f)));
        switch (surveyStep) {
            case 0:
                table.addCell(new Cell(1, 2).add(generateParagraph("√初步采样分析   □详细采样分析   □第三阶段土壤污染状况调查", 10.5f, TextAlignment.CENTER, 2f)));
                break;
            case 1:
                table.addCell(new Cell(1, 2).add(generateParagraph("□初步采样分析   √详细采样分析   □第三阶段土壤污染状况调查", 10.5f, TextAlignment.CENTER, 2f)));
                break;
            case 2:
                table.addCell(new Cell(1, 2).add(generateParagraph("□初步采样分析   □详细采样分析   √第三阶段土壤污染状况调查", 10.5f, TextAlignment.CENTER, 2f)));
                break;
        }

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        table.addCell(new Cell().add(generateParagraphWithBold("检查日期", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold(sdf.format(date), 10.5f, TextAlignment.CENTER, 2f)));

        table.addCell(new Cell().add(generateParagraphWithBold("序号", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold("检查环节", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold("检查项目", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold("检查要点", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold("检查结果", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold("检查意见", 10.5f, TextAlignment.CENTER, 2f)));

//        int[] list = {4, 4, 4};
        int[] list = {3, 2, 2, 4, 5, 3, 3, 1};
        String[] strList = {"检验检测机构资质与能力", "分析方法选择与验证", "分析方法选择与验证", "样品分析测试过程", "实验室外部质控（若开展外部质控才检查相应项目，否则不检查）", "数据溯源性", "篡改、伪造检测数据行为", "其他"};
        String[] itemList = {"机构资质", "机构分包情况", "机构检测能力", "分析方法", "方法验证", "土壤样品分析方法检出限", "地下水样品分析方法检出限", "样品保存期限", "土壤样品制备", "土壤样品制样记录", "实验室内部质控", "密码平行样品结果", "密码平行样品问题改正", "统一监控样品插入", "统一监控样品结果", "统一监控样品问题改正", "数据一致性", "数据准确性、逻辑性、可比性和合理性", "异常值判断和处理", "篡改检测数据行为", "伪造检测数据行为", "涉嫌指使篡改、伪造检测数据行为"};
        // 【第二阶段土壤污染状况调查-详细采样分析/第三阶段土壤污染状况调查】> 【点位数量】
        int k = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < list[i]; j++, k++) {
                table.addCell(new Cell().add(generateParagraphWithBold(String.valueOf(k + 1), 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));

                if (i == 7) {
                    table.addCell(new Cell(1, 2).add(generateParagraph(strList[i], 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                } else {
                    if (j == 0)
                        table.addCell(new Cell(list[i], 1).add(generateParagraph(strList[i], 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));

//                Pattern p = Pattern.compile("【[\\S]+】");
//                Matcher m = p.matcher(contentList.get(k));
//                m.find();
//                m.find();
//                m.group().substring(1, m.group().length()-1);

                    table.addCell(new Cell().add(generateParagraph(itemList[k], 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                }

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
                    table.addCell(new Cell().add(p1).add(p2).setVerticalAlignment(VerticalAlignment.MIDDLE).setPaddings(4f, 8f, 4f, 8f));
                else
                    table.addCell(new Cell().add(p2).setVerticalAlignment(VerticalAlignment.MIDDLE).setPaddings(4f, 8f, 4f, 8f));

                /////////////////////////////////////

                if (checkList[2 * k] != null && checkList[2 * k] == 1) {
                    table.addCell(new Cell().add(generateParagraph("√是  □否", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                } else if (checkList[2 * k + 1] != null && checkList[2 * k + 1] == 1) {
                    table.addCell(new Cell().add(generateParagraph("□是  √否", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                } else {
                    table.addCell(new Cell().add(generateParagraph("□是  √否", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                }

                if (reviewNoteStr[k] != null) {
                    String s = reviewNoteStr[k];
                    int index = 0;
                    int count = 0;
                    while (index < s.length()) {
                        if (s.indexOf("<img>") != -1) {
                            count++;
                            if (s.indexOf("<img>", s.indexOf("<img>") + 1) != -1)
                                s = s.replaceFirst("<img>", " \n原图请见附件图" + (k + 1) + "-" + count + "");
                            else
                                s = s.replaceFirst("<img>", " \n原图请见附件图" + (k + 1) + "-" + count + "\n");
                            hasAttachment = true;
                        } else {
                            break;
                        }
                    }
                    System.out.println("sss = " + s);
                    table.addCell(new Cell().add(generateParagraph(s, 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));

                } else {
                    table.addCell(new Cell().add(generateParagraph("/", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                }
            }
        }

        // 质量评价结论
        checkScore();

        table.addCell(new Cell(1, 2).add(generateParagraphWithBold("质量评价结论", 10.5f, TextAlignment.CENTER, 2.5f)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        if (seriousIssue) {
            table.addCell(new Cell(1, 4).add(generateParagraph("□通过（全部检查项目均判定为是） \n□一般质量问题 \n√严重质量问题（注：任一带*检查项目判定为否，即存在严重质量问题，否则为一般质量问题）", 10.5f, TextAlignment.LEFT)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        } else if (otherIssue) {
            table.addCell(new Cell(1, 4).add(generateParagraph("□通过（全部检查项目均判定为是） \n√一般质量问题 \n□严重质量问题（注：任一带*检查项目判定为否，即存在严重质量问题，否则为一般质量问题）", 10.5f, TextAlignment.LEFT)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        } else {
            table.addCell(new Cell(1, 4).add(generateParagraph("√通过（全部检查项目均判定为是） \n□一般质量问题 \n□严重质量问题（注：任一带*检查项目判定为否，即存在严重质量问题，否则为一般质量问题）", 10.5f, TextAlignment.LEFT)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        }

        // 检查总体意见
        table.addCell(new Cell(1, 2).add(generateParagraphWithBold("检查总体意见", 10.5f, TextAlignment.CENTER, 3f)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell(1, 4).add(generateParagraph("", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));

        // 检查人员（签字）
        table.addCell(new Cell(1, 2).add(generateParagraphWithBold("检查人员（签字）", 10.5f, TextAlignment.CENTER, 3f)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell(1, 4).add(generateParagraph("", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        document.add(table);

        document.add(generateParagraph("注：不涉及的检查要点不判定检查结果。", 10.5f, TextAlignment.LEFT));

        if (hasAttachment) {
            pdfDoc.setDefaultPageSize(PageSize.A4);
            // Adding an empty page
            pdfDoc.addNewPage();
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

            Paragraph paragraph = generateParagraph("\n\n附件：\n", 16f, TextAlignment.LEFT);
            paragraph.setPageNumber(7);
            document.add(paragraph);

            System.out.println("imgList = " + imgList.size());
            for (int i = 0; i < 23; i++) {
                List<String> stringList = imgList.get(i);
                if (stringList != null) {
                    for (int j = 0; j < stringList.size(); j++) {
                        document.add(generateParagraph("图" + (i + 1) + "-" + (j + 1) + "\n", 10.5f, TextAlignment.LEFT));

                        // 获得缩略图，大幅减小文件体积
                        Bitmap bitmap = Utils.getBitmapFormUri(context, stringList.get(j));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();
                        Image image = new Image(ImageDataFactory.create(data));
                        image.scaleToFit(700, 700);
                        document.add(image.setWidth(UnitValue.createPercentValue(50)).setHorizontalAlignment(HorizontalAlignment.CENTER));
//                    document.add(new Image(ImageDataFactory.create(stringList.get(j))).setHeight(UnitValue.createPercentValue(50)));
                    }
                }
            }
        }

        document.close();
        System.out.println("Paragraph added");

        // 回调
        ((ReviewForm33Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ReviewForm33Activity) context).onPdfCreatedListener();
            }
        });
    }

    private void checkScore() {
        for (int i = 0; i < 23; i++) {

            if (i == 0 || i == 19 || i == 20 || i == 21) {
                if (!seriousIssue)
                    seriousIssue = checkList[2 * i + 1] != null && checkList[(i + 1) * 2 - 1] == 1;
            } else {
                if (!otherIssue)
                    otherIssue = checkList[2 * i + 1] != null && checkList[(i + 1) * 2 - 1] == 1;
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
