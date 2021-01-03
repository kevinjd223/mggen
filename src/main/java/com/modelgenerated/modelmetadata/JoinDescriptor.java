/*
 * JoinDescriptor.java
 *
 * Created on July 11, 2004, 8:26 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata;

import com.modelgenerated.foundation.debug.Displayable;
import com.modelgenerated.foundation.debug.DisplayBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Describes the data needed to join to tables on a single column from each table. 
 * 
 * 
 * @author  kevind
 */
public class JoinDescriptor implements Displayable {
    public String leftTable;
    public String leftAlias;
    public String leftColumn;
    public String rightTable;
    public String rightAlias;
    public String rightColumn;
    
    /** Creates a new instance of JoinDescriptor */
    public JoinDescriptor() {
    }
    
    @Override
    public String display() {
        return display ("");
    }
    
    @Override
    public String display(String objectDescription) {
        Map<Object,Displayable> displayedObjects = new HashMap<Object,Displayable>();
        return display (objectDescription, 0, 0, displayedObjects);
    }
    
    @Override
    public String display(String objectDescription, int level, int maxLevels, Map<Object,Displayable> displayedObjects) {
        DisplayBuffer displayBuffer = DisplayBuffer.newInstance("JoinDescriptor", objectDescription, level, maxLevels);
        if (displayBuffer == null) {
            return "";
        }
        
        displayBuffer.addLine(level+1, "leftTable: " + leftTable); 
        displayBuffer.addLine(level+1, "leftAlias: " + leftAlias); 
        displayBuffer.addLine(level+1, "leftColumn: " + leftColumn);  
        displayBuffer.addLine(level+1, "rightTable: " + rightTable); 
        displayBuffer.addLine(level+1, "rightAlias: " + rightAlias); 
        displayBuffer.addLine(level+1, "rightColumn: " + rightColumn); 
        
        return displayBuffer.toString();
    }
    
    
}
