/*
 * GeneratorEvent.java
 *
 * Created on June 15, 2004, 11:47 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator;

/**
 *
 * @author  kevind
 */
public class GeneratorEvent {
    String data;
    GeneratorEventSeverityEnum severity;
    
    /** Creates a new instance of GeneratorEvent */
    public GeneratorEvent() {
    }

    public String getData() {
        return data;
    }    
    public void setData(String newData) {
        data = newData;
    }
    public GeneratorEventSeverityEnum getSeverity() {
        return severity;
    }    
    public void setSeverity(GeneratorEventSeverityEnum newSeverity) {
        severity = newSeverity;
    }
}
