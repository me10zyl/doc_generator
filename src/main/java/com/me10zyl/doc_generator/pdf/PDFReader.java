package com.me10zyl.doc_generator.pdf;

import lombok.Data;
import lombok.SneakyThrows;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.color.*;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.pdmodel.interactive.action.PDPageAdditionalActions;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PDFReader {

    @Data
    static class Question {
        private String allText = "";
        private String lineText = "";
    }

    @SneakyThrows
    public static void main(String[] args) {
        PDDocument document = Loader.loadPDF(new RandomAccessReadBufferedFile("c:\\pdf\\jjs.pdf"));
        int totalPages = document.getNumberOfPages();
        List<Question> questions = new ArrayList<>();
        for (int pageNumber = 0; pageNumber < totalPages; pageNumber++) {
            PDPage page = document.getPage(pageNumber);
            PDResources resources = page.getResources();
            COSDictionary cosObject = page.getCOSObject();
            InputStream contents = page.getContents();

            if (contents != null) {

                float[] endY = {0.0f};
                final Question[] q = {new Question()};
                PDFTextStripper stripper = new PDFTextStripper() {

                    @Override
                    protected void processTextPosition(TextPosition text) {
                        boolean newLine = (endY[0] != 0 && endY[0] != text.getEndY());
                        String newLineStr = newLine ? "<br/>" : "";
                        endY[0] = text.getEndY();
                        PDGraphicsState graphicsState = getGraphicsState();
                        String lastCharacter = q[0].allText.length() > 10 ? q[0].allText.substring(q[0].allText.length() - 3, q[0].allText.length()) : "";
                        try {
                            int rgb = graphicsState.getNonStrokingColor().toRGB();
                            String regex = "\\d+\\.";
                            Pattern compile = Pattern.compile(regex);
                            Matcher matcher = compile.matcher(lastCharacter);
                            boolean matches = matcher.find();
                            if (matches) {
                                q[0].allText = q[0].allText.replaceAll("\\d\\.$", "");
                                q[0].lineText = q[0].lineText.replaceAll("\\d\\.$", "");
                                q[0] = new Question();
                                questions.add(q[0]);
                            }
                            q[0].allText += newLineStr + text.getUnicode();
                            if (rgb == 16711680){ //28864
                                q[0].lineText += newLineStr + "{{c1::"+ text.getUnicode() +"}}";
                                // System.out.println("R = " + rgb + ":" +text);
                            } else {
                                q[0].lineText += newLineStr + text.getUnicode();
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
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
                System.out.println(pageText);
            }
        }
        document.close();


        File file = new File("C:\\pdf\\anki-jjs.txt");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(questions.stream().map(e->{
            return e.getLineText();
        }).collect(Collectors.joining("\n")).getBytes(StandardCharsets.UTF_8));
        fos.close();
//        System.out.println(questions);
    }
}
