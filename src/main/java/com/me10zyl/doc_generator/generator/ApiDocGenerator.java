package com.me10zyl.doc_generator.generator;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.me10zyl.doc_generator.entity.api.Api;
import com.me10zyl.doc_generator.entity.api.Parameter;
import com.me10zyl.doc_generator.entity.api.swagger.Node;
import com.me10zyl.doc_generator.properties.SwaggerProperties;
import com.me10zyl.doc_generator.service.SchemaFactory;
import com.me10zyl.doc_generator.service.SchemaVisitor;
import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfHttpRequestBuilder;
import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Html;
import com.yilnz.surfing.core.basic.Page;
import lombok.Getter;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ApiDocGenerator {

    @Autowired
    private SwaggerProperties swaggerProperties;
    private MySchemaVisitor schemaVistor = new MySchemaVisitor();

    public static class MySchemaVisitor implements SchemaVisitor{

        @Getter
        private List<Parameter> parameters = new ArrayList<>();
        @Override
        public void visit(List<Node> nodes) {
            System.out.println(nodes);
            System.out.println("------");
            StringBuilder nameBuilder = new StringBuilder();
            String firstDesc = null;
            String firstType = null;
            Boolean required = null;
            for (int i = 0; i < nodes.size(); i++) {
                Node node = nodes.get(i);
                if("properties".equals(node.getType())){
                    nameBuilder.append(node.getPropertyName());
                    if(firstDesc == null) {
                        firstDesc = node.getDesc();
                    }
                    if(firstType == null){
                        firstType = node.getJavaType();
                    }
                    if(required == null){
                        required = node.isRequired();
                    }
                    if(i != nodes.size() - 1){
                        nameBuilder.append(".");
                    }
                }else if("array".equals(node.getType())){
                    nameBuilder.append("[]");
                    if(i != nodes.size() - 1){
                        nameBuilder.append(".");
                    }
                }
            }
            Parameter parameter = new Parameter();
            parameter.setName(nameBuilder.toString().replaceAll("\\.\\[]", "[]").replaceAll("^\\[]\\.", ""));
            parameter.setDescription(firstDesc);
            parameter.setType(firstType);
            parameter.setRequired(required != null && required);
            if(parameters.stream().map(Parameter::getName).noneMatch(e->e.equals(parameter.getName()))){
                parameters.add(parameter);
            }
        }
    }


    public List<Api> buildFromSwagger(String... paths) {
        SurfSpider surfSpider = SurfSpider.create();
        SurfHttpRequestBuilder builder = new SurfHttpRequestBuilder(swaggerProperties.getUrl(), "GET");
        SurfHttpRequest r = builder.build();
        r.addHeader("Content-Type", "application/json");
        r.addHeader("Authorization", "Basic " + Base64.encode(swaggerProperties.getUsername() +
                ":" + swaggerProperties.getPassword()));
        Page page = surfSpider.addRequest(r).request().get(0);
        Html html = page.getHtml();
        if(page.getStatusCode() != 200){
            throw new RuntimeException("连不通：" + swaggerProperties.getUrl());
        }
        System.out.println("SWAGGER-JSON:" + html.get());
        System.out.println("API路径：" + paths[0]);
        return Arrays.stream(paths).map(path -> {
            Api api = new Api();
            Optional<JSONObject> first = Optional.ofNullable(JSONObject.parseObject(html.get()).getJSONObject("paths").getJSONObject(path));
            if (!first.isPresent()) {
                throw new RuntimeException("path找不到:" + path);
            }
            JSONObject jsonObject = first.get();
            api.setPath(path);
            api.setTag(((JSONArray) JsonPath.read(jsonObject.toJSONString(), "$.*.tags[0]")).get(0).toString());
            api.setSummary(((JSONArray) JsonPath.read(jsonObject.toJSONString(), "$.*.summary")).get(0).toString());
            JSONArray array = (JSONArray) JsonPath.read(jsonObject.toJSONString(), "$.*.description");
            api.setDescription((array == null || array.isEmpty()) ? "" : array.get(0).toString());
            api.setMethod(jsonObject.keySet().iterator().next());
            JSONObject jsonObject3 = jsonObject.getJSONObject(api.getMethod());
            com.alibaba.fastjson.JSONArray paras = jsonObject3.getJSONArray("parameters");
            if(paras != null) {
                api.setParameters(paras.stream().flatMap(para -> {
                    JSONObject jsonObject1 = (JSONObject) para;
                    Parameter parameter = new Parameter();
                    parameter.setName(jsonObject1.getString("name"));
                    parameter.setDescription(jsonObject1.getString("description"));
                    parameter.setRequired(jsonObject1.getBoolean("required"));
                    parameter.setType(jsonObject1.getString("type"));
                    String in = jsonObject1.getString("in");
                    parameter.setIn(in);
                    if (in.equals("body")) {
                        SchemaFactory.parseSchema(html.get(), jsonObject1.getJSONObject("schema").toJSONString(), schemaVistor, new ArrayList<>());
                        return schemaVistor.parameters.stream();
                        //return parseSchema(html, jsonObject1.getJSONObject("schema").toJSONString(), jsonObject1.getString("name"), null, null, null, null);
                        //return schemaVistor
                    }
                    return Stream.of(parameter);
                }).collect(Collectors.toList()));
            }
            schemaVistor.parameters.clear();
            JSONObject json = (JSONObject) getJson(jsonObject3, "responses.200.schema");
            if(json != null) {
                String schemaJson = json.toJSONString();
                SchemaFactory.parseSchema(html.get(), schemaJson, schemaVistor, new ArrayList<>());
            }
//            parseSchema(html, schemaJson, null, null, null, null,null )
//                    .sorted((a,b)->{
//                                return a.getName().compareTo(b.getName());
//                            });
            api.setResponses(schemaVistor.parameters.stream()
                    .distinct()
                    .collect(Collectors.toList()));
            return api;
        }).collect(Collectors.toList());
    }

    private static Stream<Parameter> parseSchema(Html html, String schema, String name, com.alibaba.fastjson.JSONArray requireFields, String parentType, String parentDesc, String parentName) {
        JSONObject jsonObject = JSONObject.parseObject(schema);
        if (jsonObject.containsKey("$ref")) {
            String ref = jsonObject.getString("$ref");
            return parseSchema(html, new JSONObject(JsonPath.read(html.get(), getJsonPathFromRef(ref))).toJSONString(), name, requireFields, parentType, parentDesc, parentName);
        } else if(jsonObject.getString("type").equals("array")){
            return parseSchema(html, jsonObject.getJSONObject("items").toJSONString(), name, requireFields, jsonObject.getString("type"), jsonObject.getString("description"), name);
        } else if(jsonObject.getString("type").equals("object")) {
            JSONObject properties = jsonObject.getJSONObject("properties");
            com.alibaba.fastjson.JSONArray requiredArray = jsonObject.getJSONArray("required");
            return properties.keySet().stream().flatMap(k->{
               return parseSchema(html, properties.getJSONObject(k).toJSONString(), k, requiredArray, parentType, parentDesc, parentName);
            });
        }else{
            Parameter parameter1 = new Parameter();
            parameter1.setName(name);
            parameter1.setType(jsonObject.getString("type"));
            parameter1.setDescription(jsonObject.getString("description"));
            if("array".equals(parentType)){
                if(parameter1.getType()  ==null) {
                    parameter1.setType(parentType);
                }
                if(parameter1.getDescription() == null) {
                    parameter1.setDescription(parentDesc);
                }
                if(!name.equals(parentName) && parentName != null) {
                    parameter1.setName(parentName + "[]." + name);
                }
            }
            parameter1.setRequired(requireFields == null ? false : requireFields.contains(parameter1.getName()));
            parameter1.setIn("body");
            return Stream.of(parameter1);
        }
    }

    private static String getJsonPathFromRef(String ref) {
        String reff = ref.replace("#", "$").replace("/", ".");
        String[] last = reff.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < last.length - 1; i++) {
            sb.append(last[i]);
            if(i != last.length - 2){
                sb.append(".");
            }
        }
        return sb.append("['").append(last[last.length-1]).append("']").toString();
    }

    private static Object getJson(JSONObject jsonObject, String path) {
        String[] split = path.split("\\.");
        JSONObject o = jsonObject;
        for (int i = 0; i < split.length - 1; i++) {
            o = o.getJSONObject(split[i]);
        }
        return o.get(split[split.length - 1]);
    }
}
