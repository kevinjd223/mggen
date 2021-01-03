/*
 * ParseResource.java
 *
 * Created on January 14, 2004, 8:15 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.strutsgenerator;

import com.modelgenerated.util.FileUtil;
import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.generator.CodeBuffer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.StringTokenizer;

/**
 *
 * @author  kevind
 */
public class ResourceUpdater {
    protected String resourceId;
    protected String resourceValue;
    protected BufferedReader in;
    protected CodeBuffer code;

    
    
    /** Creates a new instance of ParseResource */
    public ResourceUpdater() {
    }
    
    public void addResourceString(String file, String id, String value) {
        resourceId = id;
        resourceValue = value;
        Logger.debug(this, "addResourceString - file: " + file + " id: " + id + " value: " + value);
        
        try {
            in = new BufferedReader(new FileReader(file));
            code = new CodeBuffer();

            parseFile();
            
            in.close();
            
            FileUtil.writeFile(file, code.toString());             
            
        } catch (IOException e) {
            //e.printStackTrace();
            throw new RuntimeException("error parsing: " + file, e);
        }
        
    }

    private void  parseFile() throws IOException {        
        Logger.debug(this, "parseFile 1");
        String line = in.readLine();
        boolean written = false;
        while (line != null) {
            if (line.trim().length() == 0 || line.charAt(0) == '#') {
                // whitespace or comment.
                Logger.debug(this, "parseFile 2");
                code.addLine(line);
            } else {
                StringTokenizer tokenizer = new StringTokenizer(line, "=");
                String property = tokenizer.nextToken();
                
                Logger.debug(this, "parseFile 3");
                if (resourceId.compareTo(property) == 0) { 
                    // it already exists so we will do nothing.
                    code.addLine(line);
                    written = true;                     
                } else if (resourceId.compareTo(property) > 0) { 
                    code.addLine(line);
                } else if (!written) {                    
                    code.add(resourceId);
                    code.add("=");
                    code.addLine(resourceValue);                    
                    written = true;
                    code.addLine(line);                    
                } else {
                    code.addLine(line);
                }
                
            }
            line = in.readLine();
        }
        if (!written) {
            code.add(resourceId);
            code.add("=");
            code.addLine(resourceValue);                    
        }
        
        
    }
    
    
    
}
