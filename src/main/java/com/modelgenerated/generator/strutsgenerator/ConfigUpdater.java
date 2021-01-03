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
public class ConfigUpdater {
    String formName;
    String formData;    
    boolean formWritten = false;
    String actionName;
    String actionData;
    boolean actionWritten = false;

    protected BufferedReader in;
    protected CodeBuffer code;
    
    /** Creates a new instance of ConfigUpdater */
    public ConfigUpdater() {
        
        
    }
    
    public void update(String file, String formName, String formData, String actionName, String actionData) {     
        this.formName = formName;
        this.formData = formData;
        this.actionName = actionName;
        this.actionData = actionData;
        this.formWritten = false;
        this.actionWritten = false;    
        
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
                if (placeForForm(line)) {
                    code.addLine(formData);
                    code.addLine(line);
                    formWritten = true;
                } else if (placeForAction(line)) {
                    code.addLine(actionData);
                    code.addLine(line);
                    actionWritten = true;
                } else {
                    code.addLine(line);
                }
                
            }
            line = in.readLine();
        }
        
    }
    
    private boolean placeForForm(String line) {
        if (formWritten) {
            return false;
        }
        
        String FORMBEANS = "<form-beans";
        String FORMBEAN = "<form-bean";
        String ENDFORMBEANS = "</form-beans";
        String NAME = "name=";
        
        
        if (line.indexOf(ENDFORMBEANS) != -1) {
            return true;
        }
        
        if (line.indexOf(FORMBEANS) == -1 && line.indexOf(FORMBEAN) != -1) {
            int nameIndex = line.indexOf(NAME);
            Assert.check(nameIndex != -1, "didn't find name for: " + line);
            
            String nameSubString = line.substring(nameIndex + NAME.length());
            StringTokenizer tokenizer = new StringTokenizer(nameSubString, " ");
            String name = tokenizer.nextToken();
            name = StringUtil.removeQuotes(name);
            if (formName.compareTo(name) == 0) {
                formWritten = true;
            } else if (formName.compareTo(name) < 0) {
                return true;
            }
        }
        
        return false;        
    }
    
    
    private boolean placeForAction(String line) {
        if (actionWritten) {
            return false;
        }
        
        String ACTIONMAPPINGS = "<action-mappings";
        String ACTION = "<action";
        String ENDACTIONMAPPINGS = "</action-mappings";
        String PATH = "path=";
        
        
        if (line.indexOf(ENDACTIONMAPPINGS) != -1) {
            return true;
        }
        
        if (line.indexOf(ACTIONMAPPINGS) == -1 && line.indexOf(ACTION) != -1) {
            int nameIndex = line.indexOf(PATH);
            Assert.check(nameIndex != -1, "didn't find path for: " + line);
            
            String nameSubString = line.substring(nameIndex + PATH.length());
            StringTokenizer tokenizer = new StringTokenizer(nameSubString, " ");
            String name = tokenizer.nextToken();
            name = StringUtil.removeQuotes(name);
            if (formName.compareTo(name) == 0) {
                actionWritten = true;
            } else if (actionName.compareTo(name) < 0) {
                return true;
            }
        }
        
        return false;
    }
    
    
    
}
