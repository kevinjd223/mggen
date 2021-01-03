/*
 * ScreenFieldTypeEnum.java
 *
 * Created on April 5, 2003, 8:06 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata.uimodel;

/**
 *
 * @author  kevind
 */
public class ScreenFieldTypeEnum {
    public static final ScreenFieldTypeEnum STRING = new ScreenFieldTypeEnum("String");
    public static final ScreenFieldTypeEnum TEXT = new ScreenFieldTypeEnum("Text");
    public static final ScreenFieldTypeEnum DATE = new ScreenFieldTypeEnum("Date");
    public static final ScreenFieldTypeEnum BOOLEAN = new ScreenFieldTypeEnum("Boolean");
    public static final ScreenFieldTypeEnum DOUBLE = new ScreenFieldTypeEnum("Double");
    public static final ScreenFieldTypeEnum INT = new ScreenFieldTypeEnum("int");
    public static final ScreenFieldTypeEnum LINK = new ScreenFieldTypeEnum("Link");
    public static final ScreenFieldTypeEnum LIST = new ScreenFieldTypeEnum("int");
    public static final ScreenFieldTypeEnum CONSTANT = new ScreenFieldTypeEnum("Constant");
    public static final ScreenFieldTypeEnum RADIOBUTTONS = new ScreenFieldTypeEnum("RadioButtons");
    
    private static ScreenFieldTypeEnum[] list = {STRING, TEXT, DATE, BOOLEAN, DOUBLE, INT, LINK, LIST, CONSTANT, RADIOBUTTONS};
    private String value;
    
    /** Creates a new instance of ScreenFieldTypeEnum */
    private ScreenFieldTypeEnum(String initValue) {
        value = initValue;        
    }
    
    public static ScreenFieldTypeEnum getFieldType(String value) {
        for (int i = 0; i < list.length; i++) {
            if (list[i].value.equals(value)) {
                return (ScreenFieldTypeEnum)list[i];
            }            
        }
        return null;
    }
    
    public String toString() {
        return value;
    }
    
}
