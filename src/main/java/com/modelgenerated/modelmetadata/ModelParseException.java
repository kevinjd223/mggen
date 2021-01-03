/*
 * DataAccessException.java
 *
 * Created on October 23, 2002, 8:07 PM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata;

/**
 *
 * @author  kevind
 */
public class ModelParseException extends java.lang.RuntimeException {
    
    /**
     * Creates a new instance of <code>DataAccessException</code> without detail message.
     */
    public ModelParseException() {
    }
    
    
    /**
     * Constructs an instance of <code>DataAccessException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ModelParseException(String msg) {
        super(msg);
    }
    public ModelParseException(String msg, Throwable t) {
        super(msg, t);
    }
}
