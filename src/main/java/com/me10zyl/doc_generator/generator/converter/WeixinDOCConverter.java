package com.me10zyl.doc_generator.generator.converter;

import cn.hutool.core.util.StrUtil;
import com.lowagie.text.Document;
import com.lowagie.text.rtf.RtfWriter2;
import com.me10zyl.doc_generator.entity.Column;
import com.me10zyl.doc_generator.entity.Table;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;


import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Component
public class WeixinDOCConverter implements Converter{

    //Plain favor
    private static final DataFlavor PLAIN_FLAVOR = DataFlavor.stringFlavor;
    private static final DataFlavor HTML_FLAVOR = DataFlavor.allHtmlFlavor;
    //Array of data flavors
    private static final DataFlavor flavors[] = {PLAIN_FLAVOR, HTML_FLAVOR};

    @Override
    @SneakyThrows
    public String convert(Table table) {
//        FileOutputStream fos = new FileOutputStream(new File("c:\\data\\rtf\\my.rtf"));
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        Document document = new Document();
//        RtfWriter2 rtfWriter2 = RtfWriter2.getInstance(document, baos);
//        document.open();
//        com.lowagie.text.Table table1 = new com.lowagie.text.Table(6, table.getColumnList().size());
        StringBuilder sb = new StringBuilder();
        for (Column column : table.getColumnList()) {
            sb.append("<tr>");
            sb.append(td(column.getColumnName()));
            sb.append(td(column.getFullTypeString()));
            sb.append(td(column.getDefaultValue() == null ? "" : column.getDefaultValue()));
            sb.append(td(column.isNotNull() ? "是" : "否"));
            sb.append(td(column.isPk() ? "primary" : ( column.isIdx() ? column.getIdxString() : "")));
            sb.append(td(column.isPk() ? "主键" : column.getRemarks()));
            sb.append("</tr>");
        }
//        document.add(table1);
//        document.close();
//        rtfWriter2.close();
        String stringFormat = table.getColumnList().stream().map(c -> {
            String line = StrUtil.format("{}\t{}\t{}\t{}\t{}\t{}", c.getColumnName(), c.getType(), c.getDefaultValue() == null ? "" : c.getDefaultValue(), c.isNotNull() ? "是" : "否", c.isPk() ? "primary" : "idx", c.isPk() ? "主键" : c.getRemarks());
            return line;
        }).collect(Collectors.joining("\n"));
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();


//        printContents(clipboard);


        clipboard.setContents(new MyTransferable(
                new Object[]{
                        stringFormat,
                        new String(Files.readAllBytes(Paths.get("C:\\Users\\me10z\\OneDrive\\java\\doc_generator\\doc_generator\\src\\main\\resources\\wxdoc.html")))
                                .replaceFirst("\\[\\[\\[\\]\\]\\]", "【新建表】" + table.getRemarks() +  " " + table.getTableName())
                                .replaceFirst("\\[\\[\\[\\]\\]\\]", sb.toString())
                }
                , flavors), null);

        return null;
    }

    private String td(String columnName) {
        return "<td style=\"width:100.6px; width:100.6px;box-sizing:border-box;vertical-align:middle;padding-top:0px;padding-left:7.2px;padding-bottom:0px;padding-right:7.2px;border-top:1px solid #CBCDD1;border-right:1px solid #CBCDD1;border-bottom:1px solid #CBCDD1;border-left:1px solid #CBCDD1\" width=\"100.6\" colspan=\"1\" rowspan=\"1\"><p class=\"paragraph text-align-type-left\" style=\"text-align:left;line-height:100%;margin-top:3pt;margin-bottom:3pt;margin-left:0pt\"><span style=\"font-size:11pt;font-weight:normal;font-style:normal;text-decoration:;color:#333333;background:;letter-spacing:0pt;mso-font-width:100%;vertical-align:baseline;text-decoration-color:;text-underline-position:\" data-font-family=\"default\">"
                + columnName + "</span><span lang=\"EN-US\"><o:p></o:p></span></p></td>";
    }

    @SneakyThrows
    private void printContents(Clipboard clipboard) {
        DataFlavor[] availableDataFlavors = clipboard.getAvailableDataFlavors();
        for (DataFlavor availableDataFlavor : availableDataFlavors) {
            System.out.println("flavor:" + availableDataFlavor.getMimeType());
        }
        for (DataFlavor availableDataFlavor : availableDataFlavors) {
            Object data = clipboard.getData(availableDataFlavor);
            System.out.println(availableDataFlavor.getMimeType() + ":" + data);
        }


    }


    static class MyTransferable implements Transferable{
        //Array of data
        private Object dataA[] = null;
        //Array of flavors
        private DataFlavor flavorA[] = null;

        //Transferable class constructor
        public MyTransferable(Object data[], DataFlavor flavors[]){
            //Set the data passed in to the local variable
            dataA = data;
            //Set the flavors passes in to the local variable
            flavorA = flavors;
        }

        public Object getTransferData (DataFlavor flavor) throws UnsupportedFlavorException, IOException{

            String toBeExported = (String) dataA[0];
            if (flavor == DataFlavor.stringFlavor) {
                toBeExported = (String) dataA[0];
            } else if (flavor == DataFlavor.allHtmlFlavor) {
                toBeExported = (String) dataA[1];
            }

            if (String.class.equals(flavor.getRepresentationClass())) {
                return toBeExported;
            }
            throw new UnsupportedFlavorException(flavor);
        }

        public boolean isDataFlavorSupported (DataFlavor df){
            //If the flavor is text/rtf or tet/plain return true
            if(df == DataFlavor.allHtmlFlavor || df == DataFlavor.stringFlavor){
                return true;
            }
            //Return false
            else{
                return false;
            }
        }

        public DataFlavor[] getTransferDataFlavors(){
            //Return array of flavors
            return flavorA;
        }
    }
}
