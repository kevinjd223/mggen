/*
 * TilesDefGenerator.java
 *
 * Created on February 2, 2003, 7:59 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.strutsgenerator;

import com.modelgenerated.foundation.config.ConfigLocator;
import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.generator.CodeBuffer;
import com.modelgenerated.generator.GeneratorConfig;
import com.modelgenerated.modelmetadata.uimodel.ScreenDescriptor;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.StringUtil;
import java.io.File;

/**
 *
 * @author  kevind
 */
public class TilesDefGenerator {
    protected GeneratorConfig generatorConfig;
    protected TilesDefUpdater tilesDefUpdater;
    protected ScreenDescriptor screenDescriptor;
    protected String fullyQualifiedName;

    /** Creates a new instance of ValueObjectGenerator */
    public TilesDefGenerator() {
    }
    
    public void generate(ScreenDescriptor screenDescriptor) {
        generatorConfig = (GeneratorConfig)ConfigLocator.findConfig(GeneratorConfig.CONFIG_NAME);
        Assert.check(generatorConfig != null, "generatorConfig != null");        

        this.screenDescriptor = screenDescriptor;        
        
        tilesDefUpdater = new TilesDefUpdater();
        
        fullyQualifiedName = generatorConfig.getTilesDefFile();
        Logger.debug(this, "fullyQualifiedName: " + fullyQualifiedName);
        String packagePath = fullyQualifiedNameToPackage(fullyQualifiedName);
        Logger.debug(this, "packagePath: " + packagePath);
                
        File directory = new File(packagePath);
        boolean bCreated = directory.mkdirs();
        
        generateFileContent();

    }

    
    private void generateFileContent() {
        Logger.debug(this, screenDescriptor);

        // undone: consider implementing this as a prefix
        String tilesDefName = screenDescriptor.getTilesDefName();
        String tilesDefBase = screenDescriptor.getTilesDefBase();
        String menu = screenDescriptor.getMenuName();
        String jspAddFileName = screenDescriptor.getJspAddFile();
        String jspSearchFileName = screenDescriptor.getJspSearchFile();
        String jspUpdateFileName = screenDescriptor.getJspUpdateFile();
        String jspViewFileName = screenDescriptor.getJspViewFile();

    
        if (!StringUtil.isEmpty(jspAddFileName)) {
            CodeBuffer code = new CodeBuffer();
            // undone: add tiledefbase to the config.
            code.addLine("    <definition name='" + tilesDefName + "' extends='" + tilesDefBase + "'>");
            code.addLine("        <put name='title'     value='misc.default.title'/>");
            if (!StringUtil.isEmpty(menu)) {
                code.addLine("        <put name='menu'      value='" + menu + "'/>");
            }
            code.addLine("        <put name='body'      value='" + jspAddFileName + "'/>");
            code.addLine("    </definition>");
            tilesDefUpdater.update(fullyQualifiedName, tilesDefName, code.toString());
        }
        if (!StringUtil.isEmpty(jspSearchFileName)) {
            CodeBuffer code = new CodeBuffer();
            code.addLine("    <definition name='" + tilesDefName + "' extends='" + tilesDefBase + "'>");
            code.addLine("        <put name='title'     value='misc.default.title'/>");
            if (!StringUtil.isEmpty(menu)) {
                code.addLine("        <put name='menu'      value='" + menu + "'/>");
            }
            code.addLine("        <put name='body'      value='" + jspSearchFileName + "'/>");
            code.addLine("    </definition>");
            tilesDefUpdater.update(fullyQualifiedName, tilesDefName, code.toString());
        }
        if (!StringUtil.isEmpty(jspUpdateFileName)) {
            CodeBuffer code = new CodeBuffer();
            code.addLine("    <definition name='" + tilesDefName + "' extends='" + tilesDefBase + "'>");
            code.addLine("        <put name='title'     value='misc.default.title'/>");
            if (!StringUtil.isEmpty(menu)) {
                code.addLine("        <put name='menu'      value='" + menu + "'/>");
            }
            code.addLine("        <put name='body'      value='" + jspUpdateFileName + "'/>");
            code.addLine("    </definition>");
            tilesDefUpdater.update(fullyQualifiedName, tilesDefName, code.toString());
        }
        if (!StringUtil.isEmpty(jspViewFileName)) {
            CodeBuffer code = new CodeBuffer();
            code.addLine("    <definition name='" + tilesDefName + "' extends='" + tilesDefBase + "'>");
            code.addLine("        <put name='title'     value='misc.default.title'/>");
            if (!StringUtil.isEmpty(menu)) {
                code.addLine("        <put name='menu'      value='" + menu + "'/>");
            }
            code.addLine("        <put name='body'      value='" + jspViewFileName + "'/>");
            code.addLine("    </definition>");
            tilesDefUpdater.update(fullyQualifiedName, tilesDefName, code.toString());
        }
    }

    
    
    
    
    public String fullyQualifiedNameToPackage(String fqn) {
        // note this is different than the one in JavaCodeBaseGenerator
        Logger.debug(this, "FQN: " + fqn);
        return fqn.substring(0, fqn.lastIndexOf("/"));
    }
    
    
}
