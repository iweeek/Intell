package com.example.intell.tool;


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
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TabAlignment;
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

public class AddingTable {

    private static Color FONT_COLOR = new DeviceRgb(20, 20, 20);
    private static PdfFont pdfFontChinese;
    private static String FONT_FILE_CHINESE = "src/main/resources/font/simsun.ttc,0";
    private static String SOURCE = "src/main/resources/txt/review_form.txt";
    public static final String DEST = "./target/sandbox/acroforms/reporting/addingTable.pdf";

    public static void main(String[] args) throws Exception {

        new AddingTable().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws Exception {
        // Creating a PdfWriter
        PdfWriter writer = new PdfWriter(DEST);

        // Creating a PdfDocument
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(PageSize.A4.rotate());

        // Adding an empty page
        pdfDoc.addNewPage();

        // creating a Document
        Document document = new Document(pdfDoc);

        ArrayList<String> contentList = readTxtFile(SOURCE);

        // 处理中文问题
        pdfFontChinese = PdfFontFactory.createFont(FONT_FILE_CHINESE, PdfEncodings.IDENTITY_H,
                PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);

        PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);

        ArrayList fonts = new ArrayList();
        fonts.add(pdfFontChinese);
        fonts.add(font);
        String title = "浙江省建设用地土壤污染状况调查报告技术审查表";
        document.add(new Paragraph(title).setFontFamily().setFont(pdfFontChinese).setFontSize(16).setFontColor(FONT_COLOR).setTextAlignment(TextAlignment.CENTER));

        String name = "项目名称: " + "桐乡市大麻镇2022-7地块土壤污染状况初步调查报告";
        String company = "编制单位: 宁波市华测检测技术有限公司";
//        String company = "编制单位: 宁波市华测检测技术有限公司";
        String date = "评审时间: 2022.7.11";
        String serial = "第" + " 1 " + "次审查";
        new Paragraph();
//        document.add(generateParagraph("日期：", 12, TextAlignment.RIGHT).add(new Tab()).addTabStops(new TabStop(250, TabAlignment.LEFT)));

        document.add(generateParagraph(name, 12, FONT_COLOR, TextAlignment.LEFT, 0.5f)
                        .add(new Tab()).addTabStops(new TabStop(500, TabAlignment.LEFT))
                        .add(generateParagraph(date, 12, FONT_COLOR, TextAlignment.LEFT, 0.5f))
                        .add(new Tab()).addTabStops(new TabStop(700, TabAlignment.LEFT))
                        .add(generateParagraph(serial, 12, FONT_COLOR, TextAlignment.LEFT, 0.5f)).setFixedLeading(0.5f).setMultipliedLeading(0.5f)
        );
        document.add(generateParagraph(company, 12, FONT_COLOR, TextAlignment.LEFT, 0.5f));

        // Creating a table
//        float[] pointColumnWidths = {3F, 12F, 36F, 12F, 36F};
        float[] pointColumnWidths = {1F, 3F, 7F, 3F, 11F};
//        Table table = new Table(pointColumnWidths).useAllAvailableWidth();
//        Table table = new Table(UnitValue.createPercentArray(5)).useAllAvailableWidth();
        Table table = new Table(UnitValue.createPercentArray(new float[]{1.2f, 4, 10, 7, 11}));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell(new Cell().add(generateParagraphWithBold("序号", 10.5f, TextAlignment.CENTER)));
        table.addHeaderCell(new Cell().add(generateParagraphWithBold("主要项目", 10.5f, TextAlignment.CENTER)));
        table.addHeaderCell(new Cell().add(generateParagraphWithBold("审查内容", 10.5f, TextAlignment.CENTER)));
        table.addHeaderCell(new Cell().add(generateParagraphWithBold("审查结论", 10.5f, TextAlignment.CENTER)));
        table.addHeaderCell(new Cell().add(generateParagraphWithBold("审查说明", 10.5f, TextAlignment.CENTER)));
        table.addCell(new Cell(1, 5).add(generateParagraphWithBold("否决项（以下8项任意一项判定为“涉及”，则评审结论为“不予通过”）", 10.5f, TextAlignment.CENTER)));
        for (int i = 0; i < 8; i++) {
            table.addCell(new Cell().add(generateParagraphWithBold(String.valueOf(i + 1), 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
            table.addCell(new Cell(1, 2).add(generateParagraph(contentList.get(i), 12f, TextAlignment.LEFT)));

//            Cell cell = new Cell();
//            cell.setNextRenderer(new CheckboxCellRenderer(cell, "cb" + i));
//            table.addCell(cell);

            table.addCell(new Cell().add(generateParagraph("□涉及  √不涉及", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
            table.addCell(new Cell().add(generateParagraph("/", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        }
        table.addCell(new Cell(1, 5).add(generateParagraphWithBold("打分项（共计42项，按照总分计算后80分以下为“不予通过”）", 10.5f, TextAlignment.CENTER)));

        int[] list = {1, 1, 6, 11, 8, 4, 11};
        String[] strList = {"报告封面及扉页", "项目概述", "地块基本情况", "关注污染物和重点污染区分析", "土壤/地下水调查布点取样", "调查结果分析和调查结论", "附件"};
        int k = 0;
        List<String> array = contentList.subList(8, 50);
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < list[i]; k++, j++) {
//                System.out.println("i " + i + "j " + j);
                table.addCell(new Cell().add(generateParagraphWithBold(String.valueOf(k + 1), 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                if (j == 0)
                    table.addCell(new Cell(list[i], 1).add(generateParagraph(strList[i], 12f, TextAlignment.LEFT)));
//                if (array.get(k + 8).matches("[a-zA-Z]*"));

//                System.out.println(".............." + k);
                table.addCell(new Cell().add(generateParagraph(array.get(k), 12f)));

                table.addCell(new Cell().add(generateParagraph("□符合 √部分符合 □不符合", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
                table.addCell(new Cell().add(generateParagraph("/", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
            }
        }

        // 总得分
        table.addCell(new Cell(1, 2).add(generateParagraphWithBold("总得分", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().add(generateParagraphWithBold("__分", 10.5f, TextAlignment.LEFT)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().add(generateParagraphWithBold("总分计算方法：总得分 = 100×(42-1×不符合项目数-0.5×部分符合项目数)/42", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell().add(generateParagraph("总分计算方法：总得分 = 100×(42-1×不符合项目数-0.5×部分符合项目数)/42", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));

        // 审查结论
        table.addCell(new Cell(1, 2).add(generateParagraphWithBold("审查结论", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        table.addCell(new Cell(1, 3).add(generateParagraphWithBold("√通过 □不通过\n不通过，需要勾选以下选项，可以双选\n□重大瑕疵和纰漏 □80分以下", 10.5f, TextAlignment.CENTER)).setVerticalAlignment(VerticalAlignment.MIDDLE));
        document.add(table);


        document.add(generateParagraph("*若属于第一阶段调查报告的，可不对土壤/地下水调查布点取样等内容进行审查。", 12, TextAlignment.LEFT));
        document.add(generateParagraph("评审专家签名：", 12, TextAlignment.RIGHT).add(new Tab()).addTabStops(new TabStop(250, TabAlignment.LEFT)));
        document.add(generateParagraph("日期：", 12, TextAlignment.RIGHT).add(new Tab()).addTabStops(new TabStop(250, TabAlignment.LEFT)));

        document.close();
        System.out.println("Paragraph added");

    }

    private static Paragraph generateParagraph(String str, float size) {
        Style english = new Style();
        PdfFont font;
        try {
            font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        english.setFont(font).setFontSize(size).setFontColor(FONT_COLOR)
                .setTextAlignment(TextAlignment.LEFT);
        Style chinese = new Style();
        chinese.setFont(pdfFontChinese).setFontSize(size).setFontColor(FONT_COLOR)
                .setTextAlignment(TextAlignment.LEFT);

        Paragraph p = new Paragraph();
        String process = str;
        StringBuilder result = new StringBuilder();
        String regex = "[\u4E00-\u9FA5|\\、|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】|\\□" +
                "|\\①|\\②|\\③|\\④|\\⑤|\\⑥|\\⑦|\\⑧|\\⑨|\\⑩|\\⑪|\\/|\\ |\\(|\\)|\\√]";
        String copy = str.replaceAll(regex, "*");
//        System.out.println(copy);
        StringTokenizer st = new StringTokenizer(copy, "*");
//        System.out.println(st.countTokens());
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
//        System.out.println(p);
        return p;
    }

    private static Paragraph generateParagraph(String str, float size, TextAlignment textAlignment) {
        return generateParagraph(str, size).setTextAlignment(textAlignment);
    }

    private static Paragraph generateParagraphWithBold(String str, float size, TextAlignment textAlignment) {
        return generateParagraph(str, size).setTextAlignment(textAlignment).setBold();
    }

    private static Paragraph generateParagraph(String str, float size,
                                               Color color, TextAlignment textAlignment, float multipliedLeading) {
        return generateParagraph(str, size).setFontColor(color).setTextAlignment(textAlignment).setMultipliedLeading(multipliedLeading);
    }

    public static ArrayList<String> readTxtFile(String src) {
        ArrayList<String> contentList = new ArrayList<>(); //文件内容字符串
        File file = new File(src);
        try {
            InputStream instream = new FileInputStream(file);
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
            System.out.println("The File doesn't not exist.");
        } catch (IOException e) {
            System.out.println(e.getMessage());
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
//            PdfButtonFormField checkBox = PdfFormField.createCheckBox(drawContext.getDocument(), rect, name, "Yes", TYPE_CHECK);
//            checkBox.setVisibility(VISIBLE);
//            form.addField(checkBox);
        }
    }

    public static boolean isEnglish(String charaString) {
        return charaString.matches("^[a-zA-Z]*");
    }
}
