package com.me10zyl.doc_generator.service;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.me10zyl.doc_generator.entity.api.swagger.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SchemaFactory {

    public static void parseSchema(String allJson, String schemaJson, SchemaVisitor schemaVisitor, List<Node> nodes) {
        Schema schema = JSONObject.parseObject(schemaJson, Schema.class);
        if (schema.get$ref() != null) {
            //schema ref
            parseSchema(allJson, new JSONObject(JsonPath.read(allJson, getJsonPathFromRef(schema.get$ref()))).toJSONString(), schemaVisitor, nodes);
        } else if ("object".equals(schema.getType())) {
            ObjectSchema objectSchema = JSONObject.parseObject(schemaJson, ObjectSchema.class);
            JSONObject properties = objectSchema.getProperties();
            List<String> required = objectSchema.getRequired();
            for (Map.Entry<String, Object> property : properties.entrySet()) {
                List<Node> copyedNodes = new ArrayList<>(nodes);
                Node node = new Node(property.getKey(), schema.getType(), required.contains(property.getKey()));
                copyedNodes.add(node);
                parseSchema(allJson, ((JSONObject) property.getValue()).toJSONString(), schemaVisitor, copyedNodes);
            }
        } else if ("array".equals(schema.getType())) {
            Node lastNode = nodes.get(nodes.size() - 1);
            nodes.add(new Node(lastNode.getName(), schema.getType(), lastNode.isRequired()));
            ArraySchema arraySchema = JSONObject.parseObject(schemaJson, ArraySchema.class);
            parseSchema(allJson, arraySchema.getItems().toJSONString(), schemaVisitor, nodes);
        } else {
            //property
            PropertySchema propertySchema = JSONObject.parseObject(schemaJson, PropertySchema.class);
            schemaVisitor.visit(propertySchema, nodes);
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
