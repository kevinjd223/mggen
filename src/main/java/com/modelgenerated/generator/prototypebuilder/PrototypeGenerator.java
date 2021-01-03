/*
 * PrototypeGenerator.java
 *
 * Created on June 4, 2004, 3:57 PM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.prototypebuilder;

import com.modelgenerated.foundation.logging.Logger;
import java.util.Properties;


/**
 *
 * @author  kevind
 */
public class PrototypeGenerator {
    public static final String OPENBRACKET = "[[%";
    public static final String CLOSEBRACKET = "%]]";
    
    
    /** Creates a new instance of PrototypeGenerator */
    public PrototypeGenerator() {
    }
    
    public String generate(String prototype, Properties properties) {
        StringBuffer output = new StringBuffer(prototype);
        
        Logger.debug(this, "generate");
        
        for (int startIndex = output.indexOf(OPENBRACKET, 0); startIndex != -1; ) {
            Logger.debug(this, "startIndex" + startIndex);
            int endIndex = output.indexOf(CLOSEBRACKET, 0);
            Logger.debug(this, "endIndex" + endIndex);
            if (endIndex == -1) {
                Logger.debug(this, "end bracket was not found");
                // todo: report error
            }
            String key = output.substring(startIndex + OPENBRACKET.length(), endIndex).trim();
            Logger.debug(this, "key:" + key);
            
            String replacementValue = properties.getProperty(key);
            if (replacementValue == null) {
                Logger.debug(this, "replacementValue was not found - key: " + key);
                // todo: report error
            }
            output.replace(startIndex, endIndex + CLOSEBRACKET.length(), replacementValue);
            
            // don't advance startindex to lastindex as last index may change.
            startIndex = output.indexOf(OPENBRACKET, startIndex + replacementValue.length());            
        }
        
        
        return output.toString();
    }
    
    
}
