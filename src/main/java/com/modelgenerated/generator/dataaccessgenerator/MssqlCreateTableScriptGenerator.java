/*
 * MssqlCreateTableScriptGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.dataaccessgenerator;

import com.modelgenerated.util.StringUtil;

/**
 *
 * @author  kevind
 */
public class MssqlCreateTableScriptGenerator extends CreateTableScriptGenerator{
    
    /** Creates a new instance of ValueObjectGenerator */
    public MssqlCreateTableScriptGenerator() {
    }
    
    protected String getEOL() {
        return "\r\n";
    }

    protected String getPlatformDir() {
        return "mssql";
    }

    protected void generateHeader() {
        code.addLine("REM createGeneratedTables.bat - Generated table creation script");
        String copyrightNotice = model.getCopyrightNotice();
        if (!StringUtil.isEmpty(copyrightNotice)) { 
            code.addLine("* ");
            code.addLine("* " + copyrightNotice);
        }
        code.addLine();
    }
    

    public String getScriptName() {
        return "/createGeneratedTables.bat";
    }
    
    protected void generateScriptCall(String tableName) {    
        code.addLine("osql -E -d %DATABASE% < " + tableName + ".sql");        
    }
    
}
