package com.me10zyl.doc_generator.service;

import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.me10zyl.doc_generator.entity.api.swagger.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SchemaFactory {

    public static void parseSchema(String allJson, String schemaJson, SchemaVisitor schemaVisitor, List<Node> nodes) {
        Schema schema = JSONObject.parseObject(schemaJson, Schema.class);
        if (schema.getRef() != null) {
            if (nodes.stream().filter(e -> e.getRef() != null).anyMatch(e -> e.getRef().contains(schema.getRef()))) {
                for (int i = nodes.size() - 1; i >= 0; i--) {
                    Node node = nodes.get(i);
                    if(node.getJavaType() == null && "properties".equals(node.getType())){
                        node.setJavaType(schema.getOriginalRef());
                        break;
                    }
                }
                schemaVisitor.visit(nodes);
                return;
            }
            nodes.add(new Node(null, "ref", schema.getRef()));
            //schema ref
            parseSchema(allJson, new JSONObject(JsonPath.read(allJson, getJsonPathFromRef(schema.getRef()))).toJSONString(), schemaVisitor, nodes);
        } else if ("object".equals(schema.getType())) {
            ObjectSchema objectSchema = JSONObject.parseObject(schemaJson, ObjectSchema.class);
            nodes.get(nodes.size()-1).setJavaType(objectSchema.getTitle());
            JSONObject properties1 = objectSchema.getProperties();
            if (properties1 == null) {
                for (int i = nodes.size() - 1; i >= 0; i--) {
                    Node node = nodes.get(i);
                    if(node.getJavaType() == null && "properties".equals(node.getType())){
                        node.setJavaType("object");
                        break;
                    }
                }
                schemaVisitor.visit(nodes);
                return;
            }
            Node node1 = new Node(null, "object", null);
            nodes.add(node1);
            List<String> required1 = Optional.ofNullable(objectSchema.getRequired()).orElse(new ArrayList<>());
            for (Map.Entry<String, Object> property : properties1.entrySet()) {
                List<Node> copyedNodes = new ArrayList<>(nodes);
                Node node = new Node(property.getKey(), "properties", null);
                node.setRequired(required1.contains(property.getKey()));
                copyedNodes.add(node);
                parseSchema(allJson, ((JSONObject) property.getValue()).toJSONString(), schemaVisitor, copyedNodes);
            }
        } else if ("array".equals(schema.getType())) {
            nodes.add(new Node(null, "array", null));
            ArraySchema arraySchema = JSONObject.parseObject(schemaJson, ArraySchema.class);
            parseSchema(allJson, arraySchema.getItems().toJSONString(), schemaVisitor, nodes);
        } else {
            //property
            Node lastNode = nodes.get(nodes.size() - 1);
            PropertySchema propertySchema = JSONObject.parseObject(schemaJson, PropertySchema.class);
            lastNode.setDesc(propertySchema.getDescription());
            lastNode.setJavaType(propertySchema.getType());
            schemaVisitor.visit(nodes);
        }
    }

    private static String getJsonPathFromRef(String ref) {
        String reff = ref.replace("#", "$").replace("/", ".");
        String[] last = reff.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < last.length - 1; i++) {
            sb.append(last[i]);
            if (i != last.length - 2) {
                sb.append(".");
            }
        }
        return sb.append("['").append(last[last.length - 1]).append("']").toString();
    }
}
