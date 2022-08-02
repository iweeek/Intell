package com.example.intell.tool;


import static com.itextpdf.forms.fields.PdfFormField.TYPE_CHECK;
import static com.itextpdf.forms.fields.PdfFormField.VISIBLE;

import android.app.Activity;
import android.util.Log;
import android.widget.EditText;

import com.example.intell.R;
import com.example.intell.ui.ReviewForm31Activity;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
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
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class AddingTable31 {

    private Activity context;

    private static Color FONT_COLOR = new DeviceRgb(20, 20, 20);
    private static PdfFont pdfFontChinese;
    private static PdfFont font;
    private static String FONT_FILE_CHINESE = "res/raw/simsun.ttc,0";


    private boolean rejectedFlag;
    private Integer[] checkList = new Integer[24]; // 检查结果
    private Integer notMatch = 0;
    private Integer partialMatch = 0;
    private float totalScore = 0;
    private EditText[] reviewNotes = new EditText[12];

    public AddingTable31() {}

    public AddingTable31(Activity context) {
        this.context = context;
    }

    public AddingTable31(Activity context, Integer[] checkList, boolean rejectedFlag, EditText[] reviewNotes) {
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

        ArrayList<String> contentList = ReadTxtFile(R.raw.review_form_31_pdf);

        // 处理中文问题
        pdfFontChinese = PdfFontFactory.createFont(FONT_FILE_CHINESE, PdfEncodings.IDENTITY_H,
                PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);

        font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);

        ArrayList fonts = new ArrayList();
        fonts.add(pdfFontChinese);
        fonts.add(font);
        String title = "建设用地土壤污染状况调查采样方案检查记录表";
        document.add(new Paragraph(title + "\n\n").setBold().setFontFamily().setFont(pdfFontChinese).setFontSize(16).setFontColor(FONT_COLOR).setTextAlignment(TextAlignment.CENTER));

        // Creating a table
        Table table = new Table(UnitValue.createPercentArray(new float[]{5f, 9.7f, 12f, 41f, 14f, 16f}));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addCell(new Cell(1, 2).add(generateParagraphWithBold("地块名称", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell(1, 2).add(generateParagraphWithBold("", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold("编制单位名称", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold("", 10.5f, TextAlignment.CENTER, 2f)));

        table.addCell(new Cell(1, 2).add(generateParagraphWithBold("调查环节", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell(1, 2).add(generateParagraph("□初步采样分析   □详细采样分析   □第三阶段土壤污染状况调查", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold("检查日期", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold("", 10.5f, TextAlignment.CENTER, 2f)));

        table.addCell(new Cell().add(generateParagraphWithBold("序号", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold("检查环节", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold("检查项目", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold("检查要点", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold("检查结果", 10.5f, TextAlignment.CENTER, 2f)));
        table.addCell(new Cell().add(generateParagraphWithBold("检查意见", 10.5f, TextAlignment.CENTER, 2f)));

        int[] list = {4, 4, 4};
        String[] strList = {"第一阶段土壤污染状况调查", "第二阶段土壤污染状况调查-初步采样分析", "第二阶段土壤污染状况调查-详细采样分析/第三阶段土壤污染状况调查"};
        String[] itemList = {"资料收集", "现场踏勘", "人员访谈", "污染识别结论", "点位数量", "布点位置", "采样深度", "检测项目", "点位数量", "布点位置", "采样深度", "检测项目"};
        // 【第二阶段土壤污染状况调查-详细采样分析/第三阶段土壤污染状况调查】> 【点位数量】
        int k = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < list[i]; j++, k++) {
                table.addCell(new Cell().add(generateParagraphWithBold(String.valueOf(k + 1), 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                if (j == 0)
                    table.addCell(new Cell(4, 1).add(generateParagraphWithBold(strList[i], 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));

//                Pattern p = Pattern.compile("【[\\S]+】");
//                Matcher m = p.matcher(contentList.get(k));
//                m.find();
//                m.find();
//                m.group().substring(1, m.group().length()-1);

                table.addCell(new Cell().add(generateParagraphWithBold(itemList[k], 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));

                /////////////////////////////////////

                String[] split = contentList.get(k).split("。");
                Paragraph p1 = generateParagraphWithBold(split[0] + "。", 10.5f, TextAlignment.LEFT);
                // 处理要点说明这一段
//                Paragraph p2 = new Paragraph();
                String rest = contentList.get(k).substring(split[0].length()+1);
//                System.out.println(rest);
//                Text t1 = new Text(rest.substring(0, 4)).setBold();
//                Text t2 = new Text(rest.substring(4));
//                p2.add(t1);
//                p2.add(t2);
//                table.addCell(new Cell().add(p1).add(p2));

                Style english = new Style();
                Style chinese = new Style();
                english.setFont(font).setFontSize(10.5f).setFontColor(FONT_COLOR)
                        .setTextAlignment(TextAlignment.LEFT);
                chinese.setFont(pdfFontChinese).setFontSize(10.5f).setFontColor(FONT_COLOR)
                        .setTextAlignment(TextAlignment.LEFT);

                Paragraph p2 = new Paragraph();
                // 这些代码是为了 加粗部分和不加粗部分在同一段落
                p2.add(new Text(rest.substring(0, 5)).addStyle(chinese).setBold());
//                p2.add(generateParagraph(rest.substring(5), 10.5f));
                rest = rest.substring(5);
                String regex = "[\u4E00-\u9FA5|\\、|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】|\\□" +
                        "|\\①|\\②|\\③|\\④|\\⑤|\\⑥|\\⑦|\\⑧|\\⑨|\\⑩|\\⑪|\\/|\\ |\\(|\\)|\\√|\\＞|\\>|\\<|\\≤|\\≥]";
                String copy = contentList.get(k).substring(split[0].length()+1).replaceAll(regex, "*");
                StringTokenizer st = new StringTokenizer(copy, "*");
                while (st.hasMoreElements()) {
                    String element = (String) st.nextElement();
                    String[] splits = rest.split(element, 2);
                    p2.add(new Text(splits[0]).addStyle(chinese));
                    p2.add(new Text(element).addStyle(english));
                    rest = splits[1];
                }
                p2.add(new Text(rest).addStyle(chinese));
                p2.setMultipliedLeading(1f);
                table.addCell(new Cell().add(p1).add(p2).setPaddings(4f, 8f, 4f, 8f));

                /////////////////////////////////////

                if (checkList[2 * k] != null && checkList[2 * k] == 1) {
                    table.addCell(new Cell().add(generateParagraph("√是  □否", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                } else if (checkList[2 * k + 1] != null && checkList[2 * k + 1] == 1) {
                    table.addCell(new Cell().add(generateParagraph("□是  √否", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                    notMatch++;
                } else {
                    table.addCell(new Cell().add(generateParagraph("□是  √否", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                    notMatch++;
                }

                if (reviewNotes[k] != null) {
                    table.addCell(new Cell().add(generateParagraph(String.valueOf(reviewNotes[k].getText()), 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                } else {
                    table.addCell(new Cell().add(generateParagraph("/", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                }
            }
        }

        // 质量评价结论
        table.addCell(new Cell(1, 2).add(generateParagraphWithBold("质量评价结论", 10.5f, TextAlignment.CENTER, 2.5f)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        StringBuilder builder = new StringBuilder();
        if (notMatch == 0) {
            builder.append("√通过（全部检查项目均判定为是） □不通过，需补充完善或重新布点（任意一项判定为否，即存在严重质量问题）");
        } else {
            builder.append("□通过（全部检查项目均判定为是） √不通过，需补充完善或重新布点（任意一项判定为否，即存在严重质量问题）");
        }
        table.addCell(new Cell(1, 4).add(generateParagraph(builder.toString(), 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));

        // 检查总体意见
        table.addCell(new Cell(1, 2).add(generateParagraphWithBold("检查总体意见", 10.5f, TextAlignment.CENTER, 3f)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell(1, 4).add(generateParagraph("", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));

        // 检查人员（签字）
        table.addCell(new Cell(1, 2).add(generateParagraphWithBold("检查人员（签字）", 10.5f, TextAlignment.CENTER, 3f)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell(1, 4).add(generateParagraph("", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        document.add(table);

        document.add(generateParagraph("注：（1）检查要点基于《建设用地土壤污染状况调查技术导则》（HJ 25.1—2019）、《建设用地土壤污染风险管控和修复监测技术导则》（HJ 25.2—2019）、《建设用地土壤环境调查评估技术指南》等相关技术导则设定。 \n （2）对不同调查环节，不涉及的检查要点不判定检查结果；检查要点中不涉及的内容不作为检查结果的判定依据。", 10.5f, TextAlignment.LEFT));

        document.close();
        System.out.println("Paragraph added");

        // 回调，隐藏进度圈
        ((ReviewForm31Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ReviewForm31Activity) context).onPdfCreatedListener();
            }
        });
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
