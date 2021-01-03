/*
 * GeneratorEventListener.java
 *
 * Created on June 15, 2004, 11:47 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author  kevind
 */
public class GeneratorEventListListener implements GeneratorEventListener {
    List eventList = new ArrayList();
    
    /** Creates a new instance of GeneratorEventListener */
    public GeneratorEventListListener() {
    }
    
    public void event(GeneratorEvent generatorEvent) {
        eventList.add(generatorEvent);
    }
    
    public List getList() {
        // todo: should this clone the list?
        return eventList;
    }
    
    public void resetList() {
        eventList = new ArrayList();        
    }
    
}
