/*
 * ProjectBuilder.java
 *
 * Created on June 6, 2004, 7:11 AM
 * Copyright 2002-2005 Kevin Delargy.
 */

package com.modelgenerated.generator.projectgenerator;


import com.modelgenerated.generator.prototypebuilder.PrototypeGenerator;
import com.modelgenerated.foundation.config.ConfigLocator;
import com.modelgenerated.generator.GeneratorConfig;
import com.modelgenerated.util.Assert;
import com.modelgenerated.util.FileUtil;
import com.modelgenerated.util.StringUtil;
import java.io.File;
import java.util.Properties;

/**
 *
 * @author  kevind
 */
public class ProjectGenerator {
    ProjectData projectData;
    Properties properties = new Properties();
    String projectPrototypeDirectory = null;
    String FILE_SEPARATOR = "/";
    String projectDirectory;
    String buildDirectory;
    String javaDirectory;
    String webDirectory;
    String junitDirectory;
    String dataDirectory;
    String jarDirectory;
    
    
    /** Creates a new instance of ProjectBuilder */
    public ProjectGenerator() {
    }
    
    public void generate(ProjectData inputProjectData) {
        Assert.check(inputProjectData != null, "inputProjectData!= null"); 
        Assert.check(inputProjectData.getProjectDirectory() != null, "inputProjectData.getProjectDirectory() != null"); 
        
        projectData = inputProjectData;
        
        GeneratorConfig generatorConfig = (GeneratorConfig)ConfigLocator.findConfig(GeneratorConfig.CONFIG_NAME);
        Assert.check(generatorConfig != null, "generatorConfig != null");        
        
        projectPrototypeDirectory = generatorConfig.getProjectPrototypeDirectory();
        
        // projectData;
        
        projectDirectory = projectData.getProjectDirectory();
        buildDirectory = projectDirectory + FILE_SEPARATOR + "source" + FILE_SEPARATOR + "build";
        javaDirectory = projectDirectory + FILE_SEPARATOR + "source" + FILE_SEPARATOR + projectData.getJavaSubDirectory();
        webDirectory = projectDirectory + FILE_SEPARATOR + "source" + FILE_SEPARATOR + projectData.getWebSubDirectory();
        junitDirectory = projectDirectory + FILE_SEPARATOR + "source" + FILE_SEPARATOR + projectData.getJunitSubDirectory();
        dataDirectory = projectDirectory + FILE_SEPARATOR + "source" + FILE_SEPARATOR + projectData.getDataSubDirectory();
        jarDirectory = projectDirectory + FILE_SEPARATOR + "source" + FILE_SEPARATOR + projectData.getJarSubDirectory();
        
        FileUtil.createDirectory(projectDirectory);
        FileUtil.createDirectory(buildDirectory);        
        FileUtil.createDirectory(javaDirectory);
        FileUtil.createDirectory(webDirectory);
        FileUtil.createDirectory(junitDirectory);
        FileUtil.createDirectory(dataDirectory);        
        FileUtil.createDirectory(jarDirectory);
        
               
        // convert projectData to propertied
        // todo: prototypebuilder should use javabean propertied directly.
        properties.setProperty("projectName", projectData.getProjectName());
        properties.setProperty("javaSubDirectory", projectData.getJavaSubDirectory());
        properties.setProperty("webSubDirectory", projectData.getWebSubDirectory());
        properties.setProperty("junitSubDirectory", projectData.getJunitSubDirectory());
        properties.setProperty("dataSubDirectory", projectData.getDataSubDirectory());
        
        createBuildFile();
    }

    private void createBuildFile() {
        
        String buildPrototypeFile = projectPrototypeDirectory + FILE_SEPARATOR + "build_xml.prototype";
        String input = FileUtil.readStringFromFile(buildPrototypeFile);

        PrototypeGenerator prototypeGenerator = new PrototypeGenerator();        
        String output = prototypeGenerator.generate(input, properties);

        String projectDirectory = projectData.getProjectDirectory();
        String buildFile = buildDirectory + FILE_SEPARATOR + "build.xml";
        FileUtil.writeFile(buildFile, output);
        
    }
    
    
}
