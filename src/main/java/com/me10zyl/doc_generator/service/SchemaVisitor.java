package com.me10zyl.doc_generator.service;

import com.me10zyl.doc_generator.entity.api.swagger.Node;
import com.me10zyl.doc_generator.entity.api.swagger.PropertySchema;

import java.util.List;

public interface SchemaVisitor {
    void visit(PropertySchema propertySchema, List<Node> nodes);
}
