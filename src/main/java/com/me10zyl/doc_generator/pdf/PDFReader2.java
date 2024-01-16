package com.me10zyl.doc_generator.pdf;

import lombok.Data;
import lombok.SneakyThrows;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.operator.color.*;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PDFReader2 {

    @Data
    static class Question {
        private String allText = "";
        private String lineText = "";
    }

    @SneakyThrows
    public static void main(String[] args) {
        PDDocument document = Loader.loadPDF(new RandomAccessReadBufferedFile("c:\\Users\\me10z\\Desktop\\pdf\\01_2023年度初级、中级经济专业技术资格考试四川省成都市报名点拟取得资格证书人员名单.pdf"));
        int totalPages = document.getNumberOfPages();
        List<String> strings = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int pageNumber = 0; pageNumber < totalPages; pageNumber++) {
            PDPage page = document.getPage(pageNumber);
            PDResources resources = page.getResources();
            COSDictionary cosObject = page.getCOSObject();
            InputStream contents = page.getContents();

            if (contents != null) {
                PDFTextStripper stripper = new PDFTextStripper() {

                    @Override
                    protected void processTextPosition(TextPosition text) {
                        super.processTextPosition(text);
                    }


                };
                stripper.addOperator(new SetStrokingColorSpace(stripper));
                stripper.addOperator(new SetNonStrokingColorSpace(stripper));
                stripper.addOperator(new SetStrokingDeviceCMYKColor(stripper));
                stripper.addOperator(new SetNonStrokingDeviceCMYKColor(stripper));
                stripper.addOperator(new SetNonStrokingDeviceRGBColor(stripper));
                stripper.addOperator(new SetStrokingDeviceRGBColor(stripper));
                stripper.addOperator(new SetNonStrokingDeviceGrayColor(stripper));
                stripper.addOperator(new SetStrokingDeviceGrayColor(stripper));
                stripper.addOperator(new SetStrokingColor(stripper));
                stripper.addOperator(new SetStrokingColorN(stripper));
                stripper.addOperator(new SetNonStrokingColor(stripper));
                stripper.addOperator(new SetNonStrokingColorN(stripper));
                //stripper.processPage(page);
                // this.processChildStream(page.getContents(), page);
                stripper.setStartPage(pageNumber + 1);
                stripper.setEndPage(pageNumber + 1);
                String pageText = stripper.getText(document);

//                questions.add(q[0]);

                // 处理页面文本内容
               // System.out.println(pageText);
                sb.append(pageText);
            }
        }
        document.close();

        StringTokenizer stringTokenizer = new StringTokenizer(sb.toString(), "\r\n");
        while (stringTokenizer.hasMoreTokens()){
            String s = stringTokenizer.nextToken();
            if(s.contains("知识")){
                strings.add(s);
            }
        }

        System.out.println(strings.stream().collect(Collectors.joining("\n")));

//
//        File file = new File("C:\\pdf\\anki-jjs.txt");
//        FileOutputStream fos = new FileOutputStream(file);
//        fos.write(questions.stream().map(e->{
//            return e.getLineText();
//        }).collect(Collectors.joining("\n")).getBytes(StandardCharsets.UTF_8));
//        fos.close();
//        System.out.println(questions);
    }
}
