/*
 * QueryTypeEnum.java
 *
 * Created on April 5, 2003, 8:06 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata;

/**
 *
 * @author  kevind
 */
public class QueryTypeEnum {
    public static final QueryTypeEnum FINDBY_MULTI = new QueryTypeEnum("FindBy-Multi");
    public static final QueryTypeEnum FINDBY_SINGLE = new QueryTypeEnum("FindBy");
    
    private static QueryTypeEnum[] list = {FINDBY_MULTI, FINDBY_SINGLE};
    private String value;
    
    /** Creates a new instance of FieldTypeEnum */
    private QueryTypeEnum(String initValue) {
        value = initValue;        
    }
    
    public static QueryTypeEnum getFieldType(String value) {
        for (int i = 0; i < list.length; i++) {
            if (list[i].value.equals(value)) {
                return (QueryTypeEnum)list[i];
            }            
        }
        return null;
    }
    
    public String toString() {
        return value;
    }
    
}
