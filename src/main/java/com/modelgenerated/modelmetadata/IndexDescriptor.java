/*
 * QueryDescriptor.java
 *
 * Created on April 30, 2003, 9:55 PM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata;

import com.modelgenerated.foundation.debug.Displayable;
import com.modelgenerated.foundation.debug.DisplayBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author  kevind
 */
public class IndexDescriptor implements Displayable {
    private String indexName;
    private String columns;
    private boolean unique;
    
    /** Creates a new instance of QueryDescriptor */
    public IndexDescriptor() {
    }
    

    public String getIndexName() {
        return indexName;
    }
    public void setIndexName(String newIndexName) {
        indexName = newIndexName;
    }

    public String getColumns() {
        return columns;
    }    
    public void setColumns(String newColumns) {
        columns = newColumns;
    }

    public boolean getUnique() {
        return unique;
    }    
    public void setUnique(boolean newUnique) {
    	unique = newUnique;
    }
    
    
    
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
        DisplayBuffer displayBuffer = DisplayBuffer.newInstance("IndexDescriptor", objectDescription, level, maxLevels);
        if (displayBuffer == null) {
            return "";
        }
        displayBuffer.addLine(level+1, "indexName: " + indexName); 
        displayBuffer.addLine(level+1, "columns: " + columns); 
        
        return displayBuffer.toString();
    }    
    
    
}
