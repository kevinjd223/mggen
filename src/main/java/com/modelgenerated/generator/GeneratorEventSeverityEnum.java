/*
 * GeneratorEventSeverityEnum.java
 *
 * Created on June 15, 2004, 11:48 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator;

/**
 *
 * @author  kevind
 */
public class GeneratorEventSeverityEnum {
    public static final GeneratorEventSeverityEnum ERROR = new GeneratorEventSeverityEnum("error");
    public static final GeneratorEventSeverityEnum WARNING  = new GeneratorEventSeverityEnum("warning");
    
    private static GeneratorEventSeverityEnum[] list = {ERROR, WARNING};
    private String value;
    
    /** Creates a new instance of FieldTypeEnum */
    private GeneratorEventSeverityEnum(String initValue) {
        value = initValue;        
    }
    
    public static GeneratorEventSeverityEnum getFieldType(String value) {
        for (int i = 0; i < list.length; i++) {
            if (list[i].value.equals(value)) {
                return (GeneratorEventSeverityEnum)list[i];
            }            
        }
        return null;
    }
    
    public String toString() {
        return value;
    }
}
