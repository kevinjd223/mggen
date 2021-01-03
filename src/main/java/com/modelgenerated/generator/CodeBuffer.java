/*
 * CodeBuffer.java
 *
 * Created on December 21, 2002, 6:55 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator;

/**
 *
 * @author  kevind
 */
public class CodeBuffer {
    private static String EOL = "\n";
    StringBuilder strBuf;
    
    /** Creates a new instance of DisplayUtil */
    public CodeBuffer() {
        strBuf = new StringBuilder();
    }
    public CodeBuffer(String eol) {
        EOL = eol;
        strBuf = new StringBuilder();
    }
    
    
    public void add(String text) {
        strBuf.append(text);
    }
    
    public void addLine(String text) {
        strBuf.append(text);
        strBuf.append(EOL);
    }
    public void addLine() {
        strBuf.append(EOL);
    }
    
    public String toString () {
        return strBuf.toString();
    }
    
}
