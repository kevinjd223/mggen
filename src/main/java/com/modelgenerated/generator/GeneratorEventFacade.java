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
public class GeneratorEventFacade {
    
    public static void sendEvent(GeneratorEventListener eventListener, GeneratorEventSeverityEnum severity, String data) {
    
        if (eventListener == null) {
            return;
        }
        GeneratorEvent generatorEvent = new GeneratorEvent();
        generatorEvent.setSeverity(severity);
        generatorEvent.setData(data);
        
        eventListener.event(generatorEvent);    
    }
    
}
