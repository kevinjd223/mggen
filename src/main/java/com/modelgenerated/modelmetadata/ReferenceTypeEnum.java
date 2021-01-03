/*
 * ReferenceTypeEnum.java
 *
 * Created on April 5, 2003, 8:06 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata;

/**
 *
 * @author  kevind
 */
public class ReferenceTypeEnum {
    public static final ReferenceTypeEnum ONE_TO_ONE = new ReferenceTypeEnum("OneToOne");
    public static final ReferenceTypeEnum ONE_TO_MANY = new ReferenceTypeEnum("OneToMany");
    public static final ReferenceTypeEnum MANY_TO_ONE = new ReferenceTypeEnum("ManyToOne");
    public static final ReferenceTypeEnum MANY_TO_MANY = new ReferenceTypeEnum("ManyToMany");
    
    private static ReferenceTypeEnum[] list = {ONE_TO_ONE, ONE_TO_MANY, MANY_TO_ONE, MANY_TO_MANY};
    private String value;
    
    /** Creates a new instance of FieldTypeEnum */
    private ReferenceTypeEnum(String initValue) {
        value = initValue;        
    }
    
    public static ReferenceTypeEnum getReferenceType(String value) {
        for (int i = 0; i < list.length; i++) {
            if (list[i].value.equals(value)) {
                return (ReferenceTypeEnum)list[i];
            }            
        }
        return null;
    }
    
    public String toString() {
        return value;
    }
    
}
