/*
 * ScreenGenerator.java
 *
 * Created on December 22, 2003, 10:54 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.strutsgenerator;

import com.modelgenerated.foundation.config.ConfigLocator;
import com.modelgenerated.foundation.logging.Logger;
import com.modelgenerated.generator.GeneratorConfig;
import com.modelgenerated.modelmetadata.Model;
import com.modelgenerated.modelmetadata.uimodel.ScreenDescriptor;
import com.modelgenerated.modelmetadata.xml.ModelParser;
import com.modelgenerated.modelmetadata.xml.ScreenParser;
import com.modelgenerated.util.Assert;

/**
 *
 * @author  kevind
 */
public class ScreenGenerator {
    /** Creates a new instance of uiGenerator */
    public ScreenGenerator() {
    }
    
    /* 
     * What is this used for? Testing?
     */
    
    public void generateCode(String screenFile) {
        GeneratorConfig generatorConfig = (GeneratorConfig)ConfigLocator.findConfig(GeneratorConfig.CONFIG_NAME);
        Assert.check(generatorConfig != null, "generatorConfig != null");
        
        ModelParser modelParser = new ModelParser();
        Model model = modelParser.parse(generatorConfig.getModelLocation());        
        
        ScreenParser screenParser = new ScreenParser(); 
        ScreenDescriptor screenDescriptor = screenParser.parse(model, screenFile, true);        
        Assert.check(screenDescriptor != null, "screenDescriptor != null");
        
        Logger.debug(this, screenDescriptor);

        generate(model, screenDescriptor);
    }
        
    public void generate(Model model, ScreenDescriptor screenDescriptor) {        
        JspGenerator jspGenerator = new JspGenerator();
        jspGenerator.generate(model, screenDescriptor);
        
        ResourceGenerator resourceGenerator = new ResourceGenerator();
        resourceGenerator.generate(screenDescriptor);
        
        ConfigGenerator configGenerator = new ConfigGenerator();
        configGenerator.generate(screenDescriptor);
        
        TilesDefGenerator tilesDefGenerator = new TilesDefGenerator();
        tilesDefGenerator.generate(screenDescriptor);
        
        ActionGenerator actionGenerator = new ActionGenerator();
        actionGenerator.generate(model, screenDescriptor);        
    }
    
}
