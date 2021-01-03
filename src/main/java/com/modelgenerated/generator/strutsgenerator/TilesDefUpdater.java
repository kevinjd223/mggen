/*
 * ConfigUpdater.java
 *
 * Created on January 14, 2004, 4:33 PM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.strutsgenerator;


import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.util.FileUtil;
import com.modelgenerated.generator.CodeBuffer;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.StringUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.StringTokenizer;



/**
 *
 * @author  kevind
 */
public class TilesDefUpdater {
    String tileDefName;
    String tileDefData;    
    boolean tileDefWritten = false;

    protected BufferedReader in;
    protected CodeBuffer code;
    
    /** Creates a new instance of ConfigUpdater */
    public TilesDefUpdater() {
        
        
    }
    
    public void update(String file, String tileDefName, String tileDefData) {     
        this.tileDefName = tileDefName;
        this.tileDefData = tileDefData;
        this.tileDefWritten = false;
        
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
        while (line != null) {
            if (line.trim().length() == 0 || line.charAt(0) == '#') {
                Logger.debug(this, "parseFile 2");
                code.addLine(line);
            } else {
                StringTokenizer tokenizer = new StringTokenizer(line, "=");
                String property = tokenizer.nextToken();
                
                Logger.debug(this, "parseFile 3");
                if (placeForTileDef(line)) {
                    code.addLine(tileDefData);
                    code.addLine(line);
                    tileDefWritten = true;
                } else {
                    code.addLine(line);
                }
                
            }
            line = in.readLine();
        }
        
    }
    
    private boolean placeForTileDef(String line) {
        if (tileDefWritten) {
            return false;
        }
        
        String TILESDEFINITIONS = "<tiles-definitions";
        String DEFINITION = "<definition";
        String ENDTILESDEFINITIONS = "</tiles-definitions";
        String NAME = "name=";
        
        
        if (line.indexOf(ENDTILESDEFINITIONS) != -1) {
            return true;
        }
        
        if (line.indexOf(DEFINITION) != -1) {
            int nameIndex = line.indexOf(NAME);
            Assert.check(nameIndex != -1, "didn't find name for: " + line);
            
            String nameSubString = line.substring(nameIndex + NAME.length());
            StringTokenizer tokenizer = new StringTokenizer(nameSubString, " ");
            String name = tokenizer.nextToken();
            name = StringUtil.removeQuotes(name);
            if (tileDefName.compareTo(name) == 0) {
                // it already exists.
                tileDefWritten = true;
            } else if (tileDefName.compareTo(name) < 0) {
                return true;
            }
        }
        
        return false;        
    }
    
    
    
    
}
