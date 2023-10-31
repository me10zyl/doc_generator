package com.me10zyl.doc_generator.generator;

import cn.hutool.core.util.StrUtil;
import com.me10zyl.doc_generator.entity.Column;
import com.me10zyl.doc_generator.entity.Table;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class WeixinDocGenerator {

    @SneakyThrows
    public Table convertTable(){
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //printContents(clipboard);
        String html = null;
        DataFlavor[] availableDataFlavors = clipboard.getAvailableDataFlavors();
        for (DataFlavor availableDataFlavor : availableDataFlavors) {
            if(availableDataFlavor.equals(DataFlavor.allHtmlFlavor)){
                //System.out.println(clipboard.getData(availableDataFlavor));
                html = (String) clipboard.getData(availableDataFlavor);
            }
        }
        //System.out.println(html);
        Table table = new Table();
        Document document = Jsoup.parse(html);
        String span = document.getElementsByTag("span").get(0).text();
        table.setTableName(span);
        Elements tables = document.getElementsByTag("table");
        Elements trs = tables.get(0).getElementsByTag("tr");
        List<Column> columnList = new ArrayList<>();
        int i = 0;
        for (Element tr : trs) {
            if(i++ == 0){
                continue;
            }
            Elements tds = tr.getElementsByTag("td");
            Column c = new Column();
            c.setColumnName(tds.get(0).text());
            c.setTypeString(tds.get(1).text());
            c.setPk("primary".equalsIgnoreCase(tds.get(4).text()) || "PK".equals(tds.get(4).text()));
            c.setNotNull("是".equals(tds.get(3).text()));
            c.setIdx(!c.isPk() ? StrUtil.isNotBlank(tds.get(4).text()) : false);
            c.setDefaultValue(tds.get(2).text());
            c.setRemarks(tds.get(5).text());
            columnList.add(c);
        }
        table.setColumnList(columnList);
        return table;
    }

    @SneakyThrows
    private void printTable(Clipboard clipboard){
        String html = null;
        DataFlavor[] availableDataFlavors = clipboard.getAvailableDataFlavors();
        for (DataFlavor availableDataFlavor : availableDataFlavors) {
            if(availableDataFlavor.equals(DataFlavor.allHtmlFlavor)){
                //System.out.println(clipboard.getData(availableDataFlavor));
                html = (String) clipboard.getData(availableDataFlavor);
            }
        }
        Document document = Jsoup.parse(html);
        System.out.println(document.getElementsByTag("table"));
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
}
