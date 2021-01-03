/*
 * JoinList.java
 *
 * Created on July 11, 2004, 8:26 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.modelmetadata;

import com.modelgenerated.foundation.debug.Displayable;
import com.modelgenerated.foundation.debug.DisplayBuffer;
import com.modelgenerated.util.StringUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author  kevind
 */
public class JoinList extends ArrayList<JoinDescriptor> implements List<JoinDescriptor>, Displayable {
	private static final long serialVersionUID = 1L;

	/** Creates a new instance of JoinDescriptor */
    public JoinList() {
    }
    
    /**
     * Return true if the list already contains this join.
     * Test by verifying there is a join with the same left and right alias. 
     */
    public boolean contains(Object o) {
        JoinDescriptor joinDescriptor = (JoinDescriptor)o;
        
        for (JoinDescriptor enumJoinDescriptor : this) {
            if (StringUtil.same(enumJoinDescriptor.leftAlias, joinDescriptor.leftAlias)
                && StringUtil.same(enumJoinDescriptor.rightAlias, joinDescriptor.rightAlias)) {
                return true;
            }
        }
        return false;        
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
        DisplayBuffer displayBuffer = DisplayBuffer.newInstance("JoinList", objectDescription, level, maxLevels);
        if (displayBuffer == null) {
            return "";
        }
        
        
        return displayBuffer.toString();
    }
    
    
}
