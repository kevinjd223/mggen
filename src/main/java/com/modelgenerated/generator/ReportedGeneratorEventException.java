/*
 * ReportedGeneratorEventException.java
 *
 * Created on October 23, 2002, 8:07 PM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator;

/**
 *
 * @author  kevind
 */
public class ReportedGeneratorEventException extends java.lang.RuntimeException {
    
    /**
     * Creates a new instance of <code>DocumentServiceException</code> without detail message.
     */
    public ReportedGeneratorEventException() {
    }
    
    
    /**
     * Constructs an instance of <code>DocumentServiceException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ReportedGeneratorEventException(String msg) {
        super(msg);
    }
    public ReportedGeneratorEventException(String msg, Throwable t) {
        super(msg, t);
    }
    public ReportedGeneratorEventException(Throwable t) {
        super(t);
    }
}
