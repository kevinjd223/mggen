/*
 * DataAccessException.java
 *
 * Created on October 23, 2002, 8:07 PM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator;

import java.io.Serializable;

/**
 *
 * @author  kevind
 */
public class GeneratorException extends java.lang.RuntimeException implements Serializable {
    final static long serialVersionUID = 1L;
    
    /**
     * Creates a new instance of <code>DataAccessException</code> without detail message.
     */
    public GeneratorException() {
    }
    
    
    /**
     * Constructs an instance of <code>DataAccessException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public GeneratorException(String msg) {
        super(msg);
    }
    public GeneratorException(String msg, Throwable t) {
        super(msg, t);
    }
}
