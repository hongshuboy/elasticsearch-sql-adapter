package com.github.hongshuboy.adapter.elasticsearch.util;

import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.linq4j.tree.Primitive;
import org.apache.calcite.rel.type.RelDataType;

import java.util.HashMap;
import java.util.Map;

public enum FieldType {
    //ignore byte and char
    STRING(String.class, "string"),
    BOOLEAN(Primitive.BOOLEAN),
    BYTE(Primitive.BYTE),
    CHAR(Primitive.CHAR),
    SHORT(Primitive.SHORT),
    INT(Primitive.INT),
    LONG(Primitive.LONG),
    FLOAT(Primitive.FLOAT),
    DOUBLE(Primitive.DOUBLE),
    DATE(java.sql.Date.class, "date"),
    TIME(java.sql.Time.class, "time"),
    TIMESTAMP(java.sql.Timestamp.class, "timestamp");

    private final Class<?> clazz;
    private final String simpleName;

    private static final Map<String, FieldType> MAP = new HashMap<>();

    static {
        for (FieldType value : values()) {
            MAP.put(value.simpleName, value);
        }
    }

    FieldType(Primitive primitive) {
        this(primitive.boxClass, primitive.primitiveName);
    }

    FieldType(Class<?> clazz, String simpleName) {
        this.clazz = clazz;
        this.simpleName = simpleName;
    }

    public RelDataType toSQLType(JavaTypeFactory typeFactory) {
        RelDataType javaType = typeFactory.createJavaType(clazz);
        return typeFactory.createSqlType(javaType.getSqlTypeName());
    }

    public static FieldType of(String typeString) {
        return MAP.get(typeString);
    }
}
