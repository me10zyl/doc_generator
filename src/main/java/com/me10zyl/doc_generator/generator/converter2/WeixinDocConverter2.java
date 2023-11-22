package com.me10zyl.doc_generator.generator.converter2;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.me10zyl.doc_generator.entity.api.Api;
import com.me10zyl.doc_generator.entity.api.Parameter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class WeixinDocConverter2 implements Convert2{

    private static final DataFlavor PLAIN_FLAVOR = DataFlavor.stringFlavor;
    private static final DataFlavor HTML_FLAVOR = DataFlavor.allHtmlFlavor;
    private static final DataFlavor flavors[] = {PLAIN_FLAVOR, HTML_FLAVOR};

    private static final Map<String, String> hiddenMap = new HashMap<String, String>(){{
        put("current", "当前页");
        put("size", "每页条数");
        put("pages", "总页数");
        put("total", "总条数");
    }};

    private static final String[] hiddenParams = {
            "countId", "optimizeCountSql", "orders[].asc", "orders[].column", "searchCount", "maxLimit"
    };
    @SneakyThrows
    @Override
    public void convert(List<Api> apiList) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //printContents(clipboard);
        String html = new String(Files.readAllBytes(Paths.get("C:\\Users\\me10z\\OneDrive\\java\\doc_generator\\doc_generator\\src\\main\\resources\\weixindoc_api.html")));
        StringBuilder newHtml = new StringBuilder();
        for (Api api : apiList) {
            Integer[] width = new Integer[]{120,60,80,120};
            String template = convertHtml(html, api, width);
            Map<String, Integer> hashMap = new HashMap<>();
            hashMap.put("width1", width[0]);
            hashMap.put("width2", width[1]);
            hashMap.put("width3", width[2]);
            hashMap.put("width4", width[3]);
            String convertHtml = StrUtil.format(template, hashMap);
            newHtml.append(convertHtml);
        }
        clipboard.setContents(new WeixinDocConverter2.MyTransferable(
                new Object[]{"abc",newHtml.toString()}
                , flavors), null);
    }

    private String convertHtml(String html, Api api, Integer[] width) {
        html = html.replace("{tag}", api.getTag());
        html = html.replace("{path}", api.getPath());
        html = html.replace("{method}", api.getMethod());
        html = html.replace("{trs}", buildTrs(api, width));
        return html;
    }

    private CharSequence buildTrs(Api api, Integer[] width) {
        String s1 = buildPara(api.getParameters(), "输入", width);
        String s2 = buildPara(api.getResponses(), "输出", width);
        return s1 + s2;
    }

    private static String buildPara(List<Parameter> parameters, String type, Integer[] width) {
        AtomicInteger atomicInteger = new AtomicInteger();
        return parameters.stream().map(e->{
            Map<String, Object> map = BeanUtil.beanToMap(e);
            if(Arrays.stream(hiddenParams).anyMatch(e1->e1.equals(map.get("name")))){
                return null;
            }
            int i = atomicInteger.getAndIncrement();
            String format = "<tr>";
            if(i == 0){
                format += StrUtil.format("<td class='b' rowspan='{}'>{}</td>", parameters.size(), type);
            }
            if(map.get("description") == null){
                String s = hiddenMap.get(map.get("name"));
                if(s!=null){
                    map.put("description", s);
                }else {
                    map.put("description", "");
                }
            }
            if(type.equals("输出")){
                map.put("requiredString", "");
            }else {
                if ((Boolean) map.get("required")) {
                    map.put("requiredString", "是");
                } else {
                    map.put("requiredString", "否");
                }
            }
            int newWidth = (int) Math.floor(e.getName().length() * 7.8);
            if(newWidth > width[0]){
                width[0] = newWidth;
            }
            int newWidth2 = (int) Math.floor(Optional.ofNullable(e.getDescription()).orElse("").getBytes().length * 5.8);
            if(newWidth2 > width[3]){
                width[3] = newWidth2;
            }

            int newWidth3 = (int) Math.floor(e.getType().length() * 7.8);
            if(newWidth3 > width[1]){
                width[1] = newWidth3;
                if(width[1] > 120){
                    width[1] = 120;
                }
            }

            format += StrUtil.format("<td class='c'>{name}</td><td class='c'>{type}</td><td class='c'>{requiredString}</td><td class='c'>{description}</td></tr>", map);
            return format;
        }).filter(Objects::nonNull).collect(Collectors.joining(""));
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


    static class MyTransferable implements Transferable {
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

        public Object getTransferData (DataFlavor flavor) throws UnsupportedFlavorException, IOException {

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
