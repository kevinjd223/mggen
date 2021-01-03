/* EjbVersionEnum.java
 *
 * Created on April 5, 2003, 8:06 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata.service;

import java.io.Serializable;

/**
 *
 * @author  kevind
 */
public enum EjbVersionEnum {
    EJB2("2"), 
    EJB3("3");
    
    
    private final String value;
    
    private EjbVersionEnum(String initValue) {
    	value = initValue;
    }
    
    
    public static EjbVersionEnum getEjbVersionEnum(String value) {
        for (EjbVersionEnum e: EjbVersionEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        return null;
    }

    public String toString() {
        return value;
    }
    
    
    
}
