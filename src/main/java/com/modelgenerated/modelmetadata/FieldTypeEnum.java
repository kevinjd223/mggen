/*
 * FieldTypeEnum.java
 *
 * Created on April 5, 2003, 8:06 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata;

/**
 *
 * @author  kevind
 */
public class FieldTypeEnum {
    public static final FieldTypeEnum BOOLEAN = new FieldTypeEnum("Boolean");
    public static final FieldTypeEnum CLASS = new FieldTypeEnum("Class");
    public static final FieldTypeEnum COALESCE = new FieldTypeEnum("Coalesce");
    public static final FieldTypeEnum DATE = new FieldTypeEnum("Date");
    public static final FieldTypeEnum DATETIME = new FieldTypeEnum("Datetime");
    public static final FieldTypeEnum DOUBLE = new FieldTypeEnum("Double");
    public static final FieldTypeEnum ENUM = new FieldTypeEnum("Enum");
    public static final FieldTypeEnum IDENTITY = new FieldTypeEnum("Identity");
    public static final FieldTypeEnum INT = new FieldTypeEnum("int");
	public static final FieldTypeEnum INTEGER = new FieldTypeEnum("Integer");
    public static final FieldTypeEnum READONLYJOIN = new FieldTypeEnum("ReadOnlyJoin");
    public static final FieldTypeEnum STRING = new FieldTypeEnum("String");
    public static final FieldTypeEnum TEXT = new FieldTypeEnum("Text");
    
    private static FieldTypeEnum[] list = {BOOLEAN, CLASS, COALESCE, DATE, DATETIME, DOUBLE, ENUM, IDENTITY, INT, INTEGER, READONLYJOIN, STRING, TEXT};
    private String value;
    
    /** Creates a new instance of FieldTypeEnum */
    private FieldTypeEnum(String initValue) {
        value = initValue;        
    }
    
    public static FieldTypeEnum getFieldType(String value) {
        for (int i = 0; i < list.length; i++) {
            if (list[i].value.equals(value)) {
                return (FieldTypeEnum)list[i];
            }            
        }
        return null;
    }
    
    public String toString() {
        return value;
    }
    
}
